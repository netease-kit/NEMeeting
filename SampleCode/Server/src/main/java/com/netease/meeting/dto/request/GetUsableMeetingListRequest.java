package com.netease.meeting.dto.request;

import java.util.List;

/**
 * @author HJ
 * @date 2021/11/15
 **/
public class GetUsableMeetingListRequest {
    /**
     * 会议唯一id（meetingUniqueId）
     */
    private Long meetingUniqueId;
    /**
     * 会议号
     */
    private String meetingId;
    /**
     * 最小会议开始时间，毫秒
     */
    private Long minStartTime;
    /**
     * 最大会议开始时间，毫秒
     */
    private Long maxStartTime;
    /**
     * 状态列表，会议状态，1.未开始，2.进行中
     */
    private List<Integer> statusList;
    /**
     * 会议类型 1 随机号即时会议; 2 个人号即时会议; 3 随机号预约会议
     */
    private String type;
    /**
     * 分页页码，从0开始，默认0
     */
    private Integer pageIndex;
    /**
     * 分页大小，1-100，最大100，默认20
     */
    private Integer pageSize;

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

    public Long getMinStartTime() {
        return minStartTime;
    }

    public void setMinStartTime(Long minStartTime) {
        this.minStartTime = minStartTime;
    }

    public Long getMaxStartTime() {
        return maxStartTime;
    }

    public void setMaxStartTime(Long maxStartTime) {
        this.maxStartTime = maxStartTime;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
