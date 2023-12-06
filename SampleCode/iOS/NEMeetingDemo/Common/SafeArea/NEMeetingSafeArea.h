// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, NEMeetingSafeAreaInsets) {
  NEMeetingSafeAreaInsetsTop,
  NEMeetingSafeAreaInsetsBottom,
  NEMeetingSafeAreaInsetsLeft,
  NEMeetingSafeAreaInsetsRight
};

@interface NEMeetingSafeArea : NSObject
/// 安全距离
+ (CGFloat)ne_distance:(NEMeetingSafeAreaInsets)insets;
@end

NS_ASSUME_NONNULL_END
