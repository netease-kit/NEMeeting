#ifndef KIT_SERVICE_PREMEETING_REFLECTION_H
#define KIT_SERVICE_PREMEETING_REFLECTION_H

#include "kit_service_premeeting.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NERemoteHistoryMeeting,
    anchorId,
    meetingId,
    meetingNum,
    subject,
    type,
    roomEntryTime,
    roomStartTime,
    ownerAvatar,
    ownerUserUuid,
    ownerNickname,
    favoriteId,
    roomEndTime,
    timeZoneId);

ReflectionDefinition_O(NEChatroomInfo, chatroomId, exportAccess);

ReflectionDefinition_O(NEMeetingWebAppIconItem, defaultIcon, notifyIcon);

ReflectionDefinition_O(NEMeetingWebAppItem, pluginId, name, icon, description, type, homeUrl, sessionId);

ReflectionDefinition_O(NERemoteHistoryMeetingDetail, chatroomInfo, pluginInfoList);

ReflectionDefinition_O(NEScheduledMember, userUuid, role);

ReflectionDefinition_O(NEMeetingCustomizedFrequency, stepSize, stepUnit, daysOfWeek, daysOfMonth);

ReflectionDefinition_O(NEMeetingRecurringRuleEndRule, date, times, type);
ReflectionDefinition_O(NEMeetingRecurringRule, type, customizedFrequency, endRule);

ReflectionDefinition_O(NEMeetingItemLive,
    enable,
    liveWebAccessControlLevel,
    hlsPullUrl,
    httpPullUrl,
    rtmpPullUrl,
    liveUrl,
    pushUrl,
    chatRoomId,
    liveAVRoomUids,
    liveChatRoomEnable,
    meetingNum,
    state,
    taskId,
    title,
    liveChatRoomIndependent);

ReflectionDefinition_O(NEMeetingRoleConfiguration, roleType, maxCount, userList);

ReflectionDefinition_O(NEMeetingScene, code, roleTypes);

ReflectionDefinition_O(NEMeetingControl, attendeeOff, type);

ReflectionDefinition_O(NEMeetingItemSetting, cloudRecordOn, controls, currentAudioControl, currentVideoControl);

ReflectionDefinition_O(NEMeetingInterpreter, userId, firstLang, secondLang, isValid);

ReflectionDefinition_O(NEMeetingInterpretationSettings, interpreterList);

ReflectionDefinition_O(NECloudRecordConfig, enable, recordStrategy);

ReflectionDefinition_O(NEMeetingItem,
    meetingId,
    meetingNum,
    shortMeetingNum,
    subject,
    roomUuid,
    ownerUserUuid,
    ownerNickname,
    startTime,
    endTime,
    password,
    settings,
    status,
    meetingType,
    noSip,
    waitingRoomEnabled,
    enableJoinBeforeHost,
    enableGuestJoin,
    inviteUrl,
    live,
    extraData,
    roleBinds,
    recurringRule,
    scheduledMemberList,
    timezoneId,
    interpretationSettings,
    cloudRecordConfig,
    sipCid);

ReflectionDefinition_O(NEChatroomHistoryMessageSearchOption, startTime, limit, order);

ReflectionDefinition_O(NEMeetingRecord, recordId, recordStartTime, recordEndTime, infoList);

ReflectionDefinition_O(NEMeetingTranscriptionInfo, state, timeRanges, originalNosFileKeys, txtNosFileKeys, wordNosFileKeys, pdfNosFileKeys);

ReflectionDefinition_O(NEMeetingChatMessage, messageUuid, messageType, fromUserUuid, fromNick, fromAvatar, toUserUuidList, timeLong);

ReflectionDefinition_O(NEMeetingTranscriptionInterval, start, stop);

ReflectionDefinition_O(NEMeetingRecordFileInfo, type, mix, filename, md5, size, url, vid, pieceIndex, userUuid, nickname);

ReflectionDefinition_O(NELocalHistoryMeeting, meetingNum, meetingId, shortMeetingNum, subject, password, nickname, sipId);

ReflectionDefinition_O(NEMeetingTranscriptionMessage, fromUserUuid, fromNickname, content, timestamp);

ReflectionDefinition_O(NEMeetingRecordItem, recordSubject, meetingNum, roomArchiveId, recordCreateTime, recordFileSize, recordFileCount, type, subType, owner, editable, hidable, deletable);

ReflectionDefinition_O(NEMeetingRecordQueryParams, startTime, endTime, pageNum, pageSize, owner, ownerUserName, subject, meetingNum);

ReflectionDefinition_O(NEMeetingRecordEditData, subject, hide);

#endif  // KIT_SERVICE_PREMEETING_REFLECTION_H
