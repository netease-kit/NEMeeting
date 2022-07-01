//
//  Config.h
//  NEMeetingDemo
//
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * _Nonnull const kAppKey;

NS_ASSUME_NONNULL_BEGIN

@interface ServerConfig : NSObject

@property (nonatomic, copy, readonly, class) NSDictionary<NSString*,ServerConfig*>* servers;

@property (nonatomic, copy, readonly, class) ServerConfig* current;

@property (nonatomic, copy, class) NSString* serverType;

@property (nonatomic, copy, class) NSString* customAppKey;

@property (nonatomic, copy, class) NSString* customAppServerUrl;

@property (nonatomic, copy, class) NSString* customSDKServerUrl;

@property (nonatomic, copy) NSString *appKey;

@property (nonatomic, copy) NSString *appServerUrl;

@property (nonatomic, copy) NSString *sdkServerUrl;

@end
NS_ASSUME_NONNULL_END
