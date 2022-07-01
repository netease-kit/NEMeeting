/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.base;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.netease.yunxin.kit.meeting.sampleapp.widget.LoadingDialog;


/**
 * @Description A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    protected VB binding;
    @Deprecated
    protected View mRootView;
    private boolean handleOnBackDesktopPressed = false;
    private LoadingDialog dialog;


    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getLayoutResId() != 0) {
            mRootView = inflater.inflate(getLayoutResId(), container, false);
        } else {
            binding = getViewBinding();
            mRootView = binding.getRoot();
        }
        initView();
        initData();
        if (handleOnBackDesktopPressed) {
            handleOnBackPressed();
        }
        return mRootView;
    }

    protected abstract void initView();

    protected abstract void initData();

    protected void setHandleOnBackDesktopPressed(boolean isBackDesktopPressed) {
        handleOnBackDesktopPressed = isBackDesktopPressed;
    }

    //兼容非viewbinding模块
    @Deprecated
    protected int getLayoutResId() {
        return 0;
    }

    protected abstract VB getViewBinding();

    private void handleOnBackPressed() {
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(mHomeIntent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    protected void hideFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .hide(this)
                .commit();
    }


    protected void showDialogProgress() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        LoadingDialog.Builder builder = new LoadingDialog.Builder(getActivity())
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = builder.create();
        dialog.show();
    }

    protected void dissMissDialogProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
