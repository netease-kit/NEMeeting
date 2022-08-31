// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

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
