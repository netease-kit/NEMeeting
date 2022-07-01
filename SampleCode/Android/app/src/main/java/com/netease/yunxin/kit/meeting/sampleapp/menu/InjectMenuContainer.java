/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.menu;

import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;

import java.util.List;

public class InjectMenuContainer {

    private static List<NEMeetingMenuItem> selectedMenu;

    public static List<NEMeetingMenuItem> getSelectedMenu() {
        final List<NEMeetingMenuItem> tmp = selectedMenu;
        selectedMenu = null;
        return tmp;
    }

    public static void setSelectedMenu(List<NEMeetingMenuItem> selectedMenu) {
        InjectMenuContainer.selectedMenu = selectedMenu;
    }
}
