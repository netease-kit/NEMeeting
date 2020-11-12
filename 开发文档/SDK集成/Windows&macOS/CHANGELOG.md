# CHANGELOG

# 2020-11-12 @ v1.3.1

## Added

 * 使用网易会议账号登录接口 `NEAuthService::loginWithNEMeeting`
 * 使用 SSO token 登录接口 `NEAuthService::loginWithSSOToken`
 * 自动登录接口 `NEAuthService::tryAutoLogin`
 * 创建及加入会议 option 中增加 `NEShowMeetingIdOption` 配置会议中显示会议 ID 的策略

## Updated

 * 账户绑定的 AppKey 从 login 接口移动至 `NEMeetingSDK::initialized` 接口的 config 参数中
 * `NEAuthService::logout` 新增了带默认参数的形参，用以决定在退出时是否清理 SDK 缓存的用户信息

# 2020-10-29 @ v1.3.0

## Added

 * NESettingsService#NEVideoController#setTurnOnMyVideoWhenJoinMeeting新增入会前视频开关状态设置
 * NESettingsService#NEVideoController#isTurnOnMyVideoWhenJoinMeetingEnabled新增入会前视频开关状态获取
 * NESettingsService#NEAudioController#setTurnOnMyAudioWhenJoinMeeting新增入会前音频开关状态设置
 * NESettingsService#NEAudioController#isTurnOnMyAudioWhenJoinMeetingEnabled新增入会前音频开关状态获取
 * NEAuthService#getAccountInfo新增获取用户信息接口
 * NEAccountService#getAccountInfo废弃此接口，推荐使用NEAuthService#getAccountInfo

# 2020-09-29 @ v1.2.6

## Added

 * NEAuthListener#onAuthInfoExpired新增账号信息过期通知
 * MeetingDisconnectCode#MEETING_DISCONNECTING_AUTH_INFO_EXPIRED新增账号信息过期应的会议退出码
 * NEMeetingService#leaveMeeting新增从会议中离开会议接口

# 2020-09-18 @ v1.2.3

## Added

 * NEJoinMeetingParams#passwork 新增密码入会字段
 * NEMeetingStatus#MEETING_STATUS_WAITING 新增会议等待状态
 * MeetingDisconnectCode#MEETING_WAITING_VERIFY_PASSWORD 会议等待状态类型
 * NEMeetingInfo#password、subject、startTime、endTime会议信息字段
 * NEMeetingSDK#getPreMeetingService 会议预约服务
 * NEPreMeetingService#scheduleMeeting:callback:预定会议
 * NEPreMeetingService#cancelMeeting:callback:取消已预定的会议
 * NEPreMeetingService#getMeetingList:callback:查询特定状态会议列表
 * NEPreMeetingService#registerScheduleMeetingStatusListener:注册预约会议事件回调
 * NEScheduleMeetingListener#onScheduleMeetingStatusChanged会议状态回调

# 2020-09-04 @ v1.2.0

## Added

 * NEMeetingService#getCurrentMeetingInfo 获取当前会议信息
 * NEMeetingOptions#noInvite 配置会议中是否显示"邀请"按钮
 * NEMeetingOptions#noChat 配置会议中是否显示"聊天"按钮
 * NEMeetingOptions#injectedMoreMenuItems "更多"菜单中的自定义注入菜单项
 * MeetingServiceListener增加onInjectedMenuItemClick:meetingInfo，自定义菜单按钮点击事件回调

# 2020-08-31 @ v1.1.0

## Added

 * NEMeetingSDK#isInitialized查询SDK初始化状态
 * NEMeetingService#getMeetingStatus查询当前会议状态
 * 会议设置服务NESettingService用于保存和查询用户的相关会议选项

# 2020-07-10 @ v1.0.0

 * 首次正式发布
