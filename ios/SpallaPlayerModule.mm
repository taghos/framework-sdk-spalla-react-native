//
//  SpallaPlayerModule.mm
//  Pods
//
//  Created by Rogerio Shimizu on 12/19/25.
//

#import "SpallaPlayerModule.h"
#import "SpallaPlayer-Swift.h"

@interface SpallaPlayerModule ()

@end

@implementation SpallaPlayerModule

+ (NSString *)moduleName
{
  return @"SpallaPlayerModule";
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params { 
  return std::make_shared<facebook::react::NativeSpallaPlayerModuleSpecJSI>(params);
}

- (void)initialize:(nonnull NSString *)token applicationId:(nonnull NSString *)applicationId {
  dispatch_async(dispatch_get_main_queue(), ^{
    [SpallaPlayerWrapper initializeWithToken: token applicationId: applicationId];
  });
}

- (void)pause:(NSInteger)tag { 
  
}

- (void)play:(NSInteger)tag { 
  
}

- (void)seekTo:(NSInteger)tag time:(double)time { 
  
}

- (void)selectPlaybackRate:(NSInteger)tag rate:(double)rate { 
  
}

- (void)selectSubtitle:(NSInteger)tag subtitle:(NSString * _Nullable)subtitle { 
  
}

- (void)unmount:(NSInteger)tag { 
  
}


@end


