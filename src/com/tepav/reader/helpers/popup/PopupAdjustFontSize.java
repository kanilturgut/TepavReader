package com.tepav.reader.helpers.popup;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.tepav.reader.R;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;

/**
 * Author   : kanilturgut
 * Date     : 14/05/14
 * Time     : 18:05
 */
public class PopupAdjustFontSize extends PopupWindows implements PopupWindow.OnDismissListener {


    View mRootView;
    LayoutInflater mInflater;
    OnDismissListener mDismissListener;
    boolean mDidAction;
    int mAnimStyle;
    int rootWidth = 0;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    WebView webView;

    /**
     * Constructor allowing orientation override
     *
     * @param context Context
     */
    public PopupAdjustFontSize(Context context, WebView webView) {
        super(context);

        this.webView = webView;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId(R.layout.popup_adjust_font_size);

        mAnimStyle = ANIM_GROW_FROM_CENTER;
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id) {
        mRootView = mInflater.inflate(id, null);
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(mRootView);


        ImageButton bDecreaseFontSize = (ImageButton) mRootView.findViewById(R.id.bDecreaseFontSize);
        bDecreaseFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logs.i("PopupAdjustFontSize", "clicked decrease");
                int size = webView.getSettings().getDefaultFontSize();
                webView.getSettings().setDefaultFontSize(size - 1);
            }
        });
        ImageButton bIncreaseFontSize = (ImageButton) mRootView.findViewById(R.id.bIncreaseFontSize);
        bIncreaseFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logs.i("PopupAdjustFontSize", "clicked increase");
                int size = webView.getSettings().getDefaultFontSize();
                webView.getSettings().setDefaultFontSize(size + 1);
            }
        });

        ImageButton bDefaultFontSize = (ImageButton) mRootView.findViewById(R.id.bDefaultFontSize);
        bDefaultFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.getSettings().setDefaultFontSize(Constant.DEFAULT_FONT_SIZE);
            }
        });

    }

    /**
     * Set animation style
     *
     * @param mAnimStyle animation style, default is set to ANIM_AUTO
     */
    public void setAnimStyle(int mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }


    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     */
    public void show(View anchor) {
        preShow();

        int xPos, yPos;

        mDidAction = false;

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                + anchor.getHeight());

        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        int rootHeight = mRootView.getMeasuredHeight();

        if (rootWidth == 0) {
            rootWidth = screenWidth;
        }

        //automatically get X coord of popup (top left)
        if ((anchorRect.left + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.getWidth());
            xPos = (xPos < 0) ? 0 : xPos;

        } else {
            if (anchor.getWidth() > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }

        }

        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom) ? true : false;

        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;
        }


        setAnimationStyle(onTop);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    /**
     * Set animation style
     *
     * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
     *              and vice versa
     */
    void setAnimationStyle(boolean onTop) {

        switch (mAnimStyle) {

            case ANIM_GROW_FROM_CENTER:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                break;
        }
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
     * by clicking outside the dialog or clicking on sticky item.
     */
    public void setOnDismissListener(PopupAdjustFontSize.OnDismissListener listener) {
        setOnDismissListener(this);

        mDismissListener = listener;
    }

    @Override
    public void onDismiss() {
        if (!mDidAction && mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    /**
     * Listener for window dismiss
     */
    public interface OnDismissListener {
        public abstract void onDismiss();
    }
}


