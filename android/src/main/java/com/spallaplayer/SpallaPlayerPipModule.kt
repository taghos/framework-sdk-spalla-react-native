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
    private var instance: SpallaPlayerPipModule? = null

    fun registerPlayerManager(manager: RNSpallaPlayerManager) {
      activePlayerManager = manager
    }

    fun unregisterPlayerManager(manager: RNSpallaPlayerManager) {
      if (activePlayerManager == manager) {
        activePlayerManager = null
      }
    }

    /**
    * A static method that MainActivity can call without needing a ReactContext.
    * It safely forwards the call to the active module instance.
    */
    fun triggerUserLeaveHint() {
      // Check if an instance exists before calling the method
      instance?.onUserLeaveHint()
    }
  }

  override fun getName(): String = NAME

  init {
    // When React Native creates the module, assign it to our static property
    instance = this
  }

  @ReactMethod
  fun onUserLeaveHint() {
    activePlayerManager?.triggerPipImmediate()
  }

}
