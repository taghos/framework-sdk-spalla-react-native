package com.spallaplayer

import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

// This class is a requirement for Fabric's eventing system.
// It defines the shape and behavior of the events your component sends.
class RNSpallaPlayerEvent(
  surfaceId: Int,
  viewTag: Int,
  private val eventName1: String,
  private val eventData: WritableMap?
) : Event<RNSpallaPlayerEvent>(surfaceId, viewTag) {

  override fun getEventName(): String = eventName1

  override fun getEventData() = eventData
}
