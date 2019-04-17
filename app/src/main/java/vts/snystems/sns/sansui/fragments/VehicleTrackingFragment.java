package vts.snystems.sns.sansui.fragments;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.sansui.R;
import vts.snystems.sns.sansui.adapter.TrackingAdapter;
import vts.snystems.sns.sansui.classes.F;
import vts.snystems.sns.sansui.classes.MyApplication;
import vts.snystems.sns.sansui.interfaces.Constants;
import vts.snystems.sns.sansui.interfaces.VMsg;
import vts.snystems.sns.sansui.pojo.TrackingInfo;
import vts.snystems.sns.sansui.sos.classes.CurrentLatLng;

public class VehicleTrackingFragment extends Fragment {
    String[] data = {

            "MH14 GL1315",
            "MH14 GL1316",
            "MH14 GL1317",
            "MH14 GL1318",
            "MH14 GL1319",
            "MH14 GL1320",
            "MH14 GL1321",
            "MH14 GL1322"

    };

    private TrackingAdapter vehicleListAdapter;



    private ArrayList<TrackingInfo> VEHICLE_INFO = new ArrayList<>();

    private ArrayList<TrackingInfo> VEHICLE_INFO_RUNNING = new ArrayList<>();
    private ArrayList<TrackingInfo> VEHICLE_INFO_IDLE = new ArrayList<>();
    private ArrayList<TrackingInfo> VEHICLE_INFO_PARKING = new ArrayList<>();
    private ArrayList<TrackingInfo> VEHICLE_INFO_OFFLINE = new ArrayList<>();

    View view;

    @BindView(R.id.vehicleListRecycle)
    RecyclerView vehicleListRecycle;

    @BindView(R.id.serachDataEditText)
    EditText serachDataEditText;

    /*@BindView(R.id.txt_spin_list)
    Spinner txt_spin_list;*/

    @BindView(R.id.txt_error_no_data)
    TextView txt_error_no_data;

    @BindView(R.id.buttonRefresh)
    Button buttonRefresh;

    @BindView(R.id.searchBarRl)
    RelativeLayout searchBarRl;

    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    String vStatus;
    int counterCall = 0;
    Handler mHandler;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;
    String pStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.vehicle_list, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this, view);

        mHandler = new Handler();

        initialize();
        setListner();

        pStatus = "first";
        callDetails();

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(F.checkConnection())
                {
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                    buttonRefresh.setVisibility(View.GONE);
                    getAlldata();
                }
                else
                {
                    txt_error_no_data.setText(VMsg.connection);
                    buttonRefresh.setVisibility(View.GONE);
                }
            }
        });




        return view;

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
                    F.addSoScontacts(getActivity());
                }
                else
                {
                    //get curr lat long and send sms
                    //checkSmsPermission();
                    if(!F.hasPermissions(getActivity(), PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                    }
                    else
                    {
                        new CurrentLatLng().getCurrentLatLng(getActivity());
                    }

                }

            }
        });
    }

    public void callDetails()
    {

        if (F.checkConnection()) {

            String vehicleStatus = MyApplication.prefs.getString(Constants.FRG_STATUS, "0");

            if (vehicleStatus.equals("0")) {
                vStatus = "All";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();

            } else if (vehicleStatus.equals("All")) {
                vStatus = "All";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            } else if (vehicleStatus.equals("Moving")) {
                vStatus = "Moving";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            } else if (vehicleStatus.equals("Idle")) {
                vStatus = "Idle";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            } else if (vehicleStatus.equals("Parking")) {
                vStatus = "Parking";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            } else if (vehicleStatus.equals("Offline")) {
                vStatus = "Offline";
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            }


        } else {

            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.n_conn))
                    .content(getString(R.string.check_wi))
                    .positiveText(getString(R.string.ok))
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                            if (F.checkConnection()) {
                                dialog.dismiss();
                                callDetails();
                            } else {
                                dialog.dismiss();
                            }

                        }
                    })
                    .show();
        }

    }
    String loginJson;
    KProgressHUD progressDialog;
    public void getAlldata()
    {


        progressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);

        if(pStatus.equals("first"))
        {
            progressDialog.show();
        }
        try
        {


            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.webUrl + "" + Constants.getVehicleStatus,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {


                            loginJson = response;
                            new ParseResponse().execute();



                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyErrr)
                        {


                            if(pStatus.equals("first"))
                            {
                                progressDialog.dismiss();
                            }
                            else
                            {
                                progressDialog.dismiss();
                            }

                            if (volleyErrr instanceof TimeoutError)
                            {
                                searchBarRl.setVisibility(View.GONE);
                                vehicleListRecycle.setVisibility(View.GONE);

                                img_no_data.setImageResource(R.drawable.ic_av_timer_black_48dp);
                                txt_error_no_data.setText(R.string.req_t);
                                buttonRefresh.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                img_no_data.setImageResource(R.drawable.ic_av_timer_black_48dp);
                                txt_error_no_data.setText(R.string.req_t);
                                buttonRefresh.setVisibility(View.GONE);
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.devicesDetails,Function : getAlldata(final String vehicleStatus)");
                            }


                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put(Constants.USER_NAME,MyApplication.prefs.getString(Constants.USER_NAME, "0"));
                    params.put(Constants.VEHICLE_STATUS,vStatus);

                    return params;
                }
            };

            MyApplication.requestQueue.getCache().clear();
            stringRequest.setRetryPolicy(MyApplication.retryPolicy);
            MyApplication.requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    class ParseResponse extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String sMessage) {
            super.onPostExecute(sMessage);

            if(pStatus.equals("first"))
            {
                progressDialog.dismiss();
            }
            else
            {
                progressDialog.dismiss();
            }

            try {
                String[] data = sMessage.split("#");

                String success = data[0];
                String message = data[1];


                if (success.equals("1")) {

                    searchBarRl.setVisibility(View.VISIBLE);
                    vehicleListRecycle.setVisibility(View.VISIBLE);

                    vehicleListAdapter.setAllDeviceInfo(VEHICLE_INFO);
                    vehicleListAdapter.notifyDataSetChanged();

                    if (counterCall == 0)
                    {
                        refreshFragment();
                    }
                    counterCall = 1;

                } else if (success.equals("2")) {
                    searchBarRl.setVisibility(View.GONE);
                    vehicleListRecycle.setVisibility(View.GONE);
                } else if (success.equals("3")) {
                    searchBarRl.setVisibility(View.GONE);
                    vehicleListRecycle.setVisibility(View.GONE);
                } else if (success.equals("0")) {
                    searchBarRl.setVisibility(View.GONE);
                    vehicleListRecycle.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... urls) {


            String imei = Constants.NA;
            String vehicleNo = Constants.NA;
            String last_dt = Constants.LTDATE_TIME;
            String latitude = Constants.NA;
            String longitude = Constants.NA;
            String vehicleType = Constants.NA;
            String iconColor = Constants.NA;
            String deviceStatus = Constants.NA;
            String target_name = Constants.NA;
            String success = null, message = null;

            try {

                if (loginJson != null || loginJson.length() > 0) {
                    Object json = new JSONTokener(loginJson).nextValue();
                    if (json instanceof JSONObject) {

                        JSONObject loginJsonObject1 = new JSONObject(loginJson);

                        success = loginJsonObject1.getString("success");
                        //success = "0";
                        message = loginJsonObject1.getString("message");

                        if (success.equals("1"))
                        {

                            if (!VEHICLE_INFO.isEmpty()) {
                                VEHICLE_INFO.clear();
                            }

                            if (!VEHICLE_INFO_RUNNING.isEmpty()) {
                                VEHICLE_INFO_RUNNING.clear();
                            }

                            if (!VEHICLE_INFO_IDLE.isEmpty()) {
                                VEHICLE_INFO_IDLE.clear();
                            }

                            if (!VEHICLE_INFO_PARKING.isEmpty()) {
                                VEHICLE_INFO_PARKING.clear();
                            }

                            if (!VEHICLE_INFO_OFFLINE.isEmpty()) {
                                VEHICLE_INFO_OFFLINE.clear();
                            }

                            //uiUpdate("not");

                            JSONArray jsonArray = loginJsonObject1.getJSONArray("deviceDetails");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                                if (loginJsonObject.has("imei") && !loginJsonObject.isNull("imei")) {
                                    imei = loginJsonObject.getString("imei");
                                }

                                if (loginJsonObject.has("vehicleNo") && !loginJsonObject.isNull("vehicleNo")) {
                                    vehicleNo = loginJsonObject.getString("vehicleNo");
                                }
                                if (loginJsonObject.has("target_name") && !loginJsonObject.isNull("target_name")) {
                                    target_name = loginJsonObject.getString("target_name");
                                }
                                if (loginJsonObject.has("last_dt") && !loginJsonObject.isNull("last_dt")) {
                                    last_dt = loginJsonObject.getString("last_dt");
                                }
                                if (loginJsonObject.has("latitude") && !loginJsonObject.isNull("latitude")) {
                                    latitude = loginJsonObject.getString("latitude");
                                }
                                if (loginJsonObject.has("longitude") && !loginJsonObject.isNull("longitude")) {
                                    longitude = loginJsonObject.getString("longitude");
                                }
                                if (loginJsonObject.has("vehicleType") && !loginJsonObject.isNull("vehicleType")) {
                                    vehicleType = loginJsonObject.getString("vehicleType");
                                }
                                if (loginJsonObject.has("iconColor") && !loginJsonObject.isNull("iconColor")) {
                                    iconColor = loginJsonObject.getString("iconColor");
                                }
                                if (loginJsonObject.has("deviceStatus") && !loginJsonObject.isNull("deviceStatus")) {
                                    deviceStatus = loginJsonObject.getString("deviceStatus");
                                }

                                String address = F.getAddress(Double.valueOf(latitude), Double.valueOf(longitude));
                                //String address = "NA";

                                if(vStatus.equals("All"))
                                {
                                    if(deviceStatus.equals("MV"))
                                    {
                                        TrackingInfo vehicleInfo = new TrackingInfo();

                                        vehicleInfo.setvImei(imei);
                                        vehicleInfo.setvNumber(vehicleNo);
                                        vehicleInfo.setvLastTrackTime(last_dt);
                                        vehicleInfo.setVlat(latitude);
                                        vehicleInfo.setVlng(longitude);
                                        vehicleInfo.setvType(vehicleType);
                                        vehicleInfo.setvColor(iconColor);
                                        vehicleInfo.setvStatus(deviceStatus);
                                        vehicleInfo.setvTargetName(target_name);

                                        if (address.equals("NA")) {
                                            vehicleInfo.setvAddress(Constants.uLocation);
                                        } else {
                                            vehicleInfo.setvAddress(address);
                                        }
                                        VEHICLE_INFO_RUNNING.add(vehicleInfo);
                                    }
                                    else if(deviceStatus.equals("ST"))
                                    {
                                        TrackingInfo vehicleInfo = new TrackingInfo();

                                        vehicleInfo.setvImei(imei);
                                        vehicleInfo.setvNumber(vehicleNo);
                                        vehicleInfo.setvLastTrackTime(last_dt);
                                        vehicleInfo.setVlat(latitude);
                                        vehicleInfo.setVlng(longitude);
                                        vehicleInfo.setvType(vehicleType);
                                        vehicleInfo.setvColor(iconColor);
                                        vehicleInfo.setvStatus(deviceStatus);
                                        vehicleInfo.setvTargetName(target_name);

                                        if (address.equals("NA")) {
                                            vehicleInfo.setvAddress(Constants.uLocation);
                                        } else {
                                            vehicleInfo.setvAddress(address);
                                        }
                                        VEHICLE_INFO_IDLE.add(vehicleInfo);
                                    }
                                    else if(deviceStatus.equals("SP"))
                                    {
                                        TrackingInfo vehicleInfo = new TrackingInfo();

                                        vehicleInfo.setvImei(imei);
                                        vehicleInfo.setvNumber(vehicleNo);
                                        vehicleInfo.setvLastTrackTime(last_dt);
                                        vehicleInfo.setVlat(latitude);
                                        vehicleInfo.setVlng(longitude);
                                        vehicleInfo.setvType(vehicleType);
                                        vehicleInfo.setvColor(iconColor);
                                        vehicleInfo.setvStatus(deviceStatus);
                                        vehicleInfo.setvTargetName(target_name);

                                        if (address.equals("NA")) {
                                            vehicleInfo.setvAddress(Constants.uLocation);
                                        } else {
                                            vehicleInfo.setvAddress(address);
                                        }
                                        VEHICLE_INFO_PARKING.add(vehicleInfo);
                                    }
                                    else if(deviceStatus.equals("OFF"))
                                    {
                                        TrackingInfo vehicleInfo = new TrackingInfo();

                                        vehicleInfo.setvImei(imei);
                                        vehicleInfo.setvNumber(vehicleNo);
                                        vehicleInfo.setvLastTrackTime(last_dt);
                                        vehicleInfo.setVlat(latitude);
                                        vehicleInfo.setVlng(longitude);
                                        vehicleInfo.setvType(vehicleType);
                                        vehicleInfo.setvColor(iconColor);
                                        vehicleInfo.setvStatus(deviceStatus);
                                        vehicleInfo.setvTargetName(target_name);

                                        if (address.equals("NA")) {
                                            vehicleInfo.setvAddress(Constants.uLocation);
                                        } else {
                                            vehicleInfo.setvAddress(address);
                                        }
                                        VEHICLE_INFO_OFFLINE.add(vehicleInfo);
                                    }

                                }
                                else
                                {
                                    TrackingInfo vehicleInfo = new TrackingInfo();

                                    vehicleInfo.setvImei(imei);
                                    vehicleInfo.setvNumber(vehicleNo);
                                    vehicleInfo.setvLastTrackTime(last_dt);
                                    vehicleInfo.setVlat(latitude);
                                    vehicleInfo.setVlng(longitude);
                                    vehicleInfo.setvType(vehicleType);
                                    vehicleInfo.setvColor(iconColor);
                                    vehicleInfo.setvStatus(deviceStatus);
                                    vehicleInfo.setvTargetName(target_name);

                                    if (address.equals("NA")) {
                                        vehicleInfo.setvAddress(Constants.uLocation);
                                    } else {
                                        vehicleInfo.setvAddress(address);
                                    }
                                    VEHICLE_INFO.add(vehicleInfo);
                                }


                            }

                            if(vStatus.equals("All"))
                            {
                                //running
                                for (int a = 0; a < VEHICLE_INFO_RUNNING.size(); a++)
                                {
                                    VEHICLE_INFO.add(VEHICLE_INFO_RUNNING.get(a));
                                }
                                //idle
                                for (int a = 0; a < VEHICLE_INFO_IDLE.size(); a++)
                                {
                                    VEHICLE_INFO.add(VEHICLE_INFO_IDLE.get(a));
                                }
                                //parking
                                for (int a = 0; a < VEHICLE_INFO_PARKING.size(); a++)
                                {
                                    VEHICLE_INFO.add(VEHICLE_INFO_PARKING.get(a));
                                }
                                //offline
                                for (int a = 0; a < VEHICLE_INFO_OFFLINE.size(); a++)
                                {
                                    VEHICLE_INFO.add(VEHICLE_INFO_OFFLINE.get(a));
                                }
                            }

                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return success + "#" + message;
        }
    }

    public void uiUpdate(final String message) {
        new Runnable() {

            @Override
            public void run() {
                //update ui here
                // display toast here
                if (message.equals("not")) {
                    searchBarRl.setVisibility(View.VISIBLE);
                    vehicleListRecycle.setVisibility(View.VISIBLE);
                } else {
                    vehicleListRecycle.setVisibility(View.GONE);
                    txt_error_no_data.setText(message);
                }

            }
        };
    }

    public void sR(String message, String error) {

        if (pStatus.equals("first")) {
            new MaterialDialog.Builder(getActivity())
                    .title(error)
                    .content(message)
                    .positiveText("Try again")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                            getAlldata();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                        }
                    })
                    .show();
        }


    }

    private void setStaticData() {

        for (int i = 0; i < data.length; i++) {
            TrackingInfo vehicleInfo = new TrackingInfo();

            vehicleInfo.setvNumber(data[i]);

            VEHICLE_INFO.add(vehicleInfo);

        }
        vehicleListAdapter.setAllDeviceInfo(VEHICLE_INFO);
        vehicleListAdapter.notifyDataSetChanged();

    }

    private void initialize() {

        try {
            vehicleListAdapter = new TrackingAdapter(getActivity());
            vehicleListRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
            vehicleListRecycle.setAdapter(vehicleListAdapter);

            serachDataEditText.addTextChangedListener(new TextWatcher()
            {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence newText, int start, int before, int count) {
                    //M.t(""+s);
                    final ArrayList<TrackingInfo> filteredModelList = filter(VEHICLE_INFO, "" + newText);
                    vehicleListAdapter.setFilter(filteredModelList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private ArrayList<TrackingInfo> filter(ArrayList<TrackingInfo> models, String query) {
        query = query.toLowerCase();
        final ArrayList<TrackingInfo> filteredModelList = new ArrayList<>();
        filteredModelList.clear();

        if (query.length() == 0) {
            filteredModelList.addAll(models);
        } else {
            for (TrackingInfo model : models) {

                final String vehicleNumber = model.getvNumber().toLowerCase();
                /*final String runningTime = model.getRunningTime();
                final String idleTime = model.getIdleTime();
                final String stopTime = model.getStopTime();
                final String totalStop = model.getTotalStop();
                final String avgSpeed = model.getAvgSpeed();
                final String maxSpeed = model.getMaxSpeed();*/

                //vehicleNumber
                if (vehicleNumber.contains(query)) {
                    filteredModelList.add(model);
                }
                //runningTime
                /*else if (runningTime.contains(query))
                {
                    filteredModelList.add(model);
                }
                //idleTime
                else if (idleTime.contains(query))
                {
                    filteredModelList.add(model);
                }
                //stopTime
                else if (stopTime.contains(query))
                {
                    filteredModelList.add(model);
                }
                //totalStop
                else if (totalStop.contains(query))
                {
                    filteredModelList.add(model);
                }
                //avgSpeed
                else if (avgSpeed.contains(query))
                {
                    filteredModelList.add(model);
                }
                //maxSpeed
                else if (maxSpeed.contains(query))
                {
                    filteredModelList.add(model);
                }*/


            }
        }


        return filteredModelList;
    }

    Timer t;
    TimerTask mTimerTaskZhoom;

    /*private void refreshFragment()
    {

        t = new Timer();
        mTimerTaskZhoom = new TimerTask()
        {
            public void run()
            {
                MyApplication.handler.post(new Runnable()
                {
                    public void run()
                    {
                        if(F.checkConnection())
                        {
                            //Log.e("TIMERRR","Callxxx");
                            Log.e("Timer","VehicleTrackingFragment timer call");
                            callDetails("second");

                            //callHandler();
                        }
                    }
                });
            }
        };

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTaskZhoom, Constants.timerDelay, Constants.timerPeriod);  //
    }*/
    /*private void refreshFragment()
    {
        //final Handler handler = new Handler();
        MyApplication.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                if(F.checkConnection())
                {
                    //Log.e("TIMERRR","Callxxx");
                    Log.e("Timer","MonitorFragment timer call");
                    callDetails("second");

                    //callHandler();
                }

                //Do 30 sec
                MyApplication.handler.postDelayed(this, Constants.timerDelay);
            }
        }, Constants.timerDelay);


    }*/
    private void refreshFragment() {
        mIsRunning = true;
        mStatusChecker.run();


    }

    boolean mIsRunning;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (!mIsRunning) {
                return; // stop when told to stop
            }
            if (F.checkConnection()) {
                //Log.e("TIMERRR","Callxxx");
                Log.e("Timer", "VehicleTrackingFragment timer call");

                pStatus = "second";
                callDetails();

                //callHandler();
            }
            mHandler.postDelayed(mStatusChecker, Constants.timerDelay);
        }
    };

    void startRepeatingTask() {
        mIsRunning = true;
        mStatusChecker.run();
    }

    public void cancelHandler() {
        mIsRunning = false;
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cancelHandler();
    }
}
