//
//  MainViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MainViewController.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "CustomViewController.h"
#import "TimerButton.h"

@interface MainViewController ()<MeetingServiceListener>

@property (nonatomic, strong) TimerButton *restoreMeetingBtn;

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

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    if (@available(iOS 11.0, *)) {
        _restoreMeetingBtn.frame = CGRectMake(0, self.view.safeAreaInsets.top, self.view.width, 20);
    } else {
        _restoreMeetingBtn.frame = CGRectMake(0, 64, self.view.width, 20);
    }
}

- (void)onMeetingInitAction:(NSNotification *)note {
    [[NEMeetingSDK getInstance].getMeetingService addListener:self];
    [self autoLogin];
}

- (void)autoLogin {
    BOOL infoValid = [[LoginInfoManager shareInstance] infoValid];
    if (infoValid) {
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        LoginViewController *vc =  [storyboard instantiateViewControllerWithIdentifier:@"LoginViewController"];
        vc.autoLogin = YES;
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)onRestoreMeetingAction:(UIButton *)sender {
    BOOL ret = [[NEMeetingSDK getInstance].getMeetingService returnToMeeting];
    if (ret) {
        sender.hidden = YES;
    }
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
   
    [[UIApplication sharedApplication].keyWindow addSubview:self.restoreMeetingBtn];
}


#pragma mark - MeetingServiceListener
- (void)onInjectedMenuItemClick:(NEMeetingMenuItem *)menuItem
                    meetingInfo:(NEMeetingInfo *)meetingInfo {
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    if (menuItem.itemId == 101) { //会议号
        [[NEMeetingSDK getInstance].getMeetingService getCurrentMeetingInfo:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NEMeetingInfo * _Nonnull info) {
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

- (void)updateMeetingBtnWithInfo:(NEMeetingInfo *)info {
    NEMeetingStatus status = [[NEMeetingSDK getInstance] getMeetingService].getMeetingStatus;
    if (status != MEETING_STATUS_INMEETING_MINIMIZED) {
        return;;
    }

    _restoreMeetingBtn.neTitle = info.subject;
    //int64_t currentTime = [[NSDate date] timeIntervalSince1970];
    //int64_t startTime = info.startTime/1000;
    //int64_t interval = currentTime - startTime;
    int64_t interval = info.duration/1000;
    if (interval > 0) {
        _restoreMeetingBtn.startTime = interval;
    }
    _restoreMeetingBtn.hidden = NO;
}

- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
    
    if (event.status == MEETING_STATUS_INMEETING_MINIMIZED) {
        __weak typeof(self) weakSelf = self;
        [[[NEMeetingSDK getInstance] getMeetingService] getCurrentMeetingInfo:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NEMeetingInfo * _Nonnull info) {
            if (info) {
                [weakSelf updateMeetingBtnWithInfo:info];
            }
        }];
    } else {
        _restoreMeetingBtn.hidden = YES;
    }
}

- (UIButton *)restoreMeetingBtn {
    if (!_restoreMeetingBtn) {
        _restoreMeetingBtn = [TimerButton buttonWithType:UIButtonTypeCustom];
        _restoreMeetingBtn.hidden = YES;
        [_restoreMeetingBtn addTarget:self
                               action:@selector(onRestoreMeetingAction:)
                     forControlEvents:UIControlEventTouchUpInside];
    }
    return _restoreMeetingBtn;
}

@end
