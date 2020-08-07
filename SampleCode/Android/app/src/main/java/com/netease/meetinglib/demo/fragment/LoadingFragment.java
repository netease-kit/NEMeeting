/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.netease.meetinglib.demo.R;

public class LoadingFragment extends DialogFragment {

    public static LoadingFragment newInstance(String msg) {
        LoadingFragment dialogFragment = new LoadingFragment();
        dialogFragment.setArguments(new Bundle());
        dialogFragment.getArguments().putString("msg", msg);
        return dialogFragment;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1 && isAdded()) {
                LoadingFragment.super.dismissAllowingStateLoss();
            } else {
                super.handleMessage(msg);
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_loading);
        setCancelable(false);
        ((TextView) dialog.findViewById(R.id.loading)).setText(getArguments().getString("msg"));
        return dialog;
    }

    public void setMessage(String message) {
        getArguments().putString("msg", message);
        if (getDialog() != null) {
            ((TextView) getDialog().findViewById(R.id.loading)).setText(message);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove self from parent if we are restoring from saved state
        if (savedInstanceState != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void dismiss() {
        if (!isAdded()) return;
        if (handler.hasMessages(1)) return;
        handler.sendEmptyMessageDelayed(1, 500);
    }
}
