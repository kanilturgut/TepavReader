package com.tepav.reader.helpers.popup;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.User;
import com.tepav.reader.operation.CommentOperation;

/**
 * Author   : kanilturgut
 * Date     : 13/05/14
 * Time     : 14:32
 */
public class CommentWindows extends PopupWindows implements PopupWindow.OnDismissListener {


    View mRootView;
    LayoutInflater mInflater;
    OnDismissListener mDismissListener;
    boolean mDidAction;
    TextView tvUsername, tvUserEmail, tvCancel;
    EditText etCommentContent;
    Button bSendComment;
    int mAnimStyle;
    int rootWidth = 0;
    public static final int ANIM_GROW_FROM_CENTER = 3;

    /**
     * Constructor allowing orientation override
     *
     * @param context Context
     */
    public CommentWindows(Context context, DBData dbData) {
        super(context);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId(R.layout.popup_comment, dbData.getId());

        mAnimStyle = ANIM_GROW_FROM_CENTER;
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id, final String contentId) {
        mRootView = mInflater.inflate(id, null);
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(mRootView);


        User user = User.getInstance();

        tvUsername = (TextView) mRootView.findViewById(R.id.tvCommentUsername);
        tvUsername.setText(user.fullname);

        tvUserEmail = (TextView) mRootView.findViewById(R.id.tvCommentUserEmail);
        tvUserEmail.setText(user.email);

        etCommentContent = (EditText) mRootView.findViewById(R.id.etCommentContent);

        tvCancel = (TextView) mRootView.findViewById(R.id.tvCommentCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss();
                dismiss();
            }
        });

        bSendComment = (Button) mRootView.findViewById(R.id.bCommentSend);
        bSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentOperation.addComment(contentId, etCommentContent.getText().toString().trim());
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
    public void setOnDismissListener(CommentWindows.OnDismissListener listener) {
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

