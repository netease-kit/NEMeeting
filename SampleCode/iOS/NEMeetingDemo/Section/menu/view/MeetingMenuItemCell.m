// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingMenuItemCell.h"

@implementation MeetingMenuItemCell
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 10, frame.size.width - 20, frame.size.height - 20)];
        label.layer.cornerRadius = 8;
        label.textAlignment = NSTextAlignmentCenter;
        label.layer.masksToBounds = YES;
        label.textColor = [UIColor whiteColor];
        [self.contentView addSubview:label];
        self.titleLabel = label;
    }
    return self;
}

- (void)setItem:(NEMeetingMenuItem *)item {
    if ([item isKindOfClass:[NESingleStateMenuItem class]]) {
        NESingleStateMenuItem *sItem = (NESingleStateMenuItem *)item;
        self.titleLabel.text = sItem.singleStateItem.text;
    }
    if ([item isKindOfClass:[NECheckableMenuItem class]]) {
        NECheckableMenuItem *sItem = (NECheckableMenuItem *)item;
        self.titleLabel.text = sItem.uncheckStateItem.text;
    }
    switch (item.visibility) {
        case VISIBLE_ALWAYS:
            self.titleLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x96/ 255.0 blue: 1.0 alpha:1.0f];
            break;
        case VISIBLE_TO_HOST_ONLY:
            self.titleLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x77/ 255.0 blue: 0x88/255.0 alpha:1.0f];
            break;
        case VISIBLE_EXCLUDE_HOST:
            self.titleLabel.backgroundColor = [UIColor colorWithRed: 0x59/ 255.0 green: 0x33/ 255.0 blue: 0x44/ 255.0 alpha:1.0f];
            break;
        default:
            break;
    }
}
@end
