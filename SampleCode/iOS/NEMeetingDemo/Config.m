//
//  Config.m
//  NEMeetingDemo
//
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "Config.h"
#import "HttpService.h"
#import "LoginInfoManager.h"

#if ONLINE //线上环境
NSString *const kAppKey = @"m6266fa59e8eb654fd98c3c80b5b";
#elif PRIVATE // 私有化AppKey
NSString *const kAppKey = @"m5bc7efc0a3eada90e34fbb9a4ad";
#elif QA //QA环境
NSString *const kAppKey = @"mb85c72a40c43a853bc2d2000a6a";
#elif TEST //测试环境
NSString *const kAppKey = @"092dcd94d2c2566d1ed66061891cdf15";
#else
NSString *const kAppKey = @"m6266fa59e8eb654fd98c3c80b5b";
#endif




@implementation Config

+ (void)queryAccountInfoWithUserName:(NSString *)userName
                            password:(NSString *)password
                          completion:(QueryAccoutInfoBlock)completion {
    [HttpService requestLoginAuth:userName
                         password:password
                       completion:^(NSError * _Nonnull error, LoginResponse * _Nonnull response) {
        NSLog(@"%@", response);
        [LoginInfoManager shareInstance].nickName = response.ret.nickName;
        if (completion) {
            completion(error, response.ret.accountId, response.ret.accountToken);
        }
    }];
}

@end
