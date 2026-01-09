#ifndef KIT_SERVICE_REALTIME_RECORDER_REFLECTION_H
#define KIT_SERVICE_REALTIME_RECORDER_REFLECTION_H

#include "kit_define_realtime_recorder.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NEMeetingRealtimeRecorderParams, displayName, meetingNum);

ReflectionDefinition_O(NEMeetingRealtimeRecorderOptions, extraData);

ReflectionDefinition_O(NEMeetingRealtimeRecorderEvent, status, code, meetingNum);

#endif // KIT_SERVICE_REALTIME_RECORDER_REFLECTION_H
