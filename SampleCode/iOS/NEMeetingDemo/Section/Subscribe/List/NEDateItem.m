// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEDateItem.h"

@implementation NEDateItem

- (instancetype)initWithTimestamp:(uint64_t)timestamp {
  if (self = [super init]) {
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSCalendarUnit components =
        (NSCalendarUnit)(NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay |
                         NSCalendarUnitWeekday | NSCalendarUnitHour | NSCalendarUnitMinute);
    NSDateComponents *dateComponents = [[NSCalendar currentCalendar] components:components
                                                                       fromDate:date];
    _year = dateComponents.year;
    _month = dateComponents.month;
    _day = dateComponents.day;
  }
  return self;
}

@end
