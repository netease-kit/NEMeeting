/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.sdk.NEMeetingError;

public abstract class MeetingBaseFragment extends BaseFragment {

    MeetingBaseFragment() {
        super(R.layout.fragment_meeting_base);
    }

    private CheckBox[] checkBoxes = new CheckBox[2];

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkBoxes[0] = getView().findViewById(R.id.videoOption);
        checkBoxes[1] = getView().findViewById(R.id.audioOption);
    }

    protected final boolean isChecked(int index) {
        return checkBoxes[index].isChecked();
    }

    protected class MeetingCallback extends ToastCallback<Void> {

        public MeetingCallback() {
            super(getActivity(), getActionLabel());
        }

        @Override
        public void onResult(int resultCode, String resultMsg, Void resultData) {
            if (isAdded()) hideLoading();
            if (resultCode == NEMeetingError.ERROR_CODE_NO_AUTH) {
                Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
                SdkAuthenticator.getInstance().logout(false);
            } else {
                super.onResult(resultCode, resultMsg, resultData);
            }
        }

    }
}
