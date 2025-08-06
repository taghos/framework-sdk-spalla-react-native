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
  
  @objc var subtitle: String? = nil {
    didSet {
      print("Subtitle set \(subtitle ?? "nil")")
      viewController.selectSubtitle(subtitle: subtitle)
    }
  }

  @objc var playbackRate: NSNumber = 1.0 {
    didSet {
      viewController.setPlaybackRate(rate: playbackRate.doubleValue)
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
      viewController.setup(with: contentId, hideUI: hideUI, startTime: startTime.doubleValue, subtitle: subtitle)
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
  
  @objc public func selectSubtitle(_ subtitle: String?) {
    viewController.selectSubtitle(subtitle: subtitle)
  }
  
  @objc public func selectPlaybackRate(_ rate: Double) {
    viewController.setPlaybackRate(rate: rate)
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

  @objc public func unmount() {
    viewController.pause()
    viewController.removeFromParent()
  }
  
  deinit {
    //viewController.pause()
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
    case .subtitlesAvailable(let subtitles):
      onPlayerEvent(["event": "subtitlesAvailable", "subtitles": subtitles])
    case .subtitleSelected(let subtitle):
      onPlayerEvent(["event": "subtitleSelected", "subtitle": subtitle != nil ? subtitle! : NSNull()])
    case .metadataLoaded(let metadata):
      onPlayerEvent(["event": "metadataLoaded", "isLive": metadata.isLive, "duration": metadata.duration])
    case .playbackRateChanged(let rate):
      onPlayerEvent(["event": "playbackRateSelected", "rate": rate])
    @unknown default:
      break
    }
  }
}
