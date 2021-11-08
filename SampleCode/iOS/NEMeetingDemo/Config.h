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


@interface Config : NSObject

typedef void(^QueryAccoutInfoBlock)(NSError *error, NSString *accountId, NSString *accountToken);

//通过服务端查询真正的用户id和token用于登录
//用户需要自行实现该方法，回调正确的用户id和token用于登录
+ (void)queryAccountInfoWithUserName:(NSString *)userName
                            password:(NSString *)password
                          completion:(QueryAccoutInfoBlock)completion;

@end

NS_ASSUME_NONNULL_END
