# NEMeetingKit ChangeLog
## v4.5.0(May 7, 2024)
### New Feature
* 支持配置入会是否请求电话权限
* 支持配置入会是否拉取和展示小应用
* 支持配置入会是否展示通知中心菜单
* 预约会议支持设置是否允许访客入会
* 预约会议支持预选参会者列表并指定身份
* 支持通讯录邀请入会
### Compatibility
- 兼容 NERoomKit 1.28.0
- 兼容 NIMSDK_LITE 9.16.0
- 兼容 NERtcSDK  5.5.40

## v4.4.0(April 2, 2024)
### New Feature
- 入会时支持指定参会者头像：`NEMeetingParams.avatar`
- 入会时支持是否允许音频设备切换`NEMeetingOptions.enableAudioDeviceSwitch`
- 支持会中锁定特定用户视频画面
- 支持会议创建者收回主持人权限
- 预约会议支持配置是否允许参会者在主持人进会前加入会议: `NEMeetingItem.setEnableJoinBeforeHost`, `NEMeetingItem.isEnableJoinBeforeHost` 
- Android 升级适配 TargetSdk 至 API 34 （Android 14）
  - 声明了 POST_NOTIFICATION 权限（暂不执行动态申请）
  - 调整了 Android 14 上启动前台 Service 的时机至用户授权屏幕共享权限后
- 支持会中全部准入、全部移除等候室成员
- 支持本次会议自动准入等候室成员
- 支持聊天室私聊功能和聊天权限控制
### Fixed
- 修复等候室默认图片展示未撑满屏幕的问题
- 适配 Android 机型导航栏沉浸式模式
- 参会者列表添加“焦点视频”成员图标
- 修复 Android 5.x 系统白板共享黑屏问题（Flutter渲染PlatformView 失败）
- 兼容处理 会中聊天室/等候室聊天室 可能不存在的情况
### Api Changes
- 废弃 `NELoggerConfig` ，不再支持配置日志级别和日志文件路径
### Compatibility
- 兼容 NERoomKit 1.27.0
- 兼容 NIMSDK_LITE 9.15.0
- 兼容 NERtcSDK  5.5.33

## v4.3.1(March 7, 2024)
### Bug Fixes
- 修改会议邀请文本
### Compatibility
- 兼容 NERoomKit 1.26.0
- 兼容 NIMSDK_LITE 9.14.2
- 兼容 NERtcSDK  5.5.22

## v4.3.0(March 7, 2024)
### New Feature
- 新增断开音频功能，支持连接/断开本地音频
- 新增获取音频列表、切换音频设备功能
- 支持管理员修改参会者昵称
- 支持用户头像显示
### Compatibility
- 兼容 NERoomKit 1.26.0
- 兼容 NIMSDK_LITE 9.14.2
- 兼容 NERtcSDK  5.5.22

## v4.2.2(Jan 31, 2024)
### New Feature
- 升级 NERtc 版本至 5.5.21
### Compatibility
- 兼容 NERoomKit 1.25.2
- 兼容 NIMSDK_LITE 9.14.1
- 兼容 NERtcSDK  5.5.21

## v4.2.1(Jan 24, 2024)
### Bug Fixes
- 修复appKey未配置等候室，加入聊天室失败问题
### Compatibility
- 兼容 NERoomKit 1.25.1
- 兼容 NIMSDK_LITE 9.14.1
- 兼容 NERtcSDK  5.5.207

## v4.1.1(Jan 15, 2024)
### New Feature
- 升级 NERtc 版本至 5.5.207

### Compatibility
- 兼容 NERoomKit 1.25.1
- 兼容 NIMSDK_LITE 9.14.1
- 兼容 NERtcSDK 5.5.207

## v4.1.0(Jan 10, 2024)
### New Feature
- 新增等候室功能
  - 等候室开启后，新参会者会先进入等候室，管理员可以准入或者移除等候室中的参会者
  - 会中管理员可以在聊天室给等候室所有成员发消息，等候室成员可以查看等候室的聊天消息
  - 创建会议时，通过 `NEStartMeetingOptions.enableWaitingRoom` 设置会议是否开启等候室，管理员后续可以手动开关
  - 预约会议时，通过 `NEMeetingItem.setWaitingRoomEnabled` 设置会议是否开启等候室
- 新增会议水印功能，可以在会议中开启水印，后台可以配置水印内容、水印样式、是否强制打开(强制打开则端上不展示设置入口)
- 修改会控更多工具栏展示，新增安全模块，支持等候室开关、水印开关和锁定会议开关
- 新增接口 `NEMeetingService.updateInjectedMenuItem`，更新当前存在的自定义菜单项的信息和状态
- `NEAccountService.getAccountInfo` 账号信息查询新增字段： `NEAccountInfo.accountId`，`NEAccountInfo.isAnonymous`

### Compatibility
- 兼容 NERoomKit 1.25.0
- 兼容 NIMSDK_LITE 9.14.1
- 兼容 NERtcSDK  5.5.203

## v4.0.1(Dec 29, 2023)
### New Feature
- 新增updateInjectedMenuItem接口，支持动态修改已存在的菜单按钮状态
- AccountService.getAccountInfo返回增加accountId、isAnonymous，支持获取匿名入会的账号信息

### Compatibility
- 兼容 NERoomKit 1.23.1
- 兼容 NIMSDK_LITE 9.12.0
- 兼容 NERtcSDK  5.5.203

## v4.0.0(Nov 30, 2023)
### New Feature
- 新增云端录制会议能力。
- 支持在会议中查询历史消息和撤回消息。
- 在会议配置项 NEMeetingOptions 中新增以下属性：
  - showCloudRecordMenuItem：是否展示云端录制菜单按钮，默认为 true。
  - showCloudRecordingUI：是否展示云端录制过程中的UI提示，默认为 true。

### Compatibility
- 兼容 NERoomKit 1.23.0
- 兼容 NIMSDK_LITE 9.12.0
- 兼容 NERtcSDK  5.5.203

## v3.17.0 (Oct 31, 2023)

### New Features

- 新增屏幕共享服务接口 `NEScreenSharingService`
  - 开启屏幕共享 `startScreenShare`
  - 停止屏幕共享 `stopScreenShare`
  - 添加监听 `addScreenSharingStatusListener`
  - 移除监听 `removeScreenSharingStatusListener`
  - 屏幕状态变更回调 `onScreenSharingStatusChanged`
- 新增屏幕共享状态变更模型 `NEScreenSharingEvent`
  - 当前共享状态 `NEScreenSharingStatus`
  - 额外附带参数 `arg`
  - 额外附带数据对象 `obj`
- 新增屏幕共享时配置信息类 `NEScreenSharingOptions`
  - 开启/关闭音频功能 `enableAudioShare`
- 新增屏幕共享时基本参数类 `NEScreenSharingParams`
  - 用户昵称 `displayName`
  - 共享码 `sharingCode`
- `NEMeetingKit` 新增获取共享屏幕服务接口 `getScreenSharingService`

### Compatibility

* 兼容 `NERoom` 1.21.0 版本
* 兼容 `NIM` 9.12.0 版本
* 兼容 `NERtcSDK_Special` 5.5.203 版本

## v3.16.1(SEP 8, 2023)

- 适配Android 版本低于8.0无法使用画中画功能，默认以最小化

## v3.16.0(SEP 6, 2023)
### New Feature
- 会议最小化开启小窗悬浮，小窗模式下退入后台，在Android 8.0及以上系统会开启画中画

### Bug Fixes
- 网络监测toast与白板toast重复问题
- 被踢时，rtc回调与IM回调时序问题

### Compatibility
- 兼容 NERoomKit 1.20.0
- 兼容 NIMSDK_LITE 9.12.0
- 兼容 NERtcSDK  5.4.8

## v3.15.2(Aug 23, 2023)
### New Features
* 修改会议网络监听，state连续三次为poor时，toast网络异常提示
* 增加弱网或网络断开监听，收到NERoomConnectType.Disconnect显示会议重连loading，收到NERoomConnectType.Reconnect则关闭
* 增加网络异常会议断开监听，增加重新入会弹窗，可以返回首页或重新入会(复用最开始入会的参数)
### Compatibility
* 兼容 `NERoom` 1.19.2 版本
* 兼容 `NIM` 9.12.0 版本
* 兼容 `NERtc` 5.3.11 版本

## v3.15.0(Aug 11, 2023)
### New Features
* 移动端升级Flutter版本至3.10.5
* 优化RTC私有化配置服务
* 会议组件：优化入会协议-entry/config/snapshot/joinRtc合并为一个接口
* 网易会议升级RTC 5.4.3、IM 9.12.0 、白板3.9.6（NERoom升级）
* 网易会议反馈优化-移动端反馈日志下载地址提供全路径
* 请求头区分不同跨端框架
### Compatibility
* 兼容 `NERoom` 1.19.0 版本
* 兼容 `NIM` 9.12.0 版本
* 兼容 `NERtc` 5.4.3 版本

## v3.14.0(July 05, 2023)
### New Features
* 优化大房间信令交互；
### Compatibility
* 兼容 `NERoom` 1.17.0 版本
## v3.13.2(July 19, 2023)
### New Features
* 新增`NERoomRtcController.enableEncryption`，支持开启媒体流加密；
* 新增`NERoomRtcController.disableEncryption`，支持关闭媒体流加密；
### Bug Fixes
* 修复预约会议password等部分字段为空问题；
### Compatibility
* 兼容 `NERoom` 1.16.1 版本
* 兼容 `NIM` 9.10.0 版本
* 兼容 `NERtc` 5.3.7 版本

## v3.13.0(June 21, 2023)
### New Features
* 新增 `NEMeetingOptions.enableAudioShare` 入会选项，配置开启/关闭屏幕共享时的音频共享功能；
### API Changes
* 删除 `NEMeetingKitConfig.reuseIM` 字段，不影响 IM 复用的逻辑；
### Fixed
* 修复音频智能降噪不生效问题；
### Compatibility
* 兼容 `NERoom` 1.16.0 版本
* 兼容 `NIM` 9.10.0 版本
* 兼容 `NERtc` 5.3.7 版本

## v3.11.0(April 27, 2023)
### New Features
* 新增 `NEMeetingOptions.enableTransparentWhiteboard` 入会选项，配置开启/关闭白板透明标注模式；
* 新增 `NEMeetingOptions.enableFrontCameraMirror` 入会选项，配置开启/关闭前置摄像头画面镜像；
### Compatibility
* 兼容 `NERoom` 1.14.0 版本
* 兼容 `NIM` 9.8.0 版本
* 兼容 `NERtc` 4.6.50 版本

## v3.10.0(April 10, 2023)
### New Features
* 新增会议最小化接口：`NEMeetingService.minimizeCurrentMeeting`
* 支持跨AppKey加会议
### API Changes
* 重命名API中会议号字段，会议号（字符串类型）统一使用`meetingNum`命名。被重命名的字段包括：
  * `NEMeetingParams.meetingId`
  * `NEAccountInfo.meetingId`, `NEAccountInfo.shortMeetingId`
  * `NEHistoryMeetingItem.getMeetingId()`, `NEHistoryMeetingItem.getShortMeetingId()`
  * `NEMeetingItem.getMeetingId()`
  * `NEMeetingInfo.meetingId`, `NEMeetingInfo.shortMeetingId`
* 重命名API中会议唯一Id字段，会议唯一Id（长整型）统一使用`meetingId`命名。被重命名的字段包括：
  * `NEMeetingInfo.meetingUniqueId`
  * `NEMeetingItem.getMeetingUniqueId()`
  * `NEHistoryMeetingItem.getMeetingUniqueId()`
### Compatibility
* 兼容 `NERoom` 1.13.0 版本
* 兼容 `NIM` 9.8.0 版本
* 兼容 `NERtc` 4.6.50 版本
* 兼容 `Common` 1.1.15 版本
* 兼容 `CoreKit` 1.3.0 版本


## v3.9.1(May 11, 2023)
### Bug Fixes
* 修复接听电话时进行本端属性修改时序可能出错的问题；
* 调整绑定画布和订阅的时序，调整为先绑定画布后订阅视频，规避可能出现的黑屏问题(sub、unsub、sub连续信令可能造成订阅后没有流数据)；
* 新增退后台检测功能，并自动关闭视频，在回到前台后自动重新打开视频；
* 美颜功能不可用时不初始化美颜模块；
### Compatibility
* 兼容 `NERoom` 1.12.0 版本

## v3.9.0(March 02, 2023)
### New Features
* 新增网络质量检测能力；
* 新增会议系统电话接听处理能力；
* 优化共享&白板下的显示；
* 其他已知问题修复；
### Compatibility
* 兼容 `NERoom` 1.12.0 版本

## v3.8.0(January 09, 2023)
### New Features
* 新增`NEMeetingKitConfig.serverUrl`, 用于设置组件私有化服务器地址；
* 新增`NEMeetingOptions.showScreenShareUserVideo`选项控制观看共享时是否显示屏幕共享者的摄像头画面；
* 新增`NEMeetingOptions.showWhiteboardShareUserVideo`选项控制观看白板时是否显示白板共享者的摄像头画面；
* 新增`NEMeetingOptions.showFloatingMicrophone`选项控制是否显示麦克风悬浮窗；
* 会议中小窗口支持滑动；
### Compatibility
* 兼容 `NERoom` 1.11.0 版本

## v3.7.1(December 15, 2022)
### Bug Fixes
* 多语言文案更新

## v3.7.0(December 07, 2022)
### New Features
* 支持多语言
* 新增`NEMeetingLanguage`类型，枚举组件支持的语言类型；
* 新增`NEMeetingKit.switchLanguage`方法，可切换到对应的语言类型；
* 新增`NEMeetingOptions.detectMutedMic`选项控制是否开启麦克风静音检测功能；
* 新增`NEMeetingOptions.unpubAudioOnMute`选项控制本端静音时是否继续发送静音音频包；
### Compatibility
* 兼容 `NERoom` 1.10.0 版本

## v3.6.0(October 31, 2022)
### New Features
* 修复已知问题
### Compatibility
* 兼容 `NERoom` 1.8.2 版本

## v3.5.0(September 27, 2022)
### New Features
* Flutter端MeetingKit重构
* 修复已知问题
### Compatibility
* 兼容 `NERoom` 1.8.0 版本

## v3.4.0(August 31, 2022)
### New Features
* 聊天室支持图片、文件消息发送与接收
  * 新增`NEMeetingOptions.chatroomConfig`字段，类型为`NEMeetingChatroomConfig`：可配置聊天室内相关功能是否开启
* 支持全局事件监听
  * 新增`NEGlobalEventListener`全局事件监听器，可监听 RTC 实例创建、销毁的事件
  * 新增`NEMeetingKit.addGlobalEventListener`添加全局事件监听
  * 新增`NEMeetingKit.removeGlobalEventListener`移除全局事件监听
### Compatibility
* 兼容 `NERoom` 1.7.0 版本

## v3.3.0(July 28, 2022)
### New Features
* 白板共享时支持上传视频和图片
* 预约会议支持配置`SIP`功能开关：`NEMeetingItem#setNoSip`
* 新增会议倒计时结束时间提醒功能开关：`NEMeetingOptions#showMeetingRemainingTip`，默认关闭

## v3.2.0(June 30, 2022)
### New Features
* 支持虚拟背景功能：
  * 新增方法：`NESettingsService#enableVirtualBackground`,开启虚拟背景配置
  * 新增方法：`NESettingsService#isVirtualBackgroundEnabled`,是否支持虚拟背景
  * 新增方法：`NESettingsService#setBuiltinVirtualBackgrounds`,设置自定义背景图片列表
  * 新增方法：`NESettingsService#getBuiltinVirtualBackgrounds`,获取内置虚拟背景列表
* 支持IM SDK复用：
  * 新增字段：`NEMeetingKitConfig#reuseIM`,是否复用im
* G2 SDK版本升级至4.6.13
* 支持美颜功能,无对外接口变更
* 支持SIP邀请,无对外接口变更
* 支持联席主持人, 新增枚举字段：`NEMeetingRoleType#coHost`,联席主持人


## v3.1.0(June 19, 2022)
### New Features
* 新增方法：`NEMeetingService#anonymousJoinMeeting`,支持会议组建匿名入会
* 支持SIP功能：
  * 新增字段：`NEMeetingOptions#noSip`,支持会议内是否显示sip号,默认false
  * 新增字段：`NEMeetingInfo#sipId`,支持通过`NEMeetingService#getCurrentMeetingInfo`获取会议sipId
* 支持私有化功能：
  * 新增字段：`NEMeetingKitConfig#useAssetServerConfig`,支持读取asset目录下的私有化配置文件`xkit_server.config`
* 调整内部代码工程结构

## v3.0.0(May 19, 2022)
### New Features
* 基于`NERoom`1.1.0版本进行SDK架构升级和改造

### API Changes
* Maven GAV变更：`com.netease.yunxin:meetinglib` 重命名为 `com.netease.yunxin.kit.meeting:meeting`
* 对外包名修改：`com.netease.meetinglib.sdk` 重命名为 `com.netease.yunxin.kit.meeting.sdk`
* 接口类名：`NEMeetingSDK` 重命名为 `NEMeetingKit`
* `com.netease.meetinglib.sdk.NEMeetingSDKConfig` 重命名为 `com.netease.yunxin.kit.meeting.sdk.NEMeetingKitConfig`
* NEMeetingKitConfig：
  - 删除deprecated字段：`domain`、`enableDebugLog`、`logSize`、`reuseNIM`
* 隐藏暂不支持的功能字段：`useAssetServerConfig`
* NEMeetingOptions：
  - 删除deprecated字段：`audioAINSEnabled`、`injectedMoreMenuItems`、
* 隐藏暂不支持字段：`NEMeetingOptions.noSip`
* 删除 NEMeetingMenuItem 类型 - deprecated
* 删除场景相关API：
  * `NEMeetingScene.java` 类
  * `NEMeetingRoleConfiguration.java` 类
  * `NEStartMeetingParams.scene` 字段
  * `NEMeetingItemSetting.scene` 字段
  * `NEMeetingService.java`修改：
  * 删除`NEMeetingService.java`不支持接口：`subscribeRemoteAudioStream`、`subscribeRemoteAudioStreams`、`subscribeAllRemoteAudioStreams`
  * 删除`NESettingsService.java`以下美颜相关的api
