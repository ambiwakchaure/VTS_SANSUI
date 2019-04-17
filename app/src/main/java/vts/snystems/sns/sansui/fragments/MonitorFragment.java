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
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.sansui.R;
import vts.snystems.sns.sansui.classes.CustomInfoWindowGoogleMap;
import vts.snystems.sns.sansui.classes.F;
import vts.snystems.sns.sansui.classes.M;
import vts.snystems.sns.sansui.classes.MyApplication;
import vts.snystems.sns.sansui.interfaces.Constants;
import vts.snystems.sns.sansui.interfaces.VMsg;
import vts.snystems.sns.sansui.pojo.DeviceInfo;
import vts.snystems.sns.sansui.sos.classes.CurrentLatLng;
import vts.snystems.sns.sansui.volley.Rc;
import vts.snystems.sns.sansui.volley.VolleyCallback;
import vts.snystems.sns.sansui.volley.VolleyErrorCallback;


/**
 * Created by sns003 on 21-Mar-18.
 */

public class MonitorFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap mMap;

    @BindView(R.id.viewTexTView)
    TextView viewTexTView;


    @BindView(R.id.buttonMapType)
    Button buttonMapType;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    private ArrayList<DeviceInfo> DEVICE_DETAILS = new ArrayList<>();


    int counterCall = 0;
    Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.monitor_fragment, container, false);
        ButterKnife.bind(this,view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setListner();

        mHandler = new Handler();
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
                        }
                        else if(itemTitle.equals("Satellite") || itemTitle.equals("साटलाईट"))
                        {
                            MyApplication.editor.putString(Constants.MAP_TYPE,Constants.MAP_TYPE_SAT).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.satellite));
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

        getServerDAta();

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

    private void getServerDAta() {

        if(F.checkConnection())
        {
            getDeviceDetails("first");
            //callHandler();
        }
        else
        {
            new MaterialDialog.Builder(getActivity())
                    .title(VMsg.connection)
                    .content("Please check internet connection or wifi connection.")
                    .positiveText("Try again")
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                        {


                            if(F.checkConnection())
                            {
                                dialog.dismiss();
                                getServerDAta();
                                //Log.d("login","----S");
                            }
                            else
                            {
                                dialog.dismiss();
                                M.t("Please check internet connection or wifi connection.");
                            }

                        }
                    })
                    .show();
            //M.s(viewTexTView, VMsg.connection);
        }

    }


    private  void callHandler()
    {
        MyApplication.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getDeviceDetails("handler");
                //Do 1minutes
                MyApplication.handler.postDelayed(this, Constants.TIME_PERIOD);
            }
        }, Constants.TIME_PERIOD);
    }
    int cnt = 0;



    String monitorJson,flagStatus;

    private void getDeviceDetails(final String status)
    {
        try
        {

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" +MyApplication.prefs.getString(Constants.USER_NAME,"0")
                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {
                            monitorJson = result;
                            flagStatus = status;

                            //Log.e("TIMERRR","result : "+result);
                            new ParseResponse().execute();
                           // parseMonitorResponse(result);
                        }
                    },
                    new VolleyErrorCallback()
                    {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.","Timeout Error",status);

                            }
                            else
                            {
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.monitor,Function : getDeviceDetails(final String status)");
                            }
                        }
                    },

                    Constants.webUrl + "" + Constants.getVehicleInfo,
                    parameters,
                    getActivity(),
                    status);
        }
        catch (Exception e)
        {
            Log.e("TIMERRR","result : "+e);
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
                Log.e("Timer","MonitorFragment1 timer call");
                getDeviceDetails("second");

                //callHandler();
            }
            mHandler.postDelayed(mStatusChecker, Constants.monitorTimerDelay);
        }
    };

    public void cancelHandler()
    {
        mIsRunning = false;
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cancelHandler();
    }
    class ParseResponse extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            setVehiclePositions();

            if(counterCall == 0)
            {
                refreshFragment();
            }
            counterCall = 1;
        }

        @Override
        protected String doInBackground(String... urls)
        {

            String id = Constants.NA;
            String imei = Constants.NA;
            String vehicleNumber = Constants.NA;
            String speed = Constants.ZERO;
            String lastTrackedDateTime = Constants.LTDATE_TIME;
            String vehicleType = Constants.NA;
            String latitude = Constants.NA;
            String longitude = Constants.NA;
            String acc_status = Constants.NA;
            String iconColor = Constants.NA;
            String deviceStatus = Constants.NA;
            String course = Constants.ZERO;

            try
            {

                if (monitorJson != null || monitorJson.length() > 0)
                {
                    Object json = new JSONTokener(monitorJson).nextValue();
                    if (json instanceof JSONObject)
                    {

                        JSONObject loginJsonObject = new JSONObject(monitorJson);

                        String success = loginJsonObject.getString("success");
                        final String message = loginJsonObject.getString("message");

                        if(success.equals("1"))
                        {
                            if(!DEVICE_DETAILS.isEmpty())
                            {
                                DEVICE_DETAILS.clear();
                            }
                            JSONArray monitorJsonArray = loginJsonObject.getJSONArray("deviceDetails");

                            for(int i = 0; i < monitorJsonArray.length(); i++)
                            {

                                JSONObject mmJsonObject = monitorJsonArray.getJSONObject(i);

                                if(mmJsonObject.has("id") && !mmJsonObject.isNull("id"))
                                {
                                    id = mmJsonObject.getString("id");
                                }
                                if(mmJsonObject.has("imei") && !mmJsonObject.isNull("imei"))
                                {
                                    imei = mmJsonObject.getString("imei");
                                }
                                if(mmJsonObject.has("vehicleNumber") && !mmJsonObject.isNull("vehicleNumber"))
                                {
                                    vehicleNumber = mmJsonObject.getString("vehicleNumber");
                                }
                                if(mmJsonObject.has("speed") && !mmJsonObject.isNull("speed"))
                                {
                                    speed = mmJsonObject.getString("speed");
                                }
                                if(mmJsonObject.has("lastTrackedDateTime") && !mmJsonObject.isNull("lastTrackedDateTime"))
                                {
                                    lastTrackedDateTime = mmJsonObject.getString("lastTrackedDateTime");
                                }
                                if(mmJsonObject.has("vehicleType") && !mmJsonObject.isNull("vehicleType"))
                                {
                                    vehicleType = mmJsonObject.getString("vehicleType");
                                }
                                if(mmJsonObject.has("latitude") && !mmJsonObject.isNull("latitude"))
                                {
                                    latitude = mmJsonObject.getString("latitude");
                                }
                                if(mmJsonObject.has("longitude") && !mmJsonObject.isNull("longitude"))
                                {
                                    longitude = mmJsonObject.getString("longitude");
                                }
                                if(mmJsonObject.has("acc_status") && !mmJsonObject.isNull("acc_status"))
                                {
                                    acc_status = mmJsonObject.getString("acc_status");
                                }
                                if(mmJsonObject.has("iconColor") && !mmJsonObject.isNull("iconColor"))
                                {
                                    iconColor = mmJsonObject.getString("iconColor");
                                }
                                if(mmJsonObject.has("deviceStatus") && !mmJsonObject.isNull("deviceStatus"))
                                {
                                    deviceStatus = mmJsonObject.getString("deviceStatus");
                                }
                                if(mmJsonObject.has("course") && !mmJsonObject.isNull("course"))
                                {
                                    course = mmJsonObject.getString("course");
                                }


                                DeviceInfo deviceInfo = new DeviceInfo();

                                deviceInfo.setId(id);
                                deviceInfo.setVehicleIMEI(imei);
                                deviceInfo.setVehicleNumber(vehicleNumber);
                                deviceInfo.setVehicleSpeed(speed);
                                deviceInfo.setLastTrackedTime(lastTrackedDateTime);
                                deviceInfo.setVehicleType(vehicleType);
                                deviceInfo.setCoOrdinate(latitude+" "+longitude);
                                deviceInfo.setAccStatus(acc_status);
                                deviceInfo.setLastLocation(latitude+" "+longitude);
                                deviceInfo.setDeviceStatus(deviceStatus);
                                deviceInfo.setColorName(iconColor);
                                deviceInfo.setCourse(course);

                                deviceInfo.setLocationVisibleStatus("no");

                                DEVICE_DETAILS.add(deviceInfo);

                            }



                        }
                        else if(success.equals("3") || success.equals("0"))
                        {
                            if(flagStatus.equals("first"))
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        M.t(message);
                                    }
                                });
                            }


                        }

                    }
                }

            }
            catch (Exception e)
            {
              // M.t(""+e);
                e.printStackTrace();
            }

            return null;
        }
    }


    public void sR(String message, String error, final String status)
    {

        if(status.equals("first"))
        {
            new MaterialDialog.Builder(getActivity())
                    .title(error)
                    .content(message)
                    .positiveText("Try again")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                            getDeviceDetails(status);
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
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        //set google map camera position by default on india
        mMap = googleMap;

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

        LatLng india = new LatLng(28.704059, 77.102490);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 4));

        /*try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.my_json));

            if (!success) {
                Log.e(">>", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(">>", "Can't find style. Error: ", e);
        }*/

    }
    private void setVehiclePositions()
    {

         try
         {

             mMap.clear();

             LatLngBounds.Builder builder = new LatLngBounds.Builder();

             for(int i = 0; i < DEVICE_DETAILS.size(); i++)
             {
                 DeviceInfo deviceInfo = DEVICE_DETAILS.get(i);

                 String vehicleType = deviceInfo.getVehicleType();
                 String deviceStatus = deviceInfo.getDeviceStatus();
                 String course = deviceInfo.getCourse();
                 String [] latLong = deviceInfo.getCoOrdinate().split(" ");

                 Marker marker = null;

                 if(vehicleType.equals("MC"))//motorcycle
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
                     }
                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();
                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("CR"))//car
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();

                     builder.include(marker.getPosition());
                 }
                 else if(vehicleType.equals("TR"))//tractor
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     //marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("TK"))//trucks
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     //marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("CE"))//crane
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();
                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("CN"))//container
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))

                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("BL"))//bacoloader
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("TN"))//tanker
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     //marker.showInfoWindow();

                     builder.include(marker.getPosition());
                 }
                 else if(vehicleType.equals("BS"))//bus
                 {
                     if(deviceStatus.equals("MV"))//moving
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
                     }
                     else if(deviceStatus.equals("ST"))//static
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
                     }
                     else if(deviceStatus.equals("SP"))//static(parking)
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
                     }
                     else if(deviceStatus.equals("OFF"))//offline
                     {
                         marker = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
                     }

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     //marker.showInfoWindow();

                     builder.include(marker.getPosition());
                 }
                 else if(vehicleType.equals("MR"))//marker
                 {
                    if(deviceStatus.equals("MV"))//moving
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("ST"))//static
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("SP"))//static(parking)
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("OFF"))//offline
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     //marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else if(vehicleType.equals("GC"))
                 {
                    if(deviceStatus.equals("MV"))//moving
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("ST"))//static
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("SP"))//static(parking)
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                    else if(deviceStatus.equals("OFF"))//offline
                    {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                    }
                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                    // marker.showInfoWindow();

                     builder.include(marker.getPosition());

                 }
                 else
                 {
                     marker = mMap.addMarker(new MarkerOptions()
                             .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                             .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

                     marker.setTag(deviceInfo);
                     marker.setRotation(Float.valueOf(course));
                     builder.include(marker.getPosition());
                 }

             }

             CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
             mMap.setInfoWindowAdapter(customInfoWindow);

             LatLngBounds bounds = builder.build();

             int width = MyApplication.context.getResources().getDisplayMetrics().widthPixels;
             int height = MyApplication.context.getResources().getDisplayMetrics().heightPixels;
             int padding = (int) (height * 0.25); // offset from edges of the map 12% of screen


             CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
             mMap.moveCamera(cu);


         }
         catch (Exception e)
         {
             M.t(""+e);
         }





    }
}
