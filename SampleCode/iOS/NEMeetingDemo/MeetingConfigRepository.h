// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.


#ifndef MeetingConfigRepository_h
#define MeetingConfigRepository_h

@interface MeetingConfigRepository : NSObject

+ (instancetype)getInstance;

+ (NSDictionary<NSString *,NSString *> *) audioProfiles;

@property (nonatomic, assign) NSInteger joinMeetingTimeout;

@property (nonatomic, assign) BOOL noMuteAllVideo;

@property (nonatomic, assign) BOOL noMuteAllAudio;

@property (nonatomic, readonly) BOOL useSpeechAudioProfile;

@property (nonatomic, readonly) BOOL useMusicAudioProfile;

@property (nonatomic, copy) NSString *audioProfile;

@end


#endif /* MeetingConfigRepository_h */
