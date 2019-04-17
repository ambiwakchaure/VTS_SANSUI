package vts.snystems.sns.sansui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.sansui.R;
import vts.snystems.sns.sansui.activity.ActivityNearbyPlaces;
import vts.snystems.sns.sansui.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.sansui.classes.ArcProgress;
import vts.snystems.sns.sansui.classes.F;
import vts.snystems.sns.sansui.classes.LiveTrackInfoDialog;
import vts.snystems.sns.sansui.classes.M;
import vts.snystems.sns.sansui.classes.MyApplication;
import vts.snystems.sns.sansui.classes.V;
import vts.snystems.sns.sansui.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.sansui.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.sansui.interfaces.Constants;
import vts.snystems.sns.sansui.pojo.DeviceInfo;
import vts.snystems.sns.sansui.volley.Rc;
import vts.snystems.sns.sansui.volley.VolleyCallback;
import vts.snystems.sns.sansui.volley.VolleyErrorCallback;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class TrackFragment extends Fragment implements OnMapReadyCallback {

    LatLng startPoint,endPoint;

    int cntC = 0;

    String loginJson;
    @BindView(R.id.locationTextView)
    TextView locationTextView;

    @BindView(R.id.arcprogress_speed)
    ArcProgress arcprogressSpeed;

    @BindView(R.id.arcprogress_odometer)
    ArcProgress arcprogressOdometer;

    @BindView(R.id.arcprogress_fuel)
    ArcProgress arcprogressFuel;


    @BindView(R.id.image_ign_status)
    ImageView imageIgnStatus;

    @BindView(R.id.image_power_status)
    ImageView imagePowerStatus;

    @BindView(R.id.image_live_power)
    TextView imageLivePower;

    @BindView(R.id.image_gps_status)
    ImageView imageGpsStatus;

    @BindView(R.id.lastTripDistane)
    TextView lastTripDistane;


    @BindView(R.id.lastTripParkTime)
    TextView lastTripParkTime;

    @BindView(R.id.vStatusBtn)
    Button vStatusBtn;


    @BindView(R.id.multiple_actions)
    FloatingActionsMenu multiple_actions;

    @BindView(R.id.fb_Pumps)
    FloatingActionButton fb_Pumps;

    @BindView(R.id.fb_Restaurants)
    FloatingActionButton fb_Restaurants;

    @BindView(R.id.fb_Track)
    FloatingActionButton fb_Track;

    @BindView(R.id.lastTripDuration)
    TextView lastTripDuration;

    @BindView(R.id.odometerLoadingTxt)
    TextView odometerLoadingTxt;

    @BindView(R.id.fuelLoadingTxt)
    TextView fuelLoadingTxt;


    Handler mHandler;
    //Handler mHandlerFuelOdoTrip;


    private GoogleMap mMap;
    View view;
    String lastTripParkTime_v,lastTripDuration_v;
    //String parkLastTime,durLastTime,parkTotaTime,durTotalTime;

    @BindView(R.id.buttonMapType)
    Button buttonMapType;
    String vHicleStatus,vType,course = "0";
    String overspeed_value;
    ActivityPlaybackTrackInfo activityPlaybackTrackInfo;

    String IMEI,latitude,longitude,speed1 = "0",lastTripDistane_v,fuel1 = "0",IGN_STATUS,POWER_STATUS,GPS_STATUS,odometer = "0",last_dt;

    String fuelRefreshStatus,odometerRefreshStatus;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frg_track, container, false);
        ButterKnife.bind(this, view);
        initialize();

        mHandler = new Handler();
        //mHandlerFuelOdoTrip = new Handler();
       // mHandlerFuel = new Handler();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        vHicleStatus = MyApplication.prefs.getString(Constants.DEVICE_STATUS,"");
        vType = MyApplication.prefs.getString(Constants.VTYPE,"");
        IMEI = MyApplication.prefs.getString(Constants.IMEI,"");
        String LAST_UPDATE_DATE_TIME = MyApplication.prefs.getString(Constants.LAST_UPDATE_DATE_TIME,"");


        //set previous data
        setData();

        //set status of vehicle
        setVehicleStatus(vHicleStatus,vStatusBtn);

        fuelRefreshStatus = "first";
        odometerRefreshStatus = "first";


        //first get vehicle location details and vehicle status
        refreshFragmentForVehicleLoation();//963


        //get fuel odo trip data first time
        if(F.checkConnection())
        {
            //get fuel data
            getFuelDetails();
            //get odometer data
            getOdometerDetails();
            //get last trip data
            getLastTripData();
        }






        buttonMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(getActivity(), buttonMapType);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {

                        String itemTitle = item.getTitle().toString();
                        if(itemTitle.equals("Normal") || itemTitle.equals("साधारण") || itemTitle.equals("सामान्य"))
                        {

                            MyApplication.editor.putString(Constants.MAP_TYPE,Constants.MAP_TYPE_NORMAL).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.normal));
                            //startRepeatingTask();
                        }
                        else if(itemTitle.equals("Satellite") || itemTitle.equals("साटलाईट"))
                        {
                            MyApplication.editor.putString(Constants.MAP_TYPE,Constants.MAP_TYPE_SAT).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.satellite));
                            //stopRepeatingTask();
                        }
                        else if(itemTitle.equals("Hybrid") || itemTitle.equals("हायब्रीड"))
                        {
                            MyApplication.editor.putString(Constants.MAP_TYPE,Constants.MAP_TYPE_HY).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.hybrid));



                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

       // refreshFragment();
        return view;

    }


    private void getLastTripData()
    {


        try
        {

            RequestQueue requestQueue = null;
            String url = Constants.getDeviceLastTrip_v16;
            M.e("getDeviceLastTrip_v16 : "+url);
            M.e("last trip imei : "+IMEI);


            lastTripDistane.setText("Please wait...");
            lastTripParkTime.setText("Please wait...");
            lastTripDuration.setText("Please wait...");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            M.e("fuel response : "+response);
                            parseLastTrip(response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            M.e("fuel response : "+error);

                            getLastTripData();

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Constants.IMEI,IMEI);
                    return params;
                }
            };
            MyApplication.requestQueue.getCache().clear();
            if(requestQueue == null)
            {
                requestQueue = Volley.newRequestQueue(getActivity());
            }
            RetryPolicy retryPolicy = new DefaultRetryPolicy(60000, 0, 0);
            stringRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            M.e("");
            e.printStackTrace();
        }

    }

    private void parseLastTrip(String response) {


        try
        {
            if(response != null && response.length() > 0)
            {
                Object json = new JSONTokener(response).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject jsonObject = new JSONObject(response);

                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                        //odometerRefreshtxt.setVisibility(View.GONE);
                        JSONObject deviceDetails = jsonObject.getJSONObject("deviceDetails");
                        lastTripDistane_v = deviceDetails.getString("lt_dist");//Last Distance
                        lastTripParkTime_v = deviceDetails.getString("lt_stop");//Last Parking
                        lastTripDuration_v = deviceDetails.getString("lt_duration");//Last Duration

                        if(lastTripParkTime_v.equals("0:0:0") || lastTripParkTime_v.equals("0"))
                        {
                            lastTripParkTime_v = "00:00:00";
                        }
                        if(lastTripDuration_v.equals("0:0:0") || lastTripDuration_v.equals("0"))
                        {
                            lastTripDuration_v = "00:00:00";
                        }

                        lastTripDistane.setText(lastTripDistane_v+" Km");//last Distance
                        lastTripParkTime.setText(lastTripParkTime_v);//Last Parking
                        lastTripDuration.setText(lastTripDuration_v);//Last Duration
                    }
                    else
                    {

                    }
                }
                else
                {
                    //odometerRefreshtxt.setVisibility(View.GONE);
                    M.t("Invalid json found");
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getOdometerDetails()
    {


        try
        {

            RequestQueue requestQueue = null;
            String url = Constants.getDeviceOdometer_v16;
            M.e("getDeviceOdometer_v16 : "+url);
            M.e("odometer imei : "+IMEI);

            if(odometerRefreshStatus.equals("first"))
            {

                odometerLoadingTxt.setText("Odometer loading...");

            }


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            odometerLoadingTxt.setText("Odometer");
                            //M.e("fuel response : "+response);
                            parseOdometer(response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            M.e("fuel response : "+error);
                            odometerLoadingTxt.setText("Odometer Loading...");
                            getOdometerDetails();

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Constants.IMEI,IMEI);
                    return params;
                }
            };
            MyApplication.requestQueue.getCache().clear();
            if(requestQueue == null)
            {
                requestQueue = Volley.newRequestQueue(getActivity());
            }
            RetryPolicy retryPolicy = new DefaultRetryPolicy(50000, 0, 0);
            stringRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            M.e("");
            e.printStackTrace();
        }

    }

    private void parseOdometer(String response) {


        try
        {
            if(response != null && response.length() > 0)
            {
                Object json = new JSONTokener(response).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject jsonObject = new JSONObject(response);

                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                        odometerRefreshStatus = "second";
                        odometerLoadingTxt.setText("Odometer");
                        JSONObject deviceDetails = jsonObject.getJSONObject("deviceDetails");
                        String odometer = deviceDetails.getString("odometer");
                        float odo = Float.valueOf(odometer);
                        arcprogressOdometer.setProgress(odo);

                    }
                    else
                    {
                        odometerLoadingTxt.setText("Odometer Loading...");
                    }
                }
                else
                {
                    odometerLoadingTxt.setText("Odometer Loading...");
                  // M.t("Invalid json found");
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getFuelDetails() {


        try
        {

            RequestQueue requestQueue = null;
            String url = Constants.getDeviceFuel_v16;
            M.e("getDeviceFuel_v16 : "+url);
            M.e("fuel imei : "+IMEI);

            if(fuelRefreshStatus.equals("first"))
            {
                fuelLoadingTxt.setText("Fuel Loading...");
            }


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            fuelLoadingTxt.setText("Fuel");
                            //M.e("fuel response : "+response);
                            parseFuel(response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            M.e("fuel response : "+error);
                            fuelLoadingTxt.setText("Fuel Loading...");
                            getFuelDetails();

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Constants.IMEI,IMEI);
                    return params;
                }
            };
            MyApplication.requestQueue.getCache().clear();
            if(requestQueue == null)
            {
                requestQueue = Volley.newRequestQueue(getActivity());
            }
            RetryPolicy retryPolicy = new DefaultRetryPolicy(50000, 0, 0);
            stringRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            M.e("");
            e.printStackTrace();
        }

    }

    private void parseFuel(String response) {


        try
        {
            if(response != null && response.length() > 0)
            {
                Object json = new JSONTokener(response).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject jsonObject = new JSONObject(response);

                    String success = jsonObject.getString("success");
                    if(success.equals("1"))
                    {
                        fuelRefreshStatus = "second";
                        fuelLoadingTxt.setText("Fuel");
                        JSONObject deviceDetails = jsonObject.getJSONObject("deviceDetails");
                        String fuel1 = deviceDetails.getString("fuel");
                        int fuel = Integer.valueOf(fuel1);
                        arcprogressFuel.setProgress(fuel);

                    }
                    else
                    {
                        fuelLoadingTxt.setText("Fuel Loading...");
                    }
                }
                else
                {
                    fuelLoadingTxt.setText("Fuel Loading...");
                    M.t("Invalid json found");
                }

            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }

    }
    private void getVehicleLocationAndStatus()
    {

        try
        {
            String[] parameters =
                    {
                            Constants.IMEI + "#" + IMEI

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseLocationstatusResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {

                            M.e("getVehicleLocationAndStatus () VolleyError : "+volleyErrr);
                        }
                    },
                    Constants.webUrl + "" + Constants.getDeviceLastData_v16,
                    parameters,
                    getActivity(), "second");


        } catch (Exception e) {

        }


    }

    private void parseLocationstatusResponse(String result) {

        try {

            if (result != null || result.length() > 0) {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(result);

                    String success = loginJsonObject.getString("success");
                    String message = loginJsonObject.getString("message");

                    if (success.equals("1"))
                    {

                        JSONArray jsonArray = loginJsonObject.getJSONArray("deviceDetails");

                        if(jsonArray.length() > 0)
                        {
                            JSONObject vehicleJsonObject = jsonArray.getJSONObject(0);
                           // M.e("vehicleJsonObject : "+vehicleJsonObject);
                            setVehicleData(vehicleJsonObject);
                        }



                    }
                    else if (success.equals("2"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("3"))
                    {
                        M.t(message);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVehicleData(JSONObject vehicleJsonObject)
    {
        String deviceStatus = Constants.NA;
        String IGN_STATUS = Constants.ZERO;
        String POWER_STATUS = Constants.ZERO;
        String GPS_STATUS = Constants.ZERO;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String speed1 = Constants.ZERO;
        //String fuel1 = Constants.ZERO;
        String last_dt = Constants.NA;


        try
        {
            if(vehicleJsonObject.has("deviceStatus") && !vehicleJsonObject.isNull("deviceStatus"))
            {
                deviceStatus = vehicleJsonObject.getString("deviceStatus");
            }
            if(vehicleJsonObject.has("ignS") && !vehicleJsonObject.isNull("ignS"))
            {
                IGN_STATUS = vehicleJsonObject.getString("ignS");
            }
            if(vehicleJsonObject.has("powerS") && !vehicleJsonObject.isNull("powerS"))
            {
                POWER_STATUS = vehicleJsonObject.getString("powerS");
            }
            if(vehicleJsonObject.has("gpsS") && !vehicleJsonObject.isNull("gpsS"))
            {
                GPS_STATUS = vehicleJsonObject.getString("gpsS");
            }
            if(vehicleJsonObject.has("latitude") && !vehicleJsonObject.isNull("latitude"))
            {
                latitude = vehicleJsonObject.getString("latitude");
            }
            if(vehicleJsonObject.has("longitude") && !vehicleJsonObject.isNull("longitude"))
            {
                longitude = vehicleJsonObject.getString("longitude");
            }
            if(vehicleJsonObject.has("speed") && !vehicleJsonObject.isNull("speed"))
            {
                speed1 = vehicleJsonObject.getString("speed");
            }
            /*if(vehicleJsonObject.has("fuel") && !vehicleJsonObject.isNull("fuel"))
            {
                fuel1 = vehicleJsonObject.getString("fuel");
            }*/
            if(vehicleJsonObject.has("last_dt") && !vehicleJsonObject.isNull("last_dt"))
            {
                last_dt = vehicleJsonObject.getString("last_dt");
            }
            if(vehicleJsonObject.has("course") && !vehicleJsonObject.isNull("course"))
            {
                course = vehicleJsonObject.getString("course");
            }


            if(deviceStatus != null)
            {
                //if vehicle not running state
                if(deviceStatus.equals("MV"))
                {
                    //vStatusBtn.setText("Running");
                    vStatusBtn.setText(MyApplication.context.getResources().getString(R.string.running));
                    vStatusBtn.setBackgroundColor(Color.parseColor("#43a047"));

                }
                else if(deviceStatus.equals("ST"))
                {
                    //vStatusBtn.setText("Idle");
                    vStatusBtn.setText(MyApplication.context.getResources().getString(R.string.idle));
                    vStatusBtn.setBackgroundColor(Color.parseColor("#1e88e5"));
                    //cancelHandler();
                }
                else if(deviceStatus.equals("SP"))
                {
                    //vStatusBtn.setText("Parking");
                    vStatusBtn.setText(MyApplication.context.getResources().getString(R.string.parking));
                    vStatusBtn.setBackgroundColor(Color.parseColor("#f9a825"));
                    //cancelHandler();
                }
                else if(deviceStatus.equals("OFF"))
                {
                    //vStatusBtn.setText("Offline");
                    vStatusBtn.setText(MyApplication.context.getResources().getString(R.string.offline));
                    vStatusBtn.setBackgroundColor(Color.parseColor("#616161"));
                    //cancelHandler();
                }
            }
            if(IGN_STATUS.equals("0"))
            {
                imageIgnStatus.setImageResource(R.drawable.ic_ign_off);
                arcprogressSpeed.setProgress(0.0f);
            }
            else if(IGN_STATUS.equals("1"))
            {
                imageIgnStatus.setImageResource(R.drawable.ic_ign_on);
                float speed = Float.valueOf(speed1);
                arcprogressSpeed.setProgress(speed);
            }

            if(POWER_STATUS.equals("0"))
            {
                imagePowerStatus.setImageResource(R.drawable.ic_power_off);
            }
            else if(POWER_STATUS.equals("1"))
            {
                imagePowerStatus.setImageResource(R.drawable.ic_power_on);
            }
            if(GPS_STATUS.equals("0"))
            {
                imageGpsStatus.setImageResource(R.drawable.ic_gps_off);
            }
            else if(GPS_STATUS.equals("1"))
            {
                imageGpsStatus.setImageResource(R.drawable.ic_gps_on);
            }


            String address = F.getAddress(Double.valueOf(latitude),Double.valueOf(longitude));
            locationTextView.setText(address);



          //  int fuel = Integer.valueOf(fuel1);
           // arcprogressFuel.setProgress(fuel);
            //update last track date time
            activityPlaybackTrackInfo.updateTRackDateTime(last_dt);
            activityPlaybackTrackInfo.updateVNumber();


            if(cntC == 0)
            {
                if(IGN_STATUS.equals("1") && deviceStatus.equals("MV"))
                {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))));
                    LatLng india = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 15));
                }
            }
            if(IGN_STATUS.equals("1") && deviceStatus.equals("MV"))
            {
                plotRoute(latitude,longitude,deviceStatus);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initialize() {

        fb_Track.setSize(FloatingActionButton.SIZE_MINI);
        fb_Track.setIcon(R.drawable.ic_track_location_black_24dp);
        fb_Track.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String lat = MyApplication.prefs.getString(Constants.LATITUDE,"0");
                String lng = MyApplication.prefs.getString(Constants.LONGITUDE,"0");
                String url = "https://www.google.com/maps/dir/?api=1&destination="+lat+","+lng+"&mode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        fb_Restaurants.setSize(FloatingActionButton.SIZE_MINI);
        fb_Restaurants.setIcon(R.drawable.ic_restaurant_black_24dp);
        fb_Restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(F.checkConnection())
                {
                    Intent intent = new Intent(getActivity(), ActivityNearbyPlaces.class);
                    intent.putExtra(Constants.TYPE_DATA,"Restaurants");
                    startActivityForResult(intent,3);
                }
                else
                {
                    M.t(getString(R.string.check_wi));
                }
            }
        });
        fb_Pumps.setSize(FloatingActionButton.SIZE_MINI);
        fb_Pumps.setIcon(R.drawable.ic_local_gas_station_black_24dp);
        fb_Pumps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(F.checkConnection())
                {
                    Intent intent = new Intent(getActivity(), ActivityNearbyPlaces.class);
                    intent.putExtra(Constants.TYPE_DATA,"Petrol Pumps");
                    startActivityForResult(intent,3);
                }
                else
                {
                    M.t(getString(R.string.check_wi));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 3)
        {
            setMapType();
        }
    }

    private void setVehicleStatus(String vStatus, Button vStatusColLi)
    {
        Log.e("vStatus","vStatus : "+vStatus);
        if(vStatus.equals("MV"))
        {
            //vStatusColLi.setText("Running");
            vStatusColLi.setText(MyApplication.context.getResources().getString(R.string.running));
            vStatusColLi.setBackgroundColor(Color.parseColor("#43a047"));
        }
        else if(vStatus.equals("ST"))
        {
           // vStatusColLi.setText("Idle");
            vStatusColLi.setText(MyApplication.context.getResources().getString(R.string.idle));
            vStatusColLi.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if(vStatus.equals("SP"))
        {
            //vStatusColLi.setText("Parking");
            vStatusColLi.setText(MyApplication.context.getResources().getString(R.string.parking));
            vStatusColLi.setBackgroundColor(Color.parseColor("#f9a825"));
        }
        else if(vStatus.equals("OFF"))
        {
           // vStatusColLi.setText("Offline");
            vStatusColLi.setText(MyApplication.context.getResources().getString(R.string.offline));
            vStatusColLi.setBackgroundColor(Color.parseColor("#616161"));
        }
        else
        {
            vStatusColLi.setVisibility(View.GONE);
        }
    }
    private void refreshFragmentForVehicleLoation()
    {
        mIsRunning = true;
        mStatusChecker.run();


    }

    boolean mIsRunning;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run()
        {
            if (!mIsRunning) {
                return; // stop when told to stop
            }
            if(F.checkConnection())
            {
               M.e("Track Fragment Call Location");
                getVehicleLocationAndStatus();
                //Log.e("TIMERRR","Callxxx");
                //Log.e("Timer","TrackFragment timer call");
                //getRunningDevices();
            }
            mHandler.postDelayed(mStatusChecker, Constants.timerDelay);
        }
    };

    public void cancelHandler()
    {
        mIsRunning = false;
        mHandler.removeCallbacks(mStatusChecker);
    }
    /*private void refreshFragmentForFuelOdoTrip()
    {
        mIsRunningFuelOdoTrip = true;
        mStatusCheckerFuelOdoTrip.run();


    }

    boolean mIsRunningFuelOdoTrip;
    Runnable mStatusCheckerFuelOdoTrip = new Runnable() {
        @Override
        public void run()
        {
            if (!mIsRunningFuelOdoTrip) {
                return; // stop when told to stop
            }
            if(F.checkConnection())
            {
                M.e("Track Fragment Call Fuel Odo Trip");
                //get fuel data
                getFuelDetails();
                //get odometer data
                getOdometerDetails();
            }
            mHandlerFuelOdoTrip.postDelayed(mStatusCheckerFuelOdoTrip, Constants.timerDelayFuelOdoTrip);
        }
    };

    public void cancelHandlerFuelOdoTrip()
    {
        mIsRunningFuelOdoTrip = false;
        mHandlerFuelOdoTrip.removeCallbacks(mStatusCheckerFuelOdoTrip);
    }*/


    @Override
    public void onDetach() {
        super.onDetach();
        cancelHandler();
       // cancelHandlerFuelOdoTrip();
    }

    public void newInstannce(ActivityPlaybackTrackInfo activityPlaybackTrackInfo)
    {
        this.activityPlaybackTrackInfo = activityPlaybackTrackInfo;
    }


    Marker routMark;
    DeviceInfo deviceInfo;
    LatLngBounds.Builder builder;
    private void plotRoute(String latitude,String longitude,String device_status)
    {

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        if(cntC == 0)
        {
            startPoint = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
            endPoint = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        }
        else
        {

            startPoint = endPoint;
            endPoint = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        }

       //check overspeed of vehicle
        int overSpeeed = Integer.valueOf(MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED,"0"));
        //Log.e("OVERSPEED","overSpeeed : "+overSpeeed);
       // Log.e("OVERSPEED","speed1 : "+speed1);
        if(Integer.valueOf(speed1) >= overSpeeed)
        {
            options.color(Color.RED);
        }
        else
        {
            options.color(Color.BLUE);
        }
        options.startCap(new SquareCap());
        options.endCap(new SquareCap());
        options.jointType(ROUND);
        options.add(startPoint);
        options.add(endPoint);

        if(routMark != null)
        {
            routMark.remove();
        }
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        /*routMark =  mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map_track)));*/

        deviceInfo = new DeviceInfo();
        builder = new LatLngBounds.Builder();

        deviceInfo.setDeviceStatus(device_status);
        //deviceInfo.setLastLocation(latitude+" "+longitude);

        if(vType.equals("MC"))
        {


            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));

            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());


        }
        else if(vType.equals("CR"))
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("TR"))//tractor
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());

        }
        else if(vType.equals("TK"))//trucks
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("CE"))//crane
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("CN"))//container
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());

        }
        else if(vType.equals("BL"))//bacoloader
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("TN"))//tanker
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));

            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());

        }
        else if(vType.equals("BS"))//bus
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("MR"))//marker
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else if(vType.equals("GC"))
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));


            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());
        }
        else
        {

            routMark =  mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

            routMark.setTag(deviceInfo);
            routMark.setRotation(Float.valueOf(course));
            // marker.showInfoWindow();
            builder.include(routMark.getPosition());

        }

        LiveTrackInfoDialog customInfoWindow = new LiveTrackInfoDialog(getActivity());
        mMap.setInfoWindowAdapter(customInfoWindow);

        //F.setMarkerVehicleIconType(vType,mMap,new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        routMark.setRotation(Float.valueOf(course));
        mMap.addPolyline(options);
        mMap.animateCamera(CameraUpdateFactory .newLatLngZoom(routMark.getPosition(), mMap.getCameraPosition().zoom));
        cntC = 1;
    }
    private void setData()
    {


        MyApplication.prefs.getString(Constants.IMEI,"0");

        String IGN_STATUS = MyApplication.prefs.getString(Constants.IGN_STATUS,"0");
        String POWER_STATUS = MyApplication.prefs.getString(Constants.POWER_STATUS,"0");
        String GPS_STATUS = MyApplication.prefs.getString(Constants.GPS_STATUS,"0");

        if(IGN_STATUS.equals("0"))
        {
            imageIgnStatus.setImageResource(R.drawable.ic_ign_off);
            arcprogressSpeed.setProgress(0.0f);
        }
        else if(IGN_STATUS.equals("1"))
        {
            imageIgnStatus.setImageResource(R.drawable.ic_ign_on);
            float speed = Float.valueOf(MyApplication.prefs.getString(Constants.SPEED,"0"));
            arcprogressSpeed.setProgress(speed);
        }

        if(POWER_STATUS.equals("0"))
        {
            imagePowerStatus.setImageResource(R.drawable.ic_power_off);
        }
        else if(POWER_STATUS.equals("1"))
        {
            imagePowerStatus.setImageResource(R.drawable.ic_power_on);
        }

        if(GPS_STATUS.equals("0"))
        {
            imageGpsStatus.setImageResource(R.drawable.ic_gps_off);
        }
        else if(GPS_STATUS.equals("1"))
        {
            imageGpsStatus.setImageResource(R.drawable.ic_gps_on);
        }
        String address = F.getAddress(Double.valueOf(MyApplication.prefs.getString(Constants.LATITUDE,"0")),Double.valueOf(MyApplication.prefs.getString(Constants.LONGITUDE,"0")));

        Log.e("address",""+address);
        if(address.equals("NA"))
        {
            locationTextView.setText(Constants.uLocation);
        }
        else
        {
            locationTextView.setText(address);
        }


    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //set google map camera position by default on india
        mMap = googleMap;
        F.setDefaultMap(mMap);
        setMapType();
        String lat = MyApplication.prefs.getString(Constants.LATITUDE,"0");
        String lng = MyApplication.prefs.getString(Constants.LONGITUDE,"0");
        String COURSE = MyApplication.prefs.getString(Constants.COURSE,"0");

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                mMap.getUiSettings().setMapToolbarEnabled(false);
                return false;
            }
        });
        if(!V.checkNull(lat))
        {
            return;
        }
        if(!V.checkNull(lng))
        {
            return;
        }

        deviceInfo = new DeviceInfo();
        builder = new LatLngBounds.Builder();
        deviceInfo.setDeviceStatus( MyApplication.prefs.getString(Constants.DEVICE_STATUS,"0"));

        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        routMark = F.setMarkerVehicleIconType(vType,mMap,latlng,Float.valueOf(COURSE));
        routMark.setTag(deviceInfo);
        routMark.setRotation(Float.valueOf(course));
        // marker.showInfoWindow();
        builder.include(routMark.getPosition());

        LiveTrackInfoDialog customInfoWindow = new LiveTrackInfoDialog(getActivity());
        mMap.setInfoWindowAdapter(customInfoWindow);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

        //draw geofence
        drawGeofence();
    }

    private void drawGeofence() {

        ArrayList<GeoFenceObjectClass> GEOFENCE_DATA = TABLE_STORE_GEOFENCE.selectGeofence(MyApplication.prefs.getString(Constants.IMEI,"0"));

        if(!GEOFENCE_DATA.isEmpty())
        {
            for(int i = 0; i < GEOFENCE_DATA.size(); i++)
            {
                GeoFenceObjectClass geoFenceObjectClass = GEOFENCE_DATA.get(i);
                String latitude = geoFenceObjectClass.getGetGeo_fence_lat_long();
                String radius = geoFenceObjectClass.getGeo_fence_radius();

                Log.e("DAAAAA",""+radius);

                String [] latlngData = latitude.split(",");
                F.createGeofence(mMap,Double.valueOf(latlngData[0]),Double.valueOf(latlngData[1]),Integer.valueOf(radius) * 1000);

            }
        }
    }

    private void setMapType()
    {

        //set Map type
        String mType = MyApplication.prefs.getString(Constants.MAP_TYPE,Constants.MAP_TYPE_NORMAL);

        if(mType.equals(Constants.MAP_TYPE_NORMAL)|| mType.equals("साधारण") || mType.equals("सामान्य"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.normal));
        }
        else if(mType.equals(Constants.MAP_TYPE_SAT) || mType.equals("साटलाईट"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.satellite));
        }
        else if(mType.equals(Constants.MAP_TYPE_HY) || mType.equals("हायब्रीड"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.hybrid));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
