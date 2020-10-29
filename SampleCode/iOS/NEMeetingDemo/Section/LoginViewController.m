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

@end

@implementation LoginViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initNotications];
    [self doAutoLogin];
}

- (void)initNotications {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onCleanLoginInfoAction:)
                                                 name:kNEMeetingLoginInfoCleanNotication
                                               object:nil];
}

- (void)doAutoLogin {
    BOOL infoValid = [[LoginInfoManager shareInstance] infoValid];
    if (!_autoLogin || !infoValid) {
        return;
    }
    _accountInput.text = [LoginInfoManager shareInstance].account;
    _passwordInput.text = [LoginInfoManager shareInstance].password;
    [_loginBtn sendActionsForControlEvents:UIControlEventTouchUpInside];
}

- (void)doLogin:(NSString *)account
       password:(NSString *)password {
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] login:account
                                token:password
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
                     password:accountToken];
        }
    }];
}

- (void)onCleanLoginInfoAction:(NSNotification *)note {
    _passwordInput.text = @"";
    _accountInput.text = @"";
}


@end
