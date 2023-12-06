// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEMeetingSafeArea.h"

@implementation NEMeetingSafeArea
+ (CGFloat)ne_distance:(NEMeetingSafeAreaInsets)insets {
  if (@available(iOS 13.0, *)) {
    NSSet *set = [UIApplication sharedApplication].connectedScenes;
    UIWindowScene *windowScene = [set anyObject];
    UIWindow *window = windowScene.windows.firstObject;
    switch (insets) {
      case NEMeetingSafeAreaInsetsTop:
        return window.safeAreaInsets.top > 0 ? window.safeAreaInsets.top : 0;
      case NEMeetingSafeAreaInsetsLeft:
        return window.safeAreaInsets.left > 0 ? window.safeAreaInsets.left : 0;
      case NEMeetingSafeAreaInsetsRight:
        return window.safeAreaInsets.right > 0 ? window.safeAreaInsets.right : 0;
      case NEMeetingSafeAreaInsetsBottom:
        return window.safeAreaInsets.bottom > 0 ? window.safeAreaInsets.bottom : 0;
      default:
        break;
    }
    return window.safeAreaInsets.top;
  } else if (@available(iOS 11.0, *)) {
    UIWindow *window = [UIApplication sharedApplication].windows.firstObject;
    switch (insets) {
      case NEMeetingSafeAreaInsetsTop:
        return window.safeAreaInsets.top > 0 ? window.safeAreaInsets.top : 0;
      case NEMeetingSafeAreaInsetsLeft:
        return window.safeAreaInsets.left > 0 ? window.safeAreaInsets.left : 0;
      case NEMeetingSafeAreaInsetsRight:
        return window.safeAreaInsets.right > 0 ? window.safeAreaInsets.right : 0;
      case NEMeetingSafeAreaInsetsBottom:
        return window.safeAreaInsets.bottom > 0 ? window.safeAreaInsets.bottom : 0;
      default:
        break;
    }
  }
  return 0;
}
@end
