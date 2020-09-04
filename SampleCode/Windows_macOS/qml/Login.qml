import QtQuick 2.0
import QtQuick.Controls 2.12
import QtQuick.Layouts 1.12
import NetEase.Meeting.MeetingStatus 1.0

Rectangle {
    ColumnLayout {
        anchors.centerIn: parent
        Image {
            Layout.preferredHeight: 58
            Layout.preferredWidth: 220
            source: 'qrc:/images/logo.png'
        }
        TextField {
            id: textAppKey
            placeholderText: qsTr('Your application key')
            text: ''
            selectByMouse: true
            Layout.fillWidth: true
            Layout.topMargin: 20
        }
        TextField {
            id: textAccountId
            placeholderText: qsTr('Your account ID')
            text: ''
            selectByMouse: true
            Layout.fillWidth: true
        }
        TextField {
            id: textPassword
            placeholderText: qsTr('Your password')
            text: ''
            selectByMouse: true
            Layout.fillWidth: true
        }
        Button {
            id: btnSubmit
            highlighted: true
            text: 'Login'
            Layout.fillWidth: true
            enabled: textAppKey.text.length > 0 & textAccountId.text.length > 0 && textPassword.text.length > 0
            onClicked: {
                meetingManager.login(textAppKey.text,
                                     textAccountId.text,
                                     textPassword.text)
                enabled = false
            }
        }
    }

    Connections {
        target: meetingManager
        onLoginSignal: {
            btnSubmit.enabled = Qt.binding(function() { return textAppKey.text.length > 0 & textAccountId.text.length > 0 && textPassword.text.length > 0 })
            if (errorCode === MeetingStatus.ERROR_CODE_SUCCESS)
                pageLoader.setSource(Qt.resolvedUrl('qrc:/qml/Front.qml'))
            else
                toast.show(qsTr('Failed to login'))
        }
    }
}
