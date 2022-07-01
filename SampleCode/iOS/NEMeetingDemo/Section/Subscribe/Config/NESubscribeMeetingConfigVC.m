//
//  NESubscribeMeetingConfigVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NESubscribeMeetingConfigVC.h"
#import "UIView+Toast.h"
#import "NEFromDatePicker.h"
#import <YYModel/YYModel.h>

@interface NESubscribeMeetingConfigVC ()

@property (nonatomic, strong) UIButton *sureBtn;

@property (nonatomic, strong) NEMeetingItem *item;

@property (nonatomic, readonly) NEPreMeetingService *preMeetingService;

@property (nonatomic, copy) NSString *password;

@property (nonatomic, assign) BOOL muteAllAudio;

@property (nonatomic, assign) BOOL allowAudioSelfOn;

@property (nonatomic, assign) BOOL muteAllVideo;

@property (nonatomic, assign) BOOL allowVideoSelfOn;

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
    _sureBtn.size = CGSizeMake(MIN(self.view.width-2*20, 335), 50);
    _sureBtn.top = self.view.height - 42.0 - _sureBtn.height;
    _sureBtn.centerX = self.view.width/2;
    _sureBtn.layer.cornerRadius = _sureBtn.height/2;
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
    startTimeS = (startTimeS - startTimeS%(30*60) + 30*60);
    _item.startTime = startTimeS * 1000;
    _item.endTime = (startTimeS + 30*60) * 1000;
}

- (void)setupForm {
    __weak typeof(self) weakSelf = self;
    NEFromGroup *group0 = [[NEFromGroup alloc] init];
    NEFromRow *titleRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingTitle"];
    titleRow.title = @"会议主题";
    titleRow.value = _item.subject;
    titleRow.config = @{
        @"placeholder" : @"请输入会议主题",
        @"disableCopy" : @(YES),
        @"textAlignment" : @(NSTextAlignmentRight),
        @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
    };
    titleRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        weakSelf.item.subject = newValue;
    };
    [group0.rows addObject:titleRow];
    
    NEFromGroup *group1 = [[NEFromGroup alloc] init];
    NEFromRow *startTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingStartTime"];
    startTimeRow.title = @"开始时间";
    startTimeRow.value = @(_item.startTime/1000);
    uint64_t currentTimeStamp = [[NSDate date] timeIntervalSince1970];
    currentTimeStamp = (currentTimeStamp - currentTimeStamp%(30*60) + 30*60);
    startTimeRow.config = @{
        @"minDate" :@(currentTimeStamp),
        @"maxDate" : @(currentTimeStamp * 7 * 24 * 60 * 60),
    };
    startTimeRow.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    startTimeRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        [weakSelf adjustDateTime];
    };
    NEFromRow *endTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingEndTime"];
    endTimeRow.title = @"结束时间";
    endTimeRow.value = @(_item.endTime/1000);
    endTimeRow.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    endTimeRow.config = @{
        @"minDate" : @(_item.startTime/1000 + 60 * 30),
        @"maxDate" : @(_item.endTime/1000 + 24 * 60 * 60),
    };
    endTimeRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        [weakSelf adjustDateTime];
    };
    [group1.rows addObjectsFromArray:@[startTimeRow, endTimeRow]];
    
    NEFromGroup *group2 = [[NEFromGroup alloc] init];
    NEFromRow *meetingKeyRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingKey"];
    meetingKeyRow.title = @"会议密码";
    meetingKeyRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        BOOL isShowPassword = [newValue boolValue];
        if (isShowPassword) {
            [weakSelf insertPassworkRowBelow:row];
        } else {
            weakSelf.item.password = nil;
            [weakSelf deleteRowWithTag:@"kMeetingPassworkInput"];
        }
    };
    [group2.rows addObject:meetingKeyRow];
    
    NEFromGroup *group3 = [[NEFromGroup alloc] init];
    NEFromRow *sceneSettingsRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kScene"];
    sceneSettingsRow.title = @"入会人员配置";
    sceneSettingsRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        [weakSelf doSceneSettings];
    };
    [group3.rows addObjectsFromArray:@[sceneSettingsRow]];
    
    
    NEFromRow *liveLevelRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLiveLevel"];
    liveLevelRow.title = @"仅本企业员工可观看";
    liveLevelRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        BOOL isUseAppToken =  [newValue boolValue];
        if (isUseAppToken) {
            weakSelf.item.live.liveWebAccessControlLevel = NEMeetingLiveAuthLevelAppToken;
        }else{
            weakSelf.item.live.liveWebAccessControlLevel = NEMeetingLiveAuthLevelToken;
        }
    };
    liveLevelRow.value = @false;
    
    
    NEFromGroup *group4 = [[NEFromGroup alloc] init];
    NEFromRow *openRecord = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingOpenRecord"];
    openRecord.title = @"开启云端录制";
    openRecord.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        weakSelf.item.settings.cloudRecordOn = [newValue boolValue];
    };
    [group4.rows addObject:openRecord];
    
    if([[NEMeetingKit getInstance].getSettingsService isMeetingLiveEnabled]){
        
        NEFromGroup *group5 = [[NEFromGroup alloc] init];
        NEFromRow *liveRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLive"];
        liveRow.title = @"开启直播";
        liveRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
            weakSelf.item.live.enable = [newValue boolValue];
            if (weakSelf.item.live.enable) {
                [self insertRow:liveLevelRow below:row];
            } else {
                [weakSelf deleteRowWithTag:@"kMeetingLiveLevel"];
            }
        };
        liveRow.value = @false;
        [group5.rows addObject:liveRow];
        
        self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group3, group4,group5]];
    }else{
        self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group3,group4]];
    }
    
    NEFromGroup *extraDataGroup = [[NEFromGroup alloc] init];
    NEFromRow *extraDataGroupRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingExtraData"];
    extraDataGroupRow.title = @"扩展字段";
    extraDataGroupRow.value = _item.extraData;
    extraDataGroupRow.config = @{
        @"placeholder" : @"自定义扩展字段",
        @"disableCopy" : @(YES),
        @"textAlignment" : @(NSTextAlignmentRight),
        @"clearButtonMode" : @(UITextFieldViewModeWhileEditing),
    };
    extraDataGroupRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        weakSelf.item.extraData = newValue;
    };
    [extraDataGroup.rows addObject:extraDataGroupRow];
    [self.groups addObject: extraDataGroup];
    
    {
        NEFromGroup *group = [[NEFromGroup alloc] init];
        NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMute"];
        autoMuteRow.title = @"自动静音";
        autoMuteRow.subTitle = @"参会者加入会议时自动静音";
        autoMuteRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
            weakSelf.muteAllAudio = [newValue boolValue];
        };
        
        NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAudioAllowSelfOn"];
        allowSelfOnRow.title = @"允许自行解除静音";
        allowSelfOnRow.subTitle = @"允许参会者自行解除静音";
        allowSelfOnRow.value = @(YES);
        allowSelfOnRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
            weakSelf.allowAudioSelfOn = [newValue boolValue];
        };
        
        [group.rows addObjectsFromArray:@[autoMuteRow, allowSelfOnRow]];
        [self.groups addObject: group];
    }
    
    {
        NEFromGroup *group = [[NEFromGroup alloc] init];
        NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMuteVideo"];
        autoMuteRow.title = @"自动关闭视频";
        autoMuteRow.subTitle = @"参会者加入会议时自动关闭视频";
        autoMuteRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
            weakSelf.muteAllVideo = [newValue boolValue];
        };
        
        NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingVideoAllowSelfOn"];
        allowSelfOnRow.title = @"允许自行打开视频";
        allowSelfOnRow.subTitle = @"允许参会者自行打开视频";
        allowSelfOnRow.value = @(YES);
        allowSelfOnRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
            weakSelf.allowVideoSelfOn = [newValue boolValue];
        };
        
        [group.rows addObjectsFromArray:@[autoMuteRow, allowSelfOnRow]];
        [self.groups addObject: group];
    }
}

- (void)insertPassworkRowBelow:(NEFromRow *)row {
    NEFromRow *passwordInputRow = [NEFromRow rowWithType:NEFromRowTypeTextField
                                                     tag:@"kMeetingPassworkInput"];
    __weak typeof(self) weakSelf = self;
    passwordInputRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        weakSelf.item.password = newValue;
        weakSelf.password = newValue;
    };
    passwordInputRow.config = @{
                                @"placeholder" : @"请输入密码",
                                //@"keyboardType" : @(UIKeyboardTypeNumberPad),
                                };
    if (_password.length != 0) {
        _item.password = _password;
    } else {
        _item.password = @(rand()%900000+100000).stringValue;
    }
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
        [self.tableView reloadRowsAtIndexPaths:@[endRowIndex]
                              withRowAnimation:UITableViewRowAnimationNone];
    }
    
    _item.startTime = startTimestamp*1000;
    _item.endTime = endTimestamp*1000;
}

#pragma mark - Action
- (void)onSubscribeMeetingAction:(UIButton *)sender {
    if (self.muteAllAudio || self.muteAllVideo) {
        NSMutableArray* controls = [[NSMutableArray alloc]init];
        if (_muteAllAudio) {
            NEMeetingControl *control = [NEMeetingControl createAudioControl];
            control.attendeeOff = _allowAudioSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
            [controls addObject: control];
        }
        if (_muteAllVideo) {
            NEMeetingControl *control = [NEMeetingControl createVideoControl];
            control.attendeeOff = _allowVideoSelfOn ? AttendeeOffTypeOffAllowSelfOn : AttendeeOffTypeOffNotAllowSelfOn;
            [controls addObject: control];
        }
        _item.settings.controls = controls;
    }
    
    
    __weak typeof(self) weakSelf = self;
    [self.preMeetingService scheduleMeeting:_item callback:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NEMeetingItem * _Nonnull item) {
        if (resultCode == ERROR_CODE_SUCCESS) {
            [[UIApplication sharedApplication].keyWindow makeToast:@"预定成功" duration:2 position:CSToastPositionCenter];
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
        [_sureBtn addTarget:self action:@selector(onSubscribeMeetingAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _sureBtn;
}

- (NEPreMeetingService *)preMeetingService {
    return [[NEMeetingKit getInstance] getPreMeetingService];
}

- (void)doSceneSettings {
}
@end
