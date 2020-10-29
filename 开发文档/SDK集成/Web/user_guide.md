## 概述

网易会议web组件 SDK提供了一套简单易用的接口，允许开发者通过调用NEMeeting SDK(以下简称SDK)提供的API，快速地集成音视频会议功能至现有web应用中。
![会议页面示例](images/Lark20200915225519.png)

## 变更记录

| 日期 | 版本 | 变更内容 |
| :------: | :------: | :------- |
| 2020-09-15  | 1.0.0 | 首次正式发布，支持基础会议功能 |
| 2020-09-29  | 1.2.6 | 支持预约会议加入，修复已知bug |


## 快速接入

#### 开发环境准备

| 名称 | 要求 |
| :------ | :------ |
| iOS版本 | 9.0以上 |
| CPU架构支持 | ARM64、ARMV7 |
| IDE | XCode |
| 其他 | CocoaPods |

#### SDK快速接入

1. 将代码加入到页面head中（将文件路径替换为真是存在路径）

    ```js
    <script src="./NeWebMeeting_V1.0.0.js"></script>
    ```

2. 页面添加dom

    ```js
    <div id="ne-web-meeting"></div>
    ```

3. 此时全局方法neWebMeeting已注册 在需要的执行初始化

    ```js
    neWebMeeting.actions.init(800, 800);
    ```

4. 组件已注册，接入完成，使用组件 API使用会议功能

#### API说明

1. 始化化会议组件，设置宽高

    ```js
    neWebMeeting.actions.init(800, 800)//单位是px，建议比例4：3
    ```

2. 销毁WEB组件

    ```js
    neWebMeeting.actions.destory()
    ```

3. 账号登录

    ```js
    const obj = {
      accountId: '', //账号ID
      accountToken: '', //账号Token
      appkey: '', //云信服务appkey
      meetingServerDomain: '' //会议服务器地址，支持自定义部署, 为空则默认为云信线上服务器
    }
    neWebMeeting.actions.login(obj, callback)
    ```

4. 创建房间
    ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //会议ID，如果为0则是随机会议ID，1为固定私人会议ID
      video: 1, // 1开启2关闭
      audio: 1  // 1开启2关闭
    }
    neWebMeeting.actions.create(obj, callback)
    ```

5. 加入房间
    ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //要加入会议ID
      video: 1, // 1开启2关闭（匿名加入房间需要）
      audio: 1,  // 1开启2关闭（匿名加入房间需要）
      appkey: '', //云信服务appkey（匿名加入房间需要）
      meetingServerDomain: '' //会议服务器地址，支持自定义部署, 为空则默认为云信线上服务器（匿名加入房间需要）
    }
    neWebMeeting.actions.join(obj, callback)
    ```
6. 结束、离开会议回调

    ```js
    neWebMeeting.actions.afterLeave(callback) // 可在初始化后执行该方法进行注册
    // 成功离开会议，成功结束会议，主持人结束会议，其他端收到通知，均会触发
    ```

7. 当前页面成员信息
    ```js
    neWebMeeting.actions.memberInfo //内部属性：
    //nickName: 入会名称
    //audio: 音频状态
    //video: 视频状态
    //role: ‘host’ 主持人 'participant'、参会者 'AnonymousParticipant' 匿名参会者
    //avRoomUid: uid
    ```

8. 与会成员信息
    ```js
    neWebMeeting.actions.joinMemberInfo // 参会成员map，key是avRoomUid
    {
        avRoomUid: {
            accountId:"1158148553127790", //accoundId
            audio:1, // 音频状态
            avRoomUid:159739470024584, // uid
            extraMsg:"（主持人，我）", // 额外信息
            isActiveSpeaker:false, // 是否正在讲话
            isFocus:false, // 是否焦点视频
            isHost:true, // 是否主持人
            nickName:"txntm7o", // 入会名称
            stream:MediaStream, // 视频流
            video:2, // 视频状态
        }
    }
    ```

9. 设置组件的宽高

    ```js
    neWebMeeting.actions.width = 100; // 设置宽度，单位px
    neWebMeeting.actions.height = 100; // 设置高度，单位px
    ```

#### 注意事项

- web会议组件sdk要求运行在https环境中
- 初始化以及单独设置宽高时，建议使用比例4：3
- 销毁意味着退出会议
- 销毁时节点不会销毁，仍保留一部分样式，但不会影响页面结构
- 初始化后用户需要执行登陆才可以进行创建和加入
- 会议的全部功能在创建或加入之后即可使用，无需其他额外配置
- 创建会议后会直接加入会议，无需执行join
- 登陆的用户在其他页面登陆、创建或加入会议，会影响目前已经加入会议的页面，造成互踢
- API方法在执行失败后，如需进行错误排查，可以通过callback输出，例：
  ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //要加入会议ID
      video: 1, // 1开启2关闭（匿名加入房间需要）
      audio: 1,  // 1开启2关闭（匿名加入房间需要）
      appkey: '', //云信服务appkey（匿名加入房间需要）
      meetingServerDomain: '' //会议服务器地址，支持自定义部署（匿名加入房间需要）
    }
    function callback(e) {
        if(e) console.log(e.name, e.message) // 有参数时证明方法异常
    }
    neWebMeeting.actions.join(obj, callback)
  ```