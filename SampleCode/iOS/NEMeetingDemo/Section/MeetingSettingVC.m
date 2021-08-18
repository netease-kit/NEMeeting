//
//  MeetingSettingVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "MeetingSettingVC.h"
#import "MeetingConfigRepository.h"
#import "Config.h"

NSString * const kSettingsShowMeetingTime = @"kSettingsShowMeetingTime";
NSString * const kSettingsJoinMeetingOpenVideo = @"kSettingsJoinMeetingOpenVideo";
NSString * const kSettingsJoinMeetingOpenAudio = @"kSettingsJoinMeetingOpenAudio";
NSString * const kSettingsJoinMeetingTimeout = @"kSettingsJoinMeetingTimeout";

@interface MeetingSettingVC ()

@property (nonatomic, assign) BOOL showMeetingTime;
@property (nonatomic, assign) BOOL openVideoWhenJoin;
@property (nonatomic, assign) BOOL openAudioWhenJoin;
@property (nonatomic, assign) BOOL openCustomServerUrl;


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
    __weak typeof(self) weakSelf = self;
    XLFormDescriptor *form = [XLFormDescriptor formDescriptorWithTitle:@"会议设置"];
    
    XLFormSectionDescriptor *section = [XLFormSectionDescriptor formSection];
    section.title = @"入会配置";
    [form addFormSection:section];
    
    
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
    XLFormRowDescriptor *row3 = [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                                                      rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                                        title:@"开启自定义服务器域名"];
    row3.height = 60.0;
    row3.value = @(self.openCustomServerUrl);
    row3.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        weakSelf.openCustomServerUrl = [newValue boolValue];
    };
    [section addFormRow:row3];
    
    XLFormRowDescriptor *joinTimeoutItem = [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingTimeout
        rowType:XLFormRowDescriptorTypeInteger
        title:@"入会超时时间(毫秒)"];
    joinTimeoutItem.height = 60.0;
    joinTimeoutItem.value = @([[MeetingConfigRepository getInstance] joinMeetingTimeout]);
    joinTimeoutItem.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        NSInteger value = 0;
        if (newValue && (NSNull *)newValue != [NSNull null]) {
            value = [newValue integerValue];
        }
        [MeetingConfigRepository getInstance].joinMeetingTimeout = value;
    };
    [section addFormRow:joinTimeoutItem];
    
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

- (BOOL)openCustomServerUrl {
    return [[NSUserDefaults standardUserDefaults] boolForKey:@"openCustomServerUrl"];
}

- (void)setOpenCustomServerUrl:(BOOL)openCustomServerUrl {
    [[NSUserDefaults standardUserDefaults] setBool:openCustomServerUrl forKey:@"openCustomServerUrl"];
}

@end
