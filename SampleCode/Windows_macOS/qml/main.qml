import QtQuick 2.12
import QtQuick.Window 2.12
import QtQuick.Layouts 1.12
import QtQuick.Controls 2.12

Window {
    id: mainWindow
    visible: true
    width: 800
    height: 600
    title: qsTr("NetEase Meeting SDK Sample")

    Component.onCompleted: {
        pageLoader.setSource(Qt.resolvedUrl('qrc:/qml/Login.qml'))
        meetingManager.initialize()
    }

    ToastManager {
        id: toast
    }

    Loader {
        id: pageLoader
        anchors.fill: parent
    }

    Connections {
        target: mainWindow
        onClosing: {
            meetingManager.unInitialize()
            close.accepted = false
        }
    }
}
