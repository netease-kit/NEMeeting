
本文旨在说明如何在组件化`Meeting-SDK`中开启NIM复用功能。

## NIM复用的使用场景

NIM复用属于`Meeting-SDK`提供的进阶功能，大多数情况下，开发者并不需要开启该功能。当且仅当`应用中接入Meeting-SDK的同时，也需要独立接入云信IM-SDK`时，需要考虑开启复用。

## 背景知识介绍

[IM-SDK](https://netease.im/im)是网易云信提供的IM即时通讯SDK，网易会议`Meeting-SDK`在底层实现上，依赖了`IM-SDK`提供的**IM长连接通道**，主要用来完成会议中的各种**会控操作**。而为了能够建立起长连接通道，需要使用IM账号完成`IM-SDK`的登录。因此，`在Meeting-SDK中，一个Meeting账号会唯一对应一个IM账号；当开发者调用NEMeetingSDK#login接口进行登录时，SDK内部会查询并获取到该Meeting账号对应的IM账号，自动登录IM-SDK以便建立起长连接通道`。总而言之，`Meeting-SDK`与`IM-SDK`的对应关系描述如下：

```txt
Meeting-AppKey   -------- 唯一绑定 -------->  IM-AppKey

Meeting-Account  -------- 唯一绑定 -------->  IM-Account
```

> **上面的绑定关系是由会议后台系统自动生成和维护的，即自动生成IM-AppKey与IM-Account；一般而言，开发者不用关心。但如果有需要，也可以进行自定义。**


## 复用与不复用时SDK行为的差异

在复用开启或关闭情况下，Meeting-SDK的行为会表现得不一致，主要是：

|功能|初始化|登录|匿名入会|
|----|----|----|----|
|复用关闭|Meeting-SDK会自动初始化IM-SDK|会获取Meeting账号对应的IM账号自动登录IM-SDK|支持匿名入会|
|复用开启|Meeting-SDK不会自动初始化IM-SDK，由外部应用负责初始化|不自动登录IM-SDK，会判断Meeting账号与当前IM账号的对应关系来决定Meeting-SDK登录是否成功|不支持匿名入会|

## 复用步骤

开启复用功能，需要进行一系列必要的设置，下面进行一一说明。

1. 重新设置Meeting-AppKey与IM-AppKey的绑定关系
   
   默认情况下，Meeting-AppKey在申请创建时，会自动创建并绑定一个IM-AppKey(假设为AppKey1)，IM-AppKey用于初始化IM-SDK。同时因为开发者需要独立接入IM-SDK，因此也会通过网易云信后台申请到另外一个IM-AppKey(假设为AppKey2)。这两个IM-AppKey是不相同的，因此，这里首先需要重新设置绑定关系，即`将Meeting-AppKey绑定到IM-AppKey2上`，开发者可通过`网易会议开发者后台或联系技术客服`进行设置。

2. 将Meeting账号绑定到IM账号
   
   一个给定的IM账号由(imAccid、imToken)二元组唯一标识，假定现在需要复用该IM账号，则需要首先创建一个绑定到该IM账号的Meeting账号。我们需要通过[创建会议账号RESTApi](https://github.com/netease-im/NEMeeting/blob/master/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/REST%20APIs/user_guide.md#%E4%BC%9A%E8%AE%AE%E8%B4%A6%E5%8F%B7%E5%88%9B%E5%BB%BA)来创建会议账号，`入参传入要复用的IM账号的imAccid、imToken`。这样创建的Meeting账号就与该IM账号绑定到一起了，同时该接口返回Meeting账号的accountId、accountToken，可用来登录Meeting-SDK。


3. 使用Meeting账号登录Meeting-SDK
   
   完成以上两个步骤之后，就具备了在各端上开启复用的前提条件。下面以Android端为例给出具体示例代码：

   1. SDK初始化
   
      在Application中进行相关SDK的初始化操作。`在开启复用之后，Meeting-SDK不会自动进行IM-SDK的初始化，因此应用层需要显示进行IM-SDK初始化。同时，在初始化Meeting-SDK时，打开NEMeetingSDKConfig#reuseNIM开关`。如下：
      

      ```java
      public class MyMeetingApplication extends Application {
          
          @Override
          public void onCreate() {
            super.onCreate();

            // 初始化 IM-SDK (仅在复用时需要显示初始化)
            SDKOptions sdkOptions = SDKOptions.DEFAULT;
            // 具体参数由应用层根据实际情况传入
            //...
            //...
            NIMClient.config(this, null, sdkOptions);
            
            //初始化 Meeting-SDK
            NEMeetingSDKConfig config = new NEMeetingSDKConfig();
            config.appKey = context.getString(R.string.appkey);
            config.appName = context.getString(R.string.app_name);
            // 打开IM复用开关
            config.reuseNIM = true;
            // 其他初始化参数根据实际情况传入
            // ...
            // ...
            NEMeetingSDK.getInstance().initialize(this, config, callback);
          }
      }
      ```

   2. 登录IM-SDK
   
      开启IM复用时，应用层需要确保已经提前使用与Meeting账号对应的IM账号登录了IM-SDK，可通过IM账号的imAccid与imToken来完成登录：

      ```java
      NIMClient.getService(AuthService.class).login(new LoginInfo(imAccid, imToken, imAppKey));
      ```

   3. 登录Meeting-SDK
   
      最后一步，就是使用对应的Meeting账号登录Meeting-SDK，accountId与accountToken就是上面通过创建账号接口返回的。如下：

      ```java
      NEMeetingSDK.getInstance().login(accountId, accountToken, callback);
      ```

      在登录接口的回调中，我们可以检查错误码判断登录操作是否成功。如果登录成功，说明开启复用成功，否则复用失败。


## 错误码说明

|错误码|说明|修复|
|:----:|:----:|:----:|
|-3|开启了复用，但IM-SDK没有登录|确保IM-SDK已经登录后再登录Meeting-SDK|
|-5|开启了复用，IM-SDK已经登录，但账号不匹配|重新创建会议账号，并传入对应的imAccid与imToken|
