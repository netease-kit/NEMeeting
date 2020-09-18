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

@interface MeetingControlVC ()<NEAuthListener>

@property (weak, nonatomic) IBOutlet UIView *subscribeListContainer;
@property (strong, nonatomic) SubscribeMeetingListVC *subscribeListVC;

@end

@implementation MeetingControlVC

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NEMeetingSDK getInstance] removeAuthListener:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NEMeetingSDK getInstance] addAuthListener:self];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.subscribeListVC.view.frame = self.subscribeListContainer.bounds;
}

#pragma mark - Function
- (void)setupUI {
    [self hiddenBackButton];
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.titleLabel.font = [UIFont systemFontOfSize:14];
    [btn setTitle:@"注销" forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(doLogout) forControlEvents:UIControlEventTouchUpInside];
    btn.size = CGSizeMake(60, 44);
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btn];
    [self addChildViewController:self.subscribeListVC];
    [self.subscribeListContainer addSubview:self.subscribeListVC.view];
}

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

- (IBAction)onSubscribeMeeting:(UIButton *)sender {
    NESubscribeMeetingConfigVC *vc = [[NESubscribeMeetingConfigVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - <MeetingServiceListener>
- (void)onKickOut {
    [self doBeKicked];
}

#pragma mark - Getter
- (SubscribeMeetingListVC *)subscribeListVC {
    if (!_subscribeListVC) {
        _subscribeListVC = [[SubscribeMeetingListVC alloc] init];
    }
    return _subscribeListVC;
}

@end
