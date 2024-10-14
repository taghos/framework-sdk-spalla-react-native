//
//  SpallaPlayerWrapper.swift
//  Pods
//
//  Created by Rogerio Shimizu on 10/14/24.
//

import Foundation
import UIKit
import SpallaSDK

class SpallaPlayerWrapper: UIView {
  
  let viewController: SpallaPlayerViewController
  
  @objc var contentId: String? {
      didSet {
          print("Content id: \(contentId ?? "nil")")
        // this needs to be delayed a bit so hideUI can be set first when comming from RN
        // ideally we should use a chromeless class
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
          self.setupPlayer()
        }
        
      }
  }
  
  @objc var muted: Bool = false {
    didSet {
      if muted {
        viewController.mute()
      } else {
        viewController.unmute()
      }
    }
  }
  
  @objc var hideUI: Bool = false
  
  @objc var onPlayerEvent: RCTBubblingEventBlock?

  
  convenience init() {
    Spalla.shared.initialize(token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MzA1MGFmMzVhMmMxMDJhMmM0OWJmY2MiLCJyb2xlIjoiNjA1YTBjZWQyN2YyOWUyYTM2ODM3MTg3IiwiYXZhdGFyIjoicGxhY2Vob2xkZXItdXNlci5wbmciLCJjbGllbnRlIjoiNjAzN2U5NjNkZTg3YTdlNWI5NDEyMDQ5Iiwic2Vzc2FvX2lkIjoiMm1aZGJVWXBRbG5oZlBvUVhRMTdmZTF6VGYyIiwibmJmIjoxNzI3Mjg4MTAzLCJpYXQiOjE3MjcyODgxMDMsImV4cCI6MTcyOTg4MDEwMywiaXNzIjoiU3BhbGxhIiwibmJmIjoxNzI3Mjg4MTAzLCJpYXQiOjE3MjcyODgxMDMsImV4cCI6MTcyOTg4MDEwMywiaXNzIjoiU3BhbGxhIn0.9j8RXzAbxW3-E4p5jcLDGXQ95961FG_eT6NLIDNmYy8", applicationId: nil)
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
  
  override func layoutSubviews() {
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
      viewController.setup(with: contentId, isLive: false, hideUI: hideUI)
    }
  }
  
  
  @objc func play() {
    viewController.play()
  }
  
  @objc func pause() {
    viewController.pause()
  }
  
  deinit {
    viewController.pause()
  }
  
  
  
}

extension SpallaPlayerWrapper: SpallaPlayerListener {
  func onEvent(event: SpallaSDK.SpallaPlayerEvent) {
    
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
