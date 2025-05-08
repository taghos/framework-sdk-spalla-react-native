package com.spallaplayer

import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.UIManager
import com.facebook.react.uimanager.NativeViewHierarchyManager
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.common.UIManagerType
import com.spalla.sdk.android.core.SpallaSDK
import com.spalla.sdk.android.core.player.view.SpallaPlayerView


class SpallaPlayerModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext),
  LifecycleEventListener {
  private val _reactContext: ReactApplicationContext

  init {
    reactContext.addLifecycleEventListener(this)
    _reactContext = reactContext
  }

  override fun getName(): String {
    return "RNSpallaPlayer"
  }

  override fun onHostResume() {
  }

  override fun onHostPause() {

  }

  override fun onHostDestroy() {
  }

  @ReactMethod
  fun play(tag: Int) {
    _reactContext.getNativeModule(UIManagerModule::class.java)!!
      .prependUIBlock { nativeViewHierarchyManager: NativeViewHierarchyManager ->
        val playerView = nativeViewHierarchyManager.resolveView(tag)
        if (playerView is SpallaPlayerView) {
          playerView.play()
        } else {
          throw ClassCastException(
            String.format(
              "Cannot play: view with tag #%d is not a SpallaPlayerView",
              tag
            )
          )
        }
      }
  }

  @ReactMethod
  fun pause(tag: Int) {

    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      val uiManager = UIManagerHelper.getUIManager(_reactContext, UIManagerType.FABRIC)
      if (uiManager is UIManager) {
        uiManager.resolveView(tag)?.let { view ->
          if (view is SpallaPlayerView) {
            view.pause()
          } else {
            throw ClassCastException(
              "Cannot pause: view with tag #$tag is not a SpallaPlayerView"
            )
          }
        }
      }
    } else {
      val uiManager: UIManagerModule? =
        _reactContext.getNativeModule(UIManagerModule::class.java)
      uiManager?.prependUIBlock { nativeViewHierarchyManager: NativeViewHierarchyManager ->
        val playerView = nativeViewHierarchyManager.resolveView(tag)
        if (playerView is SpallaPlayerView) {
          playerView.pause()
        } else {
          throw ClassCastException(
            String.format(
              "Cannot play: view with tag #%d is not a SpallaPplayerView",
              tag
            )
          )
        }
      }
    }


  }

  @ReactMethod
  fun seekTo(tag: Int, time: Double) {
    _reactContext.getNativeModule(UIManagerModule::class.java)!!
      .prependUIBlock { nativeViewHierarchyManager: NativeViewHierarchyManager ->
        val playerView = nativeViewHierarchyManager.resolveView(tag)
        if (playerView is SpallaPlayerView) {
          playerView.seekTo(time)
        } else {
          throw ClassCastException(
            String.format(
              "Cannot play: view with tag #%d is not a SpallaPplayerView",
              tag
            )
          )
        }
      }
  }

  @ReactMethod
  fun selectSubtitle(tag: Int, subtitle: String?) {
    _reactContext.getNativeModule(UIManagerModule::class.java)!!
      .prependUIBlock { nativeViewHierarchyManager: NativeViewHierarchyManager ->
        val playerView = nativeViewHierarchyManager.resolveView(tag)
        if (playerView is SpallaPlayerView) {
          playerView.selectSubtitle(subtitle)
        } else {
          throw ClassCastException(
            String.format(
              "Cannot play: view with tag #%d is not a SpallaPplayerView",
              tag
            )
          )
        }
      }
  }

  @ReactMethod
  fun initialize(token: String, applicationId: String?) {
    SpallaSDK.initialize(_reactContext, token)
  }

}
