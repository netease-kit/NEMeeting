// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>
#import "NEFromRow.h"
#import "NEFromBaseCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEFromCellFactory : NSObject

+ (instancetype)shareInstance;

- (void)tableViewRegisterCell:(UITableView *)tableView;

- (NEFromBaseCell *)dequeueReusableCell:(UITableView *)tableView
                           forIndexPath:(NSIndexPath *)indexPath
                                rowType:(NEFromRowType)rowType;

@end

NS_ASSUME_NONNULL_END
