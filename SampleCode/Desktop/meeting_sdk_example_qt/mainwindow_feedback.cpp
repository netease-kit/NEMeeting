#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// screen sharing service ------------------------------------------------------------

void MainWindow::onSubmitFeedbackBtnClicked() {
    std::string category = ui->feedback_type->toPlainText().toStdString();
    std::string feed_back_description_str = ui->feed_back_description->toPlainText().toStdString();
    PrintLog("onSubmitFeedbackBtnClicked");
    auto service = NEMeetingKit::getInstance()->getFeedbackService();
    if (service) {
        NEFeedback feedback;
        feedback.category = category;
        feedback.description = feed_back_description_str;
        service->feedback(feedback, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("feedback errorCode: " + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onLoadFeedbackViewBtnClicked() {
    PrintLog("loadFeedbackView");
    auto service = NEMeetingKit::getInstance()->getFeedbackService();
    if (service) {
        service->loadFeedbackView([this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK loadFeedbackView code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}