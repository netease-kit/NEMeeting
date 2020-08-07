#include "nemeeting_manager.h"
#include <QGuiApplication>

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
            ipcMeetingService->addListener(this);
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
            getPersonalMeetingId();
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

void NEMeetingManager::invokeStart(const QString &meetingId, const QString &nickname, bool audio, bool video)
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
        ipcMeetingService->startMeeting(params, options, [this](NEErrorCode errorCode, const std::string& errorMessage) {
            qInfo() << "Start meeting callback, error code: " << errorCode << ", error message: " << QString::fromStdString(errorMessage);
            emit startSignal(errorCode, QString::fromStdString(errorMessage));
        });
    }
}

void NEMeetingManager::invokeJoin(const QString &meetingId, const QString &nickname, bool audio, bool video)
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

void NEMeetingManager::onMeetingStatusChanged(int status, int code)
{
    qInfo() << "Meeting status changed, status:" << status << ", code:" << code;
    emit meetingStatusChanged(status, code);
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
