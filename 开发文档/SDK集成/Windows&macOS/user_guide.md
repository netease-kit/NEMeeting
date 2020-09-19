## 概述

网易会议 Windows/macOS SDK 提供了一套简单易用的接口，允许开发者通过调用 NEMeeting SDK (以下简称SDK) 提供的API，快速地集成音视频会议功能至现有桌面应用中。

## 变更记录

| 日期 | 版本 | 变更内容 |
| :------: | :------: | :------- |
| 2020-07-10  | 1.0.0 | 首次正式发布 |
| 2020-08-31 | 1.1.0 | 新增如下接口：<br />`NEMeetingSDK#isInitialized`查询SDK初始化状态<br />     `NEMeetingService#getMeetingStatus`查询当前会议状态<br />     会议设置服务NESettingService用于保存和查询用户的相关会议选项 |
| 2020-09-04 | 1.2.0 | 新增如下接口：<br />`NEMeetingService#getCurrentMeetingInfo` 获取当前会议信息<br />    `NEMeetingOptions#noInvite` 配置会议中是否显示"邀请"按钮<br />    `NEMeetingOptions#noChat` 配置会议中是否显示"聊天"按钮<br />    `NEMeetingOptions#injectedMoreMenuItems` <br />    "更多"菜单中的自定义注入菜单项<br />    `MeetingServiceListener增加onInjectedMenuItemClick:meetingInfo:`<br />    自定义菜单按钮点击事件回调 |
| 2020-09-18 | 1.2.3 | 新增如下接口：<br />`NEJoinMeetingParams#passwork` 新增密码入会字段<br />`NEMeetingStatus#MEETING_STATUS_WAITING` 新增会议等待状态<br />`NEMeetingCode#MEETING_WAITING_VERIFY_PASSWORD` 会议等待状态类型<br />`NEMeetingInfo#password、subject、startTime、endTime`会议信息字段<br />`NEMeetingSDK#getPreMeetingService` 会议预约服务<br />`NEPreMeetingService#scheduleMeeting:callback:`预定会议<br />`NEPreMeetingService#cancelMeeting:callback:`取消已预定的会议<br />`NEPreMeetingService#getMeetingList:callback:`查询特定状态会议列表<br />`NEPreMeetingService#registerScheduleMeetingStatusListener:`注册预约会议事件回调<br />`NEScheduleMeetingListener#onScheduleMeetingStatusChanged`会议状态回调 |

## 环境要求

| 名称 | 要求 |
| :------ | :------ |
| IDE | Visual Studio 2017 or Qt5 or Xcode 11.3.1 以上 |
| OS | Windows 7 以上 or macOS 10.13 以上 |

## 业务开发

#### SDK 引入

 - [点击此处下载 Windows C++ SDK](http://yx-web.nos.netease.com/package/1599211417/NEMeeting_SDK_Windows_v1.2.0.zip)
 - [点击此处下载 macOS C++ SDK](http://yx-web.nos.netease.com/package/1599215029/NEMeeting_SDK_macOS_v1.2.0.zip)

**1）Windows 开发环境配置**

下载完成后，Windows SDK 目录结构如下：

```
├─bin     包含 SDK 的二进制文件
├─include 您需要引入的头文件目录，只需关注 nemeeting_sdk_interface_include.h 即可
└─lib     您需要引入的依赖库文件
```

将 bin 目录设置为项目的依赖目录，或放置与您已有项目的可搜索环境变量中、将 include 文件夹添加为项目的头文件搜索路径、将 lib 文件夹添加为项目的库文件搜索路径并引入库文件到项目中。以下为 Qt5 配置示例：

```
win32 {
    DEPENDPATH += $$PWD/bin
    INCLUDEPATH += $$PWD/include
    LIBS += -L$$PWD/lib -lnem_hosting_module
}
```

**2）macOS 开发环境配置**

macOS 目录结构如下：

```
├─bin     包含 SDK 的二进制文件
└─lib     包含 SDK 所需的 framework
  └── nem_hosting_module.framework
```

其中 bin 目录下的 NetEaseMeetingHost.app 及 nem_hosting_module.framework 均需要在您的程序编译后储存在应用的 `*.app/Contents/Frameworks` 目录下，项目配置过程中，将 nem_hosting_module.framework 引入到您的 Xcode 或 Qt5 工程中，以下为 Qt5 配置示例：

```
// 此处包含将 NetEaseMeetingHost.app 和 `nem_hosting_module.framework` 拷贝到应用的 `/Contents/Frameworks` 目录下
macx {
    INCLUDEPATH += $$PWD/lib/nem_hosting_module.framework/Headers
    LIBS += -F$$PWD/lib -framework nem_hosting_module
    DEPENDPATH += $$PWD/bin

    SDK_FRAMEWORK.files = $$PWD/lib/nem_hosting_module.framework
    SDK_FRAMEWORK.path = /Contents/Frameworks

    NEM_UI_SDK_APP.files = $$PWD/bin/NetEaseMeetingHost.app
    NEM_UI_SDK_APP.path = /Contents/Frameworks

    QMAKE_BUNDLE_DATA += SDK_FRAMEWORK \
                         NEM_UI_SDK_APP
}
```

#### SDK 接口介绍

在一切开始前，请将头文件 `nemeeting_sdk_interface_include.h` 和命名空间 `USING_NS_NNEM_SDK_INTERFACE` 引入到您的项目中。

**1）初始化 SDK**

调用所有接口前，您需要先初始化 SDK，SDK 会根据您指定的信息在 Windows 系统 `%LocalAppData%` 目录下及 macOS `~/Library/Application Support` 下创建缓存文件。

```C++
NEMeetingSDKConfig config;
QString displayName = QObject::tr("NetEase Meeting");
QByteArray byteDisplayName = displayName.toUtf8();
// 设置程序启动后的显示名称，如 “网易会议”，在加入会议时会提示“正在进入网易会议...”
config.getAppInfo()->ProductName(byteDisplayName.data());
// 设置您的组织名
config.getAppInfo()->OrganizationName("NetEase");
// 设置您的应用程序名称
config.getAppInfo()->ApplicationName("Meeting");
// 设置您的域名信息
config.setDomain("yunxin.163.com");
NEMeetingSDK::getInstance()->initialize(config, [this](NEErrorCode errorCode, const std::string& errorMessage) {
    ...
});
```

**2）登录鉴权**

初始化完成后，您可以调用登录接口来登录到 SDK 中。

```C++
auto authService = NEMeetingSDK::getInstance()->getAuthService();
if (authService)
{
    // 指定您登录到 SDK 中所使用的 App key
    QByteArray byteAppKey = appKey.toUtf8();
    // 指定您登录到 SDK 所使用的账户
    QByteArray byteAccountId = accountId.toUtf8();
    // 指定您登录到 SDK 使用的密码
    QByteArray byteAccountToken = accountToken.toUtf8();
    // 执行登录操作
    authService->login(byteAppKey.data(), byteAccountId.data(), byteAccountToken.data(), [this](NEErrorCode errorCode, const std::string& errorMessage) {
        ...
    });
}
```

**3）创建会议 / 加入会议**

登录成功后，您可以获取个人所拥有的个人会议 ID、创建或者加入一个会议

```C++
// 获取个人会议 ID
auto accountService = NEMeetingSDK::getInstance()->getAccountService();
if (accountService)
{
    accountService->getPersonalMeetingId([this](NEErrorCode errorCode, const std::string& errorMessage, const std::string& personalMeetingId) {
        // personalMeetingId 为登录后为您账户分配的固定会议 ID
    });
}
```

```C++
// 创建会议示例
auto meetingService = NEMeetingSDK::getInstance()->getMeetingService();
if (meetingService)
{
    QByteArray byteMeetingId = meetingId.toUtf8();
    QByteArray byteNickname = nickname.toUtf8();

    NEStartMeetingParams params;
    // 指定您要创建会议的会议 ID，留空则使用系统随机分配的会议 ID
    params.meetingId = byteMeetingId.data();
    // 指定您加入会议后使用的昵称
    params.displayName = byteNickname.data();
    // 设置是否在加入会议后启用视频和音频设备，是否显示邀请和聊天室按钮
    NEStartMeetingOptions options;
    options.noAudio = !audio;
    options.noVideo = !video;
    options.noChat = !enableChatroom;
    options.noInvite = !enableInvitation;
    // 通过 options 设置自定义菜单
    auto applicationPath = qApp->applicationDirPath();
    for (auto i = 0; i < 3; i++)
    {
        NEMeetingMenuItem item;
        item.itemId = NEM_MORE_MENU_USER_INDEX + i + 1;
        item.itemTitle = QString(QStringLiteral("Submenu") + QString::number(i + 1)).toStdString();
        item.itemImage = QString(applicationPath + "/submenu_icon.png").toStdString();
        options.injected_more_menu_items_.push_back(item);
    }
    meetingService->startMeeting(params, options, [this](NEErrorCode errorCode, const std::string& errorMessage) {
        // ... 创建会议后的回调函数
    });
}
```

```C++
// 加入会议示例
auto meetingService = NEMeetingSDK::getInstance()->getMeetingService();
if (meetingService)
{
    QByteArray byteMeetingId = meetingId.toUtf8();
    QByteArray byteNickname = nickname.toUtf8();

    NEJoinMeetingParams params;
    // 指定您要加入的会议 ID
    params.meetingId = byteMeetingId.data();
    // 指定您加入到会议后使用的昵称
    params.displayName = byteNickname.data();

    // 设置是否在加入会议后启用视频和音频，是否显示邀请和聊天室按钮
    NEJoinMeetingOptions options;
    options.noAudio = !audio;
    options.noVideo = !video;
    options.noChat = !enableChatroom;
    options.noInvite = !enableInvitation;
    // 通过 options 设置自定义菜单
    auto applicationPath = qApp->applicationDirPath();
    for (auto i = 0; i < 3; i++)
    {
        NEMeetingMenuItem item;
        item.itemId = NEM_MORE_MENU_USER_INDEX + i + 1;
        item.itemTitle = QString(QStringLiteral("Submenu") + QString::number(i + 1)).toStdString();
        item.itemImage = QString(applicationPath + "/submenu_icon.png").toStdString();
        options.injected_more_menu_items_.push_back(item);
    }
    meetingService->joinMeeting(params, options, [this](NEErrorCode errorCode, const std::string& errorMessage) {
        // 加入会议的回调，可通过返回值判断是否成功
    });
}
```

在加入或者创建会议前后，您可能需要关注议的创建/加入进度以及会议中状态的变更通知，如需关注这些信息，您需要先继承 `NEMeetingStatusListener`，然后实现 `onMeetingStatusChanged` 通知，并将该子类注册到监听队列中。示例代码如下：

```
// 继承子类并覆写 

class NEMeetingSDKManager : public NEMeetingStatusListener
{
public:
    virtual void onMeetingStatusChanged(int status, int code) override
    {
        // ...
    }
}

// 将该类对象注册到监听队列中
auto ipcMeetingService = NEMeetingSDK::getInstance()->getMeetingService();
if (ipcMeetingService)
    ipcMeetingService->addMeetingStatusListener(listener);
```

创建或加入会议完成后，您可以获取会议的一些基本信息，示例如下：

```C++
auto meetingService = NEMeetingSDK::getInstance()->getMeetingService();
if (meetingService)
{
    meetingService->getCurrentMeetingInfo([this](NEErrorCode errorCode, const std::string& errorMessage, const NEMeetingInfo& meetingInfo) {
        // 获取会议信息后的回调函数，您可以通过 meetingInfo 获取所需信息
    });
}
```

**5) 会议预约**

登录账户后您可以进行会议预约相关操作，以下代码演示了如何预约及获取已经预约的会议，并监控预约的会议状态变更。

```C++
// 预约一个会议
auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
if (ipcPreMeetingService)
{
    NEMeetingItem item;
    item.subject = meetingSubject.toUtf8().data();
    item.startTime = startTime;
    item.endTime = endTime;
    item.password = password.toUtf8().data();
    item.setting.attendeeAudioOff = attendeeAudioOff;

    ipcPreMeetingService->scheduleMeeting(item, [this](NEErrorCode errorCode, const std::string& errorMessage, const NEMeetingItem& item) {
        // ...
    });
}
```

```C++
// 取消预约会议
auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
if (ipcPreMeetingService)
{
    ipcPreMeetingService->cancelMeeting(meetingUniqueId, [this](NEErrorCode errorCode, const std::string& errorMessage) {
        // ...
    });
}
```

```C++
// 获取会议列表
auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
if (ipcPreMeetingService)
{
    std::list<NEMeetingItemStatus> status;
    status.push_back(MEETING_INIT);
    status.push_back(MEETING_STARTED);
    status.push_back(MEETING_ENDED);
    ipcPreMeetingService->getMeetingList(status, [this](NEErrorCode errorCode, const std::string& errorMessage, std::list<NEMeetingItem>& meetingItems) {
        // ...
    });
}
```

当您已经预约了几个会议后，会议可能随时有人加入、离开，此时您可以监听会议状态的变更通知实时更新 UI 上显示的会议当前状态：

```C++
// 实现一个 NEScheduleMeetingStatusListener 子类并覆写 `onScheduleMeetingStatusChanged` 方法
// 将该子类通过会议预约的服务注册到监听队列中即可关注预约会议的状态变更。
auto ipcPreMeetingService = NEMeetingSDK::getInstance()->getPremeetingService();
if (ipcPreMeetingService)
    ipcPreMeetingService->registerScheduleMeetingStatusListener(this);
```

**4）注销登录**

当您会议流程结束或想更换账号时，您需要登出原有账号。

```C++
auto authService = NEMeetingSDK::getInstance()->getAuthService();
if (authService)
{
    authService->logout([this](NEErrorCode errorCode, const std::string& errorMessage) {
        // 登出结果的回调
    });
}
```

**5）卸载 SDK**

一切完成后，当退出您的应用程序之前，需要卸载 SDK，请注意：卸载 SDK 需要等待卸载通知回调再退出您的应用程序！

```C++
NEMeetingSDK::getInstance()->unInitialize([&](NEErrorCode errorCode, const std::string& errorMessage) {
    // 卸载完成的回调，此处将退出请求抛到 UI 线程执行退出操作
});
```

