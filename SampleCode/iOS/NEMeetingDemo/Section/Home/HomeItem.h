// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface HomeItem : NSObject
@property(nonatomic, copy) NSString *title;
@property(nonatomic, copy) NSString *imageName;
+ (instancetype)itemWithTitle:(NSString *)title imageName:(NSString *)imageName;
@end

NS_ASSUME_NONNULL_END
