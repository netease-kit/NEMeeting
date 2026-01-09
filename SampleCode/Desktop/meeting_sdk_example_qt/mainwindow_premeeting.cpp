#include <QStringListModel>
#include "mainwindow.h"
#include "stable.h"
#include "ui_mainwindow.h"

USING_NS_NNEM_SDK_INTERFACE

// premeeting service ------------------------------------------------------------

void MainWindow::initPremeetingServiceMethods() {
    m_premeetingService = NEMeetingKit::getInstance()->getPreMeetingService();
    if (!m_premeetingService)
        return;
    m_premeetingService->addListener(this);

    QJsonObject getFavoriteMeetingListParameters;
    getFavoriteMeetingListParameters["anchorId"] = 0;
    getFavoriteMeetingListParameters["limit"] = 10;
    addInterface(kPreMeetingServiceModule, "getFavoriteMeetingList", getFavoriteMeetingListParameters, [this](const QJsonObject& parameters) {
        int64_t anchorId = parameters["anchorId"].toInt();
        uint32_t limit = parameters["limit"].toInt();
        m_premeetingService->getFavoriteMeetingList(
            anchorId, limit, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NERemoteHistoryMeeting>& meetingList) {
                PrintLog("PremeetingService getFavoriteMeetingList errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         +", list: " + xpack::json::encode(meetingList));
            });
    });
    // void addFavoriteMeeting(int64_t meetingId, const NECallback<int64_t>& callback)
    QJsonObject addFavoriteMeetingParameters;
    addFavoriteMeetingParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "addFavoriteMeeting", addFavoriteMeetingParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->addFavoriteMeeting(meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, int64_t meetingId) {
            PrintLog("PremeetingService addFavoriteMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                     ", meetingId: " + std::to_string(meetingId));
        });
    });
    // void removeFavoriteMeeting(int64_t meetingId, const NECallback<>& callback)
    QJsonObject removeFavoriteMeetingParameters;
    removeFavoriteMeetingParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "removeFavoriteMeeting", removeFavoriteMeetingParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->removeFavoriteMeeting(meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("PremeetingService removeFavoriteMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    // void getHistoryMeetingList(int64_t anchorId, uint32_t limit, const NEGetHistoryMeetingListCallback& callback)
    QJsonObject getHistoryMeetingListParameters;
    getHistoryMeetingListParameters["anchorId"] = 0;
    getHistoryMeetingListParameters["limit"] = 10;
    addInterface(kPreMeetingServiceModule, "getHistoryMeetingList", getHistoryMeetingListParameters, [this](const QJsonObject& parameters) {
        int64_t anchorId = parameters["anchorId"].toInt();
        uint32_t limit = parameters["limit"].toInt();
        m_premeetingService->getHistoryMeetingList(
            anchorId, limit, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NERemoteHistoryMeeting>& meetingList) {
                PrintLog("PremeetingService getHistoryMeetingList errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", list: " + xpack::json::encode(meetingList));
            });
    });
    // void getHistoryMeetingDetail(int64_t meetingId, const NEGetHistoryMeetingDetailCallback& callback) override
    QJsonObject getHistoryMeetingDetailParameters;
    getHistoryMeetingDetailParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "getHistoryMeetingDetail", getHistoryMeetingDetailParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->getHistoryMeetingDetail(
            meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NERemoteHistoryMeetingDetail& details) {
                PrintLog("PremeetingService getHistoryMeetingDetail errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", details: " + xpack::json::encode(details));
            });
    });
    // void getHistoryMeeting(int64_t meetingId, const NEGetHistoryMeetingCallback& callback) override;
    QJsonObject getHistoryMeetingParameters;
    getHistoryMeetingParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "getHistoryMeeting", getHistoryMeetingParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->getHistoryMeeting(
            meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NERemoteHistoryMeeting& meeting) {
                if (kSuccess == errorCode) {
                    PrintLog("PremeetingService getHistoryMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                             ", meeting: " + xpack::json::encode(meeting));
                } else {
                    PrintLog("PremeetingService getHistoryMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
                }
            });
    });
    // NEMeetingItem createScheduleMeetingItem()
    addInterface(kPreMeetingServiceModule, "createScheduleMeetingItem", QJsonObject(), [this](const QJsonObject& parameters) {
        NEMeetingItem item = m_premeetingService->createScheduleMeetingItem();
        PrintLog("PremeetingService createScheduleMeetingItem meetingId: " + xpack::json::encode(item));
    });
    QJsonObject scheduleMeetingParameters;
    NEMeetingItem neMeetingItem = NEMeetingItem();
    // 获取当前时间戳
    qint64 currentTimestamp = QDateTime::currentMSecsSinceEpoch();
    // 设置会议开始时间为当前时间戳加 30 分钟
    neMeetingItem.startTime = 1720163365902;  // static_cast<time_t>(currentTimestamp + 30 * 60 * 1000);
    neMeetingItem.endTime = neMeetingItem.startTime + 30 * 60 * 1000;
    NEMeetingInterpretationSettings interpretationSettings = NEMeetingInterpretationSettings();
    interpretationSettings.addInterpreter("00998252ceef122a2f84e46c", "zh", "en");
    neMeetingItem.interpretationSettings = interpretationSettings;
    scheduleMeetingParameters["item"] = getJsonObject(xpack::json::encode(neMeetingItem));
    addInterface(kPreMeetingServiceModule, "scheduleMeeting", scheduleMeetingParameters, [this](const QJsonObject& parameters) {
        NEMeetingItem item;
        QByteArray byteArray = objectToString(parameters["item"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), item);
        m_premeetingService->scheduleMeeting(item, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& meetingItem) {
            PrintLog("PremeetingService scheduleMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                     ", meetingItem: " + xpack::json::encode(meetingItem));
        });
    });
    // void cancelMeeting(const int64_t& meetingId, bool cancelRecurringMeeting, const NEOperateScheduleMeetingCallback& callback)
    QJsonObject cancelMeetingParameters;
    cancelMeetingParameters["meetingId"] = 0;
    cancelMeetingParameters["cancelRecurringMeeting"] = false;
    addInterface(kPreMeetingServiceModule, "cancelMeeting", cancelMeetingParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        bool cancelRecurringMeeting = parameters["cancelRecurringMeeting"].toBool();
        m_premeetingService->cancelMeeting(meetingId, cancelRecurringMeeting, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& it) {
            PrintLog("PremeetingService cancelMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    // void editMeeting(const NEMeetingItem& item, bool editRecurringMeeting, const NEOperateScheduleMeetingCallback& callback)
    QJsonObject editMeetingParameters;
    editMeetingParameters["item"] = getJsonObject(xpack::json::encode(NEMeetingItem()));
    editMeetingParameters["editRecurringMeeting"] = false;
    addInterface(kPreMeetingServiceModule, "editMeeting", editMeetingParameters, [this](const QJsonObject& parameters) {
        NEMeetingItem item;
        QByteArray byteArray = objectToString(parameters["item"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), item);
        bool editRecurringMeeting = parameters["editRecurringMeeting"].toBool();
        m_premeetingService->editMeeting(item, editRecurringMeeting, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& it) {
            PrintLog("PremeetingService editMeeting errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });
    // void getMeetingItemByNum(const std::string& meetingNum, const NEGetMeetingItemByNumberCallback& callback)
    QJsonObject getMeetingItemByNumParameters;
    getMeetingItemByNumParameters["meetingNum"] = "";
    addInterface(kPreMeetingServiceModule, "getMeetingItemByNum", getMeetingItemByNumParameters, [this](const QJsonObject& parameters) {
        std::string meetingNum = parameters["meetingNum"].toString().toStdString();
        m_premeetingService->getMeetingItemByNum(
            meetingNum, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& meetingItem) {
                PrintLog("PremeetingService getMeetingItemByNum errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", meetingItem: " + xpack::json::encode(meetingItem));
            });
    });
    QJsonObject getMeetingItemByIdParameters;
    getMeetingItemByIdParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "getMeetingItemById", getMeetingItemByIdParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->getMeetingItemById(
            meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& meetingItem) {
                PrintLog("PremeetingService getMeetingItemById errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", meetingItem: " + xpack::json::encode(meetingItem));
            });
    });
    QJsonObject getMeetingListParameters;
    getMeetingListParameters["status"] = QJsonArray();
    addInterface(kPreMeetingServiceModule, "getMeetingList", getMeetingListParameters, [this](const QJsonObject& parameters) {
        std::list<NEMeetingItemStatus> status;
        for (const auto& item : parameters["status"].toArray()) {
            status.push_back(static_cast<NEMeetingItemStatus>(item.toInt()));
        }
        m_premeetingService->getMeetingList(
            status, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingItem>& meetingList) {
                PrintLog("PremeetingService getMeetingList errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", meetingList: " + xpack::json::encode(meetingList));
            });
    });
    // void getScheduledMeetingMemberList(const std::string& meetingNum, const NEGetScheduledMemberListCallback& callback)
    QJsonObject getScheduledMemberListParameters;
    getScheduledMemberListParameters["meetingNum"] = "";
    addInterface(kPreMeetingServiceModule, "getScheduledMeetingMemberList", getScheduledMemberListParameters, [this](const QJsonObject& parameters) {
        std::string meetingNum = parameters["meetingNum"].toString().toStdString();
        m_premeetingService->getScheduledMeetingMemberList(
            meetingNum, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEScheduledMember>& memberList) {
                PrintLog("PremeetingService getScheduledMeetingMemberList errorCode: " + std::to_string(errorCode) +
                         ", errorMessage: " + errorMessage + ", memberList: " + xpack::json::encode(memberList));
            });
    });
    //    QJsonObject getMeetingItemByInviteCodeParameters;
    //    getMeetingItemByInviteCodeParameters["item"] = getJsonObject(xpack::json::encode(NEMeetingItem()));
    //    addInterface(kPreMeetingServiceModule, "getMeetingItemByInviteCode", getMeetingItemByInviteCodeParameters, [this](const QJsonObject&
    //    parameters) {
    //        NEMeetingItem item;
    //        QByteArray byteArray = objectToString(parameters["item"].toObject()).toUtf8();
    //        xpack::json::decode(byteArray.data(), item);
    //        m_premeetingService->getMeetingItemByInviteCode(
    //            item, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& meetingItem) {
    //                PrintLog("PremeetingService getScheduledMeetingMemberList errorCode: " + std::to_string(errorCode) + ", errorMessage: " +
    //                errorMessage +
    //                         ", memberList: " + xpack::json::encode(meetingItem));
    //            });
    //    });

    QJsonObject getLocalHistoryMeetingListParameters;
    addInterface(
        kPreMeetingServiceModule, "getLocalHistoryMeetingList", getLocalHistoryMeetingListParameters, [this](const QJsonObject& parameters) {
            m_premeetingService->getLocalHistoryMeetingList(
                [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NELocalHistoryMeeting>& localHistoryMeetingList) {
                    PrintLog("PremeetingService getLocalHistoryMeetingList errorCode: " + std::to_string(errorCode) +
                             ", errorMessage: " + errorMessage + ", localHistoryMeetingList: " + xpack::json::encode(localHistoryMeetingList));
                });
        });
    QJsonObject getMeetingCloudRecordListParameters;
    getMeetingCloudRecordListParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "getMeetingCloudRecordList", getMeetingCloudRecordListParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        m_premeetingService->getMeetingCloudRecordList(
            meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingRecord>& recordList) {
                PrintLog("PremeetingService getMeetingCloudRecordList errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage +
                         ", recordList: " + xpack::json::encode(recordList));
            });
    });

    QJsonObject getHistoryMeetingTranscriptionInfoParameters;
    getHistoryMeetingTranscriptionInfoParameters["meetingId"] = 0;
    addInterface(kPreMeetingServiceModule, "getHistoryMeetingTranscriptionInfo", getHistoryMeetingTranscriptionInfoParameters,
        [this](const QJsonObject& parameters) {
            int64_t meetingId = parameters["meetingId"].toInt();
            m_premeetingService->getHistoryMeetingTranscriptionInfo(
                meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingTranscriptionInfo>& transcriptionInfoList) {
                    PrintLog("PremeetingService getHistoryMeetingTranscriptionInfo errorCode: " + std::to_string(errorCode) +
                             ", errorMessage: " + errorMessage + ", transcriptionInfoList: " + xpack::json::encode(transcriptionInfoList));
                });
        });

    QJsonObject getHistoryMeetingTranscriptionFileUrlFileKeyParameters;
    getHistoryMeetingTranscriptionFileUrlFileKeyParameters["meetingId"] = 0;
    getHistoryMeetingTranscriptionFileUrlFileKeyParameters["fileKey"] = "";
    addInterface(kPreMeetingServiceModule, "getHistoryMeetingTranscriptionFileUrl", getHistoryMeetingTranscriptionFileUrlFileKeyParameters,
        [this](const QJsonObject& parameters) {
            int64_t meetingId = parameters["meetingId"].toInt();
            std::string fileKey = parameters["fileKey"].toString().toStdString();
            m_premeetingService->getHistoryMeetingTranscriptionFileUrl(
                meetingId, fileKey, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::string& downloadUrl) {
                    PrintLog("PremeetingService getHistoryMeetingTranscriptionFileUrl fileKey errorCode: " + std::to_string(errorCode) +
                             ", errorMessage: " + errorMessage + ", downloadUrl: " + downloadUrl);
                });
        });

    QJsonObject loadWebAppViewParameters;
    loadWebAppViewParameters["meetingId"] = 0;
    loadWebAppViewParameters["meetingWebAppItem"] = getJsonObject(xpack::json::encode(NEMeetingWebAppItem()));
    addInterface(kPreMeetingServiceModule, "loadWebAppView", loadWebAppViewParameters, [this](const QJsonObject& parameters) {
        int64_t meetingId = parameters["meetingId"].toInt();
        NEMeetingWebAppItem meetingWebAppItem;
        QByteArray byteArray = objectToString(parameters["meetingWebAppItem"].toObject()).toUtf8();
        xpack::json::decode(byteArray.data(), meetingWebAppItem);
        m_premeetingService->loadWebAppView(meetingId, meetingWebAppItem, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("PremeetingService loadWebAppView errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    });

    QJsonObject fetchChatroomHistoryMessageListParameters;
    fetchChatroomHistoryMessageListParameters["meetingId"] = 0;
    fetchChatroomHistoryMessageListParameters["option"] = getJsonObject(xpack::json::encode(NEChatroomHistoryMessageSearchOption()));
    addInterface(kPreMeetingServiceModule, "fetchChatroomHistoryMessageList", fetchChatroomHistoryMessageListParameters,
        [this](const QJsonObject& parameters) {
            int64_t meetingId = parameters["meetingId"].toInt();
            NEChatroomHistoryMessageSearchOption option;
            QByteArray byteArray = objectToString(parameters["option"].toObject()).toUtf8();
            xpack::json::decode(byteArray.data(), option);
            m_premeetingService->fetchChatroomHistoryMessageList(meetingId, option,
                [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingChatMessage>& meetingChatMessageList) {
                    PrintLog("PremeetingService fetchChatroomHistoryMessageList errorCode: " + std::to_string(errorCode) +
                             ", errorMessage: " + errorMessage + ", meetingChatMessageList: " + xpack::json::encode(meetingChatMessageList));
                });
        });

    QJsonObject exportChatroomHistoryMessagesParameters;
    exportChatroomHistoryMessagesParameters["meetingId"] = 0;
    addInterface(
        kPreMeetingServiceModule, "exportChatroomHistoryMessageList", exportChatroomHistoryMessagesParameters, [this](const QJsonObject& parameters) {
            int64_t meetingId = parameters["meetingId"].toInt();
            m_premeetingService->exportChatroomHistoryMessageList(
                meetingId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::string& downloadUrl) {
                    PrintLog("PremeetingService exportChatroomHistoryMessageList errorCode: " + std::to_string(errorCode) +
                             ", errorMessage: " + errorMessage + ", downloadUrl: " + downloadUrl);
                });
        });

    QJsonObject clearLocalHistoryMeetingListParameters;
    addInterface(
        kPreMeetingServiceModule, "clearLocalHistoryMeetingList", clearLocalHistoryMeetingListParameters, [this](const QJsonObject& parameters) {
            m_premeetingService->clearLocalHistoryMeetingList([this](MeetingErrorCode errorCode, const std::string& errorMessage) {
                PrintLog(
                    "PremeetingService clearLocalHistoryMeetingList errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            });
        });

    const auto& meetingMethods = m_methods.find(kPreMeetingServiceModule);
    QStringList methodList;
    for (const auto& method : meetingMethods->second) {
        methodList.append(method.first);
    }
    QStringListModel* meetingModel = new QStringListModel(methodList);
    ui->m_premeetingMethods->setModel(meetingModel);
    ui->m_premeetingMethods->setSpacing(2);

    connect(ui->m_premeetingMethods, &QListView::clicked, [this](const QModelIndex& index) {
        updateParameterTextEdit(kPreMeetingServiceModule, ui->m_premeetingMethods, index, ui->m_premeetingParameters);
    });
}

void MainWindow::onPremeetingRunBtnClicked() {
    const auto meetingMethods = m_methods.find(kPreMeetingServiceModule);
    if (meetingMethods == m_methods.end()) {
        PrintLog("Premeeting service is not initialized");
        return;
    }
    // 获取 QListView m_premeetingMethods 当前选择项的文本
    const auto& methodName = ui->m_premeetingMethods->model()->data(ui->m_premeetingMethods->currentIndex()).toString();
    const auto& method = meetingMethods->second.find(methodName);
    if (method == meetingMethods->second.end()) {
        PrintLog("Method is not found");
        return;
    }
    const auto& parameters = ui->m_premeetingParameters->toPlainText();
    QByteArray byteArray = parameters.toUtf8();
    method->second.runner(getJsonObject(byteArray.data()));
}

void MainWindow::onMeetingItemInfoChanged(const std::list<NEMeetingItem>& meetingItemList) {
    PrintLog("" + xpack::json::encode(meetingItemList));
}
