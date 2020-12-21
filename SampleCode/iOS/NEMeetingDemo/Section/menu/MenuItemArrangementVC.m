//
//  MenuItemArragementVC.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2020/12/10.
//

#import <Foundation/Foundation.h>
#import <NEMeetingSDK/NEMeetingSDK.h>
#import "MenuItemArrangementVC.h"
#import "SelectedMenuItemCell.h"
#import "SelectedMenuItemEditVC.h"

@interface MenuItemArrangementVC () <UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

@property (weak, nonatomic) IBOutlet UILabel *mic;

@property (weak, nonatomic) IBOutlet UILabel *camera;

@property (weak, nonatomic) IBOutlet UILabel *participants;

@property (weak, nonatomic) IBOutlet UILabel *managerParticipants;

@property (weak, nonatomic) IBOutlet UILabel *invitation;

@property (weak, nonatomic) IBOutlet UILabel *chat;

@property (weak, nonatomic) IBOutlet UILabel *single;

@property (weak, nonatomic) IBOutlet UILabel *checkable;

@end

@implementation MenuItemArrangementVC


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [_collectionView registerNib:[UINib nibWithNibName:@"SelectedMenuItemCell" bundle:nil] forCellWithReuseIdentifier: @"SelectedMenuItemCell"];
    
    [self bindAction:_mic action:@selector(addMicMenuItem)];
    [self bindAction:_camera action:@selector(addCameraMenuItem)];
    [self bindAction:_participants action:@selector(addParticipantsMenuItem)];
    [self bindAction:_managerParticipants action:@selector(addManagerParticipantsMenuItem)];
    [self bindAction:_invitation action:@selector(addInviteMenuItem)];
    [self bindAction:_chat action:@selector(addChatMenuItem)];
    [self bindAction:_single action:@selector(addSingleStateMenuItem)];
    [self bindAction:_checkable action:@selector(addCheckableMenuItem)];
}

- (void)viewWillDisappear:(BOOL)animated {
    if (self.MenuItemSelectCallback && _menuItems) {
        self.MenuItemSelectCallback(_menuItems);
    }
    [super viewWillDisappear:animated];
}

- (void)viewWillAppear:(BOOL)animated {
    [self.collectionView reloadData];
}

- (void) bindAction: (UILabel *)label action:(nullable SEL)action {
    label.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGesture =
          [[UITapGestureRecognizer alloc] initWithTarget:self action:action];
    [label addGestureRecognizer:tapGesture];
}

- (void) addMicMenuItem {
    [self addMenuItem: NEMenuItems.mic];
}

- (void) addCameraMenuItem {
    [self addMenuItem: NEMenuItems.camera];
}

- (void) addParticipantsMenuItem {
    [self addMenuItem: NEMenuItems.participants];
}

- (void) addManagerParticipantsMenuItem {
    [self addMenuItem: NEMenuItems.managerParticipants];
}

- (void) addInviteMenuItem {
    [self addMenuItem: NEMenuItems.invite];
}

- (void) addChatMenuItem {
    [self addMenuItem: NEMenuItems.chat];
}

- (void) addSingleStateMenuItem {
    NESingleStateMenuItem *item = [[NESingleStateMenuItem alloc] init];
    item.itemId = [self menuId];
    item.visibility = VISIBLE_ALWAYS;
    
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = @"单选";
    info.icon = @"calendar";
    item.singleStateItem = info;
    [self addMenuItem: item];
}

- (void) addCheckableMenuItem {
    NECheckableMenuItem *item = [[NECheckableMenuItem alloc] init];
    item.itemId = [self menuId];
    item.visibility = VISIBLE_ALWAYS;
    
    NEMenuItemInfo *info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"未选中-%@", @(item.itemId)];
    info.icon = @"checkbox_n";
    item.uncheckStateItem = info;
    
    info = [[NEMenuItemInfo alloc] init];
    info.text = [NSString stringWithFormat:@"选中-%@", @(item.itemId)];
    info.icon = @"checkbox_s";
    item.checkedStateItem = info;
    
    [self addMenuItem: item];
}

- (int) menuId {
    static int itemId = FIRST_INJECTED_MENU_ID;
    return itemId++;
}

- (void) addMenuItem: (NEMeetingMenuItem *)item {
    if (!_menuItems) {
        _menuItems = [NSMutableArray array];
    }
    [_menuItems addObject: item];
    [_collectionView reloadData];
}

#pragma mark -- UICollectionViewDataSource
-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

//定义展示的UICollectionViewCell的个数
-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return _menuItems ? _menuItems.count : 0;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString * CellIdentifier = @"SelectedMenuItemCell";
    SelectedMenuItemCell * cell = [collectionView dequeueReusableCellWithReuseIdentifier:CellIdentifier forIndexPath:indexPath];
    
    [cell bindData: _menuItems[indexPath.item]];
    
    UITapGestureRecognizer *tapGesture =
    [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onCellTap:)];
    [cell addGestureRecognizer:tapGesture];
    
    return cell;
}

- (void) onCellTap: (UITapGestureRecognizer*)sender {
    NSIndexPath * indexPath = [self.collectionView indexPathForItemAtPoint: [sender locationInView: self.collectionView]];
    NSInteger index = indexPath.item;
    NSLog(@"onCellTap: %@", @(index));
    
    SelectedMenuItemEditVC *vc = (SelectedMenuItemEditVC *)[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SelectedMenuItemEditVC"];
    __weak __typeof__ (self)weakSelf = self;
    vc.MenuItemDeleteCallback = ^{
        [weakSelf.menuItems removeObjectAtIndex: index];
    };
    vc.menuItem = _menuItems[indexPath.item];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark --UICollectionViewDelegateFlowLayout
//定义每个UICollectionView 的大小
//- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
//{
//    return CGSizeMake(60, 40);
//}

-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(5, 5, 5, 5);
}

#pragma mark --UICollectionViewDelegate
//UICollectionView被选中时调用的方法
//-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
//{
//    UICollectionViewCell * cell = (UICollectionViewCell *)[collectionView cellForItemAtIndexPath:indexPath];
//    //cell.backgroundColor = [UIColor whiteColor];
//}

////返回这个UICollectionView是否可以被选择
-(BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

@end
