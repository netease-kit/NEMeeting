// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "LoginInfoManager.h"

NSString *const kNEMeetingLoginAccount = @"kNEMeetingLoginAccount";
NSString *const kNEMeetingLoginPassword = @"kNEMeetingLoginPassword";
NSString *const kNEMeetingLoginInfoCleanNotication = @"kNEMeetingLoginInfoCleanNotication";
NSString *const kNEMeetingEnablereuseNIM = @"kNEMeetingEnablereuseNIM";
NSString *const kNEMeetingDidGetSSOToken = @"kNEMeetingDidGetSSOToken";


@interface LoginInfoManager ()

@property (nonatomic, copy) NSString *account;
@property (nonatomic, copy) NSString *password;

@end

@implementation LoginInfoManager

@synthesize reuseNIM = _reuseNIM;

+ (instancetype)shareInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[LoginInfoManager alloc] init];
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
        [self loadLoginInfo];
    }
    return self;
}


- (void)saveLoginInfo:(NSString *)account
             password:(NSString *)password {
    if (account.length == 0 ||
        password.length == 0) {
        return;
    }
    
    _account = account;
    _password = password;
    
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    [userDef setObject:account forKey:kNEMeetingLoginAccount];
    [userDef setObject:password forKey:kNEMeetingLoginPassword];
}


- (void)loadLoginInfo {
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSString *account = [userDef objectForKey:kNEMeetingLoginAccount];
    NSString *password = [userDef objectForKey:kNEMeetingLoginPassword];
    if (account.length != 0 && password.length != 0) {
        _account = account;
        _password = password;
    }
}

- (void)cleanLoginInfo {
    _account = nil;
    _password = nil;
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    [userDef removeObjectForKey:kNEMeetingLoginAccount];
    [userDef removeObjectForKey:kNEMeetingLoginPassword];
    [[NSNotificationCenter defaultCenter] postNotificationName:kNEMeetingLoginInfoCleanNotication
                                                        object:nil];
}

- (BOOL)infoValid {
    return (_account.length != 0 && _password.length != 0);
}

- (void)setReuseNIM:(BOOL)reuseNIM {
    [[NSUserDefaults standardUserDefaults] setObject:@(reuseNIM) forKey:kNEMeetingEnablereuseNIM];
}

- (BOOL)reuseNIM {
    id value = [[NSUserDefaults standardUserDefaults] objectForKey:kNEMeetingEnablereuseNIM];
    return [value boolValue];
}

@end
