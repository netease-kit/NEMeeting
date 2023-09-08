
### NEMeeting Android SDK Tutorial

此示例项目演示了如何快速集成 NEMeeting SDK 实现在线会议功能，示例代码中包含了详细的API调用场景、参数封装以及回调处理。

示例项目包含的功能如下：

- 通过账号、密码完成会议SDK登录鉴权；注销登录
- 创建会议、加入会议
- 会议内提供的其他功能(如会议控制、屏幕共享等) 

### 运行示例程序

开发者根据个人需求，补充完成示例项目后，即可运行并体验会议功能。

|功能|网易会议AppKey|网易会议账号|
|:-:|:-:|:-:|
|加入会议|需要|不需要|
|创建会议|需要|需要|

#### 声明AppKey

Appkey是应用接入会议SDK的凭证，开发者首先需要在网易会议开发者平台完成申请，并将其填写至`"app/src/main/res/values/appkey.xml"`资源文件中的对应资源项上。

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--TODO-->
    <!--Replace With Your AppKey Here-->
    <string name="appkey">Your AppKey</string>

</resources>
```

完成AppKey的申请和声明后，运行示例项目可体验“加入会议”功能，但无法使用“登录”、“创建会议”功能。

#### 对接会议账号

为了体验示例项目中的创建会议功能，在以上完善应用Appkey的基础上，还需要使用会议账号完成SDK的登录鉴权。

会议SDK的登录鉴权需要提供一个有效的会议账号(可使用网易会议PaaS服务提供的创建会议账号接口完成创建)，并通过对应的账号ID和TOKEN来完成登录鉴权。

在此示例项目中，开发者需要实现`com.netease.yunxin.kit.meeting.sampleapp.data.DefaultDataRepository#getSDKAuthInfo`方法，返回当前对应AppKey下一个真实有效的会议账号的ID和TOKEN，它们会被用来作为参数完成会议SDK中 login 接口的调用。

完成会议账号的登录实现后，运行示例项目，即可体验完整的会议功能。

### 运行环境

- Android Studio 3.0 +
- 真实 Android 设备(部分模拟器会存在功能缺失或者性能问题，推荐使用真机)




