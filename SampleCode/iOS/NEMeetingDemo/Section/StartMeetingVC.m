// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "StartMeetingVC.h"
#import <IQKeyboardManager/IQKeyboardManager.h>
#import <NEJsonModel/NEJsonModel.h>
#import <NEMeetingKit/NEMeetingKit.h>
#import "CheckBox.h"
#import "LoginInfoManager.h"
#import "MeetingConfigEnum.h"
#import "MeetingConfigRepository.h"
#import "MeetingMenuSelectVC.h"
#import "MeetingSettingVC.h"

typedef NS_ENUM(NSInteger, MeetingMenuType) {
  MeetingMenuTypeToolbar = 1,
  MeetingMenuTypeMore = 2,
};

@interface StartMeetingVC () <CheckBoxDelegate, MeetingMenuSelectVCDelegate, MeetingServiceListener>

@property(weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property(weak, nonatomic) IBOutlet CheckBox *settingCheckBox;
/// 会议号输入框
@property(weak, nonatomic) IBOutlet UITextField *meetingIdInput;
/// 昵称输入框
@property(weak, nonatomic) IBOutlet UITextField *nickInput;
/// 菜单按钮IId输入框
@property(weak, nonatomic) IBOutlet UITextField *coHostInput;
/// 菜单文本输入框
@property(weak, nonatomic) IBOutlet UITextField *extraInput;
/// tag输入框
@property(weak, nonatomic) IBOutlet UITextField *tagInput;
/// 会议密码输入框
@property(weak, nonatomic) IBOutlet UITextField *passwordInput;
/// 会议主题输入框
@property(weak, nonatomic) IBOutlet UITextField *subjectInput;
/// 加密密钥输入框
@property(weak, nonatomic) IBOutlet UITextField *encryptionKeyInput;

@property(nonatomic, copy) NSString *meetingNum;
@property(nonatomic, assign) BOOL audioOffAllowSelfOn;
@property(nonatomic, assign) BOOL audioOffNotAllowSelfOn;
@property(nonatomic, assign) BOOL videoOffAllowSelfOn;
@property(nonatomic, assign) BOOL videoOffNotAllowSelfOn;

@property(nonatomic, strong) NSArray<NEMeetingMenuItem *> *fullToolbarMenuItems;

@property(nonatomic, strong) NSArray<NEMeetingMenuItem *> *fullMoreMenuItems;
// 自定义菜单类型：toolbar/更多
@property(nonatomic, assign) MeetingMenuType currentType;
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
}

- (void)setupUI {
  [_configCheckBox
      setItemTitleWithArray:@[ @"入会时打开摄像头", @"入会时打开麦克风", @"显示会议持续时间" ]];
  [_settingCheckBox setItemTitleWithArray:@[
    @"入会时关闭聊天菜单", @"入会时关闭邀请菜单", @"入会时隐藏最小化",
    @"使用个人会议号",     @"使用默认会议设置",   @"入会时关闭画廊模式",
    @"仅显示会议ID长号",   @"仅显示会议ID短号",   @"关闭摄像头切换",
    @"关闭音频模式切换",
    @"展示白板",  // 10
    @"隐藏白板菜单按钮",   @"关闭会中改名",       @"开启云录制",
    @"隐藏Sip菜单",        @"显示用户角色标签",   @"自动静音(可解除)",
    @"自动静音(不可解除)", @"自动关视频(可解除)", @"自动关视频(不可解除)",
    @"显示会议结束提醒",   @"聊天室文件消息",     @"聊天室图片消息",
    @"开启静音检测",       @"关闭静音包",         @"显示屏幕共享者画面",
    @"显示白板共享者画面", @"设置白板透明",       @"前置摄像头镜像",
    @"显示麦克风浮窗",     @"入会时隐藏直播菜单", @"开启音频共享",
    @"开启加密",           @"显示云录制菜单按钮", @"显示云录制过程UI",
    @"开启等候室",         @"允许音频设备切换"
  ]];
  _settingCheckBox.delegate = self;
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeChatroomEnableFile];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeChatroomEnableImage];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeDetectMutedMic];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeUnpubAudioOnMute];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeShowScreenShareUserVideo];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeShowFloatingMicrophone];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeEnableFrontCameraMirror];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeJoinOffLive];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeShowCloudRecordMenuItem];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeShowCloudRecordingUI];
  [self.settingCheckBox setItemSelected:YES index:CreateMeetingSettingTypeEnableAudioDeviceSwitch];
}
#pragma mark-----------------------------  自定义toolbar/更多 菜单  -----------------------------

- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems {
  if (self.currentType == MeetingMenuTypeToolbar) {
    self.fullToolbarMenuItems = menuItems;
  } else {
    self.fullMoreMenuItems = menuItems;
  }
  [self showSeletedItemResult:menuItems];
}
/// 结束会议
- (IBAction)doCloseMeeting:(id)sender {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit.getInstance getMeetingService]
      leaveCurrentMeeting:YES
                 callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
                   if (resultCode != ERROR_CODE_SUCCESS) {
                     [weakSelf showErrorCode:resultCode msg:resultMsg];
                   }
                 }];
}

#pragma mark - Function
- (void)doStartMeeting {
  NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init];
  // 昵称
  params.displayName = _nickInput.text;
  // 会议号
  params.meetingNum = self.meetingNum ?: _meetingIdInput.text;
  // 会议密码
  params.password = _passwordInput.text.length ? _passwordInput.text : nil;
  // 标签
  params.tag = _tagInput.text;
  // 额外参数
  params.extraData = _extraInput.text.length ? _extraInput.text : nil;
  // 会议主题
  params.subject = _subjectInput.text;
  // 是否开启媒体流加密
  if ([self selectedSetting:CreateMeetingSettingTypeEnableEncryption]) {
    NEEncryptionConfig *encryptionConfig = [[NEEncryptionConfig alloc] init];
    encryptionConfig.encryptionMode = GMCryptoSM4ECB;
    encryptionConfig.encryptKey = _encryptionKeyInput.text;
    params.encryptionConfig = encryptionConfig;
  }
  NEWatermarkConfig *watermarkConfig = [[NEWatermarkConfig alloc] init];
  watermarkConfig.name = [self accountName:params.displayName];
  params.watermarkConfig = watermarkConfig;
  // 联席主持人配置
  if (_coHostInput.text.length) {
    NSString *cohostText = _coHostInput.text;
    NSData *data = [cohostText dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data
                                                        options:NSJSONReadingMutableContainers
                                                          error:nil];
    if (dic) params.roleBinds = dic.mutableCopy;
  }

  NSMutableArray<NEMeetingControl *> *controls = @[].mutableCopy;
  if (self.audioOffAllowSelfOn || self.audioOffNotAllowSelfOn) {
    NEMeetingControl *control = [NEMeetingControl createAudioControl];
    control.attendeeOff =
        self.audioOffAllowSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
    [controls addObject:control];
  }
  if (self.videoOffAllowSelfOn || self.videoOffNotAllowSelfOn) {
    NEMeetingControl *control = [NEMeetingControl createVideoControl];
    control.attendeeOff =
        self.videoOffAllowSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
    [controls addObject:control];
  }
  params.controls = controls.count ? controls : nil;

  NEStartMeetingOptions *options = [[NEStartMeetingOptions alloc] init];
  if (![self selectedSetting:CreateMeetingSettingTypeDefaultSetting]) {
    options.noVideo = ![self selectedConfig:MeetingConfigTypeJoinOnVideo];
    options.noAudio = ![self selectedConfig:MeetingConfigTypeJoinOnAudio];
    options.showMeetingTime = [self selectedConfig:MeetingConfigTypeShowTime];
  } else {
    NESettingsService *settingService = NEMeetingKit.getInstance.getSettingsService;
    options.noAudio = !settingService.isTurnOnMyAudioWhenJoinMeetingEnabled;
    options.noVideo = !settingService.isTurnOnMyVideoWhenJoinMeetingEnabled;
    options.showMeetingTime = settingService.isShowMyMeetingElapseTimeEnabled;
  }
  options.meetingIdDisplayOption = [self meetingIdDisplayOption];
  options.noInvite = [self selectedSetting:CreateMeetingSettingTypeJoinOffInvitation];
  options.noChat = [self selectedSetting:CreateMeetingSettingTypeJoinOffChatroom];
  options.noLive = [self selectedSetting:CreateMeetingSettingTypeJoinOffLive];
  options.noMinimize = [self selectedSetting:CreateMeetingSettingTypeHideMini];
  options.noGallery = [self selectedSetting:CreateMeetingSettingTypeJoinOffGallery];
  options.noSwitchCamera = [self selectedSetting:CreateMeetingSettingTypeOffSwitchCamera];
  options.noSwitchAudioMode = [self selectedSetting:CreateMeetingSettingTypeOffSwitchAudio];
  options.noRename = [self selectedSetting:CreateMeetingSettingTypeOffReName];
  options.noSip = [self selectedSetting:CreateMeetingSettingTypeHiddenSip];
  options.joinTimeout = [[MeetingConfigRepository getInstance] joinMeetingTimeout];
  options.showMemberTag = [self selectedSetting:CreateMeetingSettingTypeShowRoleLabel];
  options.showMeetingRemainingTip =
      [self selectedSetting:CreateMeetingSettingTypeShowMeeingRemainingTip];

  options.chatroomConfig.enableFileMessage =
      [self selectedSetting:CreateMeetingSettingTypeChatroomEnableFile];
  options.chatroomConfig.enableImageMessage =
      [self selectedSetting:CreateMeetingSettingTypeChatroomEnableImage];
  // 白板相关设置
  options.defaultWindowMode = [self selectedSetting:CreateMeetingSettingTypeShowWhiteboard]
                                  ? NEMeetingWindowModeWhiteBoard
                                  : NEMeetingWindowModeGallery;
  // 设置是否隐藏菜单栏白板创建按钮
  options.noWhiteBoard = [self selectedSetting:CreateMeetingSettingTypeHiddenWhiteboardButton];
  options.noCloudRecord = ![self selectedSetting:CreateMeetingSettingTypeOpenCloudRecord];

  // 音频开关
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
  options.detectMutedMic = [self selectedSetting:CreateMeetingSettingTypeDetectMutedMic];
  // 关闭静音包
  options.unpubAudioOnMute = [self selectedSetting:CreateMeetingSettingTypeUnpubAudioOnMute];
  // 屏幕共享者摄像头画面显隐
  options.showScreenShareUserVideo =
      [self selectedSetting:CreateMeetingSettingTypeShowScreenShareUserVideo];
  // 白板共享者摄像头画面显隐
  options.showWhiteboardShareUserVideo =
      [self selectedSetting:CreateMeetingSettingTypeShowWhiteboardShareUseVideo];
  // 是否开启透明白板模式
  options.enableTransparentWhiteboard =
      [self selectedSetting:CreateMeetingSettingTypeEnableTransparentWhiteboard];
  options.enableFrontCameraMirror =
      [self selectedSetting:CreateMeetingSettingTypeEnableFrontCameraMirror];
  // 麦克风悬浮显隐
  options.showFloatingMicrophone =
      [self selectedSetting:CreateMeetingSettingTypeShowFloatingMicrophone];
  // 开启音频共享
  options.enableAudioShare = [self selectedSetting:CreateMeetingSettingTypeEnableAudioShare];
  // 配置是否展示云录制菜单按钮
  options.showCloudRecordingUI =
      [self selectedSetting:CreateMeetingSettingTypeShowCloudRecordingUI];
  // 配置是否展示云录制过程中的UI提示
  options.showCloudRecordMenuItem =
      [self selectedSetting:CreateMeetingSettingTypeShowCloudRecordMenuItem];

  options.enableWaitingRoom = [self selectedSetting:CreateMeetingSettingTypeEnableWaitingRoom];
  options.enableAudioDeviceSwitch =
      [self selectedSetting:CreateMeetingSettingTypeEnableAudioDeviceSwitch];
  WEAK_SELF(weakSelf);
  [SVProgressHUD show];
  [[NEMeetingKit getInstance].getMeetingService
      startMeeting:params
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

- (NSString *)accountName:(NSString *)defaultName {
  NSString *ret = [LoginInfoManager shareInstance].account;
  if (ret.length == 0) ret = defaultName;
  return ret;
}

- (void)doGetUserMeetingNum {
  WEAK_SELF(weakSelf);
  [SVProgressHUD show];

  [[NEMeetingKit getInstance].getAccountService
      getAccontInfo:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
          [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
          if (![result isKindOfClass:[NEAccountInfo class]] || result == nil) return;
          NEAccountInfo *accountInfo = result;
          self.meetingNum = accountInfo.meetingNum;
          NSString *meetingNum = accountInfo.meetingNum;
          if (accountInfo.shortMeetingNum) {
            meetingNum =
                [NSString stringWithFormat:@"%@(短号:%@)", meetingNum, accountInfo.shortMeetingNum];
          }
          weakSelf.meetingIdInput.text = meetingNum;
        };
      }];
}

- (void)updateNickname {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit getInstance].getSettingsService
      getHistoryMeetingItem:^(NSInteger resultCode, NSString *resultMsg,
                              NSArray<NEHistoryMeetingItem *> *items) {
        if (items && items.count > 0) {
          NSLog(@"NEHistoryMeetingItem: %@ %@ %@", @(resultCode), resultMsg, items[0]);
          if ([items[0].meetingNum isEqualToString:weakSelf.meetingIdInput.text]) {
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
      string = [string stringByAppendingFormat:@" %@", single.singleStateItem.text];
    } else {
      NECheckableMenuItem *checkableItem = (NECheckableMenuItem *)item;
      string = [string stringByAppendingFormat:@" %@", checkableItem.checkedStateItem.text];
    }
  }
  [self.view makeToast:string];
}
#pragma mark-----------------------------  CheckBoxDelegate  -----------------------------
- (void)checkBoxItemdidSelected:(UIButton *)item
                        atIndex:(NSUInteger)index
                       checkBox:(CheckBox *)checkbox {
  if (checkbox != _settingCheckBox) return;

  if (index == 3) {  // 使用个人会议号
    if ([self selectedSetting:CreateMeetingSettingTypeUsePid]) {
      [self doGetUserMeetingNum];
    } else {
      self.meetingIdInput.text = @"";
      self.meetingNum = @"";
    }
  } else if (index == 4) {
    _configCheckBox.disableAllItems = [self selectedSetting:CreateMeetingSettingTypeDefaultSetting];
  }
  if (index == CreateMeetingSettingTypeAutoMuteAudio) {
    self.audioOffAllowSelfOn = item.selected;
  } else if (index == CreateMeetingSettingTypeAutoMuteAudioNotRemove) {
    self.audioOffNotAllowSelfOn = item.selected;
  } else if (index == CreateMeetingSettingTypeAutoMuteVideo) {
    self.videoOffAllowSelfOn = item.selected;
  } else if (index == CreateMeetingSettingTypeAutoMuteVideoNotRemove) {
    self.videoOffNotAllowSelfOn = item.selected;
  }
}

#pragma mark - Getter
- (BOOL)selectedConfig:(MeetingConfigType)configType {
  return [self.configCheckBox getItemSelectedAtIndex:configType];
}
- (BOOL)selectedSetting:(CreateMeetingSettingType)settingType {
  return [self.settingCheckBox getItemSelectedAtIndex:settingType];
}
- (BOOL)audioOffAllowSelfOn {
  return [self selectedSetting:CreateMeetingSettingTypeAutoMuteAudio];
}
- (void)setAudioOffAllowSelfOn:(BOOL)audioOffAllowSelfOn {
  [_settingCheckBox setItemSelected:audioOffAllowSelfOn
                              index:CreateMeetingSettingTypeAutoMuteAudio];
  if (audioOffAllowSelfOn) {
    self.audioOffNotAllowSelfOn = NO;
  }
}

- (BOOL)audioOffNotAllowSelfOn {
  return [self selectedSetting:CreateMeetingSettingTypeAutoMuteAudioNotRemove];
}

- (void)setAudioOffNotAllowSelfOn:(BOOL)audioOffNotAllowSelfOn {
  [_settingCheckBox setItemSelected:audioOffNotAllowSelfOn
                              index:CreateMeetingSettingTypeAutoMuteAudioNotRemove];
  if (audioOffNotAllowSelfOn) {
    self.audioOffAllowSelfOn = NO;
  }
}

- (BOOL)videoOffAllowSelfOn {
  return [self selectedSetting:CreateMeetingSettingTypeAutoMuteVideo];
}

- (void)setVideoOffAllowSelfOn:(BOOL)videoOffAllowSelfOn {
  [_settingCheckBox setItemSelected:videoOffAllowSelfOn
                              index:CreateMeetingSettingTypeAutoMuteVideo];
  if (videoOffAllowSelfOn) {
    self.videoOffNotAllowSelfOn = NO;
  }
}

- (BOOL)videoOffNotAllowSelfOn {
  return [self selectedSetting:CreateMeetingSettingTypeAutoMuteVideoNotRemove];
}

- (void)setVideoOffNotAllowSelfOn:(BOOL)videoOffNotAllowSelfOn {
  [_settingCheckBox setItemSelected:videoOffNotAllowSelfOn
                              index:CreateMeetingSettingTypeAutoMuteVideoNotRemove];
  if (videoOffNotAllowSelfOn) {
    self.videoOffAllowSelfOn = NO;
  }
}

- (NEMeetingIdDisplayOption)meetingIdDisplayOption {
  if ([self selectedSetting:CreateMeetingSettingTypeOnlyShowLongId]) {
    return DISPLAY_LONG_ID_ONLY;
  } else if ([self selectedSetting:CreateMeetingSettingTypeOnlyShowShortId]) {
    return DISPLAY_SHORT_ID_ONLY;
  }
  return DISPLAY_ALL;
}

@end
