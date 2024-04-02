// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "LoginViewController.h"
#import "HomePageVC.h"
#import "LoginInfoManager.h"
#import "ServerConfig.h"

@interface LoginViewController ()

@property(weak, nonatomic) IBOutlet UITextField *accountIdInput;
@property(weak, nonatomic) IBOutlet UITextField *accountTokenInput;
@property(weak, nonatomic) IBOutlet UIButton *loginWithSDKBtn;

@end

@implementation LoginViewController

- (void)viewDidLoad {
  [super viewDidLoad];
}

- (void)doLogin:(NSString *)accountId accountToken:(NSString *)accountToken {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit getInstance] login:accountId
                              token:accountToken
                           callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
                             [SVProgressHUD dismiss];
                             if (resultCode != ERROR_CODE_SUCCESS) {
                               [weakSelf showErrorCode:resultCode msg:resultMsg];
                             } else {
                               HomePageVC *vc = [[HomePageVC alloc] init];
                               [weakSelf.navigationController pushViewController:vc animated:YES];
                             }
                           }];
}

- (IBAction)onLoginWithSDKTokenAction:(id)sender {
  NSString *accountId = _accountIdInput.text;
  NSString *accountToken = _accountTokenInput.text;

  [SVProgressHUD showWithStatus:@"登录中"];
  [self doLogin:accountId accountToken:accountToken];
}
@end
