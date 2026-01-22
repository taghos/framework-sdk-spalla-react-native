package com.spallaplayer

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.spallaplayer.components.RNGoogleCastButtonManager


class SpallaPlayerPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(
      SpallaPlayerModule(reactContext),
      SpallaPlayerPipModule(reactContext)
    )
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(RNSpallaPlayerManager(), RNGoogleCastButtonManager())
  }
}
