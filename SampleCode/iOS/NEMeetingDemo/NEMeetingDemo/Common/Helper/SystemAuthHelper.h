//
//  SystemAuthHelper.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^AuthorizedFinishBlock)(void);

@interface SystemAuthHelper : NSObject

+ (void)requestAuthority;

@end

NS_ASSUME_NONNULL_END
