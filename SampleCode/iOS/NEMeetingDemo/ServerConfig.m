// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "ServerConfig.h"
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
            @"online": [[ServerConfig alloc] init: kAppKey sdkServerUrl: @"你的sdkServerUrl"],
        };
    }
    return configs;
}

- (instancetype)init: (NSString*) appkey
        sdkServerUrl: (NSString*) sdkServerUrl
{
    self = [super init];
    if (self) {
        self.appKey = appkey;
        self.sdkServerUrl = sdkServerUrl;
    }
    return self;
}

+ (ServerConfig *)current {
    static ServerConfig * currentConfig;
    if (currentConfig == nil) {
        ServerConfig* candicate = [ServerConfig servers][[ServerConfig serverType]];
        
        NSString* appKey = [ServerConfig ifEmpty:[ServerConfig customAppKey] fallback: candicate.appKey];
        NSString* sdkServerUrl = [ServerConfig ifEmpty:[ServerConfig customSDKServerUrl] fallback:candicate.sdkServerUrl];
        
        currentConfig = [[ServerConfig alloc] init: appKey sdkServerUrl: sdkServerUrl];
        
        NSLog(@"Select server config: appKey: %@, sdkServerUrl: %@", appKey, sdkServerUrl);
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

+ (void)setCustomSDKServerUrl:(NSString *)customSDKServerUrl {
    if (customSDKServerUrl && (NSNull *)customSDKServerUrl != [NSNull null]) {
        [[NSUserDefaults standardUserDefaults] setValue:customSDKServerUrl forKey:kCustomSDKServerUrl];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kCustomSDKServerUrl];
    }
}

@end

