// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
@protocol MeetingMenuSelectVCDelegate <NSObject>
- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems;
@end

@interface MeetingMenuSelectVC : BaseViewController
@property(nonatomic, strong) NSArray<NEMeetingMenuItem *> *seletedItems;
@property(nonatomic, weak) id delegate;
@property(nonatomic, assign) NSInteger currentType;

@end

NS_ASSUME_NONNULL_END
