//
//  NEFromRow.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromRow.h"

@implementation NEFromRow

+ (instancetype)rowWithType:(NEFromRowType)type tag:(NSString *)tag {
    NEFromRow *row = [[NEFromRow alloc] init];
    row.rowType = type;
    row.tag = tag;
    return row;
}

- (instancetype)init {
    if (self = [super init]) {
        _userInteractionEnabled = YES;
    }
    return self;
}


- (void)setValue:(id)value {
    if (_value != value) {
        _value = value;
        if (_onValueChanged) {
            _onValueChanged(value, self);
        }
    }
}

@end

@implementation NEFromGroup

- (instancetype)init {
    if (self = [super init]) {
        _rows = [NSMutableArray array];
    }
    return self;
}

@end
