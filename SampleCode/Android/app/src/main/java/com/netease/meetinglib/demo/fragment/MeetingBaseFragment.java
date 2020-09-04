/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.TestActivity;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingInfo;
import com.netease.meetinglib.sdk.NEMeetingMenuItem;
import com.netease.meetinglib.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.meetinglib.sdk.NEMeetingOptions;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.netease.meetinglib.sdk.NEMeetingStatusListener;

import java.util.ArrayList;
import java.util.List;

public abstract class MeetingBaseFragment extends BaseFragment {

    MeetingBaseFragment() {
        super(R.layout.fragment_meeting_base);
    }

    protected CheckBox usePersonalMeetingId;
    private CheckBox[] checkBoxes = new CheckBox[4];
    private CheckBox useDefaultMeetingOptions;
    protected List<NEMeetingMenuItem> injectedMoreMenuItems = new ArrayList<>();
    private NEMeetingService meetingService;

    public NEMeetingService getMeetingService() {
        if (meetingService == null) {
            meetingService = NEMeetingSDK.getInstance().getMeetingService();
        }
        return meetingService;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        //        TextView menuItemId = getView().findViewById(R.id.menuItemId);
//        TextView menuTitle = getView().findViewById(R.id.menuTitle);
        checkBoxes[0] = view.findViewById(R.id.videoOption);
        checkBoxes[1] = view.findViewById(R.id.audioOption);
        checkBoxes[2] = view.findViewById(R.id.noChatOptions);
        checkBoxes[3] = view.findViewById(R.id.noInviteOptions);

        usePersonalMeetingId = view.findViewById(R.id.usePersonalMeetingId);
        useDefaultMeetingOptions = view.findViewById(R.id.useDefaultOptions);

        addEditorArray(2, R.id.addItemIdEditor, getEditorLabel());
        addEditorArray(3, R.id.addTittleEditor, getEditorLabel());
        Button addMenuItemButton = getView().findViewById(R.id.addMenuItemButton);
        addMenuItemButton.setText("增加菜单");
        addMenuItemButton.setOnClickListener(v -> addMenuItem());
        useDefaultMeetingOptions.setChecked(false);
        useDefaultMeetingOptions.setOnCheckedChangeListener((checkbox, checked) -> {
            checkBoxes[0].setEnabled(!checked);
            checkBoxes[1].setEnabled(!checked);
        });
        injectedMoreMenuItems.clear();
        NEMeetingSDK.getInstance().getMeetingService().addMeetingStatusListener(listener);
        getMeetingService().setOnInjectedMenuItemClickListener(new OnCustomMenuListener());
    }


    public class OnCustomMenuListener implements NEMeetingOnInjectedMenuItemClickListener {
        @Override
        public void onInjectedMenuItemClick(NEMeetingMenuItem menuItem, NEMeetingInfo meetingInfo) {
            switch (menuItem.itemId) {
                case 101:
                    NEMeetingSDK.getInstance().getMeetingService().getCurrentMeetingInfo(new NECallback<NEMeetingInfo>() {
                        @Override
                        public void onResult(int resultCode, String resultMsg, NEMeetingInfo resultData) {
                            Toast.makeText(getActivity(), "获取房间信息NEMeetingInfo:" + resultData.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("OnCustomMenuListener", "getCurrentMeetingInfo:resultCode " + resultCode + "#resultData " + resultData.toString());
                        }
                    });
                    break;
                default:
                    Toast.makeText(getActivity(), "点击事件Id:" + menuItem.itemId + "#点击事件tittle:" + menuItem.itemId, Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), TestActivity.class));
                    break;

            }
            Log.d("OnCustomMenuListener", "onInjectedMenuItemClicked:menuItem " + menuItem.toString() + "#" + meetingInfo.toString());
        }
    }

    //首次添加，仅测试使用
//    private void addMeetingInfoItem() {
//        NEMeetingMenuItem meetingMenuItem = new NEMeetingMenuItem();
//        meetingMenuItem.itemId = 101;
//        meetingMenuItem.title = "获取房间信息";
//        injectedMoreMenuItems.add(meetingMenuItem);
//    }

    private void addMenuItem() {
        String id = getEditorText(2);
        String tittle = getEditorText(3);
        if ("".equals(id) || "".equals(tittle)) {
            Toast.makeText(getActivity(), "参数不允许为空" + id, Toast.LENGTH_SHORT).show();
            return;
        }

        NEMeetingMenuItem meetingMenuItem = new NEMeetingMenuItem();
        assert id != null;
        meetingMenuItem.itemId = Integer.parseInt(id);
        meetingMenuItem.title = tittle;
        injectedMoreMenuItems.add(meetingMenuItem);
        Toast.makeText(getActivity(), "添加自定义菜单成功", Toast.LENGTH_SHORT).show();
    }

    public NEMeetingOptions getMeetingOptions(NEMeetingOptions options) {
        options.noVideo = !isChecked(0);
        options.noAudio = !isChecked(1);
        options.noChat = isChecked(2);
        options.noInvite = isChecked(3);
//        addMeetingInfoItem();
        if (injectedMoreMenuItems != null && injectedMoreMenuItems.size() > 0) {
            options.injectedMoreMenuItems = injectedMoreMenuItems;
        }
        return options;
    }

    protected final boolean isNotUseDefaultMeetingOptions() {
        return !useDefaultMeetingOptions.isChecked();
    }

    public void clear() {
        if (injectedMoreMenuItems != null) {
            injectedMoreMenuItems.clear();
        }
    }

    protected final boolean isChecked(int index) {
        return checkBoxes[index].isChecked();
    }

    protected class MeetingCallback extends ToastCallback<Void> {

        public MeetingCallback() {
            super(getActivity(), getActionLabel());
        }

        @Override
        public void onResult(int resultCode, String resultMsg, Void resultData) {
            if (isAdded()) hideLoading();
            if (resultCode == NEMeetingError.ERROR_CODE_NO_AUTH) {
                Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
                SdkAuthenticator.getInstance().logout(false);
            } else if (resultCode == NEMeetingError.ERROR_CODE_FAILED) {
                Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
                hideLoading();
            } else {
                super.onResult(resultCode, resultMsg, resultData);
            }
            if (injectedMoreMenuItems != null) {
                injectedMoreMenuItems.clear();
            }
        }
    }


    private NEMeetingStatusListener listener = event -> {
        if (event.status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
            clear();
            // 增加会议断开连接提示。
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "会议已断开连接", Toast.LENGTH_SHORT).show());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        NEMeetingSDK.getInstance().getMeetingService().removeMeetingStatusListener(listener);
    }
}
