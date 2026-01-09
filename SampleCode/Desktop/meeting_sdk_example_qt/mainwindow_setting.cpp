#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include <QStringListModel>
#include <sstream>

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

#define SettingMethodsInit(func, note)                                                                                                              \
    m_setting_methods.insert(std::make_pair(                                                                                                        \
        list_item++, MethodInfo{#func, note, [&](std::string param) {                                                                               \
                                    PrintLog(#func " run");                                                                                         \
                                    auto service = NEMeetingKit::getInstance()->getSettingsService();                                               \
                                    if (service) {                                                                                                  \
                                        service->func([this](MeetingErrorCode errorCode, const std::string& errorMessage) {                              \
                                            PrintLog(#func " callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage); \
                                        });                                                                                                         \
                                    } else {                                                                                                        \
                                        PrintLog("MeetingKit is not initialized");                                                                  \
                                    }                                                                                                               \
                                }}));

#define SettingMethodsInitParamReq(PARAM, func, note)                                                                                               \
    m_setting_methods.insert(std::make_pair(                                                                                                        \
        list_item++, MethodInfo{#func, note, [&](std::string param) {                                                                               \
                                    PrintLog(#func " run");                                                                                         \
                                    auto service = NEMeetingKit::getInstance()->getSettingsService();                                               \
                                    if (service) {                                                                                                  \
                                        service->func(PARAM, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {                       \
                                            PrintLog(#func " callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage); \
                                        });                                                                                                         \
                                    } else {                                                                                                        \
                                        PrintLog("MeetingKit is not initialized");                                                                  \
                                    }                                                                                                               \
                                }}));

#define SettingMethodsInitResOut(T, func, note, out)                                                                                      \
    m_setting_methods.insert(                                                                                                             \
        std::make_pair(list_item++, MethodInfo{#func, note, [&](std::string param) {                                                      \
                                                   PrintLog(#func " run");                                                                \
                                                   auto service = NEMeetingKit::getInstance()->getSettingsService();                      \
                                                   if (service) {                                                                         \
                                                       service->func([=](MeetingErrorCode errorCode, const std::string& errorMessage, T ret) { \
                                                           PrintLog(#func " callback, errorCode:" + std::to_string(errorCode) +           \
                                                                    ", errorMessage:" + errorMessage + ", ret:" + out);                   \
                                                       });                                                                                \
                                                   } else {                                                                               \
                                                       PrintLog("MeetingKit is not initialized");                                         \
                                                   }                                                                                      \
                                               }}));

#define SettingMethodsInitRes(T, func, note) SettingMethodsInitResOut(T, func, note, std::to_string(ret))
#define SettingMethodsInitRes2(T, func, note) SettingMethodsInitResOut(T, func, note, ret)

std::string processStrList(std::list<std::string> ret) {
    std::string ret_str;
    for (auto& item : ret) {
        ret_str += item + ",";
    }
    return ret_str;
}
#define SettingMethodsInitRes3(T, func, note) SettingMethodsInitResOut(T, func, note, processStrList(ret))

// setting service ------------------------------------------------------------
void MainWindow::InitSettingUI() {
    ui->setting_edit_note->setReadOnly(true);
    ui->setting_list_methods->setEditTriggers(QAbstractItemView::NoEditTriggers);

    auto split_task = [](const std::string& str) {
        std::list<std::string> result;
        std::stringstream ss(str);
        std::string item;
        while (std::getline(ss, item, ',')) {
            result.push_back(item);
        }
        return result;
    };
    int list_item = 0;
    SettingMethodsInit(openSettingsWindow, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableShowMyMeetingElapseTime, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isShowMyMeetingElapseTimeEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableTurnOnMyVideoWhenJoinMeeting, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isTurnOnMyVideoWhenJoinMeetingEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableTurnOnMyAudioWhenJoinMeeting, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isTurnOnMyAudioWhenJoinMeetingEnabled, "none");
    SettingMethodsInitRes(bool, isMeetingLiveSupported, "none");
    SettingMethodsInitRes(bool, isMeetingWhiteboardSupported, "none");
    SettingMethodsInitRes(bool, isMeetingCloudRecordSupported, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableAudioAINS, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isAudioAINSEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableVirtualBackground, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isVirtualBackgroundEnabled, "none");
    SettingMethodsInitParamReq(split_task(param), setBuiltinVirtualBackgroundList, "list<string>，逗号分隔，如a,b,c");
    SettingMethodsInitRes3(std::list<std::string>, getBuiltinVirtualBackgroundList, "none");
    SettingMethodsInitParamReq(split_task(param), setExternalVirtualBackgroundList, "list<string>，逗号分隔，如a,b,c");
    SettingMethodsInitRes3(std::list<std::string>, getExternalVirtualBackgroundList, "none");
    SettingMethodsInitParamReq(param, setCurrentVirtualBackground, "std::string，填入虚拟背景路径");
    SettingMethodsInitRes2(std::string, getCurrentVirtualBackground, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableSpeakerSpotlight, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isSpeakerSpotlightEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableCameraMirror, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isCameraMirrorEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableFrontCameraMirror, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isFrontCameraMirrorEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableTransparentWhiteboard, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isTransparentWhiteboardEnabled, "none");
    SettingMethodsInitRes(bool, isBeautyFaceSupported, "none");
    SettingMethodsInitRes(int, getBeautyFaceValue, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), setBeautyFaceValue, "int, 填入 0-10");
    SettingMethodsInitRes(bool, isWaitingRoomSupported, "none");
    SettingMethodsInitRes(bool, isVirtualBackgroundSupported, "none");
    SettingMethodsInitParamReq(param, setChatroomDefaultFileSavePath, "std::string，填入路径");
    SettingMethodsInitRes2(std::string, getChatroomDefaultFileSavePath, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), setGalleryModeMaxMemberCount, "int, 最大显示人数目前支持9人或者16人");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableUnmuteAudioByPressSpaceBar, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isUnmuteAudioByPressSpaceBarEnabled, "none");
    SettingMethodsInitRes(bool, isGuestJoinSupported, "none");
    SettingMethodsInitRes(bool, isTranscriptionSupported, "none");
    auto interpretationConfig_task = [](nem_sdk_interface::NEInterpretationConfig in) -> std::string {
        return "isSupported: " + std::to_string(in.isSupported) + ",maxInterpreters: " + std::to_string(in.maxInterpreters) +
               ",enableCustomLang: " + std::to_string(in.enableCustomLang) + ",maxCustomLangNameLen: " + std::to_string(in.maxCustomLangNameLen) +
               ",defMajorAudioVolume: " + std::to_string(in.defMajorAudioVolume);
    };
    SettingMethodsInitResOut(nem_sdk_interface::NEInterpretationConfig, getInterpretationConfig, "none", interpretationConfig_task(ret));
    auto scheduledMemberConfig_task = [](nem_sdk_interface::NEScheduledMemberConfig in) -> std::string {
        return "isSupported: " + std::to_string(in.isSupported) + ",scheduleMemberMax: " + std::to_string(in.scheduleMemberMax) +
               ",coHostLimit: " + std::to_string(in.coHostLimit);
    };
    SettingMethodsInitResOut(nem_sdk_interface::NEScheduledMemberConfig, getScheduledMemberConfig, "none", scheduledMemberConfig_task(ret));
    SettingMethodsInitRes(bool, isNicknameUpdateSupported, "none");
    SettingMethodsInitRes(bool, isAvatarUpdateSupported, "none");
    SettingMethodsInitRes(bool, isCaptionsSupported, "none");
    NEMeetingASRTranslationLanguage language = NEMeetingASRTranslationLanguage::kASRTranslationLanguageChinese;
    SettingMethodsInitParamReq(static_cast<NEMeetingASRTranslationLanguage>(std::atoi(param.c_str())), setASRTranslationLanguage, "0, 1, 2, 3");
    auto aSRTranslationLanguage_task = [](nem_sdk_interface::NEMeetingASRTranslationLanguage in) -> std::string {
        return "NEMeetingASRTranslationLanguage: " + static_cast<int>(in);
    };
    SettingMethodsInitResOut(nem_sdk_interface::NEMeetingASRTranslationLanguage, getASRTranslationLanguage,"none", aSRTranslationLanguage_task(ret));
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableCaptionBilingual, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isCaptionBilingualEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableTranscriptionBilingual, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isTranscriptionBilingualEnabled, "none");

    SettingMethodsInitRes(bool, isMeetingChatSupported, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableShowNotYetJoinedMembers, "bool, 填入 1:true, 0:false");
    SettingMethodsInitRes(bool, isShowNotYetJoinedMembersEnabled, "none");
    SettingMethodsInitParamReq(static_cast<NEChatMessageNotificationType>(std::atoi(param.c_str())), setChatMessageNotificationType, "0, 1, 2");
    auto chatMessageNotificationType_task = [](nem_sdk_interface::NEChatMessageNotificationType in) -> std::string {
        return "NEChatMessageNotificationType: " + static_cast<int>(in);
    };
    SettingMethodsInitResOut(nem_sdk_interface::NEChatMessageNotificationType, getChatMessageNotificationType,"none", chatMessageNotificationType_task(ret));
    SettingMethodsInitRes(bool, isShowNameInVideoEnabled, "none");
    SettingMethodsInitParamReq(std::atoi(param.c_str()), enableShowNameInVideo, "bool, 填入 1:true, 0:false");
    QStringList methodList;
    for (const auto& method : m_setting_methods) {
        methodList.append(method.second.name);
    }
    QStringListModel* settingModel = new QStringListModel(methodList);
    ui->setting_list_methods->setModel(settingModel);
}

void MainWindow::updateSettingParameterNoteTextEdit(const QModelIndex& index) {
    auto settingMethods = m_setting_methods.find(index.row());
    if (settingMethods == m_setting_methods.end()) {
        PrintLog("Setting Method is not found");
        return;
    }
    ui->setting_edit_note->setPlainText(settingMethods->second.paramNote);
}
void MainWindow::onSettingRunBtnClicked() {
    auto settingMethods = m_setting_methods.find(ui->setting_list_methods->currentIndex().row());
    if (settingMethods == m_setting_methods.end()) {
        PrintLog("Setting Method is not found, run error");
        return;
    }
    std::string param = ui->setting_edit_param->toPlainText().toStdString();
    settingMethods->second.task(param);
}