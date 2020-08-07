/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.netease.meetinglib.demo.ContentSwitcher;
import com.netease.meetinglib.demo.R;

public abstract class BaseFragment extends Fragment {

    protected BaseFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    private LoadingFragment loadingFragment;

    private EditText[] editorArray = new EditText[2];

    protected abstract String[] getEditorLabel();

    protected abstract String getActionLabel();

    protected abstract void performAction(String first, String second);

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

        String[] labels = getEditorLabel();

        editorArray[0] = getView().findViewById(R.id.firstEditor);
        editorArray[0].setSingleLine(true);
        if (labels.length > 0) {
            editorArray[0].setHint(labels[0]);
        } else {
            editorArray[0].setVisibility(View.GONE);
        }

        editorArray[1] = getView().findViewById(R.id.secondEditor);
        editorArray[1].setSingleLine(true);
        if (labels.length > 1) {
            editorArray[1].setHint(labels[1]);
        } else {
            editorArray[1].setVisibility(View.GONE);
        }

        Button action = getView().findViewById(R.id.actionBtn);
        action.setText(getActionLabel());
        action.setOnClickListener(v -> performAction(getEditorText(0), getEditorText(1)));
    }

    protected ContentSwitcher getContentSwitcher() {
        return ContentSwitcher.class.cast(getActivity());
    }

    protected void showLoading(String msg) {
        if (loadingFragment != null) {
            loadingFragment.setMessage(msg);
        } else {
            loadingFragment = LoadingFragment.newInstance(msg);
            loadingFragment.setCancelable(false);
            loadingFragment.showNow(getChildFragmentManager(), "dialog");
        }
    }

    protected void hideLoading() {
        if (loadingFragment != null) {
            loadingFragment.dismiss();
            loadingFragment = null;
        }
    }
}
