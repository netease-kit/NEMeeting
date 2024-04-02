// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "MeetingMenuItemCell.h"

@implementation MeetingMenuItemCell
- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    UILabel *label = [[UILabel alloc]
        initWithFrame:CGRectMake(10, 10, frame.size.width - 20, frame.size.height - 20)];
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
  self.titleLabel.text = [self getMenuItemText:item];
  switch (item.visibility) {
    case VISIBLE_ALWAYS:
      self.titleLabel.backgroundColor = [UIColor colorWithRed:0x59 / 255.0
                                                        green:0x96 / 255.0
                                                         blue:1.0
                                                        alpha:1.0f];
      break;
    case VISIBLE_TO_HOST_ONLY:
      self.titleLabel.backgroundColor = [UIColor colorWithRed:0x59 / 255.0
                                                        green:0x77 / 255.0
                                                         blue:0x88 / 255.0
                                                        alpha:1.0f];
      break;
    case VISIBLE_EXCLUDE_HOST:
      self.titleLabel.backgroundColor = [UIColor colorWithRed:0x59 / 255.0
                                                        green:0x33 / 255.0
                                                         blue:0x44 / 255.0
                                                        alpha:1.0f];
      break;
    default:
      break;
  }
}

- (NSString *)getMenuItemText:(NEMeetingMenuItem *)item {
  switch (item.itemId) {
    case MIC_MENU_ID:
      return @"解除静音";
    case CAMERA_MENU_ID:
      return @"开启视频";
    case PARTICIPANTS_MENU_ID:
      return @"参会者";
    case MANAGE_PARTICIPANTS_MENU_ID:
      return @"管理参会者";
    case SWITCH_SHOW_TYPE_MENU_ID:
      return @"布局视图";
    case INVITE_MENU_ID:
      return @"邀请";
    case CHAT_MENU_ID:
      return @"聊天";
    case NOTIFY_CENTER_MENU_ID:
      return @"通知";
    case SCREEN_SHARE_MENU_ID:
      return @"共享屏幕";
    case WHITEBOARD_MENU_ID:
      return @"共享白板";
    case CLOUD_RECORD_MENU_ID:
      return @"云录制";
    case SECURITY_MENU_ID:
      return @"安全";
    case DISCONNECT_AUDIO_MENU_ID:
      return @"断开音频";
    default:
      break;
  }

  if ([item isKindOfClass:[NESingleStateMenuItem class]]) {
    NESingleStateMenuItem *sItem = (NESingleStateMenuItem *)item;
    return sItem.singleStateItem.text;
  }
  if ([item isKindOfClass:[NECheckableMenuItem class]]) {
    NECheckableMenuItem *sItem = (NECheckableMenuItem *)item;
    return sItem.uncheckStateItem.text;
  }
  return @"";
}

@end
