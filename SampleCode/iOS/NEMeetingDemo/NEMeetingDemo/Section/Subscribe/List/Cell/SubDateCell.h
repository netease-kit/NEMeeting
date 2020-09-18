//
//  SubDateCell.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEDateItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface SubDateCell : UITableViewCell

- (void)refreshWithDateItem:(NEDateItem *)item;

@end

NS_ASSUME_NONNULL_END
