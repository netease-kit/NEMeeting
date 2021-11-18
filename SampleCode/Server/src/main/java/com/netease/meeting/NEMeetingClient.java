package com.netease.meeting;

import com.netease.meeting.dto.request.EditMeetingRequest;
import com.netease.meeting.dto.request.GetUsableMeetingListRequest;
import com.netease.meeting.dto.response.*;
import com.netease.meeting.dto.request.CreateMeetingRequest;

import java.util.List;

/**
 * 网易会议api封装类，线程安全，可使用单例模式
 * @author HJ
 * @date 2021/11/12
 **/
public interface NEMeetingClient {


    /**
     * 会议账号创建
     * 注册创建一个会议账号
     *
     * @param imAccid 复用的imAccid 非必传
     * @param imToken 复用的imAccid的Token 非必传
     * @param shortId 个人会议短号，4-8位 非必传
     */
    MeetingResponse<CreateAccountResponse> createAccount(String imAccid, String imToken, String shortId);

    /**
     * 通过ImAccid查询会议账号
     * 通过ImAccid查询本企业下的一个会议账号
     *
     * @param imAccid imAccid
     */
    MeetingResponse<AccountInfo> getAccountInfoByImAccid(String imAccid);

    /**
     * 通过AccountId查询会议账号
     * 注册创建一个会议账号
     *
     * @param accountId 账户id
     */
    MeetingResponse<AccountInfo> getAccountInfoByAccountId(String accountId);

    /**
     * 会议账号更新令牌
     * 注册创建一个会议账号
     *
     * @param accountId 账户id
     */
    MeetingResponse<Void> updateToken(String accountId, String accountToken);

    /**
     * 更新个人会议短号
     *
     * @param accountId 会议用户账号
     * @param shortId 个人会议短号，4-8位，不传表示清除短号
     */
    MeetingResponse<Void> updateShortId(String accountId, String shortId);

    /**
     * 创建会议
     * @param request 参数
     */
    MeetingResponse<CreateMeetingResponse> createMeeting(CreateMeetingRequest request);


    /**
     * 查询会议
     * @param meetingUniqueId 会议唯一id  必填
     */
    MeetingResponse<GetMeetingInfoResponse> getMeetingInfoByMeetingUniqueId(Long meetingUniqueId);


    /**
     * 修改会议
     * @param request 修改参数
     */
    MeetingResponse<Void> editMeeting(EditMeetingRequest request);

    /**
     * 取消会议
     * @param meetingUniqueId 会议唯一id  必填
     */
    MeetingResponse<Void> cancelMeeting(Long meetingUniqueId);

    /**
     * 结束会议
     * @param meetingUniqueId 会议唯一id  必填
     * @param recycle 是否回收，默认不回收 非必填
     */
    MeetingResponse<Void> endMeetingByMeetingUniqueId(Long meetingUniqueId, boolean recycle);

    /**
     * 结束会议
     * @param meetingId 会议号	  必填
     * @param recycle 是否回收，默认不回收 非必填
     */
    MeetingResponse<Void> endMeetingByMeetingId(String meetingId, boolean recycle);
    /**
     * 结束会议
     * @param shortId 会议短号  必填
     * @param recycle 是否回收，默认不回收 非必填
     */
    MeetingResponse<Void> endMeetingByMeetingShortId(String shortId, boolean recycle);

    /**
     * 结束会议
     * @param meetingUniqueId 会议唯一Id	  必填
     */
    MeetingResponse<List<RecordInfo>> getRecordList(Long meetingUniqueId);

    /**
     * 查询可用的会议列表
     * @param request 查询参数
     */
    MeetingResponse<PagedResponse<GetUsableMeetingListResponse>> getUsableMeetingList(GetUsableMeetingListRequest request);

    /**
     * 关闭客户端
     */
    void shutdown();

}
