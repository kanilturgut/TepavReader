package com.tepav.reader.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.adapter.LeftMenuAdapter;
import com.tepav.reader.fragment.*;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.slidingmenu.SlidingMenu;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.ConnectionDetector;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 14:08
 */
public class MainActivity extends FragmentActivity {

    Context context;

    SlidingMenu slidingMenu;
    RelativeLayout btMenu;
    ListView lvLeftMenu;
    TextView tvLeftMenuHeader, tvActionBarHeader;
    ConnectionDetector connectionDetector;

    public static FragmentManager fm = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        slidingMenu = (SlidingMenu) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(slidingMenu);

        connectionDetector = ConnectionDetector.getInstance(context);

        tvLeftMenuHeader = (TextView) findViewById(R.id.tvLeftMenuHeader);
        tvLeftMenuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(context, Login.class));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Yeni bir içerik mevcut");

                Intent resultIntent = new Intent(context, Splash.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(Splash.class);
                stackBuilder.addNextIntent(resultIntent);

                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = { 0, 100, 600, 100, 700};
                vibrator.vibrate(pattern, -1);

                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        });

        lvLeftMenu = (ListView) findViewById(R.id.lvLeftMenu);
        btMenu = (RelativeLayout) findViewById(R.id.button_menu);
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lvLeftMenu.setAdapter(new LeftMenuAdapter(context, getResources().getStringArray(R.array.menu_items)));
                lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        onMenuItemClick(parent, view, position, id);
                    }

                });


                // Show/hide the menu
                slidingMenu.toggleMenu();
            }
        });

        fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        ft.add(R.id.activity_main_content_fragment, fragment, getString(R.string.Home));
        ft.commit();

        tvActionBarHeader = (TextView) findViewById(R.id.tvActionBarHeader);
        tvActionBarHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fm.beginTransaction();
                HomeFragment fragment = new HomeFragment();
                ft.add(R.id.activity_main_content_fragment, fragment, getString(R.string.Home));
                ft.commit();
            }
        });
    }

    private void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {

        String fragmentTag = "";

        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        if (connectionDetector.isConnectingToInternet()) {

            switch (position) {
                case Constant.LEFT_MENU_ITEM_NEWS:
                    fragmentTag = getString(R.string.News);
                    fragment = new NewsFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_BLOGS:
                    fragmentTag = getString(R.string.Blogs);
                    fragment = new BlogFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_RESEARCH_AND_PUBLICATIONS:
                    fragmentTag = getString(R.string.Research_And_Publications);
                    fragment = new PublicationFragment(fragmentTag);
                    break;
                case Constant.LEFT_MENU_ITEM_NOTES:
                    fragmentTag = getString(R.string.Notes);
                    fragment = new PublicationFragment(fragmentTag);
                    break;
                case Constant.LEFT_MENU_ITEM_REPORTS_AND_PRINTED_PUBLICATIONS:
                    fragmentTag = getString(R.string.Reports_And_Printed_Publications);
                    fragment = new PublicationFragment(fragmentTag);
                    break;
                case Constant.LEFT_MENU_ITEM_MY_READ_LIST:
                    fragmentTag = getString(R.string.Read_List);
                    fragment = new ReadListFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_FAVORITES:
                    fragmentTag = getString(R.string.Favorites);
                    fragment = new FavoriteFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_ARCHIVE:
                    fragmentTag = getString(R.string.Readed_Documents);
                    fragment = new ArchiveFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_SETTINGS:
                    if (Splash.isUserLoggedIn) {
                        fragmentTag = getString(R.string.Settings);
                        fragment = new SettingsFragment();
                     } else {
                        AlertDialogManager alertDialogManager = new AlertDialogManager();
                        alertDialogManager.showLoginDialog(context, getString(R.string.warning), getString(R.string.must_log_in), false);
                    }
                    break;
            }
        } else {
            //only show offline stuff

            switch (position) {
                case Constant.LEFT_MENU_ITEM_MY_READ_LIST:
                    fragmentTag = getString(R.string.Read_List);
                    fragment = new ReadListFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_FAVORITES:
                    fragmentTag = getString(R.string.Favorites);
                    fragment = new FavoriteFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_ARCHIVE:
                    fragmentTag = getString(R.string.Readed_Documents);
                    fragment = new ArchiveFragment();
                    break;
                case Constant.LEFT_MENU_ITEM_SETTINGS:
                    fragmentTag = getString(R.string.Settings);
                    fragment = new SettingsFragment();
                    break;
                default:
                    fragmentTag = getString(R.string.No_Internet_Fragment);
                    fragment = new NoInternetConnectionFragment();
                    break;
            }
        }


        if (fragment != null) {
            Fragment myFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

            if (myFragment == null || !myFragment.isVisible()) {
                ft.replace(R.id.activity_main_content_fragment, fragment, fragmentTag);
                ft.commit();
            }
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