package com.netease.meetinglib.demo.utils;

import android.app.AlertDialog;

public class AlertDialogUtil {
    private static AlertDialog alertDialog;

    public static void setAlertDialog(AlertDialog alertDialog) {
        AlertDialogUtil.alertDialog = alertDialog;
    }

    public static AlertDialog getAlertDialog() {
        return alertDialog;
    }
}
