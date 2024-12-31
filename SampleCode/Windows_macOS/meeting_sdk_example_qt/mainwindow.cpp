#include "mainwindow.h"

#include <QComboBox>
#include <QDebug>
#include <QTime>
#include <QVector>

#include "./ui_mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

MainWindow::MainWindow(QWidget* parent, QString appPath)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow) {
    m_appPath = appPath;
    ui->setupUi(this);
    //   m_NEMeetingSDKManager = new NEMWrapperMeetingKit();
    InitSlots();
    InitUI();
}

MainWindow::~MainWindow() {
    //   if (m_NEMeetingSDKManager) {
    //     delete m_NEMeetingSDKManager;
    //     m_NEMeetingSDKManager = nullptr;
    //   }
    delete ui;
}

void MainWindow::InitSlots() {
    connect(ui->tabWidget, &QTabWidget::currentChanged, this, [this](int index) {
        initPremeetingServiceMethods();
        initMeetingServiceMethods();
    });
    connect(ui->m_btn_clear_log, &QPushButton::clicked, this, &MainWindow::onClearLogBtnClicked);
    connect(ui->m_btn_clear_ipc_log, &QPushButton::clicked, this, &MainWindow::onClearIPCLogBtnClicked);
    connect(ui->m_cb_log_level, QOverload<int>::of(&QComboBox::currentIndexChanged), this, &MainWindow::onLoglevelChanged);
    connect(ui->m_init_btn, &QPushButton::clicked, this, &MainWindow::onInitBtnClicked);
    connect(ui->m_uninit_btn, &QPushButton::clicked, this, &MainWindow::onUnInitBtnClicked);
    connect(ui->m_btn_is_init, &QPushButton::clicked, this, &MainWindow::onIsInitBtnClicked);
//    connect(ui->m_btn_add_event_listener, &QPushButton::clicked, this, &MainWindow::onAddEventListenerBtnClicked);
//    connect(ui->m_btn_remove_event_listener, &QPushButton::clicked, this, &MainWindow::onRemoveEventListenerBtnClicked);
    connect(ui->m_btn_get_log_path, &QPushButton::clicked, this, &MainWindow::onGetLogPathBtnClicked);
    connect(ui->m_btn_set_language, &QPushButton::clicked, this, &MainWindow::onSetLanguageBtnClicked);
    ui->m_btn_set_language->setToolTip("Set language,0:auto,1:中文,2:English,3:Japanese");
    connect(ui->m_btn_get_app_notice, &QPushButton::clicked, this, &MainWindow::onGetAppNoticeBtnClicked);
    // account service
    connect(ui->m_btn_logout, &QPushButton::clicked, this, &MainWindow::onLogoutBtnClicked);
    connect(ui->m_btn_auto_login, &QPushButton::clicked, this, &MainWindow::onAutoLoginBtnClicked);
    ui->m_btn_auto_login->setToolTip("Try auto login");
    connect(ui->m_btn_account_info, &QPushButton::clicked, this, &MainWindow::onGetAccountInfoBtnClicked);
    ui->m_btn_account_info->setToolTip("Get account info");
    connect(ui->m_btn_account_add_listener, &QPushButton::clicked, this, &MainWindow::onAddAccountServiceListenerBtnClicked);
    ui->m_btn_account_add_listener->setToolTip("Add account service listener");
    connect(ui->m_btn_account_remove_listener, &QPushButton::clicked, this, &MainWindow::onRemoveAccountServiceListenerBtnClicked);
    ui->m_btn_account_remove_listener->setToolTip("Remove account service listener");
    connect(ui->m_btn_login_pwd, &QPushButton::clicked, this, &MainWindow::onLoginPwdBtnClicked);
    ui->m_btn_login_pwd->setToolTip("Login by account and password");
    connect(ui->m_btn_login_token, &QPushButton::clicked, this, &MainWindow::onLoginByTokenBtnClicked);
    ui->m_btn_login_token->setToolTip("Login by account(uuid) and token");
    connect(ui->m_btn_login_by_phone, &QPushButton::clicked, this, &MainWindow::onLoginByPhonePwdBtnClicked);
    ui->m_btn_login_by_phone->setToolTip("Login by phone number and password");
    connect(ui->m_btn_login_by_email, &QPushButton::clicked, this, &MainWindow::onLoginByEmailPwdBtnClicked);
    ui->m_btn_login_by_email->setToolTip("Login by account(email) and password");
    connect(ui->m_btn_get_sms, &QPushButton::clicked, this, &MainWindow::onRequestSmsCodeBtnClicked);
    ui->m_btn_get_sms->setToolTip("Request login sms code by phone number");
    connect(ui->m_btn_guest_sms, &QPushButton::clicked, this, &MainWindow::onRequestGuestSmsCodeBtnClicked);
    ui->m_btn_guest_sms->setToolTip("Request guest login sms code by phone number");
    connect(ui->m_btn_login_by_sms, &QPushButton::clicked, this, &MainWindow::onLoginByPhoneSmsCodeBtnClicked);
    ui->m_btn_login_by_sms->setToolTip("Login by phone number and sms code");
    connect(ui->m_btn_get_login_url, &QPushButton::clicked, this, &MainWindow::onGetLoginUrlBtnClicked);
    ui->m_btn_get_login_url->setToolTip("生成企业SSO登录链接");
    connect(ui->m_btn_login_by_uri, &QPushButton::clicked, this, &MainWindow::onLoginByURIResultBtnClicked);
    ui->m_btn_login_by_uri->setToolTip("Login by uri result(token edit)");
    connect(ui->m_btn_avatar_reset, &QPushButton::clicked, this, &MainWindow::onUpdateAvatarBtnClicked);
    ui->m_btn_avatar_reset->setToolTip("Update avatar");
    connect(ui->m_btn_nick_reset, &QPushButton::clicked, this, &MainWindow::onUpdateNickBtnClicked);
    ui->m_btn_nick_reset->setToolTip("Update nick");
    connect(ui->m_btn_pwd_reset, &QPushButton::clicked, this, &MainWindow::onResetPasswordBtnClicked);
    ui->m_btn_pwd_reset->setToolTip("Reset password");
    // premeeting service
    connect(ui->m_premeetingRun, &QPushButton::clicked, this, &MainWindow::onPremeetingRunBtnClicked);
    // meeting service
    connect(ui->m_meetingRun, &QPushButton::clicked, this, &MainWindow::onMeetingRunBtnClicked);
    // invite service
    connect(ui->invite_btn_accept, &QPushButton::clicked, this, &MainWindow::onAcceptInviteBtnClicked);
    connect(ui->invite_btn_reject, &QPushButton::clicked, this, &MainWindow::onRejectInviteBtnClicked);
    connect(ui->invite_btn_add_listener, &QPushButton::clicked, this, &MainWindow::onAddInviteListenerBtnClicked);
    connect(ui->invite_btn_remove_listener, &QPushButton::clicked, this, &MainWindow::onRemoveInviteListenerBtnClicked);
    // contacts service
    connect(ui->cont_btn_search_by_phone, &QPushButton::clicked, this, &MainWindow::onSearchContactsByPhoneBtnClicked);
    connect(ui->cont_btn_search_by_name, &QPushButton::clicked, this, &MainWindow::onSearchContactsByNameBtnClicked);
    connect(ui->cont_btn_search_infos, &QPushButton::clicked, this, &MainWindow::onGetContactsListBtnClicked);
    // feedback
    connect(ui->feed_btn_submit, &QPushButton::clicked, this, &MainWindow::onSubmitFeedbackBtnClicked);
    connect(ui->feed_btn_openWindow, &QPushButton::clicked, this, &MainWindow::onLoadFeedbackViewBtnClicked);
    // message service
    connect(ui->msg_btn_add_listener, &QPushButton::clicked, this, &MainWindow::onAddMessageListenerBtnClicked);
    connect(ui->msg_btn_remove_listener, &QPushButton::clicked, this, &MainWindow::onRemoveMessageListenerBtnClicked);
    connect(ui->msg_btn_query_unread_msgs, &QPushButton::clicked, this, &MainWindow::onQueryUnreadMessageListBtnClicked);
    connect(ui->msg_btn_clear_unread, &QPushButton::clicked, this, &MainWindow::onClearUnreadCountBtnClicked);
    connect(ui->msg_btn_delete_session_msgs, &QPushButton::clicked, this, &MainWindow::onDeleteMessageBtnClicked);
    connect(ui->msg_btn_query_history, &QPushButton::clicked, this, &MainWindow::onQueryMessageHistoryBtnClicked);
    // setting service
    connect(ui->setting_btn_run, &QPushButton::clicked, this, &MainWindow::onSettingRunBtnClicked);
    connect(ui->setting_list_methods, &QListView::clicked, [this](const QModelIndex& index) {
        updateSettingParameterNoteTextEdit(index);
    });
}
void MainWindow::InitUI() {
    ui->m_log_edit->setReadOnly(true);
    ui->m_ipc_log_edit->setReadOnly(true);
    ui->m_cb_log_level->addItem("Verbose", 0);
    ui->m_cb_log_level->addItem("Debug", 1);
    ui->m_cb_log_level->addItem("Info", 2);
    ui->m_cb_log_level->addItem("Warning", 3);
    ui->m_cb_log_level->addItem("Error", 4);
    ui->m_cb_log_level->addItem("Fatal", 5);
    ui->m_cb_log_level->setCurrentIndex(1);
    ui->m_appkey_edit->setText("");
    ui->m_server_url_edit->setText("");
#ifdef _WIN32
    ui->m_run_path_edit->setText("d:/tmp");
#else
    // 构造Contents/Frameworks路径
    QString frameworksAppPath = m_appPath + "/../Frameworks/nemeet_sdk.app/Contents/MacOS/nemeet_sdk";
    ui->m_run_path_edit->setText(frameworksAppPath);
#endif
    ui->tabWidget->setCurrentIndex(0);
    // account
    ui->m_edit_account->setText("");
    ui->m_edit_pwd->setText("");
    // message
    ui->msg_cb_order->addItem("降序", 0);
    ui->msg_cb_order->addItem("升序", 1);
    ui->msg_cb_order->setCurrentIndex(0);
    // invite
    {
        QJsonObject startMeetingParameters;
        startMeetingParameters["param"] = getJsonObject(xpack::json::encode(NEStartMeetingParams()));
        startMeetingParameters["opts"] = getJsonObject(xpack::json::encode(NEStartMeetingOptions()));
        ui->invite_edit_param->setPlainText(objectToString(startMeetingParameters));
    }
    // setting
    InitSettingUI();
}

void MainWindow::PrintLogQStr(const QString& msg, int type) {
    QTime currentTime = QTime::currentTime();
    QString formattedTime = currentTime.toString("[hh:mm:ss:zzz]");
    QMetaObject::invokeMethod(this, "DoPrintLog", Qt::QueuedConnection, Q_ARG(QString, formattedTime + msg), Q_ARG(int, type));
}
void MainWindow::PrintLog(const std::string& msg) {
    PrintLogQStr(QString::fromStdString(msg), 0);
}
void MainWindow::DoPrintLog(const QString& msg, int type) {
    if (type == 0)
        ui->m_log_edit->append(msg);
    else
        ui->m_ipc_log_edit->append(msg);
}

void MainWindow::onClearLogBtnClicked() {
    ui->m_log_edit->clear();
}
void MainWindow::onClearIPCLogBtnClicked() {
    ui->m_ipc_log_edit->clear();
}
void MainWindow::onLoglevelChanged(int index) {
    // NEMeetingSDKConfig::getInstance()->setLogLevel((NELogLevel)index);
    PrintLogQStr("Set log level: " + QString::number(index), 0);
    log_level = index;
}
void MainWindow::addInterface(const QString& module, const QString& interface, const QJsonObject& defaultParameters, const MethodRunner& runner) {
    m_methods[module][interface] = {defaultParameters, runner};
}

void MainWindow::updateParameterTextEdit(const std::string& module, const QListView* listView, const QModelIndex& index, QTextEdit* textEdit) {
    // 根据 index 获取 methodName
    const auto& methodName = listView->model()->data(index).toString();
    // const auto& methodName = ui->m_meetingMethods->get
    const auto meetingMethods = m_methods.find(module.c_str());
    if (meetingMethods == m_methods.end()) {
        PrintLog("Meeting service is not initialized");
        return;
    }
    const auto& method = meetingMethods->second.find(methodName);
    if (method == meetingMethods->second.end()) {
        PrintLog("Method is not found");
        return;
    }
    textEdit->setPlainText(objectToString(method->second.defaultParameters));
}
