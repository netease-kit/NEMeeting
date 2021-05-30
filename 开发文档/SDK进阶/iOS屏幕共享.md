
该文档主要说明如何在网易会议 iOS SDK 中接入屏幕共享功能。

## 必要条件

为了能够在网易会议 iOS SDK 中使用屏幕共享功能，需要满足以下条件：

- 会议SDK版本需**大于等于1.9.0**
- iOS 设备需运行**iOS 12及以上**的系统版本

## 实现流程

基于 iOS 系统的屏幕共享功能，需要在 App Extension 中通过 iOS 原生的 ReplayKit 特性实现录屏进程，并配合主 App 进程进行推流。需要进行屏幕共享的时候，使用 Apple ReplayKit 框架进行屏幕录制，接收系统采集的屏幕图像，并将其发送给 SDK 以传输视频流数据。

屏幕共享的主要流程包括：

### 步骤一 创建 App Group
1. 在 [Certificates, Identifiers & Profiles](https://developer.apple.com/account/resources) 页面中注册 App Group。操作步骤请参考[注册 App Group](https://help.apple.com/developer-account/?lang=en#/dev1d7b147dc)。
2. 为您的 App ID 启用 App Group 功能。操作步骤请参考[启用 App Group](https://help.apple.com/developer-account/?lang=en#/dev4cb6dfbdb)。
3. 重新下载 Provisioning Profile 并配置到 XCode 中。


### 步骤二 创建 Extension 录屏进程

创建一个类型为 Broadcast Upload Extension 的Target，用于存放屏幕共享功能的实现代码。

1. 在 Xcode 中打开项目的工程文件。

2. 在菜单中选择 Editor > Add Target...。

3. 在 iOS 页签中 选择 Broadcast Upload Extension，并单击 Next。

    ![](https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fchose_ext.jpg)

4. 在 Product Name 中为 Extension 命名，单后单击 Finish。

以上几步之后，xcode 下会为这个 Extension Target 创建对应的 Group，并自动生成了 `SampleHandler.h` 与 `SampleHandler.m`两个文件。


### 步骤三 在 Extension 进程中采集并发送数据

1. 在项目的 `Podfile` 文件中添加 `pod 'NEScreenShareBroadcaster', '0.0.7'` ，并执行 `pod install`， 引入 `NEScreenShareBroadcaster` framework 依赖 。该 framework 封装了数据的采集与发送，供 Extension 进程调用。

2. 在 `SampleHandle.m` 中 使用 步骤一 中创建的 App Group 初始化 `NEScreenShareBroadcaster`，并设置相关参数；同时将 `SampleHandle` 中的对应方法转发至 `NEScreenShareBroadcaster`。代码如下(具体实现时可原样Copy，只需修改 kAppGroup 的部分即可)：

```objc
#import "SampleHandler.h"

static NSString *kAppGroup = @"<Your_App_Group>";

@interface SampleHandler () <NEScreenShareBroadcasterDelegate>

@property (nonatomic, strong) NEScreenShareBroadcaster *broadcaster;

@end

@implementation SampleHandler

- (void)broadcastStartedWithSetupInfo:(NSDictionary<NSString *,NSObject *> *)setupInfo {
 
    // 这里初始化 NEScreenShareBroadcaster
    NEScreenShareBroadcasterOptions *options = [[NEScreenShareBroadcasterOptions alloc] init];

    // App Group 参数必需
    options.appGroup = kAppGroup;

    // 设置共享分辨率为 720P； 
    // 这里根据屏幕尺寸，选择是以宽还是以高为基准进行裁剪
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat scale = [UIScreen mainScreen].scale;
    CGFloat screenWidth = screenRect.size.width * scale;
    CGFloat screenHeight = screenRect.size.height * scale;
    if (720 / screenWidth > 1280 / screenHeight) {
        options.targetFrameSize = CGSizeMake(0, 1280); // 根据高度缩放
    } else {
        options.targetFrameSize = CGSizeMake(720, 0); // 根据宽度来缩放
    }
    options.delegate = self;
    self.broadcaster = [[NEScreenShareBroadcaster alloc] initWithOptions:options];
}

- (void)broadcastPaused {
    // 中转方法调用至 NEScreenShareBroadcaster
    [self.broadcaster broadcastPaused];
}

- (void)broadcastResumed {
    // 中转方法调用至 NEScreenShareBroadcaster
    [self.broadcaster broadcastResumed];
}

- (void)broadcastFinished {
    // 中转方法调用至 NEScreenShareBroadcaster
    [self.broadcaster broadcastFinished];
}

- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
    
    switch (sampleBufferType) {
        case RPSampleBufferTypeVideo:
            // 使用 NEScreenShareBroadcaster 发送屏幕数据
            [self.broadcaster sendVideoSampleBuffer:sampleBuffer];
            break;
        case RPSampleBufferTypeAudioApp:
            // Handle audio sample buffer for app audio
            break;
        case RPSampleBufferTypeAudioMic:
            // Handle audio sample buffer for mic audio
            break;
            
        default:
            break;
    }
}

// 会议SDK停止共享时会回调该方法
// 开发者需要响应该请求，并调用 finishBroadcastWithError 结束 Extension 进程
#pragma mark - NEScreenShareBroadcasterDelegate
- (void)onHostRequestFinishBroadcast {
    NSError *error = [NSError errorWithDomain:NSStringFromClass(self.class)
                                         code:0
                                     userInfo:@{
                                         NSLocalizedFailureReasonErrorKey:NSLocalizedString(@"屏幕共享已结束。", nil)
                                     }];
    [self finishBroadcastWithError:error];
}

@end

```

### 步骤四 在主进程中初始化会议 SDK 时传入相同的 App Group 

使用与 Extension 进程相同的 App Group 初始化会议 SDK。代码如下：

```objc

NEMeetingSDKConfig *config = [[NEMeetingSDKConfig alloc] init];
config.appKey = kAppKey; // 会议AppKey
config.appName = @"APPName";
config.broadcastAppGroup = @"<Your_App_Group>"; // 指定 App Group

// 其他初始化参数设置
// ...

[[NEMeetingSDK getInstance] initialize:config
            callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
    if (resultCode == ERROR_CODE_SUCCESS) {
        //TODO when initialize success
    } else {
        //TODO when initalize fail
    }
}];
```

完成以上几步之后，即可开启会议，通过共享菜单体验。


## 注意事项

- 主进程与 Extension 进程必须使用相同的 App Group 才能共享成功
- iOS 系统对 Extension 进程有 50M 内存大小的限制，超过该限制会导致 Extension 进程被强制终止，共享结束。超过720P的共享分辨率很容易导致内存超出限制，因此 SDK 建议共享分辨率设置为720P以下
