//
//  HttpService.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "HttpService.h"
#import "HttpTaskProtocol.h"
#import <YYModel/YYModel.h>

@implementation HttpService

+ (void)requestLoginAuth:(NSString *)account
                password:(NSString *)password
              completion:(LoginTaskBlock)block {
    
    LoginTask *task = [[LoginTask alloc] init];
    task.mobilePhone = account;
    task.authValue = password;
    task.completion = block;
    [self runTask:task];
}

#pragma mark - Run
+ (void)runTask:(id<HttpTaskProtocol>)task
{
    NSURLRequest *request = [task taskRequest];
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionTask *sessionTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable connectionError) {
        id res = nil;
        NSError *error = connectionError;
        if (connectionError == nil &&
            [response isKindOfClass:[NSHTTPURLResponse class]] &&
            [(NSHTTPURLResponse *)response statusCode] == 200)
        {
            if (data)
            {
                res = [[task responseClass] yy_modelWithJSON:data];
            }
            else
            {
                error = [NSError errorWithDomain:@"demo domain"
                                            code:-1
                                        userInfo:@{@"description" : @"invalid data"}];
                
            }
        }
        
        
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [task onGetResponse:res
                          error:error];
        });
      }];
    [sessionTask resume];
}

@end
