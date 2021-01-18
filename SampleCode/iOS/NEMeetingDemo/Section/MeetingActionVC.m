//
//  MeetingActionVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MeetingActionVC.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "MeetingSettingVC.h"
#import "LoginInfoManager.h"
#import "NEMeetingLoginViewController.h"
#import "MainViewController.h"
#import "MeetingMenuSelectVC.h"

typedef NS_ENUM(NSInteger, MeetingMenuType) {
    MeetingMenuTypeToolbar = 1,
    MeetingMenuTypeMore = 2,
};

@interface MeetingActionVC ()<MeetingMenuSelectVCDelegate>

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;

@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullToolbarMenuItems;

@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property (nonatomic, assign) MeetingMenuType currentType;

@end

@implementation MeetingActionVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"网易会议Demo";
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

#pragma mark - Actions
- (IBAction)doLogoutAction:(UIButton *)sender {
    WEAK_SELF(weakSelf);
    [[NEMeetingSDK getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            [[LoginInfoManager shareInstance] cleanLoginInfo];
            [weakSelf popToMainVC];
        }
    }];
}

- (IBAction)doOpenControlAction:(UIButton *)sender {
    NEControlParams *params = [[NEControlParams alloc] init];
    NSString *account = [LoginInfoManager shareInstance].account;
    if (account.length > 4) {
        account = [account substringWithRange:NSMakeRange(account.length - 4, 4)];
    }
    params.displayName = account ?: @"我的昵称";
    NEControlOptions *options = [[NEControlOptions alloc] init];
//    NEControlMenuItem *settingItem = [[NEControlMenuItem alloc] init];
//    settingItem.title = @"测试设置Item";
//    NEControlMenuItem *shareItem = [[NEControlMenuItem alloc] init];
//    shareItem.title = @"测试分享Item";
//    options.settingMenu = settingItem;
//    options.shareMenu = shareItem;
    options.fullToolbarMenuItems = _fullToolbarMenuItems;
    options.fullMoreMenuItems = _fullMoreMenuItems;

    __weak typeof(self) weakSelf = self;
    [[[NEMeetingSDK getInstance] getControlService] openControlUI:params opts:options callback:^(NSInteger resultCode, NSString *resultMsg) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            weakSelf.fullMoreMenuItems = nil;
            weakSelf.fullToolbarMenuItems = nil;
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
- (void)enterMenuVC:(NSArray *)items {
    MeetingMenuSelectVC *menuSeletedVC = [[MeetingMenuSelectVC alloc] init];
    menuSeletedVC.seletedItems = _fullMoreMenuItems;
    menuSeletedVC.delegate = self;
//    __weak typeof(self)weakSelf = self;
//    [menuSeletedVC setMenuItemSelectCallback:^(NSArray <NEMeetingMenuItem *> * _Nonnull menuItems) {
//        weakSelf.fullToolbarMenuItems = menuItems;
//        [weakSelf showSeletedItemResult:menuItems];
//    }];
    [self.navigationController pushViewController:menuSeletedVC animated:YES];
}
- (void)showSeletedItemResult:(NSArray *)menuItems {
    NSString *string = @"已选";
    for (NEMeetingMenuItem *item in menuItems) {
        if ([item isKindOfClass:[NESingleStateMenuItem class]]) {
            NESingleStateMenuItem *single = (NESingleStateMenuItem *)item;
            [string stringByAppendingFormat:@"%@ ",single.singleStateItem.text];
        }else {
            NECheckableMenuItem *checkableItem = (NECheckableMenuItem *)item;
            [string stringByAppendingFormat:@"%@ ",checkableItem.checkedStateItem.text];
        }
    }
    [self.view makeToast:string];
}
- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
    if (self.currentType == MeetingMenuTypeToolbar) {
        self.fullToolbarMenuItems = menuItems;
    }else {
        self.fullMoreMenuItems = menuItems;
    }
    [self showSeletedItemResult:menuItems];
}
#pragma mark - 开启美颜预览
- (IBAction)doOpenBeautyAction:(UIButton *)sender {
//    bool enbale =  [[NEMeetingSDK getInstance].getSettingsService isBeautyFaceEnabled];
//    [[NEMeetingSDK getInstance].getSettingsService setBeautyFaceValue:9];
    [[NEMeetingSDK getInstance].getSettingsService getBeautyFaceValue:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        NSLog(@"NEMeetingSDK getBeautyFaceValue resultData  %@",resultData);
    }];
    __weak typeof(self) weakSelf = self;
    [[[NEMeetingSDK getInstance] getSettingsService] openBeautyUI:^(NSInteger resultCode, NSString *resultMsg) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}

- (IBAction)doOpenMeetingSetting:(UIButton *)sender {
    MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
