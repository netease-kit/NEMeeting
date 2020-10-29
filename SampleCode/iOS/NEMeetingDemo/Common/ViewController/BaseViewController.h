//
//  BaseViewController.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UIView+Toast.h"

NS_ASSUME_NONNULL_BEGIN

@interface BaseViewController : UIViewController

- (void)hiddenBackButton;

- (void)showErrorCode:(NSInteger)code msg:(NSString *)msg;

@end

NS_ASSUME_NONNULL_END
