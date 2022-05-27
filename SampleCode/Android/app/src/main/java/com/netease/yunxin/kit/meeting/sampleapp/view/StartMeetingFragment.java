/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.StartMeetingViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEAccountService;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAttendeeOffType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAudioControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVideoControl;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StartMeetingFragment extends MeetingCommonFragment {
    private static final String TAG = StartMeetingFragment.class.getSimpleName();
    private String meetingId;
    private String currentMeetingId;
    private String content;
    private String tag;
    private JSONObject jsonScene;
    private StartMeetingViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartMeetingViewModel.class);
        usePersonalMeetingId.setEnabled(true);
        usePersonalMeetingId.setOnCheckedChangeListener((buttonView, isChecked) -> {
            determineMeetingId();
        });
        initData();
    }

    /**
     * 初始化数据，为了方便测试，可通过ADB传递参数进行验证
     * 当前支持传入tag和scene
     * tag:String类型
     * scene:Json类型
     */
    private void initData(){
        Intent intent = getActivity().getIntent();
        tag = intent.getStringExtra("tag");
        String strScene = intent.getStringExtra("scene");
        if (!TextUtils.isEmpty(strScene)) {
            try {
                jsonScene = new JSONObject(strScene);
            }catch (Exception e){
                Toast.makeText(this.getContext(),"sence params is error",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号(留空或使用个人会议号)", "昵称", "请输入密码","个人TAG", "扩展字段"};
    }

    @Override
    protected String getActionLabel() {
        return "创建会议";
    }

    @Override
    protected void performAction(String first, String second, String third,String fourth) {
        NEStartMeetingParams params = new NEStartMeetingParams();
        params.meetingId = usePersonalMeetingId.isChecked() && !TextUtils.isEmpty(currentMeetingId) ? currentMeetingId : first;
        params.displayName = second;
        if (!TextUtils.isEmpty(third)) {
            params.password = third;
        }
        if (!TextUtils.isEmpty(tag)){
            params.tag = tag;
        }
        if (!TextUtils.isEmpty(fourth)){
            params.tag = fourth;
        }
        String extraData = getEditorText(4);
        if (!TextUtils.isEmpty(extraData)) {
            params.extraData = extraData;
        }
        List<NEMeetingControl> controls = new ArrayList<>();
        if (isCheckedById(R.id.audioOffAllowSelfOn)) {
            controls.add(new NEMeetingAudioControl(NEMeetingAttendeeOffType.OffAllowSelfOn));
        } else if (isCheckedById(R.id.audioOffNotAllowSelfOn)) {
            controls.add(new NEMeetingAudioControl(NEMeetingAttendeeOffType.OffNotAllowSelfOn));
        }
        if (isCheckedById(R.id.videoOffAllowSelfOn)) {
            controls.add(new NEMeetingVideoControl(NEMeetingAttendeeOffType.OffAllowSelfOn));
        } else if (isCheckedById(R.id.videoOffNotAllowSelfOn)) {
            controls.add(new NEMeetingVideoControl(NEMeetingAttendeeOffType.OffNotAllowSelfOn));
        }
        if (controls.size() > 0) {
            params.controls = controls;
        }

        NEStartMeetingOptions options = (NEStartMeetingOptions) getMeetingOptions(new NEStartMeetingOptions());

        showDialogProgress("正在创建会议...");
        mViewModel.startMeeting(params, options, new MeetingCallback());
    }


    private void determineMeetingId() {
        if (usePersonalMeetingId.isChecked()) {
            currentMeetingId = meetingId;
            if (!TextUtils.isEmpty(content)) {
                getEditor(0).setText(content);
                return;
            }
            NEAccountService accountService = NEMeetingKit.getInstance().getAccountService();
            if (accountService == null) {
                onGetPersonalMeetingIdError();
            } else {
                accountService.getAccountInfo((resultCode, resultMsg, resultData) -> {
                    if (!isAdded()) return;
                    if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS && resultData != null) {
                        meetingId = resultData.meetingId;
                        currentMeetingId = meetingId;
                        content = meetingId + (!TextUtils.isEmpty(resultData.shortMeetingId) ? "(短号：" + resultData.shortMeetingId + ")" : "");
                        getEditor(0).setText(content);
                    } else {
                        onGetPersonalMeetingIdError();
                    }
                });
            }
        } else {
            currentMeetingId = null;
            getEditor(0).setText("");
        }
    }

    private void onGetPersonalMeetingIdError() {
        content = null;
        currentMeetingId = null;
        usePersonalMeetingId.setChecked(false);
        Toast.makeText(getActivity(), "获取个人会议号失败", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
