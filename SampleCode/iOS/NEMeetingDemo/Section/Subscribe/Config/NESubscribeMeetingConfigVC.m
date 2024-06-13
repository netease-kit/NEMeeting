// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NESubscribeMeetingConfigVC.h"
#import <NEJsonModel/NEJsonModel.h>
#import "NEFromDatePicker.h"
#import "UIView+Toast.h"

@interface NESubscribeMeetingConfigVC ()

@property(nonatomic, strong) UIButton *sureBtn;
@property(nonatomic, strong) NEMeetingItem *item;
@property(nonatomic, readonly) NEPreMeetingService *preMeetingService;
@property(nonatomic, copy) NSString *password;
@property(nonatomic, assign) BOOL muteAllAudio;
@property(nonatomic, assign) BOOL allowAudioSelfOn;
@property(nonatomic, assign) BOOL muteAllVideo;
@property(nonatomic, assign) BOOL allowVideoSelfOn;
@end

@implementation NESubscribeMeetingConfigVC

- (void)viewDidLoad {
  [super viewDidLoad];
  [self setupUI];
  [self setupItem];
  [self setupForm];
}

- (void)viewDidLayoutSubviews {
  [super viewDidLayoutSubviews];
  _sureBtn.size = CGSizeMake(MIN(self.view.width - 2 * 20, 335), 50);
  _sureBtn.top = self.view.height - 42.0 - _sureBtn.height;
  _sureBtn.centerX = self.view.width / 2;
  _sureBtn.layer.cornerRadius = _sureBtn.height / 2;
}

- (void)viewWillDisappear:(BOOL)animated {
  [super viewWillDisappear:animated];
  [NEFromDatePickerBar dismiss];
}

- (void)setupUI {
  self.title = @"预约会议";
  [self.view addSubview:self.sureBtn];
}

- (void)setupItem {
  _allowAudioSelfOn = YES;
  _allowVideoSelfOn = YES;
  _item = [[NEMeetingKit getInstance].getPreMeetingService createScheduleMeetingItem];
  uint64_t startTimeS = [[NSDate date] timeIntervalSince1970];
  startTimeS = (startTimeS - startTimeS % (30 * 60) + 30 * 60);
  _item.startTime = startTimeS * 1000;
  _item.endTime = (startTimeS + 30 * 60) * 1000;
}

/// 会议主题
- (NEFromGroup *)titleGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *titleRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingTitle"];
  titleRow.title = @"会议主题";
  titleRow.value = _item.subject;
  titleRow.config = @{
    @"placeholder" : @"请输入会议主题",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
  };
  __weak typeof(self) weakSelf = self;
  titleRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.subject = newValue;
  };
  [group.rows addObject:titleRow];
  return group;
}
/// 时间
- (NEFromGroup *)timeGroup {
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
  __weak typeof(self) weakSelf = self;
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
/// 会议密码
- (NEFromGroup *)passwordGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *meetingKeyRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingKey"];
  meetingKeyRow.title = @"会议密码";
  __weak typeof(self) weakSelf = self;
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
  enableWaitingRoomRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.waitingRoomEnabled = [newValue boolValue];
  };
  [group.rows addObject:enableWaitingRoomRow];

  NEFromRow *enableGuestJoin = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kGuestJoin"];
  enableGuestJoin.title = @"允许访客入会";
  enableGuestJoin.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.enableGuestJoin = [newValue boolValue];
  };
  [group.rows addObject:enableGuestJoin];
  return group;
}
/// 云端录制
- (NEFromGroup *)cloudRecordGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *openRecord = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                             tag:@"kMeetingOpenRecord"];
  openRecord.title = @"开启云端录制";
  __weak typeof(self) weakSelf = self;
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

/// 企业员工
- (NEFromRow *)enterpriseStaffRow {
  NEFromRow *liveLevelRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                               tag:@"kMeetingLiveLevel"];
  liveLevelRow.title = @"仅本企业员工可观看";
  __weak typeof(self) weakSelf = self;
  liveLevelRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.live.liveWebAccessControlLevel =
        [newValue boolValue] ? NEMeetingLiveAuthLevelAppToken : NEMeetingLiveAuthLevelToken;
  };
  liveLevelRow.value = @false;
  return liveLevelRow;
}
/// 直播
- (NEFromGroup *)liveGroup {
  NEFromGroup *group = [[NEFromGroup alloc] init];
  NEFromRow *liveRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLive"];
  liveRow.title = @"开启直播";
  __weak typeof(self) weakSelf = self;
  liveRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.live.enable = [newValue boolValue];
    if (weakSelf.item.live.enable) {
      [self insertRow:[weakSelf enterpriseStaffRow] below:row];
    } else {
      [weakSelf deleteRowWithTag:@"kMeetingLiveLevel"];
    }
  };
  liveRow.value = @false;
  [group.rows addObject:liveRow];
  return group;
}

/// 联席主持人
- (NEFromGroup *)cohostGroup {
  NEFromGroup *group = [NEFromGroup new];
  NEFromRow *cohostRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kCohostTitle"];
  cohostRow.title = @"角色绑定";
  cohostRow.value = [_item.roleBinds ne_modelToJSONString];
  //    cohostRow.value = @"{\"42ea43e7df3512e117fa8330\":0,\"9f9071de70e7be411abf716e\":1}";
  cohostRow.config = @{
    @"placeholder" : @"请输入配置",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing)
  };
  __weak typeof(self) weakSelf = self;
  cohostRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    NSString *coText = (NSString *)newValue;
    //        NSDictionary *dic = [NSDictionary yy_modelWithJSON:@"{\"a\":1}"];
    NSData *data = [coText dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data
                                                        options:NSJSONReadingMutableContainers
                                                          error:nil];
    if (dic) {
      weakSelf.item.roleBinds = dic;
    }
  };
  //    self.item.roleBinds = @{
  //        @"42ea43e7df3512e117fa8330": @(0),
  //        @"9f9071de70e7be411abf716e": @(1)
  //    };
  [group.rows addObject:cohostRow];
  return group;
}

/// 额外扩展
- (NEFromGroup *)extraDataGroup {
  NEFromGroup *group = [[NEFromGroup alloc] init];
  NEFromRow *extraDataGroupRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput
                                                    tag:@"kMeetingExtraData"];
  extraDataGroupRow.title = @"扩展字段";
  extraDataGroupRow.value = _item.extraData;
  extraDataGroupRow.config = @{
    @"placeholder" : @"自定义扩展字段",
    @"disableCopy" : @(YES),
    @"textAlignment" : @(NSTextAlignmentRight),
    @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
  };
  __weak typeof(self) weakSelf = self;
  extraDataGroupRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.extraData = newValue;
  };
  [group.rows addObject:extraDataGroupRow];
  return group;
}
/// 自动 静音/解除静音
- (NEFromGroup *)autoAudioGroup {
  NEFromGroup *group = [[NEFromGroup alloc] init];
  NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMute"];
  autoMuteRow.title = @"自动静音";
  autoMuteRow.subTitle = @"参会者加入会议时自动静音";
  __weak typeof(self) weakSelf = self;
  autoMuteRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.muteAllAudio = [newValue boolValue];
  };

  NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                                 tag:@"kMeetingAudioAllowSelfOn"];
  allowSelfOnRow.title = @"允许自行解除静音";
  allowSelfOnRow.subTitle = @"允许参会者自行解除静音";
  allowSelfOnRow.value = @(YES);
  allowSelfOnRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.allowAudioSelfOn = [newValue boolValue];
  };
  [group.rows addObjectsFromArray:@[ autoMuteRow, allowSelfOnRow ]];
  return group;
}
/// 自动  开/关视频
- (NEFromGroup *)autoVideoGroup {
  NEFromGroup *group = [[NEFromGroup alloc] init];
  NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                              tag:@"kMeetingAutoMuteVideo"];
  autoMuteRow.title = @"自动关闭视频";
  autoMuteRow.subTitle = @"参会者加入会议时自动关闭视频";
  __weak typeof(self) weakSelf = self;
  autoMuteRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.muteAllVideo = [newValue boolValue];
  };

  NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch
                                                 tag:@"kMeetingVideoAllowSelfOn"];
  allowSelfOnRow.title = @"允许自行打开视频";
  allowSelfOnRow.subTitle = @"允许参会者自行打开视频";
  allowSelfOnRow.value = @(YES);
  allowSelfOnRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.allowVideoSelfOn = [newValue boolValue];
  };
  [group.rows addObjectsFromArray:@[ autoMuteRow, allowSelfOnRow ]];
  return group;
}

- (void)setupForm {
  self.groups =
      @[
        [self titleGroup], [self timeGroup], [self passwordGroup], [self cloudRecordGroup],
        [self openSipGroup], [self cohostGroup]
      ]
          .mutableCopy;
  if (NEMeetingKit.getInstance.getSettingsService.isMeetingLiveEnabled) {
    [self.groups addObject:[self liveGroup]];
  }
  [self.groups
      addObjectsFromArray:@[ [self extraDataGroup], [self autoAudioGroup], [self autoVideoGroup] ]];
}

- (void)insertPassworkRowBelow:(NEFromRow *)row {
  NEFromRow *passwordInputRow = [NEFromRow rowWithType:NEFromRowTypeTextField
                                                   tag:@"kMeetingPassworkInput"];
  __weak typeof(self) weakSelf = self;
  passwordInputRow.onValueChanged = ^(id _Nonnull newValue, NEFromRow *_Nonnull row) {
    weakSelf.item.password = newValue;
    weakSelf.password = newValue;
  };
  passwordInputRow.config = @{@"placeholder" : @"请输入密码"};
  _item.password = _password.length ? _password : @(rand() % 900000 + 100000).stringValue;
  passwordInputRow.value = _item.password;
  [self insertRow:passwordInputRow below:row];
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
  _item.startTime = startTimestamp * 1000;
  _item.endTime = endTimestamp * 1000;
}

#pragma mark - Action
- (void)onSubscribeMeetingAction:(UIButton *)sender {
  if (self.muteAllAudio || self.muteAllVideo) {
    NSMutableArray *controls = [[NSMutableArray alloc] init];
    if (_muteAllAudio) {
      NEMeetingControl *control = [NEMeetingControl createAudioControl];
      control.attendeeOff =
          _allowAudioSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
      [controls addObject:control];
    }
    if (_muteAllVideo) {
      NEMeetingControl *control = [NEMeetingControl createVideoControl];
      control.attendeeOff =
          _allowVideoSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
      [controls addObject:control];
    }
    _item.settings.controls = controls;
  }

  __weak typeof(self) weakSelf = self;
  [self.preMeetingService
      scheduleMeeting:_item
             callback:^(NSInteger resultCode, NSString *_Nonnull resultMsg,
                        NEMeetingItem *_Nonnull item) {
               if (resultCode == ERROR_CODE_SUCCESS) {
                 [[UIApplication sharedApplication].keyWindow makeToast:@"预定成功"
                                                               duration:2
                                                               position:CSToastPositionCenter];
                 [weakSelf.navigationController popViewControllerAnimated:YES];
               } else {
                 NSString *msg = [NSString stringWithFormat:@"%@:%d", resultMsg, (int)resultCode];
                 [weakSelf.view makeToast:msg duration:2 position:CSToastPositionCenter];
               }
             }];
}

#pragma mark - Getter
- (UIButton *)sureBtn {
  if (!_sureBtn) {
    _sureBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_sureBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    _sureBtn.backgroundColor = UIColorFromRGB(0x337EFF);
    _sureBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [_sureBtn setTitle:@"立即预约" forState:UIControlStateNormal];
    [_sureBtn addTarget:self
                  action:@selector(onSubscribeMeetingAction:)
        forControlEvents:UIControlEventTouchUpInside];
  }
  return _sureBtn;
}

- (NEPreMeetingService *)preMeetingService {
  return [[NEMeetingKit getInstance] getPreMeetingService];
}
@end
