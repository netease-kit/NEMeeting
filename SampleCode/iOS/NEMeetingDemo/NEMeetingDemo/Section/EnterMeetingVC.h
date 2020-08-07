//
//  EnterMeetingVC.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, EnterMeetingType){
    EnterMeetingAnonymity = 0,
    EnterMeetingNormal
};

@interface EnterMeetingVC : BaseViewController

@property (nonatomic, assign) EnterMeetingType type;

@end

NS_ASSUME_NONNULL_END
