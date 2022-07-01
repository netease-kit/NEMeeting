// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEFormViewController.h"

NS_ASSUME_NONNULL_BEGIN

/// 编辑预约会议页面
@interface NSEditSubscribeMeetingVC : NEFormViewController
+ (instancetype)editWithItem:(NEMeetingItem *)item;
@end

NS_ASSUME_NONNULL_END
