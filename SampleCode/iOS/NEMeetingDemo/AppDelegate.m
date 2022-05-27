//
//  AppDelegate.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

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
    [SVProgressHUD setDefaultMaskType:SVProgressHUDMaskTypeBlack];
    return YES;
}

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (void)doSetupMeetingSdk {
    [[NSUserDefaults standardUserDefaults] registerDefaults: @{@"developerMode": @(YES)}];
    NEMeetingKitConfig *config = [[NEMeetingKitConfig alloc] init];
    config.appKey = ServerConfig.current.appKey;
    config.appName = @"测试APP Name";
    config.broadcastAppGroup = @"xxxxxxxxxxxx";
    NELoggerConfig *loggerConfig = [[NELoggerConfig alloc] init];
    //默认等级
    loggerConfig.level = NELogLevelInfo;
    // Document路径
    NSString *sdkDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    loggerConfig.path = [sdkDir stringByAppendingString: @"/log"];
    config.loggerConfig = loggerConfig;
    
    [SVProgressHUD showWithStatus:@"初始化..."];
    [[NEMeetingKit getInstance] initialize:config
                                  callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        NSLog(@"[demo init] code:%@ msg:%@ result:%@", @(resultCode), resultMsg, result);
        [SVProgressHUD dismiss];
        [[NSNotificationCenter defaultCenter] postNotificationName:kNEMeetingInitCompletionNotication
                                                            object:nil];
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


@end
