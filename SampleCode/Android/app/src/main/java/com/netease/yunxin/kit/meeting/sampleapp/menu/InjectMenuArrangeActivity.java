// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.ActivityMenuArrangementBinding;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.MenuItemCandidateBinding;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.MenuItemSelectedBinding;
import com.netease.yunxin.kit.meeting.sdk.menu.NECheckableMenuItem;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuIDs;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuItemInfo;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuItems;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuVisibility;
import com.netease.yunxin.kit.meeting.sdk.menu.NESingleStateMenuItem;
import java.util.ArrayList;
import java.util.List;

/** 自定义注入菜单编排 */
public class InjectMenuArrangeActivity extends AppCompatActivity {

  public static void start(Context context) {
    Intent starter = new Intent(context, InjectMenuArrangeActivity.class);
    context.startActivity(starter);
  }

  protected boolean edited = false;
  protected ActivityMenuArrangementBinding binding;
  List<NEMeetingMenuItem> selectedItems = new ArrayList<>();

  protected static int itemId = NEMenuIDs.FIRST_INJECTED_MENU_ID + 6;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMenuArrangementBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    initSelections();
    initCandidates();
  }

  @Override
  public void finish() {
    if (edited) {
      InjectMenuContainer.setSelectedMenu(selectedItems);
      setResult(RESULT_OK);
    }
    super.finish();
  }

  private void initSelections() {
    List<NEMeetingMenuItem> selectedMenu = InjectMenuContainer.getSelectedMenu();
    if (selectedMenu != null) {
      selectedItems.addAll(selectedMenu);
    }
    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
    layoutManager.setFlexDirection(FlexDirection.ROW);
    layoutManager.setJustifyContent(JustifyContent.FLEX_START);
    binding.selected.setLayoutManager(layoutManager);
    binding.selected.addItemDecoration(new ItemDecoration());
    binding.selected.setAdapter(new Adapter(this, Adapter.TYPE_SELECTED, selectedItems));
  }

  private void initCandidates() {
    List<NEMeetingMenuItem> items = new ArrayList<>();
    items.addAll(NEMenuItems.getBuiltinToolbarMenuItemList());
    items.addAll(NEMenuItems.getBuiltinMoreMenuItemList());
    items.add(createMenuItem(100, "打开设置"));
    items.add(createMenuItem(101, "最小化"));
    items.add(createMenuItem(102, "音频管理"));
    items.add(createMenuItem(103, "测试修改单选菜单"));
    items.add(createMenuItem(104, "测试修改多选菜单"));
    items.add(NEMenuItems.switchShowTypeMenu());
    items.add(createMenuItem(105, "获取账号信息"));
    items.add(createMenuItem(106, "通用单选菜单"));
    items.add(createMenuItem(107, "通用多选菜单"));
    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
    layoutManager.setFlexDirection(FlexDirection.ROW);
    layoutManager.setJustifyContent(JustifyContent.FLEX_START);
    binding.choices.setLayoutManager(layoutManager);
    binding.choices.addItemDecoration(new ItemDecoration());
    binding.choices.setAdapter(new Adapter(this, Adapter.TYPE_CANDIDATE, items));
  }

  NEMeetingMenuItem createMenuItem(int id, String text) {
    return new NESingleStateMenuItem(
        id, NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo(text, 0));
  }

  private void selectItem(NEMeetingMenuItem item) {
    edited = true;
    int selectId = item.getItemId();
    switch (selectId) {
      case 100:
      case 101:
      case 102:
      case 103:
      case 105:
        item = createSingleStateMenuItem(selectId);
        break;
      case 104:
        item = createCheckableMenuItem(selectId);
        break;
      case 106:
        item = createSingleStateMenuItem(itemId++);
        break;
      case 107:
        item = createCheckableMenuItem(itemId++);
        break;
    }
    selectedItems.add(item);
    binding.selected.getAdapter().notifyItemInserted(selectedItems.size());
  }

  private void deleteItem(int index) {
    if (index >= 0 && index < selectedItems.size()) {
      edited = true;
      selectedItems.remove(index);
      binding.selected.getAdapter().notifyItemRemoved(index);
    }
  }

  private void changeItemVisibility(int index, NEMenuVisibility visibility) {
    if (index >= 0 && index < selectedItems.size()) {
      edited = true;
      selectedItems.get(index).setVisibility(visibility);
      binding.selected.getAdapter().notifyItemChanged(index);
    }
  }

  protected void changeItemIcon(int index) {
    if (index >= 0 && index < selectedItems.size()) {
      edited = true;
      NEMeetingMenuItem item = selectedItems.get(index);
      if (item instanceof NESingleStateMenuItem) {
        NEMenuItemInfo info = ((NESingleStateMenuItem) item).getSingleStateItem();
        info.setIcon(R.drawable.mood);
      }
      if (item instanceof NECheckableMenuItem) {
        ((NECheckableMenuItem) item).getUncheckStateItem().setIcon(R.drawable.check_box_uncheck);
        ((NECheckableMenuItem) item).getCheckedStateItem().setIcon(R.drawable.check_box_checked);
      }
      binding.selected.getAdapter().notifyItemChanged(index);
    }
  }

  private void editItem(final int index) {
    if (index >= 0 && index < selectedItems.size()) {
      edited = true;
      final NEMeetingMenuItem item = selectedItems.get(index);
      View view = View.inflate(this, R.layout.custom_menu_item_titles, null);
      final EditText itemIdEdx = view.findViewById(R.id.itemId);
      final EditText uncheckedEdx = view.findViewById(R.id.uncheckedTitle);
      final EditText checkedEdx = view.findViewById(R.id.checkedTitle);
      AlertDialog alertDialog =
          new AlertDialog.Builder(this)
              .setTitle("编辑菜单")
              .setView(view)
              .setCancelable(true)
              .setPositiveButton(
                  "确定",
                  (dialog, which) -> {
                    try {
                      item.setItemId((int) Long.parseLong(itemIdEdx.getText().toString()));
                    } catch (NumberFormatException e) {
                    }
                    if (item instanceof NESingleStateMenuItem) {
                      ((NESingleStateMenuItem) item)
                          .getSingleStateItem()
                          .setText(uncheckedEdx.getText().toString());
                    }
                    if (item instanceof NECheckableMenuItem) {
                      ((NECheckableMenuItem) item)
                          .getUncheckStateItem()
                          .setText(uncheckedEdx.getText().toString());
                      ((NECheckableMenuItem) item)
                          .getCheckedStateItem()
                          .setText(checkedEdx.getText().toString());
                    }
                    binding.selected.getAdapter().notifyItemChanged(index);
                  })
              .create();
      String[] menuItemTitle = getMenuItemTitle(item);
      itemIdEdx.setText(String.valueOf(item.getItemId()));
      uncheckedEdx.setText(menuItemTitle[0]);
      if (item instanceof NECheckableMenuItem) {
        checkedEdx.setText(menuItemTitle[1]);
      } else {
        checkedEdx.setVisibility(View.GONE);
      }
      alertDialog.show();
    }
  }

  protected NESingleStateMenuItem createSingleStateMenuItem(int id) {
    return new NESingleStateMenuItem(
        id,
        NEMenuVisibility.VISIBLE_ALWAYS,
        new NEMenuItemInfo(String.valueOf(id), R.drawable.mood));
  }

  protected NECheckableMenuItem createCheckableMenuItem(int id) {
    return new NECheckableMenuItem(
        id,
        NEMenuVisibility.VISIBLE_ALWAYS,
        new NEMenuItemInfo(id + "-未选中", R.drawable.check_box_uncheck),
        new NEMenuItemInfo(id + "-选中", R.drawable.check_box_checked));
  }

  private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SELECTED = 0;
    private static final int TYPE_CANDIDATE = 1;

    private final Context context;
    private final int type;
    private final List<NEMeetingMenuItem> itemList;

    Adapter(Context context, int type, List<NEMeetingMenuItem> itemList) {
      this.context = context;
      this.type = type;
      this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
      return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      if (viewType == TYPE_CANDIDATE) {
        return new CandidateVH(
            MenuItemCandidateBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
      } else if (viewType == TYPE_SELECTED) {
        return new SelectionVH(
            MenuItemSelectedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
      }
      return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof CandidateVH) {
        NEMeetingMenuItem item = itemList.get(position);
        ((CandidateVH) holder).bind(item);
        holder.itemView.setOnClickListener(
            v -> {
              selectItem(item);
            });
      } else if (holder instanceof SelectionVH) {
        NEMeetingMenuItem item = itemList.get(position);
        ((SelectionVH) holder).bind(item);
        ((SelectionVH) holder)
            .binding.remove.setOnClickListener(
                v -> {
                  deleteItem(holder.getAdapterPosition());
                });
        holder.itemView.setOnClickListener(
            v -> {
              PopupMenu popupMenu = new PopupMenu(context, holder.itemView, Gravity.BOTTOM);
              popupMenu.inflate(R.menu.edit_menu_item_menu);
              popupMenu.setOnMenuItemClickListener(
                  menuItem -> {
                    switch (menuItem.getItemId()) {
                      case R.id.edit:
                        editItem(holder.getAdapterPosition());
                        break;
                      case R.id.delete:
                        deleteItem(holder.getAdapterPosition());
                        break;
                      case R.id.visibleAlways:
                        changeItemVisibility(
                            holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_ALWAYS);
                        break;
                      case R.id.visibleExcludeHost:
                        changeItemVisibility(
                            holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_EXCLUDE_HOST);
                        break;
                      case R.id.visibleToHostOnly:
                        changeItemVisibility(
                            holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_TO_HOST_ONLY);
                        break;
                      case R.id.changeIcon:
                        changeItemIcon(holder.getAdapterPosition());
                        break;
                    }
                    return true;
                  });
              popupMenu.show();
            });
      }
    }

    @Override
    public int getItemCount() {
      return itemList.size();
    }
  }

  private static class CandidateVH extends RecyclerView.ViewHolder {

    private final MenuItemCandidateBinding binding;

    public CandidateVH(MenuItemCandidateBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(NEMeetingMenuItem item) {
      binding.title.setText(getMenuItemTitle(item)[0]);
      binding.title.setBackgroundResource(R.drawable.menu_item_candidate_bg);
    }
  }

  private static class SelectionVH extends RecyclerView.ViewHolder {
    private final MenuItemSelectedBinding binding;

    public SelectionVH(MenuItemSelectedBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(NEMeetingMenuItem item) {
      binding.title.setText(getMenuItemTitle(item)[0]);
      if (item.getVisibility() == NEMenuVisibility.VISIBLE_ALWAYS) {
        binding.getRoot().setBackgroundResource(R.drawable.menu_item_selected_bg);
      } else if (item.getVisibility() == NEMenuVisibility.VISIBLE_EXCLUDE_HOST) {
        binding.getRoot().setBackgroundResource(R.drawable.menu_item_selected_bg1);
      } else if (item.getVisibility() == NEMenuVisibility.VISIBLE_TO_HOST_ONLY) {
        binding.getRoot().setBackgroundResource(R.drawable.menu_item_selected_bg2);
      }
    }
  }

  private static String[] getMenuItemTitle(NEMeetingMenuItem item) {
    switch (item.getItemId()) {
      case NEMenuIDs.MIC_MENU_ID:
        return new String[] {"静音", "解除静音"};
      case NEMenuIDs.CAMERA_MENU_ID:
        return new String[] {"停止视频", "开启视频"};
      case NEMenuIDs.SCREEN_SHARE_MENU_ID:
        return new String[] {"共享屏幕", "结束共享"};
      case NEMenuIDs.PARTICIPANTS_MENU_ID:
        return new String[] {"参会者"};
      case NEMenuIDs.MANAGE_PARTICIPANTS_MENU_ID:
        return new String[] {"管理参会者"};
      case NEMenuIDs.SWITCH_SHOW_TYPE_MENU_ID:
        return new String[] {"视图布局"};
      case NEMenuIDs.INVITE_MENU_ID:
        return new String[] {"邀请"};
      case NEMenuIDs.CHAT_MENU_ID:
        return new String[] {"聊天"};
      case NEMenuIDs.NOTIFY_CENTER_MENU_ID:
        return new String[] {"通知"};
      case NEMenuIDs.WHITEBOARD_ID:
        return new String[] {"共享白板", "退出白板"};
      case NEMenuIDs.CLOUD_RECORD_ID:
        return new String[] {"云录制", "结束录制"};
      case NEMenuIDs.SECURITY_ID:
        return new String[] {"安全"};
      case NEMenuIDs.DISCONNECT_AUDIO_ID:
        return new String[] {"断开音频", "连接音频"};
    }

    if (item instanceof NESingleStateMenuItem) {
      return new String[] {((NESingleStateMenuItem) item).getSingleStateItem().getText(), ""};
    } else if (item instanceof NECheckableMenuItem) {
      return new String[] {
        ((NECheckableMenuItem) item).getUncheckStateItem().getText(),
        ((NECheckableMenuItem) item).getCheckedStateItem().getText()
      };
    }
    return new String[] {"", ""};
  }

  private static class ItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(
        @NonNull Rect outRect,
        @NonNull View view,
        @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
      outRect.set(5, 5, 5, 5);
    }
  }
}
