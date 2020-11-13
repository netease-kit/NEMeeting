//
//  LoginViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "LoginViewController.h"
#import "MeetingControlVC.h"
#import "Config.h"
#import "LoginInfoManager.h"

@interface LoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *accountInput;
@property (weak, nonatomic) IBOutlet UITextField *passwordInput;
@property (weak, nonatomic) IBOutlet UIButton *loginBtn;

@property (weak, nonatomic) IBOutlet UITextField *accountIdInput;
@property (weak, nonatomic) IBOutlet UITextField *accountTokenInput;
@property (weak, nonatomic) IBOutlet UIButton *loginWithSDKBtn;

@end

@implementation LoginViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initNotications];
}

- (void)initNotications {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onCleanLoginInfoAction:)
                                                 name:kNEMeetingLoginInfoCleanNotication
                                               object:nil];
}

- (void)doLogin:(NSString *)accountId
       accountToken:(NSString *)accountToken {
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] login:accountId
                                token:accountToken
                             callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            [[LoginInfoManager shareInstance] saveLoginInfo:weakSelf.accountInput.text
                                                   password:weakSelf.passwordInput.text];
            MeetingControlVC *vc = [[MeetingControlVC alloc] init];
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    }];
}

- (IBAction)onLoginAction:(id)sender {
    NSString *account = _accountInput.text;
    NSString *password = _passwordInput.text;
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD showWithStatus:@"登录中"];
    [Config queryAccountInfoWithUserName:account
                                password:password
                              completion:^(NSError * _Nonnull error, NSString * _Nonnull accountId, NSString * _Nonnull accountToken) {
        if (error) {
            [SVProgressHUD dismiss];
            [weakSelf showErrorCode:error.code msg:@"HTTP登录请求失败"];
        } else {
            [weakSelf doLogin:accountId
                     accountToken:accountToken];
        }
    }];
}

- (IBAction)onLoginWithSDKTokenAction:(id)sender {
    NSString *accountId = _accountIdInput.text;
    NSString *accountToken = _accountTokenInput.text;
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD showWithStatus:@"登录中"];
    [self doLogin:accountId
            accountToken:accountToken];
}

- (void)onCleanLoginInfoAction:(NSNotification *)note {
    _passwordInput.text = @"";
    _accountInput.text = @"";
}


@end
