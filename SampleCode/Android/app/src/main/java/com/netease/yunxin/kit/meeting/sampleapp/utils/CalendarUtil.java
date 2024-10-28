// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
  private static DatePickerDialog dialog;

  public static DatePickerDialog showDatePickerDialog(
      Context context, int mYear, int mMonth, int mDay, OnDateSetListener onDateSetListener) {
    DatePickerDialog datePickerDialog =
        new DatePickerDialog(
            context,
            (view, year, month, dayOfMonth) -> {
              if (onDateSetListener != null) {
                onDateSetListener.onDateSet(view, year, month, dayOfMonth);
              }
            },
            mYear,
            mMonth,
            mDay);
    datePickerDialog.show();
    return datePickerDialog;
  }

  public interface OnDateResultCallback {
	void onDateResult(long date);
  }

  public static void showDateTimePickerDialog(Context context, long time, OnDateResultCallback listener) {
	Calendar calendar = Calendar.getInstance();
	if (time != 0) {
	  calendar.setTime(new Date(time));
	}
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH);
	int day = calendar.get(Calendar.DAY_OF_MONTH);

	dialog =
			new DatePickerDialog(
					context,
					(view, selectedYear, monthOfYear, dayOfMonth) -> {
					  calendar.set(Calendar.YEAR, selectedYear);
					  calendar.set(Calendar.MONTH, monthOfYear);
					  calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					  int hour = calendar.get(Calendar.HOUR_OF_DAY);
					  int minute = calendar.get(Calendar.MINUTE);

					  TimePickerDialog timePickerDialog =
							  new TimePickerDialog(
									  context,
									  (view1, hourOfDay, minute1) -> {
										calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
										calendar.set(Calendar.MINUTE, minute1);
										listener.onDateResult(calendar.getTime().getTime());
									  },
									  hour,
									  minute,
									  true);

					  timePickerDialog.show();
					},
					year,
					month,
					day);
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
