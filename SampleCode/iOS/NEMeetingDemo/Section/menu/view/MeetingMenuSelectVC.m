//
//  MeetingMenuSelectVC.m
//  NEMeetingDemo
//
//  Created by gyy on 2021/1/14.
//  Copyright © 2021 张根宁. All rights reserved.
//

#import "MeetingMenuSelectVC.h"
#import "MeetingMenuItemCell.h"
#import "SelectedMenuItemEditVC.h"

typedef NS_ENUM(NSInteger,NEMeetingMenuItemType) {
    /// 静音
    NEMeetingMenuItemTypeMute = 0,
    /// 停止视频
    NEMeetingMenuItemTypeCamera,
    /// 屏幕共享
    NEMeetingMenuItemTypeScreenShare,
    /// 视图布局
    NEMeetingMenuItemTypeLayout,
    /// 参会者
    NEMeetingMenuItemTypeParticipant,
    /// 管理参会者
    NEMeetingMenuItemTypeManagerParticipant,
    /// 邀请
    NEMeetingMenuItemTypeInvitation,
    /// 聊天
    NEMeetingMenuItemTypeChat,
    /// 音频管理
    NEMeetingMenuItemTypeAudioManager,
    /// 单选
    NEMeetingMenuItemTypeSingleMenu,
    /// 多选
    NEMeetingMenuItemTypeCheckMenu,
    /// 共享白板
    NEMeetingMenuItemTypeWhiteboard
};


@interface MeetingMenuSelectVC ()<UICollectionViewDelegate,UICollectionViewDataSource>
@property (nonatomic, strong) UICollectionView *selectedCollectionView;
@property (nonatomic, strong) UICollectionView *allItemCollectionView;
@property (nonatomic, strong) NSMutableArray<NEMeetingMenuItem *> *allSeletedItems;
@property (nonatomic, strong) NSArray<NEMeetingMenuItem *> *allItems;
@property (nonatomic, assign) int itemID;


@end
static NSString * cellIdentifier = @"selectedMenuItemCell";
static NSString * allCellIdentifier = @"allSelectedMenuItemCell";

@implementation MeetingMenuSelectVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [self initData];
    
}
#pragma mark - UI
- (void)setupUI {
    self.view.backgroundColor = [UIColor whiteColor];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithTitle:@"完成" style:UIBarButtonItemStyleDone target:self action:@selector(done)];
    [self.navigationItem setRightBarButtonItem:buttonItem];
    [self.view addSubview:self.selectedCollectionView];
    [self.view addSubview:self.allItemCollectionView];
    [self.selectedCollectionView registerClass:[MeetingMenuItemCell class] forCellWithReuseIdentifier:cellIdentifier];
    [self.allItemCollectionView registerClass:[MeetingMenuItemCell class] forCellWithReuseIdentifier:allCellIdentifier];
}

#pragma mark - data
- (void)initData {
    self.itemID = FIRST_INJECTED_MENU_ID;
    if (self.seletedItems) {
        [self.allSeletedItems addObjectsFromArray:self.seletedItems];
    }
    self.allItems = @[
        [NEMenuItems mic],
        [NEMenuItems camera],
        [NEMenuItems screenShare],
        [NEMenuItems switchShowType],
        [NEMenuItems participants],
        [NEMenuItems managerParticipants],
        [NEMenuItems invite],
        [NEMenuItems chat],
        [self addAudioManagerMenuItem],
        [self addSingleStateMenuItem],
        [self addCheckableMenuItem],
        [NEMenuItems whiteboard]
    ];
    [self.selectedCollectionView reloadData];
    [self.allItemCollectionView reloadData];
}

- (NEMeetingMenuItem *)createItemWithType:(NEMeetingMenuItemType)itemType {
    switch (itemType) {
        case NEMeetingMenuItemTypeMute: return [NEMenuItems mic];
        case NEMeetingMenuItemTypeCamera: return [NEMenuItems camera];
        case NEMeetingMenuItemTypeScreenShare: return [NEMenuItems screenShare];
        case NEMeetingMenuItemTypeLayout: return [NEMenuItems switchShowType];
        case NEMeetingMenuItemTypeParticipant: return [NEMenuItems participants];
        case NEMeetingMenuItemTypeManagerParticipant: return [NEMenuItems managerParticipants];
        case NEMeetingMenuItemTypeInvitation: return [NEMenuItems invite];
        case NEMeetingMenuItemTypeChat: return [NEMenuItems chat];
        case NEMeetingMenuItemTypeAudioManager: return [self addAudioManagerMenuItem];
        case NEMeetingMenuItemTypeSingleMenu: return [self addSingleStateMenuItem];
        case NEMeetingMenuItemTypeCheckMenu: return [self addCheckableMenuItem];
        default: return [NEMenuItems whiteboard];
    }
}

- (NEMeetingMenuItem *)addSingleStateMenuItem {
    NESingleStateMenuItem *item = [[NESingleStateMenuItem alloc] init];
    item.itemId = self.itemID ++;
    item.visibility = VISIBLE_ALWAYS;
    
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = @"单选";
    info.icon = @"calendar";
    item.singleStateItem = info;
    return item;
}

- (NEMeetingMenuItem *)addAudioManagerMenuItem {
    NESingleStateMenuItem *item = [[NESingleStateMenuItem alloc] init];
    item.itemId = FIRST_INJECTED_MENU_ID;
    item.visibility = VISIBLE_ALWAYS;
    
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = @"音频管理";
    info.icon = @"calendar";
    item.singleStateItem = info;
    return item;
}

- (NEMeetingMenuItem *)addCheckableMenuItem {
    NECheckableMenuItem *item = [[NECheckableMenuItem alloc] init];
    item.itemId = self.itemID ++;
    item.visibility = VISIBLE_ALWAYS;
    
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"未选中-%@", @(item.itemId)];
    info.icon = @"checkbox_n";
    item.uncheckStateItem = info;
    
    info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"选中-%@", @(item.itemId)];
    info.icon = @"checkbox_s";
    item.checkedStateItem = info;
    return item;
}
- (void)done {
    if (self.delegate && [self.delegate respondsToSelector:@selector(didSelectedItems:)]) {
        [self.delegate didSelectedItems:[self.allSeletedItems copy]];
    }
    [self.navigationController popViewControllerAnimated:YES];
}
#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if ([collectionView isEqual:self.selectedCollectionView]) {
        return self.allSeletedItems.count;
    }
    return self.allItems.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    if ([collectionView isEqual:self.selectedCollectionView]) {
        MeetingMenuItemCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:cellIdentifier forIndexPath:indexPath];
        NEMeetingMenuItem *item = self.allSeletedItems[indexPath.row];
        cell.item = item;
        return cell;
    }
    MeetingMenuItemCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:allCellIdentifier forIndexPath:indexPath];
    NEMeetingMenuItem *item = self.allItems[indexPath.row];
    cell.item = item;
    return cell;
}
#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if ([collectionView isEqual:self.selectedCollectionView]) {
        SelectedMenuItemEditVC *vc = (SelectedMenuItemEditVC *)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SelectedMenuItemEditVC"];
        __weak typeof(self)weakSelf = self;
        vc.MenuItemDeleteCallback = ^{
            [weakSelf.allSeletedItems removeObjectAtIndex:indexPath.row];
            [weakSelf.selectedCollectionView reloadData];
        };
        vc.menuItemSure = ^{
            [weakSelf.selectedCollectionView reloadData];
        };
        vc.menuItem = self.allSeletedItems[indexPath.row];
        [self.navigationController pushViewController:vc animated:YES];
        return;
    }
    NEMeetingMenuItem *item = [self createItemWithType:(NEMeetingMenuItemType)indexPath.row];
    [self.allSeletedItems addObject:item];
    [self.selectedCollectionView reloadData];
//    NEMeetingMenuItem *item = self.allItems[indexPath.row];
//    if (![self.allSeletedItems containsObject:item]) {
//        [self.allSeletedItems addObject:item];
//        [self.selectedCollectionView reloadData];
//    }else {
//        [self.view makeToast:@"已存在该选项"];
//    }
}

#pragma mark - get
- (UICollectionView *)selectedCollectionView {
    if (!_selectedCollectionView) {
        UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
        layout.itemSize = CGSizeMake(160, 60);
        layout.minimumLineSpacing = 10;
        _selectedCollectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(10, 10, [UIScreen mainScreen].bounds.size.width - 20, ([UIScreen mainScreen].bounds.size.height - 20)*0.7) collectionViewLayout:layout];
        _selectedCollectionView.delegate = self;
        _selectedCollectionView.dataSource = self;
        _selectedCollectionView.backgroundColor = [UIColor whiteColor];
    }
    return _selectedCollectionView;
}

- (UICollectionView *)allItemCollectionView {
    if (!_allItemCollectionView) {
        UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
        layout.itemSize = CGSizeMake(160, 60);
        layout.minimumLineSpacing = 10;
        _allItemCollectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(10, ([UIScreen mainScreen].bounds.size.height - 20) *0.7 + 10, [UIScreen mainScreen].bounds.size.width - 20, ([UIScreen mainScreen].bounds.size.height - 20) * 0.3) collectionViewLayout:layout];
        _allItemCollectionView.delegate = self;
        _allItemCollectionView.dataSource = self;
        _allItemCollectionView.backgroundColor = [UIColor whiteColor];
    }
    return _allItemCollectionView;
}
- (NSMutableArray<NEMeetingMenuItem *> *)allSeletedItems {
    if (!_allSeletedItems) {
        _allSeletedItems = [[NSMutableArray alloc] init];
    }
    return _allSeletedItems;
}
@end
