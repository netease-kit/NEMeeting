package com.netease.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netease.meeting.NEMeetingClient;
import com.netease.meeting.NEMeetingClientBuilder;
import com.netease.meeting.dto.MeetingSettingDto;
import com.netease.meeting.dto.SceneDto;
import com.netease.meeting.dto.request.CreateMeetingRequest;
import com.netease.meeting.dto.request.EditMeetingRequest;
import com.netease.meeting.dto.request.GetUsableMeetingListRequest;
import com.netease.meeting.dto.response.*;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author HJ
 * @date 2021/11/16
 **/
public class MeetingServiceTest {

    @Resource
    private NEMeetingClient neMeetingClient = NEMeetingClientBuilder.build("", "", "");
    @Resource
    private final Gson gson = new GsonBuilder().create();

    @Test
    public void createAccount() {
        MeetingResponse<CreateAccountResponse> response = neMeetingClient.createAccount("102d4c90d8654433b4cc73229b73a565", null, null);
        System.out.println(gson.toJson(response));
    }

    @Test
    public void getAccountInfoByImAccid() {
        MeetingResponse<AccountInfo> gresponse = neMeetingClient.getAccountInfoByImAccid("4d7b6470dac1437fbead8314b31f04eb");
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void getAccountInfoByAccountId() {
        MeetingResponse<AccountInfo> gresponse = neMeetingClient.getAccountInfoByAccountId("1163703109811494");
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void updateToken() {
        MeetingResponse<Void> gresponse = neMeetingClient.updateToken("1163703109811494", "1f18da1cb-fd4f-4416-92dc-77e339787592");
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void updateShortId() {
        MeetingResponse<Void> gresponse = neMeetingClient.updateShortId("1163703109811494", "978592");
        System.out.println(gson.toJson(gresponse));
    }


    @Test
    public void createMeeting() {
        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setHost("1163703109811494");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        request.setEndTime(calendar.getTimeInMillis());
        request.setStartTime(System.currentTimeMillis());
        request.setOwner("1163703109811494");
        request.setPassword("10001");
        request.setSubject("测试主题");
        request.setType("1");
        MeetingSettingDto meetingSettingDto = new MeetingSettingDto();
        MeetingSettingDto.Control control = new MeetingSettingDto.Control();
        control.setType("video");
        control.setAllowSelfOn(true);
        control.setAttendeeOff(1);
        control.setState(1);
        List<MeetingSettingDto.Control> controls = new ArrayList<MeetingSettingDto.Control>();
        controls.add(control);
        meetingSettingDto.setControls(controls);
        SceneDto sceneDto = new SceneDto();
        SceneDto.RoleType roleType = new SceneDto.RoleType();
        roleType.setRoleType(1);
        roleType.setMaxCount(100);
        List<SceneDto.RoleType> roleTypes = new ArrayList<SceneDto.RoleType>();
        roleTypes.add(roleType);
        sceneDto.setRoleTypes(roleTypes);
        meetingSettingDto.setScene(sceneDto);
        request.setSettings(meetingSettingDto);
        MeetingResponse<CreateMeetingResponse> gresponse = neMeetingClient.createMeeting(request);
        System.out.println(gson.toJson(gresponse));
    }


    @Test
    public void getMeetingInfoByMeetingUniqueId() {
        MeetingResponse<GetMeetingInfoResponse> gresponse = neMeetingClient.getMeetingInfoByMeetingUniqueId(105351L);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void editMeeting() {
        EditMeetingRequest request = new EditMeetingRequest();
        request.setMeetingUniqueId(105351L);
        request.setSubject("测试subject");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        request.setStartTime(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        request.setEndTime(calendar.getTimeInMillis());
        request.setPassword("1111");
        MeetingSettingDto meetingSettingDto = new MeetingSettingDto();
        MeetingSettingDto.Control control = new MeetingSettingDto.Control();
        control.setType("video");
        control.setAllowSelfOn(true);
        control.setAttendeeOff(1);
        control.setState(1);
        List<MeetingSettingDto.Control> controls = new ArrayList<MeetingSettingDto.Control>();
        controls.add(control);
        meetingSettingDto.setControls(controls);
        SceneDto sceneDto = new SceneDto();
        SceneDto.RoleType roleType = new SceneDto.RoleType();
        roleType.setRoleType(1);
        roleType.setMaxCount(100);
        List<SceneDto.RoleType> roleTypes = new ArrayList<SceneDto.RoleType>();
        roleTypes.add(roleType);
        sceneDto.setRoleTypes(roleTypes);
        meetingSettingDto.setScene(sceneDto);
        request.setSettings(meetingSettingDto);
        MeetingResponse<Void> gresponse = neMeetingClient.editMeeting(request);
        System.out.println(gson.toJson(gresponse));
    }


    @Test
    public void cancelMeeting() {
        MeetingResponse<Void> gresponse = neMeetingClient.cancelMeeting(105351L);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void endMeetingByMeetingUniqueId() {
        MeetingResponse<Void> gresponse = neMeetingClient.endMeetingByMeetingUniqueId(105351L, false);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void endMeetingByMeetingId() {
        MeetingResponse<Void> gresponse = neMeetingClient.endMeetingByMeetingId("733351580", false);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void endMeetingByMeetingShortId() {
        MeetingResponse<Void> gresponse = neMeetingClient.endMeetingByMeetingShortId("638125394", false);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void getRecordList() {
        MeetingResponse<List<RecordInfo>> gresponse = neMeetingClient.getRecordList(41207L);
        System.out.println(gson.toJson(gresponse));
    }

    @Test
    public void getUsableMeetingList() {
        GetUsableMeetingListRequest requestDto = new GetUsableMeetingListRequest();
        requestDto.setPageIndex(2);
        requestDto.setPageSize(5);
        MeetingResponse<PagedResponse<GetUsableMeetingListResponse>> gresponse = neMeetingClient.getUsableMeetingList(requestDto);
        System.out.println(gson.toJson(gresponse));
    }
}
