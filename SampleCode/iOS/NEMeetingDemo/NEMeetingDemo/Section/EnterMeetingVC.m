//
//  EnterMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "EnterMeetingVC.h"
#import "CheckBox.h"
#import "MeetingSettingVC.h"

@interface EnterMeetingVC ()<CheckBoxDelegate>

@property (weak, nonatomic) IBOutlet CheckBox *configCheckBox;
@property (weak, nonatomic) IBOutlet CheckBox *settingCheckBox;

@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UILabel *titleLab;
@property (weak, nonatomic) IBOutlet UIButton *enterBtn;
@property (nonatomic ,strong) UIButton *settingBtn;

@property (nonatomic, readonly) BOOL openVideoWhenJoin;
@property (nonatomic, readonly) BOOL openMicWhenJoin;
@property (nonatomic, readonly) BOOL showMeetingTime;
@property (nonatomic, readonly) BOOL useDefaultConfig;

@end

@implementation EnterMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
}

- (void)setupUI {
    self.type = _type;
    [_configCheckBox setItemTitleWithArray:@[@"入会时打开摄像头", @"入会时打开麦克风", @"显示会议持续时间"]];
    [_settingCheckBox setItemTitleWithArray:@[@"使用默认会议设置"]];
    _settingCheckBox.delegate = self;
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:self.settingBtn];
    self.navigationItem.rightBarButtonItem = item;
}

#pragma mark - Action
- (IBAction)onEnterMeetingAction:(id)sender {
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.meetingId = _meetingIdInput.text;
    params.displayName = _nickInput.text;
    
    NEJoinMeetingOptions *options = nil;
    if (![self useDefaultConfig]) {
        options = [[NEJoinMeetingOptions alloc] init];
        options.noAudio = ![self openMicWhenJoin];
        options.noVideo = ![self openVideoWhenJoin];
        options.showMeetingTime = [self showMeetingTime];
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
    if (index == 0) {
        _configCheckBox.disableAllItems = [self useDefaultConfig];
        _settingBtn.hidden = ![self useDefaultConfig];
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

- (BOOL)useDefaultConfig {
    return [_settingCheckBox getItemSelectedAtIndex:0];
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
