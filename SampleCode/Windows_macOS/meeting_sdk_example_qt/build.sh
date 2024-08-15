#!/bin/sh

rm -rf meeting_sdk_example_qt
#Qt环境替换成本地Qt环境
Qt=~/Qt/6.5.3/macos
#cmake   -Bmeeting_sample \
#        -DCMAKE_BUILD_TYPE=Release \
#        -DCMAKE_OSX_ARCHITECTURES=x86_64 \
#        -DCMAKE_PREFIX_PATH=~/meeting_sdk_example_qt/6.5.3/macos

cmake   -Bmeeting_sdk_example_qt \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_OSX_ARCHITECTURES='arm64;x86_64' \
        -DCMAKE_OSX_DEPLOYMENT_TARGET=10.11 \
        -DCMAKE_PREFIX_PATH=${Qt}

cmake   --build meeting_sdk_example_qt --config Release

# 如果需要打包，则需要执行以下命令：并且要签名
# ${Qt}/bin/macdeployqt6 meeting_sdk_example_qt/meeting_sdk_example_qt.app
# codesign --timestamp --options=runtime -f -s "你的签名ID" -v meeting_sdk_example_qt/meeting_sdk_example_qt.app --deep
# 启动Sample
./meeting_sdk_example_qt/meeting_sdk_example_qt.app/Contents/MacOS/meeting_sdk_example_qt