#ifndef KIT_SERVICE_MEETING_REFLECTION_H
#define KIT_SERVICE_MEETING_REFLECTION_H

#include "kit_service_meeting.h"
#include "kit_define_screen_sharing.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NEEncryptionConfig, encryptKey, encryptionMode);

ReflectionDefinition_O(NEWatermarkConfig, name, phone, email, jobNumber);

ReflectionDefinition_O(NEStartMeetingParams,
    displayName,
    avatar,
    meetingNum,
    tag,
    password,
    //    encryptionConfig,
    watermarkConfig,
    extraData,
    subject,
    controls,
    roleBinds);

ReflectionDefinition_O(NEJoinMeetingParams, displayName, avatar, meetingNum, tag, password, /* encryptionConfig,*/ watermarkConfig);

ReflectionDefinition_O(NEMenuItemInfo, icon, text);

ReflectionDefinition_O(NEMeetingMenuItem, type, itemId, visibility);

ReflectionDefinition_O(NESingleStateMenuItem, type, itemId, visibility, singleStateItem);

ReflectionDefinition_O(NECheckableMenuItem, itemId, visibility,type, checkedStateItem, uncheckStateItem, checked);

ReflectionDefinition_O(NEMenuClickInfo, itemId);

ReflectionDefinition_O(NEStatefulMenuClickInfo, state, itemId, isChecked);

ReflectionDefinition_O(NEMeetingChatroomConfig, enableFileMessage, enableImageMessage);

ReflectionDefinition_O(NEStartMeetingOptions,
    noVideo,
    noAudio,
    showMeetingTime,
    enableSpeakerSpotlight,
    noMinimize,
    noSwitchCamera,
    noSwitchAudioMode,
    noLive,
    showScreenShareUserVideo,
    enableAudioShare,
    showWhiteboardShareUserVideo,
    enableTransparentWhiteboard,
    showFloatingMicrophone,
    noChat,
    noInvite,
    noScreenShare,
    noView,
    noWhiteboard,
    noRename,
    noSip,
    noMuteAllVideo,
    noMuteAllAudio,
    audioAINSEnabled,
    showMemberTag,
    showMeetingRemainingTip,
    detectMutedMic,
    unpubAudioOnMute,
    defaultWindowMode,
    meetingIdDisplayOption,
    fullToolbarMenuItems,
    fullMoreMenuItems,
    memberActionMenuItems,
    joinTimeout,
    chatroomConfig,
    audioProfile,
    showCloudRecordMenuItem,
    showCloudRecordingUI,
    enableAudioDeviceSwitch,
    noReadPhoneState,
    noNotifyCenter,
    noWebApps,
    cloudRecordConfig,
    enableWaitingRoom,
    enableGuestJoin,
    noCaptions,
    autoEnableCaptionsOnJoin,
    noTranscription,
    pluginNotifyDuration,
    showNotYetJoinedMembers,
    chatMessageNotificationType,
    showNameInVideo,
    enableDirectMemberMediaControlByHost,
    showParticipationTime,
    enableLeaveTheMeetingRequiresConfirmation);

ReflectionDefinition_O(NEJoinMeetingOptions,
    noVideo,
    noAudio,
    showMeetingTime,
    enableSpeakerSpotlight,
    noMinimize,
    noSwitchCamera,
    noSwitchAudioMode,
    noLive,
    showScreenShareUserVideo,
    enableAudioShare,
    showWhiteboardShareUserVideo,
    enableTransparentWhiteboard,
    showFloatingMicrophone,
    noChat,
    noInvite,
    noScreenShare,
    noView,
    noWhiteboard,
    noRename,
    noSip,
    noMuteAllVideo,
    noMuteAllAudio,
    audioAINSEnabled,
    showMemberTag,
    showMeetingRemainingTip,
    detectMutedMic,
    unpubAudioOnMute,
    defaultWindowMode,
    meetingIdDisplayOption,
    fullToolbarMenuItems,
    fullMoreMenuItems,
    memberActionMenuItems,
    joinTimeout,
    chatroomConfig,
    audioProfile,
    showCloudRecordMenuItem,
    showCloudRecordingUI,
    enableAudioDeviceSwitch,
    noReadPhoneState,
    noNotifyCenter,
    noWebApps,
    enableMyAudioDeviceOnJoinRtc,
    noCaptions,
    autoEnableCaptionsOnJoin,
    noTranscription,
    showParticipationTime,
    enableLeaveTheMeetingRequiresConfirmation);

ReflectionDefinition_O(NEInMeetingUserInfo, userId, userName, tag, role);

ReflectionDefinition_O(NEMeetingInfo,
    meetingId,
    meetingNum,
    shortMeetingNum,
    subject,
    password,
    isHost,
    isLocked,
    isInWaitingRoom,
    scheduleStartTime,
    scheduleEndTime,
    startTime,
    duration,
    sipId,
    hostUserId,
    userList,
    extraData,
    timezoneId);

ReflectionDefinition_O(NEMeetingStatusListener::Event, status, code, obj);

ReflectionDefinition_O(NEMeetingInviteInfo, meetingNum, inviterName, inviterAvatar, subject, preMeetingInvitation);

ReflectionDefinition_O(NERoomSystemDevice, protocol, deviceAddress, name);

ReflectionDefinition_O(NERoomSIPCallInfo, userUuid, userName, isRepeatedCall);

ReflectionDefinition_O(NEScreenSharingParams, sharingCode, displayName);

#endif  // KIT_SERVICE_MEETING_REFLECTION_H
