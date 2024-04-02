// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NSEditSubscribeMeetingVC.h"
#import "UIView+Toast.h"
@interface NSEditSubscribeMeetingVC ()
@property(nonatomic, strong) NEMeetingItem *item;
@property(nonatomic, assign) BOOL muteAllAudio;
@property(nonatomic, assign) BOOL muteAllVideo;
@property(nonatomic, assign) BOOL allowAudioSelfOn;
@property(nonatomic, assign) BOOL allowVideoSelfOn;
@end

@implementation NSEditSubscribeMeetingVC
+ (instancetype)editWithItem:(NEMeetingItem *)item {
  NSEditSubscribeMeetingVC *vc = [NSEditSubscribeMeetingVC new];
  vc.item = item;
  vc.muteAllAudio = item.settings.currentAudioControl.attendeeOff != AttendeeOffTypeNone;
  vc.muteAllVideo = item.settings.currentVideoControl.attendeeOff != AttendeeOffTypeNone;
  vc.allowAudioSelfOn =
      item.settings.currentAudioControl.attendeeOff == AttendeeOffTypeOffAllowSelfOn;
  vc.allowVideoSelfOn =
      item.settings.currentVideoControl.attendeeOff == AttendeeOffTypeOffAllowSelfOn;
  return vc;
}
- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  self.title = @"编辑会议";
  [self setupNavigationBar];
  [self setupSubviews];
}
- (void)setupNavigationBar {
  self.navigationItem.rightBarButtonItem =
      [[UIBarButtonItem alloc] initWithTitle:@"提交"
                                       style:UIBarButtonItemStylePlain
                                      target:self
                                      action:@selector(commitAction)];
}
- (void)setupSubviews {
  self.groups = [[NEMeetingKit getInstance].getSettingsService isMeetingLiveEnabled] ?
                @[
                    [self titleGroup],
                    [self timeGroup],
                    [self pwdGroup],
                    [self cloudRecordGroup],
                    [self openSipGroup],
                    [self liveGroup],
                    [self extraDataGroup],
                    [self cohostGroup],
                    [self muteAudioGroup],
                    [self muteVideoGroup]
                ].mutableCopy
                :
                @[
                    [self titleGroup],
                    [self timeGroup],
                    [self pwdGroup],
                    [self cloudRecordGroup],
                    [self openSipGroup],
                    [self extraDataGroup],
                    [self cohostGroup],
                    [self muteAudioGroup],
                    [self muteVideoGroup]
                ].mutableCopy;

  // 密码
  if (self.item.password) {
    NEFromRow *row = [self rowWithTag:@"kMeetingKey"];
    [self insertPassworkRowBelow:row];
  }
  // 直播
  if (self.item.live.enable) {
    NEFromRow *row = [self rowWithTag:@"kMeetingLive"];
    [self insertRow:[self liveLevelRow] below:row];
  }
}

#pragma mark-----------------------------  Group  -----------------------------
- (NEFromGroup *)titleGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *titleRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingTitle"];
  titleRow.title = @"会议主题";
  titleRow.value = self.item.subject;
  titleRow.config = @{
    @"placeholder" : @"请输入会议主题",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
  };
  titleRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.subject = newValue;
  };
  [group.rows addObject:titleRow];
  return group;
}
- (NEFromGroup *)timeGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *startTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingStartTime"];
  startTimeRow.title = @"开始时间";
  startTimeRow.value = @(_item.startTime / 1000);
  uint64_t currentTimeStamp = [[NSDate date] timeIntervalSince1970];
  currentTimeStamp = (currentTimeStamp - currentTimeStamp % (30 * 60) + 30 * 60);
  startTimeRow.config = @{
    @"minDate" : @(currentTimeStamp),
    @"maxDate" : @(currentTimeStamp * 7 * 24 * 60 * 60),
  };
  startTimeRow.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
  startTimeRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    [weakSelf adjustDateTime];
  };
  NEFromRow *endTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingEndTime"];
  endTimeRow.title = @"结束时间";
  endTimeRow.value = @(_item.endTime / 1000);
  endTimeRow.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
  endTimeRow.config = @{
    @"minDate" : @(_item.startTime / 1000 + 60 * 30),
    @"maxDate" : @(_item.endTime / 1000 + 24 * 60 * 60),
  };
  endTimeRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    [weakSelf adjustDateTime];
  };
  [group.rows addObjectsFromArray:@[ startTimeRow, endTimeRow ]];
  return group;
}
- (NEFromGroup *)pwdGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *meetingKeyRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingKey"];
  meetingKeyRow.title = @"会议密码";
  meetingKeyRow.value = @(self.item.password != nil);
  meetingKeyRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    BOOL isShowPassword = [newValue boolValue];
    if (isShowPassword) {
      [weakSelf insertPassworkRowBelow:row];
    } else {
      weakSelf.item.password = nil;
      [weakSelf deleteRowWithTag:@"kMeetingPassworkInput"];
    }
  };
  [group.rows addObject:meetingKeyRow];

  NEFromRow *enableWaitingRoomRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                                       tag:@"kWaitingRoom"];
  enableWaitingRoomRow.title = @"等候室";
  enableWaitingRoomRow.value = @(self.item.waitingRoomEnabled);
  enableWaitingRoomRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.waitingRoomEnabled = [newValue boolValue];
  };
  [group.rows addObject:enableWaitingRoomRow];
  return group;
}
- (NEFromGroup *)cloudRecordGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *openRecord = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                             tag:@"kMeetingOpenRecord"];
  openRecord.title = @"开启云端录制";
  openRecord.value = @(self.item.settings.cloudRecordOn);
  openRecord.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.settings.cloudRecordOn = [newValue boolValue];
  };
  [group.rows addObject:openRecord];
  return group;
}
/// 云端录制
- (NEFromGroup *)openSipGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *openSIPRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingOpenSIP"];
  openSIPRow.title = @"开启SIP";
  __weak typeof(self) weakSelf = self;
  openSIPRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.noSip = ![newValue boolValue];
  };
  openSIPRow.value = @(!self.item.noSip);
  [group.rows addObject:openSIPRow];
  return group;
}
- (NEFromGroup *)liveGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *liveRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLive"];
  liveRow.title = @"开启直播";
  liveRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.live.enable = [newValue boolValue];
    if (weakSelf.item.live.enable) {
      [weakSelf insertRow:[weakSelf liveLevelRow] below:row];
    } else {
      [weakSelf deleteRowWithTag:@"kMeetingLiveLevel"];
    }
  };
  liveRow.value = @(self.item.live.enable);
  [group.rows addObject:liveRow];
  return group;
}
- (NEFromRow *)liveLevelRow {
  __weak typeof(self) weakSelf = self;
  NEFromRow *liveLevelRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                               tag:@"kMeetingLiveLevel"];
  liveLevelRow.title = @"仅本企业员工可观看";
  liveLevelRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.live.liveWebAccessControlLevel =
        [newValue boolValue] ? NEMeetingLiveAuthLevelAppToken : NEMeetingLiveAuthLevelToken;
  };
  liveLevelRow.value = @([self isLiveLevel]);
  return liveLevelRow;
}
- (NEFromGroup *)extraDataGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *extraDataGroupRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput
                                                    tag:@"kMeetingExtraData"];
  extraDataGroupRow.title = @"扩展字段";
  extraDataGroupRow.value = self.item.extraData;
  extraDataGroupRow.config = @{
    @"placeholder" : @"自定义扩展字段",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
  };
  extraDataGroupRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.extraData = newValue;
  };
  [group.rows addObject:extraDataGroupRow];
  return group;
}
/// 联席主持人
- (NEFromGroup *)cohostGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *cohostRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kCohostTitle"];
  cohostRow.title = @"角色绑定";
  cohostRow.value = [_item.roleBinds ne_modelToJSONString];
  cohostRow.config = @{
    @"placeholder" : @"请输入配置",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing)
  };
  __weak typeof(self) weakSelf = self;
  cohostRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    NSString *coText = (NSString *)newValue;
    NSData *data = [coText dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data
                                                        options:NSJSONReadingMutableContainers
                                                          error:nil];
    if (dic) {
      weakSelf.item.roleBinds = dic;
    }
  };
  [group.rows addObject:cohostRow];
  return group;
}

- (NEFromGroup *)muteAudioGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [[NEFromGroup alloc] init];
  NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMute"];
  autoMuteRow.title = @"自动静音";
  autoMuteRow.subTitle = @"参会者加入会议时自动静音";
  autoMuteRow.value = @(self.muteAllAudio);
  autoMuteRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.muteAllAudio = [newValue boolValue];
  };

  NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                                 tag:@"kMeetingAudioAllowSelfOn"];
  allowSelfOnRow.title = @"允许自行解除静音";
  allowSelfOnRow.subTitle = @"允许参会者自行解除静音";
  allowSelfOnRow.value = @(self.allowAudioSelfOn);
  allowSelfOnRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.allowAudioSelfOn = [newValue boolValue];
  };

  [group.rows addObjectsFromArray:@[ autoMuteRow, allowSelfOnRow ]];
  return group;
}
- (NEFromGroup *)muteVideoGroup {
  __weak typeof(self) weakSelf = self;
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                              tag:@"kMeetingAutoMuteVideo"];
  autoMuteRow.title = @"自动关闭视频";
  autoMuteRow.subTitle = @"参会者加入会议时自动关闭视频";
  autoMuteRow.value = @(self.muteAllVideo);
  autoMuteRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.muteAllVideo = [newValue boolValue];
  };

  NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                                 tag:@"kMeetingVideoAllowSelfOn"];
  allowSelfOnRow.title = @"允许自行打开视频";
  allowSelfOnRow.subTitle = @"允许参会者自行打开视频";
  allowSelfOnRow.value = @(self.allowVideoSelfOn);
  allowSelfOnRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.allowVideoSelfOn = [newValue boolValue];
  };

  [group.rows addObjectsFromArray:@[ autoMuteRow, allowSelfOnRow ]];
  return group;
}

- (BOOL)isLiveLevel {
  if (!self.item.live.enable) return NO;
  return self.item.live.liveWebAccessControlLevel == NEMeetingLiveAuthLevelAppToken;
}

- (void)insertPassworkRowBelow:(NEFromRow *)row {
  NEFromRow *passwordInputRow = [NEFromRow rowWithType:NEFromRowTypeTextField
                                                   tag:@"kMeetingPassworkInput"];
  __weak typeof(self) weakSelf = self;
  passwordInputRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.password = newValue;
  };
  passwordInputRow.config =
      @{@"placeholder" : @"请输入密码", @"keyboardType" : @(UIKeyboardTypeNumberPad)};
  self.item.password = self.item.password ?: @(rand() % 900000 + 100000).stringValue;
  passwordInputRow.value = self.item.password;
  [self insertRow:passwordInputRow below:row];
}

- (void)commitAction {
  if (self.muteAllAudio || self.muteAllVideo) {
    NSMutableArray *controls = @[].mutableCopy;
    if (self.muteAllAudio) {
      NEMeetingControl *control = [NEMeetingControl createAudioControl];
      control.attendeeOff =
          self.allowAudioSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
      [controls addObject:control];
    }
    if (self.muteAllVideo) {
      NEMeetingControl *control = [NEMeetingControl createVideoControl];
      control.attendeeOff =
          self.allowVideoSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
      [controls addObject:control];
    }
    self.item.settings.controls = controls;
  } else {
    self.item.settings.controls = nil;
  }

  __weak typeof(self) weakSelf = self;
  [[[NEMeetingKit getInstance] getPreMeetingService]
               editMeeting:self.item
      editRecurringMeeting:NO
                  callback:^(NSInteger resultCode, NSString *_Nonnull resultMsg,
                             NEMeetingItem *_Nonnull item) {
                    if (resultCode == ERROR_CODE_SUCCESS) {
                      [[UIApplication sharedApplication].keyWindow makeToast:@"预定成功"
                                                                    duration:2
                                                                    position:CSToastPositionCenter];
                      [NSNotificationCenter.defaultCenter
                          postNotificationName:kNEMeetingEditSubscribeDone
                                        object:nil];
                    } else {
                      NSString *msg =
                          [NSString stringWithFormat:@"%@:%d", resultMsg, (int)resultCode];
                      [weakSelf.view makeToast:msg duration:2 position:CSToastPositionCenter];
                    }
                  }];
}

- (void)adjustDateTime {
  NEFromRow *startRow = [self rowWithTag:@"kMeetingStartTime"];
  NSIndexPath *endRowIndex = nil;
  NEFromRow *endRow = [self rowWithTag:@"kMeetingEndTime" indexPath:&endRowIndex];

  uint64_t startTimestamp = [startRow.value longLongValue];
  uint64_t endTimestamp = [endRow.value longLongValue];
  if (endTimestamp <= startTimestamp) {
    endTimestamp = startTimestamp + 60 * 30;
  }
  endRow.value = @(endTimestamp);
  endRow.config = @{
    @"minDate" : @(startTimestamp + 60 * 30),
    @"maxDate" : @(endTimestamp + 24 * 60 * 60),
  };
  if (endRowIndex) {
    [self.tableView reloadRowsAtIndexPaths:@[ endRowIndex ]
                          withRowAnimation:UITableViewRowAnimationNone];
  }

  self.item.startTime = startTimestamp * 1000;
  self.item.endTime = endTimestamp * 1000;
}

@end
