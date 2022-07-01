// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.view.ViewGroup;

/**
 */
public abstract class BaseDelegate<T> {

    final BaseViewHolder createViewHolder(BaseAdapter<T> adapter, ViewGroup parent, int viewType) {
        BaseViewHolder vh = onCreateViewHolder(parent, viewType);
        if (vh != null) {
            vh.adapter = adapter;
        }
        return vh;
    }

    /**
     * crate view holder by view type
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * get view type by data
     *
     * @param data
     * @param pos
     * @return
     */
    public abstract int getItemViewType(T data, int pos);

    public void onDataSetChanged() {

    }
}
