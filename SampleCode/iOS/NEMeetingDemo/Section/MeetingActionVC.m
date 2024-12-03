// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingActionVC.h"
#import "HistoryViewController.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "MainViewController.h"
#import "MeetingMenuSelectVC.h"
#import "MeetingSettingVC.h"
#import "NEMeetingLoginViewController.h"
typedef NS_ENUM(NSInteger, MeetingMenuType) {
  MeetingMenuTypeToolbar = 1,
  MeetingMenuTypeMore = 2,
  MeetingMenuTypeAction = 3,
};

@interface MeetingActionVC () <MeetingMenuSelectVCDelegate>

@property(nonatomic, strong) NSMutableArray<NEMeetingMenuItem *> *menuItems;

@property(nonatomic, strong) NSArray<NEMeetingMenuItem *> *fullToolbarMenuItems;

@property(nonatomic, strong) NSArray<NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property(nonatomic, assign) MeetingMenuType currentType;

@end

@implementation MeetingActionVC

- (void)viewDidLoad {
  [super viewDidLoad];
  self.title = @"网易会议Demo";
}

- (void)popToMainVC {
  __block UIViewController *targetVC = nil;
  [self.navigationController.viewControllers
      enumerateObjectsUsingBlock:^(__kindof UIViewController *_Nonnull obj, NSUInteger idx,
                                   BOOL *_Nonnull stop) {
        if ([obj isKindOfClass:[MainViewController class]]) {
          targetVC = obj;
          *stop = YES;
        }
      }];
  if (targetVC) {
    [self.navigationController popToViewController:targetVC animated:YES];
  }
}

#pragma mark - Actions
- (IBAction)doLogoutAction:(UIButton *)sender {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode != ERROR_CODE_SUCCESS) {
      [weakSelf showErrorCode:resultCode msg:resultMsg];
    } else {
      [[LoginInfoManager shareInstance] cleanLoginInfo];
      [weakSelf popToMainVC];
    }
  }];
}
- (IBAction)configToolbarMenuItems:(UIButton *)sender {
  self.currentType = MeetingMenuTypeToolbar;
  [self enterMenuVC:_fullToolbarMenuItems];
}

- (IBAction)configMoreMenuItems:(UIButton *)sender {
  self.currentType = MeetingMenuTypeMore;
  [self enterMenuVC:_fullMoreMenuItems];
}

- (IBAction)openHistory:(UIButton *)sender {
  [self.navigationController pushViewController:[[HistoryViewController alloc] init] animated:YES];
}
- (IBAction)openFeedback:(UIButton *)sender {
  __weak typeof(self) weakSelf = self;
  [[[NEMeetingKit getInstance] getFeedbackService]
      loadFeedbackView:^(NSInteger code, NSString *message, UIViewController *viewController) {
        if (code != 0) {
          return;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
          [weakSelf.navigationController pushViewController:viewController animated:YES];
        });
      }];
}

- (void)enterMenuVC:(NSArray *)items {
  MeetingMenuSelectVC *menuSeletedVC = [[MeetingMenuSelectVC alloc] init];
  menuSeletedVC.seletedItems = _fullMoreMenuItems;
  menuSeletedVC.delegate = self;
  menuSeletedVC.currentType = _currentType;
  [self.navigationController pushViewController:menuSeletedVC animated:YES];
}
- (void)showSeletedItemResult:(NSArray *)menuItems {
  NSString *string = @"已选";
  for (NEMeetingMenuItem *item in menuItems) {
    if ([item isKindOfClass:[NESingleStateMenuItem class]]) {
      NESingleStateMenuItem *single = (NESingleStateMenuItem *)item;
      [string stringByAppendingFormat:@"%@ ", single.singleStateItem.text];
    } else {
      NECheckableMenuItem *checkableItem = (NECheckableMenuItem *)item;
      [string stringByAppendingFormat:@"%@ ", checkableItem.checkedStateItem.text];
    }
  }
  [self.view makeToast:string];
}
- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
  if (self.currentType == MeetingMenuTypeToolbar) {
    self.fullToolbarMenuItems = menuItems;
  } else {
    self.fullMoreMenuItems = menuItems;
  }
  [self showSeletedItemResult:menuItems];
}

- (IBAction)doOpenMeetingSetting:(UIButton *)sender {
  MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
  [self.navigationController pushViewController:vc animated:YES];
}

@end
