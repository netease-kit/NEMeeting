#include <QStringListModel>
#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "xpack_specialization.h"
#include "sdk/include/kit_define_meeting.h"

#include <memory>
#include <vector>
#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// meeting service ------------------------------------------------------------

void MainWindow::initMeetingServiceMethods() {
    m_meetingService = NEMeetingKit::getInstance()->getMeetingService();
    if (!m_meetingService)
        return;
    m_meetingService->addMeetingStatusListener(this);
    m_meetingService->setOnInjectedMenuItemClickListener(this);
    // addInterface(kMeetingServiceModule, "addListener")

    QJsonObject startMeetingParameters;
    NEStartMeetingParams neStartMeetingParams = NEStartMeetingParams();
    neStartMeetingParams.displayName = "123456";
    startMeetingParameters["param"] = getJsonObject(xpack::json::encode(neStartMeetingParams));

    NEStartMeetingOptions startMeetingOptions = NEStartMeetingOptions();
    std::vector<std::shared_ptr<NEMeetingMenuItem>> fullMoreMenuItems;
    NEMenuItemInfo singleStateItem = {"xx", "自定义单状态"};
    m_singleStateMenuItemPtr = std::make_shared<NESingleStateMenuItem>(102, NEMenuVisibility::VISIBLE_ALWAYS, singleStateItem);
    fullMoreMenuItems.push_back(m_singleStateMenuItemPtr);
    NEMenuItemInfo checkedStateItem = {"xx", "选中状态"};
    NEMenuItemInfo uncheckStateItem = {"xx", "未选中状态"};
    m_checkableMenuItemPtr = std::make_shared<NECheckableMenuItem>(103, NEMenuVisibility::VISIBLE_ALWAYS,checkedStateItem, uncheckStateItem);
    fullMoreMenuItems.push_back(m_checkableMenuItemPtr);
    startMeetingOptions.fullMoreMenuItems = fullMoreMenuItems;
    std::vector<NEMeetingMenuItemPtr> fullToolbarMenuItems;
    fullToolbarMenuItems.push_back(nem_sdk_interface::NEMeetingMenuItems::micMenu());
    std::vector<NEMeetingMenuItemPtr> memberActionMenuItems;
    memberActionMenuItems.push_back(nem_sdk_interface::NEActionMenuItems::audio());
    startMeetingOptions.memberActionMenuItems = memberActionMenuItems;
//    fullToolbarMenuItems.push_back(nem_sdk_interface::NEMenuItems::cameraMenu());
//    fullToolbarMenuItems.push_back(nem_sdk_interface::NEMenuItems::screenShareMenu());
//    fullToolbarMenuItems.push_back(nem_sdk_interface::NEMenuItems::participantsMenu());
//    fullToolbarMenuItems.push_back(nem_sdk_interface::NEMenuItems::managerParticipantsMenu());
    startMeetingOptions.fullToolbarMenuItems = fullToolbarMenuItems;
    startMeetingParameters["opts"] = getJsonObject(xpack::json::encode(startMeetingOptions));
    addInterface(kMeetingServiceModule, "startMeeting", startMeetingParameters, [this](const QJsonObject& parameters) {
        NEStartMeetingParams param;
        QByteArray byteArray = objectToString(parameters["param"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), param);
        NEStartMeetingOptions opts;
        byteArray = objectToString(parameters["opts"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), opts);
        m_meetingService->startMeeting(param, opts, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK startMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    QJsonObject joinMeetingParameters;
    NEJoinMeetingParams neJoinMeetingParams = NEJoinMeetingParams();
    neJoinMeetingParams.displayName = "123456";
    joinMeetingParameters["param"] = getJsonObject(xpack::json::encode(neJoinMeetingParams));
    auto joinMeetingOptions = NEJoinMeetingOptions();
    joinMeetingOptions.fullMoreMenuItems = fullMoreMenuItems;
    joinMeetingOptions.fullToolbarMenuItems = fullToolbarMenuItems;
    joinMeetingParameters["opts"] = getJsonObject(xpack::json::encode(joinMeetingOptions));
    addInterface(kMeetingServiceModule, "joinMeeting", joinMeetingParameters, [this](const QJsonObject& parameters) {
        NEJoinMeetingParams param;
        QByteArray byteArray = objectToString(parameters["param"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), param);
        NEJoinMeetingOptions opts;
        byteArray = objectToString(parameters["opts"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), opts);
        m_meetingService->joinMeeting(param, opts, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK joinMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    QJsonObject anonymousJoinMeetingParameters;
    NEStartMeetingParams neAnonymousJoinMeetingParams = NEStartMeetingParams();
    neAnonymousJoinMeetingParams.displayName = "123456";
    anonymousJoinMeetingParameters["param"] = getJsonObject(xpack::json::encode(neAnonymousJoinMeetingParams));
    auto anonymousOptions = NEStartMeetingOptions();
    anonymousOptions.fullMoreMenuItems = fullMoreMenuItems;
    anonymousOptions.fullToolbarMenuItems = fullToolbarMenuItems;
    anonymousJoinMeetingParameters["opts"] = getJsonObject(xpack::json::encode(anonymousOptions));
    addInterface(kMeetingServiceModule, "anonymousJoinMeeting", anonymousJoinMeetingParameters, [this](const QJsonObject& parameters) {
        NEJoinMeetingParams param;
        QByteArray byteArray = objectToString(parameters["param"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), param);
        NEJoinMeetingOptions opts;
        byteArray = objectToString(parameters["opts"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), opts);
        m_meetingService->anonymousJoinMeeting(param, opts, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK anonymousJoinMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });

    // void leaveCurrentMeeting(bool closeIfHost, const NELeaveMeetingCallback& callback)
    QJsonObject leaveCurrentMeetingParameters;
    leaveCurrentMeetingParameters["closeIfHost"] = false;
    addInterface(kMeetingServiceModule, "leaveCurrentMeeting", leaveCurrentMeetingParameters, [this](const QJsonObject& parameters) {
        bool closeIfHost = parameters["closeIfHost"].toBool();
        m_meetingService->leaveCurrentMeeting(closeIfHost, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK leaveCurrentMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    // void getCurrentMeetingInfo(const NEGetMeetingInfoCallback& callback)
    addInterface(kMeetingServiceModule, "getCurrentMeetingInfo", QJsonObject(), [this](const QJsonObject& parameters) {
        m_meetingService->getCurrentMeetingInfo([this](
                                                    MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingInfo& meetingInfo) {
            PrintLogQStr(QString("[MeetingService] getCurrentMeetingInfo errorCode: %1, errorMessage: %2, meetingInfo: %3")
                             .arg(errorCode)
                             .arg(errorMessage.c_str())
                             .arg(xpack::json::encode(meetingInfo).c_str()),
                0);
        });
    });
    // void getMeetingStatus(const NEGetMeetingStatusCallback& callback)
    addInterface(kMeetingServiceModule, "getMeetingStatus", QJsonObject(), [this](const QJsonObject& parameters) {
        m_meetingService->getMeetingStatus([this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingStatus& meetingStatus) {
            PrintLogQStr(QString("[MeetingService] getMeetingStatus errorCode: %1, errorMessage: %2, meetingStatus: %3")
                             .arg(errorCode)
                             .arg(errorMessage.c_str())
                             .arg(meetingStatus),
                0);
        });
    });
    // void updateInjectedMenuItem(const NEMeetingMenuItem& menu_item, const NEEmptyCallback& callback)
    QJsonObject updateInjectedMenuItemParameters;
    updateInjectedMenuItemParameters["item"] = getJsonObject(xpack::json::encode(NEMeetingMenuItem()));
    addInterface(kMeetingServiceModule, "updateInjectedMenuItem", updateInjectedMenuItemParameters, [this](const QJsonObject& parameters) {
        NEMeetingMenuItemPtr menuItem = std::make_shared<NEMeetingMenuItem>();
        QByteArray byteArray = objectToString(parameters["menu_item"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), menuItem);
        m_meetingService->updateInjectedMenuItem(menuItem, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK updateInjectedMenuItem errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    // getLocalHistoryMeetingList(const NEGetLocalMeetingHistoryListCallback& callback)
    addInterface(kMeetingServiceModule, "getLocalHistoryMeetingList", QJsonObject(), [this](const QJsonObject& parameters) {
        m_meetingService->getLocalHistoryMeetingList(
            [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NELocalHistoryMeeting>& meetingList) {
                PrintLogQStr(QString("[MeetingService] getLocalHistoryMeetingList errorCode: %1, errorMessage: %2, meetingList: %3")
                                 .arg(errorCode)
                                 .arg(errorMessage.c_str())
                                 .arg(xpack::json::encode(meetingList).c_str()),
                    0);
            });
    });
    const auto& meetingMethods = m_methods.find(kMeetingServiceModule);
    QStringList methodList;
    for (const auto& method : meetingMethods->second) {
        methodList.append(method.first);
    }
    QStringListModel* meetingModel = new QStringListModel(methodList);
    ui->m_meetingMethods->setModel(meetingModel);
    ui->m_meetingMethods->setSpacing(2);

    connect(ui->m_meetingMethods, &QListView::clicked, [this](const QModelIndex& index) {
        updateParameterTextEdit(kMeetingServiceModule, ui->m_meetingMethods, index, ui->m_meetingParameters);
    });
}

void MainWindow::onMeetingRunBtnClicked() {
    const auto meetingMethods = m_methods.find(kMeetingServiceModule);
    if (meetingMethods == m_methods.end()) {
        PrintLog("Meeting service is not initialized");
        return;
    }
    // 获取 QListView m_meetingMethods 当前选择项的文本
    const auto& methodName = ui->m_meetingMethods->currentIndex().data().toString();
    const auto& methodParameters = ui->m_meetingParameters->toPlainText();
    const auto& method = meetingMethods->second.find(methodName);
    if (method == meetingMethods->second.end()) {
        PrintLog("Method is not found");
        return;
    }
    QByteArray byteArray = methodParameters.toUtf8();
    method->second.runner(getJsonObject(byteArray.data()));
}

void MainWindow::onMeetingStatusChanged(const Event& event) {
    PrintLog("Meeting status changed, event: " + xpack::json::encode(event));
}

void MainWindow::onInjectedMenuItemClick(NEMenuClickInfoPtr clickInfoPtr,
    const NEMeetingInfo& meetingInfo) {
    // 把clickInfo强转成 NEStatefulMenuClickInfo
//    NEStatefulMenuClickInfoPtr statefulClickInfo = std::dynamic_pointer_cast<NEStatefulMenuClickInfo>(clickInfoPtr);
//    if(!statefulClickInfo) {
//        PrintLog("Meeting injected menu item clicked, clickInfo is not NEStatefulMenuClickInfo");
//        return;
//    };
    NEMeetingMenuItemPtr itemPtr = std::make_shared<NEMeetingMenuItem>();
    itemPtr->itemId = clickInfoPtr->itemId;
    itemPtr->visibility = NEMenuVisibility::VISIBLE_ALWAYS;
    if (m_singleStateMenuItemPtr && itemPtr->itemId == m_singleStateMenuItemPtr->itemId) {
        itemPtr = m_singleStateMenuItemPtr;
        PrintLog(
            "Meeting injected menu item clicked, clickInfo: " + xpack::json::encode(*clickInfoPtr) + ", meetingInfo: " + xpack::json::encode(meetingInfo));
    } else if (m_checkableMenuItemPtr && itemPtr->itemId == m_checkableMenuItemPtr->itemId) {
        m_checkableMenuItemPtr->checked = !m_checkableMenuItemPtr->checked;
        itemPtr = m_checkableMenuItemPtr;
        PrintLog(
            "Meeting injected menu item clicked, clickInfo: " + xpack::json::encode(*clickInfoPtr) + ", meetingInfo: " + xpack::json::encode(meetingInfo));
    }
    m_meetingService->updateInjectedMenuItem(itemPtr, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
        PrintLog("MeetingSDK updateInjectedMenuItem errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
    });
}
