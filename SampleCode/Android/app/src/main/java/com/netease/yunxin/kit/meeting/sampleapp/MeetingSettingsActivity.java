// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.netease.yunxin.kit.meeting.sampleapp.view.MeetingSettingsFragment;

public class MeetingSettingsActivity extends AppCompatActivity {

  public static void start(Context context) {
    Intent starter = new Intent(context, MeetingSettingsActivity.class);
    context.startActivity(starter);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FrameLayout frameLayout = new FrameLayout(this);
    frameLayout.setId(View.generateViewId());
    setContentView(
        frameLayout,
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    getSupportFragmentManager()
        .beginTransaction()
        .replace(frameLayout.getId(), new MeetingSettingsFragment())
        .commit();
  }
}
