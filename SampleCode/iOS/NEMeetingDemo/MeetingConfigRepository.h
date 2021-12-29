//
//  MeetingConfigRepository.h
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/8/4.
//

#ifndef MeetingConfigRepository_h
#define MeetingConfigRepository_h

@interface MeetingConfigRepository : NSObject

+ (instancetype)getInstance;

+ (NSDictionary<NSString *,NSString *> *) audioProfiles;

@property (nonatomic, assign) NSInteger joinMeetingTimeout;

@property (nonatomic, readonly) BOOL useSpeechAudioProfile;

@property (nonatomic, readonly) BOOL useMusicAudioProfile;

@property (nonatomic, copy) NSString *audioProfile;

@end


#endif /* MeetingConfigRepository_h */
