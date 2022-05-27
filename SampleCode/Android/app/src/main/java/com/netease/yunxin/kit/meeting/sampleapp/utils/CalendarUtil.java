package com.netease.yunxin.kit.meeting.sampleapp.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.DatePicker;


import com.manu.mdatepicker.MDatePickerDialog;

public class CalendarUtil {
    private static MDatePickerDialog dialog;

    public static DatePickerDialog showDatePickerDialog(Context context, int mYear, int mMonth, int mDay, OnDateSetListener onDateSetListener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    if (onDateSetListener != null) {
                        onDateSetListener.onDateSet(view, year, month, dayOfMonth);
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
        return datePickerDialog;
    }

    public static void showDatePickerDialog(Context context, MDatePickerDialog.OnDateResultListener onDateResultListener) {
         dialog = new MDatePickerDialog.Builder(context)
                //附加设置(非必须,有默认值)
                .setCanceledTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .setSupportTime(true)
                .setTwelveHour(false)
                //结果回调(必须)
                .setOnDateResultListener(onDateResultListener)
                .build();
        dialog.show();
    }

    public static void closeOptionsMenu() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}
