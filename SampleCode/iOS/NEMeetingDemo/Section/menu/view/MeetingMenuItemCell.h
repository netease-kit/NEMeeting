//
//  MeetingMenuItemCell.h
//  NEMeetingDemo
//
//  Created by 郭园园 on 2021/1/14.
//  Copyright © 2021 张根宁. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MeetingMenuItemCell : UICollectionViewCell
@property (nonatomic,strong)UILabel *titleLabel;
@property (nonatomic,strong)NEMeetingMenuItem *item;
@end

NS_ASSUME_NONNULL_END
