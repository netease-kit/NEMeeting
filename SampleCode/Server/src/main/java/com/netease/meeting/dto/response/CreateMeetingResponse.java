package com.netease.meeting.dto.response;

import com.netease.meeting.dto.MeetingSettingDto;

/**
 * @author HJ
 * @date 2021/11/15
 **/
public class CreateMeetingResponse {

    /**
     * 会议唯一id
     */
    private Long meetingUniqueId;

    /**
     * 随机会议码,9位数字；个人会议码，10位数字
     */
    private String meetingId;

    /**
     * 预约会议主题
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
     * 会议密码，无密码为空串
     */
    private String password;

    /**
     * 会议设置
     */
    private MeetingSettingDto settings;

    /**
     * 状态，0.无效，1.未开始，2.进行中，3.已终止，4.已取消，5.已回收
     */
    private Integer status;

    /**
     * 会议短号
     */
    private String shortId;

    public Long getMeetingUniqueId() {
        return meetingUniqueId;
    }

    public void setMeetingUniqueId(Long meetingUniqueId) {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
}
