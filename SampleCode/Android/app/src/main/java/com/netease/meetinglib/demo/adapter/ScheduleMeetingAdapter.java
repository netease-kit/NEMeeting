/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */
package com.netease.meetinglib.demo.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.base.BaseAdapter;
import com.netease.meetinglib.demo.data.ScheduleMeetingItem;
import com.netease.meetinglib.demo.databinding.ItemScheduleMeetingBinding;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

public class ScheduleMeetingAdapter extends BaseAdapter<ScheduleMeetingItem, ItemScheduleMeetingBinding> {

    private Context context;

    private OnCheckedChangeListener mChildOnCheckedChangeListener;

    private MutableLiveData<String> passWord;

    private MutableLiveData<String> tittle;

    private MutableLiveData<String> extraData;

    public ScheduleMeetingAdapter(Context context, List<ScheduleMeetingItem> data,
                                  MutableLiveData<String> passWord,
                                  MutableLiveData<String> tittle,
                                  MutableLiveData<String> extraData
    ) {
        super(data);
        this.context = context;
        this.passWord = passWord;
        this.tittle = tittle;
        this.extraData = extraData;
    }

    @Override
    public ItemScheduleMeetingBinding getViewBinding(ViewGroup parent, int viewType) {
        return ItemScheduleMeetingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void convert(ScheduleMeetingItem data, int position, VH<ItemScheduleMeetingBinding> vh) {
        final ItemScheduleMeetingBinding binding = vh.viewBinding;
        TextView tvMeetingTittle = binding.tvMeetingTittle;
        TextView tvMeetingSubTittle = binding.tvMeetingSubTittle;
        TextView tvMeetingTime = binding.tvMeetingTime;
        TextView edtMeetingTheme = binding.edtMeetingTheme;
        EditText edtMeetingPwd = binding.edtMeetingPwd;
        SwitchButton sbMeetingSwitch = binding.sbMeetingSwitch;
        tvMeetingTittle.setText(data.getTittle());
        if (!TextUtils.isEmpty(data.getSubTittle())) {
            tvMeetingSubTittle.setText(data.getSubTittle());
        }
        if (!TextUtils.isEmpty(data.getTimeTip())) {
            tvMeetingTime.setText(data.getTimeTip());
        }
        edtMeetingPwd.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    passWord.setValue(editable.toString());
                }
            }
        });
        edtMeetingTheme.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (data.getClickAction() == ScheduleMeetingItem.SET_EXTRA_DATA_ACTION) {
                        extraData.setValue(editable.toString());
                    } else {
                        tittle.setValue(editable.toString());
                    }
                }
            }
        });
        sbMeetingSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (mChildOnCheckedChangeListener != null) {
                if (data.getClickAction() == ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION) {
                    edtMeetingPwd.setVisibility(b ? View.VISIBLE : View.GONE);
                }
                mChildOnCheckedChangeListener.onCheckedChanged(compoundButton, b, data.getClickAction());
            }
        });
        switch (data.getClickAction()) {
            case ScheduleMeetingItem.EDIT_TEXT_TITLE_ACTION:
                edtMeetingTheme.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(edtMeetingTheme.getText())) {
                    edtMeetingTheme.setText(SdkAuthenticator.getAccount() + "的预约会议");
                }
                break;
            case ScheduleMeetingItem.SET_EXTRA_DATA_ACTION:
                edtMeetingTheme.setVisibility(View.VISIBLE);
                edtMeetingTheme.setHint("");
                break;
            case ScheduleMeetingItem.SET_START_TIME_ACTION:
            case ScheduleMeetingItem.SET_END_TIME_ACTION:
                tvMeetingTime.setVisibility(View.VISIBLE);
                break;
            case ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION:
                sbMeetingSwitch.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(edtMeetingPwd.getText())) {
                    edtMeetingPwd.setText(String.valueOf((Math.random() * 9 + 1) * 100000).substring(0, 6));
                }
                break;
            case ScheduleMeetingItem.SET_AUDIO_MUTE_ACTION:
            case ScheduleMeetingItem.SET_VIDEO_MUTE_ACTION:
            case ScheduleMeetingItem.SET_ALLOW_AUDIO_ON_ACTION:
            case ScheduleMeetingItem.SET_ALLOW_VIDEO_ON_ACTION:
                sbMeetingSwitch.setVisibility(View.VISIBLE);
                sbMeetingSwitch.setChecked(data.isSwitchOn());
                tvMeetingSubTittle.setVisibility(View.VISIBLE);
                break;
            case ScheduleMeetingItem.ENABLE_MEETING_LIVE_ACTION:
            case ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION:
                sbMeetingSwitch.setVisibility(View.VISIBLE);
                sbMeetingSwitch.setChecked(data.isSwitchOn());
                break;
            case ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION:
                sbMeetingSwitch.setVisibility(View.VISIBLE);
//                sbMeetingSwitch.setChecked(data.isSwitchOn());
                break;
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mChildOnCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CompoundButton compoundButton, boolean b, int clickAction);
    }
}
