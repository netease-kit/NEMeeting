// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NESubscribeMeetingDetailVC.h"
#import "UIView+Toast.h"
#import "LoginInfoManager.h"
#import "NSEditSubscribeMeetingVC.h"
@interface NESubscribeMeetingDetailVC ()
@property (nonatomic, strong) UIButton *cancelMeetingBtn;
@property (nonatomic, strong) UIButton *joinMeetingBtn;
@property (nonatomic, strong) NEMeetingItem *item;
@property (nonatomic, weak) UIAlertController *alertVC;
/// 进入中
@property (nonatomic, assign) BOOL isEntering;
@end

@implementation NESubscribeMeetingDetailVC
- (instancetype)initWithItem:(NEMeetingItem *)item {
    if (self = [super init]) {
        _item = item;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"会议详情";
    [self setupNavigaitonBar];
    [self setupSubviews];
    [self setupForm];
}
- (void)setupNavigaitonBar {
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"编辑"
                                                                              style:UIBarButtonItemStylePlain
                                                                             target:self
                                                                             action:@selector(editMeeting)];
}
- (void)setupSubviews {
    [self.view addSubview:self.cancelMeetingBtn];
    [self.view addSubview:self.joinMeetingBtn];
    [self updateWithMeetingStatus];
}
- (void)editMeeting {
    NSEditSubscribeMeetingVC *editVC = [NSEditSubscribeMeetingVC editWithItem:self.item];
    [self.navigationController pushViewController:editVC animated:YES];
}
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if (_alertVC) {
        [_alertVC dismissViewControllerAnimated:NO completion:nil];
    }
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    if (!_cancelMeetingBtn.hidden && !_joinMeetingBtn.hidden) {
        _cancelMeetingBtn.size = CGSizeMake(163, 50);
        _cancelMeetingBtn.layer.cornerRadius = _cancelMeetingBtn.height/2;
        _cancelMeetingBtn.left = self.view.width/2 - 4.0 - _cancelMeetingBtn.width;
        _cancelMeetingBtn.top = self.view.height - 42.0 - _cancelMeetingBtn.height;
        
        _joinMeetingBtn.size = _cancelMeetingBtn.size;
        _joinMeetingBtn.layer.cornerRadius = _joinMeetingBtn.height/2;
        _joinMeetingBtn.left = _cancelMeetingBtn.right + 8.0;
        _joinMeetingBtn.top = _cancelMeetingBtn.top;
        self.tableView.frame = CGRectMake(0, 0, self.view.width, _joinMeetingBtn.top - 8.0);
    } else if (!_cancelMeetingBtn.hidden && _joinMeetingBtn.hidden) {
        _cancelMeetingBtn.height = 50;
        _cancelMeetingBtn.width = MIN(self.view.width - 2*20, 335);
        _cancelMeetingBtn.centerX = self.view.width/2;
        _cancelMeetingBtn.top = self.view.height - 42.0 - _cancelMeetingBtn.height;
        _cancelMeetingBtn.layer.cornerRadius = _cancelMeetingBtn.height/2;
    } else if (_cancelMeetingBtn.hidden && !_joinMeetingBtn.hidden) {
        _joinMeetingBtn.height = 50;
        _joinMeetingBtn.width = MIN(self.view.width - 2*20, 335);
        _joinMeetingBtn.centerX = self.view.width/2;
        _joinMeetingBtn.top = self.view.height - 42.0 - _joinMeetingBtn.height;
        _joinMeetingBtn.layer.cornerRadius = _joinMeetingBtn.height/2;
    } else {
        self.tableView.frame = self.view.bounds;
    }
}

- (void)setupForm {
    // title、会议ID、时间
    NSMutableArray *dataArray = @[
        [self titleGroup],
        [self meetingIDGroup],
        [self timeGroup]
    ].mutableCopy;
    // 直播密码
    if (self.item.password.length) {
        [dataArray addObject:[self pwdGroup]];
    }
    // 直播地址
    if (self.item.live.enable && self.item.live.liveUrl.length) {
        [dataArray addObjectsFromArray:@[
            [self liveUrlGroup],
            [self enterpriseStaffGroup]
        ]];
    }
    // 扩展字段
    [dataArray addObject:[self extraDataGroup]];
    // 静音
    if (self.item.settings.currentAudioControl != nil) {
        [dataArray addObject:[self muteAudioGroup]];
    }
    // 关闭视频
    if (self.item.settings.currentVideoControl != nil) {
        [dataArray addObject:[self muteVideoGroup]];
    }
    self.groups = dataArray;
}
- (NEFromGroup *)titleGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *titleRow = [NEFromRow rowWithType:NEFromRowTypeLabel tag:@"kMeetingTitle"];
    titleRow.title = self.item.subject;
    titleRow.userInteractionEnabled = NO;
    [group.rows addObject:titleRow];
    return group;
}
- (NEFromGroup *)meetingIDGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *meetingIdRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingId"];
    meetingIdRow.title = @"会议ID";
    meetingIdRow.value = self.item.meetingId;
    meetingIdRow.userInteractionEnabled = NO;
    [group.rows addObject:meetingIdRow];
    return group;
}
- (NEFromGroup *)timeGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *startTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingStartTime"];
    startTimeRow.title = @"开始时间";
    startTimeRow.value = @(self.item.startTime/1000);
    startTimeRow.userInteractionEnabled = NO;
    
    NEFromRow *endTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingEndTime"];
    endTimeRow.title = @"结束时间";
    endTimeRow.value = @(self.item.endTime/1000);
    endTimeRow.userInteractionEnabled = NO;
    [group.rows addObjectsFromArray:@[startTimeRow, endTimeRow]];
    return group;
}
- (NEFromGroup *)pwdGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *meetingKeyRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingKey"];
    meetingKeyRow.title = @"会议密码";
    meetingKeyRow.value = self.item.password;
    meetingKeyRow.userInteractionEnabled = NO;
    [group.rows addObject:meetingKeyRow];
    return group;
}
- (NEFromGroup *)liveUrlGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *meetingLiveRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingLive"];
    meetingLiveRow.title = @"直播地址";
    meetingLiveRow.value = _item.live.liveUrl;
    meetingLiveRow.userInteractionEnabled = NO;
    [group.rows addObject:meetingLiveRow];
    return group;
}
- (NEFromGroup *)enterpriseStaffGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *meetingLiveLevelRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLiveLevel"];
    meetingLiveLevelRow.title = @"仅本企业员工可观看";
    meetingLiveLevelRow.subTitle = _item.live.liveWebAccessControlLevel == NEMeetingLiveAuthLevelAppToken?@"已开启":@"未开启";
    meetingLiveLevelRow.value = _item.live.liveWebAccessControlLevel == NEMeetingLiveAuthLevelAppToken ? @(YES) : @(NO);
    meetingLiveLevelRow.hideRightItem = YES;
    [group.rows addObject:meetingLiveLevelRow];
    return group;
}
- (NEFromGroup *)extraDataGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *extraDataRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingExtraData"];
    extraDataRow.title = @"扩展字段";
    extraDataRow.value = _item.extraData;
    extraDataRow.userInteractionEnabled = NO;
    [group.rows addObject:extraDataRow];
    return group;
}
- (NEFromGroup *)muteAudioGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMute"];
    autoMuteRow.title = @"自动静音";
    autoMuteRow.subTitle = @"参会者加入会议时自动静音";
    autoMuteRow.value = @(_item.settings.currentAudioControl.attendeeOff != AttendeeOffTypeNone);
    autoMuteRow.userInteractionEnabled = NO;
    
    NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAudioAllowSelfOn"];
    allowSelfOnRow.title = @"允许自行解除静音";
    allowSelfOnRow.subTitle = @"允许参会者自行解除静音";
    allowSelfOnRow.value = @(_item.settings.currentAudioControl.attendeeOff == AttendeeOffTypeOffAllowSelfOn);
    allowSelfOnRow.userInteractionEnabled = NO;
    
    [group.rows addObjectsFromArray:@[autoMuteRow, allowSelfOnRow]];
    return group;
}
- (NEFromGroup *)muteVideoGroup {
    NEFromGroup *group = [NEFromGroup new];
    NEFromRow *autoMuteRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingAutoMuteVideo"];
    autoMuteRow.title = @"自动关闭视频";
    autoMuteRow.subTitle = @"参会者加入会议时自动关闭视频";
    autoMuteRow.value = @(_item.settings.currentVideoControl.attendeeOff != AttendeeOffTypeNone);
    autoMuteRow.userInteractionEnabled = NO;
    
    NEFromRow *allowSelfOnRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingVideoAllowSelfOn"];
    allowSelfOnRow.title = @"允许自行打开视频";
    allowSelfOnRow.subTitle = @"允许参会者自行打开视频";
    allowSelfOnRow.value = @(_item.settings.currentVideoControl.attendeeOff == AttendeeOffTypeOffAllowSelfOn);
    allowSelfOnRow.userInteractionEnabled = NO;
    
    [group.rows addObjectsFromArray:@[autoMuteRow, allowSelfOnRow]];
    return group;
}

- (void)updateWithMeetingStatus {
    switch (self.item.status) {
        case NEMeetingItemStatusInit: {
            self.cancelMeetingBtn.hidden = NO;
            self.joinMeetingBtn.hidden = NO;
            break;
        }
        case NEMeetingItemStatusStarted:
        case NEMeetingItemStatusEnded: {
            self.cancelMeetingBtn.hidden = YES;
            self.joinMeetingBtn.hidden = NO;
            break;
        }
        case NEMeetingItemStatusInvalid:
        case NEMeetingItemStatusCancel:
        case NEMeetingItemStatusRecycled: {
            self.cancelMeetingBtn.hidden = YES;
            self.joinMeetingBtn.hidden = YES;
            break;
        }
        default: break;
    }
}

- (void)showCancelAlert {
    __weak typeof(self)weakSelf = self;
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:nil
                                                                     message:@"是否确定要取消会议"
                                                              preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction *sure = [UIAlertAction actionWithTitle:@"取消会议"
                                                   style:UIAlertActionStyleDestructive
                                                 handler:^(UIAlertAction * _Nonnull action) {
        [weakSelf doCancelMeeting];
    }];
    [alertVC addAction:sure];
    
    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"暂不取消"
                                                     style:UIAlertActionStyleCancel
                                                   handler:nil];
    [alertVC addAction:cancel];
    
    UIPopoverPresentationController *popPresenter = [alertVC popoverPresentationController];
    
    self.alertVC = alertVC;
    popPresenter.sourceView = _cancelMeetingBtn;
    popPresenter.sourceRect = _cancelMeetingBtn.bounds;
    [self presentViewController:alertVC animated:YES completion:nil];
}

#pragma mark - Function
- (void)doCancelMeeting {
    __weak typeof(self) weakSelf = self;
    [[self preMeetingService] cancelMeeting:_item.meetingUniqueId callback:^(NSInteger resultCode, NSString *resultMsg) {
        if (resultCode == ERROR_CODE_SUCCESS) {
            [[UIApplication sharedApplication].keyWindow makeToast:@"取消预定会议成功"
                                                          duration:2
                                                          position:CSToastPositionCenter];
            [weakSelf.navigationController popViewControllerAnimated:YES];
        } else {
            NSString *msg = [NSString stringWithFormat:@"%@:%@", resultMsg, @(resultCode)];
            [weakSelf.view makeToast:msg
                            duration:2
                            position:CSToastPositionCenter];
        }
    }];
}

- (void)doJoinMeeting {
    if (self.isEntering)  return;
    self.isEntering = YES;
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.displayName = [self displayName];
    params.meetingId = _item.meetingId;
    params.password = _item.password;
    NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init];
    __weak typeof(self) weakSelf = self;
    [[self meetingService] joinMeeting:params
                                  opts:options
                              callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        weakSelf.isEntering = NO;
        if (resultCode != ERROR_CODE_SUCCESS) {
            NSString *msg = [NSString stringWithFormat:@"%@:%@", resultMsg, @(resultCode)];
            [weakSelf.view makeToast:msg
                            duration:2
                            position:CSToastPositionCenter];
        }
    }];
}

- (NSString *)displayName {
    NSString *ret = [LoginInfoManager shareInstance].nickName;
    if (ret.length == 0) {
        NSString *account = [LoginInfoManager shareInstance].account;
        if (account.length > 4) {
            account = [account substringFromIndex:account.length - 4];
        }
        ret = account;
    }
    if (ret.length == 0) ret = @"xxxx";
    return ret;
}

#pragma mark - Action
- (void)doCancelMeetingAction:(UIButton *)sender {
    [self showCancelAlert];
}

- (void)doJoinMeetingAction:(UIButton *)sender {
    [self doJoinMeeting];
}

#pragma mark - Getter
- (UIButton *)cancelMeetingBtn {
    if (!_cancelMeetingBtn) {
        _cancelMeetingBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _cancelMeetingBtn.backgroundColor = [UIColor clearColor];
        _cancelMeetingBtn.layer.borderWidth = 1.0f;
        _cancelMeetingBtn.layer.borderColor = UIColorFromRGB(0x337EFF).CGColor;
        [_cancelMeetingBtn setTitle:@"取消会议" forState:UIControlStateNormal];
        [_cancelMeetingBtn setTitleColor:UIColorFromRGB(0x337EFF) forState:UIControlStateNormal];
        _cancelMeetingBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_cancelMeetingBtn addTarget:self action:@selector(doCancelMeetingAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelMeetingBtn;
}

- (UIButton *)joinMeetingBtn {
    if (!_joinMeetingBtn) {
        _joinMeetingBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _joinMeetingBtn.backgroundColor = UIColorFromRGB(0x337EFF);
        [_joinMeetingBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_joinMeetingBtn setTitle:@"加入会议" forState:UIControlStateNormal];
        _joinMeetingBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_joinMeetingBtn addTarget:self action:@selector(doJoinMeetingAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _joinMeetingBtn;
}

- (NEPreMeetingService *)preMeetingService {
    return [NEMeetingKit getInstance].getPreMeetingService;
}

- (NEMeetingService *)meetingService {
    return [NEMeetingKit getInstance].getMeetingService;;
}

@end
