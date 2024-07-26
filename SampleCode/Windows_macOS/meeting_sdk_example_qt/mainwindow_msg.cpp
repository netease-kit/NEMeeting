#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// message service ------------------------------------------------------------

void MainWindow::onAddMessageListenerBtnClicked() {
    PrintLog("onAddMessageListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->addMeetingMessageChannelListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onRemoveMessageListenerBtnClicked() {
    PrintLog("onRemoveMessageListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->removeMeetingMessageChannelListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onQueryUnreadMessageListBtnClicked() {
    std::string sessionId = ui->msg_edit_sessionid->toPlainText().toStdString();
    PrintLog("onQueryUnreadMessageListBtnClicked:" + sessionId);
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->queryUnreadMessageList(
            sessionId, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingSessionMessage>& messages) {
                PrintLog("queryUnreadMessageList callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage);
                for (auto& message : messages) {
                    PrintLog("sessionId:" + message.sessionId + ", sessionType:" + std::to_string(message.sessionType) +
                             ", messageId:" + message.messageId + ", data:" + message.data + ", time:" + std::to_string(message.time));
                }
            });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onClearUnreadCountBtnClicked() {
    std::string sessionId = ui->msg_edit_sessionid->toPlainText().toStdString();
    PrintLog("onClearUnreadCountBtnClicked:" + sessionId);
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->clearUnreadCount(sessionId, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("clearUnreadCount callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onDeleteMessageBtnClicked() {
    std::string sessionId = ui->msg_edit_sessionid->toPlainText().toStdString();
    PrintLog("onDeleteMessageBtnClicked:" + sessionId);
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->deleteAllSessionMessage(sessionId, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("deleteAllSessionMessage callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onQueryMessageHistoryBtnClicked() {
    NEMeetingGetMessageHistoryParams param;
    param.sessionId = ui->msg_edit_sessionid->toPlainText().toStdString();
    param.searchOrder = ui->msg_cb_order->currentIndex() == 0 ? NEMeetingMessageSearchOrder::kDesc : NEMeetingMessageSearchOrder::kAsc;
    param.limit = ui->msg_edit_limit->toPlainText().toInt();
    param.fromTime = ui->msg_edit_start_ts->toPlainText().toLongLong();
    param.toTime = ui->msg_edit_end_ts->toPlainText().toLongLong();
    param.time = ui->msg_edit_msg_ts->toPlainText().toLongLong();
    param.content = ui->msg_edit_content->toPlainText().toStdString();
    PrintLog("onQueryMessageHistoryBtnClicked:" + param.sessionId);
    auto service = NEMeetingKit::getInstance()->getMessageService();
    if (service) {
        service->getSessionMessagesHistory(
            param, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::list<NEMeetingSessionMessage>& messages) {
                PrintLog("getSessionMessagesHistory callback, errorCode:" + std::to_string(errorCode) + ", errorMessage:" + errorMessage);
                for (auto& message : messages) {
                    PrintLog("sessionId:" + message.sessionId + ", sessionType:" + std::to_string(message.sessionType) +
                             ", messageId:" + message.messageId + ", data:" + message.data + ", time:" + std::to_string(message.time));
                }
            });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onSessionMessageReceived(const NEMeetingSessionMessage& message) {
    PrintLog("onSessionMessageReceived, sessionId:" + message.sessionId + ", sessionType:" + std::to_string(message.sessionType) +
             ", messageId:" + message.messageId + ", data:" + message.data + ", time:" + std::to_string(message.time));
}
void MainWindow::onSessionMessageRecentChanged(const std::list<NEMeetingRecentSession>& messages) {
    PrintLog("onSessionMessageRecentChanged, size:" + messages.size());
    for (auto& message : messages) {
        PrintLog("sessionId:" + message.sessionId + ", fromAccount:" + message.fromAccount + ", fromNick:" + message.fromNick + ", sessionType:" +
                 std::to_string(message.sessionType) + ", time:" + std::to_string(message.time) + ", recentMessageId:" + message.recentMessageId +
                 ", unreadCount:" + std::to_string(message.unreadCount) + ", content:" + message.content);
    }
}
void MainWindow::onSessionMessageDeleted(const NEMeetingSessionMessage& message) {
    PrintLog("onSessionMessageDeleted, sessionId:" + message.sessionId + ", sessionType:" + std::to_string(message.sessionType) +
             ", messageId:" + message.messageId + ", data:" + message.data + ", time:" + std::to_string(message.time));
}
void MainWindow::onSessionMessageAllDeleted(const std::string& sessionId, const NEMeetingSessionType& sessionType) {
    PrintLog("onSessionMessageAllDeleted, sessionId:" + sessionId + ", sessionType:" + std::to_string(sessionType));
}