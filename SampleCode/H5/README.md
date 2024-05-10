# NEMeetingKit 使用说明

NEMeetingKit 会议组件 H5 版

### 1. 权限申请

在开始集成之前，请确保您已完成以下操作：
联系云信商务获取开通以下权限，并联系技术支持配置产品服务和功能

1.  通过文档[应用创建和服务开通](https://github.com/netease-kit/documents/blob/main/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6/%E4%BC%9A%E8%AE%AE%E7%BB%84%E4%BB%B6/%E5%BA%94%E7%94%A8%E5%88%9B%E5%BB%BA%E5%92%8C%E6%9C%8D%E5%8A%A1%E5%BC%80%E9%80%9A.md)完成相关权限开通；
2.  获取对应 AppKey；

### 2. 集成

#### 方式 1：sdk 直接集成

```js
import NEMeetingKit from "./NEMeetingKit_h5_v4.0.1.js";
```

或者

```html
<script src="./NEMeetingKit_h5_v4.0.1.js"></script>
```

#### 方式 2：npm 包集成

1. 安装依赖

```sh
npm install nemeeting-web-sdk --save
```

2. 集成

```js
import NEMeetingKit from "nemeeting-web-sdk";
```

### 3.使用

```html
<!-- 预留一个dom用于挂载会议组件 -->
<div id="ne-web-meeting"></div>
```

```js
/* 初始化
 * 需要一个id为ne-web-meeting的元素容器用于挂载会议组件
 * @param width：宽度(px)，为0则表示100%
 * @param height：高度(px)，为0则表示100%
 * @param config：入会配置
 * @param callback： 回调
 */
const config = {
  appKey: "", //云信服务appkey
};
NEMeetingKit.actions.init(0, 0, config, () => {
  console.log("init回调");

  // 检测浏览器兼容性
  NEMeetingKit.actions.checkSystemRequirements(function (err, result) {
    let str = "";
    if (err) {
      str = err;
    } else {
      str = result ? "支持" : "不支持";
    }
    console.log("浏览器兼容性检测结果：", str);
  });

  // 事件监听
  NEMeetingKit.actions.on("peerJoin", (members) => {
    console.log("成员加入回调", members);
  });
  NEMeetingKit.actions.on("peerLeave", (uuids) => {
    console.log("成员离开回调", uuids);
  });
  NEMeetingKit.actions.on("roomEnded", (reason) => {
    console.log("房间被关闭", reason);
  });
  NEMeetingKit.actions.addMeetingStatusListener({
    onMeetingStatusChanged: (status, arg, obj) => {
      console.log("会议状态变更了: ", status, arg, obj);
    },
  });

  // 获取会议相关信息
  const NEMeetingInfo = NEMeetingKit.actions.NEMeetingInfo; // 会议基本信息
  const memberInfo = NEMeetingKit.actions.memberInfo; // 当前成员信息
  const joinMemberInfo = NEMeetingKit.actions.joinMemberInfo; // 入会成员信息
});

// token登录
NEMeetingKit.actions.login(
  {
    accountId: accountId, // 账号
    accountToken: accountToken, // token
  },
  function (e) {
    console.log("login回调", e);
  }
);

// 加入会议，需要先进行token登录
NEMeetingKit.actions.join(
  {
    meetingId: meetingId, // 会议号
    nickName: nickName, // 会中昵称
    video: 1, // 视频开关，1为打开2为关闭
    audio: 1, // 音频开关，1为打开2为关闭
  },
  function (e) {
    console.log("加入会议回调", e);
  }
);

// 登出
NEMeetingKit.actions.logout(function (e) {
  console.log("logout回调", e);
});

// 取消监听
NEMeetingKit.actions.off("peerJoin");
NEMeetingKit.actions.off("peerLeave");
NEMeetingKit.actions.off("roomEnded");
NEMeetingKit.actions.removeMeetingStatusListener();

// 销毁sdk
NEMeetingKit.actions.destroy(); // 销毁
```
