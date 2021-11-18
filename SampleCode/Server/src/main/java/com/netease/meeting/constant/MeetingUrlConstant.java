package com.netease.meeting.constant;

/**
 * @author HJ
 * @date 2021/11/16
 **/
public class MeetingUrlConstant {
    /**
     * 域名
     */
    public final static String MEETING_DOMAIN = "https://meeting-api.netease.im";
    /**
     * 创建账户
     */
    public final static String CREATE_ACCOUNT = "/v1/account/create";
    /**
     * 根据imaccId获取会议账户信息
     */
    public final static String GET_BY_IMACCID = "/v1/account/getByImAccid";
    /**
     * 创建账户
     */
    public final static String GET_ACCOUNT = "/v1/account/getByAccountId";
    /**
     * 会议账号更新令牌
     */
    public final static String UPDATE_TOKEN = "/v1/account/updateToken";
    /**
     * 更新个人会议短号
     */
    public final static String UPDATE_SHORTID = "/v1/account/updateShortId";
    /**
     * 创建会议
     */
    public final static String CREATE_MEETING = "/v2/meeting/create";
    /**
     * 获取会议信息
     */
    public final static String GET_MEETING = "/v2/meeting/get";
    /**
     * 修改会议
     */
    public final static String EDIT_MEETING = "/v2/meeting/edit";
    /**
     * 取消会议
     */
    public final static String CANCEL_MEETING = "/v2/meeting/cancel";
    /**
     * 结束会议
     */
    public final static String END_MEETING_MEETING_UNIQUEID = "/v2/meeting/deleteByMeetingUniqueId";
    /**
     * 结束会议
     */
    public final static String END_MEETING_MEETING_ID = "/v2/meeting/deleteByMeetingId";
    /**
     * 结束会议
     */
    public final static String END_MEETING_MEETING_SHORTID = "/v2/meeting/deleteByMeetingShortId";
    /**
     * 获取会议录像列表
     */
    public final static String RECORD_LIST = "/v2/meeting/record/list";
    /**
     * 查询可用的会议列表
     */
    public final static String MEETING_LIST = "/v2/meeting/list";


}
