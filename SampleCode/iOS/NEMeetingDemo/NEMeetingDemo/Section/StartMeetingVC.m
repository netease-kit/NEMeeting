//
//  StartMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "StartMeetingVC.h"
#import "MeetingSettingVC.h"
#import "CheckBox.h"
#import <IQKeyboardManager/IQKeyboardManager.h>

@interface StartMeetingVC ()<CheckBoxDelegate>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;
@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UITextField *menuIdInput;
@property (weak, nonatomic) IBOutlet UITextField *menuTitleInput;
@property (nonatomic ,strong) UIButton *settingBtn;

@property (nonatomic, readonly) BOOL openVideoWhenJoinMeeting;
@property (nonatomic, readonly) BOOL openMicWhenJoinMeeting;
@property (nonatomic, readonly) BOOL showMeetingTime;
@property (nonatomic, readonly) BOOL useUserMeetingId;
@property (nonatomic, readonly) BOOL useDefaultConfig;
@property (nonatomic, readonly) BOOL disableChat;
@property (nonatomic, readonly) BOOL disableInvite;

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;

@end

@implementation StartMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
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
                                             @"显示会议持续时间",
                                             @"入会时关闭聊天菜单",
                                             @"入会时关闭邀请菜单"]];
    [_settingCheckBox setItemTitleWithArray:@[@"使用个人会议号",
                                              @"使用默认会议设置"]];
    _settingCheckBox.delegate = self;
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:self.settingBtn];
    self.navigationItem.rightBarButtonItem = item;
}

#pragma mark - Function
- (void)doStartMeeting {
    NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init];
    params.displayName = _nickInput.text;
    params.meetingId = _meetingIdInput.text;
    
    NEStartMeetingOptions *options = nil;
    if (![self useDefaultConfig]) {
        options = [[NEStartMeetingOptions alloc] init];
        options.noVideo = ![self openVideoWhenJoinMeeting];
        options.noAudio = ![self openMicWhenJoinMeeting];
        options.showMeetingTime = [self showMeetingTime];
        options.noInvite = [self disableInvite];
        options.noChat = [self disableChat];
        options.injectedMoreMenuItems = _menuItems;
    }
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[NEMeetingSDK getInstance].getMeetingService startMeeting:params
                                                          opts:options
                                                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}

- (void)doGetUserMeetingId {
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[NEMeetingSDK getInstance].getAccountService getPersonalMeetingId:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
            NSString *userId = result;
            if (![result isKindOfClass:[NSString class]]
                || result == nil) {
                userId = @"";
            }
            weakSelf.meetingIdInput.text = userId;
        }
    }];
}

#pragma mark - Actions
- (IBAction)onStartMeeting:(id)sender {
    [self doStartMeeting];
}

- (IBAction)addMenuAction:(UIButton *)sender {
    [self.view endEditing:YES];
    if (!_menuItems) {
        _menuItems = [NSMutableArray array];
    }
    NEMeetingMenuItem *item = [[NEMeetingMenuItem alloc] init];
    item.itemId = [_menuIdInput.text  integerValue];
    if (item.itemId == 101) {
        item.title = @"显示会议信息";
    } else {
        item.title = _menuTitleInput.text;
    }
    [_menuItems addObject:item];

    NSString *msg = [NSString stringWithFormat:@"添加MenuItemId:%@ title:%@",
                     @(item.itemId), item.title];
    [self.view makeToast:msg];
}

- (void)onEnterSettingAction:(id)sender {
    MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:( CheckBox *)checkbox {
    if (checkbox != _settingCheckBox) {
        return;
    }
    if (index == 0) { //使用个人会议号
        if ([self useUserMeetingId]) {
            [self doGetUserMeetingId];
        }
    } else if (index == 1) {
        _configCheckBox.disableAllItems = [self useDefaultConfig];
        _settingBtn.hidden = ![self useDefaultConfig];
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
    return [_configCheckBox getItemSelectedAtIndex:3];
}

- (BOOL)disableInvite {
    return [_configCheckBox getItemSelectedAtIndex:4];
}

- (BOOL)useUserMeetingId {
    return [_settingCheckBox getItemSelectedAtIndex:0];
}

- (BOOL)useDefaultConfig {
    return [_settingCheckBox getItemSelectedAtIndex:1];
}

- (UIButton *)settingBtn {
    if (!_settingBtn) {
        _settingBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _settingBtn.titleLabel.font = [UIFont systemFontOfSize:13.0];
        _settingBtn.frame = CGRectMake(0, 0, 60, 40);
        [_settingBtn setTitle:@"会议设置" forState:UIControlStateNormal];
        _settingBtn.hidden = YES;
        [_settingBtn addTarget:self
                        action:@selector(onEnterSettingAction:)
              forControlEvents:UIControlEventTouchUpInside];
    }
    return _settingBtn;
}

@end
