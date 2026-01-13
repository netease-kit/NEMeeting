@echo off
if exist "build" (
    rmdir /s /q "build"
)

:: 设置你的QT_PATH
:: Qt环境替换成本地Qt环境
set QT_PATH=D:\Qt\6.7.2\msvc2019_64

cmake -Bbuild -DCMAKE_GENERATOR_PLATFORM=x64 -DCMAKE_PREFIX_PATH=%QT_PATH%
cmake --build build --config Release

:: 复制SDK\BIN\下的所有文件到 build\Release\
xcopy /E /I /Y "SDK\BIN\*" ".\build\Release\"
set QT_windeployqt=%QT_PATH%\bin\windeployqt.exe
call %QT_windeployqt% .\build\Release\meeting_sdk_example_qt.exe
.\build\Release\meeting_sdk_example_qt.exe