// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;


import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.utils.StatusBarUtil;
import com.netease.yunxin.kit.meeting.sampleapp.widget.LoadingDialog;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB binding;
    private LoadingDialog dialog;

    final String logTag = getClass().getSimpleName() + '@' + hashCode();

    protected abstract void initView();

    protected abstract void initData();

    //兼容非viewbinding模块
//    @Deprecated
//    protected int getLayoutResId() {
//        return 0;
//    }

    //  overwrite the function in sub-activity and return the layout id of sub-activity
    protected abstract VB getViewBinding();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setStatusBar();

        binding = getViewBinding();
        View view = binding.getRoot();
        setContentView(view);
        initView();
        initData();
        LogUtil.log(logTag, "onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.log(logTag, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.log(logTag, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.log(logTag, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.log(logTag, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.log(logTag, "onDestroy");
    }

    protected View getContentView() {
        return findViewById(android.R.id.content);
    }


    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.blue));
        //这里做了两件事情，1.使状态栏透明并使contentView填充到状态栏 2.预留出状态栏的位置，防止界面上的控件离顶部靠的太近。这样就可以实现开头说的第二种情况的沉浸式状态栏了
//        StatusBarUtil.setTransparent(this);
    }

    protected void showDialogProgress(String message) {
        if (isDialogProgressShowing()) {
            return;
        }
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this)
                .setCancelable(true)
                .setShowMessage(true)
                .setMessage(message)
                .setCancelOutside(true);
        dialog = builder.create();
        dialog.show();
    }

    protected void showDialogProgress() {
        showDialogProgress("");
    }

    protected boolean isDialogProgressShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    protected void dissMissDialogProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
