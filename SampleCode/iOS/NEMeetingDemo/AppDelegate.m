// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "AppDelegate.h"
#import "ServerConfig.h"
#import "SystemAuthHelper.h"
#import "LoginInfoManager.h"
#import "BaseViewController.h"
#import "IMLoginVC.h"
#import <NIMSDK/NIMSDK.h>
#import "NSString+Demo.h"
#import "AppDelegate+MeetingExtension.h"
static NSString * const prefixName = @"meetingdemo://";

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [self meeting_addNotification];
    [self doSetupMeetingSdk];
    [self meeting_BeatyResource];
    [SVProgressHUD setDefaultMaskType:SVProgressHUDMaskTypeBlack];
    
    return YES;
}

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (void)doSetupMeetingSdk {
    NEMeetingKitConfig *config = [[NEMeetingKitConfig alloc] init];
    config.appKey = ServerConfig.current.appKey;
    config.serverUrl = ServerConfig.current.sdkServerUrl;
    config.reuseIM = [LoginInfoManager shareInstance].reuseNIM;
    config.appName = @"测试APP Name";
    config.broadcastAppGroup = @"xxxx";
    

    [SVProgressHUD showWithStatus:@"初始化..."];
    [[NEMeetingKit getInstance]
        initialize:config
          callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
            NSLog(@"[demo init] code:%@ msg:%@ result:%@", @(resultCode), resultMsg, result);
            [SVProgressHUD dismiss];
            [[NSNotificationCenter defaultCenter]
                postNotificationName:kNEMeetingInitCompletionNotication
                              object:nil];
            NSString *type = [[NSUserDefaults standardUserDefaults] valueForKey:@"languageType"];
            [[NEMeetingKit getInstance]
                switchLanguage:[self defaultLanguage:type]
                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
                        NSLog(@"defaultLanguage: %@", type);
                      }];
          }];
  }

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options
{
    if ([url.absoluteString containsString:prefixName]) {
        NSDictionary *dic = [url.absoluteString queryParametersFromURLString];
        NSLog(@"dic:%@",dic);
        NSString *ssoToken = [dic objectForKey:@"ssoToken"]?:@"";
        NSString *appKey = [dic objectForKey:@"appKey"]?:@"";
        NSDictionary *ssoDict = @{@"appKey":appKey,@"ssoToken":ssoToken};

        [[NSNotificationCenter defaultCenter] postNotificationName:kNEMeetingDidGetSSOToken object:ssoDict];
        return YES;
    }
    return NO;
}

- (void)applicationWillTerminate:(UIApplication *)application {
  [[[NEMeetingKit getInstance] getMeetingService] stopBroadcastExtension];
}

- (NEMeetingLanguage)defaultLanguage:(NSString *)type {
  NEMeetingLanguage language = AUTOMATIC;
  if (!type) {
    NSString *deviceLanguage =
        [[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"][0];
    if ([deviceLanguage containsString:@"ja"]) {
      type = @"日本语";
      language = JAPANESE;
    } else if ([deviceLanguage containsString:@"zh"]) {
      type = @"中文";
      language = CHINESE;
    } else {
      type = @"English";
      language = ENGLISH;
    }
    [[NSUserDefaults standardUserDefaults] setObject:type forKey:@"languageType"];
  } else {
    if ([type isEqualToString:@"日本语"]) {
      language = JAPANESE;
    } else if ([type isEqualToString:@"中文"]) {
      language = CHINESE;
    } else if ([type isEqualToString:@"English"]) {
      language = ENGLISH;
    } else {
      language = AUTOMATIC;
    }
  }
  return language;
}
@end
