//
//  MainViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MainViewController.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"

@interface MainViewController ()

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
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

@end
