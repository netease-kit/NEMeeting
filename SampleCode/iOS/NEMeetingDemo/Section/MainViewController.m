// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MainViewController.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "IMLoginVC.h"
#import "CustomViewController.h"
#import "TimerButton.h"
#import "HomePageVC.h"
#import "AppSettingsVC.h"
#import "MeetingSettingVC.h"

@interface MainViewController ()<MeetingServiceListener>
@property (nonatomic, strong) UIButton *mulIMBtn;
@property (nonatomic, strong) UIViewController *preVC;
@property (nonatomic, strong) UIAlertController *alert;
@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onMeetingInitAction:)
                                                 name:kNEMeetingInitCompletionNotication
                                               object:nil];
}

- (void)onMeetingInitAction:(NSNotification *)note {
    [[NEMeetingKit getInstance].getMeetingService addListener:self];
    [self autoLogin];
}

- (void)autoLogin {
    WEAK_SELF(weakSelf);
    [[NEMeetingKit getInstance] tryAutoLogin:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        [SVProgressHUD dismiss];
        NSLog(@"resultMsg:%@",resultMsg);
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            HomePageVC *vc = [[HomePageVC alloc] init];
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    }];
}

- (void)onEnterMulAction:(UIButton *)sender {
    IMLoginVC *vc = [[IMLoginVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onAppSettings:(id)sender {
    AppSettingsVC *vc = [[AppSettingsVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onMeetingSettings:(id)sender {
    MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)setupUI {
#ifndef ONLINE
    UILabel *env_lab = [[UILabel alloc] init];
    env_lab.text = @"TEST";
    env_lab.font = [UIFont systemFontOfSize:13.0];
    env_lab.textColor = [UIColor redColor];
    [env_lab sizeToFit];
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    env_lab.frame = CGRectMake(keyWindow.frame.size.width - env_lab.frame.size.width,
                               keyWindow.frame.size.height - env_lab.frame.size.height,
                               env_lab.frame.size.width,
                               env_lab.frame.size.height);
    [[UIApplication sharedApplication].keyWindow addSubview:env_lab];
#endif
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:self.mulIMBtn];
    self.navigationItem.rightBarButtonItem = item;
}

- (UIButton *)mulIMBtn {
    if (!_mulIMBtn) {
        _mulIMBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_mulIMBtn setTitle:@"IM复用" forState:UIControlStateNormal];
        _mulIMBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
        _mulIMBtn.frame = CGRectMake(0, 0, 60, 40);
        [_mulIMBtn addTarget:self
                      action:@selector(onEnterMulAction:)
            forControlEvents:UIControlEventTouchUpInside];
    }
    return _mulIMBtn;
}

#pragma mark - MeetingServiceListener
- (void)onInjectedMenuItemClick:(NEMeetingMenuItem *)menuItem
                    meetingInfo:(NEMeetingInfo *)meetingInfo {
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    if (menuItem.itemId == 101) { //会议号
        [[NEMeetingKit getInstance].getMeetingService getCurrentMeetingInfo:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NEMeetingInfo * _Nonnull info) {
            if (resultCode != ERROR_CODE_SUCCESS) {
                NSString *msg = [NSString stringWithFormat:@"查询会议失败.code:%@, msg:%@", @(resultCode), resultMsg];
                [keyWindow makeToast:msg
                            duration:2
                            position:CSToastPositionCenter];
            } else {
                [keyWindow makeToast:[NSString stringWithFormat:@"会议状态:%@", info]
                            duration:2
                            position:CSToastPositionCenter];
            }
        }];
    } else {
        CustomViewController *vc = (CustomViewController *)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"CustomViewController"];
        UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
        if (!preVC) {
            preVC = keyWindow.rootViewController;
        }
        vc.msg = [NSString stringWithFormat:@"Item:%@\n会议状态:%@", menuItem, meetingInfo];
        vc.modalPresentationStyle = UIModalPresentationFullScreen;
        [preVC presentViewController:vc animated:YES completion:nil];
    }
}


- (void)onInjectedMenuItemClick:(NEMenuClickInfo *)clickInfo
                    meetingInfo:(NEMeetingInfo *)meetingInfo
                stateController:(NEMenuStateController)stateController {
    if (clickInfo.itemId == 100) {
    } else if (clickInfo.itemId == 111) {
        [NEMeetingKit.getInstance.getMeetingService
            minimizeCurrentMeeting:^(NSInteger resultCode, NSString *resultMsg, id resultData) {}];
      } else {
        _alert = [UIAlertController alertControllerWithTitle:@"" message:[NSString stringWithFormat:@"%@-%@",clickInfo,meetingInfo] preferredStyle:UIAlertControllerStyleAlert];
        [_alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            stateController(NO,nil);
        }]];
        [_alert addAction:[UIAlertAction actionWithTitle:@"忽略" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {}]];
        [_alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            stateController(YES,nil);
        }]];
        UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
        _preVC = keyWindow.rootViewController.presentedViewController;
        if (!_preVC) {
            _preVC = keyWindow.rootViewController;
        }
        [_preVC presentViewController:_alert animated:YES completion:nil];
    }
}

- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
    if (event.status == MEETING_STATUS_DISCONNECTING && _preVC != nil) {
        [_preVC dismissViewControllerAnimated:YES completion:nil];
    }
    
    if (event.status == MEETING_STATUS_DISCONNECTING) {
        NSString *toastString = [NSString stringWithFormat:@"onMeetingDisconnected: %@", [self stringifyDisconnectedReason: event.arg]];
        [[UIApplication sharedApplication].keyWindow makeToast:toastString duration:2 position:CSToastPositionCenter];
    }
}

- (NSString *)stringifyDisconnectedReason:(NSInteger)disconnectCode {
    switch (disconnectCode) {
        case MEETING_DISCONNECTING_BY_HOST:
             return @"remove_by_host";
        case MEETING_DISCONNECTING_BY_NORMAL:
             return @"leave_by_self";
        case MEETING_DISCONNECTING_CLOSED_BY_HOST:
             return @"close_by_host";
        case MEETING_DISCONNECTING_CLOSED_BY_SELF_AS_HOST:
             return @"close_by_self";
        case MEETING_DISCONNECTING_LOGIN_ON_OTHER_DEVICE:
             return @"login_on_other_device";
        case MEETING_DISCONNECTING_AUTH_INFO_EXPIRED:
             return @"auth_inf_expired";
        case MEETING_DISCONNECTING_NOT_EXIST:
             return @"not_exist";
        case MEETING_DISCONNECTING_SYNC_DATA_ERROR:
             return @"sync_data_error";
        case MEETING_DISCONNECTING_RTC_INIT_ERROR:
             return @"rtc_init_error";
        case MEETING_DISCONNECTING_JOIN_CHANNEL_ERROR:
             return @"join_channel_error";
        case MEETING_DISCONNECTING_JOIN_TIMEOUT:
             return @"join_timeout";
        case MEETING_DISCONNECTING_END_OF_LIFE:
            return @"meeting_end_of_life";
        default:
            return @"unkown";
    }
}


@end
