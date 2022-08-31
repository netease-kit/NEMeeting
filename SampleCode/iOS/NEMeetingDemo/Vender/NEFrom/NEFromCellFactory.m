// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEFromCellFactory.h"

@interface NEFromCellFactory ()

@property (nonatomic, strong) NSMutableDictionary *cellDic;

@end

@implementation NEFromCellFactory

+ (instancetype)shareInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[NEFromCellFactory alloc] init];
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
        [self initCellDic];
    }
    return self;
}

- (void)initCellDic {
    _cellDic = [NSMutableDictionary dictionary];
    _cellDic[@(NEFromRowTypeTitleInput).stringValue] = @"NEFromTitleInputCell";
    _cellDic[@(NEFromRowTypeTitleDate).stringValue] = @"NEFromTitleDateCell";
    _cellDic[@(NEFromRowTypeTitleSwitch).stringValue] = @"NEFromTitleSwitchCell";
    _cellDic[@(NEFromRowTypeTextField).stringValue] = @"NEFromTextFieldCell";
    _cellDic[@(NEFromRowTypeLabel).stringValue] = @"NEFromLabelCell";
    _cellDic[@"default"] = @"NEFromBaseCell";
}

- (void)tableViewRegisterCell:(UITableView *)tableView {
    [_cellDic enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        Class cellClass = NSClassFromString(obj);
        [tableView registerClass:cellClass forCellReuseIdentifier:obj];
    }];
}

- (NEFromBaseCell *)dequeueReusableCell:(UITableView *)tableView
                           forIndexPath:(NSIndexPath *)indexPath
                                rowType:(NEFromRowType)rowType {
    NSString *cellClassName = _cellDic[@(rowType).stringValue];
    if (!cellClassName) {
        cellClassName = @"NEFromBaseCell";
    }
    return [tableView dequeueReusableCellWithIdentifier:cellClassName forIndexPath:indexPath];
}

@end
