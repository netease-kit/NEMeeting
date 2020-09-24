//
//  BaseViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "BaseViewController.h"

@interface BaseViewController ()

@end

@implementation BaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"网易会议Demo";
    self.navigationController.navigationBar.barTintColor = UIColorFromRGB(0x2864FE);
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithTitle:@""
                                                             style:UIBarButtonItemStylePlain
                                                            target:nil
                                                            action:nil];
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    self.navigationItem.backBarButtonItem = item;
}

- (void)hiddenBackButton {
    [self.navigationController.navigationItem setHidesBackButton:YES];
    [self.navigationItem setHidesBackButton:YES];
    [self.navigationController.navigationBar.backItem setHidesBackButton:YES];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}

- (void)showErrorCode:(NSInteger)code msg:(NSString *)msg {
    NSString *show = [NSString stringWithFormat:@"error:%@, code:%@", msg, @(code)];
    [self.view makeToast:show duration:2 position:CSToastPositionCenter];
}


- (BOOL)shouldAutorotate {
    return NO;
}

@end
