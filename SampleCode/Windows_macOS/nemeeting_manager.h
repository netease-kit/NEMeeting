// Copyright (c) 2014-2020 NetEase, Inc.
// All right reserved.

#ifndef NEMEETINGMANAGER_H
#define NEMEETINGMANAGER_H

#include <QObject>
#include <QDebug>
#include "nemeeting_sdk_interface_include.h"

USING_NS_NNEM_SDK_INTERFACE

class MeetingsStatus : public QObject
{
    Q_GADGET
public:
    explicit MeetingsStatus() {}
    enum Status {
        ERROR_CODE_SUCCESS = 0,
        ERROR_CODE_FAILED = -1,
        MEETING_ERROR_FAILED_IM_LOGIN_ERROR = -2,
        MEETING_ERROR_NO_NETWORK = -3,
        MEETING_ERROR_FAILED_MEETING_ALREADY_EXIST = -4,
        MEETING_ERROR_FAILED_PARAM_ERROR = -5,
        MEETING_ERROR_ALREADY_INMEETING = -6,
        MEETING_ERROR_FAILED_LOGIN_ON_OTHER_DEVICE = -7,
        MEETING_ERROR_LOCKED_BY_HOST = -8,
        MEETING_ERROR_INVALID_ID = -9,
        MEETING_ERROR_LIMITED = -10
    };
    Q_ENUM(Status)
};

class RunningStatus : public QObject
{
    Q_GADGET
public:
    explicit RunningStatus() {}
    enum Status {
        MEETING_STATUS_FAILED = MEETING_STATUS_FAILED,
        MEETING_STATUS_IDLE,
        MEETING_STATUS_WAITING,
        MEETING_STATUS_CONNECTING,
        MEETING_STATUS_INMEETING,
        MEETING_STATUS_DISCONNECTING,
    };
    Q_ENUM(Status)
    enum ExtStatus {
        MEETING_DISCONNECTING_BY_SELF = 0,
        MEETING_DISCONNECTING_REMOVED_BY_HOST,
        MEETING_DISCONNECTING_CLOSED_BY_HOST,
        MEETING_DISCONNECTING_LOGIN_ON_OTHER_DEVICE,
        MEETING_DISCONNECTING_CLOSED_BY_SELF_AS_HOST,
        MEETING_DISCONNECTING_BY_SERVER,
        MEETING_WAITING_VERIFY_PASSWORD
    };
    Q_ENUM(ExtStatus)
};

enum MoreItemSubMenuIndex
{
    kFirstSubmenu = NEM_MORE_MENU_USER_INDEX + 1,
    kSecondSubmenu,
    kThirdSubmue
};

class NEMeetingManager
        : public QObject
        , public NEMeetingStatusListener
        , public NEMeetingOnInjectedMenuItemClickListener
        , public NEScheduleMeetingStatusListener
        , public NESettingsChangeNotifyHandler
{
    Q_OBJECT
public:
    explicit NEMeetingManager(QObject *parent = nullptr);

    Q_PROPERTY(QString personalMeetingId READ personalMeetingId WRITE setPersonalMeetingId NOTIFY personalMeetingIdChanged)

    Q_INVOKABLE void initialize();
    Q_INVOKABLE void unInitialize();
    Q_INVOKABLE bool isInitializd();
    Q_INVOKABLE void login(const QString& appKey, const QString& accountId, const QString& accountToken);
    Q_INVOKABLE void getAccountInfo();
    Q_INVOKABLE void logout();
    Q_INVOKABLE void showSettings();

    Q_INVOKABLE void scheduleMeeting(const QString& meetingSubject, qint64 startTime, qint64 endTime, const QString& password, bool attendeeAudioOff);
    Q_INVOKABLE void cancelMeeting(const qint64& meetingUniqueId);
    Q_INVOKABLE void getMeetingList();
    Q_INVOKABLE void invokeStart(const QString& meetingId, const QString& nickname, bool audio, bool video,
                                 bool enableChatroom = true, bool enableInvitation = true);
    Q_INVOKABLE void invokeJoin(const QString& meetingId, const QString& nickname, bool audio, bool video,
                                bool enableChatroom = true, bool enableInvitation = true);
    Q_INVOKABLE void leaveMeeting(bool finish);
    Q_INVOKABLE int getMeetingStatus();
    Q_INVOKABLE void getMeetingInfo();

    // override virtual functions
    virtual void onMeetingStatusChanged(int status, int code) override;
    virtual void onInjectedMenuItemClick(const NEMeetingMenuItem &meeting_menu_item) override;
    virtual void onScheduleMeetingStatusChanged(uint64_t uniqueMeetingId, const int& meetingStatus) override;

    // properties
    QString personalMeetingId() const;
    void setPersonalMeetingId(const QString& personalMeetingId);

    virtual void OnAudioSettingsChange(bool status) override;
    virtual void OnVideoSettingsChange(bool status) override;
    virtual void OnOtherSettingsChange(bool status) override;

private:
    void pushSubmenus(std::vector<NEMeetingMenuItem>& items_list);

signals:
    void error(int errorCode, const QString& errorMessage);
    void initializeSignal(int errorCode, const QString& errorMessage);
    void unInitializeSignal(int errorCode, const QString& errorMessage);
    void loginSignal(int errorCode, const QString& errorMessage);
    void logoutSignal(int errorCode, const QString& errorMessage);
    void showSettingsSignal(int errorCode, const QString& errorMessage);
    void startSignal(int errorCode, const QString& errorMessage);
    void joinSignal(int errorCode, const QString& errorMessage);
    void leaveSignal(int errorCode, const QString& errorMessage);
    void getCurrentMeetingInfo(const QString& meetingId, bool isHost, bool isLocked, qint64 duration);
    void meetingStatusChanged(int meetingStatus, int extCode);
    void meetingInjectedMenuItemClicked(int itemIndex, const QString& itemGuid, const QString& itemTitle, const QString& itemImagePath);
    void personalMeetingIdChanged();
    void scheduleSignal(int errorCode, const QString& errorMessage);
    void cancelSignal(int errorCode, const QString& errorMessage);
    void getScheduledMeetingList(int errorCode, const QJsonArray& meetingList);
    void deviceStatusChanged(int type, bool status);

public slots:
    void onGetMeetingListUI();

    bool checkAudio();
    void setCheckAudio(bool checkAudio);

    bool checkVideo();
    void setCheckVideo(bool checkVideo);

private:
    std::atomic_bool    m_initialized;
    QString             m_personalMeetingId;
};

#endif // NEMEETINGMANAGER_H
