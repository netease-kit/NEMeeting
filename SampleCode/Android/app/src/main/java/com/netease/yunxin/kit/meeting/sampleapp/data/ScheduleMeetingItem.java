// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.data;

public class ScheduleMeetingItem {

  private String tittle;

  private String subTittle;

  private String timeTip;

  private boolean isSwitchOn;

  private int clickAction;

  private String extraData;

  private String valueString; // 回填数据

  public static final int EDIT_TEXT_TITLE_ACTION = 0;

  public static final int SET_START_TIME_ACTION = 1;

  public static final int SET_END_TIME_ACTION = 2;

  public static final int ENABLE_MEETING_PWD_ACTION = 3;

  public static final int SET_AUDIO_MUTE_ACTION = 4;

  public static final int SET_ALLOW_AUDIO_ON_ACTION = 5;

  public static final int SET_VIDEO_MUTE_ACTION = 6;

  public static final int SET_ALLOW_VIDEO_ON_ACTION = 7;

  public static final int ENABLE_MEETING_LIVE_ACTION = 8;

  public static final int ENABLE_MEETING_LIVE_LEVEL_ACTION = 9;

  public static final int ENABLE_MEETING_RECORD_ACTION = 10;

  public static final int SET_EXTRA_DATA_ACTION = 11;

  public static final int SET_ROLE_BIND = 12; // 设置成员绑定角色

  public static final int ENABLE_MEETING_NO_SIP_ACTION = 13;

  public static final int ENABLE_MEETING_WAITING_ROOM = 14;

  public static final int ENABLE_MEETING_GUEST_JOIN = 15;

  public ScheduleMeetingItem(
      String tittle,
      String subTittle,
      String timeTip,
      boolean isSwitchOn,
      int clickAction,
      String valueString) {
    this.tittle = tittle;
    this.subTittle = subTittle;
    this.clickAction = clickAction;
    this.timeTip = timeTip;
    this.isSwitchOn = isSwitchOn;
    this.valueString = valueString;
  }

  public ScheduleMeetingItem(String tittle, int clickAction, String valueString) {
    this(tittle, "", "", false, clickAction, valueString);
  }

  public ScheduleMeetingItem(String tittle, String subTittle, boolean isSwitchOn, int clickAction) {
    this(tittle, subTittle, "", isSwitchOn, clickAction, "");
  }

  public ScheduleMeetingItem(
      String tittle, boolean isSwitchOn, int clickAction, String valueString) {
    this(tittle, "", "", isSwitchOn, clickAction, valueString);
  }

  public ScheduleMeetingItem(String tittle, String timeTip, int clickAction, String valueString) {
    this(tittle, "", timeTip, false, clickAction, valueString);
  }

  public String getValueString() {
    return valueString;
  }

  public void setValueString(String valueString) {
    this.valueString = valueString;
  }

  public String getTittle() {
    return tittle;
  }

  public void setTittle(String tittle) {
    this.tittle = tittle;
  }

  public String getSubTittle() {
    return subTittle;
  }

  public void setSubTittle(String subTittle) {
    this.subTittle = subTittle;
  }

  public int getClickAction() {
    return clickAction;
  }

  public void setClickAction(int clickAction) {
    this.clickAction = clickAction;
  }

  public String getTimeTip() {
    return timeTip;
  }

  public void setTimeTip(String timeTip) {
    this.timeTip = timeTip;
    this.valueString = timeTip;
  }

  public boolean isSwitchOn() {
    return isSwitchOn;
  }

  public void setSwitchOn(boolean switchOn) {
    isSwitchOn = switchOn;
  }

  public String getExtraData() {
    return extraData;
  }

  public void setExtraData(String extraData) {
    this.extraData = extraData;
  }
}
