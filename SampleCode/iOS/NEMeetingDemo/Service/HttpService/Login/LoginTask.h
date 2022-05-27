//
//  LoginTask.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpTaskProtocol.h"
#import "LoginResponse.h"

NS_ASSUME_NONNULL_BEGIN

//extern NSString * _Nonnull const kAuthHost;

typedef void(^LoginTaskBlock)(NSError *error, LoginResponse *response);

@interface LoginTask : NSObject<HttpTaskProtocol>

@property (nonatomic, copy) NSString *mobilePhone;
@property (nonatomic, copy) NSString *authValue;
@property (nonatomic, strong) LoginTaskBlock completion;

@end

NS_ASSUME_NONNULL_END
