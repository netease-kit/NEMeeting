//
//  SubscribeMeetingListVC.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "SubscribeMeetingListVC.h"
#import "SubMeetingCell.h"
#import "SubDateCell.h"
#import "NESubscribeMeetingDetailVC.h"
#import <Reachability/Reachability.h>

@interface SubscribeMeetingListVC ()<UITableViewDelegate, UITableViewDataSource, NEScheduleMeetingListener>

@property (weak, nonatomic) IBOutlet UITableView *listView;
@property (nonatomic, strong) NSMutableArray *datas;
@property (nonatomic, strong) NSMutableArray <NEMeetingItem *> *items;
@property (nonatomic, strong) Reachability *connect;
@end

@implementation SubscribeMeetingListVC

- (void)dealloc {
    [[NEMeetingKit getInstance].getPreMeetingService removeListener:self];
    [self.connect stopNotifier];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [self setupDatas];
    [[NEMeetingKit getInstance].getPreMeetingService addListener:self];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(netStateChange) name:kReachabilityChangedNotification object:nil];
    [self.connect startNotifier];
}
- (void)netStateChange {
    // 1.检测手机是否能上网络(WiFi\3G\2.5G)
    Reachability *connect = [Reachability reachabilityForInternetConnection];
    // 2.判断网络状态
    if ([connect currentReachabilityStatus] == ReachableViaWiFi ||
        [connect currentReachabilityStatus] == ReachableViaWWAN) {
        [self setupDatas];
    }
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    _listView.frame = self.view.bounds;
}

- (void)setupUI {
    _listView.hidden = YES;
    [_listView registerNib:[UINib nibWithNibName:@"SubMeetingCell" bundle:nil]
    forCellReuseIdentifier:@"SubMeetingCell"];
    [_listView registerNib:[UINib nibWithNibName:@"SubDateCell" bundle:nil]
    forCellReuseIdentifier:@"SubDateCell"];
    [_listView registerClass:[UITableViewCell class]
      forCellReuseIdentifier:@"defaultCell"];
    _listView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

- (void)setupDatas {
    __weak typeof(self) weakSelf = self;
    NSArray *meetingStatus = @[@(NEMeetingItemStatusInit),
                               @(NEMeetingItemStatusStarted),
                               @(NEMeetingItemStatusEnded)];
    [[NEMeetingKit getInstance].getPreMeetingService getMeetingList:meetingStatus
                                                           callback:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NSArray<NEMeetingItem *> * _Nonnull items) {
        if (resultCode == ERROR_CODE_SUCCESS) {
            weakSelf.items = [NSMutableArray arrayWithArray:items];
            [weakSelf groupItems];
        }
    }];
}

- (void)mergeDatas:(NSArray *)datas {
    NSMutableArray *targetItems = [NSMutableArray array];
    
    for (NEMeetingItem *changeItem in datas) {
        NEMeetingItem *item = [self itemWithMeetingUniqueId:changeItem.meetingUniqueId];
        if (item) { //存在则删除原有的
            [_items removeObject:item];
        }
        
        //添加
        if (changeItem.status == NEMeetingItemStatusInit ||
            changeItem.status == NEMeetingItemStatusStarted ||
            changeItem.status == NEMeetingItemStatusEnded) {
            [targetItems addObject:changeItem];
        }
    }
    
    //剩下的再添加进来
    [targetItems addObjectsFromArray:_items];
    
    [_items removeAllObjects];
    [_items addObjectsFromArray:targetItems];
    
    //排序处理
    [self groupItems];
}

- (void)groupItems {
    //排序
    [_items sortUsingComparator:^NSComparisonResult(NEMeetingItem *obj1, NEMeetingItem *obj2) {
        if (obj1.startTime == obj2.startTime) {
            return obj1.createTime > obj2.createTime;
        } else {
            return obj1.startTime > obj2.startTime;
        }
    }];
    
    //分组
    if (!_datas) {
        _datas = [NSMutableArray array];
    } else {
        [_datas removeAllObjects];
    }
    __block NEDateItem *preDateItem = nil;
    __weak typeof(self) weakSelf = self;
    [_items enumerateObjectsUsingBlock:^(NEMeetingItem * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NEDateItem *item = [[NEDateItem alloc] initWithTimestamp:obj.startTime/1000];
        if (preDateItem == nil || preDateItem.day != item.day) {
            [weakSelf.datas addObject:item];
        }
        [weakSelf.datas addObject:obj];
        preDateItem = item;
    }];
    
    //刷新
    self.listView.hidden = (_datas.count == 0);
    [self.listView reloadData];
}

- (NEMeetingItem *)itemWithMeetingUniqueId:(uint64_t)uniqueId {
    __block NEMeetingItem *ret = nil;
    [_items enumerateObjectsUsingBlock:^(NEMeetingItem * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (obj.meetingUniqueId == uniqueId) {
            ret = obj;
            *stop = YES;
        }
    }];
    return ret;
}

#pragma mark - <UITableViewDelegate, UITableViewDataSource>
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _datas.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    id item = _datas[indexPath.row];
    if ([item isKindOfClass:[NEMeetingItem class]]) {
        SubMeetingCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SubMeetingCell"
                                                               forIndexPath:indexPath];
        [cell refreshWithItem:item];
        return cell;
    } else if ([item isKindOfClass:[NEDateItem class]]) {
        SubDateCell * cell = [tableView dequeueReusableCellWithIdentifier:@"SubDateCell"
                                                             forIndexPath:indexPath];
        [cell refreshWithDateItem:item];
        return cell;
    } else {
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"defaultCell"
                                                                forIndexPath:indexPath];
        return cell;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    id item = _datas[indexPath.row];
    if ([item isKindOfClass:[NEMeetingItem class]]) {
        return 66.0;
    } else if ([item isKindOfClass:[NEDateItem class]]) {
        return 72.0;
    } else {
        return 1.0f;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    id item = _datas[indexPath.row];
    if ([item isKindOfClass:[NEMeetingItem class]]) {
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        NESubscribeMeetingDetailVC *vc = [[NESubscribeMeetingDetailVC alloc] initWithItem:item];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

#pragma mark - <NEScheduleMeetingListener>
- (void)onScheduleMeetingStatusChange:(NSArray<NEMeetingItem *> *)changedMeetingItemList
                          incremental:(BOOL)incremental{
    [self setupDatas];
//    if (incremental) { //增量
//        [self mergeDatas:changedMeetingItemList];
//    } else { //全量
//        [self setupDatas];
//    }
}


- (Reachability *)connect {
    if (!_connect) {
        _connect = [Reachability reachabilityForInternetConnection];
    }
    return _connect;
}
@end
