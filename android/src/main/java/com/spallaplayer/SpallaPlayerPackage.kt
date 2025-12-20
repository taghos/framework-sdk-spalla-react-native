package com.spallaplayer

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager
import com.spallaplayer.components.RNGoogleCastButtonManager
import java.util.ArrayList

class SpallaPlayerViewPackage : ReactPackage {
  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    val viewManagers: MutableList<ViewManager<*, *>> = ArrayList()
    viewManagers.add(SpallaPlayerViewManager())
    viewManagers.add(RNGoogleCastButtonManager())
    return viewManagers
  }

  @Deprecated("Migrate to [BaseReactPackage] and implement [getModule] instead.")
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(SpallaPlayerModuleFabric(reactContext))
  }

  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return when (name) {
      SpallaPlayerModule.NAME -> SpallaPlayerModuleFabric(reactContext)
      else -> null
    }
  }

}
