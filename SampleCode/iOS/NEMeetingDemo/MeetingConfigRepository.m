//
//  MeetingConfigRepository.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/8/4.
//

#import <Foundation/Foundation.h>
#import "MeetingConfigRepository.h"

NSString * const kMeetingConfigJoinTimeout = @"MeetingConfig_joinTimeout";

@implementation MeetingConfigRepository

+ (instancetype)getInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[MeetingConfigRepository alloc] init];
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


@end
