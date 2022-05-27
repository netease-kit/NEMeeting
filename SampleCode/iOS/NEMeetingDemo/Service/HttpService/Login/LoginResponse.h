//
//  LoginResponse.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface LoginData : NSObject

@property (nonatomic, copy) NSString *accountId;
@property (nonatomic, copy) NSString *accountToken;
@property (nonatomic, copy) NSString *appKey;
@property (nonatomic, copy) NSString *countryCode;
@property (nonatomic, copy) NSString *imAccid;
@property (nonatomic, copy) NSString *imToken;
@property (nonatomic, copy) NSString *meetingToken;
@property (nonatomic, copy) NSString *mobilePhone;
@property (nonatomic, copy) NSString *nickName;
@property (nonatomic, copy) NSString *personalMeetingId;
@property (nonatomic, copy) NSString *userId;
@property (nonatomic, copy) NSString *userOpenId;

@end

@interface LoginResponse : NSObject

@property (nonatomic, assign) NSInteger code;
@property (nonatomic, copy) NSString *costTime;
@property (nonatomic, copy) NSString *requestId;
@property (nonatomic, copy) NSString *msg;
@property (nonatomic, strong) LoginData *ret;

@end

NS_ASSUME_NONNULL_END
