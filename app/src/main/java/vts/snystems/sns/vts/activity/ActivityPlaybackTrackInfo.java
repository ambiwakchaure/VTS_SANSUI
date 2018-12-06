package vts.snystems.sns.vts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.fragments.InfoFragment;
import vts.snystems.sns.vts.fragments.PlayBacksFragment;
import vts.snystems.sns.vts.fragments.TrackFragment;
import vts.snystems.sns.vts.interfaces.Constants;


public class ActivityPlaybackTrackInfo extends AppCompatActivity {


    FragmentManager fm;


    @BindView(R.id.monitorImageView)
    ImageView monitorImageView;

    @BindView(R.id.statusImageView)
    ImageView statusImageView;

    @BindView(R.id.accountImageView)
    ImageView accountImageView;

    @BindView(R.id.monitorLinearLayout)
    LinearLayout monitorLinearLayout;

    @BindView(R.id.statusLinearLayout)
    LinearLayout statusLinearLayout;

    @BindView(R.id.profileLinearLayout)
    LinearLayout profileLinearLayout;

    @BindView(R.id.vNumber)
    TextView vNumber;

    @BindView(R.id.lastDateTime)
    TextView lastDateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_device_details);
        ButterKnife.bind(this);


        getPreviousData();
        fm = getSupportFragmentManager();

        String FRG_FLAG = MyApplication.prefs.getString(Constants.FRG_FLAG,"first");

        if(FRG_FLAG.equals("first"))
        {
            callDefaultFragment(FRG_FLAG);
        }
        else
        {
            callDefaultFragment(FRG_FLAG);
        }
    }
    private void getPreviousData()
    {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            String IMEI = bundle.getString(Constants.IMEI);
            String VEHICLE_NUMBER = bundle.getString(Constants.VEHICLE_NUMBER);
            String VEHICLE_NAME = bundle.getString(Constants.VEHICLE_NAME);
            String VEHICLE_OVER_SPEED = bundle.getString(Constants.VEHICLE_OVER_SPEED);
            String SPEED = bundle.getString(Constants.SPEED);
            String LAST_UPDATE_DATE_TIME = bundle.getString(Constants.LAST_UPDATE_DATE_TIME);
            String LATITUDE = bundle.getString(Constants.LATITUDE);
            String LONGITUDE = bundle.getString(Constants.LONGITUDE);
            String ODOMETER = bundle.getString(Constants.ODOMETER);
            String FUEL = bundle.getString(Constants.FUEL);
            String IGN_STATUS = bundle.getString(Constants.IGN_STATUS);
            String POWER_STATUS = bundle.getString(Constants.POWER_STATUS);
            String GPS_STATUS = bundle.getString(Constants.GPS_STATUS);
            String DIST_LAST = bundle.getString(Constants.DIST_LAST);
           // String PARK_LAST = bundle.getString(Constants.PARK_LAST);
          //  String DUR_LAST = bundle.getString(Constants.DUR_LAST);
           // String DIST_TOTAL = bundle.getString(Constants.DIST_TOTAL);
            //String PARK_TOTAL = bundle.getString(Constants.PARK_TOTAL);
            //String DUR_TOTAL = bundle.getString(Constants.DUR_TOTAL);

            String ICON_COLOR = bundle.getString(Constants.ICON_COLOR);
            String VTYPE = bundle.getString(Constants.VTYPE);
            String DEVICE_STATUS = bundle.getString(Constants.DEVICE_STATUS);
            String COURSE = bundle.getString(Constants.COURSE);

            vNumber.setText(VEHICLE_NUMBER);

            String [] data = LAST_UPDATE_DATE_TIME.split(" ");

            if(data.length > 0)
            {
                lastDateTime.setText("Last track : "+F.parseDate(data[0],"Year")+" "+data[1]);
            }


            //set color code status
            //setVehicleStatus(DEVICE_STATUS,vStatusBtn);


            MyApplication.editor.putString(Constants.IMEI,IMEI).commit();
            MyApplication.editor.putString(Constants.VEHICLE_NUMBER,VEHICLE_NUMBER).commit();
            MyApplication.editor.putString(Constants.VEHICLE_NAME,VEHICLE_NAME).commit();
            MyApplication.editor.putString(Constants.VEHICLE_OVER_SPEED,VEHICLE_OVER_SPEED).commit();
            MyApplication.editor.putString(Constants.LATITUDE,LATITUDE).commit();
            MyApplication.editor.putString(Constants.LONGITUDE,LONGITUDE).commit();
            MyApplication.editor.putString(Constants.ODOMETER,ODOMETER).commit();
            MyApplication.editor.putString(Constants.FUEL,FUEL).commit();
            MyApplication.editor.putString(Constants.IGN_STATUS,IGN_STATUS).commit();
            MyApplication.editor.putString(Constants.POWER_STATUS,POWER_STATUS).commit();
            MyApplication.editor.putString(Constants.GPS_STATUS,GPS_STATUS).commit();
            MyApplication.editor.putString(Constants.DIST_LAST,DIST_LAST).commit();
            //MyApplication.editor.putString(Constants.PARK_LAST,PARK_LAST).commit();
            //MyApplication.editor.putString(Constants.DUR_LAST,DUR_LAST).commit();
            //MyApplication.editor.putString(Constants.DIST_TOTAL,DIST_TOTAL).commit();
            //MyApplication.editor.putString(Constants.PARK_TOTAL,PARK_TOTAL).commit();
            //MyApplication.editor.putString(Constants.DUR_TOTAL,DUR_TOTAL).commit();
            MyApplication.editor.putString(Constants.SPEED,SPEED).commit();
            MyApplication.editor.putString(Constants.ICON_COLOR,ICON_COLOR).commit();
            MyApplication.editor.putString(Constants.VTYPE,VTYPE).commit();
            MyApplication.editor.putString(Constants.DEVICE_STATUS,DEVICE_STATUS).commit();
            MyApplication.editor.putString(Constants.LAST_UPDATE_DATE_TIME,LAST_UPDATE_DATE_TIME).commit();
            MyApplication.editor.putString(Constants.COURSE,COURSE).commit();

        }

    }


    private void callDefaultFragment(String status) {

        if(status.equals("first"))
        {
            //cancel refresh timer
            // trackFragment.cancelTimer();
            TrackFragment trackFragment = new TrackFragment();
            trackFragment.newInstannce(ActivityPlaybackTrackInfo.this);
            fm.beginTransaction().replace(R.id.main_contenier, trackFragment).commit();
            monitorImageView.setImageResource(R.drawable.ic_play_arrow);
            statusImageView.setImageResource(R.drawable.ic_details_active);
            accountImageView.setImageResource(R.drawable.ic_info);
        }
        else
        {
            PlayBacksFragment playBackListFragment = new PlayBacksFragment();
            fm.beginTransaction().replace(R.id.main_contenier, playBackListFragment).commit();
            monitorImageView.setImageResource(R.drawable.ic_play_arrow_active);
            statusImageView.setImageResource(R.drawable.ic_details);
            accountImageView.setImageResource(R.drawable.ic_info);
        }

    }
    public void updateTRackDateTime(String dateTime)
    {
        String [] data = dateTime.split(" ");
        if(data.length > 0)
        {
            lastDateTime.setText("Last track : "+F.parseDate(data[0],"Year")+" "+data[1]);
        }

    }

    public void goBack(View view)
    {
        MyApplication.editor.putString(Constants.FRG_FLAG,"first");
        finish();
    }

    @OnClick(R.id.monitorLinearLayout)
    public void onMonitorLinearLayoutClicked()
    {

        //cancel refresh timer
       // trackFragment.cancelTimer();
        TrackFragment trackFragment = new TrackFragment();
        fm.beginTransaction().replace(R.id.main_contenier, trackFragment).commit();
        monitorImageView.setImageResource(R.drawable.ic_play_arrow);
        statusImageView.setImageResource(R.drawable.ic_details_active);
        accountImageView.setImageResource(R.drawable.ic_info);
    }

    @OnClick(R.id.statusLinearLayout)
    public void onStatusLinearLayoutClicked()
    {
        MyApplication.editor.putString(Constants.FRG_FLAG,"second");

        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.profileLinearLayout)
    public void onProfileLinearLayoutClicked() {

        //cancel refresh timer
        //trackFragment.cancelTimer();

        InfoFragment infoFragment = new InfoFragment();
        infoFragment.newInsatance(ActivityPlaybackTrackInfo.this);
        fm.beginTransaction().replace(R.id.main_contenier, infoFragment).commit();
        monitorImageView.setImageResource(R.drawable.ic_play_arrow);
        statusImageView.setImageResource(R.drawable.ic_details);
        accountImageView.setImageResource(R.drawable.ic_info_active);
    }

    public void updateVNumber()
    {
        String v_Number = MyApplication.prefs.getString(Constants.VEHICLE_NUMBER,"0");
        vNumber.setText(v_Number);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //cancel refresh timer
       // trackFragment.cancelTimer();
        MyApplication.editor.putString(Constants.FRG_FLAG,"first");

    }

    //share your vehicle location
    public void shareVehicleLocation(View view)
    {
        String link = "http://maps.google.com/maps?q=loc:"+MyApplication.prefs.getString(Constants.LATITUDE,"0")+","+MyApplication.prefs.getString(Constants.LONGITUDE,"0");
        String subjectBody = "Vehicle Name : "+MyApplication.prefs.getString(Constants.VEHICLE_NAME,"0")+"\r\nVehicle Number : "+MyApplication.prefs.getString(Constants.VEHICLE_NUMBER,"0")+"\r\nUser Name : "+MyApplication.prefs.getString(Constants.USER_NAME,"0")+"\r\nLocation : "+link;

        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, subjectBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Vehicle Location"));

    }
}
