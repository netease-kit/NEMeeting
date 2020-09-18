package com.netease.meetinglib.demo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.meetinglib.demo.R;

public class ImageTextButton extends RelativeLayout {

    public static final int ICON_POSITION_LEFT = 0;
    public static final int ICON_POSITION_TOP = 1;
    public static final int ICON_POSITION_RIGHT = 2;
    public static final int ICON_POSITION_BOTTOM = 3;

    private ImageView mIconView;
    private TextView mBtnTextView;
    private LinearLayout mArea;
    // icon positon, default left
    private int mIconPositon = ICON_POSITION_LEFT;

    public ImageTextButton(Context context) {
        super(context);
        init(context, null);
    }

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageTextButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageTextButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        mIconPositon = typedArray.getInt(R.styleable.ImageTextButton_itb_icon_position, ICON_POSITION_LEFT);

        View rootView = initLayout(context);

        mArea = getArea(rootView);
        mIconView = getIconView(mArea);
        mBtnTextView = getBtnTextView(mArea);

        if (mArea == null || mIconView == null || mBtnTextView == null) {
            return;
        }

        mArea.setGravity(CENTER_IN_PARENT);

        // padding
        initPadding(typedArray);
        // backgroud shap
        initBackground(typedArray);
        // button text
        initTextView(typedArray, mBtnTextView);
        // icon
        initIcon(typedArray, mIconView);

        typedArray.recycle();
    }

    private View initLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (mIconPositon) {
//            case ICON_POSITION_LEFT:
//                return inflater.inflate(R.layout.layout_image_text_button_left, this);
//            case ICON_POSITION_TOP:
//                return inflater.inflate(R.layout.layout_image_text, this);
//            case ICON_POSITION_RIGHT:
//                return inflater.inflate(R.layout.layout_image_text_button_right, this);
            case ICON_POSITION_BOTTOM:
//                return inflater.inflate(R.layout.layout_image_text, this);
            default:
                return inflater.inflate(R.layout.layout_image_text, this);
        }
    }

    private LinearLayout getArea(View rootView) {
        if (rootView != null) {
            return (LinearLayout) rootView.findViewById(R.id.area);
        }
        return null;
    }

    private ImageView getIconView(View parentView) {
        if (parentView != null) {
            return (ImageView) parentView.findViewById(R.id.icon);
        }
        return null;
    }

    private TextView getBtnTextView(View parentView) {
        if (parentView != null) {
            return (TextView) parentView.findViewById(R.id.button_text);
        }
        return null;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void initBackground(TypedArray typedArray) {
        int radius = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_itb_radius,
                getResources().getDimensionPixelOffset(R.dimen.default_radius));
        int btnNormalBgColor = typedArray.getColor(R.styleable.ImageTextButton_itb_bg,
                getResources().getColor(R.color.default_bg));
        int btnPressedBgColor = typedArray.getColor(R.styleable.ImageTextButton_itb_bg_pressed,
                -1);
        if (btnPressedBgColor == -1) {
            int alpha = Color.alpha(btnNormalBgColor);
            int red = Color.red(btnNormalBgColor);
            int green = Color.green(btnNormalBgColor);
            int blue = Color.blue(btnNormalBgColor);
            if (alpha < 0xFF) {
                btnPressedBgColor = Color.argb(0xFF, red, green, blue);
            } else {
                btnPressedBgColor = Color.argb(0x90, red, green, blue);
            }
        }

        int btnDisabledBgColor = typedArray.getColor(R.styleable.ImageTextButton_itb_bg_disabled,
                getResources().getColor(R.color.default_bg_disabled));

        GradientDrawable normalShape = new GradientDrawable();
        normalShape.setColor(btnNormalBgColor);
        if (radius > 0) {
            normalShape.setCornerRadius(radius);
        }

        GradientDrawable pressedShape = new GradientDrawable();
        pressedShape.setColor(btnPressedBgColor);
        if (radius > 0) {
            pressedShape.setCornerRadius(radius);
        }

        GradientDrawable disabledShape = new GradientDrawable();
        disabledShape.setColor(btnDisabledBgColor);
        if (radius > 0) {
            disabledShape.setCornerRadius(radius);
        }

        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, pressedShape);
        bg.addState(new int[]{android.R.attr.state_enabled}, normalShape);
        setBackground(bg);
        setClickable(true);
    }

    private void initPadding(TypedArray typedArray) {
        int padding = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_itb_padding,
                getResources().getDimensionPixelOffset(R.dimen.default_padding));
        if (padding > 0) {
            setPadding(padding, padding, padding, padding);
        }
    }

    private void initTextView(TypedArray typedArray, TextView textView) {
        String btnText = typedArray.getString(R.styleable.ImageTextButton_itb_text);
        int btnTextSize = typedArray.getDimensionPixelSize(R.styleable.ImageTextButton_itb_text_size,
                getResources().getDimensionPixelSize(R.dimen.default_text_size));
        if (TextUtils.isEmpty(btnText)) {
            textView.setVisibility(GONE);
        } else {
            int btnTextColor = typedArray.getColor(R.styleable.ImageTextButton_itb_text_color,
                    getResources().getColor(R.color.default_text_color));
            textView.setTextColor(btnTextColor);
            textView.setTextSize(px2dip(btnTextSize));
            int marggin = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_itb_icon_text_marggin,
                    getResources().getDimensionPixelOffset(R.dimen.default_text_margin));
            ViewGroup.MarginLayoutParams params = (MarginLayoutParams) textView.getLayoutParams();
            switch (mIconPositon) {
                case ICON_POSITION_LEFT:
                    params.setMargins(marggin, 0, 0, 0);
                    break;
                case ICON_POSITION_TOP:
                    params.setMargins(0, marggin, 0, 0);
                    break;
                case ICON_POSITION_RIGHT:
                    params.setMargins(0, 0, marggin, 0);
                    break;
                case ICON_POSITION_BOTTOM:
                    params.setMargins(0, 0, 0, marggin);
                    break;
                default:
                    params.setMargins(marggin, 0, 0, 0);
                    break;
            }
            textView.setLayoutParams(params);
            textView.setText(btnText);
        }
    }

    private void initIcon(TypedArray typedArray, ImageView iconView) {
        Drawable icon = typedArray.getDrawable(R.styleable.ImageTextButton_itb_icon);
        if (icon != null) {
            int iconSize = typedArray.getDimensionPixelSize(R.styleable.ImageTextButton_itb_icon_size,
                    getResources().getDimensionPixelOffset(R.dimen.default_icon_width));
            ViewGroup.LayoutParams params = iconView.getLayoutParams();
            params.height = iconSize;
            params.width = iconSize;
            iconView.setLayoutParams(params);
            iconView.setImageDrawable(icon);
        } else {
            iconView.setVisibility(GONE);
        }
    }

    /**
     * getIconView
     *
     * @return ImageView
     */
    public ImageView getIconView() {
        return mIconView;
    }

    /**
     * getButtonTextView
     *
     * @return TextView
     */
    public TextView getButtonTextView() {
        return mBtnTextView;
    }

    /**
     * set image
     *
     * @param resId image resource id
     */
    public void setIcon(int resId) {
        if (mIconView != null) {
            mIconView.setImageResource(resId);
        }
    }

    /**
     * set image
     *
     * @param drawable image
     */
    public void setIcon(Drawable drawable) {
        if (mIconView != null) {
            mIconView.setImageDrawable(drawable);
        }
    }

    /**
     * set button text
     *
     * @param text button text to show
     */
    public void setButtonText(String text) {
        if (mBtnTextView != null) {
            mBtnTextView.setText(text);
        }
    }

    /**
     * change background
     *
     * @param drawable new background
     */
    public void changeBackground(Drawable drawable) {
        setBackground(drawable);
    }
}
