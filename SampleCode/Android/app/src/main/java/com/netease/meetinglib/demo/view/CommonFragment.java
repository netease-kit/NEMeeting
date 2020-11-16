/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.netease.meetinglib.demo.MeetingSettingsActivity;
import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.widget.LoadingDialog;

public abstract class CommonFragment extends Fragment {

    private LoadingDialog dialog;
    protected CommonFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }


    private EditText[] editorArray = new EditText[5];


    protected abstract String getActionLabel();

    protected abstract void performAction(String first, String second,String third);


    protected final EditText getEditor(int index) {
        return editorArray[index];
    }

    protected final String getEditorText(int index) {
        Editable text = getEditor(index).getText();
        return text != null ? text.toString() : null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView title = getView().findViewById(R.id.title);
        title.setText(getActionLabel());


        Button action = getView().findViewById(R.id.actionBtn);
        action.setText(getActionLabel());
        action.setOnClickListener(v -> performAction(getFirstEditTextContent(), getEditorText(1),getEditorText(4)));

        Button actionToMeetingSettings = getView().findViewById(R.id.action_to_meeting_settings);
        actionToMeetingSettings.setOnClickListener(v ->  MeetingSettingsActivity.start(getActivity()));
    }

    protected String getFirstEditTextContent(){
        return getEditorText(0);
    }

    protected void addEditorArray(int i, int editor, String[] labels) {
        editorArray[i] = getView().findViewById(editor);
        editorArray[i].setSingleLine(true);
        if (labels.length > i) {
            editorArray[i].setHint(labels[i]);
        } else {
            editorArray[i].setVisibility(View.GONE);
        }
    }

    protected void showDialogProgress(String message) {
        if (isDialogProgressShowing()) {
            return;
        }
        LoadingDialog.Builder builder = new LoadingDialog.Builder(getActivity())
                .setCancelable(true)
                .setShowMessage(true)
                .setMessage(message)
                .setCancelOutside(true);
        dialog = builder.create();
        dialog.show();
    }

    protected void showDialogProgress() {
        showDialogProgress("");
    }

    protected boolean isDialogProgressShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    protected void dissMissDialogProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
