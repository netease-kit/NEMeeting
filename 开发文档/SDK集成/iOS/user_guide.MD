## 概述

网易会议iOS SDK提供了一套简单易用的接口，允许开发者通过调用NEMeeting SDK(以下简称SDK)提供的API，快速地集成音视频会议功能至现有iOS应用中。

## 变更记录

| 日期 | 版本 | 变更内容 |
| :------: | :------: | :------- |
| 2020-07-10  | 1.0.0 | 首次正式发布 |
| 2020-08-31 | 1.1.0 | 新增如下接口：<br />     `NEMeetingSDK#isInitialized`查询SDK初始化状态<br />     `NEMeetingService#getMeetingStatus`查询当前会议状态<br />     会议设置服务NESettingService用于保存和查询用户的相关会议选项 |
| 2020-09-04 | 1.2.0 | 新增如下接口：<br />    `NEMeetingService#getCurrentMeetingInfo` 获取当前会议信息<br />    `NEMeetingOptions#noInvite` 配置会议中是否显示"邀请"按钮<br />    `NEMeetingOptions#noChat` 配置会议中是否显示"聊天"按钮<br />    `NEMeetingOptions#injectedMoreMenuItems` <br />    "更多"菜单中的自定义注入菜单项<br />    `MeetingServiceListener增加onInjectedMenuItemClick:meetingInfo:`<br />    自定义菜单按钮点击事件回调 |

## 快速接入

#### 开发环境准备

| 名称 | 要求 |
| :------ | :------ |
| iOS版本 | 9.0以上 |
| CPU架构支持 | ARM64、ARMV7 |
| IDE | XCode |
| 其他 | CocoaPods |

#### SDK快速接入

1. 新建iOS工程

    a. 运行XCode，选择Create a new Xcode project，选择Single View App，选择Next。
    ![new android project](images/new_project.png)
    
    b. 配置工程相关信息，选择Next。
    ![configure project](images/configure_project.png)
    
    c. 然后选择合适的工程本地路径，选择Create完成工程创建。

2. 通过CocoaPods集成SDK

    进入到工程路径执行pod命令，生成Podfile文件，注意CocoaPods版本使用1.9.1以上的，防止因为版本过低导致无法拉取sdk。
    ```groovy
    pod init
    ```
    打开Podfile文件添加如下代码，保存。

    ```
    pod 'NEMeetingSDK'
    ```

    执行pod命令，安装SDK

    ```
    pod install
    ```

3. 权限处理

    网易会议SDK正常工作需要摄像头、麦克风权限，用户需要在工程中的Info.list文件中配置相关的权限信息

    ![auth](images/auth.png)

    以上权限需要在进入会议之前由用户根据需要进行权限申请，权限申请的代码如下：

    ```objective-c
    //相机权限申请
    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo 
     								         completionHandler:^(BOOL granted) {}];
    //麦克风权限申请
    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio 
                             completionHandler:^(BOOL granted) {}];
    ```

4. 渲染view注册。在info.plist文件中注册平台渲染视图，保证正常渲染。

    `io.flutter.embedded_views_preview     String       YES`
    
    ![viewconfig](/Users/zhanggenning/Documents/meeting-sdk-docs/ios-sdk/images/viewconfig.png)
    
5. SDK初始化

    在使用SDK其他功能之前首先需要完成SDK初始化，初始化操作建议在**AppDelegate.m**的**application:didFinishLaunchingWithOptions:**方法执行。代码示例如下：
    ```objective-c
        NEMeetingSDKConfig *config = [[NEMeetingSDKConfig alloc] init];
        config.appKey = [DemoConfig shareConfig].appKey;
        [[NEMeetingSDK getInstance] initialize:config
                                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
            if (resultCode == ERROR_CODE_SUCCESS) {
                //TODO when initialize success
            } else {
                //TODO when initalize fail
            }
        }];
    ```

    **注意：其他操作一定更要等到初始化接口的回调返回之后再执行，否则会失败。**

6. 调用相关接口完成特定功能，详情请参考API文档。

- 登录鉴权
    ```objective-c
    //[NEMeetingSDK getInstance]
    - (void)login:(NSString *)account
        token:(NSString *)token
         callback:(NECallbackOnResult)callback
    ```
    
- 创建会议
    ```objective-c
    //[[NEMeetingSDK getInstance] getMeetingService]
    - (void)startMeeting:(NEStartMeetingParams *)param
                    opts:(NEStartMeetingOptions *)opts
                callback:(NECallbackOnResult)callback;
    ```
- 加入会议
    ```objective-c
    //[[NEMeetingSDK getInstance] getMeetingService]
    - (void)joinMeeting:(NEJoinMeetingParams *)param
                   opts:(NEJoinMeetingOptions *)opts
               callback:(NECallbackOnResult)callback;
    ```
- 注销登录
    ```objective-c
    //[NEMeetingSDK getInstance]
    - (void)logout:(NECallbackOnResult)callback;
    ```

## 业务开发

### 初始化

#### 描述

在使用SDK其他接口之前，首先需要完成初始化操作。

#### 业务流程

1. 配置初始化相关参数

```objective-c
NEMeetingSDKConfig *config = [[NEMeetingSDKConfig alloc] init];
config.appKey = [DemoConfig shareConfig].appKey; //应用APPKey
```

2. 调用接口并进行回调处理，该接口无额外回调结果数据

```objective-c
[[NEMeetingSDK getInstance] initialize:config
                          callback:^(NSInteger resultCode, NSString *resultMsg, id result) 
{
			if (resultCode == ERROR_CODE_SUCCESS) {
      		//初始化成功
      } else {
          //初始化失败
      }
}];
```

#### 注意事项

- 其他操作一定更要等到初始化接口的回调返回之后再执行，否则会失败

--------------------

### 登录鉴权

#### 描述

请求SDK进行登录鉴权，只有完成SDK登录鉴权才允许创建会议。

#### 业务流程

1. 获取登录用账号ID和Token。Token由网易会议应用服务器下发，但SDK不提供对应接口获取该信息，需要开发者自己实现。

```objective-c
NSString *accountId = @"accountToken";
NSString *accountToken = @"accountToken";
```

2. 登录并进行回调处理，该接口无额外回调结果数据

```objective-c
[[NEMeetingSDK getInstance] login:accountId
                            token:accountToken
                         callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
      if (resultCode == ERROR_CODE_SUCCESS) {
         	//登录成功
      } else {
					//登录失败
      }
}];
```

#### 注意事项

- SDK不提供账号注册机制，第三方应用集成SDK时需要为第三方应用的用户帐号绑定网易会议系统中企业管理员开通的员工帐号，第三方应用的用户帐号和企业员工帐号是1:1映射的。

--------------------

### 创建会议

#### 描述

在已经完成SDK登录鉴权的状态下，创建并开始一个新的会议。

#### 业务流程

1. 配置创建会议用的相关参数

```objective-c
NEStartMeetingParams *params = [[NEStartMeetingParams alloc] init]; //会议参数
params.meetingId = @"123456789";    //会议号
params.displayName = @"我的会议昵称;  //会议昵称

NEStartMeetingParams params = new NEStartMeetingParams();   //会议参数
params.meetingId = "123456789";                             //会议号
params.displayName = "我的会议昵称";                          //会议昵称

NEStartMeetingOptions *options = [[NEStartMeetingOptions alloc] init]; //会议选项
options.noVideo = YES; //入会时关闭视频，默认为YES
options.noAudio = YES; //入会时关闭音频，默认为YES
```

2. 调用接口并进行回调处理。该接口无额外回调结果数据，可根据错误码判断是否成功

```objective-c
NEMeetingService *meetingServce = [NEMeetingSDK getInstance].getMeetingService;
[meetingServce startMeeting:params
                       opts:options
                   callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode == ERROR_CODE_SUCCESS) {
        //创建会议成功
    } else {
        //创建会议失败
    }
}];
```

3. 创建会议成功后，SDK会拉起会议界面并接管会议逻辑，开发者无需做其他处理。创会人会自动成为该会议的主持人，可进行相关的会议控制操作。其他参会者可通过该会议号加入到该会议中来。

#### 注意事项

- 创建会议时，会议号可以配置为个人会议号(登录后可通过**[NEMeetingSDK getInstance].getAccountService**获取)，或者置空(此时由服务器随机分配会议号)。
- 该接口仅支持**在登录鉴权成功后调用**，其他状态下调用不会成功

--------------------

### 加入会议

#### 描述

在已登录或未登录的状态下，加入一个当前正在进行中的会议。

#### 业务流程

1. 配置加入会议用的相关参数

```objective-c
NEJoinMeetingParams *params = [[NEJoinMeetingParams alloc] init]; //会议参数
params.meetingId = @"123456789";    //会议号
params.displayName = @"我的会议昵称;  //会议昵称

NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init]; //会议选项
options.noAudio = YES;  //入会时关闭视频，默认为YES
options.noVideo = YES;  //入会时关闭音频，默认为YES
```

2. 调用接口并进行回调处理。该接口无额外回调结果数据，可根据错误码判断是否成功

```objective-c
NEMeetingService *meetingServce = [NEMeetingSDK getInstance].getMeetingService;
[meetingServce joinMeeting:params
                      opts:options
                  callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode == ERROR_CODE_SUCCESS) {
        //加入会议成功
    } else {
        //加入会议失败
    }
}];
```

3. 加入会议成功后，SDK会拉起会议界面并接管会议逻辑，开发者无需做其他处理。

#### 注意事项

- 会议号不能为空，需要配置为真实进行中的会议ID
- 该接口支持登录和未登录状态调用

--------------------

### 监听会议状态

#### 描述

通过注册会议状态回调接口，可获取到会议状态变更的通知。

#### 业务流程

1. 注册delegate，在回调中接收会议状态。

```objective-c
//会议添加监听
[[NEMeetingSDK getInstance].getMeetingService addListener:self];

//MeetingServiceListener协议实现
- (void)onMeetingStatusChanged:(NEMeetingEvent *)event {
     //根据event处理相关业务
}
```

2. 停止接收会议状态

```objective-c
[[NEMeetingSDK getInstance].getMeetingService removeListener:self];
```

#### 注意事项

- 在SDK初始化成功后可以调用

--------------------

### 监听会议状态

#### 描述

在完成登录授权状态下，获取当前账号关联的个人会议ID

#### 业务流程

1. 确认已经通过入会方式（加入会议/创建会议/匿名入会）在会议内
2. 调用接口并进行回调处理。该接口的回调数据类型为NEMeetingInfo对象类型；如果当前无正在进行中的会议，则回调数据对象为空，错误原因请参考错误码类型。

```objective-c
 [[NEMeetingSDK getInstance].getMeetingService getCurrentMeetingInfo:^(NSInteger resultCode, NSString * _Nonnull resultMsg, NEMeetingInfo * _Nonnull info) {
    if (resultCode != ERROR_CODE_SUCCESS) {
        //查询失败
    } else {
       //查询成功
    }
}];
```

#### 注意事项

- 在SDK进入会议状态，才能获取当前会议信息

--------------------

### 获取个人会议号

#### 描述

在完成登录授权状态下，获取当前账号关联的个人会议ID。

#### 业务流程

1. 确认已完成SDK登录鉴权

2. 调用接口并进行回调处理。该接口的回调结果数据类型为字符串类型，可根据错误码判断是否成功

```objective-c
NEAccountService *accountService = [NEMeetingSDK getInstance].getAccountService;
[accountService getPersonalMeetingId:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode == ERROR_CODE_SUCCESS) {
        //获取个人会议号成功，result即为个人会议号
    } else {
        //获取个人会议号失败
    }
}];
```

3. 获取个人会议号后，可用于创建会议

#### 注意事项

- 仅在已登录状态下才会成功返回数据

--------------------

### 自定义会中【更多】菜单内容

#### 描述

在更多菜单中注入自定义选项，方便用户业务扩展。

#### 业务流程

	1. 配置匿名入会/创建会议/加入会议的Options相关参数，增加自定义列表

  ```objective-c
NEJoinMeetingOptions *options = [[NEJoinMeetingOptions alloc] init];
options.noChat = NO;
options.noInvite = NO;

//配置自定义菜单
NSMutableArray *menuItems = [NSMutableArray array];
for (int i = 0; i < 3; i++) { //最多不能超过3个，超过之后只取前3个
    NEMeetingMenuItem *item = [[NEMeetingMenuItem alloc] init];
    item.itemId = 100 + i; //必须大于100，且要保证不重复
    item.title = [@"测试Title" stringByAppendingString:@(i).stringValue];
}
options.injectedMoreMenuItems = menuItems;
  ```

2. 设置回调接口开始监听，并在回调方法中处理自定义按钮事件

```objective-c
//添加回调监听
[[NEMeetingSDK getInstance].getMeetingService addListener:self];

//实现MeetingServiceListener协议方法
- (void)onInjectedMenuItemClick:(NEMeetingMenuItem *)menuItem
                    meetingInfo:(NEMeetingInfo *)meetingInfo {
      //menuItem 是点击的item，需要用户根据itemId进行匹配
      //meetingInfo 是当前的会议状态
}

```

#### 注意事项

- 自定义会中【更多】菜单内容，需要在入会前完成设置，在会议中设置不会生效
- 默认【更多】菜单内容中的邀请和聊天，支持隐藏/显示配置
- 合法的自定义菜单项需满足以下条件：
  - itemId >= 100，且为整数Int类型
  - title不为空和空格（对空格做trim处理），且长度不大于10字符
- 会议自定义菜单项，更多菜单中最多支持添加三个自定义菜单项，如果超过三个仅显示前三个合法自定义按钮

--------------------

### 注销

#### 描述

请求SDK注销当前已登录账号，返回未登录状态。

#### 业务流程

1. 调用接口并进行回调处理。该接口无额外回调结果数据，可根据错误码判断是否成功

```objective-c
[[NEMeetingSDK getInstance] logout:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode == ERROR_CODE_SUCCESS) {
        //注销登录成功
    } else {
        //注销登录失败
    }
}];
```

#### 注意事项

- 账号注销后，登录状态被清空，不再允许创建会议

### 使用会议设置服务

#### 描述

通过会议设置服务，可设置和查询用户的当前会议设置，如入会时的音视频开关、会议持续时间的显示等。

#### 业务流程

1. 获取会议设置服务

```objective-c
NESettingsService *settingsService = [NEMeetingSDK getInstance].getSettingsService;
```

2. 调用不同接口保存设置项或查询设置项

```objective-c
// 设置并保存会议设置
[settingsService enableShowMyMeetingElapseTime:YES];
[settingsService setTurnOnMyAudioWhenJoinMeeting:YES];
[settingsService setTurnOnMyVideoWhenJoinMeeting:YES];

//查询会议设置
BOOL showMeetingElapseTimeEnabled = [settingsService isShowMyMeetingElapseTimeEnabled];
BOOL audioEnabled = [settingsService isTurnOnMyAudioWhenJoinMeetingEnabled];
BOOL videoEnabled = [settingsService isTurnOnMyVideoWhenJoinMeetingEnabled];
```

#### 注意事项

- 针对已登录用户而言，每个用户有自己独立的一份会议设置；其他所有未登录用户、匿名用户共享一份会议设置。
- 会议设置项仅在当前设备上保存，不会漫游。
- 调用创建会议/加入会议接口时，如果接口中`NEMeetingOptions`入参为`null`，SDK会使用会议设置服务中已保存的相关配置进行创会/入会。