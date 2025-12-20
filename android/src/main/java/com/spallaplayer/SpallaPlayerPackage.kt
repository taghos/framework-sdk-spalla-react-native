package com.spallaplayer

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.ModuleSpec
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.spallaplayer.components.RNGoogleCastButtonManager

class SpallaPlayerViewPackage : BaseReactPackage() {

  override fun getViewManagers(
    reactContext: ReactApplicationContext
  ): List<ModuleSpec> =
    listOf(
      ModuleSpec.viewManagerSpec {
        SpallaPlayerViewManager()
      },
      ModuleSpec.viewManagerSpec {
        RNGoogleCastButtonManager()
      }
    )

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider =
    ReactModuleInfoProvider {
      mapOf(
        SpallaPlayerModule.REACT_NAME to ReactModuleInfo(
          SpallaPlayerModule.REACT_NAME,
          className = SpallaPlayerModule::class.java.name,
          canOverrideExistingModule = false,
          needsEagerInit = false,
          isCxxModule = false,
          isTurboModule = false
        )
      )
    }

  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return when (name) {
      SpallaPlayerModule.REACT_NAME -> SpallaPlayerModule(reactContext)
      else -> null
    }
  }

}
