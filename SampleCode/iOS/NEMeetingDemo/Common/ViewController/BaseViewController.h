// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <UIKit/UIKit.h>
#import "UIView+Toast.h"

NS_ASSUME_NONNULL_BEGIN

@interface BaseViewController : UIViewController

- (void)hiddenBackButton;

- (void)showMessage:(NSString *)message;
- (void)showErrorCode:(NSInteger)code msg:(NSString *)msg;
- (void)setupSubviews;
- (void)makeConstraints;
@end

NS_ASSUME_NONNULL_END
