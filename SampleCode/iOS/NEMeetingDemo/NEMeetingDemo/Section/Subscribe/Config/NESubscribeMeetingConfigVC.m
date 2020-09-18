//
//  NESubscribeMeetingConfigVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NESubscribeMeetingConfigVC.h"
#import "UIView+Toast.h"

@interface NESubscribeMeetingConfigVC ()

@property (nonatomic, strong) UIButton *sureBtn;

@property (nonatomic, strong) NEMeetingItem *item;

@property (nonatomic, readonly) NEPreMeetingService *preMeetingService;

@property (nonatomic, copy) NSString *password;

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

- (void)setupUI {
    self.title = @"预约会议";
    [self.view addSubview:self.sureBtn];
}

- (void)setupItem {
    _item = [[NEMeetingSDK getInstance].getPreMeetingService createScheduleMeetingItem];
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
    NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMute"];
    autoMuteRow.title = @"自动静音";
    autoMuteRow.subTitle = @"参会者加入会议时自动静音";
    autoMuteRow.onValueChanged = ^(id  _Nonnull newValue, NEFromRow * _Nonnull row) {
        weakSelf.item.settings.attendeeAudioOff = [newValue boolValue];
    };
    [group3.rows addObject:autoMuteRow];
    
    self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group3]];
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
    return [[NEMeetingSDK getInstance] getPreMeetingService];
}

@end
