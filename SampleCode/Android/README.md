# NEMeetingKit

会议组件, 内置标准会议场景UI，可快速集成实现标准音视频会议功能。

[Release notes](https://doc.yunxin.163.com/meetingkit/docs/home-page?platform=android)

[Reference documentation](https://doc.yunxin.163.com/docs/interface/NEMeetingKit/Latest/Android/)

[Browse source](https://g.hz.netease.com/yunxin-app/xkit-flutter/-/tree/master/meeting)

### Declaring dependencies
如需添加 NEMeetingKit 的依赖项，您必须将 mavenCentral 代码库添加到项目中。

在应用或模块的 `build.gradle` 文件中添加所需组件的依赖项：

* Kotlin
```
dependencies {
    val meetingkit_version = "3.16.0"
    implementation("com.netease.yunxin.kit.meeting:meeting:$meetingkit_version")
}
```

* Groovy
```
dependencies {
    def meetingkit_version = "3.16.0"
    implementation "com.netease.yunxin.kit.meeting:meeting:$meetingkit_version"
}
```