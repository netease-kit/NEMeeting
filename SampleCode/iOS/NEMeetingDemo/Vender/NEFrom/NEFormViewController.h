// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <UIKit/UIKit.h>
#import "NEFromRow.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEFormViewController : UIViewController

@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) NSMutableArray<NEFromGroup *> *groups;

- (void)insertRow:(NEFromRow *)row below:(NEFromRow *)below;

- (void)deleteRow:(NEFromRow *)row;

- (void)deleteRowWithTag:(NSString *)tag;

- (NEFromRow *)rowWithTag:(NSString *)tag;

- (NSIndexPath *)rowIndexPathWithTag:(NSString *)tag;

- (NEFromRow *)rowWithTag:(NSString *)tag indexPath:(NSIndexPath *_Nullable*_Nullable)indexPath;

@end

NS_ASSUME_NONNULL_END
