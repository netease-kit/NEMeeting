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

@property (nonatomic, assign) NSInteger joinMeetingTimeout;

@end


#endif /* MeetingConfigRepository_h */
