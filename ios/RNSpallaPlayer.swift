@objc(RNSpallaPlayer)
class RNSpallaPlayer: RCTViewManager {

  override func view() -> (SpallaPlayerWrapper) {
    return SpallaPlayerWrapper()
  }

  @objc override static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
