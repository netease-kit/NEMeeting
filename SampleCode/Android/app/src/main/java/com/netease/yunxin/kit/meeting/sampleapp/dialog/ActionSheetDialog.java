package com.netease.yunxin.kit.meeting.sampleapp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.netease.yunxin.kit.meeting.sampleapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */

public class ActionSheetDialog extends BottomSheetDialog {

    public static final class ActionItem {

        public static final int DEFAULT_COLOR = R.color.black;

        public static final int TITLE_ACTION = -1;

        public static final int CANCEL_ACTION = -2;

        public int action;

        public String text;

        public int textColor;

        public Object attach;

        public ActionItem(int action, String text) {
            this(action, text, DEFAULT_COLOR);
        }

        public ActionItem(int action, String text, int textColor) {
            this.action = action;
            this.text = text;
            this.textColor = textColor;
        }
    }

    public ActionSheetDialog(@NonNull Context context) {
        super(context, R.style.ActionSheetDialogStyle);
    }

    public ActionSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected ActionSheetDialog(@NonNull Context context, boolean cancelable,
                                OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private List<ActionItem> actions;

    private RecyclerView recyclerView;

    private OnItemClickListener listener;

    public void setTitle(String title) {
        addAction(new ActionItem(ActionItem.TITLE_ACTION, title));
    }

    public void clearActions() {
        if (actions != null) {
            actions.clear();
        }
    }

    public void addAction(int action, String title) {
        addAction(new ActionItem(action, title));
    }

    public void addAction(int action, String title, String attach) {
        ActionItem item = new ActionItem(action, title);
        item.attach = attach;
        addAction(item);
    }

    public void appendAction(int action, String title, String attach) {
        ActionItem item = new ActionItem(action, title);
        item.attach = attach;
        boolean find = false;
        for (ActionItem sub : actions) {
            if (sub.action == item.action) {
                sub.text = item.text;
                sub.attach = item.attach;
                find = true;
                break;
            }
        }
        if (!find) {
            actions.add(item);
        }
    }

    public void addAction(int action, String title, int color) {
        addAction(new ActionItem(action, title, color));
    }

    private void addAction(ActionItem item) {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(item);
    }

    public void setOnItemClickListener(OnItemClickListener<ActionItem> listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.action_sheet);
        findViews();
        initData();
        super.onCreate(savedInstanceState);
    }

    private void findViews() {
        recyclerView = findViewById(R.id.action_list);
    }

    private void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addCancelAction();
        BaseAdapter adapter = new BaseAdapter(actions, listener);
        adapter.setDelegate(new BaseDelegate<ActionItem>() {

            @Override
            public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        return new TitleViewHolder(parent);
                    case 1:
                        return new CancelViewHolder(parent);
                }
                return new ActionViewHolder(parent);
            }

            @Override
            public int getItemViewType(ActionItem data, int pos) {
                if (data.action == ActionItem.TITLE_ACTION) {
                    return 0;
                } else if (data.action == ActionItem.CANCEL_ACTION) {
                    return 1;
                }
                return 2;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void addCancelAction() {
        if (actions != null) {
            for (ActionItem item : actions) {
                if (item.action == ActionItem.CANCEL_ACTION) {
                    actions.remove(item);
                    break;
                }
            }
        }
        // move 2 last
        addAction(new ActionItem(ActionItem.CANCEL_ACTION, getContext().getString(R.string.cancel)));
    }

    private final class ActionViewHolder extends BaseViewHolder<ActionItem> {

        /**
         * single view may be direct construction, eg: TextView view = new TextView(context);
         *
         * @param parent current no use, may be future use
         */
        public ActionViewHolder(ViewGroup parent) {
            super(parent, R.layout.action_sheet_item);
        }

        private TextView actionItem;

        private View splitLine;

        @Override
        public void findViews() {
            actionItem = itemView.findViewById(R.id.action_text);
            splitLine = itemView.findViewById(R.id.split_line);
        }

        @Override
        protected void onBindViewHolder(ActionItem data) {
            if (isFirstItem()) {
                if (adapter.getItemCount() == 2) {// 只有当前和cancel
                    actionItem.setBackgroundResource(R.drawable.white_round_box_13dp_shape_selector);
                    splitLine.setVisibility(View.GONE);
                } else {
                    actionItem.setBackgroundResource(R.drawable.white_top_round_box_13dp_shape_selector);
                }
            } else {
                splitLine.setVisibility(View.VISIBLE);
                if (getAdapterPosition() == adapter.getItemCount() - 2) {// 除了cancel的最后一条
                    actionItem.setBackgroundResource(R.drawable.white_bottom_round_box_13dp_shape_selector);
                } else {
                    actionItem.setBackgroundResource(R.drawable.white_middle_item_selector);
                }
            }
            actionItem.setTextColor(itemView.getContext().getResources().getColor(data.textColor));
            actionItem.setText(data.text);
        }
    }

    private final class TitleViewHolder extends BaseViewHolder<ActionItem> {

        /**
         * single view may be direct construction, eg: TextView view = new TextView(context);
         *
         * @param parent current no use, may be future use
         */
        public TitleViewHolder(ViewGroup parent) {
            super(parent, R.layout.action_sheet_title);
        }

        private TextView actionItem;

        @Override
        public void findViews() {
            actionItem = itemView.findViewById(R.id.action_text);
        }

        @Override
        protected void onBindViewHolder(ActionItem data) {
            actionItem.setText(data.text);
        }
    }

    private final class CancelViewHolder extends BaseViewHolder<ActionItem> {

        /**
         * single view may be direct construction, eg: TextView view = new TextView(context);
         *
         * @param parent current no use, may be future use
         */
        public CancelViewHolder(ViewGroup parent) {
            super(parent, R.layout.action_sheet_cancel);
        }

        private TextView actionItem;

        @Override
        public void findViews() {
            actionItem = itemView.findViewById(R.id.action_text);
        }

        @Override
        protected void onBindViewHolder(ActionItem data) {
            actionItem.setText(data.text);
        }
    }

    @Override
    public void show() {
        super.show();
        addCancelAction();
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
