//
//  SelectedMenuItemCell.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2020/12/11.
//

#import <Foundation/Foundation.h>
#import <NEMeetingSDK/NEMeetingSDK.h>
#import "SelectedMenuItemCell.h"


@interface SelectedMenuItemCell ()

@property (nonatomic, strong) NEMeetingMenuItem *item;

@property (weak, nonatomic) IBOutlet UILabel * itemLabel;

@end

@implementation SelectedMenuItemCell


- (void)bindData:(NEMeetingMenuItem *)data {
    if ([data isKindOfClass: [NESingleStateMenuItem class]]) {
        NESingleStateMenuItem *sItem = (NESingleStateMenuItem *)data;
        _itemLabel.text = sItem.singleStateItem.text;
    }
    if ([data isKindOfClass: [NECheckableMenuItem class]]) {
        NECheckableMenuItem *sItem = (NECheckableMenuItem *)data;
        _itemLabel.text = sItem.uncheckStateItem.text;
    }
    switch (data.visibility) {
        case VISIBLE_ALWAYS:
            _itemLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x96/ 255.0 blue: 1.0 alpha:1.0f];
            break;
        case VISIBLE_TO_HOST_ONLY:
            _itemLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x77/ 255.0 blue: 0x88/255.0 alpha:1.0f];
            break;
        case VISIBLE_EXCLUDE_HOST:
            _itemLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x33/ 255.0 blue: 0x44/ 255.0 alpha:1.0f];
            break;
        default:
            break;
    }
}

@end
