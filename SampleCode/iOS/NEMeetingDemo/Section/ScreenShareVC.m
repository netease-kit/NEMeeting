// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "ScreenShareVC.h"
#import <Masonry/Masonry.h>
#import "UIView+Toast.h"

static NSUInteger maxLength = 6;

@interface ScreenShareVC () <UITextFieldDelegate, NEScreenSharingStatusListener>
@property(nonatomic, strong) UITextField *displayNameTF;
@property(nonatomic, strong) UITextField *sharingCodeTF;
@property(nonatomic, assign) BOOL isOpenAudioShare;
@property(nonatomic, strong) UIButton *startBtn;
@property(nonatomic, strong) UIButton *stopBtn;
@end

@implementation ScreenShareVC
- (void)dealloc {
  [NEMeetingKit.getInstance.getScreenSharingService removeScreenSharingStatusListener:self];
}
- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  self.view.backgroundColor = UIColor.whiteColor;
  [NEMeetingKit.getInstance.getScreenSharingService addScreenSharingStatusListener:self];
  [self updateOperationBtn:false];
}
- (void)setupSubviews {
  UILabel *nameLabel = [self createLabel:@"昵称"];
  [self.view addSubview:nameLabel];
  [self.view addSubview:self.displayNameTF];
  UILabel *codeLabel = [self createLabel:@"共享码"];
  [self.view addSubview:codeLabel];
  [self.view addSubview:self.sharingCodeTF];
  UILabel *switchLabel = [self createLabel:@"是否音频共享"];
  [self.view addSubview:switchLabel];
  UISwitch *control = [self createSwitch];
  [self.view addSubview:control];

  [self.view addSubview:self.startBtn];
  [self.view addSubview:self.stopBtn];

  [nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.equalTo(self.view.mas_left).offset(20);
    make.height.mas_equalTo(30);
    if (@available(iOS 11.0, *)) {
      make.top.equalTo(self.view.mas_safeAreaLayoutGuideTop).offset(20);
    } else {
      make.top.equalTo(self.view.mas_top).offset(20);
    }
  }];
  [self.displayNameTF mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.equalTo(nameLabel.mas_left);
    make.height.mas_equalTo(30);
    make.width.mas_equalTo(200);
    make.top.equalTo(nameLabel.mas_bottom).offset(10);
  }];
  [codeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.equalTo(nameLabel.mas_left);
    make.height.mas_equalTo(30);
    make.top.equalTo(self.displayNameTF.mas_bottom).offset(20);
  }];
  [self.sharingCodeTF mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.equalTo(self.displayNameTF.mas_left);
    make.height.mas_equalTo(30);
    make.width.mas_equalTo(200);
    make.top.equalTo(codeLabel.mas_bottom).offset(10);
  }];
  [switchLabel mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.equalTo(nameLabel.mas_left);
    make.height.mas_equalTo(30);
    make.top.equalTo(self.sharingCodeTF.mas_bottom).offset(20);
  }];
  [control mas_makeConstraints:^(MASConstraintMaker *make) {
    make.size.mas_equalTo(CGSizeMake(50, 30));
    make.right.equalTo(self.view.mas_right).offset(-20);
    make.centerY.equalTo(switchLabel.mas_centerY);
  }];

  [self.startBtn mas_makeConstraints:^(MASConstraintMaker *make) {
    make.size.mas_equalTo(CGSizeMake(100, 40));
    make.top.equalTo(switchLabel.mas_bottom).offset(100);
    make.centerX.equalTo(self.view.mas_centerX).offset(-100);
  }];
  [self.stopBtn mas_makeConstraints:^(MASConstraintMaker *make) {
    make.size.mas_equalTo(CGSizeMake(100, 40));
    make.top.equalTo(self.startBtn.mas_top);
    make.centerX.equalTo(self.view.mas_centerX).offset(100);
  }];
}

- (void)switchChanged:(UISwitch *)sender {
  self.isOpenAudioShare = sender.on;
}
- (void)startScreenShare:(UIButton *)btn {
  [self.view endEditing:YES];
  NSString *displayName = self.displayNameTF.text;
  NSString *sharingCode = self.sharingCodeTF.text;
  if (!displayName.length) {
    [self.view makeToast:@"昵称不能为空"];
    return;
  }
  if (!sharingCode.length) {
    [self.view makeToast:@"共享码不能为空"];
    return;
  }
  btn.backgroundColor = UIColor.grayColor;
  btn.enabled = false;
  NEScreenSharingOptions *opt = [NEScreenSharingOptions new];
  opt.enableAudioShare = self.isOpenAudioShare;
  NEScreenSharingParams *params = [NEScreenSharingParams new];
  params.displayName = displayName;
  params.sharingCode = sharingCode;
  [NEMeetingKit.getInstance.getScreenSharingService
      startScreenShare:params
                  opts:opt
              callback:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
                if (resultCode != ERROR_CODE_SUCCESS) {
                  [self.view makeToast:[self errorMsg:resultCode msg:resultMsg]];
                  btn.backgroundColor = UIColorFromRGB(0x2864FE);
                  btn.enabled = true;
                }
                NSLog(@"开始共享 Code: %ld. Message: %@", resultCode, resultMsg);
              }];
}
- (void)stopScreenShare {
  [NEMeetingKit.getInstance.getScreenSharingService
      stopScreenShare:^(NSInteger resultCode, NSString *resultMsg, id resultData) {
        NSLog(@"停止共享 Code: %ld. Message: %@", resultCode, resultMsg);
        if (resultCode != ERROR_CODE_SUCCESS) {
          [self.view makeToast:resultMsg];
        }
      }];
}
- (UISwitch *)createSwitch {
  UISwitch *control = [[UISwitch alloc] initWithFrame:CGRectZero];
  control.on = false;
  [control addTarget:self
                action:@selector(switchChanged:)
      forControlEvents:UIControlEventValueChanged];
  return control;
}
- (UILabel *)createLabel:(NSString *)text {
  UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
  label.text = text;
  label.font = [UIFont systemFontOfSize:18];
  return label;
}
- (UITextField *)createTextField:(NSString *)placeholder {
  UITextField *tf = [[UITextField alloc] initWithFrame:CGRectZero];
  tf.placeholder = placeholder;
  tf.borderStyle = UITextBorderStyleRoundedRect;
  return tf;
}
- (UIButton *)createBtn:(NSString *)title target:(id)target action:(SEL)action {
  UIButton *btn = [[UIButton alloc] initWithFrame:CGRectZero];
  [btn setTitle:title forState:UIControlStateNormal];
  [btn setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
  btn.backgroundColor = UIColorFromRGB(0x2864FE);
  [btn addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
  return btn;
}
#pragma mark------------------------ UITextFieldDelegate ------------------------
- (BOOL)textField:(UITextField *)textField
    shouldChangeCharactersInRange:(NSRange)range
                replacementString:(NSString *)string {
  NSCharacterSet *allowedCharacters = [NSCharacterSet alphanumericCharacterSet];
  return ([string rangeOfCharacterFromSet:allowedCharacters.invertedSet].location == NSNotFound) &&
         (textField.text.length + string.length - range.length <= maxLength);
}

- (void)textfieldChanged:(UITextField *)textField {
  textField.text = [textField.text uppercaseString];
}
- (void)updateOperationBtn:(BOOL)isSharing {
  self.stopBtn.backgroundColor = isSharing ? UIColorFromRGB(0x2864FE) : UIColor.grayColor;
  self.stopBtn.enabled = isSharing;
  self.startBtn.backgroundColor = !isSharing ? UIColorFromRGB(0x2864FE) : UIColor.grayColor;
  self.startBtn.enabled = !isSharing;
}
#pragma mark------------------------ NEScreenSharingStatusListener ------------------------
- (void)onScreenSharingStatusChanged:(NEScreenSharingEvent *)event {
  NSLog(@" Event.status = %@", [self stringWithScreenShareStatus:event.status]);
  [self updateOperationBtn:(event.status == NEScreenSharingStatusStarted ||
                            event.status == NEScreenSharingStatusWaiting)];
}
- (NSString *)stringWithScreenShareStatus:(NEScreenSharingStatus)status {
  switch (status) {
    case NEScreenSharingStatusIdle:
      return @"共享 -> 未在会议中";
    case NEScreenSharingStatusWaiting:
      return @"共享 -> 等待中";
    case NEScreenSharingStatusStarted:
      return @"共享 -> 共享中";
    case NEScreenSharingStatusEnded:
      return @"共享 -> 结束";
    default:
      return @"共享 -> 未知";
  }
}

- (NSString *)errorMsg:(NSInteger)code msg:(NSString *)msg {
  switch (code) {
    case 1612:
      return @"共享码错误";
    case 1620:
      return @"共享码使用中";
    default:
      break;
  }
  return [NSString stringWithFormat:@"Code: %ld Msg: %@", code, msg];
}
#pragma mark------------------------ Getter ------------------------
- (UITextField *)displayNameTF {
  if (!_displayNameTF) {
    _displayNameTF = [self createTextField:@"请输入昵称"];
  }
  return _displayNameTF;
}
- (UITextField *)sharingCodeTF {
  if (!_sharingCodeTF) {
    _sharingCodeTF = [self createTextField:@"请输入共享码"];
    _sharingCodeTF.keyboardType = UIKeyboardTypeASCIICapable;
    _sharingCodeTF.delegate = self;
    [_sharingCodeTF addTarget:self
                       action:@selector(textfieldChanged:)
             forControlEvents:UIControlEventEditingChanged];
  }
  return _sharingCodeTF;
}
- (UIButton *)startBtn {
  if (!_startBtn) {
    _startBtn = [self createBtn:@"开始共享" target:self action:@selector(startScreenShare:)];
  }
  return _startBtn;
}
- (UIButton *)stopBtn {
  if (!_stopBtn) {
    _stopBtn = [self createBtn:@"停止共享" target:self action:@selector(stopScreenShare)];
  }
  return _stopBtn;
}
@end
