// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>

extern NSString *_Nonnull const kNEMeetingLoginInfoCleanNotication;
extern NSString *_Nonnull const kNEMeetingDidGetSSOToken;

NS_ASSUME_NONNULL_BEGIN

@interface LoginInfoManager : NSObject

+ (instancetype)shareInstance;

@property(readonly) NSString *account;

@property(readonly) NSString *password;

@property(nonatomic, copy) NSString *nickName;

- (void)saveLoginInfo:(NSString *)account password:(NSString *)password;

- (void)loadLoginInfo;

- (void)cleanLoginInfo;

- (BOOL)infoValid;

@end

NS_ASSUME_NONNULL_END
