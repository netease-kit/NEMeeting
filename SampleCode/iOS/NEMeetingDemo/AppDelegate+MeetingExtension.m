// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "AppDelegate+MeetingExtension.h"

@implementation AppDelegate (MeetingExtension)
- (void)meeting_addNotification {
    [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(resetWindow) name:kNEMeetingResetWindow object:nil];
}
- (void)resetWindow {
    // 需要重新初始化
    [self doSetupMeetingSdk];
    UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    UIViewController *vc = [storyBoard instantiateInitialViewController];
    UIApplication.sharedApplication.keyWindow.rootViewController = vc;
    [UIApplication.sharedApplication.keyWindow makeKeyAndVisible];
}


- (void)meeting_BeatyResource {
    NSString *sourcePath = [NSBundle.mainBundle pathForResource:@"Beaty" ofType:@"bundle"];
    NSArray *subPaths = [NSFileManager.defaultManager subpathsAtPath:[NSBundle.mainBundle pathForResource:@"Beaty" ofType:@"bundle"]];
    for (NSString *subPath in subPaths) {
        NSString *path = [sourcePath stringByAppendingPathComponent:subPath];
        NSString *toPath = [[self documentPath] stringByAppendingPathComponent:subPath];
        if ([self isExistFile:toPath]) {
            BOOL isDelete = [NSFileManager.defaultManager removeItemAtPath:toPath error:nil];
            NSLog(@"美颜资源删除%@", isDelete ? @"成功" : @"失败");
        }
        if ([self isExistFile:path]) {
            BOOL isSuccess = [[NSFileManager defaultManager] copyItemAtPath:path toPath:toPath error:nil];
            NSLog(@"美颜资源copy%@", isSuccess ? @"成功" : @"失败");
        }
    }
}

- (NSString *)documentPath {
    return  [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
}

- (BOOL)isExistFile:(NSString *)path {
    return [NSFileManager.defaultManager fileExistsAtPath:path];
}
@end
