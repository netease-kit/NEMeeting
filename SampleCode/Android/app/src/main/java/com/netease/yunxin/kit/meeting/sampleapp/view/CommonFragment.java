// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import androidx.fragment.app.Fragment;
import com.netease.yunxin.kit.meeting.sampleapp.widget.LoadingDialog;

public abstract class CommonFragment extends Fragment {

  private LoadingDialog dialog;

  protected abstract void performAction(
      String meetingNum, String displayName, String password, String personalTag);

  protected void showDialogProgress(String message) {
    if (isDialogProgressShowing()) {
      return;
    }
    LoadingDialog.Builder builder =
        new LoadingDialog.Builder(getActivity())
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
