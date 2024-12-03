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
    case SETTINGS_MENU_ID:
      return @"设置";
    case FEEDBACK_MENU_ID:
      return @"反馈";
    case SIP_CALL_MENU_ID:
      return @"SIP";
    case CAPTIONS_MENU_ID:
      return @"字幕";
    case TRANSCRIPTION_MENU_ID:
      return @"实时转写";
    case INTERPRETATION_MENU_ID:
      return @"同声传译";
    case BEAUTY_MENU_ID:
      return @"美颜";
    case VIRTUAL_BACKGROUND_MENU_ID:
      return @"虚拟背景";
    case LIVE_MENU_ID:
      return @"直播";
    default:
      break;
  }

  switch (item.itemId) {
    case AUDIO_ACTION_MENU_ID:
      return @"解除静音";
    case VIDEO_ACTION_MENU_ID:
      return @"开启视频";
    case FOCUS_VIDEO_ACTION_MENU_ID:
      return @"焦点视频";
    case LOCK_VIDEO_ACTION_MENU_ID:
      return @"锁定视频";
    case CHANGE_HOST_ACTION_MENU_ID:
      return @"移交主持人";
    case RECLAIM_HOST_ACTION_MENU_ID:
      return @"收回主持人";
    case REMOVE_MEMBER_ACTION_MENU_ID:
      return @"移除成员";
    case REJECT_HANDS_UP_ACTION_MENU_ID:
      return @"手放下";
    case WHITEBOARD_INTERACTION_ACTION_MENU_ID:
      return @"白板互动";
    case SCREEN_SHARE_ACTION_MENU_ID:
      return @"共享屏幕";
    case WHITEBOARD_SHARE_ACTION_MENU_ID:
      return @"共享白板";
    case UPDATE_NICK_ACTION_MENU_ID:
      return @"改名";
    case AUDIO_AND_VIDEO_ACTION_MENU_ID:
      return @"关闭音视频";
    case CO_HOST_ACTION_MENU_ID:
      return @"设为联席主持人";
    case PUTIN_WAITING_ROOM_ACTION_MENU_ID:
      return @"移至等候室";
    case CHAT_PRIVATE_ACTION_MENU_ID:
      return @"私聊";
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
