//
//  SSOLoginViewController.m
//  NEMeetingDemo
//
//  Created by wulei on 2020/11/9.
//  Copyright © 2020 张根宁. All rights reserved.
//

#import "SSOLoginViewController.h"
#import "MeetingControlVC.h"
#import "Config.h"
#import "LoginInfoManager.h"

@interface SSOLoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *accountInput;
@property (weak, nonatomic) IBOutlet UIButton *loginBtn;

@end

@implementation SSOLoginViewController

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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(getSSOToken:) name:kNEMeetingDidGetSSOToken object:nil];
}

- (void)doAutoLogin {
    BOOL infoValid = [[LoginInfoManager shareInstance] infoValid];
    if (!_autoLogin || !infoValid) {
        return;
    }
    _accountInput.text = [LoginInfoManager shareInstance].account;
    [_loginBtn sendActionsForControlEvents:UIControlEventTouchUpInside];
}
- (void)getSSOToken:(NSNotification *)notification {
    NSLog(@"notification:%@",notification.object);
    NSString *SSOToken = notification.object;
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] loginWithSSOToken:SSOToken
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

- (IBAction)onLoginAction:(id)sender {
    NSString *corpCode = _accountInput.text;
    corpCode = [self dealWithSpecialCharacter: corpCode];
    NSString *urlString = [NSString stringWithFormat:@"https://meeting-api.netease.im/v1/auth/sso/authorize?ssoAppNamespace=%@&ssoClientLoginUrl=nemeetingdemo://meeting.netease.im/", corpCode];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlString]];
}

- (void)onCleanLoginInfoAction:(NSNotification *)note {
    
    _accountInput.text = @"";
}

- (NSString *)dealWithSpecialCharacter:(NSString *)string
{
    return CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes(kCFAllocatorDefault,
                                                                     (CFStringRef)string,
                                                                     NULL,
                                                                     CFSTR(":/?#[]@!$&’()*+,;="),
                                                                     kCFStringEncodingUTF8));
}

@end
