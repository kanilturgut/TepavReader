package com.tepav.reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.*;
import com.tepav.reader.R;
import com.tepav.reader.fragment.BlogFragment;
import com.tepav.reader.fragment.NewsFragment;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.slidingmenu.SlidingMenu;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 14:08
 */
public class MainActivity extends FragmentActivity {

    Context context;

    SlidingMenu slidingMenu;
    ImageButton btMenu;
    ListView lvLeftMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        slidingMenu = (SlidingMenu) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(slidingMenu);

        lvLeftMenu = (ListView) findViewById(R.id.lvLeftMenu);
        lvLeftMenu.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_items)));
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMenuItemClick(parent, view, position, id);
            }

        });

        btMenu = (ImageButton) findViewById(R.id.button_menu);
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show/hide the menu
                slidingMenu.toggleMenu();
            }
        });

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        NewsFragment fragment = new NewsFragment();

        ft.add(R.id.activity_main_content_fragment, fragment);
        ft.commit();
    }

    private void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        switch (position) {
            case Constant.LEFT_MENU_ITEM_NEWS:
                fragment = new NewsFragment();
                break;
            case Constant.LEFT_MENU_ITEM_BLOGS:
                fragment = new BlogFragment();
                break;
            case Constant.LEFT_MENU_ITEM_PUBLICATIONS:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_REPORTS:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_NOTES:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_PRINTED_PUBLICATIONS:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_MY_READ_LIST:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_FAVORITES:
                notImplemented();
                break;
            case Constant.LEFT_MENU_ITEM_ARCHIVE:
                notImplemented();
                break;

        }


        if (fragment != null) {
            ft.replace(R.id.activity_main_content_fragment, fragment);
            ft.commit();
        }
        slidingMenu.toggleMenu();
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShown()) {
            slidingMenu.toggleMenu();
        } else {
            super.onBackPressed();
        }
    }

    void notImplemented() {
        Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show();
    }

}