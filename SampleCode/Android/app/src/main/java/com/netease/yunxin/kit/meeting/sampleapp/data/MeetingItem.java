// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.data;


import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemLive;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemSetting;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingRoleType;

import java.io.Serializable;
import java.util.Map;

public class MeetingItem implements Comparable<MeetingItem>, Serializable {
    /**
     * 会议唯一Id，服务器生成
     */
    private long meetingUniqueId;

    /**
     * 会议唯一id
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
    private NEMeetingItemSetting setting;
    /**
     * 会议状态
     */
    private NEMeetingItemStatus status;
    
    private NEMeetingItemLive live;
    
    private boolean isGroupFirst;

    private String extraData;

    private Map<String,NEMeetingRoleType> roleBinds;

    public Map<String, NEMeetingRoleType> getRoleBinds() {
        return roleBinds;
    }

    public void setRoleBinds(Map<String, NEMeetingRoleType> roleBinds) {
        this.roleBinds = roleBinds;
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

    public NEMeetingItemLive getLive() {
        return live;
    }
    public void setLive(NEMeetingItemLive live) {
        this.live = live;
    }

    public void setSetting(NEMeetingItemSetting setting) {
        this.setting = setting;
    }

    public NEMeetingItemSetting getSetting() {
        return setting;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public int compareTo(MeetingItem meetingItem) {
        long t = getStartTime() - meetingItem.getStartTime();
        if (t == 0) {
            t = getStartTime() - meetingItem.getStartTime();
        }
        return t > 0 ? 0 : -1;
    }

    @Override
    public String toString() {
        return "MeetingItem{" +
                "meetingUniqueId=" + meetingUniqueId +
                ", meetingId='" + meetingId + '\'' +
                ", subject='" + subject + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
//                ", updateTime=" + updateTime +
                ", password='" + password + '\'' +
                ", hourAndMin='" + hourAndMin + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", setting=" + setting +
                ", status='" + status + '\'' +
                '}';
    }

}
