// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "HomeItem.h"

@implementation HomeItem
+ (instancetype)itemWithTitle:(NSString *)title imageName:(NSString *)imageName {
  HomeItem *item = [self new];
  item.title = title;
  item.imageName = imageName;
  return item;
}
@end
