// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingSettingVC.h"
#import <Foundation/Foundation.h>
#import "MeetingConfigRepository.h"
#import "ServerConfig.h"

NSString *const kSettingsShowMeetingTime = @"kSettingsShowMeetingTime";
NSString *const kSettingsJoinMeetingOpenVideo = @"kSettingsJoinMeetingOpenVideo";
NSString *const kSettingsJoinMeetingOpenAudio = @"kSettingsJoinMeetingOpenAudio";
NSString *const kSettingsAudioAINS = @"kSettingsAudioAINS";
NSString *const kSettingsJoinMeetingTimeout = @"kSettingsJoinMeetingTimeout";

NSString *const kSettingsOpenAudioSettings = @"kSettingsOpenAudioSettings";

@interface MeetingSettingVC ()

@property(nonatomic, assign) BOOL showMeetingTime;
@property(nonatomic, assign) BOOL openVideoWhenJoin;
@property(nonatomic, assign) BOOL openAudioWhenJoin;
@property(nonatomic, assign) BOOL openCustomServerUrl;
@property(nonatomic, assign) BOOL audioAINSEnabled;

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

  XLFormRowDescriptor *row0 =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsShowMeetingTime
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"显示会议持续时间"];
  row0.height = 60.0;
  row0.value = @(self.showMeetingTime);
  row0.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        weakSelf.showMeetingTime = [newValue boolValue];
      };
  [section addFormRow:row0];

  XLFormRowDescriptor *row1 =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenVideo
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"入会时开启视频"];
  row1.height = 60.0;
  row1.value = @(self.openVideoWhenJoin);
  row1.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        weakSelf.openVideoWhenJoin = [newValue boolValue];
      };
  [section addFormRow:row1];

  XLFormRowDescriptor *row2 =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"入会时开启音频"];
  row2.height = 60.0;
  row2.value = @(self.openAudioWhenJoin);
  row2.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        weakSelf.openAudioWhenJoin = [newValue boolValue];
      };
  [section addFormRow:row2];
  XLFormRowDescriptor *row3 =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"开启自定义服务器域名"];
  row3.height = 60.0;
  row3.value = @(self.openCustomServerUrl);
  row3.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        weakSelf.openCustomServerUrl = [newValue boolValue];
      };
  [section addFormRow:row3];

  XLFormRowDescriptor *joinTimeoutItem =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingTimeout
                                            rowType:XLFormRowDescriptorTypeInteger
                                              title:@"入会超时时间(毫秒)"];
  joinTimeoutItem.height = 60.0;
  joinTimeoutItem.value = @([[MeetingConfigRepository getInstance] joinMeetingTimeout]);
  joinTimeoutItem.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        NSInteger value = 0;
        if (newValue && (NSNull *)newValue != [NSNull null]) {
          value = [newValue integerValue];
        }
        [MeetingConfigRepository getInstance].joinMeetingTimeout = value;
      };
  [section addFormRow:joinTimeoutItem];

  XLFormRowDescriptor *audioAINS =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsAudioAINS
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"语音智能降噪"];
  audioAINS.height = 60.0;
  audioAINS.value = @(self.audioAINSEnabled);
  audioAINS.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        weakSelf.audioAINSEnabled = [newValue boolValue];
      };
  [section addFormRow:audioAINS];

  {
    XLFormRowDescriptor *row =
        [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                              rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                title:@"关闭全体静音/解除全体静音"];
    row.height = 60.0;
    row.value = @([MeetingConfigRepository getInstance].noMuteAllAudio);
    row.onChangeBlock = ^(id _Nullable oldValue, id _Nullable newValue,
                          XLFormRowDescriptor *_Nonnull rowDescriptor) {
      [MeetingConfigRepository getInstance].noMuteAllAudio = [newValue boolValue];
    };
    [section addFormRow:row];
  }

  {
    XLFormRowDescriptor *row =
        [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                              rowType:XLFormRowDescriptorTypeBooleanSwitch
                                                title:@"关闭全体关闭视频/打开全体视频"];
    row.height = 60.0;
    row.value = @([MeetingConfigRepository getInstance].noMuteAllVideo);
    row.onChangeBlock = ^(id _Nullable oldValue, id _Nullable newValue,
                          XLFormRowDescriptor *_Nonnull rowDescriptor) {
      [MeetingConfigRepository getInstance].noMuteAllVideo = [newValue boolValue];
    };
    [section addFormRow:row];
  }
  XLFormRowDescriptor *row =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsJoinMeetingOpenAudio
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"使用虚拟背景"];
  row.height = 60.0;
  row.value = @([NEMeetingKit.getInstance.getSettingsService isVirtualBackgroundEnabled]);
  row.onChangeBlock = ^(id _Nullable oldValue, id _Nullable newValue,
                        XLFormRowDescriptor *_Nonnull rowDescriptor) {
    [NEMeetingKit.getInstance.getSettingsService enableVirtualBackground:[newValue boolValue]];
    if ([newValue boolValue]) {
      [NEMeetingKit.getInstance.getSettingsService setBuiltinVirtualBackgrounds:[self fetchArray]];
    }
  };
  [section addFormRow:row];

#pragma mark-----------------------------  自定义加密  -----------------------------
  XLFormSectionDescriptor *encryptSection = [XLFormSectionDescriptor formSection];
  encryptSection.title = @"音视频自定义加密";
  [form addFormSection:encryptSection];

  XLFormRowDescriptor *audioEncryptRow =
      [XLFormRowDescriptor formRowDescriptorWithTag:@"audioEncrypt"
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"音频加密"];
  audioEncryptRow.height = 60.0;
  audioEncryptRow.value = @([[NSUserDefaults standardUserDefaults] boolForKey:@"audioEncrypt"]);
  audioEncryptRow.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        [[NSUserDefaults standardUserDefaults] setBool:[newValue boolValue] forKey:@"audioEncrypt"];
      };
  [encryptSection addFormRow:audioEncryptRow];

  XLFormRowDescriptor *videoEncryptRow =
      [XLFormRowDescriptor formRowDescriptorWithTag:@"videoEncrypt"
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"视频加密"];
  videoEncryptRow.height = 60.0;
  videoEncryptRow.value = @([[NSUserDefaults standardUserDefaults] boolForKey:@"videoEncrypt"]);
  videoEncryptRow.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        [[NSUserDefaults standardUserDefaults] setBool:[newValue boolValue] forKey:@"videoEncrypt"];
      };
  [encryptSection addFormRow:videoEncryptRow];

#pragma mark------------------------ 音频设置开关 ------------------------
  XLFormSectionDescriptor *audioSettingsSection = [XLFormSectionDescriptor formSection];
  audioSettingsSection.title = @"音频设置";
  [form addFormSection:audioSettingsSection];

  XLFormRowDescriptor *audioSettingSwitch =
      [XLFormRowDescriptor formRowDescriptorWithTag:kSettingsOpenAudioSettings
                                            rowType:XLFormRowDescriptorTypeBooleanSwitch
                                              title:@"音频设置开关"];
  audioSettingSwitch.height = 60.0;
  audioSettingSwitch.value = @(MeetingConfigRepository.getInstance.isOpenAudioSetting);
  audioSettingSwitch.onChangeBlock =
      ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
        MeetingConfigRepository.getInstance.isOpenAudioSetting = [newValue boolValue];
        weakSelf.form = [weakSelf setupForm];
      };
  [audioSettingsSection addFormRow:audioSettingSwitch];

  if (MeetingConfigRepository.getInstance.isOpenAudioSetting) {
    {
      XLFormRowDescriptor *audioProfile =
          [XLFormRowDescriptor formRowDescriptorWithTag:@"audioProfile"
                                                rowType:XLFormRowDescriptorTypeSelectorActionSheet
                                                  title:@"音频质量模式"];
      audioProfile.height = 60.0;
      NSMutableArray *selectorOptions = [[NSMutableArray alloc] init];
      for (id key in MeetingConfigRepository.audioProfiles) {
        [selectorOptions
            addObject:[XLFormOptionsObject
                          formOptionsObjectWithValue:key
                                         displayText:[MeetingConfigRepository.audioProfiles
                                                         objectForKey:key]]];
      }
      audioProfile.selectorOptions = selectorOptions;
      audioProfile.value = [XLFormOptionsObject
          formOptionsOptionForValue:[MeetingConfigRepository getInstance].audioProfile
                        fromOptions:selectorOptions];
      audioProfile.onChangeBlock = ^(id _Nullable oldValue, id _Nullable newValue,
                                     XLFormRowDescriptor *_Nonnull rowDescriptor) {
        if (newValue && (NSNull *)newValue != [NSNull null]) {
          [[MeetingConfigRepository getInstance] setAudioProfile:[newValue formValue]];
        }
      };
      [audioSettingsSection addFormRow:audioProfile];
    }
    {
      XLFormRowDescriptor *audioScenario =
          [XLFormRowDescriptor formRowDescriptorWithTag:@"audioScenario"
                                                rowType:XLFormRowDescriptorTypeSelectorActionSheet
                                                  title:@"音频应用场景"];
      audioScenario.height = 60.0;
      NSMutableArray *selectorOptions = [[NSMutableArray alloc] init];
      for (id key in MeetingConfigRepository.audioScenarios) {
        [selectorOptions
            addObject:[XLFormOptionsObject
                          formOptionsObjectWithValue:key
                                         displayText:[MeetingConfigRepository.audioScenarios
                                                         objectForKey:key]]];
      }
      audioScenario.selectorOptions = selectorOptions;
      audioScenario.value = [XLFormOptionsObject
          formOptionsOptionForValue:[MeetingConfigRepository getInstance].audioScenario
                        fromOptions:selectorOptions];
      audioScenario.onChangeBlock = ^(id _Nullable oldValue, id _Nullable newValue,
                                      XLFormRowDescriptor *_Nonnull rowDescriptor) {
        if (newValue && (NSNull *)newValue != [NSNull null]) {
          [MeetingConfigRepository.getInstance setAudioScenario:[newValue formValue]];
        }
      };
      [audioSettingsSection addFormRow:audioScenario];
    }
  }
  return form;
}
- (NSArray<NEMeetingVirtualBackground *> *)fetchArray {
  NEMeetingVirtualBackground *source = [NEMeetingVirtualBackground new];
  source.path = [self pathWithName:@"1"];

  NEMeetingVirtualBackground *source1 = [NEMeetingVirtualBackground new];
  source1.path = [self pathWithName:@"2"];
  NEMeetingVirtualBackground *source2 = [NEMeetingVirtualBackground new];
  source2.path = [self pathWithName:@"3"];

  NEMeetingVirtualBackground *source3 = [NEMeetingVirtualBackground new];
  source3.path = [self pathWithName:@"4"];
  NEMeetingVirtualBackground *source4 = [NEMeetingVirtualBackground new];
  source4.path = [self pathWithName:@"5"];
  NEMeetingVirtualBackground *source5 = [NEMeetingVirtualBackground new];
  source5.path = [self pathWithName:@"6"];
  return @[ source, source1, source2, source3, source4, source5 ];
}
- (NSString *)pathWithName:(NSString *)name {
  NSString *sourcePath = [[NSBundle mainBundle] pathForResource:name ofType:@"png"];
  NSString *path =
      [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
  return [self copyFile:sourcePath toPath:path];
}
- (NSString *)copyFile:(NSString *)sourcePath toPath:(NSString *)toPath {
  BOOL retVal = YES;  // If the file already exists, we'll return success…
  NSString *finalLocation = [toPath stringByAppendingPathComponent:[sourcePath lastPathComponent]];
  if (![[NSFileManager defaultManager] fileExistsAtPath:finalLocation]) {
    retVal = [[NSFileManager defaultManager] copyItemAtPath:sourcePath
                                                     toPath:finalLocation
                                                      error:NULL];
  }
  return retVal ? finalLocation : @"";
}

#pragma mark - Getter
- (BOOL)showMeetingTime {
  return [[NEMeetingKit getInstance].getSettingsService isShowMyMeetingElapseTimeEnabled];
}

- (void)setShowMeetingTime:(BOOL)showMeetingTime {
  [[NEMeetingKit getInstance].getSettingsService enableShowMyMeetingElapseTime:showMeetingTime];
}

- (BOOL)openVideoWhenJoin {
  return [[NEMeetingKit getInstance].getSettingsService isTurnOnMyVideoWhenJoinMeetingEnabled];
}

- (void)setOpenVideoWhenJoin:(BOOL)openVideoWhenJoin {
  [[NEMeetingKit getInstance].getSettingsService setTurnOnMyVideoWhenJoinMeeting:openVideoWhenJoin];
}

- (BOOL)openAudioWhenJoin {
  return [[NEMeetingKit getInstance].getSettingsService isTurnOnMyAudioWhenJoinMeetingEnabled];
}

- (void)setOpenAudioWhenJoin:(BOOL)openAudioWhenJoin {
  [[NEMeetingKit getInstance].getSettingsService setTurnOnMyAudioWhenJoinMeeting:openAudioWhenJoin];
}

- (BOOL)openCustomServerUrl {
  return [[NSUserDefaults standardUserDefaults] boolForKey:@"openCustomServerUrl"];
}

- (void)setOpenCustomServerUrl:(BOOL)openCustomServerUrl {
  [[NSUserDefaults standardUserDefaults] setBool:openCustomServerUrl forKey:@"openCustomServerUrl"];
}

- (BOOL)audioAINSEnabled {
  return [[NEMeetingKit getInstance].getSettingsService isAudioAINSEnabled];
}

- (void)setAudioAINSEnabled:(BOOL)enable {
  [[NEMeetingKit getInstance].getSettingsService enableAudioAINS:enable];
}

@end
