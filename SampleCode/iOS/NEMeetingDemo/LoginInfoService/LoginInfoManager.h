//
//  LoginInfoManager.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * _Nonnull const kNEMeetingLoginInfoCleanNotication;
extern NSString * _Nonnull const kNEMeetingDidGetSSOToken;

NS_ASSUME_NONNULL_BEGIN

@interface LoginInfoManager : NSObject

+ (instancetype)shareInstance;

@property (readonly) NSString *account;

@property (readonly) NSString *password;

@property (nonatomic, copy) NSString *nickName;

//是否复用IM
@property (nonatomic, assign) BOOL reuseNIM;

- (void)saveLoginInfo:(NSString *)account
             password:(NSString *)password ;

- (void)loadLoginInfo;

- (void)cleanLoginInfo;

- (BOOL)infoValid;

@end

NS_ASSUME_NONNULL_END
