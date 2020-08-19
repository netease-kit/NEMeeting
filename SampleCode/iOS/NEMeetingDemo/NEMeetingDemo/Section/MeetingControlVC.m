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

@interface MeetingControlVC ()<NEAuthListener>

@end

@implementation MeetingControlVC

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NEMeetingSDK getInstance] removeAuthListener:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self hiddenBackButton];
    [[NEMeetingSDK getInstance] addAuthListener:self];
}

#pragma mark - Function
- (void)popToLoginVC {
    __block UIViewController *targetVC = nil;
    [self.navigationController.viewControllers enumerateObjectsUsingBlock:^(__kindof UIViewController * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[LoginViewController class]]) {
            targetVC = obj;
            *stop = YES;
        }
    }];
    if (targetVC) {
        [self.navigationController popToViewController:targetVC animated:YES];
    }
}

- (void)doBeKicked {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [window makeToast:@"您已在其他设备登录" duration:2 position:CSToastPositionCenter];
    [self doLogout];
}

- (void)doLogout {
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
        [[LoginInfoManager shareInstance] cleanLoginInfo];
        [weakSelf popToLoginVC];
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

- (IBAction)onLogoutAction:(id)sender {
    [self doLogout];
}

#pragma mark - <MeetingServiceListener>
- (void)onKickOut {
    [self doBeKicked];
}

@end
