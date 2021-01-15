//
//  StartMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "StartMeetingVC.h"
#import "MeetingSettingVC.h"
#import "CheckBox.h"
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/StartMeetingVC.m
#import "MeetingMenuSelectVC.h"
=======
#import "MenuItemArrangementVC.h"
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/StartMeetingVC.m
#import <IQKeyboardManager/IQKeyboardManager.h>
#import <NEMeetingSDK/NEMeetingSDK.h>

typedef NS_ENUM(NSInteger, MeetingMenuType) {
    MeetingMenuTypeToolbar = 1,
    MeetingMenuTypeMore = 2,
};

@interface StartMeetingVC ()<CheckBoxDelegate,MeetingMenuSelectVCDelegate>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;
@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UITextField *menuIdInput;
@property (weak, nonatomic) IBOutlet UITextField *menuTitleInput;
@property (weak, nonatomic) IBOutlet UIButton *settingBtn;

@property (nonatomic, copy) NSString *meetingId;

@property (nonatomic, readonly) BOOL openVideoWhenJoinMeeting;
@property (nonatomic, readonly) BOOL openMicWhenJoinMeeting;
@property (nonatomic, readonly) BOOL showMeetingTime;
@property (nonatomic, readonly) BOOL useUserMeetingId;
@property (nonatomic, readonly) BOOL useDefaultConfig;
@property (nonatomic, readonly) BOOL disableChat;
@property (nonatomic, readonly) BOOL disableInvite;
@property (nonatomic, readonly) BOOL disableMinimize;
@property (nonatomic, readonly) BOOL disableGallery;
@property (nonatomic, readonly) BOOL disableCameraSwitch;
@property (nonatomic, readonly) BOOL disableAudioModeSwitch;

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;

<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/StartMeetingVC.m
@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullToolbarMenuItems;

@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property (nonatomic, assign) MeetingMenuType currentType;
=======
@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *fullToolbarMenuItems;

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *fullMoreMenuItems;
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/StartMeetingVC.m

@end


@implementation StartMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [IQKeyboardManager sharedManager].enable = YES;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [IQKeyboardManager sharedManager].enable = NO;
    _menuItems = nil;
}

- (void)setupUI {
    [_configCheckBox setItemTitleWithArray:@[@"入会时打开摄像头",
                                             @"入会时打开麦克风",
                                             @"显示会议持续时间"]];
    [_settingCheckBox setItemTitleWithArray:@[@"入会时关闭聊天菜单",
                                              @"入会时关闭邀请菜单",
                                              @"入会时隐藏最小化",
                                              @"使用个人会议号",
                                              @"使用默认会议设置",
                                              @"入会时关闭画廊模式",
                                              @"仅显示会议ID长号",
                                              @"仅显示会议ID短号",
                                              @"关闭摄像头切换",
                                              @"关闭音频模式切换"]];
    [_settingCheckBox setItemSelected:YES index:2];
    _settingCheckBox.delegate = self;
}
- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
    if (self.currentType == MeetingMenuTypeToolbar) {
        self.fullToolbarMenuItems = menuItems;
    }else {
        self.fullMoreMenuItems = menuItems;
    }
    [self showSeletedItemResult:menuItems];
}

#pragma mark - Function
- (void)doStartMeeting {
    NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init];
    params.displayName = _nickInput.text;
    params.meetingId = self.meetingId ? : _meetingIdInput.text;
    NEStartMeetingOptions *options = nil;
    if (![self useDefaultConfig]) {
        options = [[NEStartMeetingOptions alloc] init];
        options.noVideo = ![self openVideoWhenJoinMeeting];
        options.noAudio = ![self openMicWhenJoinMeeting];
        options.showMeetingTime = [self showMeetingTime];
        options.meetingIdDisplayOption = [self meetingIdDisplayOption];
        options.noInvite = [self disableInvite];
        options.noChat = [self disableChat];
        options.noMinimize = [self disableMinimize];
        options.injectedMoreMenuItems = _menuItems;
        options.noGallery = [self disableGallery];
        options.noSwitchCamera = [self disableCameraSwitch];
        options.noSwitchAudioMode = [self disableAudioModeSwitch];
    }
    
    options.fullToolbarMenuItems = _fullToolbarMenuItems;
    options.fullMoreMenuItems = _fullMoreMenuItems;
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[NEMeetingSDK getInstance].getMeetingService startMeeting:params
                                                          opts:options
                                                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            weakSelf.fullMoreMenuItems = nil;
            weakSelf.fullToolbarMenuItems = nil;
        }
    }];
}

- (void)doGetUserMeetingId {
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    
    [[NEMeetingSDK getInstance].getAccountService getAccontInfo:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            if (![result isKindOfClass:[NEAccountInfo class]] || result == nil) return;
            NEAccountInfo *accountInfo = result;
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/StartMeetingVC.m
            self.meetingId = accountInfo.meetingId;
            NSString *meetingId = accountInfo.meetingId;
            if (accountInfo.shortMeetingId) {
                meetingId = [NSString stringWithFormat:@"%@(短号:%@)",meetingId,accountInfo.shortMeetingId];
            }
            weakSelf.meetingIdInput.text = meetingId;
=======
            NSString *showText = [NSString stringWithFormat:@"%@(短号:%@)",accountInfo.meetingId,accountInfo.shortMeetingId];
            weakSelf.meetingIdInput.text = showText;
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/StartMeetingVC.m
        };
    }];
}

#pragma mark - Actions
- (IBAction)onStartMeeting:(id)sender {
    [self doStartMeeting];
}

- (IBAction)configToolbarMenuItems:(UIButton *)sender {
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/StartMeetingVC.m
    self.currentType = MeetingMenuTypeToolbar;
    [self enterMenuVC:_fullToolbarMenuItems];
}

- (IBAction)configMoreMenuItems:(UIButton *)sender {
    self.currentType = MeetingMenuTypeMore;
    [self enterMenuVC:_fullMoreMenuItems];
=======
    MenuItemArrangementVC *vc = (MenuItemArrangementVC *)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"MenuItemArrangementVC"];
    vc.menuItems = _fullToolbarMenuItems;
    __weak __typeof__ (self)weakSelf = self;
    vc.MenuItemSelectCallback = ^(NSMutableArray <NEMeetingMenuItem *> *menuItems) {
        weakSelf.fullToolbarMenuItems = menuItems;
    };
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)configMoreMenuItems:(UIButton *)sender {
    MenuItemArrangementVC *vc = (MenuItemArrangementVC *)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"MenuItemArrangementVC"];
    vc.menuItems = _fullMoreMenuItems;
    __weak __typeof__ (self)weakSelf = self;
    vc.MenuItemSelectCallback = ^(NSMutableArray <NEMeetingMenuItem *> *menuItems) {
        weakSelf.fullMoreMenuItems = menuItems;
    };
    [self.navigationController pushViewController:vc animated:YES];
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/StartMeetingVC.m
}

- (IBAction)addMenuAction:(UIButton *)sender {
    [self.view endEditing:YES];
    if (!_menuItems) {
        _menuItems = [NSMutableArray array];
    }
    NEMeetingMenuItem *item = [[NEMeetingMenuItem alloc] init];
    item.itemId = [_menuIdInput.text  intValue];
    if (item.itemId == 101) {
        item.title = @"显示会议信息";
    } else {
        item.title = _menuTitleInput.text;
    }
    [_menuItems addObject:item];

    NSString *msg = [NSString stringWithFormat:@"添加MenuItemId:%@ title:%@",
                     @(item.itemId), item.title];
    [self.view makeToast:msg];
}

- (IBAction)onEnterSettingAction:(id)sender {
    MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)enterMenuVC:(NSArray *)items {
    MeetingMenuSelectVC *menuSeletedVC = [[MeetingMenuSelectVC alloc] init];
    menuSeletedVC.seletedItems = items;
    menuSeletedVC.delegate = self;
    [self.navigationController pushViewController:menuSeletedVC animated:YES];
}
- (void)showSeletedItemResult:(NSArray *)menuItems {
    NSString *string = @"已选";
    for (NEMeetingMenuItem *item in menuItems) {
        if ([item isKindOfClass:[NESingleStateMenuItem class]]) {
            NESingleStateMenuItem *single = (NESingleStateMenuItem *)item;
            string = [string stringByAppendingFormat:@" %@",single.singleStateItem.text];
        }else {
            NECheckableMenuItem *checkableItem = (NECheckableMenuItem *)item;
            string = [string stringByAppendingFormat:@" %@",checkableItem.checkedStateItem.text];
        }
    }
    [self.view makeToast:string];
}
-(void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:( CheckBox *)checkbox {
    if (checkbox != _settingCheckBox) {
        return;
    }
    if (index == 3) { //使用个人会议号
        if ([self useUserMeetingId]) {
            [self doGetUserMeetingId];
        }else {
            self.meetingIdInput.text = @"";
<<<<<<< HEAD:SampleCode/iOS/NEMeetingDemo/NEMeetingDemo/Section/StartMeetingVC.m
            self.meetingId = @"";
=======
>>>>>>> upstream/master:SampleCode/iOS/NEMeetingDemo/Section/StartMeetingVC.m
        }
    } else if (index == 4) {
        _configCheckBox.disableAllItems = [self useDefaultConfig];
        _settingBtn.hidden = ![self useDefaultConfig];
    }
}

#pragma mark - Getter
- (BOOL)openVideoWhenJoinMeeting {
    return [_configCheckBox getItemSelectedAtIndex:0];
}

- (BOOL)openMicWhenJoinMeeting {
    return [_configCheckBox getItemSelectedAtIndex:1];
}

- (BOOL)showMeetingTime {
    return [_configCheckBox getItemSelectedAtIndex:2];
}

- (BOOL)disableChat {
    return [_settingCheckBox getItemSelectedAtIndex:0];
}

- (BOOL)disableInvite {
    return [_settingCheckBox getItemSelectedAtIndex:1];
}

- (BOOL)disableMinimize {
    return [_settingCheckBox getItemSelectedAtIndex:2];
}

- (BOOL)useUserMeetingId {
    return [_settingCheckBox getItemSelectedAtIndex:3];
}

- (BOOL)useDefaultConfig {
    return [_settingCheckBox getItemSelectedAtIndex:4];
}

- (BOOL)disableGallery {
    return [_settingCheckBox getItemSelectedAtIndex:5];
}

- (NEMeetingIdDisplayOption) meetingIdDisplayOption {
    if ([_settingCheckBox getItemSelectedAtIndex:6]) {
        return DISPLAY_LONG_ID_ONLY;
    } else if ([_settingCheckBox getItemSelectedAtIndex:7]) {
        return DISPLAY_SHORT_ID_ONLY;
    }
    return DISPLAY_ALL;
}

- (BOOL)disableCameraSwitch {
    return [_settingCheckBox getItemSelectedAtIndex:8];
}

- (BOOL)disableAudioModeSwitch {
    return [_settingCheckBox getItemSelectedAtIndex:9];
}

@end
