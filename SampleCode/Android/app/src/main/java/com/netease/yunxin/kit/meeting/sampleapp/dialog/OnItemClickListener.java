// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.view.View;

/**
 */
public interface OnItemClickListener<T> {

    void onClick(View v, int pos, T data);

    boolean onLongClick(View v, int pos, T data);
}
