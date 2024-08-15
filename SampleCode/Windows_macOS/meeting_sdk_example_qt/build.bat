@echo off
if exist "meeting_sdk_example_qt" (
    rmdir /s /q "meeting_sdk_example_qt"
)

:: Qt环境替换成本地Qt环境
cmake   -Bmeeting_sdk_example_qt -DCMAKE_GENERATOR_PLATFORM=x64 -DCMAKE_PREFIX_PATH=D:\Qt\6.7.2\msvc2019_64
cmake   --build meeting_sdk_example_qt --config Release

xcopy /E /I /Y "SDK\BIN\*" "..\meeting_sdk_example_qt\Release\"
set QT_windeployqt=%QT_PLUGIN_PATH%\..\..\bin\windeployqt.exe
call %QT_windeployqt% .\meeting_sdk_example_qt\Release\meeting_sdk_example_qt.exe