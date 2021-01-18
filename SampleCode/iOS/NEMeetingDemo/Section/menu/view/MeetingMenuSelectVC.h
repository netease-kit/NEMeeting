//
//  MeetingMenuSelectVC.h
//  NEMeetingDemo
//
//  Created by gyy on 2021/1/14.
//  Copyright © 2021 张根宁. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
@protocol MeetingMenuSelectVCDelegate <NSObject>
- (void)didSelectedItems:(NSArray<NEMeetingMenuItem *> *)menuItems;
@end

@interface MeetingMenuSelectVC : BaseViewController
@property (nonatomic, strong) NSArray<NEMeetingMenuItem *> *seletedItems;
@property (nonatomic, weak) id delegate;

@end

NS_ASSUME_NONNULL_END
