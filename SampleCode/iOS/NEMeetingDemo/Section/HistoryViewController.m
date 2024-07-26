// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "HistoryViewController.h"
#import <NEMeetingKit/NEMeetingKit.h>

@interface HistoryViewController ()

@property(nonatomic, strong) NSArray<NERemoteHistoryMeeting *> *data;

@end

@implementation HistoryViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.

  __weak typeof(self) weakSelf = self;
  [[[NEMeetingKit getInstance] getPreMeetingService]
      getHistoryMeetingList:0
                      limit:20
                   callback:^(NSInteger code, NSString *_Nonnull message,
                              NSArray<NERemoteHistoryMeeting *> *_Nonnull data) {
                     weakSelf.data = data;
                     dispatch_async(dispatch_get_main_queue(), ^{
                       [weakSelf.tableView reloadData];
                     });
                   }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
  return _data.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"UITableViewCell"];
  if (!cell) {
    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                  reuseIdentifier:@"UITableViewCell"];
  }
  cell.textLabel.text = _data[indexPath.row].subject;
  cell.detailTextLabel.text = _data[indexPath.row].meetingNum;
  return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
  [tableView deselectRowAtIndexPath:indexPath animated:NO];
  long meetingId = _data[indexPath.row].meetingId;
  __weak typeof(self) weakSelf = self;
  [[[NEMeetingKit getInstance] getPreMeetingService]
      getHistoryMeetingDetail:meetingId
                     callback:^(NSInteger code, NSString *message,
                                NERemoteHistoryMeetingDetail *data) {
                       /// 测试聊天记录
                       NEChatroomHistoryMessageSearchOption *option =
                           [[NEChatroomHistoryMessageSearchOption alloc] init];
                       option.startTime = 0;
                       option.limit = 20;
                       option.order = NEChatroomMessageSearchOrderDesc;
                       [[[NEMeetingKit getInstance] getPreMeetingService]
                           fetchChatroomHistoryMessageList:meetingId
                                                    option:option
                                                  callback:^(NSInteger code,
                                                             NSString *_Nonnull message,
                                                             NSArray<NEMeetingChatMessage *>
                                                                 *_Nonnull messages) {
                                                    NSLog(@"%@", messages);
                                                  }];

                       /// 测试小应用跳转
                       if (data.pluginInfoList.count > 0) {
                         [[[NEMeetingKit getInstance] getPreMeetingService]
                             loadWebAppView:meetingId
                                       item:data.pluginInfoList.firstObject
                                   callback:^(NSInteger code, NSString *message,
                                              UIViewController *viewController) {
                                     if (code != 0) {
                                       return;
                                     }
                                     dispatch_async(dispatch_get_main_queue(), ^{
                                       [weakSelf.navigationController
                                           pushViewController:viewController
                                                     animated:YES];
                                     });
                                   }];
                       }
                     }];
}

@end
