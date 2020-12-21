//
//  MeetingControlVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MeetingControlVC.h"
#import "StartMeetingVC.h"
#import "EnterMeetingVC.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "SubscribeMeetingListVC.h"
#import "NESubscribeMeetingConfigVC.h"
#import "MeetingActionVC.h"
#import "NEMeetingLoginViewController.h"
#import "MainViewController.h"
@interface MeetingControlVC ()<NEAuthListener, NEControlListener>

@property (weak, nonatomic) IBOutlet UIView *subscribeListContainer;
@property (strong, nonatomic) SubscribeMeetingListVC *subscribeListVC;

@end

@implementation MeetingControlVC

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NEMeetingSDK getInstance] removeAuthListener:self];
    [[[NEMeetingSDK getInstance] getControlService] removeListener:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NEMeetingSDK getInstance] addAuthListener:self];
    [[[NEMeetingSDK getInstance] getControlService] addListener:self];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.subscribeListVC.view.frame = self.subscribeListContainer.bounds;
}

#pragma mark - Function
- (void)setupUI {
    [self hiddenBackButton];
    [self addChildViewController:self.subscribeListVC];
    [self.subscribeListContainer addSubview:self.subscribeListVC.view];
}

- (void)popToMainVC {
    __block UIViewController *targetVC = nil;
    [self.navigationController.viewControllers enumerateObjectsUsingBlock:^(__kindof UIViewController * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[MainViewController class]]) {
            targetVC = obj;
            *stop = YES;
        }
    }];
    if (targetVC) {
        [self.navigationController popToViewController:targetVC animated:YES];
    }
}

- (void)doBeKickedWithInfo:(NSString *)info {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [window makeToast:info duration:2 position:CSToastPositionCenter];
    [self doLogout];
}

- (void)doLogout {
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
        [[LoginInfoManager shareInstance] cleanLoginInfo];
        [weakSelf popToMainVC];
    }];
}

#pragma mark - Actions
- (IBAction)onCreateMeetingAction:(id)sender {
    StartMeetingVC *vc = [[StartMeetingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onJoinMeetingAction:(id)sender {
    EnterMeetingVC *vc = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"EnterMeetingVC"];
    vc.type = EnterMeetingNormal;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onSubscribeMeeting:(UIButton *)sender {
    NESubscribeMeetingConfigVC *vc = [[NESubscribeMeetingConfigVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onEnterActionVC:(id)sender {
    MeetingActionVC *vc = [[MeetingActionVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - <MeetingServiceListener>
- (void)onKickOut {
    [self doBeKickedWithInfo:@"您已在其他设备登录"];
}

- (void)onAuthInfoExpired {
    [self doBeKickedWithInfo:@"登录状态已过期，请重新登录"];
}

#pragma mark - <>
- (void)onStartMeetingResult:(NEControlResult *)result {
    if (result.code == ERROR_CODE_SUCCESS) {
        return;
    }
    NSString *msg = [NSString stringWithFormat:@"start: code:%d msg:%@", (int)result.code, result.message];
    if (self.presentedViewController) {
        [self.presentedViewController.view makeToast:msg
                                            duration:2
                                            position:CSToastPositionCenter];
    } else {
        [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                      duration:2
                                                      position:CSToastPositionCenter];
    }
}

- (void)onJoinMeetingResult:(NEControlResult *)result {
    if (result.code == ERROR_CODE_SUCCESS) {
        return;
    }
    NSString *msg = [NSString stringWithFormat:@"join:code:%d msg:%@", (int)result.code, result.message];
    if (self.presentedViewController) {
        [self.presentedViewController.view makeToast:msg
                                            duration:2
                                            position:CSToastPositionCenter];
    } else {
        [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                      duration:2
                                                      position:CSToastPositionCenter];
    }
}

- (void)onUnbind:(int)unBindType {
    NSString *msg = [NSString stringWithFormat:@"电视与遥控器解绑，原因:%d", unBindType];
    [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                  duration:2
                                                  position:CSToastPositionCenter];
}

- (void)onTCProtocolUpgrade:(NETCProtocolUpgrade *)tcProtocolUpgrade {
    NSString *msg = [NSString stringWithFormat:@"遥控器与电视协议版本不同，遥控器的协议版本：%@，电视的协议版本：%@，是否兼容：%hhd", tcProtocolUpgrade.controllerProtocolVersion, tcProtocolUpgrade.tvProtocolVersion, tcProtocolUpgrade.isCompatible];
    [[UIApplication sharedApplication].keyWindow makeToast:msg
                                                  duration:2
                                                  position:CSToastPositionCenter];
}

#pragma mark - Getter
- (SubscribeMeetingListVC *)subscribeListVC {
    if (!_subscribeListVC) {
        _subscribeListVC = [[SubscribeMeetingListVC alloc] init];
    }
    return _subscribeListVC;
}

@end
