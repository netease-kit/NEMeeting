// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

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
  if (@available(iOS 15.0, *)) {
    UINavigationBarAppearance *navBar = [[UINavigationBarAppearance alloc] init];
    [navBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
    navBar.backgroundColor = UIColorFromRGB(0x2864FE);
    navBar.backgroundEffect = nil;
    self.navigationController.navigationBar.scrollEdgeAppearance = navBar;
    self.navigationController.navigationBar.standardAppearance = navBar;
  }
  [self setupSubviews];
  [self makeConstraints];
}
- (void)setupSubviews {
}
- (void)makeConstraints {
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
- (void)showMessage:(NSString *)message {
  [self.view makeToast:message duration:2 position:CSToastPositionCenter];
}
- (void)showErrorCode:(NSInteger)code msg:(NSString *)msg {
  NSString *show = [NSString stringWithFormat:@"error:%@, code:%@", msg, @(code)];
  [self.view makeToast:show duration:2 position:CSToastPositionCenter];
}

- (BOOL)shouldAutorotate {
  return YES;
}
- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
  return UIInterfaceOrientationMaskAll;
}
@end
