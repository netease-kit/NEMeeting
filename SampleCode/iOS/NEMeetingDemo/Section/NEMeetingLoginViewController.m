// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEMeetingLoginViewController.h"
#import "MeetingControlVC.h"
#import "ServerConfig.h"
#import "LoginInfoManager.h"

@interface NEMeetingLoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *accountInput;
@property (weak, nonatomic) IBOutlet UITextField *passwordInput;
@property (weak, nonatomic) IBOutlet UIButton *loginBtn;

@end

@implementation NEMeetingLoginViewController

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

- (IBAction)onLoginAction:(id)sender {
    NSString *account = _accountInput.text;
    NSString *password = _passwordInput.text;
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD showWithStatus:@"登录中"];
    [[NEMeetingKit getInstance] loginWithNEMeeting:account
                                password:password
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

- (void)onCleanLoginInfoAction:(NSNotification *)note {
    _passwordInput.text = @"";
    _accountInput.text = @"";
}

@end
