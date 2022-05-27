//
//  AppDelegate+MeetingExtension.m
//  NEMeetingDevDemo
//
//  Created by Topredator on 2022/4/3.
//

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
@end
