//
//  Config.m
//  NEMeetingDemo
//
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "Config.h"
//#import "HttpService.h"
#import "LoginInfoManager.h"

NSString *const kAppKey = @"Your_Meeting_App_Key";

NSString * const kServerType = @"serverType";
NSString * const kCustomAppKey = @"customAppKey";
NSString * const kCustomAppServerUrl = @"customAppServerUrl";
NSString * const kCustomSDKServerUrl = @"customSDKServerUrl";


@implementation ServerConfig


+ (NSDictionary<NSString *,ServerConfig *> *)servers {
    static NSDictionary *configs;
    if (configs == nil) {
        configs = @{
            @"online": [[ServerConfig alloc] init: kAppKey
                                     appServerUrl: @""
                                     sdkServerUrl: @""],
        };
    }
    return configs;
}

- (instancetype)init: (NSString*) appkey
        appServerUrl: (NSString*) appServerUrl
        sdkServerUrl: (NSString*) sdkServerUrl
{
    self = [super init];
    if (self) {
        self.appKey = appkey;
        self.appServerUrl = appServerUrl;
        self.sdkServerUrl = sdkServerUrl;
    }
    return self;
}

+ (ServerConfig *)current {
    static ServerConfig * currentConfig;
    if (currentConfig == nil) {
        ServerConfig* candicate = [ServerConfig servers][[ServerConfig serverType]];
        
        NSString* appKey = [ServerConfig ifEmpty:[ServerConfig customAppKey] fallback: candicate.appKey];
        NSString* appServerUrl = [ServerConfig ifEmpty:[ServerConfig customAppServerUrl] fallback:candicate.appServerUrl];
        NSString* sdkServerUrl = [ServerConfig ifEmpty:[ServerConfig customSDKServerUrl] fallback:candicate.sdkServerUrl];
        
        currentConfig = [[ServerConfig alloc] init: appKey
                                      appServerUrl: appServerUrl
                                      sdkServerUrl: sdkServerUrl];
        
        NSLog(@"Select server config: appKey: %@, appServerUrl: %@, sdkServerUrl: %@", appKey, appServerUrl, sdkServerUrl);
    }
    
    return currentConfig;
}

+ (NSString *) ifEmpty: (NSString*) value fallback: (nonnull NSString*) fallback  {
    if (value == nil || value.length == 0) {
        return fallback;
    }
    return value;
}

+ (NSString*)serverType {
    return [ServerConfig ifEmpty:[[NSUserDefaults standardUserDefaults] stringForKey: kServerType] fallback: @"online"];
}

+ (void)setServerType:(NSString*)serverType {
    [[NSUserDefaults standardUserDefaults] setValue:serverType forKey:kServerType];
}

+ (NSString *)customAppKey {
    return [[NSUserDefaults standardUserDefaults] stringForKey: kCustomAppKey];
}

+ (void)setCustomAppKey:(NSString *)customAppKey {
    if (customAppKey && (NSNull *)customAppKey != [NSNull null]) {
        [[NSUserDefaults standardUserDefaults] setValue:customAppKey forKey:kCustomAppKey];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kCustomAppKey];
    }
}

+ (NSString *)customAppServerUrl {
    return [[NSUserDefaults standardUserDefaults] stringForKey: kCustomAppServerUrl];
}

+ (void)setCustomAppServerUrl:(NSString *)customAppServerUrl {
    if (customAppServerUrl && (NSNull *)customAppServerUrl != [NSNull null]) {
        [[NSUserDefaults standardUserDefaults] setValue:customAppServerUrl forKey:kCustomAppServerUrl];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kCustomAppServerUrl];
    }
}

+ (NSString *)customSDKServerUrl {
    return [[NSUserDefaults standardUserDefaults] stringForKey: kCustomSDKServerUrl];
}

+ (void)setcustomSDKServerUrl:(NSString *)customSDKServerUrl {
    if (customSDKServerUrl && (NSNull *)customSDKServerUrl != [NSNull null]) {
        [[NSUserDefaults standardUserDefaults] setValue:customSDKServerUrl forKey:kCustomSDKServerUrl];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kCustomSDKServerUrl];
    }
}

@end



@implementation Config

+ (void)queryAccountInfoWithUserName:(NSString *)userName
                            password:(NSString *)password
                          completion:(QueryAccoutInfoBlock)completion {
    //请根据自己情况获得真正的用户id和token,并调用completion返回
}

@end
