//
//  NSString+Demo.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NSString+Demo.h"
#import "NSData+Demo.h"

@implementation NSString (Demo)

- (NSString *)authValueEncode {
    NSString *ret = [[NSString stringWithFormat:@"%@@163", self] lowercaseString];
    NSData *data = [ret dataUsingEncoding:NSUTF8StringEncoding];
    return [data md5];
}

@end
