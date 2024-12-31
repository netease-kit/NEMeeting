// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingMenuSelectVC.h"
#import "MeetingMenuItemCell.h"
#import "SelectedMenuItemEditVC.h"

@interface MeetingMenuSelectVC () <UICollectionViewDelegate, UICollectionViewDataSource>
@property(nonatomic, strong) UICollectionView *selectedCollectionView;
@property(nonatomic, strong) UICollectionView *allItemCollectionView;
@property(nonatomic, strong) NSMutableArray<NEMeetingMenuItem *> *allSeletedItems;
@property(nonatomic, strong) NSMutableArray<NEMeetingMenuItem *> *allItems;

@end
static NSString *cellIdentifier = @"selectedMenuItemCell";
static NSString *allCellIdentifier = @"allSelectedMenuItemCell";

@implementation MeetingMenuSelectVC

- (void)viewDidLoad {
  [super viewDidLoad];
  [self setupUI];
  [self initData];
}
#pragma mark - UI
- (void)setupUI {
  self.view.backgroundColor = [UIColor whiteColor];
  UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithTitle:@"完成"
                                                                 style:UIBarButtonItemStyleDone
                                                                target:self
                                                                action:@selector(done)];
  [self.navigationItem setRightBarButtonItem:buttonItem];
  [self.view addSubview:self.selectedCollectionView];
  [self.view addSubview:self.allItemCollectionView];
  [self.selectedCollectionView registerClass:[MeetingMenuItemCell class]
                  forCellWithReuseIdentifier:cellIdentifier];
  [self.allItemCollectionView registerClass:[MeetingMenuItemCell class]
                 forCellWithReuseIdentifier:allCellIdentifier];
}

#pragma mark - data
- (void)initData {
  if (self.seletedItems) {
    [self.allSeletedItems addObjectsFromArray:self.seletedItems];
  }
  self.allItems = [NSMutableArray array];
  if (_currentType == 3) {
    [self.allItems addObjectsFromArray:[NEActionMenuItems defaultActionMenuItems]];
  } else {
    [self.allItems addObjectsFromArray:[NEMenuItems defaultMoreMenuItems]];
    [self.allItems addObjectsFromArray:[NEMenuItems defaultToolbarMenuItems]];
  }

  [self.allItems addObject:[self addSingleStateMenuItem]];
  [self.allItems addObject:[self addEditCheckableMenuItem]];

  [self.selectedCollectionView reloadData];
  [self.allItemCollectionView reloadData];
}

- (NEMeetingMenuItem *)addSingleStateMenuItem {
  NESingleStateMenuItem *item = [[NESingleStateMenuItem alloc] init];
  item.itemId = FIRST_INJECTED_MENU_ID;
  item.visibility = VISIBLE_ALWAYS;

  NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
  info.text = @"自定义单选";
  info.icon = @"calendar";
  item.singleStateItem = info;
  return item;
}

- (NEMeetingMenuItem *)addEditCheckableMenuItem {
  NECheckableMenuItem *item = [[NECheckableMenuItem alloc] init];
  item.itemId = FIRST_INJECTED_MENU_ID + 1;
  item.visibility = VISIBLE_ALWAYS;

  NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
  info.text = [NSString stringWithFormat:@"修改未选中-%@", @(item.itemId)];
  info.icon = @"checkbox_n";
  item.uncheckStateItem = info;

  info = [[NEMenuItemInfo alloc] init];
  info.text = [NSString stringWithFormat:@"修改选中-%@", @(item.itemId)];
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
- (NSInteger)collectionView:(UICollectionView *)collectionView
     numberOfItemsInSection:(NSInteger)section {
  if ([collectionView isEqual:self.selectedCollectionView]) {
    return self.allSeletedItems.count;
  }
  return self.allItems.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView
                           cellForItemAtIndexPath:(NSIndexPath *)indexPath {
  if ([collectionView isEqual:self.selectedCollectionView]) {
    MeetingMenuItemCell *cell =
        [collectionView dequeueReusableCellWithReuseIdentifier:cellIdentifier
                                                  forIndexPath:indexPath];
    NEMeetingMenuItem *item = self.allSeletedItems[indexPath.row];
    cell.item = item;
    return cell;
  }
  MeetingMenuItemCell *cell =
      [collectionView dequeueReusableCellWithReuseIdentifier:allCellIdentifier
                                                forIndexPath:indexPath];
  NEMeetingMenuItem *item = self.allItems[indexPath.row];
  cell.item = item;
  return cell;
}
#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView
    didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
  if ([collectionView isEqual:self.selectedCollectionView]) {
    SelectedMenuItemEditVC *vc = (SelectedMenuItemEditVC *)[[UIStoryboard storyboardWithName:@"Main"
                                                                                      bundle:nil]
        instantiateViewControllerWithIdentifier:@"SelectedMenuItemEditVC"];
    __weak typeof(self) weakSelf = self;
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
  } else {
    [self.allSeletedItems addObject:self.allItems[indexPath.row]];
    [self.selectedCollectionView reloadData];
  }
}

#pragma mark - get
- (UICollectionView *)selectedCollectionView {
  if (!_selectedCollectionView) {
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    layout.itemSize = CGSizeMake(160, 60);
    layout.minimumLineSpacing = 10;
    _selectedCollectionView = [[UICollectionView alloc]
               initWithFrame:CGRectMake(10, 10, [UIScreen mainScreen].bounds.size.width - 20,
                                        ([UIScreen mainScreen].bounds.size.height - 20) * 0.7)
        collectionViewLayout:layout];
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
    _allItemCollectionView = [[UICollectionView alloc]
               initWithFrame:CGRectMake(10,
                                        ([UIScreen mainScreen].bounds.size.height - 20) * 0.7 + 10,
                                        [UIScreen mainScreen].bounds.size.width - 20,
                                        ([UIScreen mainScreen].bounds.size.height - 20) * 0.3)
        collectionViewLayout:layout];
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
