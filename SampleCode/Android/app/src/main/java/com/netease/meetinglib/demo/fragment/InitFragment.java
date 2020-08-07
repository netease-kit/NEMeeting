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

public class InitFragment extends Fragment {

    public InitFragment() {
        super(R.layout.fragment_init);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.login).setOnClickListener( v ->
            getContentSwitcher().switchContent(Constants.CONTENT_ID_LOGIN)
        );

        getView().findViewById(R.id.joinMeeting).setOnClickListener( v ->
                getContentSwitcher().switchContent(Constants.CONTENT_ID_JOIN_MEETING_ANONYMOUS)
        );
    }

    private ContentSwitcher getContentSwitcher() {
        return ContentSwitcher.class.cast(getActivity());
    }
}
