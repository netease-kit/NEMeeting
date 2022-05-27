package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.yunxin.kit.meeting.sampleapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;


/**
 *
 */
public class EditDialog extends AlertDialog {

    public interface Callback {

        void result(String content);
    }

    private TextView titleView;

    private EditText editView;

    private TextView messageView;

    private TextView cancelView;

    private TextView okView;

    private CharSequence title;

    private CharSequence message;

    private CharSequence hint;

    private String emptyTips;

    private CharSequence cancel;

    private CharSequence ok;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected EditDialog(@NonNull Context context) {
        this(context, R.style.CommonDialogStyle);
    }

    protected EditDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected EditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLayout();
        initView();
        super.onCreate(savedInstanceState);
    }

    /**
     * load layout
     */
    private void loadLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = findViews(inflater);
        setView(view);
        setViewsListener();
    }

    @NonNull
    private View findViews(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_edit_layout, null);
        titleView = view.findViewById(R.id.title);
        messageView = view.findViewById(R.id.message);
        editView = view.findViewById(R.id.edit_box);
        cancelView = view.findViewById(R.id.cancel);
        okView = view.findViewById(R.id.ok);
        return view;
    }

    private void setViewsListener() {
        okView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = editView.getText().toString().trim();
                if (TextUtils.isEmpty(content) && !TextUtils.isEmpty(emptyTips)) {
                    Toast.makeText(getContext(), emptyTips, Toast.LENGTH_LONG).show();
                    return;
                }
                if (callback != null) {
                    callback.result(content);
                }
                hideKeyboard(editView);
                dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(editView);
                dismiss();
            }
        });
    }

    /**
     * init view state
     */
    private void initView() {
        setTitle(title);
        setMessage(message);
        setHint(hint);
        setCancel(cancel);
        setOk(ok);
        showKeyboard(editView);
    }

    public static void hideKeyboard(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(final View view) {
        if (view == null) {
            return;
        }
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }


    public static EditDialog show(Context context, String title, CharSequence message, String hint, boolean cancelable,
                                  boolean cancelOnTouchOutside, Callback callback) {
        return show(context, title, message, hint, null, null, cancelable, cancelOnTouchOutside, null, callback);
    }


    public static EditDialog show(Context context, String title, CharSequence message, String hint, CharSequence cancel,
                                  CharSequence ok, boolean cancelable, boolean cancelOnTouchOutside, String emptyTips,
                                  Callback callback) {
        EditDialog dialog = new EditDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.setCancel(cancel);
        dialog.setOk(ok);
        dialog.setHint(hint);
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.setCallback(callback);
        dialog.setEmptyTips(emptyTips);
        dialog.show();
        return dialog;
    }

    public void setHint(CharSequence hint) {
        if (editView != null) {
            editView.setHint(hint);
            //editView.setSelection(editView.getText().length());// 光标
        } else {
            this.hint = hint;
        }
    }

    public void setEmptyTips(String emptyTips) {
        this.emptyTips = emptyTips;
    }

    public void setCancel(CharSequence cancel) {
        if (cancelView != null) {
            cancelView.setText(TextUtils.isEmpty(cancel) ? getContext().getString(R.string.cancel) : cancel);
        } else {
            this.cancel = cancel;
        }
    }

    public void setOk(CharSequence ok) {
        if (okView != null) {
            okView.setText(TextUtils.isEmpty(ok) ? getContext().getString(R.string.sure) : ok);
        } else {
            this.ok = ok;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleView != null) {
            titleView.setText(title);
            titleView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        } else {
            this.title = title;
        }
    }

    @Override
    public void setMessage(CharSequence message) {
        if (messageView != null) {
            messageView.setText(message);
            messageView.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        } else {
            this.message = message;
        }
    }

}
