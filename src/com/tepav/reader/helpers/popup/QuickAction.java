package com.tepav.reader.helpers.popup;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import org.json.JSONException;

public class QuickAction extends PopupWindows implements OnDismissListener {

    private View mRootView;
    private LayoutInflater mInflater;
    private OnDismissListener mDismissListener;

    private boolean mDidAction;

    private int mAnimStyle;
    private int rootWidth = 0;

    public static final int ANIM_GROW_FROM_CENTER = 3;

    News news = null;
    Blog blog = null;
    Publication publication = null;


    /**
     * Constructor allowing orientation override
     *
     * @param context Context
     */
    public QuickAction(Context context, DBHandler dbHandler, Object object) {
        super(context);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId(R.layout.popup_horizontal, dbHandler, object);

        mAnimStyle = ANIM_GROW_FROM_CENTER;
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id, final DBHandler dbHandler, final Object object) {
        mRootView = mInflater.inflate(id, null);
        mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setContentView(mRootView);



        if (object instanceof News) {
            news = (News) object;
        } else if (object instanceof Blog) {
            blog = (Blog) object;
        } else if (object instanceof Publication) {
            publication = (Publication) object;
        }

        LinearLayout llReadList = (LinearLayout) mRootView.findViewById(R.id.llReadList);
        llReadList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OnClick", "llReadList");

                if (news != null) {
                    try {
                        dbHandler.insert(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (blog != null) {
                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (publication != null) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });
        LinearLayout llFavList = (LinearLayout) mRootView.findViewById(R.id.llFavList);
        llFavList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OnClick", "llFavList");

                if (news != null) {
                    try {
                        dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (blog != null) {
                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (publication != null) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });
        LinearLayout llArchive = (LinearLayout) mRootView.findViewById(R.id.llArchive);
        llArchive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OnClick", "llArchive");
                if (news != null) {
                    try {
                        dbHandler.insert(News.toDBData(news), DBHandler.TABLE_ARCHIVE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (blog != null) {
                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_ARCHIVE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (publication != null) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_ARCHIVE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
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

        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

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
    private void setAnimationStyle(boolean onTop) {

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
    public void setOnDismissListener(QuickAction.OnDismissListener listener) {
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