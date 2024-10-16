package com.spallaplayer

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
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

class RNSpallaPlayerManager() : SimpleViewManager<SpallaPlayerView>(), SpallaPlayerListener {
  private var _playerView: SpallaPlayerView? = null
  private var _reactContext: ReactContext? = null

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
    _playerView?.load(contentId, false, true)
  }

  @ReactProp(name = "muted")
  fun setMuted(view: SpallaPlayerView, muted: Boolean) {
    _playerView?.setMuted(muted)
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
