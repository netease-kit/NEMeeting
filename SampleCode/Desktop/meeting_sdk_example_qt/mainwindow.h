#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QJsonArray>
#include <QJsonDocument>
#include <QJsonObject>
#include <QListView>
#include <QMainWindow>
#include <QTextEdit>
#include "kit_service_meeting_reflection.h"
#include "kit_service_premeeting_reflection.h"
#include "nemeeting_sdk_manager.h"
#include <QThread>

QT_BEGIN_NAMESPACE
namespace Ui {
class MainWindow;
}
QT_END_NAMESPACE

static const char* kMeetingServiceModule = "meeting";
static const char* kPreMeetingServiceModule = "premeeting";

class MainWindow : public QMainWindow,
                   public NEGlobalEventListener,
                   public NEAccountServiceListener,
                   public NEMeetingStatusListener,
                   public NEMeetingOnInjectedMenuItemClickListener,
                   public NEPreMeetingListener,
                   public NEMeetingMessageChannelListener,
                   public NEMeetingInviteStatusListener,
                   public NEMeetingRealtimeRecorderStatusListener,
                   public NEMeetingWebAppClickListener {
    Q_OBJECT

public:
    using MethodRunner = std::function<void(const QJsonObject&)>;
    struct Method {
        QJsonObject defaultParameters;
        MethodRunner runner;
    };

    MainWindow(QWidget* parent = nullptr, QString appPath = "");
    ~MainWindow() final;
    void InitSlots();
    void InitUI();
    void PrintLogQStr(const QString& msg, int type);
    void PrintLog(const std::string& msg);

    void InitSettingUI();
                     
    QString getRunPath();
signals:
    void hawkMessageSignal(const QString& msg, const std::function<void(QString)>& responseCallback);
                                        
private:
    QString runPath;
    
private slots:
    void DoPrintLog(const QString& msg, int type);
    void onClearLogBtnClicked();
    void onClearIPCLogBtnClicked();
    void onLoglevelChanged(int index);
    void onInitBtnClicked();
    void onUnInitBtnClicked();
    void onIsInitBtnClicked();
    void onAddEventListenerBtnClicked();
    void onRemoveEventListenerBtnClicked();
    void onGetLogPathBtnClicked();
    void onSetLanguageBtnClicked();
    void onGetAppNoticeBtnClicked();
    // account service ------------------------------------------------------------
    void onLogoutBtnClicked();
    void onAutoLoginBtnClicked();
    void onGetAccountInfoBtnClicked();
    void onAddAccountServiceListenerBtnClicked();
    void onRemoveAccountServiceListenerBtnClicked();
    void onLoginPwdBtnClicked();
    void onLoginByTokenBtnClicked();
    void onLoginByPhonePwdBtnClicked();
    void onLoginByEmailPwdBtnClicked();
    void onRequestSmsCodeBtnClicked();
    void onRequestGuestSmsCodeBtnClicked();
    void onLoginByPhoneSmsCodeBtnClicked();
    void onGetLoginUrlBtnClicked();
    void onLoginByURIResultBtnClicked();
    void onUpdateAvatarBtnClicked();
    void onUpdateNickBtnClicked();
    void onResetPasswordBtnClicked();
    // premeeting service ------------------------------------------------------------
    void initPremeetingServiceMethods();
    void onPremeetingRunBtnClicked();
    // meeting service ------------------------------------------------------------
    void initMeetingServiceMethods();
    void onMeetingRunBtnClicked();
    void updateParameterTextEdit(const std::string& module, const QListView* listView, const QModelIndex& index, QTextEdit* textEdit);
    // invite service ------------------------------------------------------------
    void onAcceptInviteBtnClicked();
    void onRejectInviteBtnClicked();
    void onAddInviteListenerBtnClicked();
    void onRemoveInviteListenerBtnClicked();
    // contacts service ------------------------------------------------------------
    void onSearchContactsByPhoneBtnClicked();
    void onSearchContactsByNameBtnClicked();
    void onGetContactsListBtnClicked();
    // message service ------------------------------------------------------------
    void onAddMessageListenerBtnClicked();
    void onRemoveMessageListenerBtnClicked();
    void onQueryUnreadMessageListBtnClicked();
    void onClearUnreadCountBtnClicked();
    void onDeleteMessageBtnClicked();
    void onQueryMessageHistoryBtnClicked();
    // setting service ------------------------------------------------------------
    void updateSettingParameterNoteTextEdit(const QModelIndex& index);
    void onSettingRunBtnClicked();
    // feedback service ------------------------------------------------------------
    void onSubmitFeedbackBtnClicked();
    void onLoadFeedbackViewBtnClicked();
                       
    void onRealtimeRecorderStartBtnClicked();
    void onRealtimeRecorderStopBtnClicked();
                       
    void onRealtimeRecorderAddListenerBtnClicked();
    void onRealtimeRecorderRemoveListenerBtnClicked();
                       
    void onWebAppGetWebAppListBtnClicked();
    void onWebAppEnableWebAppBtnClicked();
    void onWebAppDisableWebAppBtnClicked();
    void onWebAppSetWebAppClickListenerBtnClicked();

private:
    void beforeRtcEngineInitializeWithRoomUuid(const std::string& roomUuid) override;
    void afterRtcEngineInitializeWithRoomUuid(const std::string& roomUuid) override;
    void beforeRtcEngineReleaseWithRoomUuid(const std::string& roomUuid) override;
    void onKickOut() override;
    void onAuthInfoExpired() override;
    void onReconnected() override;
    void onAccountInfoUpdated(NEAccountInfo accountInfo) override;
    void onMeetingStatusChanged(const Event& event) override;
    void onInjectedMenuItemClick(NEMenuClickInfoPtr clickInfo, const NEMeetingInfo& meetingInfo) override;
    void onMeetingInviteStatusChanged(NEMeetingInviteStatus status, int meetingId, NEMeetingInviteInfo inviteInfo) override;
    void onMeetingItemInfoChanged(const std::list<NEMeetingItem>& meetingItemList) override;

    void onSessionMessageReceived(const NEMeetingSessionMessage& message) override;
    void onSessionMessageRecentChanged(const std::list<NEMeetingRecentSession>& messages) override;
    void onSessionMessageDeleted(const NEMeetingSessionMessage& message) override;
    void onSessionMessageAllDeleted(const std::string& sessionId, const NEMeetingSessionType& sessionType) override;
                       
    void onMeetingRealtimeRecorderStatusChanged(NEMeetingRealtimeRecorderEvent event) override;
    bool onClickWebAppIcon(const NEWebAppClickInfo& clickInfo, const NEMeetingInfo& meetingInfo) override;

private:
    void addInterface(const QString& module, const QString& interface, const QJsonObject& defaultParameters, const MethodRunner& runner);
    QJsonObject getJsonObject(const std::string& parameters) {
        QByteArray byteArray = QByteArray::fromStdString(parameters);
        QJsonDocument doc = QJsonDocument::fromJson(byteArray.data());
        return doc.object();
    }

    QString objectToString(const QJsonObject& object) {
        QJsonDocument doc(object);
        return doc.toJson(QJsonDocument::Indented);
    }

private:
    using MethodMap = std::map<QString, std::map<QString, Method>>;
    Ui::MainWindow* ui;
    QString m_appPath;
    int log_level = 1;  // NELogLevel::LOG_LEVEL_INFO;
    MethodMap m_methods;
    //   NEMWrapperMeetingKit* m_NEMeetingSDKManager = nullptr;

    // services
    NEMeetingService* m_meetingService{nullptr};
    NEPreMeetingService* m_premeetingService{nullptr};
    NECheckableMenuItemPtr m_checkableMenuItemPtr;
    NESingleStateMenuItemPtr m_singleStateMenuItemPtr;
    struct MethodInfo {
        QString name;
        QString paramNote;
        std::function<void(std::string)> task;
    };
    std::map<int, MethodInfo> m_setting_methods;
};
#endif  // MAINWINDOW_H
