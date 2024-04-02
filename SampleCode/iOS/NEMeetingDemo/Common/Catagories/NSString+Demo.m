// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NSData+Demo.h"
#import "NSString+Demo.h"

@implementation NSString (Demo)

- (NSString *)authValueEncode {
  NSString *ret = [[NSString stringWithFormat:@"%@@163", self] lowercaseString];
  NSData *data = [ret dataUsingEncoding:NSUTF8StringEncoding];
  return [data md5];
}
- (NSDictionary<NSString *, NSString *> *)queryParametersFromURLString {
  NSURL *url = [NSURL URLWithString:self];
  NSURLComponents *urlComponents = [NSURLComponents componentsWithURL:url
                                              resolvingAgainstBaseURL:NO];
  NSMutableDictionary<NSString *, NSString *> *queryParams =
      [NSMutableDictionary<NSString *, NSString *> new];
  for (NSURLQueryItem *queryItem in [urlComponents queryItems]) {
    if (queryItem.value == nil) {
      continue;
    }
    [queryParams setObject:queryItem.value forKey:queryItem.name];
  }
  return queryParams;
}

@end
