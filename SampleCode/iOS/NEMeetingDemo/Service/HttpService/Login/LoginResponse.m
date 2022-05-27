//
//  LoginResponse.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "LoginResponse.h"

@implementation LoginResponse

- (NSString *)description {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@"code"] = @(_code);
    dic[@"costTime"] = _costTime;
    dic[@"requestId"] = _requestId;
    dic[@"ret"] = _ret;
    return [dic description];
}

@end

@implementation LoginData

- (NSString *)description {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@"accountId"] = _accountId;
    dic[@"accountToken"] = _accountToken;
    dic[@"appKey"] = _appKey;
    dic[@"countryCode"] = _countryCode;
    dic[@"imAccid"] = _imAccid;
    dic[@"imToken"] = _imToken;
    dic[@"meetingToken"] = _meetingToken;
    dic[@"mobilePhone"] = _mobilePhone;
    dic[@"nickName"] = _nickName;
    dic[@"personalMeetingId"] = _personalMeetingId;
    dic[@"userId"] = _userId;
    dic[@"userOpenId"] = _userOpenId;
    return [dic description];
}

@end
