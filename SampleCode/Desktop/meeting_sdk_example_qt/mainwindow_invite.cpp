#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// invite service ------------------------------------------------------------

void MainWindow::onAcceptInviteBtnClicked() {
    PrintLog("onAcceptInviteBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingInviteService();
    if (service) {
        QByteArray byteParameter = ui->invite_edit_param->toPlainText().toUtf8();
        const auto& parameterObject = getJsonObject(byteParameter.data());
        NEJoinMeetingParams param;
        QByteArray byteParam = objectToString(parameterObject["param"].toObject()).toUtf8();
        xpack::json::decode(byteParam.data(), param);
        NEJoinMeetingOptions opts;
        QByteArray byteOpts = objectToString(parameterObject["opts"].toObject()).toUtf8();
        xpack::json::decode(byteOpts.data(), opts);
        service->acceptInvite(param, opts, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("InviteService acceptInvite code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onRejectInviteBtnClicked() {
    int64_t id = ui->invite_edit_meeting_id->toPlainText().toLongLong();
    PrintLog("onRejectInviteBtnClicked:" + id);
    auto service = NEMeetingKit::getInstance()->getMeetingInviteService();
    if (service) {
        service->rejectInvite(id, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("InviteService rejectInvite code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onAddInviteListenerBtnClicked() {
    PrintLog("onAddInviteListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingInviteService();
    if (service) {
        service->addMeetingInviteStatusListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onRemoveInviteListenerBtnClicked() {
    PrintLog("onRemoveInviteListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMeetingInviteService();
    if (service) {
        service->removeMeetingInviteStatusListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onMeetingInviteStatusChanged(NEMeetingInviteStatus status, int meetingId, NEMeetingInviteInfo inviteInfo) {
    PrintLog("onMeetingInviteStatusChanged status:" + std::to_string(status) + " meetingId:" + std::to_string(meetingId) +
             "\ninviteInfo meetingNum:" + inviteInfo.meetingNum + " inviterName:" + inviteInfo.inviterName +
             " inviterAvatar:" + inviteInfo.inviterAvatar + " subject:" + inviteInfo.subject +
             ", preMeetingInvitation:" + std::to_string(inviteInfo.preMeetingInvitation));
}
