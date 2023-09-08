# NEMeetingKit ChangeLog
## v3.16.0(SEP 6, 2023)
### New Feature
- 会议最小化开启小窗悬浮，小窗模式下退入后台，在Android 8.0及以上系统会开启画中画

### Bug Fixes
- 应用修复会议内外，美颜设置不一致问题
- 修复会中改名时，被多设备登录操作退出黑屏问题
- 聊天室发送文件下载后查看图层问题修复
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
