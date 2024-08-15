#ifndef KIT_SERVICE_MESSAGE_REFLECTION_H
#define KIT_SERVICE_MESSAGE_REFLECTION_H

#include "kit_service_meeting.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NEMeetingSessionMessage, sessionId, sessionType, messageId, data, time);

ReflectionDefinition_O(NEMeetingRecentSession, sessionId, fromAccount, fromNick, sessionType, recentMessageId, unreadCount, content, time);

#endif  // KIT_SERVICE_MESSAGE_REFLECTION_H
