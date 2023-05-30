// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

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
