#ifndef KIT_SERVICE_FEEDBACK_REFLECTION_H
#define KIT_SERVICE_FEEDBACK_REFLECTION_H

#include "kit_service_feedback.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

ReflectionDefinition_O(NEFeedback,
    category,
    description,
    time,
    imageList,
    needAudioDump);

#endif  // KIT_SERVICE_FEEDBACK_REFLECTION_H
