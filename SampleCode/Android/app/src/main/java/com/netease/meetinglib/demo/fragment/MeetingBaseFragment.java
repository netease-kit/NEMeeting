/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.view.View;
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

    protected CheckBox usePersonalMeetingId;
    private CheckBox[] checkBoxes = new CheckBox[2];
    private CheckBox useDefaultMeetingOptions;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        checkBoxes[0] = view.findViewById(R.id.videoOption);
        checkBoxes[1] = view.findViewById(R.id.audioOption);
        usePersonalMeetingId = view.findViewById(R.id.usePersonalMeetingId);
        useDefaultMeetingOptions = view.findViewById(R.id.useDefaultOptions);
        useDefaultMeetingOptions.setChecked(false);
        useDefaultMeetingOptions.setOnCheckedChangeListener((checkbox, checked) -> {
            checkBoxes[0].setEnabled(!checked);
            checkBoxes[1].setEnabled(!checked);
        });
    }

    protected final boolean isNotUseDefaultMeetingOptions() {
        return !useDefaultMeetingOptions.isChecked();
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
