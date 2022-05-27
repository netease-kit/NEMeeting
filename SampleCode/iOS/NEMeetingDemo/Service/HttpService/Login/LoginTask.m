//
//  LoginTask.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "LoginTask.h"
#import "NSString+Demo.h"
#import "Config.h"

@implementation LoginTask

- (NSURLRequest *)taskRequest {
    NSString *urlString = [ServerConfig.current.appServerUrl stringByAppendingString:@"/ne-meeting-account/loginByUsernamePassword"];
    NSURL *url = [NSURL URLWithString:urlString];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url
                                                                cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                            timeoutInterval:30];
    [request setHTTPMethod:@"Post"];
    [request addValue:@"application/json;charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    //header
    [request addValue:@"2" forHTTPHeaderField:@"clientType"];
    [request addValue:@"1.0" forHTTPHeaderField:@"sdkVersion"];
    [request addValue:kAppVersionName forHTTPHeaderField:@"appVersionName"];
    [request addValue:kAppVersionCode forHTTPHeaderField:@"appVersionCode"];
    
    //params
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    params[@"loginType"] = @(1);
    params[@"username"] = _mobilePhone ?: @"";
    params[@"password"] = [_authValue authValueEncode] ?: @"";
    request.HTTPBody = [NSJSONSerialization dataWithJSONObject:params options:NSJSONWritingPrettyPrinted error:nil];
    return request;
}

- (Class)responseClass {
    return LoginResponse.class;
}

- (void)onGetResponse:(id)response
                error:(NSError *)error {
    
    if (!error && [response isKindOfClass:[LoginResponse class]]) {
        LoginResponse *res = (LoginResponse *)response;
        if (res.code != 200) {
            error = [NSError errorWithDomain:@"demo.http.response"
                                        code:res.code
                                    userInfo:@{NSLocalizedDescriptionKey : res.msg ?: @""}];
        }
    }

    if (_completion) {
        _completion(error, response);
    }
}

@end
