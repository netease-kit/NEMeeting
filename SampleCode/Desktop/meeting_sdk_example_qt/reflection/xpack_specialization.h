#ifndef XPACK_SPECIALIZATION_H
#define XPACK_SPECIALIZATION_H

// #include <json/reader.h>
// #include <json/value.h>
#include "kit_service_meeting.h"
#include "xpack/json.h"

USING_NS_NNEM_SDK_INTERFACE;

#define ReflectionDefinitionInternal(Class, ...) XPACK_OUT(Class, A(__VA_ARGS__));

#define RegistParamDefinition(Class, ...)                \
    namespace param_registrar {                          \
    static const ParamRegistrar<Class> g##Class{#Class}; \
    }

#define ReflectionDefinition(Class, ...) ReflectionDefinitionInternal(Class, __VA_ARGS__);

#define ReflectionDefinitionAndReg(Class, ...)        \
    ReflectionDefinitionInternal(Class, __VA_ARGS__); \
    RegistParamDefinition(Class, __VA_ARGS__)

#define ReflectionDefinitionInternal_O(Class, ...) XPACK_OUT(Class, O(__VA_ARGS__));

#define ReflectionDefinition_O(Class, ...) ReflectionDefinitionInternal_O(Class, __VA_ARGS__);

#define ReflectionDefinitionWithNoFiled(Class)                                                \
    namespace xpack {                                                                         \
    template <>                                                                               \
    struct is_xpack_xtype<Class> {                                                            \
        static bool const value = true;                                                       \
    };                                                                                        \
    template <class OBJ>                                                                      \
    bool xpack_xtype_decode(OBJ& obj, const char* key, Class& val, const Extend* ext) {       \
        return true;                                                                          \
    }                                                                                         \
    template <class OBJ>                                                                      \
    bool xpack_xtype_encode(OBJ& obj, const char* key, const Class& val, const Extend* ext) { \
        return true;                                                                          \
    }                                                                                         \
    }

#define ReflectionDefinitionAndReg_O(ClassName, Class, ...) \
    ReflectionDefinitionInternal_O(Class, __VA_ARGS__);     \
    RegistParamDefinition(ClassName, __VA_ARGS__)

namespace xpack {

template <>
struct is_xpack_xtype<std::shared_ptr<NEMeetingMenuItem>> {
    static bool const value = true;
};
template <class OBJ>
bool xpack_xtype_decode(OBJ& obj, std::shared_ptr<NEMeetingMenuItem>& val, const Extend* ext) {
    NEMeetingMenuItem baseItem;
    obj.decode(baseItem, ext);
    switch (baseItem.type) {
        case kNEMeetingMenuTypeSingleState: {
            auto singleStateItem = std::make_shared<NESingleStateMenuItem>();
            obj.decode(*singleStateItem, ext);
            val = singleStateItem;
            break;
        }
        case kNEMeetingMenuTypeCheckable: {
            auto checkableItem = std::make_shared<NECheckableMenuItem>();
            obj.decode(*checkableItem, ext);
            val = checkableItem;
            break;
        }
        case kNEMeetingMenuTypeDefault:
        default:
            break;
    }
    return true;
}

template <class OBJ>
bool xpack_xtype_encode(OBJ& obj, const char* key, const std::shared_ptr<NEMeetingMenuItem>& val, const Extend* ext) {
    if (!val) {
        return true;
    }
    switch (val->type) {
        case kNEMeetingMenuTypeSingleState: {
            auto singleStateItem = std::dynamic_pointer_cast<NESingleStateMenuItem>(val);
            return obj.encode(key, *singleStateItem, ext);
        }
        case kNEMeetingMenuTypeCheckable: {
            auto checkableItem = std::dynamic_pointer_cast<NECheckableMenuItem>(val);
            return obj.encode(key, *checkableItem, ext);
        }
        case kNEMeetingMenuTypeDefault:
        default:
            assert(0);
            return false;
    }
}

}  // namespace xpack

#endif
