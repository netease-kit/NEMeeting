// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

public class EmptyRecyclerView extends RecyclerView {

  private ViewGroup emptyView;
  private static final String TAG = "EmptyRecyclerView";

  private final AdapterDataObserver observer =
      new AdapterDataObserver() {
        @Override
        public void onChanged() {
          checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
          Log.i(TAG, "onItemRangeInserted" + itemCount);
          checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
          checkIfEmpty();
        }
      };

  public EmptyRecyclerView(Context context) {
    super(context);
  }

  public EmptyRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void checkIfEmpty() {
    if (emptyView != null && getAdapter() != null) {
      final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
      emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
      setVisibility(emptyViewVisible ? GONE : VISIBLE);
    }
  }

  @Override
  public void setAdapter(Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(observer);
    }
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerAdapterDataObserver(observer);
    }

    checkIfEmpty();
  }

  //设置没有内容时，提示用户的空布局
  public void setEmptyView(View emptyView) {
    this.emptyView = (ViewGroup) emptyView;
    checkIfEmpty();
  }
}
