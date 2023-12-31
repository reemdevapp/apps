package com.app.youtubers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.youtubers.advertise.AdConfig;
import com.app.youtubers.advertise.AdNetworkHelper;
import com.app.youtubers.data.AppConfig;
import com.app.youtubers.data.GDPR;
import com.app.youtubers.data.RemoteConfig;
import com.app.youtubers.data.ThisApplication;
import com.app.youtubers.fragment.FragmentAbout;
import com.app.youtubers.fragment.FragmentFavorite;
import com.app.youtubers.fragment.FragmentHome;
import com.app.youtubers.fragment.FragmentPlaylist;
import com.app.youtubers.room.AppDatabase;
import com.app.youtubers.room.DAO;
import com.app.youtubers.room.table.EntityInfo;
import com.app.youtubers.utils.OnLoadInfoFinished;
import com.app.youtubers.utils.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ActivityMain extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdapterTabMenu adapter;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private DAO database;
    private ThisApplication global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getDb(this).getDAO();
        global = ThisApplication.getInstance();

        setupToolbar(R.id.toolbar, getString(R.string.app_name));
        initViewPagerAndTab();
        initNavigationMenu();

        prepareAds();

        checkVersion();
    }

    private void initViewPagerAndTab() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new AdapterTabMenu(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), getString(R.string.tab_menu_home));
        adapter.addFragment(new FragmentPlaylist(), getString(R.string.tab_menu_playlist));
        adapter.addFragment(new FragmentFavorite(), getString(R.string.tab_menu_favorite));
        adapter.addFragment(new FragmentAbout(), getString(R.string.tab_menu_about));

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupToolbar(int toolbarId, String title) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        Tools.setSystemBarColor(this);
    }


    private void initNavigationMenu() {
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                showInterstitialAd();
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                onItemNavigationClicked(item.getItemId());
                return true;
            }
        });

        View nav_header = nav_view.getHeaderView(0);
        loadNavigationHeader(nav_header);
    }

    private void onItemNavigationClicked(int id) {
        if (id == R.id.nav_subscribe) {
            Tools.subscribeAction(this);
        } else if (id == R.id.nav_description) {
            EntityInfo info = database.getInfo();
            Tools.descDialog(this, info.getDescription());
        } else if (id == R.id.nav_notification) {
            ActivityNotification.navigate(this);
        } else if (id == R.id.nav_rate) {
            Tools.rateAction(this);
        }
    }

    private void loadNavigationHeader(final View view) {
        final View lyt_loading_drawer = (View) findViewById(R.id.lyt_loading_drawer);
        final TextView tv_failed_drawer = (TextView) findViewById(R.id.tv_failed_drawer);
        final ProgressBar progress_drawer = (ProgressBar) findViewById(R.id.progress_drawer);

        lyt_loading_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isOnRequestInfo()) return;
                tv_failed_drawer.setVisibility(View.GONE);
                progress_drawer.setVisibility(View.VISIBLE);
                global.retryLoadChannelInfo();
            }
        });

        global.setOnLoadInfoFinished(new OnLoadInfoFinished() {
            @Override
            public void onComplete(EntityInfo data) {
                loadNavigationHeader(view);
            }

            @Override
            public void onFailed() {
                tv_failed_drawer.setVisibility(View.VISIBLE);
                progress_drawer.setVisibility(View.GONE);
            }
        });

        final EntityInfo info = database.getInfo();
        view.findViewById(R.id.bt_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isOnRequestInfo()) return;
                if (global.isEverUpdate() && info != null) {
                    loadNavigationHeader(view);
                } else {
                    database.deleteInfo();
                    lyt_loading_drawer.setVisibility(View.VISIBLE);
                    tv_failed_drawer.setVisibility(View.GONE);
                    progress_drawer.setVisibility(View.VISIBLE);
                    global.retryLoadChannelInfo();
                }
            }
        });

        if (info == null) return;

        lyt_loading_drawer.setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tv_title)).setText(info.getTitle());
        String total_video = String.format(getString(R.string.total_video), info.getVideoCount());
        ((TextView) view.findViewById(R.id.tv_total_video)).setText(total_video);

        Glide.with(this).load(info.getBannerUrl())
                .into(((ImageView) view.findViewById(R.id.image_banner)));

        Glide.with(this).load(info.getThumbUrl())
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) view.findViewById(R.id.image_avatar));

        Toast.makeText(this, getString(R.string.info_loaded), Toast.LENGTH_SHORT).show();
    }

    private AdNetworkHelper adNetworkHelper;

    private void prepareAds() {
        adNetworkHelper = new AdNetworkHelper(this);
        adNetworkHelper.showGDPR();
        adNetworkHelper.loadBannerAd(AdConfig.ADS_MAIN_BANNER);
        adNetworkHelper.loadInterstitialAd(AdConfig.ADS_MAIN_INTERSTITIAL);
    }

    public boolean showInterstitialAd() {
        return adNetworkHelper.showInterstitialAd(AdConfig.ADS_MAIN_INTERSTITIAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(ActivityMain.this, ActivitySearch.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private class AdapterTabMenu extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public AdapterTabMenu(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void checkVersion() {
        if (RemoteConfig.app_version != 0 && BuildConfig.VERSION_CODE < RemoteConfig.app_version) {
            showDialogUpdateApp();
        }
    }

    private void showDialogUpdateApp() {
        final boolean force = RemoteConfig.force_update;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_of_out_update);
        builder.setMessage(R.string.app_of_out_update_msg);
        builder.setCancelable(!force);
        builder.setPositiveButton(R.string.UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                Tools.rateAction(ActivityMain.this);
            }
        });
        builder.setNegativeButton(force ? R.string.EXIT_APP : R.string.CLOSE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(force) finish();
            }
        });
        builder.show();
    }

}
