package com.spallaplayer

import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.uimanager.NativeViewHierarchyManager
import com.facebook.react.uimanager.UIManagerModule
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
    _reactContext.getNativeModule(UIManagerModule::class.java)!!
      .prependUIBlock { nativeViewHierarchyManager: NativeViewHierarchyManager ->
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
  fun initialize(token: String, applicationId: String?) {
    SpallaSDK.initialize(_reactContext, token)
  }

}
