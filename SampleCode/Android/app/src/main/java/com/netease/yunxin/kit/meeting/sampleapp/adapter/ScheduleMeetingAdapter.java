// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
package com.netease.yunxin.kit.meeting.sampleapp.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.lifecycle.MutableLiveData;
import com.kyleduo.switchbutton.SwitchButton;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.data.ScheduleMeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.ItemScheduleMeetingBinding;
import java.util.List;

public class ScheduleMeetingAdapter
    extends BaseAdapter<ScheduleMeetingItem, ItemScheduleMeetingBinding> {

  private Context context;

  private OnCheckedChangeListener mChildOnCheckedChangeListener;

  private MutableLiveData<String> passWord;

  private MutableLiveData<String> tittle;

  private MutableLiveData<String> extraData;

  private MutableLiveData<String> roleBindData;

  public static final int VIEW_TYPE = 100;

  public ScheduleMeetingAdapter(
      Context context,
      List<ScheduleMeetingItem> data,
      MutableLiveData<String> passWord,
      MutableLiveData<String> tittle,
      MutableLiveData<String> extraData,
      MutableLiveData<String> roleBindData) {
    super(data);
    this.context = context;
    this.passWord = passWord;
    this.tittle = tittle;
    this.extraData = extraData;
    this.roleBindData = roleBindData;
  }

  @Override
  public ItemScheduleMeetingBinding getViewBinding(ViewGroup parent, int viewType) {
    return ItemScheduleMeetingBinding.inflate(
        LayoutInflater.from(parent.getContext()), parent, false);
  }

  @Override
  public int getItemViewType(int position) {
    return VIEW_TYPE;
  }

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
    edtMeetingPwd.addTextChangedListener(
        new TextWatcherAdapter() {

          @Override
          public void afterTextChanged(Editable editable) {
            if (editable != null) {
              passWord.setValue(editable.toString());
            }
          }
        });
    edtMeetingTheme.addTextChangedListener(
        new TextWatcherAdapter() {

          @Override
          public void afterTextChanged(Editable editable) {
            if (editable != null) {
              if (data.getClickAction() == ScheduleMeetingItem.SET_EXTRA_DATA_ACTION) {
                extraData.setValue(editable.toString());
              } else if (data.getClickAction() == ScheduleMeetingItem.SET_ROLE_BIND) {
                roleBindData.setValue(editable.toString());
              } else {
                tittle.setValue(editable.toString());
              }
            }
          }
        });
    sbMeetingSwitch.setOnCheckedChangeListener(
        (compoundButton, b) -> {
          if (mChildOnCheckedChangeListener != null) {
            if (data.getClickAction() == ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION) {
              edtMeetingPwd.setVisibility(b ? View.VISIBLE : View.GONE);
            }
            mChildOnCheckedChangeListener.onCheckedChanged(
                compoundButton, b, data.getClickAction());
          }
        });
    switch (data.getClickAction()) {
      case ScheduleMeetingItem.EDIT_TEXT_TITLE_ACTION:
        edtMeetingTheme.setVisibility(View.VISIBLE);
        sbMeetingSwitch.setVisibility(View.GONE);
        if (TextUtils.isEmpty(edtMeetingTheme.getText())) {
          edtMeetingTheme.setText(
              TextUtils.isEmpty(data.getValueString())
                  ? SdkAuthenticator.getAccount(null, 4) + "的预约会议"
                  : data.getValueString());
        }
        break;
      case ScheduleMeetingItem.SET_EXTRA_DATA_ACTION:
        edtMeetingTheme.setVisibility(View.VISIBLE);
        edtMeetingTheme.setHint("");
        if (!TextUtils.isEmpty(data.getValueString())) {
          edtMeetingTheme.setText(data.getValueString());
        }
        break;
      case ScheduleMeetingItem.SET_ROLE_BIND:
        edtMeetingTheme.setVisibility(View.VISIBLE);
        sbMeetingSwitch.setVisibility(View.GONE);
        edtMeetingTheme.setHint("{\"dew323esd23ew23e3r\":1}");
        if (!TextUtils.isEmpty(data.getValueString())) {
          edtMeetingTheme.setText(data.getValueString());
        } else if (!TextUtils.isEmpty(roleBindData.getValue())) {
          edtMeetingTheme.setText(roleBindData.getValue());
        } else {
          edtMeetingTheme.setText("");
        }
        break;
      case ScheduleMeetingItem.SET_START_TIME_ACTION:
      case ScheduleMeetingItem.SET_END_TIME_ACTION:
        tvMeetingTime.setVisibility(View.VISIBLE);
        sbMeetingSwitch.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.getValueString())) {
          tvMeetingTime.setText(data.getValueString());
        } else {
          tvMeetingTime.setText(data.getTimeTip());
        }
        break;
      case ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION:
        sbMeetingSwitch.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(data.getValueString())) {
          sbMeetingSwitch.setChecked(data.isSwitchOn());
          edtMeetingPwd.setText(data.getValueString());
        }
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
        edtMeetingTheme.setVisibility(View.GONE);

        break;
      case ScheduleMeetingItem.ENABLE_MEETING_NO_SIP_ACTION:
      case ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION:
      case ScheduleMeetingItem.ENABLE_MEETING_WAITING_ROOM:
        sbMeetingSwitch.setVisibility(View.VISIBLE);
        sbMeetingSwitch.setChecked(data.isSwitchOn());
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
