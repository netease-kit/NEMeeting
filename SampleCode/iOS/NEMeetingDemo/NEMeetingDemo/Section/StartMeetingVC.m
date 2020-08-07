//
//  StartMeetingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "StartMeetingVC.h"
#import "CheckBox.h"

@interface StartMeetingVC ()<CheckBoxDelegate>

@property (weak, nonatomic) IBOutlet CheckBox *checkBox;
@property (weak, nonatomic) IBOutlet UITextField *meetingIdInput;
@property (weak, nonatomic) IBOutlet UITextField *nickInput;

@property (nonatomic, readonly) BOOL openVideoWhenJoinMeeting;
@property (nonatomic, readonly) BOOL openMicWhenJoinMeeting;
@property (nonatomic, readonly) BOOL useUserMeetingId;

@end

@implementation StartMeetingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [_checkBox setItemTitleWithArray:@[@"入会时打开摄像头", @"入会时打开麦克风", @"使用个人会议号"]];
    _checkBox.delegate = self;
}

#pragma mark - Function
- (void)doStartMeeting {
    NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init];
    params.displayName = _nickInput.text;
    params.meetingId = _meetingIdInput.text;
    
    NEStartMeetingOptions *options = [[NEStartMeetingOptions alloc] init];
    options.noVideo = ![self openVideoWhenJoinMeeting];
    options.noAudio = ![self openMicWhenJoinMeeting];
    
    WEAK_SELF(weakSelf);
    [SVProgressHUD show];
    [[NEMeetingSDK getInstance].getMeetingService startMeeting:params opts:options callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
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

-(void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:(CheckBox *)checkbox {
    if (index == 2) { //使用个人会议号
        if ([self useUserMeetingId]) {
            [self doGetUserMeetingId];
        }
    }
}

#pragma mark - Getter
- (BOOL)openVideoWhenJoinMeeting {
    return [_checkBox getItemSelectedAtIndex:0];
}

- (BOOL)openMicWhenJoinMeeting {
    return [_checkBox getItemSelectedAtIndex:1];
}

- (BOOL)useUserMeetingId {
    return [_checkBox getItemSelectedAtIndex:2];
}

@end
