# 前提条件
> 开通 NERoom 房间组件，并选择了 线上会议 场景模板。
https://doc.yunxin.163.com/meetingkit/guide?platform=pc

# examples
![image_init.png](images/image_init.png)
## Qt 环境安装
### Windows && macOS
1. 下载 Qt 安装包
> 下载地址
```angular2html
https://developer.aliyun.com/mirror/qt/
```
> 下载命令(安装器qt-online-installer-macOS-x64-4.8.0打开)
```shell
/Volumes/qt-online-installer-macOS-x64-4.8.0/qt-online-installer-macOS-x64-4.8.0.app/Contents/MacOS/qt-online-installer-macOS-x64-4.8.0 --mirror https://mirrors.aliyun.com/qt
```
2. Qt 版本选择
> 选择 Qt 6.5.3 版本

rm -rf meeting_sdk_example_qt && 
cmake   -Bmeeting_sdk_example_qt \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_OSX_ARCHITECTURES=x86_64 \
        -DCMAKE_PREFIX_PATH=~/meeting_sdk_example_qt/6.5.3/macos \
cmake   --build meeting_sdk_example_qt --config Release