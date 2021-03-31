//
//  IMLoginVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "IMLoginVC.h"
#import "LoginInfoManager.h"
#import "Config.h"
#import <NIMSDK/NIMSDK.h>
#import <CommonCrypto/CommonDigest.h>

@interface IMLoginVC ()

@property (weak, nonatomic) IBOutlet UITextField *imAccidInput;
@property (weak, nonatomic) IBOutlet UITextField *imTokenInput;
@property (weak, nonatomic) IBOutlet UILabel *imStateLab;
@property (weak, nonatomic) IBOutlet UISwitch *imMulEnableSwitcher;

@end

@interface NSString (IMLogin)
- (NSString *)MD5String;
@end

@implementation IMLoginVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupIMSDK];
    [self setupUI];
}

- (void)setupIMSDK {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NIMSDK sharedSDK] registerWithAppID:kIMAppKey cerName:nil];
    });
}

- (void)setupUI {
    self.title = @"IM登录";
    _imMulEnableSwitcher.on = [LoginInfoManager shareInstance].reuseNIM;
    [self updateIMState];
}

- (void)updateIMState {
    NSString *currentAccount = [NIMSDK sharedSDK].loginManager.currentAccount;
    NSString *stateStr = [NIMSDK sharedSDK].loginManager.isLogined ? @"LOGINED":@"UNLOGINED";
    _imStateLab.text = [NSString stringWithFormat:@"当前IM状态：%@#%@", currentAccount, stateStr];
}

- (IBAction)loginAction:(id)sender {
    NSString *accid = _imAccidInput.text;
    NSString *token = _imTokenInput.text;
    __weak typeof(self) weakSelf = self;
    [[NIMSDK sharedSDK].loginManager login:accid token:token completion:^(NSError * _Nullable error) {
        if (error) {
            NSString *msg = [NSString stringWithFormat:@"IM登录失败：%@", error];
            [weakSelf.view makeToast:msg duration:2 position:CSToastPositionCenter];
        }
        [weakSelf updateIMState];
    }];
}

- (IBAction)logoutAction:(id)sender {
    __weak typeof(self) weakSelf = self;
    [[NIMSDK sharedSDK].loginManager logout:^(NSError * _Nullable error) {
        if (error) {
            NSString *msg = [NSString stringWithFormat:@"IM注销失败：%@", error];
            [weakSelf.view makeToast:msg duration:2 position:CSToastPositionCenter];
        }
        [weakSelf updateIMState];
    }];
}

- (IBAction)imMulEnableAction:(UISwitch *)sender {
    [LoginInfoManager shareInstance].reuseNIM = sender.on;
}

@end

@implementation NSString (IMLogin)

- (NSString *)MD5String {
    const char *cstr = [self UTF8String];
    unsigned char result[16];
    CC_MD5(cstr, (CC_LONG)strlen(cstr), result);
    return [NSString stringWithFormat:
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ];
}

@end
