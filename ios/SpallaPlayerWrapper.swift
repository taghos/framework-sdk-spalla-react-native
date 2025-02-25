//
//  SpallaPlayerWrapper.swift
//  SpallaSample
//
//  Created by Rogerio Shimizu on 11/30/23.
//

import Foundation
import UIKit
import SpallaSDK

@objc public class SpallaPlayerWrapper: UIView {
  
  let viewController: SpallaPlayerViewController
  
  @objc var contentId: String? {
    didSet {
      print("Content id: \(contentId ?? "nil")")
      // hacky! this needs to be delayed a bit so hideUI and startTime can be set first when comming from RN
      // ideally we should use a chromeless class
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [self] in
        self.setupPlayer()
      }
    }
  }
  
  @objc var muted: Bool = false {
    didSet {
      print("Mute called \(muted)")
      updateMutedState()
    }
  }
  
  @objc var hideUI: Bool = false {
    didSet {
      print("Hide UI set to \(hideUI)")
    }
  }
  
  
  @objc var startTime: NSNumber = 10 {
    didSet {
      print("Start time set \(startTime)")
    }
  }
  
  @objc var onPlayerEvent: RCTBubblingEventBlock?
  
  convenience public init() {
    self.init(frame: CGRect.zero)
  }
  
  override init(frame: CGRect) {
    viewController = SpallaPlayerViewController()
    super.init(frame: frame)
    
    //add view controller on main window
    self.window?.rootViewController?.addChild(viewController)
    if let playerView = viewController.view {
      self.addSubview(playerView)
    }
    
    //add listeners
    viewController.registerPlayerListener(listener: self)
  }
  
  override public func layoutSubviews() {
    super.layoutSubviews()
    if let playerView = viewController.view {
      playerView.frame = self.bounds
    }
    
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  func setupPlayer() {
    if let contentId {
      print("Start time \(startTime)")
      viewController.setup(with: contentId, isLive: false, hideUI: hideUI, startTime: startTime.doubleValue)
    }
  }
  
  @objc public func play() {
    viewController.play()
  }
  
  @objc public func pause() {
    viewController.pause()
  }
  
  @objc public func seekTo(time: Float) {
    viewController.seekTo(time: TimeInterval(time))
  }
  
  private func updateMutedState() {
    viewController.mute()
    if muted {
      viewController.mute()
    } else {
      viewController.unmute()
    }
  }
    
  
  @objc static public func initialize(token: String, applicationId: String) {
    Spalla.shared.initialize(token: token, applicationId: applicationId)
  }
  
  deinit {
    viewController.pause()
    viewController.removeFromParent()
  }
}

extension SpallaPlayerWrapper: SpallaPlayerListener {
  public func onEvent(event: SpallaSDK.SpallaPlayerEvent) {
    
    guard let onPlayerEvent else { return }
    
    switch event {
    case .durationUpdate(let time):
      onPlayerEvent(["event": "durationUpdate", "duration": time])
    case .ended:
      onPlayerEvent(["event": "ended"])
    case .muted:
      onPlayerEvent(["event": "muted"])
    case .pause:
      onPlayerEvent(["event": "pause"])
    case .play: 
      onPlayerEvent(["event": "play"])
    case .playing:
      onPlayerEvent(["event": "playing"])
    case .unmuted:
      onPlayerEvent(["event": "unmuted"])
    case .error(let error, let canRetry):
      onPlayerEvent(["event": "error", "error": error, "canRetry": canRetry])
    case .timeUpdate(let time):
      onPlayerEvent(["event": "timeUpdate", "time": time])
    case .waiting:
      onPlayerEvent(["event": "buffering"])
    @unknown default:
      break
    }
  }
}
