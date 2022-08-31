// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "EnterMeetingVC.h"
#import "CheckBox.h"
#import "MeetingMenuSelectVC.h"
#import "MeetingConfigRepository.h"
#import "MeetingConfigEnum.h"
#import <IQKeyboardManager/IQKeyboardManager.h>

typedef NS_ENUM(NSInteger, MeetingMenuType) {
    MeetingMenuTypeToolbar = 1,
    MeetingMenuTypeMore = 2,
};

@interface EnterMeetingVC ()<CheckBoxDelegate, MeetingServiceListener,MeetingMenuSelectVCDelegate>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;

@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UILabel *titleLab;
@property (weak, nonatomic) IBOutlet UIButton *enterBtn;
@property (weak, nonatomic) IBOutlet UITextField *passworkInput;
@property (weak, nonatomic) IBOutlet UITextField *tagInput;
@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullToolbarMenuItems;
@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property (nonatomic, assign) MeetingMenuType currentType;
@end

@implementation EnterMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NEMeetingKit getInstance].getMeetingService addListener:self];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [IQKeyboardManager sharedManager].enable = YES;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [IQKeyboardManager sharedManager].enable = NO;
}

- (void)setupUI {
    self.type = _type;
    [_configCheckBox setItemTitleWithArray:@[@"入会时打开摄像头",
                                             @"入会时打开麦克风",
                                             @"显示会议持续时间"]];
    [_settingCheckBox setItemTitleWithArray:@[@"入会时关闭聊天菜单",
                                              @"入会时关闭邀请菜单",
                                              @"入会时隐藏最小化",
                                              @"使用默认会议设置",
                                              @"入会时关闭画廊模式",
                                              @"仅显示会议ID长号",
                                              @"仅显示会议ID短号",
                                              @"关闭摄像头切换",
                                              @"关闭音频模式切换",
                                              @"显示白板窗口",
                                              @"隐藏白板菜单按钮",
                                              @"关闭会中改名",
                                              @"隐藏Sip菜单",
                                              @"显示用户角色标签"
                                            ]];
    _settingCheckBox.delegate = self;
}

- (IBAction)onLeaveCurrentMeeting:(id)sender {
    WEAK_SELF(weakSelf);
    [[NEMeetingKit.getInstance getMeetingService] leaveCurrentMeeting:NO callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}


#pragma mark - Action
- (IBAction)onEnterMeetingAction:(id)sender {
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.meetingId =  _meetingIdInput.text;
    params.displayName = _nickInput.text;
    params.password = _passworkInput.text;
    params.tag = _tagInput.text;
    
    NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init];
    // 默认会议配置
    if (![self selectedSetting:MeetingSettingTypeDefaultSetting]) {
        options.noAudio = ![self selectedConfig:MeetingConfigTypeJoinOnAudio];
        options.noVideo = ![self selectedConfig:MeetingConfigTypeJoinOnVideo];
        options.showMeetingTime = [self selectedConfig:MeetingConfigTypeShowTime];
    } else {
        NESettingsService *settingService = NEMeetingKit.getInstance.getSettingsService;
        options.noAudio = !settingService.isTurnOnMyAudioWhenJoinMeetingEnabled;
        options.noVideo = !settingService.isTurnOnMyVideoWhenJoinMeetingEnabled;
        options.showMeetingTime = settingService.isShowMyMeetingElapseTimeEnabled;
    }
    options.meetingIdDisplayOption = [self meetingIdDisplayOption];
    options.noChat = [self selectedSetting:MeetingSettingTypeJoinOffChatroom];
    options.noInvite = [self selectedSetting:MeetingSettingTypeJoinOffInvitation];
    options.noMinimize = [self selectedSetting:MeetingSettingTypeHideMini];
    options.noGallery = [self selectedSetting:MeetingSettingTypeJoinOffGallery];
    options.noSwitchCamera = [self selectedSetting:MeetingSettingTypeOffSwitchCamera];
    options.noSwitchAudioMode = [self selectedSetting:MeetingSettingTypeOffSwitchAudio];
    options.noWhiteBoard = [self selectedSetting:MeetingSettingTypeHiddenWhiteboardButton];
    options.noRename = [self selectedSetting:MeetingSettingTypeOffReName];
    options.noSip = [self selectedSetting:MeetingSettingTypeHiddenSip];
    options.joinTimeout = [[MeetingConfigRepository getInstance] joinMeetingTimeout];
//    options.audioAINSEnabled = [[[NEMeetingKit getInstance] getSettingsService] isAudioAINSEnabled];
    options.showMemberTag = [self selectedSetting:MeetingSettingTypeShowRoleLabel];
    
    //白板相关设置
    options.defaultWindowMode = [self selectedSetting:MeetingSettingTypeShowWhiteboard] ? NEMeetingWindowModeWhiteBoard : NEMeetingWindowModeGallery;
    
    if ([MeetingConfigRepository getInstance].useMusicAudioProfile) {
        options.audioProfile = [NEAudioProfile createMusicAudioProfile];
    } else if ([MeetingConfigRepository getInstance].useSpeechAudioProfile) {
        options.audioProfile = [NEAudioProfile createSpeechAudioProfile];
    }
    options.noMuteAllAudio = [MeetingConfigRepository getInstance].noMuteAllAudio;
    options.noMuteAllVideo = [MeetingConfigRepository getInstance].noMuteAllVideo;
    options.fullToolbarMenuItems = _fullToolbarMenuItems;
    options.fullMoreMenuItems = _fullMoreMenuItems;

    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    // 匿名入会
    if (self.type == EnterMeetingAnonymity) {
        [NEMeetingKit.getInstance.getMeetingService anonymousJoinMeeting:params
                                                                    opts:options
                                                                callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
            [SVProgressHUD dismiss];
            if (resultCode != ERROR_CODE_SUCCESS) {
                [weakSelf showErrorCode:resultCode msg:resultMsg];
            }else {
                weakSelf.fullMoreMenuItems = nil;
                weakSelf.fullToolbarMenuItems = nil;
            }
        }];
        return;
    } 
    [[[NEMeetingKit getInstance] getMeetingService] joinMeeting:params
                                                           opts:options
                                                       callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }else {
            weakSelf.fullMoreMenuItems = nil;
            weakSelf.fullToolbarMenuItems = nil;
        }
    }];
}

- (void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:(CheckBox *)checkbox {
    if (checkbox != _settingCheckBox) return;
    if (index == 3) {
        _configCheckBox.disableAllItems = [self selectedSetting:MeetingSettingTypeDefaultSetting];
    }
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
    menuSeletedVC.seletedItems = items;
    menuSeletedVC.delegate = self;
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

- (void)updateNickname {
    WEAK_SELF(weakSelf);
    [[NEMeetingKit getInstance].getSettingsService getHistoryMeetingItem:^(NSInteger resultCode, NSString* resultMsg, NSArray<NEHistoryMeetingItem *> * items) {
        if (items && items.count > 0) {
            NSLog(@"NEHistoryMeetingItem: %@ %@ %@", @(resultCode), resultMsg, items[0]);
            if ([items[0].meetingId isEqualToString: weakSelf.meetingIdInput.text]) {
                weakSelf.nickInput.text = items[0].nickname;
            }
        }
    }];
}

#pragma mark - MeetingServiceListener
- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
    if (event.arg == MEETING_WAITING_VERIFY_PASSWORD) {
        [SVProgressHUD dismiss];
    }
    if (event.status == MEETING_STATUS_DISCONNECTING) {
        [self updateNickname];
    }
}

- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
    if (self.currentType == MeetingMenuTypeToolbar) {
        self.fullToolbarMenuItems = menuItems;
    }else {
        self.fullMoreMenuItems = menuItems;
    }
    [self showSeletedItemResult:menuItems];
}
#pragma mark - Setter && Getter
- (void)setType:(EnterMeetingType)type {
    _type = type;
    NSString *title = type == EnterMeetingJoin ? @"加入会议" : @"匿名入会";
    self.titleLab.text = title;
    [self.enterBtn setTitle:title forState:UIControlStateNormal];
}

- (BOOL)selectedConfig:(MeetingConfigType)configType {
    return [self.configCheckBox getItemSelectedAtIndex:configType];
}
- (BOOL)selectedSetting:(MeetingSettingType)settingType {
    return [self.settingCheckBox getItemSelectedAtIndex:settingType];
}

- (NEMeetingIdDisplayOption)meetingIdDisplayOption {
    if ([self selectedSetting:MeetingSettingTypeOnlyShowLongId]) {
        return DISPLAY_LONG_ID_ONLY;
    } else if ([self selectedSetting:MeetingSettingTypeOnlyShowShortId]) {
        return DISPLAY_SHORT_ID_ONLY;
    }
    return DISPLAY_ALL;
}

@end
