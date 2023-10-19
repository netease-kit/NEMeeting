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
@property (strong, nonatomic) IBOutlet UITextField *encryptionKeyInput;
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
  [_configCheckBox
      setItemTitleWithArray:@[ @"入会时打开摄像头", @"入会时打开麦克风", @"显示会议持续时间" ]];
  [_settingCheckBox setItemTitleWithArray:@[
    @"入会时关闭聊天菜单", @"入会时关闭邀请菜单", @"入会时隐藏最小化", @"使用默认会议设置",
    @"入会时关闭画廊模式", @"仅显示会议ID长号",   @"仅显示会议ID短号", @"关闭摄像头切换",
    @"关闭音频模式切换",   @"显示白板窗口",       @"隐藏白板菜单按钮", @"关闭会中改名",
    @"隐藏Sip菜单",        @"显示用户角色标签",   @"显示会议结束提醒", @"聊天室文件消息",
    @"聊天室图片消息",     @"开启静音检测",       @"关闭静音包",       @"显示屏幕共享者画面",
    @"显示白板共享者画面", @"设置白板透明", @"前置摄像头镜像",  @"显示麦克风浮窗",
    @"入会时隐藏直播菜单", @"开启音频共享", @"开启加密"
  ]];
  _settingCheckBox.delegate = self;
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeChatroomEnableFile];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeChatroomEnableImage];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeDetectMutedMic];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeUnpubAudioOnMute];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeShowScreenShareUserVideo];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeShowFloatingMicrophone];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeEnableFrontCameraMirror];
  [self.settingCheckBox setItemSelected:YES index:MeetingSettingTypeJoinOffLive];
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
  params.meetingNum = _meetingIdInput.text;
  params.displayName = _nickInput.text;
  params.password = _passworkInput.text;
  params.tag = _tagInput.text;

    // 是否开启媒体流加密
    if ([self selectedSetting:MeetingSettingTypeEnableEncryption]) {
      NEEncryptionConfig *encryptionConfig = [[NEEncryptionConfig alloc] init];
      encryptionConfig.encryptionMode = GMCryptoSM4ECB;
      encryptionConfig.encryptKey = _encryptionKeyInput.text;
      params.encryptionConfig = encryptionConfig;
    }
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
  // 是否显示直播按钮
  options.noLive = [self selectedSetting:MeetingSettingTypeJoinOffLive];
  options.noInvite = [self selectedSetting:MeetingSettingTypeJoinOffInvitation];
  options.noMinimize = [self selectedSetting:MeetingSettingTypeHideMini];
  options.noGallery = [self selectedSetting:MeetingSettingTypeJoinOffGallery];
  options.noSwitchCamera = [self selectedSetting:MeetingSettingTypeOffSwitchCamera];
  options.noSwitchAudioMode = [self selectedSetting:MeetingSettingTypeOffSwitchAudio];
  options.noWhiteBoard = [self selectedSetting:MeetingSettingTypeHiddenWhiteboardButton];
  options.noRename = [self selectedSetting:MeetingSettingTypeOffReName];
  options.noSip = [self selectedSetting:MeetingSettingTypeHiddenSip];
  options.joinTimeout = [[MeetingConfigRepository getInstance] joinMeetingTimeout];
  //    options.audioAINSEnabled = [[[NEMeetingKit getInstance] getSettingsService]
  //    isAudioAINSEnabled];
  options.showMemberTag = [self selectedSetting:MeetingSettingTypeShowRoleLabel];
  options.showMeetingRemainingTip = [self selectedSetting:MeetingSettingTypeShowMeeingRemainingTip];
  // 白板相关设置
  options.defaultWindowMode = [self selectedSetting:MeetingSettingTypeShowWhiteboard]
                                  ? NEMeetingWindowModeWhiteBoard
                                  : NEMeetingWindowModeGallery;
  // 聊天室 file、image消息收发
  options.chatroomConfig.enableFileMessage =
      [self selectedSetting:MeetingSettingTypeChatroomEnableFile];
  options.chatroomConfig.enableImageMessage =
      [self selectedSetting:MeetingSettingTypeChatroomEnableImage];

  // 音频设置
  BOOL isOpenAudioSettings = MeetingConfigRepository.getInstance.isOpenAudioSetting;
  if (isOpenAudioSettings) {
    NSString *profile = MeetingConfigRepository.getInstance.audioProfile;
    NSString *scenario = MeetingConfigRepository.getInstance.audioScenario;
    NEAudioProfile *audioProfile = [NEAudioProfile new];
    audioProfile.profile = profile.integerValue;
    audioProfile.scenario = scenario.integerValue;
    options.audioProfile = audioProfile;
  }
  //  if ([MeetingConfigRepository getInstance].useMusicAudioProfile) {
  //    options.audioProfile = [NEAudioProfile createMusicAudioProfile];
  //  } else if ([MeetingConfigRepository getInstance].useSpeechAudioProfile) {
  //    options.audioProfile = [NEAudioProfile createSpeechAudioProfile];
  //  }
  options.noMuteAllAudio = [MeetingConfigRepository getInstance].noMuteAllAudio;
  options.noMuteAllVideo = [MeetingConfigRepository getInstance].noMuteAllVideo;
  options.fullToolbarMenuItems = _fullToolbarMenuItems;
  options.fullMoreMenuItems = _fullMoreMenuItems;

  // 开启静音检测
  options.detectMutedMic = [self selectedSetting:MeetingSettingTypeDetectMutedMic];
  // 关闭静音包
  options.unpubAudioOnMute = [self selectedSetting:MeetingSettingTypeUnpubAudioOnMute];

  // 屏幕共享者摄像头画面显隐
  options.showScreenShareUserVideo =
      [self selectedSetting:MeetingSettingTypeShowScreenShareUserVideo];
  // 白板共享者摄像头画面显隐
  options.showWhiteboardShareUserVideo =
      [self selectedSetting:MeetingSettingTypeShowWhiteboardShareUseVideo];
  // 是否开启透明白板模式
  options.enableTransparentWhiteboard =
        [self selectedSetting:MeetingSettingTypeEnableTransparentWhiteboard];
  // 前置摄像头镜像
  options.enableFrontCameraMirror =
        [self selectedSetting:MeetingSettingTypeEnableFrontCameraMirror];
  // 麦克风悬浮显隐
  options.showFloatingMicrophone = [self selectedSetting:MeetingSettingTypeShowFloatingMicrophone];
  // 开启音频共享
  options.enableAudioShare = [self selectedSetting:MeetingSettingTypeEnabeAudioShare];
  WEAK_SELF(weakSelf);
  [SVProgressHUD show];
  // 匿名入会
  if (self.type == EnterMeetingAnonymity) {
    [NEMeetingKit.getInstance.getMeetingService
        anonymousJoinMeeting:params
                        opts:options
                    callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
                      [SVProgressHUD dismiss];
                      if (resultCode != ERROR_CODE_SUCCESS) {
                        [weakSelf showErrorCode:resultCode msg:resultMsg];
                      } else {
                        weakSelf.fullMoreMenuItems = nil;
                        weakSelf.fullToolbarMenuItems = nil;
                      }
                    }];
    return;
  }
  [[[NEMeetingKit getInstance] getMeetingService]
      joinMeeting:params
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
            if ([items[0].meetingNum isEqualToString: weakSelf.meetingIdInput.text]) {
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
