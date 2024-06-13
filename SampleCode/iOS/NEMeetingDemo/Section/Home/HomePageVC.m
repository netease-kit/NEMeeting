// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "HomePageVC.h"
#import <Masonry/Masonry.h>
#import "EnterMeetingVC.h"
#import "HomeItem.h"
#import "HomeItemCell.h"
#import "LoginInfoManager.h"
#import "LoginViewController.h"
#import "MainViewController.h"
#import "MeetingActionVC.h"
#import "NEMeetingLoginViewController.h"
#import "NEMeetingSafeArea.h"
#import "NESubscribeMeetingConfigVC.h"
#import "ScreenShareVC.h"
#import "StartMeetingVC.h"
#import "SubscribeMeetingListVC.h"

@interface HomePageVC () <UICollectionViewDataSource,
                          UICollectionViewDelegate,
                          NEMeetingAuthListener,
                          NEMeetingInviteStatusListener>
@property(nonatomic, strong) UICollectionView *collectionView;
@property(nonatomic, strong) UIView *subscribeListContainer;
@property(nonatomic, strong) UIButton *settingBtn;
@property(nonatomic, strong) UIView *line;
@property(nonatomic, strong) SubscribeMeetingListVC *subscribeListVC;
@property(nonatomic, copy) NSArray<HomeItem *> *items;

@end

@implementation HomePageVC
- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
  [[NEMeetingKit getInstance] removeAuthListener:self];
  [[NEMeetingKit getInstance].getMeetingService removeListener:self];
  [[NSNotificationCenter defaultCenter] removeObserver:self
                                                  name:kNEMeetingEditSubscribeDone
                                                object:nil];
}
- (void)viewDidLoad {
  [super viewDidLoad];
  [self setupUI];
  // Do any additional setup after loading the view.
  [[NEMeetingKit getInstance] addAuthListener:self];
  [[NEMeetingKit getInstance].getMeetingService addListener:self];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(editDone)
                                               name:kNEMeetingEditSubscribeDone
                                             object:nil];
}
- (void)editDone {
  [self.navigationController popToViewController:self animated:YES];
}
- (void)viewDidLayoutSubviews {
  [super viewDidLayoutSubviews];
  self.subscribeListVC.view.frame = self.subscribeListContainer.bounds;
}
- (void)setupUI {
  self.view.backgroundColor = UIColor.whiteColor;
  [self hiddenBackButton];
  [self addChildViewController:self.subscribeListVC];
  [self.subscribeListContainer addSubview:self.subscribeListVC.view];
}

- (void)popToMainVC {
  __block UIViewController *targetVC = nil;
  [self.navigationController.viewControllers
      enumerateObjectsUsingBlock:^(__kindof UIViewController *_Nonnull obj, NSUInteger idx,
                                   BOOL *_Nonnull stop) {
        if ([obj isKindOfClass:[MainViewController class]]) {
          targetVC = obj;
          *stop = YES;
        }
      }];
  if (targetVC) {
    [self.navigationController popToViewController:targetVC animated:YES];
  }
}
- (void)doBeKickedWithInfo:(NSString *)info {
  UIWindow *window = [UIApplication sharedApplication].keyWindow;
  [window makeToast:info duration:2 position:CSToastPositionCenter];
  [self doLogout];
}

- (void)doLogout {
  WEAK_SELF(weakSelf);
  [[NEMeetingKit getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode != ERROR_CODE_SUCCESS) {
      [weakSelf showErrorCode:resultCode msg:resultMsg];
    }
    [[LoginInfoManager shareInstance] cleanLoginInfo];
    [weakSelf popToMainVC];
  }];
}
- (void)setupSubviews {
  [self.view addSubview:self.collectionView];
  [self.view addSubview:self.line];
  [self.view addSubview:self.subscribeListContainer];
  [self.view addSubview:self.settingBtn];
}
- (void)makeConstraints {
  [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
    make.height.mas_equalTo(@126);
    if (@available(iOS 11.0, *)) {
      make.left.equalTo(self.view.mas_safeAreaLayoutGuideLeft);
      make.top.equalTo(self.view.mas_safeAreaLayoutGuideTop);
      make.right.equalTo(self.view.mas_safeAreaLayoutGuideRight);
    } else {
      make.left.equalTo(self.view.mas_left);
      make.right.equalTo(self.view.mas_right);
      make.top.equalTo(self.view.mas_top);
    }
  }];
  [self.line mas_makeConstraints:^(MASConstraintMaker *make) {
    make.height.mas_equalTo(1);
    make.left.equalTo(self.view.mas_left);
    make.right.equalTo(self.view.mas_right);
    make.top.equalTo(self.collectionView.mas_bottom);
  }];
  [self.subscribeListContainer mas_makeConstraints:^(MASConstraintMaker *make) {
    if (@available(iOS 11.0, *)) {
      make.left.equalTo(self.view.mas_safeAreaLayoutGuideLeft);
      make.right.equalTo(self.view.mas_safeAreaLayoutGuideRight);
      make.bottom.equalTo(self.view.mas_safeAreaLayoutGuideBottom);
    } else {
      make.left.equalTo(self.view.mas_left);
      make.right.equalTo(self.view.mas_right);
      make.bottom.equalTo(self.view.mas_bottom);
    }
    make.top.equalTo(self.line.mas_bottom);
  }];
  [self.settingBtn mas_makeConstraints:^(MASConstraintMaker *make) {
    make.size.mas_equalTo(CGSizeMake(60, 60));
    if (@available(iOS 11.0, *)) {
      make.right.equalTo(self.view.mas_safeAreaLayoutGuideRight).offset(-16);
      make.bottom.equalTo(self.view.mas_safeAreaLayoutGuideBottom).offset(-32);
    } else {
      make.right.equalTo(self.view.mas_right).offset(-16);
      make.bottom.equalTo(self.view.mas_bottom).offset(-32);
    }
  }];
}
- (void)viewWillLayoutSubviews {
  [super viewWillLayoutSubviews];
  UICollectionViewFlowLayout *layout =
      (UICollectionViewFlowLayout *)self.collectionView.collectionViewLayout;
  layout.itemSize = CGSizeMake(
      (self.view.bounds.size.width - [NEMeetingSafeArea ne_distance:NEMeetingSafeAreaInsetsLeft] -
       [NEMeetingSafeArea ne_distance:NEMeetingSafeAreaInsetsRight]) /
          4,
      layout.itemSize.height);
  [self.collectionView.collectionViewLayout invalidateLayout];
}
- (void)gotoSettingPage:(UIButton *)btn {
  MeetingActionVC *vc = [[MeetingActionVC alloc] init];
  [self.navigationController pushViewController:vc animated:YES];
}
#pragma mark NEMeetingInviteStatusListener

- (void)onMeetingInviteStatusChanged:(NEMeetingInviteStatus)status meetingId:(NSInteger)meetingId inviteInfo:(NEMeetingInviteInfo *)inviteInfo {
  if (status == NEMeetingInviteStatusCalling) {
    UIAlertController *alert = [UIAlertController
        alertControllerWithTitle:@"收到会议邀请"
                         message:[NSString stringWithFormat:@"%@", inviteInfo.inviterName]
                  preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction
        actionWithTitle:@"拒绝"
                  style:UIAlertActionStyleCancel
                handler:^(UIAlertAction *_Nonnull action) {
                  [[NEMeetingKit getInstance].getMeetingInviteService rejectInvite:meetingId
                                                                   callback:nil];
                }];
    UIAlertAction *acceptAction =
        [UIAlertAction actionWithTitle:@"接听"
                                 style:UIAlertActionStyleDefault
                               handler:^(UIAlertAction *_Nonnull action) {
                                 NEJoinMeetingParams *param = [[NEJoinMeetingParams alloc] init];
                                 param.meetingNum = inviteInfo.meetingNum;
                                 param.displayName = @"123";
                                 [[NEMeetingKit getInstance].getMeetingInviteService
                                     acceptInvite:param
                                             opts:[[NEJoinMeetingOptions alloc] init]
                                         callback:nil];
                               }];
    [alert addAction:cancelAction];
    [alert addAction:acceptAction];
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
    if (!preVC) {
      preVC = keyWindow.rootViewController;
    }
    [preVC presentViewController:alert animated:YES completion:nil];
  }
}

#pragma mark------------------------ MeetingServiceListener ------------------------
- (void)onKickOut {
  [self doBeKickedWithInfo:@"您已在其他设备登录"];
}

- (void)onAuthInfoExpired {
  [self doBeKickedWithInfo:@"登录状态已过期，请重新登录"];
}

- (void)onInjectedMenuItemClick:(NEMenuClickInfo *)clickInfo
                    meetingInfo:(NEMeetingInfo *)meetingInfo
                stateController:(NEMenuStateController)stateController {
  UIAlertController *alert = [UIAlertController
      alertControllerWithTitle:@""
                       message:[NSString stringWithFormat:@"%@-%@", clickInfo, meetingInfo]
                preferredStyle:UIAlertControllerStyleAlert];
  UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消"
                                                         style:UIAlertActionStyleCancel
                                                       handler:^(UIAlertAction *_Nonnull action) {
                                                         stateController(NO, nil);
                                                       }];
  UIAlertAction *ignoreAction = [UIAlertAction actionWithTitle:@"忽略"
                                                         style:UIAlertActionStyleDefault
                                                       handler:^(UIAlertAction *_Nonnull action){
                                                       }];
  UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确认"
                                                     style:UIAlertActionStyleDefault
                                                   handler:^(UIAlertAction *_Nonnull action) {
                                                     stateController(YES, nil);
                                                   }];
  [alert addAction:cancelAction];
  [alert addAction:ignoreAction];
  [alert addAction:okAction];
  UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
  UIViewController *preVC = keyWindow.rootViewController.presentedViewController;
  if (!preVC) {
    preVC = keyWindow.rootViewController;
  }
  [preVC presentViewController:alert animated:YES completion:nil];
}
#pragma mark------------------------ UICollectionViewDataSource ------------------------
- (NSInteger)collectionView:(UICollectionView *)collectionView
     numberOfItemsInSection:(NSInteger)section {
  return self.items.count;
}
- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView
                           cellForItemAtIndexPath:(NSIndexPath *)indexPath {
  HomeItemCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"HomeItemCell"
                                                                 forIndexPath:indexPath];
  [cell configWithItem:self.items[indexPath.row]];
  return cell;
}
#pragma mark------------------------ UICollectionViewDelegate ------------------------
- (void)collectionView:(UICollectionView *)collectionView
    didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
  switch (indexPath.row) {
    case 0: {
      StartMeetingVC *vc = [[StartMeetingVC alloc] init];
      [self.navigationController pushViewController:vc animated:YES];
    } break;
    case 1: {
      EnterMeetingVC *vc = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]
          instantiateViewControllerWithIdentifier:@"EnterMeetingVC"];
      vc.type = EnterMeetingJoin;
      [self.navigationController pushViewController:vc animated:YES];
    } break;
    case 2: {
      NESubscribeMeetingConfigVC *vc = [[NESubscribeMeetingConfigVC alloc] init];
      [self.navigationController pushViewController:vc animated:YES];
    } break;
    default: {
      ScreenShareVC *screenShareVC = [ScreenShareVC new];
      [self.navigationController pushViewController:screenShareVC animated:YES];
    } break;
  }
}

#pragma mark------------------------ Getter ------------------------
- (UICollectionView *)collectionView {
  if (!_collectionView) {
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    layout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
    layout.itemSize = CGSizeMake(UIScreen.mainScreen.bounds.size.width / 4, 124);
    layout.minimumLineSpacing = 0;
    layout.minimumInteritemSpacing = 0;
    layout.sectionInset = UIEdgeInsetsZero;
    _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero
                                         collectionViewLayout:layout];
    _collectionView.dataSource = self;
    _collectionView.delegate = self;
    _collectionView.backgroundColor = UIColor.whiteColor;
    [_collectionView registerClass:HomeItemCell.class forCellWithReuseIdentifier:@"HomeItemCell"];
  }
  return _collectionView;
}
- (UIView *)subscribeListContainer {
  if (!_subscribeListContainer) {
    _subscribeListContainer = [[UIView alloc] initWithFrame:CGRectZero];
    _subscribeListContainer.layer.masksToBounds = YES;
  }
  return _subscribeListContainer;
}
- (UIButton *)settingBtn {
  if (!_settingBtn) {
    _settingBtn = [[UIButton alloc] initWithFrame:CGRectZero];
    _settingBtn.backgroundColor = [UIColor colorWithRed:39 / 255.0
                                                  green:120 / 255.0
                                                   blue:251 / 255.0
                                                  alpha:1];
    [_settingBtn setTitle:@"设置" forState:UIControlStateNormal];
    [_settingBtn setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
    _settingBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    _settingBtn.layer.cornerRadius = 30;
    _settingBtn.layer.masksToBounds = YES;
    [_settingBtn addTarget:self
                    action:@selector(gotoSettingPage:)
          forControlEvents:UIControlEventTouchUpInside];
  }
  return _settingBtn;
}
- (SubscribeMeetingListVC *)subscribeListVC {
  if (!_subscribeListVC) {
    _subscribeListVC = [[SubscribeMeetingListVC alloc] init];
  }
  return _subscribeListVC;
}
- (UIView *)line {
  if (!_line) {
    _line = [[UIView alloc] initWithFrame:CGRectZero];
    _line.backgroundColor = [UIColor colorWithRed:242 / 255.0
                                            green:242 / 255.0
                                             blue:247 / 255.0
                                            alpha:1];
  }
  return _line;
}
- (NSArray<HomeItem *> *)items {
  if (!_items) {
    _items = @[
      [HomeItem itemWithTitle:@"即刻会议" imageName:@"createMeeting"],
      [HomeItem itemWithTitle:@"加入会议" imageName:@"joinMeeting"],
      [HomeItem itemWithTitle:@"预约会议" imageName:@"subscribeMeeting"],
      [HomeItem itemWithTitle:@"共享屏幕" imageName:@"shareScreen"],
    ];
  }
  return _items;
}
@end
