/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.JoinMeetingViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingParams;


public class JoinMeetingFragment extends MeetingCommonFragment {
    private static final String TAG = JoinMeetingFragment.class.getSimpleName();
    private JoinMeetingViewModel mViewModel;
    private String tag;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usePersonalMeetingId.setEnabled(false);
        // 加入会议隐藏录制开关功能
        (getView().findViewById(R.id.noCloudRecord)).setVisibility(View.GONE);
        mViewModel = ViewModelProviders.of(this).get(JoinMeetingViewModel.class);
        initData();
    }

    /**
     * 初始化数据，为了方便测试，可通过ADB传递参数进行验证
     * 当前支持传入tag
     * tag:String类型
     */
    private void initData(){
        Intent intent = getActivity().getIntent();
        tag = intent.getStringExtra("tag");
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号", "昵称", "请输入密码","个人TAG"};
    }

    @Override
    protected String getActionLabel() {
        return isAnonymous() ? "匿名入会" : "加入会议";
    }

    @Override
    protected void performAction(String first, String second,String third,String fourth) {
        NEJoinMeetingParams params = new NEJoinMeetingParams();
        params.meetingId = first;
        params.displayName = second;
        params.password = third;
        if (!TextUtils.isEmpty(tag)){
            params.tag = tag;
        }
        if (!TextUtils.isEmpty(fourth)){
            params.tag = fourth;
        }
        NEJoinMeetingOptions options = (NEJoinMeetingOptions) getMeetingOptions(new NEJoinMeetingOptions());
        showDialogProgress("正在加入会议...");
        mViewModel.joinMeeting(params, options, new MeetingCallback());
    }

    private boolean isAnonymous() {
        return !SdkAuthenticator.getInstance().isAuthorized();
    }
}
