#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <react_native_spalla_player-Swift.h>
@import SpallaSDK;

@interface RCT_EXTERN_MODULE(RNSpallaPlayer, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(contentId, NSString)

RCT_EXPORT_VIEW_PROPERTY(muted, BOOL)

RCT_EXPORT_VIEW_PROPERTY(hideUI, BOOL)

RCT_EXPORT_VIEW_PROPERTY(startTime, NSNumber)

RCT_EXPORT_VIEW_PROPERTY(subtitle, NSString)

RCT_EXPORT_VIEW_PROPERTY(playbackRate, NSNumber)

RCT_EXPORT_METHOD(play: (nonnull NSNumber *) reactTag {
  
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view play];
          }
    
  }];
})

RCT_EXPORT_METHOD(pause: (nonnull NSNumber *) reactTag {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view pause];
          }
  }];
})

RCT_EXPORT_METHOD(seekTo: (nonnull NSNumber *) reactTag time:(float)time) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view seekToTime: time];
          }
  }];
}

RCT_EXPORT_METHOD(selectSubtitle: (nonnull NSNumber *) reactTag subtitle:(NSString *)subtitle) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view selectSubtitle: subtitle];
          }
  }];
}

RCT_EXPORT_METHOD(selectPlaybackRate: (nonnull NSNumber *) reactTag rate:(NSNumber *)rate) {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view selectPlaybackRate: rate.doubleValue];
          }
  }];
}

RCT_EXPORT_METHOD(initialize: (nonnull NSString *) token: (NSString *) applicationId) {
  dispatch_async(dispatch_get_main_queue(), ^{
    [SpallaPlayerWrapper initializeWithToken: token applicationId: applicationId];
  });
}

RCT_EXPORT_METHOD(unmount: (nonnull NSNumber *) reactTag {
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
          UIView *view = viewRegistry[reactTag];
          if (!view || ![view isKindOfClass:[SpallaPlayerWrapper class]]) {
              RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
              return;
          } else {
            [(SpallaPlayerWrapper *)view unmount];
          }
  }];
})

RCT_EXPORT_VIEW_PROPERTY(onPlayerEvent, RCTBubblingEventBlock)

@end
