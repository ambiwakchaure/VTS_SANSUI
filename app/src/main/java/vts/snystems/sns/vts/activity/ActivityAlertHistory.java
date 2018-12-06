package vts.snystems.sns.vts.activity;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.fragments.TodayAlertHistroyFragment;
import vts.snystems.sns.vts.fragments.WeekAlertHistroyFragment;
import vts.snystems.sns.vts.fragments.YesterdayAlertHistroyFragment;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;

public class ActivityAlertHistory extends AppCompatActivity
{


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_alert_history);

        ButterKnife.bind(this);
        setListner();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.aHist));
        toolbar.setNavigationIcon(R.drawable.ic_back_24);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS =
            {
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            };
    private void setListner()
    {

        sosFloating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String SOS_FC = MyApplication.prefs.getString(Constants.SOS_FC,"0");
                String SOS_SC = MyApplication.prefs.getString(Constants.SOS_SC,"0");
                String SOS_TC = MyApplication.prefs.getString(Constants.SOS_TC,"0");

                if(SOS_FC.equals("0") && SOS_SC.equals("0") && SOS_TC.equals("0"))
                {
                    F.addSoScontacts(ActivityAlertHistory.this);
                }
                else
                {
                    //get curr lat long and send sms
                    //checkSmsPermission();
                    if(!F.hasPermissions(ActivityAlertHistory.this, PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions(ActivityAlertHistory.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    else
                    {
                        new CurrentLatLng().getCurrentLatLng(ActivityAlertHistory.this);
                    }

                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        String language = MyApplication.prefs.getString(Constants.APP_LANGUAGE,"en");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Configuration config = newBase.getResources().getConfiguration();
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            config.setLocale(locale);
            newBase = newBase.createConfigurationContext(config);
        }
        super.attachBaseContext(newBase);
    }
    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodayAlertHistroyFragment(), getResources().getString(R.string.today));
        adapter.addFragment(new YesterdayAlertHistroyFragment(), getResources().getString(R.string.yesterday));
        adapter.addFragment(new WeekAlertHistroyFragment(), getResources().getString(R.string.week));
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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
}
