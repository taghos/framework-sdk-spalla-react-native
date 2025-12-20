import android.util.Log
import android.view.View
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

class SpallaPlayerViewManager() : ViewGroupManager<SpallaPlayerView>(),
  SpallaPlayerListener, SpallaPlayerFullScreenListener {
  private var _playerView: SpallaPlayerView? = null
  private var _reactContext: ReactContext? = null
  //private var _container: SpallaPlayerContainerView? = null

  private var contentId: String? = null
  private var startTime: Double? = null
  private var subtitle: String? = null
  private var loadTimer: Timer? = null
  private var playbackRate: Double = 1.0
  private var hideUI: Boolean? = null

  override fun getName() = "RNSpallaPlayer"

  override fun createViewInstance(context: ThemedReactContext): SpallaPlayerView {
    _reactContext = context
    val player = SpallaPlayerView(context)
    _playerView = player
    _playerView?.registerPlayerListener(this)
    _playerView?.registerFullScreenListener(this)
    return player
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

  override fun onDropViewInstance(view: SpallaPlayerView) {
    Log.v("RNSpallaPlayerManager", "onDropViewInstance")

    try {
      view.onDestroy()
    } catch (e: Exception) {
      e.printStackTrace()
    }
    super.onDropViewInstance(view)
  }

  override fun addView(parent: SpallaPlayerView, child: View, index: Int) {
    parent.addView(child, index)
  }

  override fun removeViewAt(parent: SpallaPlayerView, index: Int) {
    parent.removeViewAt(index)
  }

  override fun getChildCount(parent: SpallaPlayerView): Int {
    return parent.childCount
  }

  override fun getChildAt(parent: SpallaPlayerView, index: Int): View {
    return parent.getChildAt(index)
  }

  @ReactProp(name = "contentId")
  fun setContentId(view: SpallaPlayerView, contentId: String) {
    this.contentId = contentId
    checkAndLoadPlayer(view)
  }

  @ReactProp(name = "muted")
  fun setMuted(view: SpallaPlayerView, muted: Boolean) {
    _playerView?.setMuted(muted)
  }

  @ReactProp(name = "startTime")
  fun setStartTime(view: SpallaPlayerView, startTime: Double) {
    this.startTime = startTime
    //checkAndLoadPlayer(view)
  }

  @ReactProp(name = "subtitle")
  fun setSubtitle(view: SpallaPlayerView, subtitle: String?) {
    this.subtitle = subtitle
    _playerView?.selectSubtitle(subtitle)
  }

  @ReactProp(name = "playbackRate")
  fun setPlaybackRate(view: SpallaPlayerView, playbackRate: Double) {
    if (playbackRate > 0) {
      this.playbackRate = playbackRate
      _playerView?.selectPlaybackRate(playbackRate)
    }
  }

  @ReactProp(name = "hideUI")
  fun setHideUI(view: SpallaPlayerView, hideUI: Boolean) {
    this.hideUI = hideUI
    checkAndLoadPlayer(view)
  }

  private fun checkAndLoadPlayer(view: SpallaPlayerView) {
    if (contentId != null && startTime != null && hideUI != null) {
      view.load(contentId!!, true, startTime!!, subtitle, hideUI!!)
    }
  }

  private fun startLoadTimer(view: SpallaPlayerView) {
    // Cancel any existing timer
    loadTimer?.cancel()

    // Start a new timer to check if all required properties are set
    loadTimer = Timer()
    loadTimer?.schedule(object : TimerTask() {
      override fun run() {
        loadTimer?.cancel()
        loadTimer = null
        // Call view.load with the required properties
        view.load(contentId!!, true, startTime!!, subtitle = subtitle, hideUI!!)
      }
    }, 300) // Delay of 200ms to allow all properties to be set
  }

  override fun onEvent(event: SpallaPlayerEvent) {
    val map: WritableMap = Arguments.createMap()

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
      }

      is PlaybackRateSelected -> {
        map.putString("event", "playbackRateSelected")
        map.putDouble("rate", event.rate)
      }

    }
    _playerView?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }
  }

  override fun onEnterFullScreen() {
    Log.v("SpallaPlayerViewManager", "onEnterFullScreen")
    val map: WritableMap = Arguments.createMap()
    map.putString("event", "enterFullScreen")
    _playerView?.let { container ->
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
    _playerView?.let { container ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        container.id,
        "onPlayerEvent",
        map
      )
    }
  }

}
