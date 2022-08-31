// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "SystemAuthHelper.h"


#define kAPPName [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleName"]

@implementation SystemAuthHelper

+ (void)requestAuthority {
    [self requestCameraAuthority];
    [self requestAudioAuthority];
}

+(BOOL)checkAuthority:(AVAuthorizationStatus)_status{
    return (_status == AVAuthorizationStatusAuthorized) || (_status == AVAuthorizationStatusNotDetermined);
}

+(void)showAlertController:(AuthorizedFinishBlock)block device:(NSString *)device{
    UIAlertController *alertC = [UIAlertController alertControllerWithTitle:@"没有权限" message:[NSString stringWithFormat:@"请开启‘%@’对 %@ 的使用权限",kAPPName,device] preferredStyle:UIAlertControllerStyleAlert];
    [alertC addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil]];
    [alertC addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
    }]];
    [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:alertC
                                                                                 animated:YES
                                                                               completion:block];
}

#pragma mark - 摄像头权限
+(BOOL)checkCameraAuthority{
    return [self checkAuthority:[AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo]];
}

+(void)cameraAuthorityCheckSuccess:(AuthorizedFinishBlock)success
                              fail:(AuthorizedFinishBlock)fail;{
    if ([self checkCameraAuthority]) {
        if (success) {
            success();
        }
    }else{
        [self showAlertController:fail device:@"相机"];
    }
}

+(void)requestCameraAuthority{
    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!granted) {
                [self showAlertController:nil device:@"相机"];
            }
        });
    }];
}
 
#pragma mark - 麦克风权限
+(BOOL)checkAudioAuthority{
    return [self checkAuthority:[AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio]];
}
+(void)audioAuthorityCheckSuccess:(AuthorizedFinishBlock)success
                             fail:(AuthorizedFinishBlock)fail{
    if ([self checkAudioAuthority]) {
        if (success) {
            success();
        }
    }else{
        [self showAlertController:fail device:@"麦克风"];
    }
}

+(void)requestAudioAuthority{
    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio completionHandler:^(BOOL granted) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!granted) {
                [self showAlertController:nil device:@"麦克风"];
            }
        });
    }];
}

@end
