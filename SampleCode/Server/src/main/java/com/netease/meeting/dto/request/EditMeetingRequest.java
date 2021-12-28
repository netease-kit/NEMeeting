package com.netease.meeting.dto.request;

import com.netease.meeting.dto.MeetingSettingDto;

/**
 * 修改会议
 * @author HJ
 * @date 2021/11/15
 **/
public class EditMeetingRequest {
    /**
     * 必选
     * 会议唯一Id
     */
    private Long meetingUniqueId;
    /**
     * 必选
     * 会议主题 30字符以内
     */
    private String subject;
    /**
     * 预约开始时间，毫秒
     */
    private Long startTime;
    /**
     * 预约结束时间，毫秒，-1无限期
     */
    private Long endTime;
    /**
     * 会议密码，密码置空传空串
     */
    private String password;
    /**
     * 会议设置
     */
    private MeetingSettingDto settings;

    public Long getMeetingUniqueId() {
        return meetingUniqueId;
    }

    public void setMeetingUniqueId(Long meetingUniqueId) {
        this.meetingUniqueId = meetingUniqueId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MeetingSettingDto getSettings() {
        return settings;
    }

    public void setSettings(MeetingSettingDto settings) {
        this.settings = settings;
    }
}
