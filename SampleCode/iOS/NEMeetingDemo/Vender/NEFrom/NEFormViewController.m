// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEFormViewController.h"
#import "NEFromCellFactory.h"
#import "NEFromBaseCell.h"
#import <IQKeyboardManager/IQKeyboardManager.h>

@interface NEFormViewController ()<UITableViewDelegate, UITableViewDataSource>

@end

@implementation NEFormViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self.view addSubview:self.tableView];
    [self addHideKeyboard];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    _tableView.frame = CGRectMake(self.view.bounds.origin.x, self.view.bounds.origin.y, self.view.width, self.view.height - 100);
}

-(void)addHideKeyboard
{
    UITapGestureRecognizer *gestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideKeyboard)];
    [self.tableView addGestureRecognizer:gestureRecognizer];
    gestureRecognizer.cancelsTouchesInView = NO;
}

- (void) hideKeyboard {
    [self.view endEditing:YES];
}

- (void)setGroups:(NSMutableArray *)groups {
    _groups = groups;
    [self.tableView reloadData];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [IQKeyboardManager sharedManager].enable = YES;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self.view endEditing:YES];
    [IQKeyboardManager sharedManager].enable = NO;
}

- (NEFromRow *)rowWithTag:(NSString *)tag {
    return [self rowWithTag:tag indexPath:nil];;
}

- (NSIndexPath *)rowIndexPathWithTag:(NSString *)tag {
    NSIndexPath *ret = nil;
    [self rowWithTag:tag indexPath:&ret];
    return ret;
}

- (NEFromRow *)rowWithTag:(NSString *)tag indexPath:(NSIndexPath **)indexPath {
    __block NSInteger section = 0;
    __block NSInteger row = 0;
    __block NEFromRow *ret = nil;
    __block BOOL isExist = NO;
    if (!tag) {
        return nil;
    }
    [_groups enumerateObjectsUsingBlock:^(NEFromGroup * _Nonnull g_obj, NSUInteger g_idx, BOOL * _Nonnull g_stop) {
        if (isExist) {
            *g_stop = YES;
        }
        [g_obj.rows enumerateObjectsUsingBlock:^(NEFromRow * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([obj.tag isEqualToString:tag]) {
                ret = obj;
                row = idx;
                section = g_idx;
                isExist = YES;
                *stop = YES;
            }
        }];
    }];
    NSIndexPath *index = isExist ? [NSIndexPath indexPathForRow:row inSection:section] : nil;
    if (indexPath) {
        *indexPath = index;
    }
    
    return ret;
}

- (void)insertRow:(NEFromRow *)row below:(NEFromRow *)below {
    NSIndexPath *belowIndex = [self rowIndexPathWithTag:below.tag];
    if (row && belowIndex) {
        NSIndexPath *targetIndex = [NSIndexPath indexPathForRow:belowIndex.row+1 inSection:belowIndex.section];
        [_groups[targetIndex.section].rows insertObject:row atIndex:targetIndex.row];
        [self.tableView insertRowsAtIndexPaths:@[targetIndex] withRowAnimation:UITableViewRowAnimationFade];
    }
}

- (void)deleteRow:(NEFromRow *)row {
    NSIndexPath *targetIndex = [self rowIndexPathWithTag:row.tag];
    if (targetIndex) {
        [_groups[targetIndex.section].rows removeObjectAtIndex:targetIndex.row];
        [self.tableView deleteRowsAtIndexPaths:@[targetIndex] withRowAnimation:UITableViewRowAnimationFade];
    }
}

- (void)deleteRowWithTag:(NSString *)tag {
    NSIndexPath *targetIndex = [self rowIndexPathWithTag:tag];
    if (targetIndex) {
        [_groups[targetIndex.section].rows removeObjectAtIndex:targetIndex.row];
        [self.tableView deleteRowsAtIndexPaths:@[targetIndex] withRowAnimation:UITableViewRowAnimationFade];
    }
}

#pragma mark - <UITableViewDelegate, UITableViewDataSource>
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return _groups.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _groups[section].rows.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEFromGroup *group = _groups[indexPath.section];
    NEFromRow *row = group.rows[indexPath.row];
    NEFromBaseCell *cell = [[NEFromCellFactory shareInstance] dequeueReusableCell:tableView
                                                                     forIndexPath:indexPath
                                                                          rowType:row.rowType];
    [cell refreshCellWithRow:row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 20;
}

#pragma mark - Getter
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)
                                                  style:UITableViewStyleGrouped];
        _tableView.showsVerticalScrollIndicator = NO;
        _tableView.showsHorizontalScrollIndicator = NO;
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.rowHeight = 56.0f;
        _tableView.sectionHeaderHeight = 20;
        _tableView.sectionFooterHeight = CGFLOAT_MIN;
        _tableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
        [[NEFromCellFactory shareInstance] tableViewRegisterCell:_tableView];
    }
    return _tableView;
}

@end
