#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// screen sharing service ------------------------------------------------------------

void MainWindow::onRealtimeRecorderStartBtnClicked() {
    std::string extraData = ui->realtime_recorder_extraData->toPlainText().toStdString();
    std::string displayName = ui->realtime_recorder_displayname->toPlainText().toStdString();
    std::string meetingNum = ui->realtime_recorder_meetingNum->toPlainText().toStdString();
    PrintLog("onRealtimeRecorderStartBtnClicked");
    auto service = NEMeetingKit::getInstance()->getRealtimeRecorderService();
    if (service) {
        NEMeetingRealtimeRecorderParams params;
        params.displayName = displayName;
        params.meetingNum = meetingNum;
        
        NEMeetingRealtimeRecorderOptions opt;
        opt.extraData = extraData;
        
        service->startRealtimeRecorder(params, opt, [this](MeetingErrorCode errorCode, const std::string& errorMessage, int64_t meetingId) {
            PrintLog("MeetingSDK startRealtimeRecorder errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onRealtimeRecorderStopBtnClicked() {
    PrintLog("onRealtimeRecorderStopBtnClicked");
    auto service = NEMeetingKit::getInstance()->getRealtimeRecorderService();
    if (service) {
      service->stopRealtimeRecorder([this](MeetingErrorCode errorCode, const std::string& errorMessage) {
          PrintLog("MeetingSDK stopRealtimeRecorder code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onRealtimeRecorderAddListenerBtnClicked() {
    PrintLog("onRealtimeRecorderAddListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getRealtimeRecorderService();
    if (service) {
        service->addMeetingRealtimeRecorderStatusListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onRealtimeRecorderRemoveListenerBtnClicked() {
    PrintLog("onRealtimeRecorderRemoveListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getRealtimeRecorderService();
    if (service) {
        service->removeMeetingRealtimeRecorderStatusListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onMeetingRealtimeRecorderStatusChanged(NEMeetingRealtimeRecorderEvent event) {
    PrintLog("MeetingSDK onMeetingRealtimeRecorderStatusChanged status:" + std::to_string(event.status) + ", meetingNum: " + event.meetingNum  + ", code: " + std::to_string(event.code));
}
