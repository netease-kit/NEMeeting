#include "nemeeting_manager.h"
#include <QGuiApplication>
#include <QDesktopServices>
#include <QUrl>
#include <QJsonObject>
#include <QJsonArray>

NEMeetingManager::NEMeetingManager(QObject *parent)
    : QObject(parent)
    , m_initialized(false)
{
    connect(this, &NEMeetingManager::unInitializeSignal, this, []() {
        qApp->exit();
    });
}

void NEMeetingManager::initialize()
{
    if (m_initialized){
        emit initializeSignal(0, "");
        return;
    }

    NEMeetingSDKConfig config;
    QString displayName = QObject::tr("NetEase Meeting");
    QByteArray byteDisplayName = displayName.toUtf8();
    config.setLogSize(10);
    config.getAppInfo()->ProductName(byteDisplayName.data());
    config.getAppInfo()->OrganizationName("NetEase");
    config.getAppInfo()->ApplicationName("Meeting");
    config.setDomain("yunxin.163.com");
    NEMeetingSDK::getInstance()->initialize(config, [this](NEErrorCode errorCode, const std::string& errorMessage) {
        qInfo() << "Initialize callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
        emit initializeSignal(errorCode, QString::fromStdString(errorMessage));
        auto ipcMeetingService = NEMeetingSDK::getInstance()->getMeetingService();
        if (ipcMeetingService)
        {
            ipcMeetingService->addMeetingStatusListener(this);
            ipcMeetingService->setOnInjectedMenuItemClickListener(this);
        }
        auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
        if (ipcPreMeetingService)
        {
            ipcPreMeetingService->registerScheduleMeetingStatusListener(this);
        }
        m_initialized = true;
    });
}

void NEMeetingManager::unInitialize()
{
    qInfo() << "Do uninitialize, initialize flag: " << m_initialized;

    if (!m_initialized)
        return;

    NEMeetingSDK::getInstance()->setExceptionHandler(nullptr);
    NEMeetingSDK::getInstance()->unInitialize([&](NEErrorCode errorCode, const std::string& errorMessage) {
        qInfo() << "Uninitialize callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
        m_initialized = false;
        emit unInitializeSignal(errorCode, QString::fromStdString(errorMessage));
        qInfo() << "Uninitialized successfull";
    });
}

void NEMeetingManager::login(const QString& appKey, const QString &accountId, const QString &accountToken)
{
    qInfo() << "Login to apaas server, appkey: " << appKey << ", account ID: " << accountId << ", token: " << accountToken;

    auto ipcAuthService = NEMeetingSDK::getInstance()->getAuthService();
    if (ipcAuthService)
    {
        QByteArray byteAppKey = appKey.toUtf8();
        QByteArray byteAccountId = accountId.toUtf8();
        QByteArray byteAccountToken = accountToken.toUtf8();
        ipcAuthService->login(byteAppKey.data(), byteAccountId.data(), byteAccountToken.data(), [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Login callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit loginSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::logout()
{
    qInfo() << "Logout from apaas server";

    auto ipcAuthService = NEMeetingSDK::getInstance()->getAuthService();
    if (ipcAuthService)
    {
        ipcAuthService->logout([this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Logout callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit logoutSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::showSettings()
{
    qInfo() << "Post show settings request";

    auto ipcSettingsService = NEMeetingSDK::getInstance()->getSettingsService();
    if (ipcSettingsService)
    {
        ipcSettingsService->showSettingUIWnd(NESettingsUIWndConfig(), [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Show settings wnd callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit showSettingsSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::scheduleMeeting(const QString &meetingSubject, qint64 startTime, qint64 endTime, const QString &password, bool attendeeAudioOff)
{
    QString strPassword = password.isEmpty() ? "null" : "no null";
    auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
    if (ipcPreMeetingService)
    {
        NEMeetingItem item;
        item.subject = meetingSubject.toUtf8().data();
        item.startTime = startTime;
        item.endTime = endTime;
        item.password = password.toUtf8().data();
        item.setting.attendeeAudioOff = attendeeAudioOff;

        ipcPreMeetingService->scheduleMeeting(item, [this](NEErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& item) {
            qInfo() << "Schedule meeting callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit scheduleSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::cancelMeeting(const qint64 &meetingUniqueId)
{
    qInfo() << "cancel a meeting with meeting uniqueId:" << meetingUniqueId;

    auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
    if (ipcPreMeetingService)
    {
        ipcPreMeetingService->cancelMeeting(meetingUniqueId, [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Cancel meeting callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit cancelSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::getMeetingList()
{
    qInfo() << "Get meeting list from IPC client.";

    auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
    if (ipcPreMeetingService)
    {
        std::list<NEMeetingItemStatus> status;
        status.push_back(MEETING_INIT);
        status.push_back(MEETING_STARTED);
        status.push_back(MEETING_ENDED);
        ipcPreMeetingService->getMeetingList(status, [this](NEErrorCode errorCode, const std::string& errorMessage, std::list<NEMeetingItem>& meetingItems) {
            qInfo() << "GetMeetingList callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            QJsonArray jsonArray;
            if (errorCode == ERROR_CODE_SUCCESS)
            {
                for (auto& item : meetingItems)
                {
                    qInfo() << "Got meeting list, unique meeting ID: " << item.meetingUniqueId
                            << ", meeting ID: " << QString::fromStdString(item.meetingId)
                            << ", topic: " << QString::fromStdString(item.subject)
                            << ", start time: " << item.startTime
                            << ", end time: " << item.endTime
                            << ", create time: " << item.createTime
                            << ", update time: " << item.updateTime
                            << ", status: " << item.status
                            << ", mute after member join: " << item.setting.attendeeAudioOff;
                    QJsonObject object;
                    object["uniqueMeetingId"] = item.meetingUniqueId;
                    object["meetingId"] = QString::fromStdString(item.meetingId);
                    object["topic"] = QString::fromStdString(item.subject);
                    object["startTime"] = item.startTime;
                    object["endTime"] = item.endTime;
                    object["createTime"] = item.createTime;
                    object["updateTime"] = item.updateTime;
                    object["status"] = item.status;
                    object["attendeeAudioOff"] = item.setting.attendeeAudioOff;
                    jsonArray.push_back(object);
                }
                emit getScheduledMeetingList(errorCode, jsonArray);
            }
            else
            {
                emit getScheduledMeetingList(errorCode, jsonArray);
            }
        });
    }
}

void NEMeetingManager::invokeStart(const QString &meetingId, const QString &nickname, bool audio, bool video,
                                   bool enableChatroom/* = true*/, bool enableInvitation/* = true*/)
{
    qInfo() << "Start a meeting with meeting ID:" << meetingId << ", nickname: " << nickname << ", audio: " << audio << ", video: " << video;

    auto ipcMeetingService = NEMeetingSDK::getInstance()->getMeetingService();
    if (ipcMeetingService)
    {
        QByteArray byteMeetingId = meetingId.toUtf8();
        QByteArray byteNickname = nickname.toUtf8();

        NEStartMeetingParams params;
        params.meetingId = byteMeetingId.data();
        params.displayName = byteNickname.data();

        NEStartMeetingOptions options;
        options.noAudio = !audio;
        options.noVideo = !video;
        options.noChat = !enableChatroom;
        options.noInvite = !enableInvitation;

        pushSubmenus(options.injected_more_menu_items_);

        ipcMeetingService->startMeeting(params, options, [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Start meeting callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit startSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::invokeJoin(const QString &meetingId, const QString &nickname, bool audio, bool video,
                                  bool enableChatroom/* = true*/, bool enableInvitation/* = true*/)
{
    qInfo() << "Join a meeting with meeting ID:" << meetingId << ", nickname: " << nickname << ", audio: " << audio << ", video: " << video;

    auto ipcMeetingService = NEMeetingSDK::getInstance()->getMeetingService();
    if (ipcMeetingService)
    {
        QByteArray byteMeetingId = meetingId.toUtf8();
        QByteArray byteNickname = nickname.toUtf8();

        NEJoinMeetingParams params;
        params.meetingId = byteMeetingId.data();
        params.displayName = byteNickname.data();

        NEJoinMeetingOptions options;
        options.noAudio = !audio;
        options.noVideo = !video;
        options.noChat = !enableChatroom;
        options.noInvite = !enableInvitation;

        pushSubmenus(options.injected_more_menu_items_);

        ipcMeetingService->joinMeeting(params, options, [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Join meeting callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit joinSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::getPersonalMeetingId()
{
    qInfo() << "Post get personal meeting ID request";

    auto ipcAccountService = NEMeetingSDK::getInstance()->getAccountService();
    if (ipcAccountService)
    {
        ipcAccountService->getPersonalMeetingId([this](NEErrorCode errorCode, const std::string& errorMessage, const std::string& personalMeetingId) {
            qInfo() << "Get personal meeting ID callback, error code: " << errorCode << ", error message: "
                    << QString::fromStdString(errorMessage) << ", meeting ID: " << QString::fromStdString(personalMeetingId);
            if (errorCode == ERROR_CODE_SUCCESS)
            {
                setPersonalMeetingId(QString::fromStdString(personalMeetingId));
            }
            else
            {
                emit error(errorCode, QString::fromStdString(errorMessage));
            }
        });
    }
}

void NEMeetingManager::getMeetingInfo()
{
    auto ipcMeetingService = NEMeetingSDK::getInstance()->getMeetingService();
    if (ipcMeetingService)
    {
        ipcMeetingService->getCurrentMeetingInfo([this](NEErrorCode errorCode, const std::string& errorMessage, const NEMeetingInfo& meetingInfo) {
            if (errorCode == ERROR_CODE_SUCCESS)
                emit getCurrentMeetingInfo(QString::fromStdString(meetingInfo.meetingId), meetingInfo.isHost, meetingInfo.isLocked);
            else
                emit error(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::onMeetingStatusChanged(int status, int code)
{
    qInfo() << "Meeting status changed, status:" << status << ", code:" << code;
    emit meetingStatusChanged(status, code);
}

void NEMeetingManager::onInjectedMenuItemClick(const NEMeetingMenuItem &meeting_menu_item)
{
    qInfo() << "Meeting injected menu item clicked, item index: " << meeting_menu_item.itemId
            << ", guid: " << QString::fromStdString(meeting_menu_item.itemGuid)
            << ", title: " << QString::fromStdString(meeting_menu_item.itemTitle)
            << ", image path: " << QString::fromStdString(meeting_menu_item.itemImage);

    QDesktopServices::openUrl(QUrl(QString::fromStdString("https://www.google.com.hk/search?q=" + meeting_menu_item.itemTitle)));

    emit meetingInjectedMenuItemClicked(meeting_menu_item.itemId,
                                        QString::fromStdString(meeting_menu_item.itemGuid),
                                        QString::fromStdString(meeting_menu_item.itemTitle),
                                        QString::fromStdString(meeting_menu_item.itemImage));
}

void NEMeetingManager::onScheduleMeetingStatusChanged(uint64_t uniqueMeetingId, const int &meetingStatus)
{
    qInfo() << "Scheduled meeting status changed, unique meeting ID:" << uniqueMeetingId << meetingStatus;
    QMetaObject::invokeMethod(this, "onGetMeetingListUI", Qt::AutoConnection);
}

QString NEMeetingManager::personalMeetingId() const
{
    return m_personalMeetingId;
}

void NEMeetingManager::setPersonalMeetingId(const QString &personalMeetingId)
{
    m_personalMeetingId = personalMeetingId;
    emit personalMeetingIdChanged();
}

void NEMeetingManager::pushSubmenus(std::vector<NEMeetingMenuItem> &items_list)
{
    auto applicationPath = qApp->applicationDirPath();
    for (auto i = 0; i < 3; i++)
    {
        NEMeetingMenuItem item;
        item.itemId = NEM_MORE_MENU_USER_INDEX + i + 1;
        item.itemTitle = QString(QStringLiteral("Submenu") + QString::number(i + 1)).toStdString();
        item.itemImage = QString(applicationPath + "/icon_qr code.png").toStdString();
        items_list.push_back(item);
    }
}

void NEMeetingManager::onGetMeetingListUI()
{
    getMeetingList();
}
