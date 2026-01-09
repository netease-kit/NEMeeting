#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include "stable.h"
#include "xpack_specialization.h"

USING_NS_NNEM_SDK_INTERFACE

// account service ------------------------------------------------------------
#define PrintAccountInfo(accountInfo)                                                                                                              \
    PrintLog(                                                                                                                                      \
        "corpName:" + accountInfo.corpName + "\n userUuid:" + accountInfo.userUuid + "\n userToken:" + accountInfo.userToken + "\n nickname:" +    \
        accountInfo.nickname + "\n avatar:" + accountInfo.avatar + "\n phoneNumber:" + accountInfo.phoneNumber + "\n email:" + accountInfo.email + \
        "\n privateMeetingNum:" + accountInfo.privateMeetingNum + "\n privateShortMeetingNum:" + accountInfo.privateShortMeetingNum +              \
        "\n isInitialPassword:" + std::to_string(accountInfo.isInitialPassword) + "\n isAnonymous:" + std::to_string(accountInfo.isAnonymous) \
    )

void MainWindow::onLogoutBtnClicked() {
    PrintLog("onLogoutBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->logout([this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK logout code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onAutoLoginBtnClicked() {
    PrintLog("onAutoLoginBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->tryAutoLogin([this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEAccountInfo& accountInfo) {
            PrintLog("MeetingSDK tryAutoLogin code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onGetAccountInfoBtnClicked() {
    PrintLog("onGetAccountInfoBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->getAccountInfo([this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEAccountInfo& accountInfo) {
            PrintLog("MeetingSDK getAccountInfo code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onAddAccountServiceListenerBtnClicked() {
    PrintLog("onAddAccountServiceListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->addListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onRemoveAccountServiceListenerBtnClicked() {
    PrintLog("onRemoveAccountServiceListenerBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->removeListener(this);
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onLoginPwdBtnClicked() {
    std::string account = ui->m_edit_account->toPlainText().toStdString();
    std::string password = ui->m_edit_pwd->toPlainText().toStdString();
    PrintLog("onLoginPwdBtnClicked:" + account + ":" + password);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginByPassword(account, password, [this](MeetingErrorCode errorCode, const std::string& errorMessage, const NEAccountInfo& accountInfo) {
            PrintLog("MeetingSDK loginByPassword code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage + ", accountInfo: ");
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onLoginByTokenBtnClicked() {
    std::string account = ui->m_edit_account->toPlainText().toStdString();
    std::string token = ui->m_edit_token->toPlainText().toStdString();
    PrintLog("onLoginByTokenBtnClicked:" + account + ":" + token);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginByToken(account, token, [this](MeetingErrorCode errorCode, const std::string& errorMessage, NEAccountInfo accountInfo) {
            PrintLog("MeetingSDK loginByToken code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onLoginByPhonePwdBtnClicked() {
    std::string phoneNumber = ui->m_edit_phone->toPlainText().toStdString();
    std::string password = ui->m_edit_pwd->toPlainText().toStdString();
    PrintLog("onLoginByPhonePwdBtnClicked: phoneNumber " + phoneNumber + ",password :" + password);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginByPhoneNumber(phoneNumber, password, [this](MeetingErrorCode errorCode, const std::string& errorMessage, NEAccountInfo accountInfo) {
            PrintLog("MeetingSDK loginByPhone code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onLoginByEmailPwdBtnClicked() {
    std::string email = ui->m_edit_account->toPlainText().toStdString();
    std::string password = ui->m_edit_pwd->toPlainText().toStdString();
    PrintLog("onLoginByEmailPwdBtnClicked:" + email + ":" + password);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginByEmail(email, password, [this](MeetingErrorCode errorCode, const std::string& errorMessage, NEAccountInfo accountInfo) {
            PrintLog("MeetingSDK loginByEmail code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onRequestSmsCodeBtnClicked() {
    std::string phoneNumber = ui->m_edit_phone->toPlainText().toStdString();
    PrintLog("onRequestSmsCodeBtnClicked:" + phoneNumber);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->requestSmsCodeForLogin(phoneNumber, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK requestSmsCode code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingSDK is not initialized");
    }
}
void MainWindow::onRequestGuestSmsCodeBtnClicked() {
    std::string phoneNumber = ui->m_edit_phone->toPlainText().toStdString();
    PrintLog("onRequestGuestSmsCodeBtnClicked:" + phoneNumber);
    PrintLog("MeetingKit is not implemented");
}
void MainWindow::onLoginByPhoneSmsCodeBtnClicked() {
    std::string phoneNumber = ui->m_edit_phone->toPlainText().toStdString();
    std::string smsCode = ui->m_edit_sms_code->toPlainText().toStdString();
    PrintLog("onLoginByPhoneSmsCodeBtnClicked:" + phoneNumber + ":" + smsCode);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginBySmsCode(phoneNumber, smsCode, [this](MeetingErrorCode errorCode, const std::string& errorMessage, NEAccountInfo accountInfo) {
            PrintLog("MeetingSDK loginBySmsCode code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onGetLoginUrlBtnClicked() {
    PrintLog("onGetLoginUrlBtnClicked");
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        // need schemaUrl
        service->generateSSOLoginWebURL("",[this](MeetingErrorCode errorCode, const std::string& errorMessage, const std::string& loginUrl) {
            PrintLog("MeetingSDK getLoginUrl code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage + ", loginUrl: " + loginUrl);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onLoginByURIResultBtnClicked() {
    std::string uri = ui->m_edit_token->toPlainText().toStdString();
    PrintLog("onLoginByURIResultBtnClicked:" + uri);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->loginBySSOUri(uri, [this](MeetingErrorCode errorCode, const std::string& errorMessage, NEAccountInfo accountInfo) {
            PrintLog("MeetingSDK loginByURIResult code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
            PrintAccountInfo(accountInfo);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onUpdateAvatarBtnClicked() {
    std::string imagePath = ui->m_edit_change_info->toPlainText().toStdString();
    PrintLog("onUpdateAvatarBtnClicked:" + imagePath);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->updateAvatar(imagePath, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK updateAvatar code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onUpdateNickBtnClicked() {
    std::string nickname = ui->m_edit_change_info->toPlainText().toStdString();
    PrintLog("onUpdateNickBtnClicked:" + nickname);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->updateNickname(nickname, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK updateNickname code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}
void MainWindow::onResetPasswordBtnClicked() {
    std::string userUuid = ui->m_edit_account->toPlainText().toStdString();
    std::string newPassword = ui->m_edit_pwd->toPlainText().toStdString();
    std::string oldPassword = ui->m_edit_pwd_old->toPlainText().toStdString();
    PrintLog("onResetPasswordBtnClicked:" + userUuid + ":" + newPassword + ":" + oldPassword);
    auto service = NEMeetingKit::getInstance()->getAccountService();
    if (service) {
        service->resetPassword(userUuid, newPassword, oldPassword, [this](MeetingErrorCode errorCode, const std::string& errorMessage) {
            PrintLog("MeetingSDK resetPassword code:" + std::to_string(errorCode) + ", errorMessage: " + errorMessage);
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onKickOut() {
    PrintLog("onKickOut");
}
void MainWindow::onAuthInfoExpired() {
    PrintLog("onAuthInfoExpired");
}
void MainWindow::onReconnected() {
    PrintLog("onReconnected");
}
void MainWindow::onAccountInfoUpdated(NEAccountInfo accountInfo) {
    PrintLog("onAccountInfoUpdated");
    PrintAccountInfo(accountInfo);
}
