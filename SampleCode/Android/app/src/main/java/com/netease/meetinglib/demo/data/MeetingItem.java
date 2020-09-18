package com.netease.meetinglib.demo.data;


import com.netease.meetinglib.sdk.NEMeetingItemStatus;

public class MeetingItem implements Comparable<MeetingItem> {
    /**
     * 会议唯一Id，服务器生成
     */
    private long meetingUniqueId;

    /**
     * 会议号
     */
    private String meetingId;

    /**
     * 会议主题
     */
    private String subject;

    /**
     * 会议开始时间戳
     */
    private long startTime;

    /**
     * 会议结束时间戳
     */
    private long endTime;

    /**
     * 会议信息变更时间戳
     */
    private long updateTime;

    /**
     * 会议信息创建时间
     */
    private long createTime;

    /**
     * 会议密码
     */
    private String password;
    /**
     * 当前HH：mm
     */
    private String hourAndMin;

    /**
     * 显示日期到天
     */
    private String day;

    /**
     * 显示日期到月
     */
    private String month;
    /**
     * 会议额外选项
     */
    private MeetingItemSetting setting;
    /**
     * 会议状态
     */
    private NEMeetingItemStatus status;
    private boolean isGroupFirst;

    public MeetingItemSetting getSetting() {
        return setting;
    }

    public void setSetting(MeetingItemSetting setting) {
        this.setting = setting;
    }

    public NEMeetingItemStatus getStatus() {
        return status;
    }

    public void setStatus(NEMeetingItemStatus status) {
        this.status = status;
    }

    public long getMeetingUniqueId() {
        return meetingUniqueId;
    }

    public void setMeetingUniqueId(long meetingUniqueId) {
        this.meetingUniqueId = meetingUniqueId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHourAndMin() {
        return hourAndMin;
    }

    public void setHourAndMin(String hourAndMin) {
        this.hourAndMin = hourAndMin;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isGroupFirst() {
        return isGroupFirst;
    }

    public void setGroupFirst(boolean groupFirst) {
        isGroupFirst = groupFirst;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public int compareTo(MeetingItem meetingItem) {
        long t = getStartTime() - meetingItem.getStartTime();
        if (t == 0) {
            t = getCreateTime() - meetingItem.getCreateTime();
        }
        return t > 0 ? 0 : -1;
    }

    public static class MeetingItemSetting {
        /**
         * 入会时音频开关
         */
        public boolean isAttendeeAudioOff;

        public boolean isAttendeeAudioOff() {
            return isAttendeeAudioOff;
        }

        public void setAttendeeAudioOff(boolean attendeeAudioOff) {
            isAttendeeAudioOff = attendeeAudioOff;
        }
    }

    @Override
    public String toString() {
        return "MeetingItem{" +
                "meetingUniqueId=" + meetingUniqueId +
                ", meetingId='" + meetingId + '\'' +
                ", subject='" + subject + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", updateTime=" + updateTime +
                ", password='" + password + '\'' +
                ", hourAndMin='" + hourAndMin + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", setting=" + setting +
                ", status='" + status + '\'' +
                '}';
    }
}
