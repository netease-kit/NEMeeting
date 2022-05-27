//
//  HttpService.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LoginTask.h"

NS_ASSUME_NONNULL_BEGIN

@interface HttpService : NSObject

+ (void)requestLoginAuth:(NSString *)account
                password:(NSString *)password
              completion:(LoginTaskBlock)completion;

@end

NS_ASSUME_NONNULL_END
