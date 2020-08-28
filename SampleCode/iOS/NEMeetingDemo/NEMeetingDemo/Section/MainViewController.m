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

@interface MainViewController ()

@property (nonatomic, strong) UIButton *mulIMBtn;

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

- (void)onEnterMulAction:(UIButton *)sender {
    IMLoginVC *vc = [[IMLoginVC alloc] init];
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

@end
