//
//  EnterMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "EnterMeetingVC.h"
#import "CheckBox.h"
#import "MeetingSettingVC.h"
#import <IQKeyboardManager/IQKeyboardManager.h>

@interface EnterMeetingVC ()<CheckBoxDelegate, MeetingServiceListener>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;

@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UILabel *titleLab;
@property (weak, nonatomic) IBOutlet UIButton *enterBtn;
@property (weak, nonatomic) IBOutlet UITextField *menuIdInput;
@property (weak, nonatomic) IBOutlet UITextField *menuTitleInput;
@property (weak, nonatomic) IBOutlet UITextField *passworkInput;
@property (weak, nonatomic) IBOutlet UIButton *settingBtn;

@property (nonatomic, readonly) BOOL openVideoWhenJoin;
@property (nonatomic, readonly) BOOL openMicWhenJoin;
@property (nonatomic, readonly) BOOL showMeetingTime;
@property (nonatomic, readonly) BOOL useDefaultConfig;
@property (nonatomic, readonly) BOOL disableChat;
@property (nonatomic, readonly) BOOL disableInvite;
@property (nonatomic, readonly) BOOL disableMinimize;
@property (nonatomic, readonly) BOOL disableGallery;

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;
@end

@implementation EnterMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [[NEMeetingSDK getInstance].getMeetingService addListener:self];
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
    self.type = _type;
    [_configCheckBox setItemTitleWithArray:@[@"入会时打开摄像头",
                                             @"入会时打开麦克风",
                                             @"显示会议持续时间"]];
    [_settingCheckBox setItemTitleWithArray:@[@"入会时关闭聊天菜单",
                                              @"入会时关闭邀请菜单",
                                              @"入会时隐藏最小化",
                                              @"使用默认会议设置",
                                              @"入会时关闭画廊模式",
                                              @"仅显示会议ID长号",
                                              @"仅显示会议ID短号"]];
    [_settingCheckBox setItemSelected:YES index:2];
    _settingCheckBox.delegate = self;
}

#pragma mark - Action
- (IBAction)onEnterMeetingAction:(id)sender {
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.meetingId = _meetingIdInput.text;
    params.displayName = _nickInput.text;
    params.password = _passworkInput.text;
    
    NEJoinMeetingOptions *options = nil;
    if (![self useDefaultConfig]) {
        options = [[NEJoinMeetingOptions alloc] init];
        options.noAudio = ![self openMicWhenJoin];
        options.noVideo = ![self openVideoWhenJoin];
        options.showMeetingTime = [self showMeetingTime];
        options.meetingIdDisplayOption = [self meetingIdDisplayOption];
        options.noChat = [self disableChat];
        options.noInvite = [self disableInvite];
        options.noMinimize = [self disableMinimize];
        options.injectedMoreMenuItems = _menuItems;
        options.noGallery = [self disableGallery];
    }

    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[[NEMeetingSDK getInstance] getMeetingService] joinMeeting:params
                                                           opts:options
                                                       callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}

- (IBAction)onEnterSettingAction:(id)sender {
    MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:( CheckBox *)checkbox {
    if (checkbox != _settingCheckBox) {
        return;
    }
    if (index == 3) {
        _configCheckBox.disableAllItems = [self useDefaultConfig];
        _settingBtn.hidden = ![self useDefaultConfig];
    }
}

- (IBAction)addMenuAction:(UIButton *)sender {
    [self.view endEditing:YES];
    if (!_menuItems) {
        _menuItems = [NSMutableArray array];
    }
    NEMeetingMenuItem *item = [[NEMeetingMenuItem alloc] init];
    item.itemId = [_menuIdInput.text  intValue];
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

#pragma mark - MeetingServiceListener
- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
    if (event.arg == MEETING_WAITING_VERIFY_PASSWORD) {
        [SVProgressHUD dismiss];
    }
}


#pragma mark - Setter && Getter
- (void)setType:(EnterMeetingType)type {
    _type = type;
    switch (type) {
        case EnterMeetingNormal:
        {
            _titleLab.text = @"加入会议";
            [_enterBtn setTitle:@"加入会议" forState:UIControlStateNormal];
            break;
        }
        case EnterMeetingAnonymity:
        default:
            break;
    }
}

- (BOOL)openVideoWhenJoin {
    return [_configCheckBox getItemSelectedAtIndex:0];
}

- (BOOL)openMicWhenJoin {
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

- (BOOL)useDefaultConfig {
    return [_settingCheckBox getItemSelectedAtIndex:3];
}

- (BOOL)disableGallery {
    return [_settingCheckBox getItemSelectedAtIndex:4];
}

- (NEMeetingIdDisplayOption) meetingIdDisplayOption {
    if ([_settingCheckBox getItemSelectedAtIndex:5]) {
        return DISPLAY_LONG_ID_ONLY;
    } else if ([_settingCheckBox getItemSelectedAtIndex:6]) {
        return DISPLAY_SHORT_ID_ONLY;
    }
    return DISPLAY_ALL;
}

@end
