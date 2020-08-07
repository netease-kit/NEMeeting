//
//  LoginInfoManager.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * _Nonnull const kNEMeetingLoginInfoCleanNotication;

NS_ASSUME_NONNULL_BEGIN

@interface LoginInfoManager : NSObject

+ (instancetype)shareInstance;

@property (readonly) NSString *account;

@property (readonly) NSString *password;

- (void)saveLoginInfo:(NSString *)account
             password:(NSString *)password ;

- (void)loadLoginInfo;

- (void)cleanLoginInfo;

- (BOOL)infoValid;

@end

NS_ASSUME_NONNULL_END
