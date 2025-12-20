#import "SpallaPlayerView.h"

#import <react/renderer/components/SpallaPlayerViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/SpallaPlayerViewSpec/EventEmitters.h>
#import <react/renderer/components/SpallaPlayerViewSpec/Props.h>
#import <react/renderer/components/SpallaPlayerViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"
#import "SpallaPlayer-Swift.h"


using namespace facebook::react;

@interface SpallaPlayerView () <RCTSpallaPlayerViewViewProtocol>

@end

@implementation SpallaPlayerView {
    SpallaPlayerWrapper * _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<SpallaPlayerViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const SpallaPlayerViewProps>();
    _props = defaultProps;

    [self ensureFreshWrapper];
  }

  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<SpallaPlayerViewProps const>(_props);
  const auto &newViewProps = *std::static_pointer_cast<SpallaPlayerViewProps const>(props);
  
  BOOL didRefresh = [self ensureFreshWrapper];
  
  if (oldViewProps.contentId != newViewProps.contentId || didRefresh) {
    [_view setContentId: [[NSString alloc] initWithUTF8String: newViewProps.contentId.c_str()]];
  }
  
  if (oldViewProps.startTime != newViewProps.startTime || didRefresh) {
    [_view setStartTime: [NSNumber numberWithDouble: newViewProps.startTime]];
  }
  
  if (oldViewProps.hideUI != newViewProps.hideUI || didRefresh) {
    [_view setHideUI: newViewProps.hideUI];
  }
  
  if (oldViewProps.muted != newViewProps.muted || didRefresh) {
    [_view setMuted: newViewProps.muted];
  }
  
  if (oldViewProps.playbackRate != newViewProps.playbackRate || didRefresh) {
    [_view setPlaybackRate: [NSNumber numberWithDouble: newViewProps.playbackRate]];
  }
  
  if (oldViewProps.subtitle != newViewProps.subtitle || didRefresh) {
    // SpallaPlayerViewProps::subtitle is a std::string (not an optional), so use empty() to check for "no subtitle".
    if (!newViewProps.subtitle.empty()) {
      [_view setSubtitle: [[NSString alloc] initWithUTF8String: newViewProps.subtitle.c_str()]];
    } else {
      [_view setSubtitle: nil];
    }
  }

  [super updateProps:props oldProps:oldProps];
  
}

- (BOOL)ensureFreshWrapper {
  if (_view == nil) {
    _view = [[SpallaPlayerWrapper alloc] init];
    
    __weak __typeof(self) weakSelf = self;
    [_view setOnPlayerEvent:^(NSDictionary<NSString *,id> * _Nonnull value) {
      SpallaPlayerViewEventEmitter::OnPlayerEvent event = SpallaPlayerViewEventEmitter::OnPlayerEvent{
        .event = std::string([[value objectForKey:@"event"] UTF8String]),
        .duration = value[@"duration"] != nil ? [value[@"duration"] doubleValue] : 0.0,
        .time = value[@"time"] != nil ? [value[@"time"] doubleValue] : 0.0,
        .rate = value[@"rate"] != nil ? [value[@"rate"] doubleValue] : 0.0,
        .subtitle = value[@"subtitle"] != nil ? std::string([value[@"subtitle"] UTF8String]) : std::string(),
        .isLive = value[@"isLive"] != nil ? [value[@"isLive"] boolValue] : false,
        .error = value[@"error"] != nil ? std::string([value[@"error"] UTF8String]) : std::string()
         
      };
      weakSelf.eventEmitter.onPlayerEvent(event);
    }];
    
    self.contentView = _view;
    return YES;
  }
  return NO;
  
}

- (void)updateState:(const facebook::react::State::Shared &)state
           oldState:(const facebook::react::State::Shared &)oldState {
  [super updateState:state oldState:oldState];
  
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args
{
  RCTSpallaPlayerViewHandleCommand(self, commandName, args);
}

- (void)pause {
  [_view pause];
}

- (void)play {
  [_view play];
}

- (void) seekTo:(double)time {
  [_view seekToTime: time];
}

- (void) selectPlaybackRate:(float)rate {
  [_view selectPlaybackRate: rate];
}

- (void) selectSubtitle:(NSString *)subtitle {
  [_view selectSubtitle: subtitle];
}

- (void) unmount {
  [_view unmount];
}

- (void)prepareForRecycle {
  [super prepareForRecycle];

  [_view setOnPlayerEvent: nil];
  [_view unmount];
  _view = nil;
}

- (const SpallaPlayerViewEventEmitter &)eventEmitter
{
  return static_cast<const SpallaPlayerViewEventEmitter &>(*_eventEmitter);
}

Class<RCTComponentViewProtocol> SpallaPlayerViewCls(void)
{
    return SpallaPlayerView.class;
}

@end
