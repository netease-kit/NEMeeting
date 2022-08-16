/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */
package com.netease.meetinglib.demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.netease.meetinglib.demo.base.BaseAdapter;
import com.netease.meetinglib.demo.data.ScheduleMeetingDetailItem;
import com.netease.meetinglib.demo.databinding.ItemScheduleMeetingDetailBinding;
import com.netease.meetinglib.demo.utils.DateUtil;

import java.util.List;

public class ScheduleMeetingDetailAdapter extends BaseAdapter<ScheduleMeetingDetailItem, ItemScheduleMeetingDetailBinding> {
    private Context context;
    private OnChildClickListener mOnChildClickListener;


    public ScheduleMeetingDetailAdapter(Context context, List<ScheduleMeetingDetailItem> data) {
        super(data);
        this.context = context;
    }

    @Override
    public ItemScheduleMeetingDetailBinding getViewBinding(ViewGroup parent, int viewType) {
        return ItemScheduleMeetingDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void convert(ScheduleMeetingDetailItem data, int position, VH<ItemScheduleMeetingDetailBinding> vh) {
        final ItemScheduleMeetingDetailBinding binding = vh.viewBinding;
        TextView tvMeetingStart = binding.tvMeetingStart;
        TextView tvMeetingDes = binding.tvMeetingDes;
        TextView tvMeetingEnd = binding.tvMeetingEnd;

        tvMeetingEnd.setOnClickListener(view -> {
            if (mOnChildClickListener != null) {
                mOnChildClickListener.onChildClickListener(data.getClickAction());
            }
        });
        tvMeetingStart.setText(data.getStart());
        if (!TextUtils.isEmpty(data.getDescription())) {
            tvMeetingDes.setText(data.getDescription());
            tvMeetingDes.setVisibility(View.VISIBLE);
        } else {
            tvMeetingDes.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(data.getEnd())) {
            String end;
            if (data.getClickAction() == 0) {//0特殊界面映射
                end = DateUtil.stampToDate(data.getEnd());
            } else {
                end = data.getEnd();
                tvMeetingEnd.setTextColor(Color.parseColor("#FF337EFF"));
            }
            tvMeetingEnd.setText(end);
            tvMeetingEnd.setVisibility(View.VISIBLE);
        } else {
            tvMeetingEnd.setVisibility(View.GONE);
        }

        if (data.getOn() != null) {
            binding.switchButton.setVisibility(View.VISIBLE);
            binding.switchButton.setChecked(data.getOn());
        }
    }

    public void setOnChildClickListener(OnChildClickListener mOnChildClickListener) {
        this.mOnChildClickListener = mOnChildClickListener;
    }

    public interface OnChildClickListener {
        void onChildClickListener(int clickAction);
    }
}
