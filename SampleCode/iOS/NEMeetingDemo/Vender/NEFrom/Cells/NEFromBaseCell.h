// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

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
