// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, EnterMeetingType) { EnterMeetingAnonymity = 0, EnterMeetingJoin };

@interface EnterMeetingVC : BaseViewController

@property(nonatomic, assign) EnterMeetingType type;

@end

NS_ASSUME_NONNULL_END
