import QtQuick 2.12
import QtQuick.Window 2.12
import QtQuick.Layouts 1.12
import QtQuick.Controls 2.12
import QtGraphicalEffects 1.0

Window {
    id: mainWindow
    visible: true
    width: 900
    height: 600
    title: qsTr("NetEase Meeting SDK Sample")

    Component.onCompleted: {
        pageLoader.setSource(Qt.resolvedUrl('qrc:/qml/Login.qml'))
        // meetingManager.initialize()
        // meetingManager.isInitializd()
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
