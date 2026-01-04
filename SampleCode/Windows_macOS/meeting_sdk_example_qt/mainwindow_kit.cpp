#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

void MainWindow::onInitBtnClicked() {
    PrintLog("onInitBtnClicked");
    std::string appkey = ui->m_appkey_edit->toPlainText().toStdString();
    std::string url = ui->m_server_url_edit->toPlainText().toStdString();
    std::string runPath = ui->m_run_path_edit->toPlainText().toStdString();
    nem_sdk_interface::NEMeetingKitConfig config;
    config.setAppKey(appkey);
    config.setAppName("NetEase Meeting");
    config.getAppInfo()->ProductName("NetEase Meeting");
    config.getAppInfo()->OrganizationName("NetEase");
    config.getAppInfo()->ApplicationName("Meeting");
    config.getAppInfo()->SDKPath(runPath);
    config.setRunAdmin(false);
    config.setUseAssetServerConfig(false);
    config.setLanguage(NEMeetingLanguage::kNEChinese);
    config.setServerUrl(url);
    config.getLoggerConfig()->LoggerLevel((NELogLevel)log_level);
    NEMeetingKit::getInstance()->initialize(config, [this](
                                                        MeetingErrorCode errorCode, const std::string& errorMessage, const NEMeetingCorpInfo& info) {
        PrintLog("MeetingSDK initialize errorMessage: " + errorMessage);
        if (errorCode == kSuccess) {
            QString infoStr =
                QString::fromStdString("MeetingSDK initialize info: \n appKey:" + info.getAppKey() + "\n corpName:" + info.getCorpName() +
                                       "\n corpCode:" + info.getCorpCode() + "\n SSOLevel:" + std::to_string(info.getSSOLevel()));
            auto idps = info.getIdpList();
            infoStr += "\n IDPs: ";
            if (idps.size() > 0) {
                for (auto& idp : idps) {
                    infoStr += QString::fromStdString(
                        "\n name:" + idp.getName() + " id:" + std::to_string(idp.getType()) + "\n type:" + std::to_string(idp.getType()));
                }
            }
            PrintLogQStr(infoStr, 0);
        }
    });
    NEMeetingKit::getInstance()->setLogHandler([this](int level, const std::string& log) {
        if (level < log_level) {
            return;
        }
        PrintLogQStr(QString::fromStdString("[IPC][" + std::to_string(level) + "]" + log), 1);
    });
}

void MainWindow::onUnInitBtnClicked() {
    PrintLog("onUnInitBtnClicked");
    NEMeetingKit::getInstance()->unInitialize([this](MeetingErrorCode errorCode, const std::string& errorMessage) {
        PrintLog("MeetingSDK unInitialize code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
    });
}

void MainWindow::onIsInitBtnClicked() {
    bool isInit = NEMeetingKit::getInstance()->isInitialized();
    PrintLogQStr("MeetingSDK isInitialized: " + QString::number(isInit), 0);
}

void MainWindow::onAddEventListenerBtnClicked() {

}

void MainWindow::onRemoveEventListenerBtnClicked() {
    PrintLog("onRemoveEventListenerBtnClicked");
    NEMeetingKit::getInstance()->removeGlobalEventListener(this);
}
void MainWindow::onGetLogPathBtnClicked() {
    PrintLog("onGetLogPathBtnClicked");
    NEMeetingKit::getInstance()->getLogPath([this](MeetingErrorCode errorCode, const std::string& errorMessage, std::string logPath) {
        PrintLog("MeetingSDK getLogPath code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage + ", logPath: " + logPath);
    });
}
void MainWindow::onSetLanguageBtnClicked() {
    int type = ui->m_edit_language->toPlainText().toInt();
    PrintLog("onSetLanguageBtnClicked:" + std::to_string(type));
    NEMeetingKit::getInstance()->switchLanguage((NEMeetingLanguage)type, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
        PrintLog("MeetingSDK switchLanguage code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
    });
}
void MainWindow::onGetAppNoticeBtnClicked() {
    PrintLog("onGetAppNoticeBtnClicked");
    NEMeetingKit::getInstance()->getAppNoticeTips([this](MeetingErrorCode errorCode, const std::string& errorMessage, NEMeetingAppNoticeTips tips) {
        PrintLog("MeetingSDK getAppNotice code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        if (errorCode == kSuccess) {
            PrintLog("curTime:" + std::to_string(tips.curTime) + ", tips:" + std::to_string(tips.tipsList.size()));
            for (auto& tip : tips.tipsList) {
                PrintLog("title:" + tip.title + ", content:" + tip.content + ", okBtnLabel:" + tip.okBtnLabel + ", url:" + tip.url +
                         ", type:" + std::to_string(tip.type) + ", time:" + std::to_string(tip.time) + ", enable:" + std::to_string(tip.enable));
            }
        }
    });
}

void MainWindow::beforeRtcEngineInitializeWithRoomUuid(const std::string& roomUuid) {
    PrintLog("beforeRtcEngineInitializeWithRoomUuid:" + roomUuid);
}

void MainWindow::afterRtcEngineInitializeWithRoomUuid(const std::string& roomUuid) {
    PrintLog("afterRtcEngineInitializeWithRoomUuid:" + roomUuid);
}

void MainWindow::beforeRtcEngineReleaseWithRoomUuid(const std::string& roomUuid) {
    PrintLog("beforeRtcEngineReleaseWithRoomUuid:" + roomUuid);
}
