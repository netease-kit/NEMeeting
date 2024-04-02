// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#ifndef SelectedMenuItemEditVC_h
#define SelectedMenuItemEditVC_h

@interface SelectedMenuItemEditVC : UIViewController

@property(nonatomic, strong) NEMeetingMenuItem *menuItem;

@property(nonatomic, copy) void (^MenuItemDeleteCallback)(void);
@property(nonatomic, copy) void (^menuItemSure)(void);
@end

#endif /* SelectedMenuItemEditVC_h */
