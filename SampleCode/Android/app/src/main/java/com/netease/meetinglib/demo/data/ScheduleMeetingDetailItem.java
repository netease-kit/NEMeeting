
package com.netease.meetinglib.demo.data;

public class ScheduleMeetingDetailItem {

    private String start;
    private String description;
    private String end;
    private Boolean on;

    private int clickAction;

    public static final int COPY_MEETING_ID_ACTION = 1;
    public static final int COPY_LIVE_URL_ACTION = 2;
    public static final int COPY_PASSWORD_ACTION = 4;
    public static final int COPY_LIVE_LEVEL_ACTION = 5;

    public ScheduleMeetingDetailItem(String start, String description, String end, int clickAction) {
        this.start = start;
        this.description = description;
        this.end = end;
        this.clickAction = clickAction;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getClickAction() {
        return clickAction;
    }

    public void setClickAction(int clickAction) {
        this.clickAction = clickAction;
    }

    public Boolean getOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }
}
