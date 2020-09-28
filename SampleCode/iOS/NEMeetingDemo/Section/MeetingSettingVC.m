//
//  MeetingSettingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MeetingSettingVC.h"

NSString * const kSettingsShowMeetingTime = @"kSettingsShowMeetingTime";
NSString * const kSettingsJoinMeetingOpenVideo = @"kSettingsJoinMeetingOpenVideo";
NSString * const kSettingsJoinMeetingOpenAudio = @"kSettingsJoinMeetingOpenAudio";

@interface MeetingSettingVC ()

@property (nonatomic, assign) BOOL showMeetingTime;
@property (nonatomic, assign) BOOL openVideoWhenJoin;
@property (nonatomic, assign) BOOL openAudioWhenJoin;

@end

@implementation MeetingSettingVC

@synthesize showMeetingTime = _showMeetingTime;
@synthesize openVideoWhenJoin = _openVideoWhenJoin;
@synthesize openAudioWhenJoin = _openAudioWhenJoin;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"会议设置";
    self.form = [self setupForm];
}

- (XLFormDescriptor *)setupForm {
    XLFormDescriptor *form = [XLFormDescriptor formDescriptorWithTitle:@"会议设置"];
    XLFormSectionDescriptor *section = [XLFormSectionDescriptor formSection];
    [form addFormSection:section];
    
    __weak typeof(self) weakSelf = self;
    XLFormRowDescriptor *row0 = [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsShowMeetingTime
                                                                      rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                                        title:@"显示会议持续时间"];
    row0.height = 60.0;
    row0.value = @(self.showMeetingTime);
    row0.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        weakSelf.showMeetingTime = [newValue boolValue];
    };
    [section addFormRow:row0];
    
    XLFormRowDescriptor *row1 = [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenVideo
                                                                      rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                                        title:@"入会时开启视频"];
    row1.height = 60.0;
    row1.value = @(self.openVideoWhenJoin);
    row1.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        weakSelf.openVideoWhenJoin = [newValue boolValue];
    };
    [section addFormRow:row1];
    
    XLFormRowDescriptor *row2 = [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                                                      rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                                        title:@"入会时开启音频"];
    row2.height = 60.0;
    row2.value = @(self.openAudioWhenJoin);
    row2.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        weakSelf.openAudioWhenJoin = [newValue boolValue];
    };
    [section addFormRow:row2];
    return form;
}

#pragma mark - Getter
- (BOOL)showMeetingTime {
    return [[NEMeetingSDK getInstance].getSettingsService isShowMyMeetingElapseTimeEnabled];
}

- (void)setShowMeetingTime:(BOOL)showMeetingTime {
    [[NEMeetingSDK getInstance].getSettingsService enableShowMyMeetingElapseTime:showMeetingTime];
}

- (BOOL)openVideoWhenJoin {
    return [[NEMeetingSDK getInstance].getSettingsService isTurnOnMyVideoWhenJoinMeetingEnabled];
}

- (void)setOpenVideoWhenJoin:(BOOL)openVideoWhenJoin {
    [[NEMeetingSDK getInstance].getSettingsService setTurnOnMyVideoWhenJoinMeeting:openVideoWhenJoin];
}

- (BOOL)openAudioWhenJoin {
    return [[NEMeetingSDK getInstance].getSettingsService isTurnOnMyAudioWhenJoinMeetingEnabled];
}

- (void)setOpenAudioWhenJoin:(BOOL)openAudioWhenJoin {
    [[NEMeetingSDK getInstance].getSettingsService setTurnOnMyAudioWhenJoinMeeting:openAudioWhenJoin];
}

@end
