package com.netease.meeting.dto.response;

/**
 * 创建账户结果返回封装类
 * @author HJ
 * @date 2021/11/12
 **/
public class CreateAccountResponse {

    /**
     * 会议用户账号ID
     */
    private String accountId;

    /**
     * 会议用户账号令牌
     */
    private String accountToken;

    /**
     * 个人会议的会议码
     */
    private Long personalMeetingId;

    /**
     * 个人会议短号
     */
    private String shortId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public Long getPersonalMeetingId() {
        return personalMeetingId;
    }

    public void setPersonalMeetingId(Long personalMeetingId) {
        this.personalMeetingId = personalMeetingId;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
}
