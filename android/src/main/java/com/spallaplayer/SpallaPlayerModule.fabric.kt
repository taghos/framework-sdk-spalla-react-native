package com.spallaplayer

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.UIManager
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.common.UIManagerType
import com.spalla.sdk.android.core.SpallaSDK
import com.spalla.sdk.android.core.player.view.SpallaPlayerView

class SpallaPlayerModuleFabric(
  val reactContext: ReactApplicationContext
): ReactContextBaseJavaModule(reactContext) {

  override fun getName() = "SpallaPlayerModule"

  @ReactMethod
  fun play(tag: Double) {
    reactContext.runOnUiQueueThread {
      val view = getSpallaPlayerView(tag)
      view?.play()
    }
  }

  @ReactMethod
  fun pause(tag: Double) {
    reactContext.runOnUiQueueThread {
      val view = getSpallaPlayerView(tag)
      view?.pause()
    }
  }

  @ReactMethod
  fun seekTo(tag: Double, time: Double) {
    reactContext.runOnUiQueueThread {
      val view = getSpallaPlayerView(tag)
      view?.seekTo(time)
    }
  }

  @ReactMethod
  fun selectSubtitle(tag: Double, subtitle: String?) {
    reactContext.runOnUiQueueThread {
      val view = getSpallaPlayerView(tag)
      view?.selectSubtitle(subtitle)
    }
  }

  @ReactMethod
  fun selectPlaybackRate(tag: Double, rate: Double) {
    reactContext.runOnUiQueueThread {
      val view = getSpallaPlayerView(tag)
      view?.selectPlaybackRate(rate)
    }
  }

  @ReactMethod
  fun unmount(tag: Double) {
    try {
      pause(tag)
    } catch (e: Exception) {
      Log.e("SpallaPlayerModule", "Error in unmount method", e)
    }
  }

  @ReactMethod
  fun initialize(token: String?, applicationId: String?) {
    try {
      if (token.isNullOrBlank()) {
        throw IllegalArgumentException("Token cannot be null or empty")
      }
      SpallaSDK.initialize(reactContext, token)
    } catch (e: Exception) {
      Log.e("SpallaPlayerModule", "Failed to initialize SpallaSDK", e)
    }
  }

  fun getSpallaPlayerView(tag: Double): SpallaPlayerView? {
    try {
      val uiManager = UIManagerHelper.getUIManager(reactContext, UIManagerType.FABRIC)
      if (uiManager is UIManager) {
        val view = uiManager.resolveView(tag.toInt())
        if (view is SpallaPlayerView) {
          return view
        } else if (view != null) {
          Log.e(
            "SpallaPlayerModule",
            "Cannot play: view with tag #$tag is not a SpallaPlayerView"
          )
        } else {
          Log.e("SpallaPlayerModule", "Cannot find view with tag #$tag")
        }
      } else {
        Log.e("SpallaPlayerModule", "UIManager is not available")
      }
    } catch (e: Exception) {
      Log.e("SpallaPlayerModule", "Error in play method", e)
    }
    return null
  }
}
