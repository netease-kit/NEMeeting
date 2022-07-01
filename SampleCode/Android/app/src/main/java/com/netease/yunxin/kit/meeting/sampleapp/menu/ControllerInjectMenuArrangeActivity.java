package com.netease.yunxin.kit.meeting.sampleapp.menu;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sdk.menu.NECheckableMenuItem;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuItemInfo;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuVisibility;
import com.netease.yunxin.kit.meeting.sdk.menu.NESingleStateMenuItem;

public class ControllerInjectMenuArrangeActivity extends InjectMenuArrangeActivity {

    @Override
    protected void changeItemIcon(int index) {
        if (index >= 0 && index < selectedItems.size()) {
            edited = true;
            NEMeetingMenuItem item = selectedItems.get(index);
            if (item instanceof NESingleStateMenuItem) {
                NEMenuItemInfo info =
                        ((NESingleStateMenuItem) item).getSingleStateItem();
                info.setIcon(R.drawable.mood_black);
            }
            if (item instanceof NECheckableMenuItem) {
                ((NECheckableMenuItem) item).getUncheckStateItem().setIcon(R.drawable.check_box_uncheck_black);
                ((NECheckableMenuItem) item).getCheckedStateItem().setIcon(R.drawable.check_box_checked_black);
            }
            binding.selected.getAdapter().notifyItemChanged(index);
        }
    }

    protected NESingleStateMenuItem createSingleStateMenuItem() {
        final int id = itemId++;
        return new NESingleStateMenuItem(id, NEMenuVisibility.VISIBLE_ALWAYS,
                                         new NEMenuItemInfo(String.valueOf(id), R.drawable.mood_black));
    }

    protected NECheckableMenuItem createCheckableMenuItem() {
        final int id = itemId++;
        return new NECheckableMenuItem(id,
                                       NEMenuVisibility.VISIBLE_ALWAYS, new NEMenuItemInfo(
                id + "-未选中", R.drawable.check_box_uncheck_black),
                                       new NEMenuItemInfo(id + "-选中", R.drawable.check_box_checked_black));
    }

}
