//
//  TimerButton.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TimerButton : UIButton

@property(nonatomic, copy) NSString *neTitle;
@property(nonatomic, assign) int64_t startTime;

@end

NS_ASSUME_NONNULL_END
