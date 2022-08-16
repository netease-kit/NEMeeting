## 概述

网易会议web组件 SDK提供了一套简单易用的接口，允许开发者通过调用NEMeeting SDK(以下简称SDK)提供的API，快速地集成音视频会议功能至现有web应用中。
![会议页面示例](images/Lark20200915225519.png)

## 变更记录

|     日期     |   版本   | 变更内容                                                                                                                                              |
|:----------:|:------:|:--------------------------------------------------------------------------------------------------------------------------------------------------|
| 2020-09-15 | 1.0.0  | 首次正式发布，支持基础会议功能                                                                                                                                   |
| 2020-09-29 | 1.2.6  | 支持预约会议加入，修复已知bug                                                                                                                                  |
| 2020-10-29 | 1.3.0  | 支持预约会议密码加入，修复已知bug                                                                                                                                |
| 2020-10-22 | 1.2.8  | 支持多端互踢，增加*NEMeetingInfo*字段                                                                                                                        
| 2020-11-12 | 1.3.1  | 增加*shortId*字段  <br>  增加两种登陆方式 *loginWithNEMeeting* *loginWithSSOToken* <br> 增加初始化配置，兼容已有方案                                                        |
| 2020-11-20 | 1.3.2  | 增加创会入会额外可选配置： *meetingIdDisplayOptions* 会议号展示逻辑                                                                                                   |
| 2020-11-27 | 1.3.3  | 补充关闭预约会议密码回调监听 <br> 补充创建会议提示已存在会议取消操作监听 <br> 加入会议增加预约会议密码参数*password* <br> 调整会议画廊模式展示策略                                                           |
| 2020-12-21 | 1.5.0  | 补充自定义按钮配置                                                                                                                                         |
| 2021-01-15 | 1.5.2  | 增加音频流订阅方法 <br> *subscribeRemoteAudioStream*订阅用户音频流 <br> *subscribeAllRemoteAudioStreams*订阅所有用户音频流 <br> *subscribeRemoteAudioStreams*订阅用户音频流Bylist |
| 2021-02-05 | 1.6.0  | 修复部分已知bug                                                                                                                                         |
| 2021-03-17 | 1.7.0  | 增加会中改名入口 <br> 创建，加入会议增加 *noRename* 字段（是否使用会中改名）<br> 新增*defaultWindowMode*选项配置“会议视图模式”，支持普通和白板模式                                                   |
| 2021-03-30 | 1.7.2  | 增加成员进出事件监听*peerJoin* *peerLeave* <br> 网络事件监听*networkQuality*<br> 国际化配置*setLocale* *useLocale*                                                     |
| 2021-04-29 | 1.8.1  | 共享时支持视频显示 <br> 增加额外会议信息 <br> 增加会议创建配置                                                                                                             |
| 2021-06-02 | 1.9.1  | 修复已知bug                                                                                                                                           |
| 2021-07-08 | 1.10.0 | 增加创建会议额外配置*scene* <br> 增加创会入会时自定义*memberTag* <br> 增加日志方法*uploadLog* *downLog*                                                                     |
| 2021-07-20 | 1.12.0 | 升级音视频SDK                                                                                                                                          |
| 2021-08-12 | 1.12.2 | 调整主讲人切换间隔 <br> 修复已知bug                                                                                                                            |
| 2021-09-09 | 2.0.4  | 订阅接口支持promise <br> 修复已知bug                                                                                                                        |
| 2021-10-28 | 2.0.10 | 增加获取会议人员布局位置接口 <br> 增加主持人离会提示是否移交权限 <br> 修复已知bug                                                                                                  |
| 2021-11-08 | 2.0.12 | 增加角色标签显示 <br> 支持一键开启全体摄像头 <br> 支持创建会议时关闭音视频 <br> 添加聊天室 <br> 修复已知bug                                                                               |
| 2022-02-17 | 2.2.5  | 添加设置焦点分辨率 <br> 添加设置默认主持人焦点配置 <br> 添加是否根据声音大小排序配置 <br> 大小流功能 <br> 聊天室定向消息 <br> 修复已知bug                                                                    |

## 快速接入

#### 开发环境准备

| 名称 | 要求 |
| :------ | :------ |
| Chrome | 74以上 |
| Safari | 12以上 |
| Node | 8以上 |
| IE | 不支持 |
| 其他 | 待验证 |

#### SDK快速接入

1. 将代码加入到页面head中（将文件路径替换为真实存在路径）

    ```js
    <script src="./NeWebMeeting_V1.10.0.js"></script>
    ```

2. 页面添加dom

    ```js
    <div id="ne-web-meeting"></div>
    ```

3. 此时全局方法neWebMeeting已注册 在需要的执行初始化

    ```js
    neWebMeeting.actions.init(800, 800, config);
    ```

4. 组件已注册，接入完成，使用组件 API使用会议功能

#### API说明

1. 初始化会议组件，设置宽高

    ```js
    const config = {
        appKey: '', //网易会议appkey
        meetingServerDomain: '', //会议服务器地址，支持私有化部署
        imPrivateConf: {// 选填，IM SDK私有化配置仅限于私有化配置时使用
        },
        neRtcServerAddresses: { // 选填，G2 SDK私有化配置仅私有化配置使用
        }
    }
    neWebMeeting.actions.init(800, 800, config)//宽，高，配置项 宽高单位是px，建议比例4：3
    ```

    **初始化如果传入了appKey和meetingServerDomain，则后续方法在非必要情况无需传入该值**

    meetingServerDomain 如果地址不带协议传，默认使用https，如果地址带协议，则根据地址协议来

    比如：传 xxx.xxx.com 则作为 https://xxx.xxx.com

    传 http://xxx.xxx.com 则使用http协议

2. 销毁WEB组件

    ```js
    neWebMeeting.actions.destroy()
    ```

3. 账号登录

    ```js
    const obj = {
      accountId: '', //账号ID
      accountToken: '', //账号Token
    }
    neWebMeeting.actions.login(obj, callback)
    ```

4. 账号密码登录

    ```js
    neWebMeeting.actions.loginWithNEMeeting(account, password, callback)
    // account 账号username
    // password 密码 无需加密，内部已封装
    ```

5. SSOToken登录

    ```js
    neWebMeeting.actions.loginWithSSOToken(ssoToken, callback)
    // ssoToken 获取到的sso登陆token
    ```

6. 创建房间
    ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //会议ID，如果为1则是随机会议ID，0为固定私人会议ID
      video: 1, // 1开启2关闭
      audio: 1, // 1开启2关闭
      meetingIdDisplayOptions: 0, // 0 都展示 1 展示长号，2 展示短号 默认为 0
      toolBarList: [], // 主区按钮自定义设置
      moreBarList: [], // 更多区按钮自定义排列
      noRename: false, // 是否开启会中改名，默认为false（开启）
      defaultWindowMode: 1, // 入会时模式，1 常规（默认）， 2白板
      noCloudRecord: false, // 开启会议录制，false（默认） 录制 true 不录制
      memberTag: '', // 成员自定义tag
      attendeeVideoOff: 0, // 成员入会后全体关闭视频，且不允许自主打开，默认允许打开
      attendeeAudioOff: 0, // 成员入会后全体静音，且不允许自主打开，默认允许打开
      showMaxCount: false, // 是否显示会议应进最大人数,需配合extraData字段设置
      showSubject: false, // 是否显示会议主题
      showMemberTag: false, // 是否显示成员标签
      extraData: '', // 扩展字段，格式为json字符串，如果showMaxCount字段设置为true，且该字段传{maxCount: 100}，会议应进最大人数为100
      scene: { // 会议场景参数
        roleTypes: [
            {
                roleType: 1, // number 1 普通参会者 2 主持人
                maxCount: 5, // number  场景角色上线
                accountIds: [], // Array<string> 场景角色限制哪些账号可以入会
            }
        ]
      },
      muteBtnConfig: {
        showMuteAllVideo: true, // 显示全体关闭视频按钮
        showUnMuteAllVideo: true, // 显示全体开启按钮
        showMuteAllAudio: true, // 显示全体静音按钮
        showUnMuteAllAudio: true, // 显示全体解除静音按钮
      },
      showFocusBtn: false, // 是否显示设置焦点后画面右上角的按钮， 默认为true
      videoProfile: { // 默认画面配置
        resolution: 180, // 默认画面分辨率
        frameRate: 15, // 默认画面帧率
      },
      focusVideoProfile: { // 焦点画面配置
        resolution: 480, // 焦点画面分辨率
        frameRate: 15, // 焦点画面帧率
      },
      enableSortByVoice: true, // 是否根据声音大小显示主画面 默认为true
      enableSetDefaultFocus: false, // 是否默认设置支持人为焦点画面 默认为false
      chatroomConfig: { // 聊天室定向消息
        tags: [], // 本端聊天室标签（填写一个即可）
        enableDirectionalTransmission: false, // 是否开启定向消息 默认为false
        defaultDirectionalTags: [] // 默认可以发送目标人员的标签组，为空则可以发送给全体人员
      }
    }
    neWebMeeting.actions.create(obj, callback)
    ```

    关于自定义按钮详细配置可以参考[自定义按钮详细介绍](#自定义按钮详细介绍)

7. 加入房间

    ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //要加入会议ID
      video: 1, // 1开启2关闭（匿名加入房间需要）
      audio: 1,  // 1开启2关闭（匿名加入房间需要）
      password: '', // 加入预约会议时可使用
      meetingIdDisplayOptions: 0, // 0 都展示 1 展示长号，2 展示短号 默认为 0
      appkey: '', //网易会议appkey（匿名加入房间需要，初始化传入则暂不需要）
      meetingServerDomain: '', //会议服务器地址，支持私有化部署, 为空则默认为云信线上服务器（匿名加入房间需要，初始化传入则暂不需要）
      toolBarList: [], // 主区按钮自定义设置
      moreBarList: [], // 更多区按钮自定义排列
      noRename: false, // 是否开启会中改名，默认为false（开启）
      defaultWindowMode: 1, // 入会时模式，1 常规（默认）， 2白板
      memberTag: '', // 成员自定义tag
      showMaxCount: false, // 是否显示会议应进最大人数
      showSubject: false, // 是否显示会议主题
      showMemberTag: false, // 是否显示成员标签
      muteBtnConfig: {
        showMuteAllVideo: true, // 显示全体关闭视频按钮
        showUnMuteAllVideo: true, // 显示全体开启按钮
        showMuteAllAudio: true, // 显示全体静音按钮
        showUnMuteAllAudio: true, // 显示全体解除静音按钮
      },
      showFocusBtn: false, // 是否显示设置焦点后画面右上角的按钮， 默认为true
      videoProfile: { // 默认画面配置
        resolution: 180, // 默认画面分辨率
        frameRate: 15, // 默认画面帧率
      },
      focusVideoProfile: { // 焦点画面配置
        resolution: 480, // 焦点画面分辨率
        frameRate: 15, // 焦点画面帧率
      },
      enableSortByVoice: true, // 是否根据声音大小显示主画面 默认为true
      enableSetDefaultFocus: false, // 是否默认设置支持人为焦点画面 默认为false
      chatroomConfig: { // 聊天室定向消息
        tags: [], // 本端聊天室标签（填写一个即可）
        enableDirectionalTransmission: false, // 是否开启定向消息 默认为false
        defaultDirectionalTags: [] // 默认可以发送目标人员的标签组，为空则可以发送给全体人员
      }
    }
    neWebMeeting.actions.join(obj, callback)
    ```

    关于自定义按钮详细配置可以参考[自定义按钮详细介绍](#自定义按钮详细介绍)

8. 结束、离开会议回调

    ```js
    neWebMeeting.actions.afterLeave(callback) // 可在初始化后执行该方法进行注册
    // 成功离开会议，成功结束会议，主持人结束会议，其他端收到通知，均会触发
    // callback会返回参数
    // 0 自己离开会议
    // 1 自己结束会议
    // 2 主持人踢出自己
    // 3 主持人结束会议
    ```

9. 当前页面成员信息

    ```js
    neWebMeeting.actions.memberInfo //内部属性：
    //nickName: 入会名称
    //audio: 音频状态
    //video: 视频状态
    //role: ‘host’ 主持人 'participant'、参会者 'AnonymousParticipant' 匿名参会者
    //avRoomUid: uid
    ```

10. 与会成员信息

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
            stream: MediaStream, // 视频流
            video:2, // 视频状态
            memberTag: '', // 成员自定义tag
        }
    }
    ```

11. 当前会议信息

    ```js
    neWebMeeting.actions.NEMeetingInfo // 当前会议信息
    // meetingId 会议ID
    // isHost 是否主持人
    // isLocked 会议是否锁定
    // shortMeetingId 短号
    // password 会议密码，没有则为空
    // sipId 会议sipId
    ```

12. 设置组件的宽高

    ```js
    neWebMeeting.actions.width = 100; // 设置宽度，单位px
    neWebMeeting.actions.height = 100; // 设置高度，单位px

    ```

13. 动态更新底部列表

    ```js
    var obj = {
        toolBarList: [], // 主区按钮自定义设置
        moreBarList: [], // 更多区按钮自定义排列
    }
    neWebMeeting.actions.updateCutomList(obj, callback)
    ```

    关于自定义按钮详细配置可以参考[自定义按钮详细介绍](#自定义按钮详细介绍)

14. 订阅用户音频流

    ```js
    var accountId = '', // 账号accountId string
        subscribe = false, // 是否订阅，true订阅 false取消订阅 boolean
        callback = (e) => {
            if(e) console.error(e)
        }; // 执行回调，包含e则执行有错误（对照错误码参考）
    neWebMeeting.actions.subscribeRemoteAudioStream(accountId, subscribe, callback)
    ```

15. 订阅全体用户音频流

    ```js
    var subscribe = false, // 是否订阅，true订阅 false取消订阅 boolean
        callback = (e) => {
            if(e) console.error(e)
        }; // 执行回调，包含e则执行有错误（对照错误码参考）
    neWebMeeting.actions.subscribeAllRemoteAudioStreams(subscribe, callback)
    ```

16. 订阅用户音频流bylist

    ```js
    var accountIds = [], // 账号accountId Array<string>
        subscribe = false, // 是否订阅，true订阅 false取消订阅 boolean
        callback = (e) => {
            if(e) console.error(e)
        }; // 执行回调，包含e则执行有错误（对照错误码参考）
    neWebMeeting.actions.subscribeRemoteAudioStreams(accountIds, subscribe, callback)
    ```

17. 成员加入，成员离开通知

    ```js
    neWebMeeting.actions.on('peerJoin', function(uid) {
      console.log('成员加入', uid);
    })
    neWebMeeting.actions.on('peerLeave', function(uid) {
      console.log('成员离开', uid);
    })
    ```
    

18. 网络情况通知[参考](https://dev.yunxin.163.com/docs/product/%E9%9F%B3%E8%A7%86%E9%A2%91%E9%80%9A%E8%AF%9D2.0/%E8%BF%9B%E9%98%B6%E5%8A%9F%E8%83%BD/%E4%BD%93%E9%AA%8C%E6%8F%90%E5%8D%87/%E9%80%9A%E8%AF%9D%E4%B8%AD%E8%B4%A8%E9%87%8F%E7%9B%91%E6%B5%8B?#%E4%B8%8A%E4%B8%8B%E8%A1%8C%E7%BD%91%E7%BB%9C%E8%B4%A8%E9%87%8F%E5%90%8C%E6%AD%A5)

    ```js
    neWebMeeting.actions.on('networkQuality', function(data) {
      console.log('网络情况', data);
      // data Array
      //uid: 房间里具体那位成员的网络情况
      //downlinkNetworkQuality：下行网络质量打分。
      //uplinkNetworkQuality：上行网络质量打分。
    })
    ```

19. 国际化配置

    ```js
        const enLocale = {
            // 相关配置项
        }
        neWebMeeting.actions.setLocale('en', enLocale);
        neWebMeeting.actions.useLocale('en');
    ```

20. 获取会议历史信息

    ```js
        neWebMeeting.actions.getHistoryMeetingItem(() => {
          console.log('上次会议信息', data);
          // 数据格式同会议信息
        })
    ```

21. 获取服务器配置信息

    ```js
        neWebMeeting.acitons.NESettingsService
        // isWhiteboardEnabled  白板是否开启      boolean
        // isCloudRecordEnabled 云端录制是否开启   boolean
    ```

22. 日志相关

    * 日志仅在本端保存24小时

    * 日志上报，start字段请上传该场会议开始时间，便于问题排查

    * 需要人员排查时请提供该场会议的meetingId

        * 日志上传

        ```js
            type LogNames = 'meetingLog'|'rtcLog'
            neWebMeeting.actions.uploadLog(
                logNames: Array<LogNames>, 
                // 日志类型 
                // meetingLog 会议日志
                // rtcLog 音视频日志
                start: number, // 开始日志时间戳 默认 0
                end: number, // 结束日志时间戳 默认 当前时间戳
            ).then((res: Array<string>) => {
                console.log(res);
                // res 上传路径以及文件名
            })

            // 例子
            // 上传全部日志
            neWebMeeting.actions.uploadLog(['meetingLog', 'rtcLog']) 
            // 上传某一日志
            neWebMeeting.actions.uploadLog(['rtcLog'])
            // 上传某一时间段日志，如最近一小时
            neWebMeeting.actions.uploadLog(['meetingLog', 'rtcLog'], Date.now() - 3600000, Date.now())

        ```

        * 日志下载
    
        ```js
            type LogNames = 'meetingLog'|'rtcLog'
            // 执行后直接触发下载
            neWebMeeting.actions.downloadLog(
                logNames: Array<LogNames>, 
                // 日志类型 
                // meetingLog 会议日志
                // rtcLog 音视频日志
                start: number, // 开始日志时间戳 默认 0
                end: number, // 结束日志时间戳 默认 当前时间戳
            )

            // 例子
            // 下载全部日志
            neWebMeeting.actions.downloadLog(['meetingLog', 'rtcLog']) 
            // 下载某一日志
            neWebMeeting.actions.downloadLog(['rtcLog'])
            // 下载某一时间段日志，如最近一小时
            neWebMeeting.actions.downloadLog(['meetingLog', 'rtcLog'], Date.now() - 3600000, Date.now())

        ```

23. 获取会议人员布局位置信息
    * 主动获取
        ```js
            neWebMeeting.acitons.layout(): Layout
            type Layout = {
                canvas: { // 画布数据
                    height: number;
                    width: number;
                },
                users: Array<LayoutUser> // 每个成员布局数据
            }
            type LayoutUser = {
                uid: number,
                x: number,
                y: number,
                width: number,
                height: number,
                isScreen?: boolean // 是否为共享屏幕
            }
        ```
    * 监听布局变化
        ```js
        neWebMeeting.actions.on('layoutChange', function(layout) {
            // todo
        })
        ```
#### 自定义按钮详细介绍

1. <span id="custom-introduction">自定义组件的基本结构</span>

```js
{
   "toolBarList":[
      {"id":0}, // 预置按钮
      {"id":1},
      {"id":2},
      {
          "id":3, // 预置按钮根据Id区分单状态与多状态
          "btnConfig":{ // 单状态按钮配置-object
            "icon":"", // 图标 url地址
            "text":"我是展示文案" // 展示文案
          },
      },
      {
          "id":5
      },
      {
          "id":20, // 预置按钮根据Id区分单状态与多状态
          "btnConfig":[{ // 多状态按钮配置-array
              "icon":"", // 图标 url地址
              "text":"", // 展示文案
          },{
              "icon":"",
              "text":"",
          }],
      },
   ],
   "moreBarList":[
      {
         "id":102,
         "type":"single", // 单状态按钮
         "btnConfig":{ // 单状态按钮配置-object
            "icon":"", // 图标 url地址
            "text":"我是展示文案" // 展示文案
         },
         "visibility":0, // 可见范围
         "injectItemClick": function(btnItem) {
             // TODO
         }
      },{
         "id":103,
         "type":"multiple", // 多状态按钮
         "btnConfig":[{ // 多状态按钮配置-array
            "icon":"", // 图标 url地址
            "text":"我是false", // 展示文案
            "status":false // 按钮状态
         },{
            "icon":"",
            "text":"我是true",
            "status":true
         }],
         "visibility":0, // 可见范围
         "btnStatus":false, // 默认按钮状态
         "injectItemClick": function(btnItem) {
          // TODO
          // 通过调整形参的btnStatus去控制按钮状态
          btnItem.btnStatus = true;
        }
      }
   ]
}
```

2. 配置项目介绍

    | 字段 | 含义 | 类型 | 必填 | 样例 |
    | :-: | :-: | :-: | :-: | :- |
    | id | 按钮的唯一标识 <br> 非预置按钮id大于100 <br> 预置按钮则小于等于100 | number | 是 | 0 |
    | type | 按钮类型 <br> 单状态：single <br> 多状态：multiple| string | 非预置按钮必填 | single |
    | btnConfig | 按钮配置项 <br> 单状态Object <br> 多状态Array[Object] | object\|array | 非预置按钮必填 | [参考样例](#custom-introduction) |
    | btnConfig下object | 图标url：icon（必填） <br> 图标文案：text（必填） <br> 图标状态：status（多状态按钮必填） | object | 非预置按钮必填 | [参考样例](#custom-introduction) |
    | visibility | 按钮可见范围 <br> 全局可见（**默认**）：0  <br> 主持人可见：1 <br> 非主持人可见：2 | number | 否 | 0 |
    | btnStatus | 多状态按钮展示状态配置字段 <br> 类型未限制需与btnConfig配置状态保持对应 | number\|boolean\|string | 非预置多状态按钮必填 | [参考样例](#custom-introduction) |
    |injectItemClick| 按钮触发回调 | function | 是 | [参考样例](#custom-introduction) |

3. 预置属性说明


    | 配置字段 | 内容(id, type) | 能否在更多区域配置 |
    | :-: | :- | :- |
    | 预置按钮唯一值（id） | 音频(0, multiple) <br> 视频(1, multiple) <br> 屏幕共享(2, multiple) <br> 参会者列表(3, single) <br> 画廊切换(5, multiple) <br> 邀请(20, single) <br> 聊天（21, single)<br> 白板（22，multiple） | false <br> false <br> true <br> false <br> true <br> false <br> false <br> true|
    | 按钮可见性（visibility）| 0总是可见(默认) <br> 1主持人可见 <br> 2非主持人可见 <br> | -- |

    预置按钮无法设置状态，只能根据预先设置的状态调整文案与icon

    | 多状态按钮id | 默认状态顺序（数组第一位与第二位） |
    | :- | :- |
    | 0 音频 | 开启，关闭 |
    | 1 视频 | 开启，关闭 |
    | 2 屏幕共享 | 结束共享，共享屏幕 |
    | 5 视图布局 | 演讲者模式，画廊模式 |

4. 使用说明以及注意事项

    * 预置按钮中，仅屏幕共享，视图切换可以放置在更多区域，其他预置按钮放置**不会生效**
    * 预置按钮，**无法**替换其默认方法，需要自定义方法的情况下请使用自定义按钮
    * 多状态的预置按钮需**按照顺序**填入需要配置的按钮信息
    * 配置按钮icon时**仅限**使用url网络资源进行配置
    * icon资源尺寸

        ```
        mdpi 24px * 24px
        xhdpi 48px * 48px
        ```

    * 配置异常时请参考返回的报错信息进行处理（即加入创建时的callback）
    * 未配置成功时，不会影响整体已有按钮配置

## 注意事项

* web会议组件sdk要求运行在**https**环境中
* 初始化以及单独设置宽高时，建议使用比例4：3
* 销毁意味着退出会议
* 销毁时节点不会销毁，仍保留一部分样式，但不会影响页面结构
* 初始化后用户需要执行登陆才可以进行创建和加入
* 会议的全部功能在创建或加入之后即可使用，无需其他额外配置
* 创建会议后会直接加入会议，无需执行join
* 登陆的用户在其他页面登陆、创建或加入会议，会影响目前已经加入会议的页面，造成互踢
* 如果期望会议组件全屏展示，需要在补充样式

    ```css
        html, body {
            height: 100%;
        }
    ```

* API方法在执行失败后，如需进行错误排查，可以通过callback输出，例：

  ```js
    const obj = {
      nickName: '', //人员昵称
      meetingId: '', //要加入会议ID
      video: 1, // 1开启2关闭（匿名加入房间需要）
      audio: 1,  // 1开启2关闭（匿名加入房间需要）
    }
    function callback(e) {
        if(e) console.log(e.name, e.message) // 有参数时证明方法异常
    }
    neWebMeeting.actions.join(obj, callback)
  ```

* 支持esmodule形式引入，如使用，请参考以下方式使用

    ```js
    import { actions } from './NeWebMeeting_V1.12.2.js'
    aciotns.init();
    // or
    import neWebMeeting from './NeWebMeeting_V1.12.2.js'
    neWebMeeting.init();

    // 需要安装@babel/plugin-transform-modules-umd并进行配置
    ```

* v1.3.1更新的初始化配置，不会影响现有的appkey和meetingServerDomain的配置，如果在login传入则优先使用login配置

* 国际化默认配置为**zh**，如果替换的**zh**下的配置，会造成配置丢失，请谨慎操作

* 如使用1.8.1版本sdk，其他端也请同样使用1.8.1及1.8.1以上的sdk，如未替换，会造成兼容性问题

* 在使用新版sdk屏幕共享功能时，请将浏览器升级至最新（至少Chrome74以上），不然会产生黑屏问题
