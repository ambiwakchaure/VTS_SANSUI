package vts.snystems.sns.vts.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.immobiliser.activity.ActivityImmobiliser;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.db.TABLE_NOTIFICATION;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.fragments.AlertFragment;
import vts.snystems.sns.vts.fragments.ListViewFragment;
import vts.snystems.sns.vts.fragments.SettingFragment;
import vts.snystems.sns.vts.fragments.VehicleTrackingFragment;
import vts.snystems.sns.vts.fragments.monitor.MonitorFragmentCluster;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.services.GetNotifications;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.drawer_layout)
    DrawerLayout drawerll;

    @BindView(R.id.menuLeft)
    ImageView menuLeft;

    @BindView(R.id.listView)
    LinearLayout listView;

    @BindView(R.id.mapView)
    LinearLayout mapView;

    @BindView(R.id.monitorImageView)
    ImageView monitorImageView;

    @BindView(R.id.statusImageView)
    ImageView statusImageView;

    @BindView(R.id.notificationImageView)
    ImageView notificationImageView;

    @BindView(R.id.accountImageView)
    ImageView accountImageView;


    FragmentManager fm;

    @BindView(R.id.monitorLinearLayout)
    LinearLayout monitorLinearLayout;

    @BindView(R.id.statusLinearLayout)
    LinearLayout statusLinearLayout;

    @BindView(R.id.notificationLinearLayout)
    LinearLayout notificationLinearLayout;

    @BindView(R.id.profileLinearLayout)
    LinearLayout profileLinearLayout;

    @BindView(R.id.vehicleListText)
    TextView vehicleListText;

    @BindView(R.id.toggleLayout)
    LinearLayout toggleLayout;

    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;

    @BindView(R.id.notification_count)
    TextView notification_count;


    @BindView(R.id.profileImage)
    CircleImageView profileImage;

    private MyReceiver myReceiver;

    //TextView textView;
    NavigationView navigationView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshContent("first");
    }

    private void refreshContent(String status) {

        setContentView(R.layout.activity_home);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        callDefultFragment(status);
        //set profile image if exists
        F.setImage(profileImage);

        getNotiCount();
        setClickListner();




        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GetNotifications.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

//        if (!isMyServiceRunning(GetNotifications.class)) {
            MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();


            Intent i = new Intent(HomeActivity.this, GetNotifications.class);
            startService(i);
//        }

        navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        navigationView1.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView1.getHeaderView(0);
        LinearLayout dashboardMenu = headerLayout.findViewById(R.id.dashboardMenu);
        LinearLayout alertHistoryMenu = headerLayout.findViewById(R.id.alertHistoryMenu);
        LinearLayout aboutUsMenu = headerLayout.findViewById(R.id.aboutUsMenu);
        LinearLayout faqMenu = headerLayout.findViewById(R.id.faqMenu);
        LinearLayout logoutMenu = headerLayout.findViewById(R.id.logoutMenu);
        LinearLayout vehicleSummaryMenu = headerLayout.findViewById(R.id.vehicleSummaryMenu);
        LinearLayout distanceSumMenu = headerLayout.findViewById(R.id.distanceSumMenu);
        LinearLayout mapViewMenu = headerLayout.findViewById(R.id.mapViewMenu);
        LinearLayout vehicleStatuswMenu = headerLayout.findViewById(R.id.vehicleStatuswMenu);
        LinearLayout immobilseMenu = headerLayout.findViewById(R.id.immobilseMenu);

        immobilseMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                drawerll.closeDrawer(GravityCompat.START);
                startActivity(new Intent(HomeActivity.this, ActivityImmobiliser.class));


            }
        });

        mapViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                listView.setBackgroundResource(R.drawable.round_button_new);
                mapView.setBackgroundResource(R.drawable.round_button);
                callMapFragment("replace");


            }
        });
        vehicleStatuswMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                callVStatusFrg("replace");

            }
        });

        distanceSumMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                startActivity(new Intent(HomeActivity.this, ActivityDistanceSummary.class));


            }
        });

        dashboardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                callDefultFragment("second");


            }
        });
        alertHistoryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);

                startActivity(new Intent(HomeActivity.this, ActivityAlertHistory.class));


            }
        });
        vehicleSummaryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);

                startActivity(new Intent(HomeActivity.this, ActivityTravelSummary.class));


            }
        });
        aboutUsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                startActivity(new Intent(HomeActivity.this, ActivityAbout.class));


            }
        });
        faqMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                startActivity(new Intent(HomeActivity.this, ActivityFAQ.class));

            }
        });
        logoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerll.closeDrawer(GravityCompat.START);
                MaterialDialog  md = new MaterialDialog.Builder(HomeActivity.this)
                        .title(getResources().getString(R.string.logout))
                        .content(getResources().getString(R.string.logout_msg))
                        .positiveText(getResources().getString(R.string.logout))
                        .negativeText(getResources().getString(R.string.cancel))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                                MyApplication.editor.clear().commit();
                                //clear all notification data
                                TABLE_NOTIFICATION.deleteDateWise();
                                Intent i = new Intent(HomeActivity.this, ActivityLogin.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
                Typeface tf = Typeface.createFromAsset(HomeActivity.this.getAssets(), "TitilliumWeb-Regular.ttf");
                md.getTitleView().setTypeface(tf);
                md.getContentView().setTypeface(tf);
                md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
                md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
                md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
                md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);

            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        String language = MyApplication.prefs.getString(Constants.APP_LANGUAGE, "en");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Configuration config = newBase.getResources().getConfiguration();
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            config.setLocale(locale);
            newBase = newBase.createConfigurationContext(config);
        }
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // SHIFT_NAMES.clear();
        getNotiCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getNotiCount() {


        ArrayList<String> notiCount = TABLE_NOTIFICATION.getNoticount();
        M.e("NOTIFICATION", "notiCount : " + notiCount);


        if (!notiCount.isEmpty()) {

            notification_count.setVisibility(View.VISIBLE);
            notification_count.setText(notiCount.get(0));

        }
    }

    private void callDefultFragment(String status) {
//


        if (status.equals("first")) {
            //check dash preference
            String dashPref = MyApplication.prefs.getString(Constants.DASH_PREF, "0");

            if (dashPref.equals("0")) {

                listView.setBackgroundResource(R.drawable.round_button);
                mapView.setBackgroundResource(R.drawable.round_button_new);
                MyApplication.editor.putString(Constants.DASH_PREF, "list").commit();
                callListFragment("add");
            }
            if (dashPref.equals("map")) {

                listView.setBackgroundResource(R.drawable.round_button_new);
                mapView.setBackgroundResource(R.drawable.round_button);
                callMapFragment("add");

            } else if (dashPref.equals("list")) {
                listView.setBackgroundResource(R.drawable.round_button);
                mapView.setBackgroundResource(R.drawable.round_button_new);
                callListFragment("add");

            } else if (dashPref.equals("vStatus")) {

                callVStatusFrg("add");

            } else {
                listView.setBackgroundResource(R.drawable.round_button);
                mapView.setBackgroundResource(R.drawable.round_button_new);
                callListFragment("add");
            }


        } else if (status.equals("second")) {
            listView.setBackgroundResource(R.drawable.round_button);
            mapView.setBackgroundResource(R.drawable.round_button_new);
            callListFragment("replace");
        } else if (status.equals("third")) {
            toggleLayout.setVisibility(View.GONE);
            vehicleListText.setVisibility(View.VISIBLE);
            vehicleListText.setText(getResources().getString(R.string.setting));
            //M.t("Vehicle List");
            FragmentManager fm = getSupportFragmentManager();

            SettingFragment settingFragment = new SettingFragment();
            SettingFragment.newInstance(HomeActivity.this);
            fm.beginTransaction().replace(R.id.main_contenier, settingFragment).commit();


            monitorImageView.setImageResource(R.drawable.ic_monitor_24);
            //   monitorTextView.setTextColor(Color.parseColor("#03A9F4"));

            statusImageView.setImageResource(R.drawable.ic_device_list_24);
            //   statusTextView.setTextColor(Color.parseColor("#616161"));

            notificationImageView.setImageResource(R.drawable.ic_settings_on);
            //   notificationTextView.setTextColor(Color.parseColor("#616161"));

            accountImageView.setImageResource(R.drawable.ic_notification_24);
            //    profileTextView.setTextColor(Color.parseColor("#616161"));


        }
    }

    private void callListFragment(String frgStatus) {


        toggleLayout.setVisibility(View.VISIBLE);
        vehicleListText.setVisibility(View.GONE);

        FragmentManager fm = getSupportFragmentManager();


        ListViewFragment listViewFragment = new ListViewFragment();

        if (frgStatus.equals("replace")) {
            MyApplication.editor.putString(Constants.FRG_STATUS, "All").commit();
            fm.beginTransaction().replace(R.id.main_contenier, listViewFragment).commit();
        } else if (frgStatus.equals("add")) {
            MyApplication.editor.putString(Constants.FRG_STATUS, "All").commit();
            fm.beginTransaction().add(R.id.main_contenier, listViewFragment).commit();
        }

        monitorImageView.setImageResource(R.drawable.ic_monitor_24_active);
        //   monitorTextView.setTextColor(Color.parseColor("#03A9F4"));

        statusImageView.setImageResource(R.drawable.ic_device_list_24);
        //   statusTextView.setTextColor(Color.parseColor("#616161"));

        notificationImageView.setImageResource(R.drawable.ic_settings_off);
        //   notificationTextView.setTextColor(Color.parseColor("#616161"));

        accountImageView.setImageResource(R.drawable.ic_notification_24);
        //    profileTextView.setTextColor(Color.parseColor("#616161"));
    }

    private void callMapFragment(String frgStatus) {


        toggleLayout.setVisibility(View.VISIBLE);
        vehicleListText.setVisibility(View.GONE);

        FragmentManager fm = getSupportFragmentManager();

        //listViewFragment.cancelTimer();
        //monitorFragment.cancelTimer();

        //MonitorFragment monitorFragment = new MonitorFragment();
        MonitorFragmentCluster monitorFragment = new MonitorFragmentCluster();
        if (frgStatus.equals("replace")) {
            fm.beginTransaction().replace(R.id.main_contenier, monitorFragment).commit();
        } else if (frgStatus.equals("add")) {
            fm.beginTransaction().add(R.id.main_contenier, monitorFragment).commit();
        }


        monitorImageView.setImageResource(R.drawable.ic_monitor_24_active);
        //   monitorTextView.setTextColor(Color.parseColor("#03A9F4"));
        statusImageView.setImageResource(R.drawable.ic_device_list_24);
        //   statusTextView.setTextColor(Color.parseColor("#616161"));
        notificationImageView.setImageResource(R.drawable.ic_settings_off);
        //   notificationTextView.setTextColor(Color.parseColor("#616161"));
        accountImageView.setImageResource(R.drawable.ic_notification_24);
        //    profileTextView.setTextColor(Color.parseColor("#616161"));
    }

    private void setClickListner() {

        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawerll.openDrawer(GravityCompat.START);
                if (drawerll.isDrawerOpen(GravityCompat.START)) {
                    //Log.e("DRAWER", "one");
                    drawerll.closeDrawer(GravityCompat.START);
                } else {
                    //Log.e("DRAWER", "two");
                    drawerll.openDrawer(GravityCompat.START);
                }
            }
        });
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setBackgroundResource(R.drawable.round_button);
                mapView.setBackgroundResource(R.drawable.round_button_new);

                callDefultFragment("second");
            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setBackgroundResource(R.drawable.round_button_new);
                mapView.setBackgroundResource(R.drawable.round_button);
                callMapFragment("replace");
            }
        });

    }

    @Override
    public void onBackPressed()
    {

        MaterialDialog dg = new MaterialDialog.Builder(HomeActivity.this)
                .title(getString(R.string.app_name))
                .content(R.string.c_app)
                .positiveText(R.string.ex)
                .negativeText(R.string.cancel_l)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        closeApp();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                    }

                })
                .show();
        Typeface tf = Typeface.createFromAsset(HomeActivity.this.getAssets(), "TitilliumWeb-Regular.ttf");
        dg.getTitleView().setTypeface(tf);
        dg.getContentView().setTypeface(tf);
        dg.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
        dg.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
        dg.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
        dg.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);

    }

    private void closeApp()
    {

        //clear when password not remember
        String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");
        if (remember.equals("not_remember")) {
            MyApplication.editor.clear().commit();
            MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
            TABLE_NOTIFICATION.deleteDateWise();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
        else super.onBackPressed();

    }

    /*@Override
    public void onBackPressed()
    {

        new MaterialDialog.Builder(HomeActivity.this)
                .title(getString(R.string.app_name))
                .content(R.string.c_app)
                .positiveText(R.string.ex)
                .negativeText(R.string.cancel_l)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        //clear when password not remember
                        String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");
                        if (remember.equals("not_remember")) {
                            MyApplication.editor.clear().commit();
                            MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                            TABLE_NOTIFICATION.deleteDateWise();
                        }

                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
                            drawer.closeDrawer(GravityCompat.END);
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                    }

                })
                .show();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menue_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        return true;
    }

    @OnClick(R.id.monitorLinearLayout)
    public void onMonitorLinearLayoutClicked() {

        listView.setBackgroundResource(R.drawable.round_button);
        mapView.setBackgroundResource(R.drawable.round_button_new);
        callListFragment("replace");
        /*String dashPref = MyApplication.prefs.getString(Constants.DASH_PREF, "0");
        if(dashPref.equals("map"))
        {

            listView.setBackgroundResource(R.drawable.round_button_new);
            mapView.setBackgroundResource(R.drawable.round_button);
            callMapFragment("replace");

        }
        else if(dashPref.equals("list"))
        {
            listView.setBackgroundResource(R.drawable.round_button);
            mapView.setBackgroundResource(R.drawable.round_button_new);
            callListFragment("replace");

        }
        else if(dashPref.equals("vStatus"))
        {
            callVStatusFrg("replace");

        }*/
    }

    @OnClick(R.id.statusLinearLayout)
    public void onStatusLinearLayoutClicked() {
        callVStatusFrg("replace");
    }

    public void callVStatusFrg(String status) {
        toggleLayout.setVisibility(View.GONE);
        vehicleListText.setVisibility(View.VISIBLE);
        vehicleListText.setText(R.string.vehicle_status);

        //M.t("Vehicle List");
        FragmentManager fm = getSupportFragmentManager();

        MyApplication.editor.putString(Constants.FRG_STATUS, "All").commit();

        VehicleTrackingFragment vehicleTrackingFragment = new VehicleTrackingFragment();
        // listViewFragment.cancelTimer();
        //  vehicleTrackingFragment.cancelHandler();
        if (status.equals("add")) {
            fm.beginTransaction().add(R.id.main_contenier, vehicleTrackingFragment).commit();
        } else {
            fm.beginTransaction().replace(R.id.main_contenier, vehicleTrackingFragment).commit();
        }
        monitorImageView.setImageResource(R.drawable.ic_monitor_24);
        statusImageView.setImageResource(R.drawable.ic_device_list_24_active);
        notificationImageView.setImageResource(R.drawable.ic_settings_off);
        accountImageView.setImageResource(R.drawable.ic_notification_24);
        //profileTextView.setTextColor(Color.parseColor("#616161"));
    }

    @OnClick(R.id.notificationLinearLayout)
    public void onNotificationLinearLayoutClicked() {
        refreshActivity();
    }

    public void refreshActivity() {
        refreshContent("third");
    }

    @OnClick(R.id.profileLinearLayout)
    public void onProfileLinearLayoutClicked() {


        toggleLayout.setVisibility(View.GONE);
        vehicleListText.setVisibility(View.VISIBLE);
        vehicleListText.setText(getResources().getString(R.string.alerts));


        FragmentManager fm = getSupportFragmentManager();
        AlertFragment alertFragment = new AlertFragment();

        fm.beginTransaction().replace(R.id.main_contenier, alertFragment).commit();

        monitorImageView.setImageResource(R.drawable.ic_monitor_24);
        //   monitorTextView.setTextColor(Color.parseColor("#03A9F4"));

        statusImageView.setImageResource(R.drawable.ic_device_list_24);
        //   statusTextView.setTextColor(Color.parseColor("#616161"));

        notificationImageView.setImageResource(R.drawable.ic_settings_off);
        //   notificationTextView.setTextColor(Color.parseColor("#616161"));

        accountImageView.setImageResource(R.drawable.ic_notification_24_active);
        //    profileTextView.setTextColor(Color.parseColor("#616161"));

        notification_count.setVisibility(View.GONE);
        //update seen status
        String stastsExists = TABLE_NOTIFICATION.checkNotiExistss();
        if (stastsExists.equals("1")) {

            TABLE_NOTIFICATION.updateSeenStatus();
        }

    }


    @OnClick(R.id.profileLayout)
    public void onViewClicked() {
        //textView.setText("amol");
        Intent i = new Intent(HomeActivity.this, ActivityProfile.class);
        startActivityForResult(i, 3);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String datapassedStatus = arg1.getStringExtra("DATAPASSED");

            if (datapassedStatus.equals("update")) {

                getNotiCount();

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 3) {
            //set profile image if exists
            F.setImage(profileImage);
        }


    }
}
