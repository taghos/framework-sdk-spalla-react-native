package com.spallaplayer

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableNativeArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent
import com.spalla.sdk.android.core.player.entities.SpallaPlayerEvent.*
import com.spalla.sdk.android.core.player.listeners.SpallaPlayerListener
import com.spalla.sdk.android.core.player.view.SpallaPlayerView
import java.util.Timer
import java.util.TimerTask

class RNSpallaPlayerManager() : SimpleViewManager<SpallaPlayerView>(), SpallaPlayerListener {
  private var _playerView: SpallaPlayerView? = null
  private var _reactContext: ReactContext? = null

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
    player.registerPlayerListener(this)

    return player
  }

  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
    return MapBuilder.builder<String, Any>()
      .put(
        "onPlayerEvent",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPlayerEvent")
        )
      )
      .build()
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
    if(playbackRate > 0) {
      this.playbackRate = playbackRate
      _playerView?.selectPlaybackRate(playbackRate)
    }
  }

  @ReactProp(name = "hideUI")
  fun setPlaybackRate(view: SpallaPlayerView, hideUI: Boolean) {
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
          view.load(contentId!!, true,startTime!!, subtitle = subtitle, hideUI!!)
      }
    }, 300) // Delay of 200ms to allow all properties to be set
  }

  override fun onEvent( spallaPlayerEvent: SpallaPlayerEvent) {
    val map: WritableMap = Arguments.createMap()

    when (spallaPlayerEvent) {
      is DurationUpdate -> {
        map.putString("event", "durationUpdate")
        map.putDouble("duration", spallaPlayerEvent.duration)
      }
      Ended -> map.putString("event", "ended")
      is Error -> {
        map.putString("event", "error")
        map.putString("message", spallaPlayerEvent.message)
      }
      Pause -> map.putString("event", "pause")
      Play -> map.putString("event", "play")
      Playing -> map.putString("event", "playing")
      is TimeUpdate -> {
        map.putString("event", "timeUpdate")
        map.putDouble("time", spallaPlayerEvent.currentTime)
      }
      Waiting -> map.putString("event", "buffering")
      is SubtitlesAvailable -> {
        map.putString("event", "subtitlesAvailable")

        var subs = Arguments.createArray()
        spallaPlayerEvent.subtitles.forEach {
          subs.pushString(it)
        }
        map.putArray("subtitles", subs)
      }
      is SubtitleSelected -> {
        map.putString("event", "subtitleSelected")
        map.putString("subtitle", spallaPlayerEvent.subtitle)
      }
      is MetadataLoaded -> {
        map.putString("event", "metadataLoaded")
        map.putBoolean("isLive", spallaPlayerEvent.metadata.isLive)
        map.putDouble("duration", spallaPlayerEvent.metadata.duration)

        // make sure current playback rate is applied if set initially
        _playerView?.selectPlaybackRate(playbackRate)
      }
      is PlaybackRateSelected -> {
        map.putString("event", "playbackRateSelected")
        map.putDouble("rate", spallaPlayerEvent.rate)
      }

    }
    _playerView?.let { player ->
      _reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
        player.id,
        "onPlayerEvent",
        map
      )
    }
  }

}
