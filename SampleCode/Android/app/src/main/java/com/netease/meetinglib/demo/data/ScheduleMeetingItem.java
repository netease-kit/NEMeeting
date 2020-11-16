
package com.netease.meetinglib.demo.data;

public class ScheduleMeetingItem {

    private String tittle;

    private String subTittle;
    private String timeTip;
    private boolean isAttendeeAudioOff;

    private int clickAction;

    public static final int EDIT_TEXT_TITLE_ACTION = 0;
    public static final int SET_START_TIME_ACTION = 1;
    public static final int SET_END_TIME_ACTION = 2;
    public static final int ENABLE_MEETING_PWD_ACTION = 3;
    public static final int ENABLE_MEETING_MUTE_ACTION = 4;


    public ScheduleMeetingItem(String tittle, String subTittle, String timeTip, boolean isAttendeeAudioOff, int clickAction) {
        this.tittle = tittle;
        this.subTittle = subTittle;
        this.clickAction = clickAction;
        this.timeTip = timeTip;
        this.isAttendeeAudioOff = isAttendeeAudioOff;
    }

    public ScheduleMeetingItem(String tittle, int clickAction) {
        this(tittle, "", "", false, clickAction);
    }

    public ScheduleMeetingItem(String tittle, String subTittle, boolean isAttendeeAudioOff, int clickAction) {
        this(tittle, subTittle, "", false, clickAction);
    }

    public ScheduleMeetingItem(String tittle, boolean isAttendeeAudioOff, int clickAction) {
        this(tittle, "", "", isAttendeeAudioOff, clickAction);
    }


    public ScheduleMeetingItem(String tittle, String timeTip, int clickAction) {
        this(tittle, "", timeTip, false, clickAction);
    }


    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getSubTittle() {
        return subTittle;
    }

    public void setSubTittle(String subTittle) {
        this.subTittle = subTittle;
    }

    public int getClickAction() {
        return clickAction;
    }

    public void setClickAction(int clickAction) {
        this.clickAction = clickAction;
    }

    public String getTimeTip() {
        return timeTip;
    }

    public void setTimeTip(String timeTip) {
        this.timeTip = timeTip;
    }


    public boolean isAttendeeAudioOff() {
        return isAttendeeAudioOff;
    }

    public void setAttendeeAudioOff(boolean attendeeAudioOff) {
        isAttendeeAudioOff = attendeeAudioOff;
    }

}
