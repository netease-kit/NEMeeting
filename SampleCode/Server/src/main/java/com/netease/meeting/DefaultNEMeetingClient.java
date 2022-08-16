package com.netease.meeting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.netease.meeting.config.ClientConfig;
import com.netease.meeting.dto.request.CreateMeetingRequest;
import com.netease.meeting.dto.request.EditMeetingRequest;
import com.netease.meeting.dto.request.GetUsableMeetingListRequest;
import com.netease.meeting.dto.response.*;
import com.netease.meeting.http.HttpClientComponent;
import com.netease.meeting.http.ServiceClient;
import com.netease.meeting.util.CheckSumUtil;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.netease.meeting.constant.MeetingUrlConstant.*;

/**
 * @author HJ
 * @date 2021/11/12
 **/
public class DefaultNEMeetingClient implements NEMeetingClient {
    private final String endpoint;
    private final String appKey;
    private final String appSecret;
    private final ServiceClient serviceClient;
    private final Gson gson = new GsonBuilder().create();

    public DefaultNEMeetingClient(String endpoint, String appKey, String appSecret, ClientConfig config) {
        this.endpoint = endpoint;
        this.appKey = appKey;
        this.appSecret = appSecret;
        serviceClient = new HttpClientComponent(config == null ? new ClientConfig() : config);
    }

    @Override
    public MeetingResponse<CreateAccountResponse> createAccount(String imAccid, String imToken, String shortId) {
        HashMap<String, String> map = new HashMap<String, String>(3);
        map.put("imAccid", imAccid);
        map.put("imToken", imToken);
        map.put("shortId", shortId);
        return serviceClient.postForEntity(endpoint + CREATE_ACCOUNT, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<CreateAccountResponse>>() {
        });
    }

    @Override
    public MeetingResponse<AccountInfo> getAccountInfoByImAccid(String imAccid) {
        HashMap<String, String> map = new HashMap<String, String>(1);
        map.put("imAccid", imAccid);
        return serviceClient.postForEntity(endpoint + GET_BY_IMACCID, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<AccountInfo>>() {
        });
    }

    @Override
    public MeetingResponse<AccountInfo> getAccountInfoByAccountId(String accountId) {
        HashMap<String, String> map = new HashMap<String, String>(1);
        map.put("accountId", accountId);
        return serviceClient.postForEntity(endpoint + GET_ACCOUNT, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<AccountInfo>>() {
        });
    }


    @Override
    public MeetingResponse<Void> updateToken(String accountId, String accountToken) {
        HashMap<String, String> map = new HashMap<String, String>(2);
        map.put("accountId", accountId);
        map.put("accountToken", accountToken);
        return serviceClient.postForEntity(endpoint + UPDATE_TOKEN, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<Void> updateShortId(String accountId, String shortId) {
        HashMap<String, String> map = new HashMap<String, String>(2);
        map.put("accountId", accountId);
        map.put("shortId", shortId);
        return serviceClient.postForEntity(endpoint + UPDATE_SHORTID, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<CreateMeetingResponse> createMeeting(CreateMeetingRequest request) {
        return serviceClient.postForEntity(endpoint + CREATE_MEETING, gson.toJson(request), getHeader(), new TypeToken<MeetingResponse<CreateMeetingResponse>>() {
        });
    }

    @Override
    public MeetingResponse<GetMeetingInfoResponse> getMeetingInfoByMeetingUniqueId(Long meetingUniqueId) {
        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put("meetingUniqueId", meetingUniqueId);
        return serviceClient.postForEntity(endpoint + GET_MEETING, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<GetMeetingInfoResponse>>() {
        });
    }

    @Override
    public MeetingResponse<Void> editMeeting(EditMeetingRequest request) {
        return serviceClient.postForEntity(endpoint + EDIT_MEETING, gson.toJson(request), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<Void> cancelMeeting(Long meetingUniqueId) {
        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put("meetingUniqueId", meetingUniqueId);
        return serviceClient.postForEntity(endpoint + CANCEL_MEETING, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<Void> endMeetingByMeetingUniqueId(Long meetingUniqueId, boolean recycle) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.put("meetingUniqueId", meetingUniqueId);
        map.put("recycle", recycle);
        return serviceClient.postForEntity(endpoint + END_MEETING_MEETING_UNIQUEID, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<Void> endMeetingByMeetingId(String meetingId, boolean recycle) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.put("meetingId", meetingId);
        map.put("recycle", recycle);
        return serviceClient.postForEntity(endpoint + END_MEETING_MEETING_ID, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<Void> endMeetingByMeetingShortId(String shortId, boolean recycle) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.put("shortId", shortId);
        map.put("recycle", recycle);
        return serviceClient.postForEntity(endpoint + END_MEETING_MEETING_SHORTID, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<Void>>() {
        });
    }

    @Override
    public MeetingResponse<List<RecordInfo>> getRecordList(Long meetingUniqueId) {
        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put("meetingUniqueId", meetingUniqueId);
        return serviceClient.postForEntity(endpoint + RECORD_LIST, gson.toJson(map), getHeader(), new TypeToken<MeetingResponse<List<RecordInfo>>>() {
        });
    }

    @Override
    public MeetingResponse<PagedResponse<GetUsableMeetingListResponse>> getUsableMeetingList(GetUsableMeetingListRequest request) {
        return serviceClient.postForEntity(endpoint + MEETING_LIST, gson.toJson(request), getHeader(), new TypeToken<MeetingResponse<PagedResponse<GetUsableMeetingListResponse>>>() {
        });
    }

    @Override
    public void shutdown() {
        serviceClient.shutdown();
    }

    private Map<String, String> getHeader() {
        Map<String, String> headerMap = new HashMap<String, String>(5);
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        headerMap.put("AppKey", appKey);
        headerMap.put("Nonce", nonce);
        headerMap.put("CurTime", curTime);
        headerMap.put("CheckSum", CheckSumUtil.getCheckSum(nonce, curTime, appSecret));
        headerMap.put(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        return headerMap;
    }

}
