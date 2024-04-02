// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <CommonCrypto/CommonDigest.h>
#import "NSData+Demo.h"

@implementation NSData (Demo)

- (NSString *)md5 {
  unsigned char r[CC_MD5_DIGEST_LENGTH];
  CC_MD5([self bytes], (CC_LONG)[self length], r);
  return [NSString
      stringWithFormat:@"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x", r[0],
                       r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10], r[11], r[12],
                       r[13], r[14], r[15]];
}

@end
