// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingConfigRepository.h"
#import <Foundation/Foundation.h>

NSString *const kMeetingConfigJoinTimeout = @"MeetingConfig_joinTimeout";
NSString *const kNoMuteAllVideo = @"MeetingConfig_noMuteAllVideo";
NSString *const kNoMuteAllAudio = @"MeetingConfig_noMuteAllAudio";

static NSString *const kIsOpenAudioSettingKey = @"com.meeting.userdefault.key.isopenaudiosetting";

@implementation MeetingConfigRepository
+ (NSDictionary<NSString *, NSString *> *)audioProfiles {
  static NSDictionary *profiles;
  if (profiles == nil) {
    profiles = @{
      @"0" : @"默认",
      @"1" : @"标准音质",
      @"2" : @"标准音质扩展",
      @"3" : @"中等音质",
      @"4" : @"中等音质（立体音）",
      @"5" : @"高音质",
      @"6" : @"高音质（立体音）",
    };
  }
  return profiles;
}
+ (NSDictionary<NSString *, NSString *> *)audioScenarios {
  static NSDictionary *scenarios;
  if (scenarios == nil) {
    scenarios = @{@"0" : @"默认", @"1" : @"语音场景", @"2" : @"音乐场景", @"3" : @"聊天室场景"};
  }
  return scenarios;
}
+ (instancetype)getInstance {
  static id instance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    instance = [[MeetingConfigRepository alloc] init];
    [[NSUserDefaults standardUserDefaults] registerDefaults:@{@"audioProfile" : @"0"}];
    [[NSUserDefaults standardUserDefaults] registerDefaults:@{@"audioScenario" : @"0"}];
    [[NSUserDefaults standardUserDefaults] registerDefaults:@{kNoMuteAllVideo : @(YES)}];
    [[NSUserDefaults standardUserDefaults] registerDefaults:@{kNoMuteAllAudio : @(NO)}];
  });
  return instance;
}

- (NSInteger)joinMeetingTimeout {
  NSInteger value = [[NSUserDefaults standardUserDefaults] integerForKey:kMeetingConfigJoinTimeout];
  return value > 0 ? value : 45000;
}

- (void)setJoinMeetingTimeout:(NSInteger)joinMeetingTimeout {
  [[NSUserDefaults standardUserDefaults] setInteger:joinMeetingTimeout
                                             forKey:kMeetingConfigJoinTimeout];
}

- (void)setAudioProfile:(NSString *)audioProfile {
  [[NSUserDefaults standardUserDefaults] setValue:audioProfile forKey:@"audioProfile"];
}
- (void)setAudioScenario:(NSString *)audioScenario {
  [[NSUserDefaults standardUserDefaults] setValue:audioScenario forKey:@"audioScenario"];
}
- (BOOL)noMuteAllAudio {
  return [[NSUserDefaults standardUserDefaults] boolForKey:kNoMuteAllAudio];
}

- (void)setNoMuteAllAudio:(BOOL)noMuteAllAudio {
  [[NSUserDefaults standardUserDefaults] setBool:noMuteAllAudio forKey:kNoMuteAllAudio];
}

- (BOOL)noMuteAllVideo {
  return [[NSUserDefaults standardUserDefaults] boolForKey:kNoMuteAllVideo];
}

- (void)setNoMuteAllVideo:(BOOL)noMuteAllVideo {
  [[NSUserDefaults standardUserDefaults] setBool:noMuteAllVideo forKey:kNoMuteAllVideo];
}

- (NSString *)audioProfile {
  return [[NSUserDefaults standardUserDefaults] stringForKey:@"audioProfile"];
}
- (NSString *)audioScenario {
  return [[NSUserDefaults standardUserDefaults] stringForKey:@"audioScenario"];
}
- (BOOL)useMusicAudioProfile {
  return [self.audioProfile isEqualToString:@"music"];
}

- (BOOL)useSpeechAudioProfile {
  return [self.audioProfile isEqualToString:@"speech"];
}

- (void)setIsOpenAudioSetting:(BOOL)isOpenAudioSetting {
  [NSUserDefaults.standardUserDefaults setBool:isOpenAudioSetting forKey:kIsOpenAudioSettingKey];
}
- (BOOL)isOpenAudioSetting {
  return [NSUserDefaults.standardUserDefaults boolForKey:kIsOpenAudioSettingKey];
}
@end
