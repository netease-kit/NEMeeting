// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/** */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
  protected final Context context;

  /** typeless is OK */
  protected RecyclerView.Adapter adapter;

  /** bound to data type T */
  private T data;

  private final Runnable refreshTask =
      new Runnable() {
        @Override
        public void run() {
          bindViewHolder(data);
        }
      };

  public BaseViewHolder(ViewGroup parent, int layoutId) {
    this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
  }

  public BaseViewHolder(View view) {
    super(view);

    context = view.getContext();
  }

  /** find all views */
  public abstract void findViews();

  public final <V extends View> V findViewById(int id) {
    return itemView.findViewById(id);
  }

  /**
   * on bind data
   *
   * @param data
   */
  protected abstract void onBindViewHolder(T data);

  /*package*/
  final void bindViewHolder(T data) {
    this.data = data;
    onBindViewHolder(data);
  }

  protected final void refresh() {
    itemView.removeCallbacks(refreshTask);
    itemView.post(refreshTask);
  }

  /**
   * is clickable, if true item click and long click is delegated
   *
   * @return
   */
  public boolean isClickable() {
    return true;
  }

  public final boolean isFirstItem() {
    return getAdapterPosition() == 0;
  }

  public final boolean isLastItem() {
    return getAdapterPosition() == adapter.getItemCount() - 1;
  }
}
