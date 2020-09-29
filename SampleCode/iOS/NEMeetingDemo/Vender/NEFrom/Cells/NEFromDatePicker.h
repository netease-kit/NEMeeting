//
//  NEFromDatePicker.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^NDFromDateDidSelected)(NSDate *date);

@interface NEFromDatePickerBar : UIView

+ (void)showWithDate:(NSDate *)date
             minDate:(NSDate *)minDate
             maxDate:(NSDate *)maxDate
            selected:(NDFromDateDidSelected)selectedBlock;

+ (void)dismiss;

@end

@interface NEFromDatePicker : UIView

@end

NS_ASSUME_NONNULL_END
