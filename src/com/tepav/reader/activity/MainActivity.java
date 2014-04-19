package com.tepav.reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import com.tepav.reader.R;
import com.tepav.reader.fragment.BlogFragment;
import com.tepav.reader.fragment.NewsFragment;
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
    ListView lvMenu;
    String[] lvMenuItems = {"Haberler", "Günlük", "Araştırma ve Yayınlar", "Raporlar", "Notlar", "Basılı Yayın", "Okuma Listem", "Favoriler", "Okuduklarım"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        slidingMenu = (SlidingMenu) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(slidingMenu);

        lvMenu = (ListView) findViewById(R.id.menu_listview);
        lvMenu.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, lvMenuItems));
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onMenuItemClick(parent, view, position, id);
            }

        });

        btMenu = (ImageButton) findViewById(R.id.button_menu);
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show/hide the menu
                toggleMenu(v);
            }
        });

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        NewsFragment fragment = new NewsFragment();

        ft.add(R.id.activity_main_content_fragment, fragment);
        ft.commit();
    }

    public void toggleMenu(View v) {
        slidingMenu.toggleMenu();
    }

    private void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {

        String selectedItem = lvMenuItems[position];


        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        if (selectedItem.compareTo(lvMenuItems[0]) == 0) {
            fragment = new NewsFragment();
        } else if (selectedItem.compareTo(lvMenuItems[1]) == 0) {
            fragment = new BlogFragment();
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
}