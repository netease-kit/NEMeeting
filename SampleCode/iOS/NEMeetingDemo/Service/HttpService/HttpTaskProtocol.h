//
//  HttpTaskProtocol.h
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol HttpTaskProtocol <NSObject>

- (NSURLRequest *)taskRequest;

- (Class)responseClass;

- (void)onGetResponse:(id)response
                error:(NSError *)error;

@end

NS_ASSUME_NONNULL_END
