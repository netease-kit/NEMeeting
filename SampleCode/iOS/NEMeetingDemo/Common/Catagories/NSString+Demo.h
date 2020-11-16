//
//  NSString+Demo.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (Demo)

- (NSString *)authValueEncode;
-(NSDictionary<NSString *, NSString *> *)queryParametersFromURLString;
@end

NS_ASSUME_NONNULL_END
