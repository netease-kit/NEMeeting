#include "./ui_mainwindow.h"
#include "mainwindow.h"

#include <sstream>

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// contacts service ------------------------------------------------------------
void MainWindow::onSearchContactsByPhoneBtnClicked() {
    std::string phone = ui->cont_edit_keyname->toPlainText().toStdString();
    int page_num = ui->cont_edit_page_num->toPlainText().toInt();
    int page_size = ui->cont_edit_page_size->toPlainText().toInt();
    PrintLog(
        "onSearchContactsByPhoneBtnClicked, phone:" + phone + ", page_num:" + std::to_string(page_num) + ", page_size:" + std::to_string(page_size));
    auto service = NEMeetingKit::getInstance()->getContactsService();
    if (service) {
        service->searchContactListByPhoneNumber(
            phone, page_size , page_num, [this](int error_code, const std::string& message, std::list<NEContact> contacts) {
                PrintLog("searchContactsByPhone, error_code:" + std::to_string(error_code) + ", message:" + message +
                         ", contacts size:" + std::to_string(contacts.size()));
                if (error_code == kSuccess) {
                    int i = 0;
                    for (auto& contact : contacts) {
                        i++;
                        PrintLog(std::to_string(i) + ", userUuid:" + contact.userUuid + ", name:" + contact.name + ", avatar:" + contact.avatar +
                                 ", dept:" + contact.dept + ", phoneNumber:" + contact.phoneNumber);
                    }
                }
            });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onSearchContactsByNameBtnClicked() {
    std::string name = ui->cont_edit_keyname->toPlainText().toStdString();
    int page_num = ui->cont_edit_page_num->toPlainText().toInt();
    int page_size = ui->cont_edit_page_size->toPlainText().toInt();
    PrintLog(
        "onSearchContactsByNameBtnClicked, name:" + name + ", page_num:" + std::to_string(page_num) + ", page_size:" + std::to_string(page_size));
    auto service = NEMeetingKit::getInstance()->getContactsService();
    if (service) {
        service->searchContactListByName(
            name, page_size , page_num, [this](int error_code, const std::string& message, std::list<NEContact> contacts) {
                PrintLog("searchContactsByName, error_code:" + std::to_string(error_code) + ", message:" + message +
                         ", contacts size:" + std::to_string(contacts.size()));
                if (error_code == kSuccess) {
                    int i = 0;
                    for (auto& contact : contacts) {
                        i++;
                        PrintLog(std::to_string(i) + ", userUuid:" + contact.userUuid + ", name:" + contact.name + ", avatar:" + contact.avatar +
                                 ", dept:" + contact.dept + ", phoneNumber:" + contact.phoneNumber);
                    }
                }
            });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}

void MainWindow::onGetContactsListBtnClicked() {
    std::string infos = ui->cont_edit_ids->toPlainText().toStdString();
    std::list<std::string> ids;
    // split ids
    std::string id;
    std::istringstream iss(infos);
    while (std::getline(iss, id, ',')) {
        ids.push_back(id);
    }
    PrintLog("onGetContactListBtnClicked, ids size:" + std::to_string(ids.size()));
    auto service = NEMeetingKit::getInstance()->getContactsService();
    if (service) {
        service->getContactsInfo(ids, [this](int error_code, const std::string& message, NEContactsInfoResult ret) {
            PrintLog("getContactList, error_code:" + std::to_string(error_code) + ", message:" + message);
            if (error_code == kSuccess) {
                int i = 0;
                PrintLog("find size:" + std::to_string(ret.foundList.size()));
                for (auto& contact : ret.foundList) {
                    i++;
                    PrintLog(std::to_string(i) + ", userUuid:" + contact.userUuid + ", name:" + contact.name + ", avatar:" + contact.avatar +
                             ", dept:" + contact.dept + ", phoneNumber:" + contact.phoneNumber);
                }
                i = 0;
                PrintLog("not found size:" + std::to_string(ret.notFoundList.size()));
                for (auto& id : ret.notFoundList) {
                    i++;
                    PrintLog(std::to_string(i) + ", id:" + id);
                }
            }
        });
    } else {
        PrintLog("MeetingKit is not initialized");
    }
}