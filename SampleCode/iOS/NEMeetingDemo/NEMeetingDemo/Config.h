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

@interface Config : NSObject

typedef void(^QueryAccoutInfoBlock)(NSError *error, NSString *accountId, NSString *accountToken);

//通过服务端查询真正的用户id和token用于登录
//用户需要自行实现该方法，回调正确的用户id和token用于登录
+ (void)queryAccountInfoWithUserName:(NSString *)userName
                            password:(NSString *)password
                          completion:(QueryAccoutInfoBlock)completion;

@end

NS_ASSUME_NONNULL_END
