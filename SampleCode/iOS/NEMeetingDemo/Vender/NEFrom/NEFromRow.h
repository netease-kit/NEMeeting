// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class NEFromRow;

typedef NS_ENUM(NSInteger, NEFromRowType){
    NEFromRowTypeTitleInput,
    NEFromRowTypeTitleDate,
    NEFromRowTypeTitleSwitch,
    NEFromRowTypeTextField,
    NEFromRowTypeLabel,
};

typedef void(^onRowValueChangedBlock)(id newValue, NEFromRow *row);
typedef void(^onRowSelectedBlock)(NEFromRow *row);

@interface NEFromRow : NSObject

@property (nonatomic, copy) NSString *tag;

@property (nonatomic, assign) NEFromRowType rowType;

@property (nonatomic, copy) NSString *title;

@property (nonatomic, copy) NSString *subTitle;

@property (nonatomic, strong) id value;

@property (nonatomic, assign) UITableViewCellAccessoryType accessoryType;

@property (nonatomic, assign) BOOL userInteractionEnabled;

@property (nonatomic, strong) onRowValueChangedBlock onValueChanged;

@property (nonatomic, strong) onRowSelectedBlock onRowSelected;

@property (nonatomic, strong) NSDictionary *config;

@property (nonatomic, assign) BOOL hideRightItem;

+ (instancetype)rowWithType:(NEFromRowType)type tag:(NSString *)tag;

@end


@interface NEFromGroup : NSObject

@property (nonatomic, strong) NSMutableArray<NEFromRow *> *rows;

@end

NS_ASSUME_NONNULL_END
