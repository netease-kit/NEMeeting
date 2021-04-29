QT += quick quickcontrols2

CONFIG      += c++11
TARGET      = NEMeetingSample
DESTDIR     = $$PWD/bin
MOC_DIR     = $$PWD/tmp/moc
OBJECTS_DIR = $$PWD/tmp/obj
UI_DIR      = $$PWD/tmp/ui
RCC_DIR     = $$PWD/tmp/rcc

# The following define makes your compiler emit warnings if you use
# any Qt feature that has been marked deprecated (the exact warnings
# depend on your compiler). Refer to the documentation for the
# deprecated API to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

HEADERS += \
    nemeeting_manager.h

SOURCES += main.cpp \
    nemeeting_manager.cpp

RESOURCES += qml.qrc

win32 {
    INCLUDEPATH += $$PWD/include
    LIBS += -L$$PWD/lib -lnem_hosting_module
    DEPENDPATH += $$PWD/bin
}

macx {
    INCLUDEPATH += $$PWD/lib/nem_hosting_module.framework/Headers
    LIBS += -F$$PWD/lib -framework nem_hosting_module
    DEPENDPATH += $$PWD/bin

    IPC_SERVER_SDK_FRAMEWORK.files = $$PWD/lib/nem_hosting_module.framework
    IPC_SERVER_SDK_FRAMEWORK.path = /Contents/Frameworks

    NEM_UI_SDK_APP.files = $$PWD/bin/NetEaseMeetingHost.app
    NEM_UI_SDK_APP.path = /Contents/Frameworks

    QMAKE_BUNDLE_DATA += IPC_SERVER_SDK_FRAMEWORK \
                         NEM_UI_SDK_APP
}

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Additional import path used to resolve QML modules just for Qt Quick Designer
QML_DESIGNER_IMPORT_PATH =

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target
