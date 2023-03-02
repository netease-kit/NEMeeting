// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
package com.netease.yunxin.kit.meeting.sampleapp.base;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.util.ArrayList;
import java.util.List;

/** @Description TODO */
public abstract class BaseAdapter<T, VB extends ViewBinding>
    extends RecyclerView.Adapter<BaseAdapter.VH<VB>> {
  protected List<T> mDatas;
  protected VB binding; //默认
  protected OnItemClickListener onItemClickListener;
  private static final int EMPTY_VIEW = 1;

  public BaseAdapter(List<T> datas) {
    this.mDatas = datas;
  }

  public abstract VB getViewBinding(ViewGroup parent, int viewType);

  @Override
  public VH<VB> onCreateViewHolder(ViewGroup parent, int viewType) {
    return VH.get(getViewBinding(parent, viewType));
  }

  @Override
  public void onBindViewHolder(VH<VB> holder, int position) {
    //③ 对RecyclerView的每一个itemView设置点击事件
    holder.itemView.setOnClickListener(
        v -> {
          if (onItemClickListener != null) {
            //                int pos = holder.getLayoutPosition();
            onItemClickListener.onItemClick(holder.itemView, position);
          }
        });
    convert(mDatas.get(position), position, holder);
  }

  @Override
  public int getItemCount() {
    return mDatas.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (mDatas.size() == 0) {
      return EMPTY_VIEW;
    }
    return super.getItemViewType(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public abstract void convert(T data, int position, VH<VB> holder);

  public void addNewData(int position, T data) {
    if (mDatas == null) {
      mDatas = new ArrayList<>();
    }
    mDatas.add(position, data);
    //更新数据集不是用adapter.notifyDataSetChanged()而是notifyItemInserted(position)与notifyItemRemoved(position) 否则没有动画效果。
    notifyItemInserted(position);
  }

  public void updateData(int position, T data) {
    if (mDatas == null) {
      mDatas = new ArrayList<>();
    }
    mDatas.set(position, data);
    notifyItemChanged(position, data);
  }

  public void itemMove(int fromPosition, int toPosition) {
    try {
      mDatas.add(toPosition, mDatas.remove(fromPosition)); //数据更换
    } catch (Exception e) {
      e.printStackTrace();
    }
    notifyItemMoved(fromPosition, toPosition); //执行动画
    notifyItemRangeChanged(
        Math.min(fromPosition, toPosition),
        Math.abs(fromPosition - toPosition) + 1); //受影响的itemd都刷新下
  }

  public List<T> getData() {
    return mDatas;
  }

  //这里用addAll也很重要，如果用this.list = mList，调用notifyDataSetChanged()无效
  //notifyDataSetChanged()数据源发生改变的时候调用的，this.list = mList，list并没有发生改变
  public void setData(List<T> datas) {
    if (mDatas != null) {
      mDatas.clear();
    }
    this.mDatas = datas;
    notifyDataSetChanged();
  }

  public void resetData(List<T> datas) {
    if (mDatas != null) {
      mDatas.clear();
    }
    mDatas.addAll(datas);
    notifyDataSetChanged();
  }

  public void deleteItem(int posion) {
    if (mDatas == null || mDatas.isEmpty()) {
      return;
    }
    mDatas.remove(posion);
    notifyItemRemoved(posion);
  }

  public void clear() {
    if (mDatas == null || mDatas.isEmpty()) {
      return;
    }
    mDatas.clear();
    notifyDataSetChanged();
  }

  // ① 定义点击回调接口
  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  // ② 定义一个设置点击监听器的方法
  public void setOnItemClickListener(OnItemClickListener listener) {
    this.onItemClickListener = listener;
  }

  public static class VH<VB extends ViewBinding> extends RecyclerView.ViewHolder {
    public final VB viewBinding;

    private VH(VB viewBinding) {
      super(viewBinding.getRoot());
      this.viewBinding = viewBinding;
    }

    public static <VB extends ViewBinding> VH<VB> get(VB viewBinding) {
      return new VH(viewBinding);
    }
  }
}
