//
//  EnterMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "EnterMeetingVC.h"
#import "CheckBox.h"

@interface EnterMeetingVC ()

@property (weak, nonatomic) IBOutlet CheckBox *checkBox;
@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;
@property (weak, nonatomic) IBOutlet UILabel *titleLab;
@property (weak, nonatomic) IBOutlet UIButton *enterBtn;

@property (nonatomic, readonly) BOOL openVideoWhenJoin;
@property (nonatomic, readonly) BOOL openMicWhenJoin;

@end

@implementation EnterMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
}

- (void)setupUI {
    self.type = _type;
    [_checkBox setItemTitleWithArray:@[@"入会时打开摄像头", @"入会时打开麦克风"]];
    [_checkBox setNormalImage:[UIImage imageNamed:@"checkbox_n"]];
    [_checkBox setSelectedImage:[UIImage imageNamed:@"checkbox_s"]];
}

- (BOOL)openVideoWhenJoin {
    return [_checkBox getItemSelectedAtIndex:0];
}

- (BOOL)openMicWhenJoin {
    return [_checkBox getItemSelectedAtIndex:1];
}

#pragma mark - Action
- (IBAction)onEnterMeetingAction:(id)sender {
    NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init];
    params.meetingId = _meetingIdInput.text;
    params.displayName = _nickInput.text;
    
    NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init];
    options.noAudio = ![self openMicWhenJoin];
    options.noVideo = ![self openVideoWhenJoin];
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[[NEMeetingSDK getInstance] getMeetingService] joinMeeting:params opts:options callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
        [SVProgressHUD dismiss];
        if (resultCode != ERROR_CODE_SUCCESS) {
            [weakSelf showErrorCode:resultCode msg:resultMsg];
        }
    }];
}


#pragma mark - Setter
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

@end
