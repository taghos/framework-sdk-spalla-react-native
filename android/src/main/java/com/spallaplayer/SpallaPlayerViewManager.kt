package com.spallaplayer

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent.*
import com.spalla.sdk.android.core.player.listeners.SpallaPlayerFullScreenListener
import com.spalla.sdk.android.core.player.listeners.SpallaPlayerListener
import com.spalla.sdk.android.core.player.view.SpallaPlayerView
import java.util.Timer
import java.util.TimerTask

class RNSpallaPlayerManager() : ViewGroupManager<SpallaPlayerContainerView>(),
  SpallaPlayerListener, SpallaPlayerFullScreenListener {
  private var _playerView: SpallaPlayerView? = null
  private var _reactContext: ReactContext? = null
  private var _container: SpallaPlayerContainerView? = null

  private var contentId: String? = null
  private var startTime: Double? = null
  private var subtitle: String? = null
  private var loadTimer: Timer? = null
  private var playbackRate: Double = 1.0
  private var hideUI: Boolean? = null
  private var isPlaying: Boolean = false
  private var pipTriggered: Boolean = false

  override fun getName() = "RNSpallaPlayer"

  private val pipModeListener = Consumer<PictureInPictureModeChangedInfo> { info ->
    val map: WritableMap = Arguments.createMap()
    map.putString("event", if (info.isInPictureInPictureMode) "enterPiP" else "exitPiP")
    map.putBoolean("isInPictureInPictureMode", info.isInPictureInPictureMode)

    // Reset the flag when PiP mode changes
    pipTriggered = false

    // we will only dispatch exits for now
    if(!info.isInPictureInPictureMode) {
      _container?.let { container ->
        _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
          container.id,
          "onPlayerEvent",
          map
        )
      }
    }
    _reactContext?.currentActivity?.let { activity ->
      _playerView?.onPictureInPictureModeChanged(activity, info.isInPictureInPictureMode)
    }
  }

  override fun createViewInstance(context: ThemedReactContext): SpallaPlayerContainerView {
    _reactContext = context
    //context.addLifecycleEventListener(this)

    // Register this manager for direct PiP access
    SpallaPlayerPipModule.registerPlayerManager(this)

    if (_reactContext?.currentActivity is AppCompatActivity) {
      val activity = _reactContext?.currentActivity as? AppCompatActivity
      activity?.addOnPictureInPictureModeChangedListener(pipModeListener)
    }

    val container = SpallaPlayerContainerView(context)
    _playerView = container.spallaPlayerView
    _playerView?.registerPlayerListener(this)
    _playerView?.registerFullScreenListener(this)
    this._container = container

    return container
  }

  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
    val eventMap = mutableMapOf<String, Any>()

    eventMap["onPlayerEvent"] = mapOf(
      "phasedRegistrationNames" to mapOf(
        "bubbled" to "onPlayerEvent"
      )
    )

    return eventMap
  }

  override fun onDropViewInstance(view: SpallaPlayerContainerView) {
    Log.v("RNSpallaPlayerManager", "onDropViewInstance")

    if (_reactContext?.currentActivity is AppCompatActivity) {
      val activity = _reactContext?.currentActivity as? AppCompatActivity
      activity?.removeOnPictureInPictureModeChangedListener(pipModeListener)
    }

    // Unregister this manager
    SpallaPlayerPipModule.unregisterPlayerManager(this)

    view.post {
      try {
        loadTimer?.cancel()
        view.spallaPlayerView.onDestroy()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    super.onDropViewInstance(view)
  }

  override fun addView(parent: SpallaPlayerContainerView, child: View, index: Int) {
    parent.addView(child, index)
  }

  override fun removeViewAt(parent: SpallaPlayerContainerView, index: Int) {
    parent.removeViewAt(index)
  }

  override fun getChildCount(parent: SpallaPlayerContainerView): Int {
    return parent.childCount
  }

  override fun getChildAt(parent: SpallaPlayerContainerView, index: Int): View {
    return parent.getChildAt(index)
  }

  @ReactProp(name = "contentId")
  fun setContentId(view: SpallaPlayerContainerView, contentId: String) {
    this.contentId = contentId
    //delay initialization for a bit
    loadTimer?.cancel()
    loadTimer = Timer()
    loadTimer?.schedule(object : TimerTask() {
      override fun run() {
        checkAndLoadPlayer(view)
      }
    }, 100)
  }

  @ReactProp(name = "muted")
  fun setMuted(view: SpallaPlayerContainerView, muted: Boolean) {
    _playerView?.setMuted(muted)
  }

  @ReactProp(name = "startTime")
  fun setStartTime(view: SpallaPlayerContainerView, startTime: Double) {
    this.startTime = startTime
    //checkAndLoadPlayer(view)
  }

  @ReactProp(name = "subtitle")
  fun setSubtitle(view: SpallaPlayerContainerView, subtitle: String?) {
    this.subtitle = subtitle
    _playerView?.selectSubtitle(subtitle)
  }

  @ReactProp(name = "playbackRate")
  fun setPlaybackRate(view: SpallaPlayerContainerView, playbackRate: Double) {
    if (playbackRate > 0) {
      this.playbackRate = playbackRate
      _playerView?.selectPlaybackRate(playbackRate)
    }
  }

  @ReactProp(name = "hideUI")
  fun setHideUI(view: SpallaPlayerContainerView, hideUI: Boolean) {
    this.hideUI = hideUI
  }

  private fun checkAndLoadPlayer(view: SpallaPlayerContainerView) {
    if (contentId != null && startTime != null && hideUI != null) {
      view.post {
        view.spallaPlayerView.load(contentId!!, true, startTime!!, subtitle, hideUI!!)
      }
    }
  }

  override fun onEvent(event: SpallaPlayerEvent) {
    val map: WritableMap = Arguments.createMap()

    //Log.v("RNSpallaPlayerManager", "onEvent: $event")
    when (event) {
      is DurationUpdate -> {
        map.putString("event", "durationUpdate")
        map.putDouble("duration", event.duration)
        requestLayout()
      }

      Ended -> {
        map.putString("event", "ended")
        isPlaying = false
      }

      is Error -> {
        map.putString("event", "error")
        map.putString("message", event.message)
        isPlaying = false
      }

      Pause -> {
        map.putString("event", "pause")
        isPlaying = false
      }

      Play -> {
        map.putString("event", "play")
        isPlaying = false
      }

      Playing -> {
        map.putString("event", "playing")
        isPlaying = true
      }

      is TimeUpdate -> {
        map.putString("event", "timeUpdate")
        map.putDouble("time", event.currentTime)
      }

      Waiting -> map.putString("event", "buffering")
      is SubtitlesAvailable -> {
        map.putString("event", "subtitlesAvailable")

        var subs = Arguments.createArray()
        event.subtitles.forEach {
          subs.pushString(it)
        }
        map.putArray("subtitles", subs)
      }

      is SubtitleSelected -> {
        map.putString("event", "subtitleSelected")
        map.putString("subtitle", event.subtitle)
      }

      is MetadataLoaded -> {
        map.putString("event", "metadataLoaded")
        map.putBoolean("isLive", event.metadata.isLive)
        map.putDouble("duration", event.metadata.duration)

        // make sure current playback rate is applied if set initially
        _playerView?.selectPlaybackRate(playbackRate)
        requestLayout()
      }

      is PlaybackRateSelected -> {
        map.putString("event", "playbackRateSelected")
        map.putDouble("rate", event.rate)
        requestLayout()
      }

      is AdBegin -> {
        map.putString("event", "adBegin")
      }
      is AdBreakBegin -> {
        map.putString("event", "adBreakBegin")
      }
      is AdBreakEnd -> {
        map.putString("event", "adBreakEnd")
      }
      is AdEnd -> {
        map.putString("event", "adEnd")
      }
      is AdError -> {
        map.putString("event", "adError")
      }
      is PictureInPictureModeChanged -> TODO()
    }
    _container?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }
  }

  override fun onEnterFullScreen() {

    val map: WritableMap = Arguments.createMap()
    map.putString("event", "enterFullScreen")
    _container?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }
  }

  override fun onExitFullScreen() {
    val map: WritableMap = Arguments.createMap()
    map.putString("event", "exitFullScreen")
    _container?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }
  }

  fun requestLayout() {
    _playerView?.post(measureAndLayout)
  }

  private val measureAndLayout = Runnable {
    _playerView?.let {
      it.measure(
        View.MeasureSpec.makeMeasureSpec(it.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(it.height, View.MeasureSpec.EXACTLY)
      )
      it.layout(
        it.left,
        it.top,
        it.right,
        it.bottom
      )
    }
  }

  fun triggerPipImmediate() {
    if (pipTriggered) return

    // dispatch early, as waiting for the callback on piplistener may be too late
    val map: WritableMap = Arguments.createMap()
    map.putString("event", "enterPiP")
    map.putBoolean("isInPictureInPictureMode", true)

    _container?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }

    _playerView?.let { player ->
      val activity = _reactContext?.currentActivity
      if (activity != null && isPlaying) {
        pipTriggered = true
        player.enterPictureInPictureMode(activity = activity)
      }
    }
  }

}
