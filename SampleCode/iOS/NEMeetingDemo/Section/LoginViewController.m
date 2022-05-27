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
}

- (void)doLogin:(NSString *)accountId
       accountToken:(NSString *)accountToken {
    WEAK_SELF(weakSelf);
    [[NEMeetingKit getInstance] login:accountId
                                token:accountToken
                             callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            MeetingControlVC *vc = [[MeetingControlVC alloc] init];
            [weakSelf.navigationController pushViewController:vc animated:YES];
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
@end
