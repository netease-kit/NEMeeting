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
@interface MeetingControlVC ()<NEAuthListener>

@property (weak, nonatomic) IBOutlet UIView *subscribeListContainer;
@property (strong, nonatomic) SubscribeMeetingListVC *subscribeListVC;

@end

@implementation MeetingControlVC

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NEMeetingKit getInstance] removeAuthListener:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NEMeetingKit getInstance] addAuthListener:self];
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
    [[NEMeetingKit getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
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


- (void)onInjectedMenuItemClick:(NEMenuClickInfo *)clickInfo
                    meetingInfo:(NEMeetingInfo *)meetingInfo
                stateController:(NEMenuStateController)stateController {
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
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
    if (!preVC) {
        preVC = keyWindow.rootViewController;
    }
    [preVC presentViewController:alert animated:YES completion:nil];
    
}

#pragma mark - Getter
- (SubscribeMeetingListVC *)subscribeListVC {
    if (!_subscribeListVC) {
        _subscribeListVC = [[SubscribeMeetingListVC alloc] init];
    }
    return _subscribeListVC;
}

@end
