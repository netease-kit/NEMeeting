//
//  MeetingConfigRepository.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/8/4.
//

#import <Foundation/Foundation.h>
#import "MeetingConfigRepository.h"

NSString * const kMeetingConfigJoinTimeout = @"MeetingConfig_joinTimeout";
NSString * const kNoMuteAllVideo = @"MeetingConfig_noMuteAllVideo";
NSString * const kNoMuteAllAudio = @"MeetingConfig_noMuteAllAudio";

@implementation MeetingConfigRepository

+ (NSDictionary<NSString *,NSString *> *) audioProfiles {
    static NSDictionary *profiles;
    if (profiles == nil) {
        profiles = @{
            @"default": @"默认",
            @"speech": @"语音",
            @"music": @"音乐",
        };
    }
    return profiles;
}

+ (instancetype)getInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[MeetingConfigRepository alloc] init];
        [[NSUserDefaults standardUserDefaults] registerDefaults: @{@"audioProfile": @"default"}];
        [[NSUserDefaults standardUserDefaults] registerDefaults: @{kNoMuteAllVideo: @(YES)}];
        [[NSUserDefaults standardUserDefaults] registerDefaults: @{kNoMuteAllAudio: @(NO)}];
    });
    return instance;
}

- (NSInteger)joinMeetingTimeout {
    NSInteger value = [[NSUserDefaults standardUserDefaults] integerForKey: kMeetingConfigJoinTimeout];
    return value > 0 ? value : 45000;
}

- (void)setJoinMeetingTimeout:(NSInteger)joinMeetingTimeout {
    [[NSUserDefaults standardUserDefaults] setInteger:joinMeetingTimeout forKey:kMeetingConfigJoinTimeout];
}

- (void) setAudioProfile:(NSString *)audioProfile {
    [[NSUserDefaults standardUserDefaults] setValue:audioProfile forKey: @"audioProfile"];
}

- (BOOL)noMuteAllAudio {
    return [[NSUserDefaults standardUserDefaults] boolForKey:kNoMuteAllAudio];
}

- (void)setNoMuteAllAudio:(BOOL)noMuteAllAudio {
    [[NSUserDefaults standardUserDefaults] setBool:noMuteAllAudio forKey: kNoMuteAllAudio];
}

- (BOOL)noMuteAllVideo {
    return [[NSUserDefaults standardUserDefaults] boolForKey:kNoMuteAllVideo];
}

- (void)setNoMuteAllVideo:(BOOL)noMuteAllVideo {
    [[NSUserDefaults standardUserDefaults] setBool:noMuteAllVideo forKey: kNoMuteAllVideo];
}

- (NSString *)audioProfile {
   return [[NSUserDefaults standardUserDefaults] stringForKey: @"audioProfile"];
}

- (BOOL)useMusicAudioProfile {
    return [self.audioProfile isEqualToString: @"music"];
}

- (BOOL)useSpeechAudioProfile {
    return [self.audioProfile isEqualToString: @"speech"];
}


@end
