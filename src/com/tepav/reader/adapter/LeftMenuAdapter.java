package com.tepav.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.helpers.Constant;

/**
 * Author : kanilturgut
 * Date : 20.04.2014
 * Time : 15:28
 */
public class LeftMenuAdapter extends ArrayAdapter<String>{

    Context context;
    String[] menuItems;

    RelativeLayout rlLeftMenuNormal, rlLeftMenuSubMenu, rlLeftMenuNormalWithCount;
    ImageView ivLeftMenuNormalImageOfItem, ivLeftMenuSubMenuImageOfItem, ivLeftMenuNormalImageOfItemlWithCount;
    TextView tvLeftMenuNormalTitleOfItem, tvLeftMenuSubMenuTitleOfItem, tvLeftMenuNormalTitleOfItemlWithCount, tvLeftMenuNormalCountOfItemlWithCount;

    public LeftMenuAdapter(Context context, String[] menuItems) {
        super(context, R.layout.custom_row_left_menu, menuItems);

        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_row_left_menu, parent, false);

        rlLeftMenuNormal = (RelativeLayout) rowView.findViewById(R.id.rlLeftMenuNormal);
        rlLeftMenuSubMenu = (RelativeLayout) rowView.findViewById(R.id.rlLeftMenuSubMenu);
        rlLeftMenuNormalWithCount = (RelativeLayout) rowView.findViewById(R.id.rlLeftMenuNormalWithCount);

        switch (position) {
            case Constant.LEFT_MENU_ITEM_NEWS:
            case Constant.LEFT_MENU_ITEM_BLOGS:
            case Constant.LEFT_MENU_ITEM_PUBLICATIONS:
                changeVisibility(rlLeftMenuNormal, rlLeftMenuSubMenu, rlLeftMenuNormalWithCount);

                ivLeftMenuNormalImageOfItem = (ImageView) rowView.findViewById(R.id.ivLeftMenuNormalImageOfItem);
                tvLeftMenuNormalTitleOfItem = (TextView) rowView.findViewById(R.id.tvLeftMenuNormalTitleOfItem);
                tvLeftMenuNormalTitleOfItem.setText(menuItems[position]);

                break;
            case Constant.LEFT_MENU_ITEM_REPORTS:
            case Constant.LEFT_MENU_ITEM_NOTES:
            case Constant.LEFT_MENU_ITEM_PRINTED_PUBLICATIONS:
            case Constant.LEFT_MENU_ITEM_FAVORITES:
            case Constant.LEFT_MENU_ITEM_ARCHIVE:
                changeVisibility(rlLeftMenuSubMenu, rlLeftMenuNormalWithCount, rlLeftMenuNormal);

                ivLeftMenuSubMenuImageOfItem = (ImageView) rowView.findViewById(R.id.ivLeftMenuSubMenuImageOfItem);
                tvLeftMenuSubMenuTitleOfItem = (TextView) rowView.findViewById(R.id.tvLeftMenuSubMenuTitleOfItem);
                tvLeftMenuSubMenuTitleOfItem.setText(menuItems[position]);

                break;
            case Constant.LEFT_MENU_ITEM_MY_READ_LIST:
                changeVisibility(rlLeftMenuNormalWithCount, rlLeftMenuNormal, rlLeftMenuSubMenu);

                ivLeftMenuNormalImageOfItemlWithCount = (ImageView) rowView.findViewById(R.id.ivLeftMenuNormalImageOfItemlWithCount);
                tvLeftMenuNormalTitleOfItemlWithCount = (TextView) rowView.findViewById(R.id.tvLeftMenuNormalTitleOfItemlWithCount);
                tvLeftMenuNormalTitleOfItemlWithCount.setText(menuItems[position]);

                tvLeftMenuNormalCountOfItemlWithCount = (TextView) rowView.findViewById(R.id.tvLeftMenuNormalCountOfItemlWithCount);


                break;

        }

        return rowView;
    }

    // first layout will be visible, others will be gone
    void changeVisibility(RelativeLayout... relativeLayouts) {

        for (int i = 0; i < relativeLayouts.length; i++) {

            if (i == 0)
                relativeLayouts[i].setVisibility(RelativeLayout.VISIBLE);
            else
                relativeLayouts[i].setVisibility(RelativeLayout.GONE);
        }
    }
}
