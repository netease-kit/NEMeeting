#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE


void MainWindow::onWebAppGetWebAppListBtnClicked() {
    PrintLog("onWebAppGetWebAppListBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingWebAppService();
    if (service) {
        service->getWebAppList([this](MeetingErrorCode errorCode, const std::string& errorMessage, std::list<NEMeetingWebAppItem> itemList) {
            PrintLog("MeetingSDK getWebAppList code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onWebAppEnableWebAppBtnClicked() {
    std::string pluginId = ui->webapp_edit_pluginid->toPlainText().toStdString();
    PrintLog("onWebAppEnableWebAppBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingWebAppService();
    if (service) {
        service->enableWebApp(pluginId, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK enableWebApp code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onWebAppDisableWebAppBtnClicked() {
    std::string pluginId = ui->webapp_edit_pluginid->toPlainText().toStdString();
    PrintLog("onWebAppDisableWebAppBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingWebAppService();
    if (service) {
        service->disableWebApp(pluginId, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK disableWebApp code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onWebAppSetWebAppClickListenerBtnClicked() {
    PrintLog("onWebAppSetWebAppClickListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingWebAppService();
    if (service) {
        service->setWebAppClickListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

bool MainWindow::onClickWebAppIcon(const NEWebAppClickInfo& clickInfo, const NEMeetingInfo& meetingInfo) {
    PrintLog("MeetingSDK onClickWebAppIcon clickInfo pluginId:" + clickInfo.pluginId + ", meetingInfo: " + meetingInfo.meetingNum);
    return true;
}
