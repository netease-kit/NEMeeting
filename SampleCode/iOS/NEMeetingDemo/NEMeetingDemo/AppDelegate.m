//
//  AppDelegate.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "AppDelegate.h"
#import "Config.h"
#import "SystemAuthHelper.h"
#import "LoginInfoManager.h"
#import "BaseViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [self doSetupMeetingSdk];
    [SystemAuthHelper requestAuthority];
    [SVProgressHUD setDefaultMaskType:SVProgressHUDMaskTypeBlack];
    return YES;
}

- (void)doSetupMeetingSdk {
    NEMeetingSDKConfig *config = [[NEMeetingSDKConfig alloc] init];
    config.appKey = kAppKey;
    config.reuseNIM = [LoginInfoManager shareInstance].reuseNIM;
    config.enableDebugLog = YES;
    [SVProgressHUD showWithStatus:@"初始化..."];
    [[NEMeetingSDK getInstance] initialize:config
                                  callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        NSLog(@"[demo init] code:%@ msg:%@ result:%@", @(resultCode), resultMsg, result);
        [SVProgressHUD dismiss];
        [[NSNotificationCenter defaultCenter] postNotificationName:kNEMeetingInitCompletionNotication
                                                            object:nil];
    }];
}

@end
