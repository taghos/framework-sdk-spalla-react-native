
#import "RNGoogleCastButton.h"

@import GoogleCast;


#import "RNGoogleCastButton.h"
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

@implementation RNGoogleCastButton

RCT_EXPORT_MODULE(RNGoogleCastButton)

- (UIView *)view {
  return [[GCKUICastButton alloc] init];
}

- (nullable GCKUICastButton *) getWrapper: (nonnull NSNumber *)reactTag viewRegistry:(NSDictionary<NSNumber *,UIView *> *) viewRegistry {
  UIView *view = viewRegistry[reactTag];
  if (!view || ![view isKindOfClass:[GCKUICastButton class]]) {
      RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
      return NULL;
  } else {
    return (GCKUICastButton *) view;
    
  }
  
}

RCT_EXPORT_VIEW_PROPERTY(tintColor, UIColor)

@end
