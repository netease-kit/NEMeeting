//
//  NESubscribeMeetingDetailVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NESubscribeMeetingDetailVC.h"
#import "UIView+Toast.h"
#import "LoginInfoManager.h"

@interface NESubscribeMeetingDetailVC ()

@property (nonatomic, strong) UIButton *cancelMeetingBtn;
@property (nonatomic, strong) UIButton *joinMeetingBtn;
@property (nonatomic, strong) NEMeetingItem *item;

@property (nonatomic, readonly) NEPreMeetingService *preMeetingService;
@property (nonatomic, readonly) NEMeetingService *meetingService;

@property (nonatomic, weak) UIAlertController *alertVC;

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
    [self.view addSubview:self.cancelMeetingBtn];
    [self.view addSubview:self.joinMeetingBtn];
    [self setupForm];
    [self updateWithMeetingStatus];
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
    NEFromGroup *group0 = [[NEFromGroup alloc] init];
    NEFromRow *titleRow = [NEFromRow rowWithType:NEFromRowTypeLabel tag:@"kMeetingTitle"];
    titleRow.title = _item.subject;
    [group0.rows addObject:titleRow];
    
    NEFromGroup *group1 = [[NEFromGroup alloc] init];
    NEFromRow *meetingIdRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingId"];
    meetingIdRow.title = @"会议ID";
    meetingIdRow.value = _item.meetingId;
    [group1.rows addObject:meetingIdRow];
    
    NEFromGroup *group2 = [[NEFromGroup alloc] init];
    NEFromRow *startTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingStartTime"];
    startTimeRow.title = @"开始时间";
    startTimeRow.value = @(_item.startTime/1000);
    startTimeRow.userInteractionEnabled = NO;
    NEFromRow *endTimeRow = [NEFromRow rowWithType:NEFromRowTypeTitleDate tag:@"kMeetingEndTime"];
    endTimeRow.title = @"结束时间";
    endTimeRow.value = @(_item.endTime/1000);
    endTimeRow.userInteractionEnabled = NO;
    [group2.rows addObjectsFromArray:@[startTimeRow, endTimeRow]];
    
    NEFromGroup *group3 = [[NEFromGroup alloc] init];
    NEFromRow *meetingKeyRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingKey"];
    meetingKeyRow.title = @"会议密码";
    meetingKeyRow.value = _item.password;
    [group3.rows addObject:meetingKeyRow];
    
    NEFromGroup *group4 = [[NEFromGroup alloc] init];
    NEFromRow *meetingLiveRow = [NEFromRow rowWithType:NEFromRowTypeTitleInput tag:@"kMeetingLive"];
    meetingLiveRow.title = @"直播地址";
    meetingLiveRow.value = _item.live.liveUrl;
    [group4.rows addObject:meetingLiveRow];
    
    NEFromGroup *group5 = [[NEFromGroup alloc] init];
    NEFromRow *meetingLiveLevelRow = [NEFromRow rowWithType:NEFromRowTypeTitleSwitch tag:@"kMeetingLiveLevel"];
    meetingLiveLevelRow.title = @"仅本企业员工可观看";
    meetingLiveLevelRow.subTitle = _item.live.liveWebAccessControlLevel == NEMeetingLiveAuthLevelAppToken?@"已开启":@"未开启";
    meetingLiveLevelRow.value = _item.live.liveWebAccessControlLevel == NEMeetingLiveAuthLevelAppToken ? @(YES) : @(NO);
    meetingLiveLevelRow.hideRightItem = YES;
   
    [group5.rows addObject:meetingLiveLevelRow];
    
    if (_item.password.length != 0) {
        if(_item.live.enable && _item.live.liveUrl.length !=0 ){
            self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group3, group4,group5]];
        }else{
            self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group3]];
        }
    } else {
        if(_item.live.enable && _item.live.liveUrl.length !=0){
            self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2, group4,group5]];
        }else{
            self.groups = [NSMutableArray arrayWithArray:@[group0, group1, group2]];
        }
    }
}

- (void)updateWithMeetingStatus {
    switch (_item.status) {
        case NEMeetingItemStatusInit:
        case NEMeetingItemStatusStarted:{
            _cancelMeetingBtn.hidden = NO;
            _joinMeetingBtn.hidden = NO;
            break;
        }
        case NEMeetingItemStatusEnded:{
            _cancelMeetingBtn.hidden = YES;
            _joinMeetingBtn.hidden = NO;
            break;
        }
        case NEMeetingItemStatusInvalid:
        case NEMeetingItemStatusCancel:
        case NEMeetingItemStatusRecycled:
        {
            _cancelMeetingBtn.hidden = YES;
            _joinMeetingBtn.hidden = YES;
            break;
        }
        default:
            break;
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
    
    _alertVC = alertVC;
    popPresenter.sourceView = _cancelMeetingBtn;
    popPresenter.sourceRect = _cancelMeetingBtn.bounds;
    [self presentViewController:alertVC animated:YES completion:nil];
}

#pragma mark - Function
- (void)doCancelMeeting {
    __weak typeof(self) weakSelf = self;
    [self.preMeetingService cancelMeeting:_item.meetingUniqueId callback:^(NSInteger resultCode, NSString *resultMsg) {
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
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.displayName = [self displayName];
    params.meetingId = _item.meetingId;
    NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init];
    __weak typeof(self) weakSelf = self;
    [self.meetingService joinMeeting:params opts:options callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
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
    if (ret.length == 0){
        ret = @"xxxx";
    }
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
    return [NEMeetingSDK getInstance].getPreMeetingService;
}

- (NEMeetingService *)meetingService {
    return [NEMeetingSDK getInstance].getMeetingService;;
}

@end
