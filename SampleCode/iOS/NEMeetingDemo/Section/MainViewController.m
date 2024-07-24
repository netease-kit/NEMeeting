// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MainViewController.h"
#import "AppSettingsVC.h"
#import "CustomEncryptObserver.h"
#import "CustomViewController.h"
#import "HomePageVC.h"
#import "IMLoginVC.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "MeetingSettingVC.h"
#import "TimerButton.h"
@interface MainViewController () <MeetingServiceListener, NEGlobalEventListener>
@property(nonatomic, strong) UIButton *mulIMBtn;
@property(nonatomic, strong) UIViewController *preVC;
@property(nonatomic, strong) UIAlertController *alert;
@property(nonatomic, strong) CustomEncryptObserver *encryptObserver;
@end

@implementation MainViewController

- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
  [[NEMeetingKit getInstance] removeGlobalEventListener:self];
}

- (void)viewDidLoad {
  [super viewDidLoad];
  [self setupUI];
  [[NEMeetingKit getInstance] addGlobalEventListener:self];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onMeetingInitAction:)
                                               name:kNEMeetingInitCompletionNotication
                                             object:nil];
}

- (void)viewDidLayoutSubviews {
  [super viewDidLayoutSubviews];
}

- (void)onMeetingInitAction:(NSNotification *)note {
  [[NEMeetingKit getInstance].getMeetingService addListener:self];
  [self autoLogin];
}

- (void)autoLogin {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit getInstance]
      tryAutoLogin:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        [SVProgressHUD dismiss];
        NSLog(@"resultMsg:%@", resultMsg);
        if (resultCode != ERROR_CODE_SUCCESS) {
          [weakSelf showErrorCode:resultCode msg:resultMsg];
        } else {
          HomePageVC *vc = [[HomePageVC alloc] init];
          [weakSelf.navigationController pushViewController:vc animated:YES];
        }
      }];
}

- (void)onEnterMulAction:(UIButton *)sender {
  IMLoginVC *vc = [[IMLoginVC alloc] init];
  [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onAppSettings:(id)sender {
  AppSettingsVC *vc = [[AppSettingsVC alloc] init];
  [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)onMeetingSettings:(id)sender {
  MeetingSettingVC *vc = [[MeetingSettingVC alloc] init];
  [self.navigationController pushViewController:vc animated:YES];
}

- (CustomEncryptObserver *)encryptObserver {
  if (!_encryptObserver) {
    _encryptObserver = [[CustomEncryptObserver alloc] init];
  }
  return _encryptObserver;
}
#pragma mark------------------------ NEGlobalEventListener ------------------------
- (void)beforeRtcEngineReleaseWithRoomUuid:(NSString *)roomUuid
                                rtcWrapper:(NERtcWrapper *)rtcWrapper {
  NSLog(@"Rtc销毁前操作. RoomUuid: %@", roomUuid);
}
- (void)beforeRtcEngineInitializeWithRoomUuid:(NSString *)roomUuid
                                   rtcWrapper:(NERtcWrapper *)rtcWrapper {
  NSLog(@"Rtc初始化之前操作");
}
- (void)afterRtcEngineInitializeWithRoomUuid:(NSString *)roomUuid
                                  rtcWrapper:(NERtcWrapper *)rtcWrapper {
  NSLog(@"Rtc初始化之后操作");
  BOOL videoEncrypt = [[NSUserDefaults standardUserDefaults] boolForKey:@"videoEncrypt"];
  BOOL audioEncrypt = [[NSUserDefaults standardUserDefaults] boolForKey:@"audioEncrypt"];
  if (videoEncrypt || audioEncrypt) {
    NERtcEncryptionConfig *config = [[NERtcEncryptionConfig alloc] init];
    config.mode = NERtcEncryptionModeCustom;
    config.observer = self.encryptObserver;
    [NERtcEngine.sharedEngine enableEncryption:true config:config];
  }
}
- (void)setupUI {
#ifndef ONLINE
  UILabel *env_lab = [[UILabel alloc] init];
  env_lab.text = @"TEST";
  env_lab.font = [UIFont systemFontOfSize:13.0];
  env_lab.textColor = [UIColor redColor];
  [env_lab sizeToFit];
  UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
  env_lab.frame = CGRectMake(keyWindow.frame.size.width - env_lab.frame.size.width,
                             keyWindow.frame.size.height - env_lab.frame.size.height,
                             env_lab.frame.size.width, env_lab.frame.size.height);
  [[UIApplication sharedApplication].keyWindow addSubview:env_lab];
#endif
  UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:self.mulIMBtn];
  self.navigationItem.rightBarButtonItem = item;
}

- (UIButton *)mulIMBtn {
  if (!_mulIMBtn) {
    _mulIMBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_mulIMBtn setTitle:@"IM复用" forState:UIControlStateNormal];
    _mulIMBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
    _mulIMBtn.frame = CGRectMake(0, 0, 60, 40);
    [_mulIMBtn addTarget:self
                  action:@selector(onEnterMulAction:)
        forControlEvents:UIControlEventTouchUpInside];
  }
  return _mulIMBtn;
}

#pragma mark - MeetingServiceListener
- (void)onInjectedMenuItemClick:(NEMeetingMenuItem *)menuItem
                    meetingInfo:(NEMeetingInfo *)meetingInfo {
  UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
  if (menuItem.itemId == 101) {  // 会议号
    [[NEMeetingKit getInstance].getMeetingService
        getCurrentMeetingInfo:^(NSInteger resultCode, NSString *_Nonnull resultMsg,
                                NEMeetingInfo *_Nonnull info) {
          if (resultCode != ERROR_CODE_SUCCESS) {
            NSString *msg = [NSString
                stringWithFormat:@"查询会议失败.code:%@, msg:%@", @(resultCode), resultMsg];
            [keyWindow makeToast:msg duration:2 position:CSToastPositionCenter];
          } else {
            [keyWindow makeToast:[NSString stringWithFormat:@"会议状态:%@", info]
                        duration:2
                        position:CSToastPositionCenter];
          }
        }];
  } else {
    CustomViewController *vc = (CustomViewController *)[[UIStoryboard storyboardWithName:@"Main"
                                                                                  bundle:nil]
        instantiateViewControllerWithIdentifier:@"CustomViewController"];
    UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
    if (!preVC) {
      preVC = keyWindow.rootViewController;
    }
    vc.msg = [NSString stringWithFormat:@"Item:%@\n会议状态:%@", menuItem, meetingInfo];
    vc.modalPresentationStyle = UIModalPresentationFullScreen;
    [preVC presentViewController:vc animated:YES completion:nil];
  }
}

- (void)onInjectedMenuItemClick:(NEMenuClickInfo *)clickInfo
                    meetingInfo:(NEMeetingInfo *)meetingInfo
                stateController:(NEMenuStateController)stateController {
  if (clickInfo.itemId == 100) {
    [self showEditMenuItemDialog:100 isCheckable:false];
  } else if (clickInfo.itemId == 101) {
    [self showEditMenuItemDialog:101 isCheckable:true];
  } else if (clickInfo.itemId == 102) {
    [NEMeetingKit.getInstance.getAccountService
        getAccountInfo:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
          NEAccountInfo *accountInfo = (NEAccountInfo *)resultData;

          NSString *infoString = [NSString
              stringWithFormat:@"会议号: %@\n短会议号: %@\n账号名称: %@\n账号ID: %@\n",
                               accountInfo.privateMeetingNum, accountInfo.privateShortMeetingNum,
                               accountInfo.nickname, accountInfo.userUuid];
          self->_alert = [UIAlertController alertControllerWithTitle:@""
                                                             message:infoString
                                                      preferredStyle:UIAlertControllerStyleAlert];

          [self->_alert addAction:[UIAlertAction actionWithTitle:@"确定"
                                                           style:UIAlertActionStyleDefault
                                                         handler:nil]];
          [self showAlertDialog:self->_alert];
        }];
  } else if (clickInfo.itemId == 111) {
    [NEMeetingKit.getInstance.getMeetingService
        minimizeCurrentMeeting:^(NSInteger resultCode, NSString *resultMsg, id resultData){
        }];
  } else {
    _alert = [UIAlertController
        alertControllerWithTitle:@""
                         message:[NSString stringWithFormat:@"%@-%@", clickInfo, meetingInfo]
                  preferredStyle:UIAlertControllerStyleAlert];
    [_alert addAction:[UIAlertAction actionWithTitle:@"取消"
                                               style:UIAlertActionStyleCancel
                                             handler:^(UIAlertAction *_Nonnull action) {
                                               stateController(NO, nil);
                                             }]];
    [_alert addAction:[UIAlertAction actionWithTitle:@"忽略"
                                               style:UIAlertActionStyleDefault
                                             handler:^(UIAlertAction *_Nonnull action){
                                             }]];
    [_alert addAction:[UIAlertAction actionWithTitle:@"确定"
                                               style:UIAlertActionStyleDefault
                                             handler:^(UIAlertAction *_Nonnull action) {
                                               stateController(YES, nil);
                                             }]];
    [self showAlertDialog:_alert];
  }
}

- (void)showAlertDialog:(UIAlertController *)alert {
  UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
  UIViewController *presentingVC = keyWindow.rootViewController;
  while (presentingVC.presentedViewController) {
    presentingVC = presentingVC.presentedViewController;
  }
  [presentingVC presentViewController:alert animated:YES completion:nil];
}

- (void)showEditMenuItemDialog:(int)itemId isCheckable:(BOOL)isCheckable {
  UIAlertController *_alert =
      [UIAlertController alertControllerWithTitle:@"Title"
                                          message:@"动态修改菜单项"
                                   preferredStyle:UIAlertControllerStyleAlert];

  [_alert addTextFieldWithConfigurationHandler:^(UITextField *_Nonnull textField) {
    textField.placeholder = @"请输入要更新的菜单文本";
  }];

  if (isCheckable) {
    [_alert addTextFieldWithConfigurationHandler:^(UITextField *_Nonnull textField) {
      textField.placeholder = @"是否选择,输入true or false";
    }];
  }

  [_alert addAction:[UIAlertAction actionWithTitle:@"取消"
                                             style:UIAlertActionStyleCancel
                                           handler:nil]];
  [_alert addAction:[UIAlertAction
                        actionWithTitle:@"确定"
                                  style:UIAlertActionStyleDefault
                                handler:^(UIAlertAction *_Nonnull action) {
                                  UITextField *textField = _alert.textFields.firstObject;
                                  BOOL checked = false;
                                  if (_alert.textFields.count > 1) {
                                    UITextField *checkedField = _alert.textFields[1];
                                    checked = [checkedField.text isEqualToString:@"true"];
                                  }

                                  NSString *inputText = textField.text;
                                  NEMeetingMenuItem *item = [self generateMenuItem:itemId
                                                                              text:inputText
                                                                           checked:checked
                                                                       isCheckable:isCheckable];
                                  [NEMeetingKit.getInstance.getMeetingService
                                      updateInjectedMenuItem:item
                                                    callback:^(NSInteger resultCode,
                                                               NSString *resultMsg, id resultData) {
                                                      NSLog(@"resultCode:%@ resultMsg:%@",
                                                            @(resultCode), resultMsg);
                                                    }];
                                }]];
  [self showAlertDialog:_alert];
}

- (NEMeetingMenuItem *)generateMenuItem:(int)itemId
                                   text:(NSString *)text
                                checked:(BOOL)checked
                            isCheckable:(BOOL)isCheckable {
  if (isCheckable) {
    NECheckableMenuItem *item = [[NECheckableMenuItem alloc] init];
    item.itemId = itemId;
    item.visibility = VISIBLE_ALWAYS;
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"%@未-%@", text, @(item.itemId)];
    info.icon = @"checkbox_n";
    item.uncheckStateItem = info;

    info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"%@-%@", text, @(item.itemId)];
    info.icon = @"checkbox_s";
    item.checkedStateItem = info;
    item.checked = checked;
    return item;
  } else {
    NESingleStateMenuItem *item = [[NESingleStateMenuItem alloc] init];
    item.itemId = itemId;
    item.visibility = VISIBLE_ALWAYS;

    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"%@", text];
    info.icon = @"calendar";
    item.singleStateItem = info;
    return item;
  }
}

- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
  if (event.status == MEETING_STATUS_DISCONNECTING && _preVC != nil) {
    [_preVC dismissViewControllerAnimated:YES completion:nil];
  }
  if (event.status == MEETING_STATUS_DISCONNECTING) {
    NSString *toastString =
        [NSString stringWithFormat:@"onMeetingDisconnected: %@",
                                   [self stringifyDisconnectedReason:event.arg]];
    [[UIApplication sharedApplication].keyWindow makeToast:toastString
                                                  duration:2
                                                  position:CSToastPositionCenter];
  }
}

- (NSString *)stringifyDisconnectedReason:(NSInteger)disconnectCode {
  switch (disconnectCode) {
    case MEETING_DISCONNECTING_BY_HOST:
      return @"remove_by_host";
    case MEETING_DISCONNECTING_BY_NORMAL:
      return @"leave_by_self";
    case MEETING_DISCONNECTING_CLOSED_BY_HOST:
      return @"close_by_host";
    case MEETING_DISCONNECTING_CLOSED_BY_SELF_AS_HOST:
      return @"close_by_self";
    case MEETING_DISCONNECTING_LOGIN_ON_OTHER_DEVICE:
      return @"login_on_other_device";
    case MEETING_DISCONNECTING_AUTH_INFO_EXPIRED:
      return @"auth_inf_expired";
    case MEETING_DISCONNECTING_NOT_EXIST:
      return @"not_exist";
    case MEETING_DISCONNECTING_SYNC_DATA_ERROR:
      return @"sync_data_error";
    case MEETING_DISCONNECTING_RTC_INIT_ERROR:
      return @"rtc_init_error";
    case MEETING_DISCONNECTING_JOIN_CHANNEL_ERROR:
      return @"join_channel_error";
    case MEETING_DISCONNECTING_JOIN_TIMEOUT:
      return @"join_timeout";
    case MEETING_DISCONNECTING_END_OF_LIFE:
      return @"meeting_end_of_life";
    default:
      return @"unkown";
  }
}

@end
