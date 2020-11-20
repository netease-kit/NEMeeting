# CHANGELOG


# 2020-11-20 @ v1.3.2


* 新增会议初始化信息配置

  - 额外增加创会，入会时配置项 `NEMeetingIdDisplayOption` 短号显示规则（可选）

  - 增加底部展示参会人数

  - 优化了弹窗效果

# 2020-11-13 @ v1.3.1

## Added

* 新增会议初始化信息配置
  
  - 额外增加config配置项 `neWebMeeting.actions.init(width, height, config)`

* 新增获取会议短号信息

  - `neWebMeeting.actions.NEMeetingInfo.shortMeetingId`

* 新增两种登陆方式

  - 账密登陆：`neWebMeeting.actions.loginWithNEMeeting`

  - SSO登陆：`neWebMeeting.actions.loginWithSSOToken`