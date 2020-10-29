//
//  Config.m
//  NEMeetingDemo
//
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "Config.h"

NSString *const kAppKey = @"请填入您的AppKey";

@implementation Config

+ (void)queryAccountInfoWithUserName:(NSString *)userName
                            password:(NSString *)password
                          completion:(QueryAccoutInfoBlock)completion {
   // 请根据自己情况获得真正的用户id和token,并调用completion返回
}

@end
