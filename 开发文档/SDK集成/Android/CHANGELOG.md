# CHANGELOG

# 2020-11-13 @ v1.3.1

## Added
* 观看屏幕共享时支持手势缩放
* 新增SSOToken登录、自动登录
    - SSOToken登录：`NEMeetingSDK.loginWithSSOToken(String ssoToken, NECallback<Void> callback)`
    - 自动登录：`NEMeetingSDK.getInstance().tryAutoLogin(NECallback<Void> callback)`
* 支持配置会议内“会议号”显示规则
    - 配置项：`NEMeetingOptions.meetingIdDisplayOptions`
* 支持配置画廊模式开关
    - 配置项：`NEMeetingOptions.noGallery`



# 2020-10-29 @ v1.3.0

## Added
* 新增画廊模式
* 新增结束会议添加断网提示
* 新增会议私有化部署
    - 开启私有化配置：`NEMeetingSDKConfig.useAssetServerConfig` 
* 新增获取会议账号信息方法
    - `NEAccountService.getAccountInfo()`
* 支持企业客户自定义个人会议短号
    - 会议短号获取：`NEAccountInfo.shortMeetingId`
    - 邀请里面包含了短号字段 `shortId`

## Changed
* 个人会议号获取方式变更
    - `NEAccountInfo.meetingId`
* 屏幕共享文案调整
* 删除会议转场页面取消按钮
* 状态栏适配优化
* 会中视觉优化调整

## Fixed
* 修复会议显示时长偏差
* 优化屏幕共享过程中正在讲话逻辑

## Removed
* `NEMeetingInfo.getPersonalMeetingId`

--------
# 2020-09-29 @ v1.2.6

## Added
* `NEMeetingSDKConfig.NEForegroundServiceConfig`新增配置会议时显示前台服务
* `NEAuthListener.onAuthInfoExpired`新增账号信息过期通知
* `NEMeetingCode.MEETING_DISCONNECTING_AUTH_INFO_EXPIRED`新增账号信息过期对应的会议退出码

---------
# 2020-09-24 @ v1.2.5

## Added
* `NEMeetingSDKConfig.appName`增加配置入会应用名称
* `NEMeetingOptions.noMinimize`配置会议中是否允许最小化会议页面
* `NEMeetingStatus.MEETING_STATUS_INMEETING_MINIMIZED`新增会议最小化状态 
* `NEMeetingSDK.getControlService()`新增遥控器服务
* `NEControlService.openControlUI`打开遥控器
* `NEControlService.setOnCustomMenuItemClickListener`设置遥控器自定义点击事件 
* `NEControlService.registerControlListener`注册监听遥控器回调
* `NEControlService.unRegisterControlListener`反注册监听遥控器回调
* `NEControlService.getCurrentMeetingInfo`获取当前会议详情。如果当前无正在进行中的会议，则回调数据对象为空

-------
# 2020-09-18 @ v1.2.3

## Added
* `NEJoinMeetingParams.password`新增密码入会字段
* `NEMeetingStatus.MEETING_STATUS_WAITING`新增会议等待状态
* `NEMeetingCode.MEETING_WAITING_VERIFY_PASSWORD`新增会议等待状态类型 
* `NEMeetingInfo.password、subject、startTime、endTime`新增会议信息字段
* `NEMeetingSDK.NEPreMeetingService`新增预约会议服务
* `NEPreMeetingService.scheduleMeeting` 新增预定会议
* `NEPreMeetingService.cancelMeeting`新增取消已预定的会议
* `NEPreMeetingService.getMeetingItemById`新增查询预定会议信
* `NEPreMeetingService.getMeetingList`新增查询特定状态下的会议列表
* `NEPreMeetingService.registerScheduleMeetingStatusListener`新增注册预定会议状态变更监听器
* `NEPreMeetingService.unRegisterScheduleMeetingStatusListener`新增反注册预定会议状态变更监听器 

-------
# 2020-09-04   @ v1.2.0

## Added
* `MeetingServiceListener增加onInjectedMenuItemClick:meetingInfo:`添加自定义菜单按钮监听
* `NEMeetingService.getCurrentMeetingInfo`获取当前会议信息
* `NEMeetingOptions.noInvite`配置会议中是否显示"邀请"按钮 
* `NEMeetingOptions.noChat`配置会议中是否显示"聊天"按钮
* `NEMeetingOptions#injectedMoreMenuItems"更多"菜单中的自定义注入菜单项

-------
# 2020-08-31  @ v1.1.0

## Added
* `NEMeetingSDK.isInitialized()`查询SDK初始化状态
* `NEMeetingService.getMeetingStatus()`查询当前会议状态
* 会议设置服务`NEAccountService`用于保存和查询用户的相关会议选项

-------
# 2020-07-10 @ v1.0.0

## Added
* 首次正式发布



