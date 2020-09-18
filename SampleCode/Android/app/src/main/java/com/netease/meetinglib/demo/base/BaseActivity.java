package com.netease.meetinglib.demo.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.utils.StatusBarUtil;
import com.netease.meetinglib.demo.widget.LoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
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
