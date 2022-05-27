//
//  StartMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "StartMeetingVC.h"
#import "MeetingSettingVC.h"
#import "CheckBox.h"
#import "MeetingMenuSelectVC.h"
#import <IQKeyboardManager/IQKeyboardManager.h>
#import <NEMeetingKit/NEMeetingKit.h>
#import <YYModel/YYModel.h>
#import "MeetingConfigRepository.h"

typedef NS_ENUM(NSInteger, MeetingMenuType) {
    MeetingMenuTypeToolbar = 1,
    MeetingMenuTypeMore = 2,
};

@interface StartMeetingVC ()<CheckBoxDelegate,MeetingMenuSelectVCDelegate,MeetingServiceListener>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;
/// 会议号输入框
@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
/// 昵称输入框
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
/// 菜单按钮IId输入框
@property (weak, nonatomic) IBOutlet UITextField *menuIdInput;
/// 菜单文本输入框
@property (weak, nonatomic) IBOutlet UITextField *menuTitleInput;

@property (weak, nonatomic) IBOutlet UIButton *settingBtn;
/// tag输入框
@property (weak, nonatomic) IBOutlet UITextField *tagInput;
/// 会议密码输入框
@property (weak, nonatomic) IBOutlet UITextField *passwordInput;

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
@property (nonatomic, readonly) BOOL disableRename;
@property (nonatomic, readonly) BOOL disableSip;
@property (nonatomic, readonly) BOOL showMemberTag;
@property (nonatomic, assign) BOOL audioOffAllowSelfOn;
@property (nonatomic, assign) BOOL audioOffNotAllowSelfOn;
@property (nonatomic, assign) BOOL videoOffAllowSelfOn;
@property (nonatomic, assign) BOOL videoOffNotAllowSelfOn;


@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;

@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullToolbarMenuItems;

@property (nonatomic, strong) NSArray <NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property (nonatomic, assign) MeetingMenuType currentType;

@end


@implementation StartMeetingVC

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
                                              @"关闭音频模式切换",
                                              @"展示白板",//10
                                              @"隐藏白板菜单按钮",
                                              @"关闭会中改名",
                                              @"开启云录制",
//                                              @"隐藏Sip菜单",
                                              @"显示用户角色标签",
                                              @"自动静音(可解除)",
                                              @"自动静音(不可解除)",
                                              @"自动关视频(可解除)",
                                              @"自动关视频(不可解除)",
    ]];
//    [_settingCheckBox setItemSelected:YES index:2];
    _settingCheckBox.delegate = self;
}
#pragma mark -----------------------------  自定义toolbar/更多 菜单  -----------------------------

- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
    if (self.currentType == MeetingMenuTypeToolbar) {
        self.fullToolbarMenuItems = menuItems;
    }else {
        self.fullMoreMenuItems = menuItems;
    }
    [self showSeletedItemResult:menuItems];
}
/// 结束会议
- (IBAction)doCloseMeeting:(id)sender {
    WEAK_SELF(weakSelf);
    [[NEMeetingKit.getInstance getMeetingService] leaveCurrentMeeting:YES callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}

#pragma mark - Function
- (void)doStartMeeting {
    NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init];
    params.displayName = _nickInput.text;
    params.meetingId = self.meetingId ? : _meetingIdInput.text;
    if (_passwordInput.text.length > 0) {
        params.password = _passwordInput.text;
    }
    params.tag = _tagInput.text;

    if (_menuTitleInput.text.length > 0) {
        params.extraData = _menuTitleInput.text;
    }
    
    NSMutableArray<NEMeetingControl*> *controls = [[NSMutableArray alloc]init];
    if (self.audioOffAllowSelfOn || self.audioOffNotAllowSelfOn) {
        NEMeetingControl* control = [NEMeetingControl createAudioControl];
        control.attendeeOff = self.audioOffAllowSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
        [controls addObject: control];
    }
    if (self.videoOffAllowSelfOn || self.videoOffNotAllowSelfOn) {
        NEMeetingControl* control = [NEMeetingControl createVideoControl];
        control.attendeeOff = self.videoOffAllowSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
        [controls addObject: control];
    }
    if (controls.count > 0) {
        params.controls = controls;
    }
    
    
    NEStartMeetingOptions *options = [[NEStartMeetingOptions alloc] init];
    if (![self useDefaultConfig]) {
        options.noVideo = ![self openVideoWhenJoinMeeting];
        options.noAudio = ![self openMicWhenJoinMeeting];
        options.showMeetingTime = [self showMeetingTime];
    } else {
        NESettingsService *settingService = NEMeetingKit.getInstance.getSettingsService;
        options.noAudio = !settingService.isTurnOnMyAudioWhenJoinMeetingEnabled;
        options.noVideo = !settingService.isTurnOnMyVideoWhenJoinMeetingEnabled;
        options.showMeetingTime = settingService.isShowMyMeetingElapseTimeEnabled;
    }
    options.meetingIdDisplayOption = [self meetingIdDisplayOption];
    options.noInvite = [self disableInvite];
    options.noChat = [self disableChat];
    options.noMinimize = [self disableMinimize];
    options.noGallery = [self disableGallery];
    options.noSwitchCamera = [self disableCameraSwitch];
    options.noSwitchAudioMode = [self disableAudioModeSwitch];
    options.noRename = [self disableRename];
//    options.noSip = [self disableSip];
    options.joinTimeout = [[MeetingConfigRepository getInstance] joinMeetingTimeout];
    options.showMemberTag = [self showMemberTag];
    
    //白板相关设置
    if ([self showWhiteboard]) {
        //设置默认展示白板窗口
        options.defaultWindowMode = NEMeetingWindowModeWhiteBoard;
    }
    //设置是否隐藏菜单栏白板创建按钮
    options.noWhiteBoard = [self hideWhiteboardMenueButton];
    options.noCloudRecord = ![self openRecord];
    if ([MeetingConfigRepository getInstance].useMusicAudioProfile) {
        options.audioProfile = [NEAudioProfile createMusicAudioProfile];
    } else if ([MeetingConfigRepository getInstance].useSpeechAudioProfile) {
        options.audioProfile = [NEAudioProfile createSpeechAudioProfile];
    }
//    if (options.audioProfile != nil) {
//        options.audioProfile.enableAINS = options.audioAINSEnabled;
//    }
    options.noMuteAllAudio = [MeetingConfigRepository getInstance].noMuteAllAudio;
    options.noMuteAllVideo = [MeetingConfigRepository getInstance].noMuteAllVideo;
    options.fullToolbarMenuItems = _fullToolbarMenuItems;
    options.fullMoreMenuItems = _fullMoreMenuItems;
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[NEMeetingKit getInstance].getMeetingService startMeeting:params
                                                          opts:options
                                                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            if (resultCode == MEETING_ERROR_FAILED_MEETING_ALREADY_EXIST) {
                [weakSelf showMessage:@"会议创建失败，该会议还在进行中"];
                return;
            }
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
    
    [[NEMeetingKit getInstance].getAccountService getAccontInfo:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            if (![result isKindOfClass:[NEAccountInfo class]] || result == nil) return;
            NEAccountInfo *accountInfo = result;
            self.meetingId = accountInfo.meetingId;
            NSString *meetingId = accountInfo.meetingId;
            if (accountInfo.shortMeetingId) {
                meetingId = [NSString stringWithFormat:@"%@(短号:%@)",meetingId,accountInfo.shortMeetingId];
            }
            weakSelf.meetingIdInput.text = meetingId;
        };
    }];
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
    if (event.status == MEETING_STATUS_DISCONNECTING) {
        [self updateNickname];
    }
}

#pragma mark - Actions
/// 创建会议
- (IBAction)onStartMeeting:(id)sender {
    [self doStartMeeting];
}
/// 自定义toolbar菜单
- (IBAction)configToolbarMenuItems:(UIButton *)sender {
    self.currentType = MeetingMenuTypeToolbar;
    [self enterMenuVC:_fullToolbarMenuItems];
}
/// 自定义更多菜单
- (IBAction)configMoreMenuItems:(UIButton *)sender {
    self.currentType = MeetingMenuTypeMore;
    [self enterMenuVC:_fullMoreMenuItems];
}
/// 添加菜单
- (IBAction)addMenuAction:(UIButton *)sender {
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
    if (!menuItems.count) return;
    
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
#pragma mark -----------------------------  CheckBoxDelegate  -----------------------------
- (void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:( CheckBox *)checkbox {
    if (checkbox != _settingCheckBox) return;
    
    if (index == 3) { //使用个人会议号
        if ([self useUserMeetingId]) {
            [self doGetUserMeetingId];
        }else {
            self.meetingIdInput.text = @"";
            self.meetingId = @"";
        }
    } else if (index == 4) {
        _configCheckBox.disableAllItems = [self useDefaultConfig];
        _settingBtn.hidden = ![self useDefaultConfig];
    }
    if (index == 16) {
        self.audioOffAllowSelfOn = item.selected;
    } else if (index == 17) {
        self.audioOffNotAllowSelfOn = item.selected;
    } else if (index == 18) {
        self.videoOffAllowSelfOn = item.selected;
    } else if (index == 19) {
        self.videoOffNotAllowSelfOn = item.selected;
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
- (BOOL)showWhiteboard {
    return [_settingCheckBox getItemSelectedAtIndex:10];
}
- (BOOL)hideWhiteboardMenueButton {
    return [_settingCheckBox getItemSelectedAtIndex:11];
}

- (BOOL)disableRename {
    return [_settingCheckBox getItemSelectedAtIndex:12];
}

- (BOOL)openRecord {
    return [_settingCheckBox getItemSelectedAtIndex:13];
}

//- (BOOL)disableSip {
//    return [_settingCheckBox getItemSelectedAtIndex:14];
//}

- (BOOL)showMemberTag {
    return [_settingCheckBox getItemSelectedAtIndex:14];
}

- (BOOL)audioOffAllowSelfOn {
    return [_settingCheckBox getItemSelectedAtIndex:15];
}

- (void)setAudioOffAllowSelfOn:(BOOL)audioOffAllowSelfOn {
    [_settingCheckBox setItemSelected:audioOffAllowSelfOn index:15];
    if (audioOffAllowSelfOn) {
        self.audioOffNotAllowSelfOn = NO;
    }
}

- (BOOL)audioOffNotAllowSelfOn {
    return [_settingCheckBox getItemSelectedAtIndex:16];
}

- (void)setAudioOffNotAllowSelfOn:(BOOL)audioOffNotAllowSelfOn {
    [_settingCheckBox setItemSelected:audioOffNotAllowSelfOn index:16];
    if (audioOffNotAllowSelfOn) {
        self.audioOffAllowSelfOn = NO;
    }
}

- (BOOL)videoOffAllowSelfOn {
    return [_settingCheckBox getItemSelectedAtIndex:17];
}

- (void)setVideoOffAllowSelfOn:(BOOL)videoOffAllowSelfOn {
    [_settingCheckBox setItemSelected:videoOffAllowSelfOn index:17];
    if (videoOffAllowSelfOn) {
        self.videoOffNotAllowSelfOn = NO;
    }
}

- (BOOL)videoOffNotAllowSelfOn {
    return [_settingCheckBox getItemSelectedAtIndex:18];
}

- (void)setVideoOffNotAllowSelfOn:(BOOL)videoOffNotAllowSelfOn {
    [_settingCheckBox setItemSelected:videoOffNotAllowSelfOn index:18];
    if (videoOffNotAllowSelfOn) {
        self.videoOffAllowSelfOn = NO;
    }
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

- (IBAction)sceneSettings:(id)sender {
}
@end
