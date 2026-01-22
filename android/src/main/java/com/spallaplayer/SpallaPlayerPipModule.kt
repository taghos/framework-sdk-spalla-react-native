package com.spallaplayer

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = SpallaPlayerPipModule.NAME)
class SpallaPlayerPipModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  companion object {
    const val NAME = "SpallaPlayerPipModule"
    private var activePlayerManager: RNSpallaPlayerManager? = null

    fun registerPlayerManager(manager: RNSpallaPlayerManager) {
      activePlayerManager = manager
    }

    fun unregisterPlayerManager(manager: RNSpallaPlayerManager) {
      if (activePlayerManager == manager) {
        activePlayerManager = null
      }
    }
  }

  override fun getName(): String = NAME

  @ReactMethod
  fun onUserLeaveHint() {
    activePlayerManager?.triggerPipImmediate()
  }
}
