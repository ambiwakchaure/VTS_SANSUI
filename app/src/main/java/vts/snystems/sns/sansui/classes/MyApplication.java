package vts.snystems.sns.sansui.classes;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import vts.snystems.sns.sansui.db.DBHelper;
import vts.snystems.sns.sansui.interfaces.Constants;


/**
 * Created by sns003 on 19-Mar-18.
 */

public class MyApplication extends Application
{
    public static Context context;
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;

    public static DBHelper db = null;

    public static RequestQueue requestQueue = null;
    public static RetryPolicy retryPolicy;

    public static ProgressDialog progressDialog;

    public static Handler handler;

    //public static TrackFragment fragmentT;
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();

        //change language of app


        if (db == null)
        {
            db = new DBHelper(context);
            db.getWritableDatabase();
        }

        //fragmentT = new TrackFragment();

        //progressDialog
        progressDialog = new ProgressDialog(context);
        //volley
        if(requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(context);
        }
        //retryPolicy = new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        retryPolicy = new DefaultRetryPolicy(30000, 0, 0);

        //shared preferences
        prefs = getSharedPreferences(Constants.PREF_KEY, 0);
        editor = prefs.edit();
        editor.commit();



        //set app language
        String currentLng = prefs.getString(Constants.APP_LANGUAGE,"0");
        if(currentLng.equals("0"))
        {
            F.setLanguage("en");
        }
        else
        {
            F.setLanguage(currentLng);
        }

    }
}
