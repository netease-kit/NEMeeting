#!/bin/sh

rm -rf meeting_sdk_example_qt
#Qt环境替换成本地Qt环境
Qt=~/Qt/6.5.3/macos

cmake   -Bmeeting_sdk_example_qt -GXcode \
        -DCMAKE_BUILD_TYPE=Debug \
        -DCMAKE_OSX_ARCHITECTURES='arm64' \
        -DCMAKE_OSX_DEPLOYMENT_TARGET=10.11 \
        -DCMAKE_PREFIX_PATH=${Qt} \
        -DCMAKE_XCODE_ATTRIBUTE_CODE_SIGN_IDENTITY="" \
        -DCMAKE_XCODE_ATTRIBUTE_CODE_SIGNING_ALLOWED=NO

cmake   --build meeting_sdk_example_qt --config Debug

${Qt}/bin/macdeployqt6 meeting_sdk_example_qt/Debug/meeting_sdk_example_qt.app
