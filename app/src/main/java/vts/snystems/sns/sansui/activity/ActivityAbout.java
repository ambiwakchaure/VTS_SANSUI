package vts.snystems.sns.sansui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Locale;

import vts.snystems.sns.sansui.R;
import vts.snystems.sns.sansui.classes.MyApplication;
import vts.snystems.sns.sansui.errorHandler.ExceptionHandler;
import vts.snystems.sns.sansui.interfaces.Constants;

public class ActivityAbout extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_about);
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

    public void goBack(View view)
    {
        finish();
    }
}
