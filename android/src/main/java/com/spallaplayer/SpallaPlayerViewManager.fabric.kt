package com.spallaplayer

import android.util.Log
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.annotations.ReactProp
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent.*
import com.spalla.sdk.android.core.player.listeners.SpallaPlayerFullScreenListener
import com.spalla.sdk.android.core.player.listeners.SpallaPlayerListener
import com.spalla.sdk.android.core.player.view.SpallaPlayerView
import java.util.Timer

// These imports will work because Codegen runs for Fabric builds.
// Your IDE will show errors on these lines until you build the project.
import com.facebook.react.viewmanagers.SpallaPlayerViewManagerInterface
import com.facebook.react.viewmanagers.SpallaPlayerViewManagerDelegate

// Implement the Codegen interface for Fabric support
@ReactModule(name = SpallaPlayerViewManager.REACT_CLASS)
class SpallaPlayerViewManager() : ViewGroupManager<SpallaPlayerView>(),
  SpallaPlayerViewManagerInterface<SpallaPlayerView>, // Fabric interface
  SpallaPlayerListener, SpallaPlayerFullScreenListener {

  companion object {
    const val REACT_CLASS = "SpallaPlayerView"
  }

  // The delegate is required for Fabric to handle props and commands.
  private val mDelegate = SpallaPlayerViewManagerDelegate(this)

  // This method is required by the Fabric architecture.
  override fun getDelegate() = mDelegate

  private var _playerView: SpallaPlayerView? = null
  private var _reactContext: ReactContext? = null
  //private var _container: SpallaPlayerView? = null
  private var contentId: String? = null
  private var startTime: Double? = null
  private var subtitle: String? = null
  private var loadTimer: Timer? = null
  private var playbackRate: Double = 1.0
  private var hideUI: Boolean? = null
  private var _viewTag: Int = -1

  override fun getName() = REACT_CLASS

  override fun createViewInstance(context: ThemedReactContext): SpallaPlayerView {
    _reactContext = context
    //val container = SpallaPlayerContainerView(context)
    val player = SpallaPlayerView(context)

    // Defer initialization until view is attached to window
    player.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(view: View) {
        try {
          _playerView = player
          _playerView?.registerPlayerListener(this@SpallaPlayerViewManager)
          _playerView?.registerFullScreenListener(this@SpallaPlayerViewManager)
          // Remove listener to avoid memory leaks
          view.removeOnAttachStateChangeListener(this)

          checkAndLoadPlayer(player)
        } catch (e: Exception) {
          Log.e("SpallaPlayerViewManager", "Error during view attachment", e)
        }
      }

      override fun onViewDetachedFromWindow(view: View) {
        _playerView?.pause()

      }
    })

    //this._container = container
    //return container
    return player
  }

  override fun onDropViewInstance(view: SpallaPlayerView) {
    Log.v("RNSpallaPlayerManager", "onDropViewInstance")
    try {
      view.onDestroy()
    } catch (e: Exception) {
      e.printStackTrace()
    }
    super.onDropViewInstance(view)
  }

  // --- Prop Setters ---
  @ReactProp(name = "contentId")
  override fun setContentId(view: SpallaPlayerView, value: String?) {
    _viewTag = view.tag as? Int ?: -1
    if (value != null) {
      this.contentId = value
      //add a hacky delay here, so if content and starttime are changed at same time,
      //start time is set first
      view.postDelayed({
        checkAndLoadPlayer(view)
      }, 100L)
    }
  }

  @ReactProp(name = "muted")
  override fun setMuted(view: SpallaPlayerView, value: Boolean) {
    _playerView?.setMuted(value)
  }

  @ReactProp(name = "startTime")
  override fun setStartTime(view: SpallaPlayerView, value: Double) {
    this.startTime = value
    //checkAndLoadPlayer(view)
  }

  @ReactProp(name = "subtitle")
  override fun setSubtitle(view: SpallaPlayerView, value: String?) {
    this.subtitle = value
    _playerView?.selectSubtitle(subtitle)
  }

  @ReactProp(name = "playbackRate")
  override fun setPlaybackRate(view: SpallaPlayerView, value: Float) {
    if (value > 0) {
      this.playbackRate = value.toDouble()
      _playerView?.selectPlaybackRate(playbackRate)
    }
  }

  @ReactProp(name = "hideUI")
  override fun setHideUI(view: SpallaPlayerView, value: Boolean) {
    this.hideUI = value
    //checkAndLoadPlayer(view)
  }

  override fun play(view: SpallaPlayerView?) {
    view?.play()
  }

  override fun pause(view: SpallaPlayerView?) {
    view?.pause()
  }

  override fun seekTo(
    view: SpallaPlayerView?,
    time: Double
  ) {
    view?.seekTo(time)
  }

  override fun selectSubtitle(
    view: SpallaPlayerView?,
    subtitle: String?
  ) {
    view?.selectSubtitle(subtitle)
  }

  override fun selectPlaybackRate(
    view: SpallaPlayerView?,
    rate: Float
  ) {
    view?.selectPlaybackRate(rate.toDouble())
  }

  override fun unmount(view: SpallaPlayerView?) {
    view?.pause()
    view?.onDestroy()
  }

  private fun checkAndLoadPlayer(view: SpallaPlayerView) {
    try {
      // Only load if view is attached to window and all required props are set
      if (contentId != null && startTime != null && hideUI != null && view.isAttachedToWindow) {
        view.load(contentId!!, true, startTime!!, subtitle, hideUI!!)
      }
    } catch (e: Exception) {
      Log.e("SpallaPlayerViewManager", "Error loading player", e)
    }
  }

  // --- Command Handling ---
  override fun receiveCommand(
    root: SpallaPlayerView,
    commandId: String,
    args: ReadableArray?
  ) {
    mDelegate.receiveCommand(root, commandId, args)
  }

  // --- Event Handling ---
  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
    return mutableMapOf(
      "onPlayerEvent" to mapOf(
        "phasedRegistrationNames" to mapOf(
          "bubbled" to "onPlayerEvent"
        )
      )
    )
  }

  override fun onEvent(event: SpallaPlayerEvent) {
    val map: WritableMap = Arguments.createMap()
    val eventName = "onPlayerEvent"

    Log.v("RNSpallaPlayerManager", "onEvent: $event")
    when (event) {
      is DurationUpdate -> {
        map.putString("event", "durationUpdate")
        map.putDouble("duration", event.duration)
      }
      Ended -> map.putString("event", "ended")
      is Error -> {
        map.putString("event", "error")
        map.putString("message", event.message)
      }
      Pause -> map.putString("event", "pause")
      Play -> map.putString("event", "play")
      Playing -> map.putString("event", "playing")
      is TimeUpdate -> {
        map.putString("event", "timeUpdate")
        map.putDouble("time", event.currentTime)
      }
      Waiting -> map.putString("event", "buffering")
      is SubtitlesAvailable -> {
        map.putString("event", "subtitlesAvailable")
        val subs = Arguments.createArray()
        event.subtitles.forEach { subs.pushString(it) }
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
        _playerView?.selectPlaybackRate(playbackRate)
      }
      is PlaybackRateSelected -> {
        map.putString("event", "playbackRateSelected")
        map.putDouble("rate", event.rate)
      }
    }

    _playerView?.let { view ->
      _reactContext?.let { reactContext ->
        val surfaceId = UIManagerHelper.getSurfaceId(reactContext)
        val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.id)
        eventDispatcher?.dispatchEvent(
          RNSpallaPlayerEvent(surfaceId, view.id, eventName, map)
        )
      }
    }
  }

  override fun onEnterFullScreen() {
    val map: WritableMap = Arguments.createMap()
    map.putString("event", "enterFullScreen")
    dispatchEvent("onPlayerEvent", map)
  }

  override fun onExitFullScreen() {
    val map: WritableMap = Arguments.createMap()
    map.putString("event", "exitFullScreen")
    dispatchEvent("onPlayerEvent", map)
  }

  private fun dispatchEvent(eventName: String, eventData: WritableMap) {
    _playerView?.let { view ->
      _reactContext?.let { reactContext ->
        if (_viewTag != -1) {
          val surfaceId = UIManagerHelper.getSurfaceId(reactContext)
          val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.id)
          eventDispatcher?.dispatchEvent(
            RNSpallaPlayerEvent(surfaceId, _viewTag, eventName, eventData)
          )
        }
      }
    }
  }

}
