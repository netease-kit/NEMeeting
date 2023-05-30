// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.view.View;

/** */
public class ItemClickListerAdapter<T> implements OnItemClickListener<T> {

  @Override
  public void onClick(View v, int pos, T data) {}

  @Override
  public boolean onLongClick(View v, int pos, T data) {
    return false;
  }
}
