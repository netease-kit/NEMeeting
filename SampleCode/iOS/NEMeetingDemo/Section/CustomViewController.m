//
//  CustomViewController.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "CustomViewController.h"
#import "UIView+Toast.h"

@interface CustomViewController ()

@end

@implementation CustomViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.view makeToast:_msg
                duration:2
                position:CSToastPositionCenter];
}

- (IBAction)doExitAction:(id)sender {
    [self dismissViewControllerAnimated:YES
                             completion:nil];
}

@end
