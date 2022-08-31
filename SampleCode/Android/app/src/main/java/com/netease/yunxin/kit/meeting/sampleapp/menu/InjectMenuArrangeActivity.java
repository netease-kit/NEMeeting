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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义注入菜单编排
 */
public class InjectMenuArrangeActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, InjectMenuArrangeActivity.class);
        context.startActivity(starter);
    }

    protected boolean edited = false;
    protected ActivityMenuArrangementBinding binding;
    List<NEMeetingMenuItem> selectedItems = new ArrayList<>();

    protected static int itemId = NEMenuIDs.FIRST_INJECTED_MENU_ID;

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
        items.add(new NESingleStateMenuItem(100, NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo("单选菜单", 0)));
        items.add(new NESingleStateMenuItem(101, NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo("多选菜单", 0)));
        items.add(new NESingleStateMenuItem(102, NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo("音频管理", 0)));
        items.add(NEMenuItems.switchShowTypeMenu());
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.choices.setLayoutManager(layoutManager);
        binding.choices.addItemDecoration(new ItemDecoration());
        binding.choices.setAdapter(new Adapter(this, Adapter.TYPE_CANDIDATE, items));
    }

    private void selectItem(NEMeetingMenuItem item) {
        edited = true;
        int itemId = item.getItemId();
        if (itemId == 100) {
            item = createSingleStateMenuItem();
        } else if (itemId == 101) {
            item = createCheckableMenuItem();
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
                NEMenuItemInfo info =
                        ((NESingleStateMenuItem) item).getSingleStateItem();
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
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("编辑菜单")
                    .setView(view)
                    .setCancelable(true)
                    .setPositiveButton("确定", (dialog, which) -> {
                        try {
                            item.setItemId((int) Long.parseLong(itemIdEdx.getText().toString()));
                        } catch (NumberFormatException e) {
                        }
                        if (item instanceof NESingleStateMenuItem) {
                            ((NESingleStateMenuItem) item).getSingleStateItem().setText(uncheckedEdx.getText().toString());
                        }
                        if (item instanceof NECheckableMenuItem) {
                            ((NECheckableMenuItem) item).getUncheckStateItem().setText(uncheckedEdx.getText().toString());
                            ((NECheckableMenuItem) item).getCheckedStateItem().setText(checkedEdx.getText().toString());
                        }
                        binding.selected.getAdapter().notifyItemChanged(index);
                    }).create();
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

    protected NESingleStateMenuItem createSingleStateMenuItem() {
        final int id = itemId++;
        return new NESingleStateMenuItem(id, NEMenuVisibility.VISIBLE_ALWAYS,
                new NEMenuItemInfo(String.valueOf(id), R.drawable.mood));
    }

    protected NECheckableMenuItem createCheckableMenuItem() {
        final int id = itemId++;
        return new NECheckableMenuItem(id,
                NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo(
                id + "-未选中", R.drawable.check_box_uncheck),
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
                return new CandidateVH(MenuItemCandidateBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent,
                        false));
            } else if (viewType == TYPE_SELECTED) {
                return new SelectionVH(MenuItemSelectedBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent,
                        false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                     int position) {
            if (holder instanceof CandidateVH) {
                NEMeetingMenuItem item = itemList.get(position);
                ((CandidateVH) holder).bind(item);
                holder.itemView.setOnClickListener(v -> {
                    selectItem(item);
                });
            } else if (holder instanceof SelectionVH) {
                NEMeetingMenuItem item = itemList.get(position);
                ((SelectionVH) holder).bind(item);
                ((SelectionVH) holder).binding.remove.setOnClickListener(v -> {
                    deleteItem(holder.getAdapterPosition());
                });
                holder.itemView.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(context,
                            holder.itemView, Gravity.BOTTOM);
                    popupMenu.inflate(R.menu.edit_menu_item_menu);
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                editItem(holder.getAdapterPosition());
                                break;
                            case R.id.delete:
                                deleteItem(holder.getAdapterPosition());
                                break;
                            case R.id.visibleAlways:
                                changeItemVisibility(holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_ALWAYS);
                                break;
                            case R.id.visibleExcludeHost:
                                changeItemVisibility(holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_EXCLUDE_HOST);
                                break;
                            case R.id.visibleToHostOnly:
                                changeItemVisibility(holder.getAdapterPosition(), NEMenuVisibility.VISIBLE_TO_HOST_ONLY);
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
        if (item instanceof NESingleStateMenuItem) {
            return new String[]{((NESingleStateMenuItem) item).getSingleStateItem().getText(), ""};
        } else if (item instanceof NECheckableMenuItem) {
            return new String[]{((NECheckableMenuItem) item).getUncheckStateItem().getText(),
                    ((NECheckableMenuItem) item).getCheckedStateItem().getText()};
        }
        return new String[]{"", ""};
    }

    private static class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.set(5, 5, 5, 5);
        }
    }
}

