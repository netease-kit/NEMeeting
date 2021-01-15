//
//  MainViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MainViewController.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "IMLoginVC.h"
#import "CustomViewController.h"
#import "TimerButton.h"
#import "MeetingControlVC.h"

@interface MainViewController ()<MeetingServiceListener>

@property (nonatomic, strong) UIButton *mulIMBtn;
@property (nonatomic, strong) TimerButton *restoreMeetingBtn;
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
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] tryAutoLogin:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        [SVProgressHUD dismiss];
        NSLog(@"resultMsg:%@",resultMsg);
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            MeetingControlVC *vc = [[MeetingControlVC alloc] init];
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    }];
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/MainViewController.m
=======
}

- (void)onEnterMulAction:(UIButton *)sender {
    IMLoginVC *vc = [[IMLoginVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/MainViewController.m
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
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/MainViewController.m

=======
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:self.mulIMBtn];
    self.navigationItem.rightBarButtonItem = item;
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/MainViewController.m
    [[UIApplication sharedApplication].keyWindow addSubview:self.restoreMeetingBtn];
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


- (void)onInjectedMenuItemClick:(NEMenuClickInfo *)clickInfo
                    meetingInfo:(NEMeetingInfo *)meetingInfo
                stateController:(NEMenuStateController)stateController {
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/MainViewController.m
    if(clickInfo.itemId==100){
        UIAlertController *actionSheet = [UIAlertController alertControllerWithTitle:@"音频管理" message:@"自定义音频订阅管理" preferredStyle:UIAlertControllerStyleAlert];

        [actionSheet addAction:[UIAlertAction actionWithTitle:@"订阅所有音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeAll: YES];
        }]];

        [actionSheet addAction:[UIAlertAction actionWithTitle:@"取消订阅所有音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeAll: NO];
        }]];

        [actionSheet addAction:[UIAlertAction actionWithTitle:@"订阅成员音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeSingle: YES];
        }]];
        [actionSheet addAction:[UIAlertAction actionWithTitle:@"取消订阅成员音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeSingle: NO];
        }]];
        [actionSheet addAction:[UIAlertAction actionWithTitle:@"批量订阅成员音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeBatch: YES];
        }]];
        [actionSheet addAction:[UIAlertAction actionWithTitle:@"批量取消订阅成员音频" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [self handleSubscribeBatch: NO];
        }]];
        [actionSheet addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        }]];
        // Present action sheet.

        UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
        UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
        if (!preVC) {
            preVC = keyWindow.rootViewController;
        }
        [preVC presentViewController:actionSheet animated:YES completion:nil];

    }else{
        _alert = [UIAlertController alertControllerWithTitle:@"" message:[NSString stringWithFormat:@"%@-%@",clickInfo,meetingInfo] preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            stateController(NO,nil);
        }];
        UIAlertAction *ignoreAction = [UIAlertAction actionWithTitle:@"忽略" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        }];
        UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确认" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            stateController(YES,nil);
        }];
        [_alert addAction:cancelAction];
        [_alert addAction:ignoreAction];
        [_alert addAction:okAction];
        UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
        _preVC = keyWindow.rootViewController.presentedViewController;
        if (!_preVC) {
            _preVC = keyWindow.rootViewController;
        }
        [_preVC presentViewController:_alert animated:YES completion:nil];
    }
}

- (void)handleSubscribeAll:(BOOL)subscribe {
    [[[NEMeetingSDK getInstance] getMeetingService] subscribeAllRemoteAudioStreams:subscribe
                                                                          callback:^(NSInteger resultCode, NSString *resultMsg, id resultData){
        NSString *toastString = [NSString stringWithFormat:@"subscribeAllRemoteAudioStreams code: %ld msg: %@", (long)resultCode, resultMsg];
        [[UIApplication sharedApplication].keyWindow makeToast:toastString duration:2 position:CSToastPositionCenter];
    }];
}

- (void)handleSubscribeSingle:(BOOL)subscribe {
    UIAlertController *editeDialog = [UIAlertController alertControllerWithTitle:@"音频订阅管理" message:@"请输入你需要订阅人的id" preferredStyle:UIAlertControllerStyleAlert];

    [editeDialog addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.placeholder = @"请输入用户accountId";
    }];

    //添加确定和取消按钮
    UIAlertAction *cacleAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {

    }];

    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
        NSString *accountId =  editeDialog.textFields[0].text;
        [[[NEMeetingSDK getInstance] getMeetingService] subscribeRemoteAudioStream:accountId
                                                                         subscribe:subscribe callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
            NSString *toastString = [NSString stringWithFormat:@"subscribeRemoteAudioStream code: %ld msg: %@", (long)resultCode, resultMsg];
            [[UIApplication sharedApplication].keyWindow makeToast:toastString duration:2 position:CSToastPositionCenter];
        }];
    }];
    [editeDialog addAction:cacleAction];
    [editeDialog addAction:sureAction];

    
=======
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:[NSString stringWithFormat:@"%@-%@",clickInfo,meetingInfo] preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        stateController(NO,nil);
    }];
    UIAlertAction *ignoreAction = [UIAlertAction actionWithTitle:@"忽略" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    }];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确认" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        stateController(YES,nil);
    }];
    [alert addAction:cancelAction];
    [alert addAction:ignoreAction];
    [alert addAction:okAction];
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/MainViewController.m
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
    if (!preVC) {
        preVC = keyWindow.rootViewController;
    }
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/MainViewController.m
    [preVC presentViewController:editeDialog animated:YES completion:nil];
}

- (void)handleSubscribeBatch:(BOOL)subscribe {
    UIAlertController *editeDialog = [UIAlertController alertControllerWithTitle:@"音频订阅管理" message:@"请输入你需要订阅人的id，用英文逗号分割" preferredStyle:UIAlertControllerStyleAlert];

    [editeDialog addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.placeholder = @"请输入用户accountId用英文逗号分割";
    }];

    //添加确定和取消按钮
    UIAlertAction *cacleAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {

    }];

    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
        NSString *list =  editeDialog.textFields[0].text;
        NSArray *listItems = [list componentsSeparatedByString:@","];
        
        [[[NEMeetingSDK getInstance] getMeetingService] subscribeRemoteAudioStreams:listItems
                                                                         subscribe:subscribe callback:^(NSInteger resultCode, NSString *resultMsg, NSArray <NSString *>* faildAccountIds) {
            NSString *faildIdString = [faildAccountIds componentsJoinedByString:@" "];
            NSString *toastString = [NSString stringWithFormat:@"subscribeRemoteAudioStreams code: %ld msg: %@ faildIds %@", (long)resultCode, resultMsg,faildIdString];
            [[UIApplication sharedApplication].keyWindow makeToast:toastString duration:2 position:CSToastPositionCenter];
            [self.view makeToast:toastString];
        }];
    }];
    [editeDialog addAction:cacleAction];
    [editeDialog addAction:sureAction];

    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
    if (!preVC) {
        preVC = keyWindow.rootViewController;
    }
    [preVC presentViewController:editeDialog animated:YES completion:nil];

}
=======
    [preVC presentViewController:alert animated:YES completion:nil];
    
}


>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/MainViewController.m

- (void)updateMeetingBtnWithInfo:(NEMeetingInfo *)info {
    NEMeetingStatus status = [[NEMeetingSDK getInstance] getMeetingService].getMeetingStatus;
    if (status != MEETING_STATUS_INMEETING_MINIMIZED) {
        return;;
    }

    NSLog(@"meetingId: %@ meetingUniqueId: %llu", info.meetingId, info.meetingUniqueId);
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
    }else{
        if(event.status == MEETING_STATUS_DISCONNECTING && _preVC != nil){
            [_preVC dismissViewControllerAnimated:YES completion:nil];
        }
        _restoreMeetingBtn.hidden = YES;
    }
}

- (void)onUnbind:(int)unBindType {
    NSString *msg = [NSString stringWithFormat:@"电视与遥控器解绑，原因:%d", unBindType];
    [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                  duration:2
                                                  position:CSToastPositionCenter];
    if(_preVC != nil){
        [_preVC dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)onTCProtocolUpgrade:(NETCProtocolUpgrade *)tcProtocolUpgrade {
    NSString *msg = [NSString stringWithFormat:@"遥控器与电视协议版本不同，遥控器的协议版本：%@，电视的协议版本：%@，是否兼容：%hhd", tcProtocolUpgrade.controllerProtocolVersion, tcProtocolUpgrade.tvProtocolVersion, tcProtocolUpgrade.isCompatible];
    [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                  duration:2
                                                  position:CSToastPositionCenter];
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
