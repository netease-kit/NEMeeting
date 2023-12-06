## v4.0.0(2023-11-30)

### New Features

- 会议状态变更事件增加回调：开始加入 RTC、加入 RTC 成功、加入 RTC 失败

### Refactor

- 优化会议组件 H5 聊天室接收图片展示

## v3.16.0(2023-09-06)

### New Features

- 会议组件 H5 支持自定义菜单
- 会议组件 H5 聊天室支持接收和下载图片
- 增加会议状态变更回调 API
- 支持配置是否显示长短会议号
- 新增匿名入会 API

### Refactor

- 💡 会议层添加 framework、修改上报耗时

### Bug Fixed

- 白板权限被取消后没有弹窗提示
- 修复断网情况下本端无法更新音视频状态
- 修复断网后移除成员，重新入会后仍会被移除

- ### Compatibility

- Compatible with `NERoom` version `1.20.0`

## v3.14.1(2023-08-09)

### NEW Features

- 新增会议剩余时间提醒

### Bug Fixes

- 修复匿名成员离开成员列表数量错误问题

## v3.12.0(2023-05-30)

### NEW Features

- 支持主持人会前设置开启关闭音视频设备

## v3.11.0(2023-04-27)

### NEW Features

- 新增私有化部署支持

### Bug Fixes

- 修复房间从 2 人变 1 人情况下界面显示未移除离开人员问题

## v3.10.1(2023-04-27)

**Note:** Version bump only for package nemeeting-web-sdk

## v3.10.0(2023-04-11)

### NEW Features

- 统一字段 meetingId 为 meetingNum
- checkSystemRequirements 方法支持初始化之前调用

### Bug Fixes

- 修复 ios 下 video 遮挡昵称问题

## v3.9.1(2023-03-13)

### Bug Fixes

- 修复 ios 切换视图到主屏幕出现画布遮挡问题

## v3.9.0(2023-03-02)

### Bug Fixes

- 修复关闭视频后重新打开帧率未按照上次设置值
- ### Compatibility

- Compatible with `NERoom` version `1.12.0`

## v3.8.0(2023-01-09)

### Bug Fixes

- 修复成员互踢后，成员数量显示错误，后入会成员无法显示问题

### Compatibility

- Compatible with `NERoom` version `1.11.0`

## v3.7.1(2022-12-23)

**Note:** Version bump only for package nemeeting-web-sdk

## v3.7.0(2022-12-08)

### NEW Features

- 支持显示会议主题

### Bug Fixes

- 视频播放无权限无法重新播放问题
- 修复聊天室显示系统透传消息问题，设置音频默认属性为 music_standard
- 修复未开启视频无法显示共享流问题
- ### Compatibility

- Compatible with `NERoom` version `1.10.0`

## v1.0.0

- NEMeetinKit V1.0.0 全新发布
