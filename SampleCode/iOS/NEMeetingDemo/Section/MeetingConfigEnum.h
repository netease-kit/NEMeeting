// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.


#ifndef MeetingConfigEnum_h
#define MeetingConfigEnum_h

/// 配置选项类型
typedef NS_ENUM(NSInteger, MeetingConfigType) {
  MeetingConfigTypeJoinOnVideo = 0,
  MeetingConfigTypeJoinOnAudio,
  MeetingConfigTypeShowTime
};
/// 加入/匿名 入会设置选项类型
typedef NS_ENUM(NSInteger, MeetingSettingType) {
  MeetingSettingTypeJoinOffChatroom = 0,
  MeetingSettingTypeJoinOffInvitation,
  MeetingSettingTypeHideMini,
  MeetingSettingTypeDefaultSetting,
  MeetingSettingTypeJoinOffGallery,
  MeetingSettingTypeOnlyShowLongId,
  MeetingSettingTypeOnlyShowShortId,
  MeetingSettingTypeOffSwitchCamera,
  MeetingSettingTypeOffSwitchAudio,
  MeetingSettingTypeShowWhiteboard,
  MeetingSettingTypeHiddenWhiteboardButton,
  MeetingSettingTypeOffReName,
  MeetingSettingTypeHiddenSip,
  MeetingSettingTypeShowRoleLabel,
  MeetingSettingTypeShowMeeingRemainingTip,
  MeetingSettingTypeChatroomEnableFile,
  MeetingSettingTypeChatroomEnableImage,
  MeetingSettingTypeDetectMutedMic,
  MeetingSettingTypeUnpubAudioOnMute,
  MeetingSettingTypeShowScreenShareUserVideo,
  MeetingSettingTypeShowWhiteboardShareUseVideo,
  MeetingSettingTypeEnableTransparentWhiteboard,
  MeetingSettingTypeEnableFrontCameraMirror,
  MeetingSettingTypeShowFloatingMicrophone,
  MeetingSettingTypeJoinOffLive,
  MeetingSettingTypeEnabeAudioShare,
  MeetingSettingTypeEnableEncryption
};

/// 创建会议设置选项类型
typedef NS_ENUM(NSInteger, CreateMeetingSettingType) {
  CreateMeetingSettingTypeJoinOffChatroom = 0,
  CreateMeetingSettingTypeJoinOffInvitation,
  CreateMeetingSettingTypeHideMini,
  CreateMeetingSettingTypeUsePid,
  CreateMeetingSettingTypeDefaultSetting,
  CreateMeetingSettingTypeJoinOffGallery,
  CreateMeetingSettingTypeOnlyShowLongId,
  CreateMeetingSettingTypeOnlyShowShortId,
  CreateMeetingSettingTypeOffSwitchCamera,
  CreateMeetingSettingTypeOffSwitchAudio,
  CreateMeetingSettingTypeShowWhiteboard,
  CreateMeetingSettingTypeHiddenWhiteboardButton,
  CreateMeetingSettingTypeOffReName,
  CreateMeetingSettingTypeOpenCloudRecord,
  CreateMeetingSettingTypeHiddenSip,
  CreateMeetingSettingTypeShowRoleLabel,
  CreateMeetingSettingTypeAutoMuteAudio,
  CreateMeetingSettingTypeAutoMuteAudioNotRemove,
  CreateMeetingSettingTypeAutoMuteVideo,
  CreateMeetingSettingTypeAutoMuteVideoNotRemove,
  CreateMeetingSettingTypeShowMeeingRemainingTip,
  CreateMeetingSettingTypeChatroomEnableFile,
  CreateMeetingSettingTypeChatroomEnableImage,
  CreateMeetingSettingTypeDetectMutedMic,
  CreateMeetingSettingTypeUnpubAudioOnMute,
  CreateMeetingSettingTypeShowScreenShareUserVideo,
  CreateMeetingSettingTypeShowWhiteboardShareUseVideo,
  CreateMeetingSettingTypeEnableTransparentWhiteboard,
  CreateMeetingSettingTypeEnableFrontCameraMirror,
  CreateMeetingSettingTypeShowFloatingMicrophone,
  CreateMeetingSettingTypeJoinOffLive,
  CreateMeetingSettingTypeEnabeAudioShare,
  CreateMeetingSettingTypeEnableEncryption
};

#endif /* MeetingConfigEnum_h */
