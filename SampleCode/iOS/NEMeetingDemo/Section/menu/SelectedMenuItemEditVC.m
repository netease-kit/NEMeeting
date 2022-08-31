// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>
#import <NEMeetingKit/NEMeetingKit.h>
#import "SelectedMenuItemEditVC.h"
#import "CheckBox.h"
#import "UIView+Toast.h"

@interface SelectedMenuItemEditVC () <CheckBoxDelegate>

@property (weak, nonatomic) IBOutlet UIButton *delete;
@property (weak, nonatomic) IBOutlet UIButton *done;
@property (weak, nonatomic) IBOutlet UITextField *itemIdEdx;
@property (weak, nonatomic) IBOutlet UITextField *itemUncheckText;
@property (weak, nonatomic) IBOutlet UITextField *itemCheckText;
@property (weak, nonatomic) IBOutlet CheckBox *configs;

@end


@implementation SelectedMenuItemEditVC

- (void)viewDidLoad {
    _itemIdEdx.placeholder = @"菜单ID";
    _itemIdEdx.keyboardType = UIKeyboardTypeNumberPad;
    _itemUncheckText.placeholder = @"未选中状态描述";
    _itemCheckText.placeholder = @"选中状态描述";
    
    [_configs setItemTitleWithArray:@[@"始终可见",
                                             @"仅普通参会者可见",
                                             @"仅主持人可见",
                                      @"替换SDK内置菜单图标"]];
    _configs.delegate = self;
    
    NEMeetingMenuItem *item = _menuItem;
    _itemIdEdx.text = [NSString stringWithFormat: @"%@", @(item.itemId)];
    if ([item isKindOfClass: [NESingleStateMenuItem class]]) {
        NESingleStateMenuItem *sItem = (NESingleStateMenuItem *)item;
        _itemUncheckText.text = sItem.singleStateItem.text;
        _itemCheckText.hidden = YES;
    }
    if ([item isKindOfClass: [NECheckableMenuItem class]]) {
        NECheckableMenuItem *sItem = (NECheckableMenuItem *)item;
        _itemUncheckText.text = sItem.uncheckStateItem.text;
        _itemCheckText.text = sItem.checkedStateItem.text;
    }
    
    switch (item.visibility) {
        case VISIBLE_ALWAYS:
            [_configs setItemSelected:YES index:0];
            break;
        case VISIBLE_EXCLUDE_HOST:
            [_configs setItemSelected:YES index:1];
            break;
        case VISIBLE_TO_HOST_ONLY:
            [_configs setItemSelected:YES index:2];
            break;
        default:
            break;
    }
}
- (IBAction)deleteItem:(id)sender {
    NSLog(@"delete item at index");
    if (_MenuItemDeleteCallback) {
        _MenuItemDeleteCallback();
    }
    [self.navigationController popViewControllerAnimated: YES];
}

- (IBAction)editDone:(id)sender {
    NEMeetingMenuItem *item = _menuItem;

    if (_itemIdEdx.text == nil || _itemIdEdx.text.length == 0) {
        [self makeToast: @"菜单ID不能为空"];
        return;
    }
    
    if (_itemUncheckText.text == nil || _itemUncheckText.text.length == 0) {
        [self makeToast: @"菜单标题不能为空"];
        return;
    }

    if ([item isKindOfClass: [NECheckableMenuItem class]] && 
        (_itemCheckText.text == nil || _itemCheckText.text.length == 0)) {
        [self makeToast: @"菜单标题不能为空"];
        return;
    }
    
    if ([self onlyCheckBoxAtIndexSelected: 0]) {
        item.visibility = VISIBLE_ALWAYS;
    } else if ([self onlyCheckBoxAtIndexSelected: 1]) {
        item.visibility = VISIBLE_EXCLUDE_HOST;
    } else if ([self onlyCheckBoxAtIndexSelected: 2]) {
        item.visibility = VISIBLE_TO_HOST_ONLY;
    } else {
        [self makeToast: @"菜单可见性不能留空"];
        return;
    }
    
    item.itemId = [_itemIdEdx.text  intValue];
    
    BOOL replaceIcon = [_configs getItemSelectedAtIndex:3];
    
    if ([item isKindOfClass: [NESingleStateMenuItem class]]) {
        NESingleStateMenuItem *sItem = (NESingleStateMenuItem *)item;
        sItem.singleStateItem.text = _itemUncheckText.text;
        if (replaceIcon) {
            sItem.singleStateItem.icon = @"calendar";
        }
    }
    if ([item isKindOfClass: [NECheckableMenuItem class]]) {
        NECheckableMenuItem *sItem = (NECheckableMenuItem *)item;
        sItem.uncheckStateItem.text = _itemUncheckText.text;
        sItem.checkedStateItem.text = _itemCheckText.text;
        if (replaceIcon) {
            sItem.uncheckStateItem.icon = @"checkbox_n";
            sItem.checkedStateItem.icon = @"checkbox_s";
        }
    }
    if (self.menuItemSure) self.menuItemSure();
    [self.navigationController popViewControllerAnimated: YES];
}

-(void)checkBoxItemdidSelected:(UIButton *)item
                       atIndex:(NSUInteger)index
                      checkBox:( CheckBox *)checkbox {
    for (int i = 0; i < 3; i++) {
        if ( i != index) {
            [_configs setItemSelected:NO index:i];
        }
    }
}

- (BOOL) onlyCheckBoxAtIndexSelected: (NSUInteger)index {
    for (int i = 0; i < 3; i++) {
        if ([_configs getItemSelectedAtIndex: i]) {
            if (i != index) {
                return NO;
            }
        }else if (i == index) {
            return NO;
        }
    }
    return YES;
}

- (void)makeToast: (NSString *)message {
    [self.view makeToast:message duration:[CSToastManager defaultDuration] position:CSToastPositionCenter style:nil];
}

@end
