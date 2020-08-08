import QtQuick 2.0
import QtQuick.Controls 2.12
import QtQuick.Layouts 1.12
import NetEase.Meeting.RunningStatus 1.0
import NetEase.Meeting.MeetingStatus 1.0

Rectangle {
    ToolButton {
        anchors.top: parent.top
        anchors.topMargin: 10
        anchors.right: parent.right
        anchors.rightMargin: 10
        text: qsTr("⋮")
        onClicked: meetingManager.showSettings()
    }

    ColumnLayout {
        anchors.centerIn: parent

        TextField {
            id: textMeetingId
            enabled: !checkBox.checked
            text: checkBox.checked ? meetingManager.personalMeetingId : ''
            placeholderText: qsTr('Meeting ID')
            selectByMouse: true
            Layout.fillWidth: true
        }

        TextField {
            id: textNickname
            text: qsTr('nickname')
            placeholderText: qsTr('Your nickname')
            selectByMouse: true
            Layout.fillWidth: true
        }

        RowLayout {
            Layout.fillWidth: true
            CheckBox {
                id: checkAudio
                checked: true
                text: qsTr('Enable audio')
            }

            CheckBox {
                id: checkVideo
                checked: true
                text: qsTr('Enable video')
            }
        }

        CheckBox {
            id: checkBox
            text: qsTr('Personal meeting ID: %1').arg(meetingManager.personalMeetingId)
            Layout.alignment: Qt.AlignHCenter
        }

        RowLayout {
            Layout.topMargin: 20
            Layout.alignment: Qt.AlignHCenter
            Button {
                id: btnCreate
                highlighted: true
                text: qsTr('Create')
                Layout.alignment: Qt.AlignLeft
                Layout.preferredWidth: 120
                onClicked: {
                    meetingManager.invokeStart(checkBox.checked ? meetingManager.personalMeetingId : '',
                                               textNickname.text, checkAudio.checked, checkVideo.checked)
                }
            }
            Button {
                id: btnJoin
                highlighted: true
                text: qsTr('Join')
                Layout.alignment: Qt.AlignRight
                Layout.preferredWidth: 120
                onClicked: {
                    meetingManager.invokeJoin(textMeetingId.text, textNickname.text, checkAudio.checked, checkVideo.checked)
                }
            }
        }
    }

    Connections {
        target: meetingManager
        onStartSignal: {
            switch (errorCode) {
            case MeetingStatus.ERROR_CODE_SUCCESS:
                toast.show(qsTr('Create successfull'))
                break
            case MeetingStatus.MEETING_ERROR_FAILED_MEETING_ALREADY_EXIST:
                toast.show(qsTr('Meeting already started'))
                break
            case MeetingStatus.ERROR_CODE_FAILED:
                toast.show(qsTr('Failed to start meeting'))
                break
            default:
                toast.show(errorMessage)
                break
            }
        }
        onJoinSignal: {
            switch (errorCode) {
            case MeetingStatus.ERROR_CODE_SUCCESS:
                toast.show(qsTr("Join successfull"))
                break
            case MeetingStatus.MEETING_ERROR_LOCKED_BY_HOST:
                toast.show(qsTr('The meeting is locked'))
                break
            case MeetingStatus.MEETING_ERROR_INVALID_ID:
                toast.show(qsTr('Meeting not exist'))
                break
            case MeetingStatus.MEETING_ERROR_LIMITED:
                toast.show(qsTr('Exceeds the limit'))
                break
            case MeetingStatus.ERROR_CODE_FAILED:
                toast.show(qsTr('Failed to join meeting'))
                break
            default:
                toast.show(errorMessage)
                break
            }
        }
        onMeetingStatusChanged: {
            switch (meetingStatus) {
            case RunningStatus.MEETING_STATUS_CONNECTING:
                break;
            case RunningStatus.MEETING_STATUS_DISCONNECTING:
                if (extCode === RunningStatus.MEETING_DISCONNECTING_BY_NORMAL)
                    toast.show(qsTr('Your have been left this meeting'))
                else if (extCode === RunningStatus.MEETING_DISCONNECTING_BY_HOST)
                    toast.show(qsTr('This meeting has been ended'))
                else if (extCode === RunningStatus.MEETING_DISCONNECTING_BY_KICKOUT)
                    toast.show(qsTr('You have been removed from meeting by host'))
                else if (extCode === RunningStatus.MEETING_DISCONNECTING_BY_SERVER)
                    toast.show(qsTr('You have been discconected from server'))
                else if (extCode === RunningStatus.MEETING_DISCONNECTING_BY_MULTI_SPOT) {
                    toast.show(qsTr('You have been kickout by other client'))
                }
                break
            }
        }
    }
}
