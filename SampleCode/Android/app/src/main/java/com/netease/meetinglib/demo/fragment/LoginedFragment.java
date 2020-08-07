/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.netease.meetinglib.demo.Constants;
import com.netease.meetinglib.demo.ContentSwitcher;
import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;


public class LoginedFragment extends Fragment {

    public LoginedFragment() {
        super(R.layout.fragment_logined);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getView().findViewById(R.id.startMeeting).setOnClickListener( v ->
            getContentSwitcher().switchContent(Constants.CONTENT_ID_START_MEETING)
        );

        getView().findViewById(R.id.joinMeeting).setOnClickListener(v ->
            getContentSwitcher().switchContent(Constants.CONTENT_ID_JOIN_MEETING)
        );

        getView().findViewById(R.id.logout).setOnClickListener( v -> SdkAuthenticator.getInstance().logout(true));
    }


    private ContentSwitcher getContentSwitcher() {
        return ContentSwitcher.class.cast(getActivity());
    }
}
