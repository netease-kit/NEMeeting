#ifndef KIT_SERVICE_ACCOUNT_REFLECTION_H
#define KIT_SERVICE_ACCOUNT_REFLECTION_H

#include "kit_service_account.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NEServiceBundle, name, maxMinutes, maxMembers, expireTimestamp, expireTip);

ReflectionDefinition_O(NEAccountInfo,
    corpName,
    userUuid,
    userToken,
    nickname,
    avatar,
    phoneNumber,
    email,
    privateMeetingNum,
    privateShortMeetingNum,
    isInitialPassword,
    serviceBundle,
    isAnonymous);
#endif  // KIT_SERVICE_ACCOUNT_REFLECTION_H
