package com.netease.meeting.dto.request;


import com.netease.meeting.dto.MeetingSettingDto;

/**
 * 创建会议参数封装
 * @author HJ
 * @date 2021/11/15
 **/
public class CreateMeetingRequest {
    /**
     * 必选
     * 会议主持人
     */
    private String host;
    /**
     * 会议拥有人，不填则使用host作为owner
     */
    private String owner;
    /**
     * 必选
     * 会议类型 1 随机号即时会议; 2 个人号即时会议; 3 随机号预约会议
     */
    private String type;
    /**
     * 会议密码，无密码为空串
     */
    private String password;
    /**
     * 会议设置
     */
    private MeetingSettingDto settings;
    /**
     * 必选
     * 会议主题 30字符以内
     */
    private String subject;
    /**
     * type=3: 必选; type=1: 非必选
     * 预约开始时间，毫秒
     */
    private Long startTime;
    /**
     * type=3: 必选; type=1: 非必选
     * 预约结束时间，毫秒
     */
    private Long endTime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
