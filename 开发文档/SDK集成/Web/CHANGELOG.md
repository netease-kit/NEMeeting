# CHANGELOG


# 2021-04-29 @ v1.8.1

  * 增加共享时支持显示视频

  * 创建加入会议增加字段

    * *cloudRecordOn* 是否开启服务端录制配置项

  * 增加额外会议信息 *sipId*

  * 优化白板性能消耗问题

  * 修复已知bug
# 2021-03-30 @ v1.7.2

  * 增加会中成员进出通知

  * 修复若干bug

    * 全体静音状态异常

    * 会中改名无提示

    * 断网优化

    * 订阅无声音问题修复
# 2021-03-17 @ v 1.7.0

* 增加会中改名功能

  * *noRename* 创建，加入会议，会中改名配置

* 修复部分bug

* 新增会中白板功能

  * 新增白板菜单项，控制开启白板共享和结束白板共享

* 新增*defaultWindowMode*选项配置“会议视图模式”，支持普通和白板模式

* 修复部分已知bug
# 2021-01-15 @ v1.5.2

* 增加音频订阅方法

  * *subscribeRemoteAudioStream* 订阅用户音频流
  * *subscribeAllRemoteAudioStreams* 订阅所有用户音频流
  * *subscribeRemoteAudioStreams* 订阅用户音频流Bylist

* 修复部分情况下加入会议失败的问题

# 2020-12-21 @ v1.5.0

* 增加自定义按钮配置

# 2020-11-27 @ v1.3.3

* 补充关闭预约会议密码回调监听

* 补充创建会议提示已存在会议取消操作监听

* 加入会议增加预约会议密码参数*password*

* 调整会议画廊模式展示策略

# 2020-11-20 @ v1.3.2


* 新增会议初始化信息配置

  - 额外增加创会，入会时配置项 `meetingIdDisplayOptions` 短号显示规则（可选）

* 增加底部展示参会人数

* 优化了弹窗效果

# 2020-11-13 @ v1.3.1

## Added

* 新增会议初始化信息配置
  
  - 额外增加config配置项 `neWebMeeting.actions.init(width, height, config)`

* 新增获取会议短号信息

  - `neWebMeeting.actions.NEMeetingInfo.shortMeetingId`

* 新增两种登陆方式

  - 账密登陆：`neWebMeeting.actions.loginWithNEMeeting`

  - SSO登陆：`neWebMeeting.actions.loginWithSSOToken`
