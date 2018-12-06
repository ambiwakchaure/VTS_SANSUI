package vts.snystems.sns.vts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.db.TABLE_NOTIFICATION;
import vts.snystems.sns.vts.interfaces.Constants;


public class ActivitySplash extends AppCompatActivity {

    @BindView(R.id.textViewVersionDetails)
    TextView textViewVersionDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        textViewVersionDetails.setText("v"+F.returnVersionName());
        MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();

        //delete alerts before last three days
        String stastsExists = TABLE_NOTIFICATION.checkNotiExistss();
        if (stastsExists.equals("1")) {
            String todaysDate = F.getSystemDate();
            String yesterdayDate = F.getDate(-1);
            String thirdDate = F.getDate(-2);
            TABLE_NOTIFICATION.deleteDateWise(todaysDate, yesterdayDate, thirdDate);
        }


        //set IGN and Overspeed bydefault ON
        setNotiBydefaultOn();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication.editor.commit();
                String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");
                MyApplication.editor.commit();

                if (remember.equals("0")) {
                    Intent i = new Intent(ActivitySplash.this, ActivityLogin.class);
                    startActivity(i);
                    finish();
                } else if (remember.equals("not_remember")) {
                    Intent i = new Intent(ActivitySplash.this, ActivityLogin.class);
                    startActivity(i);
                    finish();
                } else if (remember.equals("remember")) {
                    Intent i = new Intent(ActivitySplash.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 2000);
    }

    private void setNotiBydefaultOn()
    {
        String IGNITION_DEFLT = MyApplication.prefs.getString(Constants.IGNITION_DEFLT, "0");
        String OVERSPEED_DEFLT = MyApplication.prefs.getString(Constants.OVERSPEED_DEFLT, "0");

        //ign
        if(IGNITION_DEFLT.equals("0"))
        {
            MyApplication.editor.putString(Constants.IGNITION_C, "on").commit();
            MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
        }
        else
        {
            MyApplication.editor.putString(Constants.IGNITION_C, "off").commit();
            MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
        }

        //over speed
        if(OVERSPEED_DEFLT.equals("0"))
        {
            MyApplication.editor.putString(Constants.OVERSPEED_C, "on").commit();
            MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
        }
        else
        {
            MyApplication.editor.putString(Constants.OVERSPEED_C, "off").commit();
            MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
            MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
        }



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
}
