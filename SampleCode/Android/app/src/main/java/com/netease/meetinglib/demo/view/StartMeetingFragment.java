/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.netease.meetinglib.demo.viewmodel.StartMeetingViewModel;
import com.netease.meetinglib.sdk.NEAccountService;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingScene;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;

import org.json.JSONArray;
import org.json.JSONObject;


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
        return new String[]{"会议号(留空或使用个人会议号)", "昵称", "请输入密码","个人TAG"};
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
        if (jsonScene != null){
            params.scene = NEMeetingScene.fromJson(jsonScene);
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
            NEAccountService accountService = NEMeetingSDK.getInstance().getAccountService();
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
