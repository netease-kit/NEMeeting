//
//  NEFromBaseCell.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEFromRow.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEFromBaseCell : UITableViewCell

@property (nonatomic, strong) NEFromRow *row;

@property (nonatomic, strong) UILabel *titleLabel;

- (void)refreshCellWithRow:(NEFromRow *)row;

- (void)doInit;

@end

NS_ASSUME_NONNULL_END
