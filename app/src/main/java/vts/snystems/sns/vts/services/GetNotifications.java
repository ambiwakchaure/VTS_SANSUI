package vts.snystems.sns.vts.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.db.TABLE_NOTIFICATION;
import vts.snystems.sns.vts.geofence.classes.CheckGeofence;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class GetNotifications extends Service
{
    int i = 0;
    String currentDateTime="",nextDateTime="",storedDate1="";


    public final static String MY_ACTION = "MY_ACTION";
    Context context;

    /** indicates how to behave if the service is killed */
    int mStartMode = START_STICKY;
    /** interface for clients that bind */
    IBinder mBinder;
    /*indicates whether onRebind should be used */
    boolean mAllowRebind;
    Handler mHandler;
    /** Called when the service is being created. */
    @Override
    public void onCreate()
    {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)
    {

        //compile 'com.jakewharton:butterknife:7.0.1'
        //@Bind(R.id.textView1) TextView title;
        try
        {
            context = this;
            mHandler = new Handler(Looper.getMainLooper());
            //checkNotificationUpdates();
            refreshData();

            //temp stop

        }
        catch (Exception e)
        {
            Log.e("systemTime","systemTime :"+e);
        }
        return mStartMode;
    }
    boolean mIsRunning;
    Runnable mStatusChecker = new Runnable()
    {
        @Override
        public void run()
        {
            if (!mIsRunning)
            {
                return; // stop when told to stop
            }

            if(F.checkConnection())
            {
                Log.e("imei service","Service running...");
                //check geofence entry exit point
                String username = MyApplication.prefs.getString(Constants.USER_NAME,"0");
                Log.e("imei service","username : "+username);
                if(!username.equals("0"))
                {
                    CheckGeofence.checkGeofence(context,username,MY_ACTION);
                }
                currentDateTime = F.getSystemDate();
                if(nextDateTime.length()==0)
                {
                    nextDateTime = F.getSystemNextDate(currentDateTime);
                    storedDate1 = nextDateTime;


                }
                //Log.e("getNotifaction","1// : "+currentDateTime+" // "+nextDateTime+" // "+storedDate1);
                if(currentDateTime.equals(storedDate1))
                {
                    Log.e("getNotifaction","store// : ");
                    nextDateTime = F.getSystemNextDate(currentDateTime);
                    storedDate1 = nextDateTime;
                    MyApplication.editor.putString(Constants.NOTI_ALERT,"off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG,true).commit();
                }

                //Log.e("getNotifaction","2// : "+currentDateTime+" // "+nextDateTime+" // "+storedDate1);

                if(!MyApplication.prefs.getBoolean(Constants.SERVICE_FLAG,false))
                {

                    String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");
                    String settingAlertStatus = MyApplication.prefs.getString(Constants.NOTI_ALERT, "0");


                    M.e("NOTIFICATION","remember: "+remember);
                    M.e("NOTIFICATION","settingAlertStatus: "+settingAlertStatus);

                    if(!remember.equals("0"))
                    {
                        if(settingAlertStatus.equals("on"))
                        {
                            String lowBat = MyApplication.prefs.getString(Constants.LOW_BAT_C,"0");
                            String oSpeed = MyApplication.prefs.getString(Constants.OVERSPEED_C,"0");
                            String sosC = MyApplication.prefs.getString(Constants.SOS_C,"0");
                            String pCut = MyApplication.prefs.getString(Constants.POWER_CUT_C,"0");
                            String ignC = MyApplication.prefs.getString(Constants.IGNITION_C,"0");


                            if(lowBat.equals("on") || oSpeed.equals("on") || sosC.equals("on") || pCut.equals("on") || ignC.equals("on"))
                            {
                                if(F.checkConnection())
                                {
                                    getNotification();
                                }
                            }


                        }

                    }

                }
            }


            mHandler.postDelayed(mStatusChecker, 15000); // 60 sec
        }
    };
    private void refreshData()
    {
        mIsRunning = true;
        mStatusChecker.run();


    }
    public void cancelHandler()
    {
        mIsRunning = false;
        mHandler.removeCallbacks(mStatusChecker);
    }


    /*private void checkNotificationUpdates()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                currentDateTime = F.getSystemDate();
                if(nextDateTime.length()==0)
                {
                    nextDateTime = F.getSystemNextDate(currentDateTime);
                    storedDate1 = nextDateTime;


                }
                //Log.e("getNotifaction","1// : "+currentDateTime+" // "+nextDateTime+" // "+storedDate1);
                if(currentDateTime.equals(storedDate1))
                {
                    Log.e("getNotifaction","store// : ");
                    nextDateTime = F.getSystemNextDate(currentDateTime);
                    storedDate1 = nextDateTime;
                    MyApplication.editor.putString(Constants.NOTI_ALERT,"off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG,true).commit();
                }

                //Log.e("getNotifaction","2// : "+currentDateTime+" // "+nextDateTime+" // "+storedDate1);

                if(!MyApplication.prefs.getBoolean(Constants.SERVICE_FLAG,false))
                {

                    String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");
                    String settingAlertStatus = MyApplication.prefs.getString(Constants.NOTI_ALERT, "0");


                    M.e("NOTIFICATION","remember: "+remember);
                    M.e("NOTIFICATION","settingAlertStatus: "+settingAlertStatus);

                    if(!remember.equals("0"))
                    {
                        if(settingAlertStatus.equals("on"))
                        {
                            String lowBat = MyApplication.prefs.getString(Constants.LOW_BAT_C,"0");
                            String oSpeed = MyApplication.prefs.getString(Constants.OVERSPEED_C,"0");
                            String sosC = MyApplication.prefs.getString(Constants.SOS_C,"0");
                            String pCut = MyApplication.prefs.getString(Constants.POWER_CUT_C,"0");
                            String ignC = MyApplication.prefs.getString(Constants.IGNITION_C,"0");


                            if(lowBat.equals("on") || oSpeed.equals("on") || sosC.equals("on") || pCut.equals("on") || ignC.equals("on"))
                            {
                                if(F.checkConnection())
                                {
                                    getNotification();
                                }
                            }


                        }

                    }

                }
                //Do 30 sec
                handler.postDelayed(this, 15000);
            }
        }, 15000);


    }*/

    public void getNotification()
    {
        String userName = MyApplication.prefs.getString(Constants.USER_NAME,"0");
        try
        {
            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName
                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onSuccess(String result)
                        {

                            parseResponse(result);

                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {



                        }
                    },

                    Constants.webUrl + "" + Constants.getVehicleNotification,
                    parameters,
                    context, "second");



        } catch (Exception e) {

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void parseResponse(String loginJson)
    {

        String vehicle_number = Constants.NA;
        String created_date = Constants.LTDATE_TIME;
        String type = Constants.NA;
        String priority = Constants.NA;
        String code = Constants.NA;

        try
        {

            if (loginJson != null || loginJson.length() > 0)
            {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject)
                {

                    JSONObject loginJsonObject1 = new JSONObject(loginJson);

                    String success = loginJsonObject1.getString("status");
                    String message = loginJsonObject1.getString("message");

                    if (success.equals("1"))
                    {

                        JSONArray jsonArray = loginJsonObject1.getJSONArray("notification");

                        String all = MyApplication.prefs.getString(Constants.ALL_C,"0");
                        String lowBat = MyApplication.prefs.getString(Constants.LOW_BAT_C,"0");
                        String oSpeed = MyApplication.prefs.getString(Constants.OVERSPEED_C,"0");
                        String sosC = MyApplication.prefs.getString(Constants.SOS_C,"0");
                        String pCut = MyApplication.prefs.getString(Constants.POWER_CUT_C,"0");
                        String ignC = MyApplication.prefs.getString(Constants.IGNITION_C,"0");

                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                            if(loginJsonObject.has("vehicle_number") && !loginJsonObject.isNull("vehicle_number"))
                            {
                                vehicle_number = loginJsonObject.getString("vehicle_number");
                            }

                            if(loginJsonObject.has("created_date") && !loginJsonObject.isNull("created_date"))
                            {
                                created_date = loginJsonObject.getString("created_date");
                            }

                            if(loginJsonObject.has("type") && !loginJsonObject.isNull("type"))
                            {
                                type = loginJsonObject.getString("type");
                            }

                            if(loginJsonObject.has("priority") && !loginJsonObject.isNull("priority"))
                            {
                                priority = loginJsonObject.getString("priority");
                            }
                            if(loginJsonObject.has("code") && !loginJsonObject.isNull("code"))
                            {
                                code = loginJsonObject.getString("code");
                            }

                            /*if(code.equals("osa"))
                            {
                                if(loginJsonObject.has("code") && !loginJsonObject.isNull("code"))
                                {
                                    code = loginJsonObject.getString("code");
                                }
                            }*/

                            String remember = MyApplication.prefs.getString(Constants.REMEMBER_PASSWORD, "0");

                            if(!remember.equals("0"))
                            {

                                    try
                                    {

                                        String [] dataData = created_date.split(" ");

                                        if(dataData.length == 2)
                                        {
                                            String created_date_check = dataData[0];
                                            //low bateryy
                                            if(lowBat.equals("on"))
                                            {

                                                if(code.equals("lba"))
                                                {
                                                    //check already data
                                                    String statuss = TABLE_NOTIFICATION.checkAlreadyNotificationn(vehicle_number,
                                                            created_date_check,
                                                            type,
                                                            priority);

                                                    if(statuss.equals("0"))
                                                    {
                                                        F.genNotification(
                                                                context,
                                                                i,
                                                                vehicle_number,
                                                                type,
                                                                created_date,
                                                                priority,
                                                                MY_ACTION);
                                                    }

                                                }


                                            }
                                            //oSpeed
                                            if(oSpeed.equals("on"))
                                            {

                                                if(code.equals("osa"))
                                                {
                                                    //check already data
                                                    String statuss = TABLE_NOTIFICATION.checkAlreadyNotificationn(vehicle_number,
                                                            created_date_check,
                                                            type,
                                                            priority);

                                                    if(statuss.equals("0"))
                                                    {
                                                        F.genNotification(
                                                                context,
                                                                i,
                                                                vehicle_number,
                                                                type,
                                                                created_date,
                                                                priority,
                                                                MY_ACTION);
                                                    }

                                                }


                                            }
                                            //sos
                                            if(sosC.equals("on"))
                                            {

                                                if(code.equals("sos"))
                                                {
                                                    //check already data
                                                    String statuss = TABLE_NOTIFICATION.checkAlreadyNotificationn(vehicle_number,
                                                            created_date_check,
                                                            type,
                                                            priority);

                                                    if(statuss.equals("0"))
                                                    {
                                                        F.genNotification(
                                                                context,
                                                                i,
                                                                vehicle_number,
                                                                type,
                                                                created_date,
                                                                priority,
                                                                MY_ACTION);
                                                    }

                                                }


                                            }
                                            //pCut
                                            if(pCut.equals("on"))
                                            {
                                                if(code.equals("pca"))
                                                {
                                                    //check already data
                                                    String statuss = TABLE_NOTIFICATION.checkAlreadyNotificationn(vehicle_number,
                                                            created_date_check,
                                                            type,
                                                            priority);

                                                    if(statuss.equals("0"))
                                                    {
                                                        F.genNotification(
                                                                context,
                                                                i,
                                                                vehicle_number,
                                                                type,
                                                                created_date,
                                                                priority,
                                                                MY_ACTION);
                                                    }

                                                }

                                            }
                                            //ignC
                                            if(ignC.equals("on"))
                                            {
                                                if(code.equals("ign"))
                                                {
                                                    //check already data
                                                    String statuss = TABLE_NOTIFICATION.checkAlreadyNotificationnIGN(vehicle_number,
                                                            created_date_check,
                                                            type,
                                                            priority);

                                                    if(statuss.equals("0"))
                                                    {
                                                        F.genNotification(
                                                                context,
                                                                i,
                                                                vehicle_number,
                                                                type,
                                                                created_date,
                                                                priority,
                                                                MY_ACTION);
                                                    }


                                                }
                                            }
                                        }


                                    }
                                    catch (Exception e)
                                    {

                                    }



                            }


                        }



                    }
                    else if (success.equals("2"))
                    {
                        //M.t(message);
                    }
                    else if (success.equals("3"))
                    {
                       // M.t(message);
                    }
                    else if (success.equals("0"))
                    {
                       // M.t(message);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy()
    {
        cancelHandler();
    }
    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent)
    {

    }


    @Override
    public IBinder onBind(Intent intent)
    {

        return null;
    }


}
