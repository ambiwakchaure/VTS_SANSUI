package vts.snystems.sns.vts.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityAlertHistory;
import vts.snystems.sns.vts.activity.ActivityNearbyPlaces;
import vts.snystems.sns.vts.activity.ActivityPlayBack;
import vts.snystems.sns.vts.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.vts.classes.ArcProgress;
import vts.snystems.sns.vts.classes.CustomInfoWindowGoogleMap;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.LiveTrackInfoDialog;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.V;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.fonts.MyTextViewNormal;
import vts.snystems.sns.vts.fonts.MyTextViewSmiBold;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.pojo.AlertHistoryInfo;
import vts.snystems.sns.vts.pojo.DeviceInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

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

    Handler mHandler;


    private GoogleMap mMap;
    View view;
    String lastTripParkTime_v,lastTripDuration_v;
    //String parkLastTime,durLastTime,parkTotaTime,durTotalTime;

    @BindView(R.id.buttonMapType)
    Button buttonMapType;
    String vHicleStatus,vType,course = "0";
    String overspeed_value;
    ActivityPlaybackTrackInfo activityPlaybackTrackInfo;

    String latitude,longitude,speed1 = "0",lastTripDistane_v,fuel1 = "0",IGN_STATUS,POWER_STATUS,GPS_STATUS,odometer = "0",last_dt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frg_track, container, false);
        ButterKnife.bind(this, view);
        initialize();

        mHandler = new Handler();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        vHicleStatus = MyApplication.prefs.getString(Constants.DEVICE_STATUS,"");
        vType = MyApplication.prefs.getString(Constants.VTYPE,"");


        //set status of vehicle
        setVehicleStatus(vHicleStatus,vStatusBtn);

        //refreshFragment();
        setData();
        //start handler when fragment close
        refreshFragment();
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
    private void refreshFragment()
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
                //Log.e("TIMERRR","Callxxx");
                Log.e("Timer","TrackFragment timer call");
                getRunningDevices();
            }
            mHandler.postDelayed(mStatusChecker, Constants.timerDelay);
        }
    };

    public void cancelHandler()
    {
        mIsRunning = false;
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        //cancel handler when fragment close
        cancelHandler();

    }
    public void getRunningDevices()
    {

        try
        {

            String[] parameters =
                    {
                            Constants.IMEI + "#" + MyApplication.prefs.getString(Constants.IMEI,"0")

                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {

                            //parseResponse(result);
                            loginJson = result;
                            new ParseResponse().execute();

                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                        }
                    },

                    Constants.webUrl + "" + Constants.getDeviceDetail,
                    parameters,
                    getActivity(), "second");



        } catch (Exception e) {

        }

    }

    public void newInstannce(ActivityPlaybackTrackInfo activityPlaybackTrackInfo)
    {
        this.activityPlaybackTrackInfo = activityPlaybackTrackInfo;
    }

    class ParseResponse extends AsyncTask<String,String,String>
    {



        @Override
        protected String doInBackground(String... urls)
        {

            String deviceStatus = null;
            try
            {

                if (loginJson != null || loginJson.length() > 0)
                {
                    Object json = new JSONTokener(loginJson).nextValue();
                    if (json instanceof JSONObject)
                    {

                        JSONObject loginJsonObject1 = new JSONObject(loginJson);

                        String success = loginJsonObject1.getString("success");
                        String message = loginJsonObject1.getString("message");

                        if (success.equals("1"))
                        {

                            JSONArray jsonArray = loginJsonObject1.getJSONArray("deviceDetails");

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            latitude = jsonObject.getString("latitude");
                            longitude = jsonObject.getString("longitude");
                            speed1 = jsonObject.getString("speed");
                            odometer = jsonObject.getString("odometer");
                            fuel1 = jsonObject.getString("fuel");
                            IGN_STATUS = jsonObject.getString("ignS");
                            POWER_STATUS = jsonObject.getString("powerS");
                            GPS_STATUS = jsonObject.getString("gpsS");
                           // parkLastTime = jsonObject.getString("lt_stop"); //removed from webservice
                           // durLastTime = jsonObject.getString("lt_duration");//removed from webservice
                            lastTripDistane_v = jsonObject.getString("lt_dist");//Last Distance
                            lastTripParkTime_v = jsonObject.getString("lt_stop");//Last Parking
                            lastTripDuration_v = jsonObject.getString("lt_duration");//Last Duration
                            course = jsonObject.getString("course");
                            deviceStatus = jsonObject.getString("deviceStatus");
                            overspeed_value = jsonObject.getString("overspeed_value");
                            last_dt = jsonObject.getString("last_dt");


                            //for running vehicle share location
                            MyApplication.editor.putString(Constants.LATITUDE,latitude).commit();
                            MyApplication.editor.putString(Constants.LONGITUDE,longitude).commit();

                            
                        }

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return deviceStatus;
        }
        @Override
        protected void onPostExecute(String deviceStatus)
        {
            super.onPostExecute(deviceStatus);

            try
            {
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


                float odo = Float.valueOf(odometer);
                int fuel = Integer.valueOf(fuel1);


                arcprogressOdometer.setProgress(odo);
                arcprogressFuel.setProgress(fuel);


                if(lastTripParkTime_v.equals("0:0:0") || lastTripParkTime_v.equals("0"))
                {
                    lastTripParkTime_v = "00:00:00";
                }
                if(lastTripDuration_v.equals("0:0:0") || lastTripDuration_v.equals("0"))
                {
                    lastTripDuration_v = "00:00:00";
                }
                //lastTripDistane.setText(total_dist1+" Km");//last Distance
                lastTripDistane.setText(lastTripDistane_v+" Km");//last Distance

                //lastTripParkTime.setText(parkTotaTime);//Last Parking
                //lastTripDuration.setText(durTotalTime);//Last Duration

                lastTripParkTime.setText(lastTripParkTime_v);//Last Parking
                lastTripDuration.setText(lastTripDuration_v);//Last Duration

                //update last track date time
                activityPlaybackTrackInfo.updateTRackDateTime(last_dt);


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
            catch (IllegalStateException e)
            {

            }
            catch (Exception e)
            {

            }

        }
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

        float odo = Float.valueOf(MyApplication.prefs.getString(Constants.ODOMETER,"0"));
        int fuel = Integer.valueOf(MyApplication.prefs.getString(Constants.FUEL,"0"));


        arcprogressOdometer.setProgress(odo);
        arcprogressFuel.setProgress(fuel);
        lastTripDistane.setText(MyApplication.prefs.getString(Constants.DIST_LAST,"0")+" Km");

        /*lastTripDistane_v = jsonObject.getString("lt_dist");//Last Distance
        lastTripParkTime_v = jsonObject.getString("lt_stop");//Last Parking*/

        lastTripParkTime_v = MyApplication.prefs.getString(Constants.PARK_LAST,"0");//lt_dist
        lastTripDuration_v = MyApplication.prefs.getString(Constants.DUR_LAST,"0");//lt_stop

        if(lastTripParkTime_v.equals("0:0:0") || lastTripParkTime_v.equals("0"))
        {
            lastTripParkTime_v = "00:00:00";
        }
        if(lastTripDuration_v.equals("0:0:0") || lastTripDuration_v.equals("0"))
        {
            lastTripDuration_v = "00:00:00";
        }

        lastTripParkTime.setText(lastTripParkTime_v);
        lastTripDuration.setText(lastTripDuration_v);
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
