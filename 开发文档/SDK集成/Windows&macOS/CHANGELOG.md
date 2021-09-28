# 2021-09-28 @ v2.0.6

## Added

- 增加会中音量检测

## Changed

- G2 SDK升级至4.4.2

## Fixed

- 修复mac下首次安装，音视频无法打开问题
- 修复离开会议，设置美颜不生效问题
- 修复离开会议偶先程序卡死问题
- 修复设置页面打开时，同时入会程序异常崩溃问题

# 2021-09-09 @ v2.0.4

## Added

## Changed

- 优化音视频权限判断
- IM SDK升级至8.7.0

## Fixed

- 修复异常退出重新入会，音视频状态未同步问题
- 修复云端录制失效问题

# 2021-08-12 @ v2.0.0

## Added

- 即刻会议增加入会密码`NEMeetingParams::password`
- 接口支持结束会议 `NEMeetingService::leaveMeeting`
- 增加入会超时配置以及入会的部分具体错误信息 `NEMeetingOptions::joinTimeout`

## Changed

- G2 SDK 升级到 4.3.8
- 重构 native，改名为 roomkit
- 优化 IPC 反初始化的逻辑
- 优化创建/加入会议聊天室开关

## Fixed

- 修复参会者列表共享白板图标显示问题
- 修复直播“仅本企业观看”显示问题
- 修复匿名入会，昵称设置无效问题

# 2021-07-08 @ v1.10.0

## Added

* 创建会议增加会议场景参数，支持传入受邀用户列表: `NEStartMeetingParams::scene`
* 预约/编辑会议增加会议场景参数，支持传入受邀用户列表：`NEMeetingItemSetting::scene`
* 创建/加入会议增加自定义标签参数：`NEMeetingParams::tag`
* 会议服务，当前会议成员信息增加自定义标签参数：`NEMeetingService::getCurrentMeetingInfo#NEInMeetingUserInfo::tag`

## Changed

## Fixed

# 2021-05-27 @ v1.9.0

## Added

* 会议服务，当前会议信息新增属性： `NEMeetingService::getCurrentMeetingInfo`
   - 会议成员列表：`NEMeetingInfo::userList`
   - 会议唯一ID：`NEMeetingInfo::meetingUniqueId`
   - 会议主题：`NEMeetingInfo::subject`
   - 会议密码：`NEMeetingInfo::password`
   - 会议开始时间：`NEMeetingInfo::startTime`
   - 会议预约的开始时间：`NEMeetingInfo::scheduleStartTime`
   - 会议预约的结束时间：`NEMeetingInfo::scheduleEndTime`
* 初始化增加日志配置：`NEMeetingSDKConfig::getLoggerConfig`
* 初始化支持设置运行权限：`NEMeetingSDKConfig::setRunAdmin`

## Changed

* mac G2 SDK升级到4.1.1
* mac 相芯美颜SDK回退到7.2.0
* 替换日志库为yx_alog
* 更新接口文档

## Fixed

* 修复成员列表排序不对问题
* 修复全体静音/取消程序卡顿问题
* 修复共享时昵称显示的问题
* 修复部分未翻译的问题
* 修复部分场景下共享时视频窗口大小不正常的问题

# 2021-04-29 @ v1.8.1

## Added

## Changed

## Fixed

# 2021-04-28 @ v1.8.0

## Added

* 创建会议增加云端录制配置参数:  `NEStartMeetingOption::noCloudRecord`
* 会议设置服务新增白板查询接口:  `NEWhiteboardController::isWhiteboardEnabled`
* 会议设置服务新增云端录制查询接口:  `NERecordController::isCloudRecordEnabled`
* 编辑/预约会议新增参数:  `NEMeetingItem::cloudRecordOn`
* 会话服务,会议信息新增sip号: `NEMeetingService::getCurrentMeetingInfo#NEMeetingInfo::sipId`
* 会议设置服务,会议信息新增sip号: `NESettingsService::getHistoryMeetingItem#NEHistoryMeetingItem::sipId`
* 初始化配置新增保活间隔设置: `NEMeetingSDKConfig::setKeepAliveInterval`
* 共享时支持显示视频

## Changed

* G2 SDK升级到4.1.0
* 相芯美颜SDK升级到7.3.0
* 白板SDK升级到3.1.0
* 会议直播视图增加共享屏幕视图
* 共享时隐藏设置/取消设置焦点视频的入口
* 优化共享时的性能

## Fixed

* 修复主持人全体静音，把自己也静音的问题
* 修复共享ppt时，部分场景下对端看不到画面的问题
* 修复windows下，分辨率超过1080P桌面共享对端看到模糊的问题
* 修复主持人移交时多次提示的问题
* 修复匿名入会时，入会中状态多次通知的问题
* 修复断线重连，偶现崩溃问题

# 2021-03-30 @ v1.7.2

## Added

* 支持设置直播权限 `NEMeetingItem::liveWebAccessControlLevel`

## Changed

* 更新白板地址

## Fixed

* 修复共享屏幕下，举手提示异常问题
* 修复共享屏幕下，聊天消息数目不同步问题
* 修复移交主持人给屏幕共享者，新主持人直播窗口成员列表空白问题
* 修复授权白板权限给离开会议的参会者时的崩溃问题

# 2021-03-18 @ v1.7.1

## Added

## Changed

## Fixed

* 修复成员加入/离开会议画面闪烁问题

# 2021-03-18 @ v1.7.0

## Added

* 支持Windows共享屏幕共享音频 + 流畅优先
* 支持MacOS屏幕共享流畅优先
* 支持主持人结束成员屏幕共享
* 支持聊天室文本单条右键复制
* 支持会中白板功能
    - 新增白板菜单项，控制开启白板共享和结束白板共享
    - 新增`NEMeetingOptions.noWhiteBoard`选项配置白板功能是否开启，默认开启
    - 支持配置会议视图模式`NEMeetingOptions.defaultWindowMode`，支持普通和白板模式
* 支持会中改名，通过`NEMeetingOptions.noRename`选项配置该功能是否开启，默认为开启

## Changed

* G2 SDK升级到4.0.1版本
* 取消自动随机移交主持人
* 更新开发环境为VS2019 + QT5.15.0

## Fixed

* 修复屏幕共享下，聊天窗口未同步消息的问题

# 2021-01-15 @ v1.5.2

## Added

* 支持单个用户音频订阅/取消订阅接口`NEMeetingSDK::subscribeRemoteAudioStream`
* 支持多个用户音频订阅/取消订阅接口`NEMeetingSDK::subscribeRemoteAudioStreams`
* 支持全部用户音频订阅/取消订阅接口`NEMeetingSDK::subscribeAllRemoteAudioStreams`

## Changed

* 优化账号登录流程
* 调整`NEAuthService::Login`接口为不带appkey

## Fixed

* 修复结束共享时视频显示为共享画面的问题
* 修复macOS下部分wps的版本播放时不能共享的问题
* 修复会议非正常退出后，关联进程没有退出的问题
* 修复画廊模式下成员列表切换会闪烁的问题
* 修复部分显卡下会崩溃的问题
* 修复windows下共享时聊天窗口闪烁的问题
* 修复结束共享时偶现的崩溃问题

# 2020-12-21 @ v1.5.0

## Added

* 支持视频美颜 NESettingsService.GetBeautyFaceController()
* 支持直播功能 NESettingsService.GetLiveController()
* 支持会中禁止息屏
* 支持全局静音举手功能
* 支持展示SIP客户端入会信息

## Changed

* MacOS共享支持WPS
* 应用共享优化
* 支持自定义工具栏
* 适配部分分辨率下共享工具栏显示

## Fixed

* 修复共享状态下断网重连后其他端画面异常问题
* 修复win7下共享崩溃问题
* 修复窗口闪烁问题
  
# 2020-11-27 @ v1.3.3

## Added

* 支持共享应用

## Changed

* G2 SDK 升级到3.8.1
* 成员列表搜索时自动去掉首尾空格
* 会议画廊模式每页不在展示自己，只在首页展示

## Fixed

* 修复多端入会互踢时偶现崩溃问题 
* 修复会议画廊模式修改数量不生效的问题
* 修复移交主持人时成员离开房间，成员再进入房间时，成员列表有重复数据

# 2020-11-20 @ v1.3.2

## Added

* 会议举手功能
* 支持录制配置能力

## Changed

* G2 SDK 升级到3.8.0
* quick controls 1升级到quick controls 2
* 会议预约自适应窗口比例
* 调整更新升级页面视觉样式

## Fixed

* 修复应用内更新无法自动
* 修复拔掉副屏，副屏无法停止共享
* 修复共享结束时收到聊天消息，消息报错问题

# 2020-11-13 @ v1.3.1

## Added

* 会中反馈及会后反馈
* 预约会议编辑功能
* 使用网易会议账号登录接口 `NEAuthService::loginWithNEMeeting`
* 使用 SSO token 登录接口 `NEAuthService::loginWithSSOToken`
* 自动登录接口 `NEAuthService::tryAutoLogin`
* 创建及加入会议 option 中增加 NEShowMeetingIdOption 配置会议中显示会议 ID 的策略

## Updated

* `NEMeetingSDK::initialized` config 参数中增加 AppKey 参数用于设置全局默认应用 Key 信息
* `NEAuthService::logout` 新增了带默认参数的形参，用以决定在退出时是否清理 SDK 缓存的用户信息

# 2020-10-29 @ v1.3.0

## Added

* 个人会议短号解析能力
* 组件在 `AuthService` 中增加 `getAccountInfo` 接口用于获取用户资料信息
* 组件对私有化环境能力支持
* 共享中不显示工具条
* 预约会议详情页

## Changed

* 调整入会前后的整体 UI 视觉样式
* 升级 G2 SDK 到 3.7.0

## Fixed

* 匿名入会输入错误会议码后无法再次入会
* 安装包签名失败导致部分场景无法正常安装（Windows only）
* 本地视频画面无法镜像
* 多端登录后入会本地视频无法渲染
* 多拓展屏下缩放比例不一致拖动导致界面异常
* 多拓展屏下部分窗口和全局 Toast 提示不跟随窗口
* 会议持续时间计时不准确
* 断网后无法再次开启会议（macOS only）
* 屏幕共享正在讲话文案优化

## Removed

* 组件入会过程中取消按钮

## Deprecated

* 组件原有 `AccountService` 及功能函数 `getPersonalMeetingId()` 废弃不再使用
