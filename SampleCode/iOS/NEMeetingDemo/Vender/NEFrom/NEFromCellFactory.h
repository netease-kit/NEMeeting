//
//  NEFromCellFactory.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

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
