package vts.snystems.sns.vts.fragments.monitor;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityLogin;
import vts.snystems.sns.vts.activity.ActivitySplash;
import vts.snystems.sns.vts.activity.HomeActivity;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.fragments.MonitorFragment;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.DeviceInfo;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class MonitorFragmentCluster extends Fragment implements OnMapReadyCallback,ClusterManager.OnClusterItemInfoWindowClickListener<MarkerDialogInfo>
{
    // Declare a variable for the cluster manager.
    private ClusterManager<MarkerDialogInfo> mClusterManager;
    private GoogleMap mMap;
    private MarkerDialogInfo clusterItem;
    private ArrayList<MarkerDialogInfo> DEVICE_DETAILS = new ArrayList<>();
    private String status;

    @BindView(R.id.buttonMapType)
    Button buttonMapType;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    int counterCall = 0;
    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.monitor_fragment_cluster, container, false);
        ButterKnife.bind(this,view);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setListener();
        mHandler = new Handler();
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

    private void setListener() {

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
                            MyApplication.editor.putString(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE, vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_NORMAL).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.normal));
                        }
                        else if(itemTitle.equals("Satellite") || itemTitle.equals("साटलाईट"))
                        {
                            MyApplication.editor.putString(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE, vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_SAT).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.satellite));
                        }
                        else if(itemTitle.equals("Hybrid") || itemTitle.equals("हायब्रीड"))
                        {
                            MyApplication.editor.putString(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE, vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_HY).commit();
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.hybrid));
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
        sosFloating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String SOS_FC = MyApplication.prefs.getString(vts.snystems.sns.vts.interfaces.Constants.SOS_FC,"0");
                String SOS_SC = MyApplication.prefs.getString(vts.snystems.sns.vts.interfaces.Constants.SOS_SC,"0");
                String SOS_TC = MyApplication.prefs.getString(vts.snystems.sns.vts.interfaces.Constants.SOS_TC,"0");

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
            status = "first";
            getDeviceDetails();
            //callHandler();
        }
        else
        {
            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.n_conn))
                    .content(getString(R.string.check_wi))
                    .positiveText(getString(R.string.ok))
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
                            }

                        }
                    })
                    .show();
            //M.s(viewTexTView, VMsg.connection);
        }

    }
    String monitorJson,flagStatus;
    KProgressHUD progressDialog;
    private void getDeviceDetails()
    {

        progressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);
        try
        {

            if(status.equals("first"))
            {
                progressDialog.show();
            }


            String[] parameters =
                    {
                            vts.snystems.sns.vts.interfaces.Constants.USER_NAME + "#" + MyApplication.prefs.getString(vts.snystems.sns.vts.interfaces.Constants.USER_NAME,"0")
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
                            if(status.equals("first"))
                            {
                                progressDialog.dismiss();
                            }
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR(getString(R.string.req_t),getString(R.string.tout));

                            }
                            else
                            {
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.monitor,Function : getDeviceDetails(final String status)");
                            }
                        }
                    },

                    vts.snystems.sns.vts.interfaces.Constants.webUrl + "" + vts.snystems.sns.vts.interfaces.Constants.getVehicleInfo,
                    parameters,
                    getActivity(),
                    status);
        }
        catch (Exception e)
        {
            Log.e("TIMERRR","result : "+e);
        }

    }
    public void sR(String message, String error)
    {

        if(status.equals("first"))
        {
            new MaterialDialog.Builder(getActivity())
                    .title(error)
                    .content(message)
                    .positiveText(getString(R.string.refresh))
                    .negativeText(getString(R.string.cancel_l))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                            getDeviceDetails();
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
            if (!mIsRunning)
            {
                return; // stop when told to stop
            }
            if(F.checkConnection())
            {
                //Log.e("TIMERRR","Callxxx");
                Log.e("Timer","MonitorFragmentCluster timer call");
                status = "second";
                getDeviceDetails();
                cnt = 1;
                //callHandler();
            }
            mHandler.postDelayed(mStatusChecker, vts.snystems.sns.vts.interfaces.Constants.monitorTimerDelay);
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

            if(status.equals("first"))
            {
                progressDialog.dismiss();
            }

            setUpCluster();
            //setVehiclePositions();
            if(counterCall == 0)
            {

                refreshFragment();
            }
            counterCall = 1;
        }


        @Override
        protected String doInBackground(String... urls)
        {
            String id = vts.snystems.sns.vts.interfaces.Constants.NA;
            String imei = vts.snystems.sns.vts.interfaces.Constants.NA;
            String vehicleNumber = vts.snystems.sns.vts.interfaces.Constants.NA;
            String speed = vts.snystems.sns.vts.interfaces.Constants.ZERO;
            String lastTrackedDateTime = vts.snystems.sns.vts.interfaces.Constants.LTDATE_TIME;
            String vehicleType = vts.snystems.sns.vts.interfaces.Constants.NA;
            String latitude = vts.snystems.sns.vts.interfaces.Constants.NA;
            String longitude = vts.snystems.sns.vts.interfaces.Constants.NA;
            String acc_status = vts.snystems.sns.vts.interfaces.Constants.NA;
            String iconColor = vts.snystems.sns.vts.interfaces.Constants.NA;
            String deviceStatus = vts.snystems.sns.vts.interfaces.Constants.NA;
            String course = vts.snystems.sns.vts.interfaces.Constants.ZERO;

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


                                int icon = returnIcon(vehicleType);

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(icon));

                                MarkerDialogInfo offsetItem = new MarkerDialogInfo(
                                        Double.valueOf(latitude),
                                        Double.valueOf(longitude),
                                        vehicleNumber,
                                        deviceStatus,
                                        "NA",
                                        markerOptions
                                );
                                DEVICE_DETAILS.add(offsetItem);
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

    private int returnIcon(String vehicleType) {


        int icon;
        if(vehicleType.equals("MC"))//motorcycle
        {
            icon = R.drawable.ic_bike_map;
        }
        else if(vehicleType.equals("CR"))//car
        {
            icon = R.drawable.ic_car_map;
        }
        else if(vehicleType.equals("TR"))//tractor
        {

            icon = R.drawable.ic_marker_map;
        }
        else if(vehicleType.equals("TK"))//trucks
        {
            icon = R.drawable.ic_truck_map;

        }
        else if(vehicleType.equals("CE"))//crane
        {
            icon = R.drawable.ic_marker_map;

        }
        else if(vehicleType.equals("CN"))//container
        {
            icon = R.drawable.ic_marker_map;

        }
        else if(vehicleType.equals("BL"))//bacoloader
        {
            icon = R.drawable.ic_marker_map;

        }
        else if(vehicleType.equals("TN"))//tanker
        {
            icon = R.drawable.ic_tanker_map;
        }
        else if(vehicleType.equals("BS"))//bus
        {
            icon = R.drawable.ic_bus_map;
        }
        else if(vehicleType.equals("MR"))//marker
        {

            icon = R.drawable.ic_marker_map;
        }
        else if(vehicleType.equals("GC"))
        {

            icon = R.drawable.ic_marker_map;
        }
        else
        {
            icon = R.drawable.ic_marker_map;
        }

        return icon;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;



        //set Map type
        String mType = MyApplication.prefs.getString(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE, vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_NORMAL);

        if(mType.equals(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_NORMAL)|| mType.equals("साधारण") || mType.equals("सामान्य"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.normal));
        }
        else if(mType.equals(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_SAT) || mType.equals("साटलाईट"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.satellite));
        }
        else if(mType.equals(vts.snystems.sns.vts.interfaces.Constants.MAP_TYPE_HY) || mType.equals("हायब्रीड"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            buttonMapType.setText(MyApplication.context.getResources().getString(R.string.hybrid));
        }
        //setUpCluster();


    }
    int cnt = 0;
    private void setUpCluster()
    {


        if(mMap != null)
        {
            mMap.clear();
        }

        LatLng latLng = DEVICE_DETAILS.get(0).getPosition();
        Log.e("Timer","cnt : "+cnt);
        // Position the map.
        if(cnt == 0)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 10));

        }
        else
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), mMap.getCameraPosition().zoom));

        }


        mClusterManager = new ClusterManager<MarkerDialogInfo>(getActivity(), mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerDialogInfo>()
        {
            @Override
            public boolean onClusterItemClick(MarkerDialogInfo item)
            {
                //Toast.makeText(MapsActivity.this, "setOnClusterItemClickListener", Toast.LENGTH_SHORT).show();
                clusterItem = item;
                return false;
            }
        });


        //addItems();
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerAdapter());
        mClusterManager.setRenderer(new OwnIconRendered(getActivity(), mMap, mClusterManager));
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }


    private void addItems()
    {
        //MarkerDialogInfo markerItem = new MarkerItem(markerOptions);
        //clusterManager.addItem(markerItem);

        // Add ten cluster items in close proximity, for purposes of this example.


        for (int i = 0; i < DEVICE_DETAILS.size(); i++)
        {



            MarkerDialogInfo offsetItem1 = DEVICE_DETAILS.get(i);
            LatLng latLng = offsetItem1.getPosition();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Constants.latitude[i], Constants.longitude[i]))
                    .icon(offsetItem1.getIcon());

            MarkerDialogInfo offsetItem = new MarkerDialogInfo(

                    latLng.latitude,
                    latLng.longitude,
                    offsetItem1.getName(),
                    offsetItem1.getStatus(),
                    offsetItem1.getAddress(),
                    markerOptions
            );
            mClusterManager.addItem(offsetItem);
        }




    }
    @Override
    public void onClusterItemInfoWindowClick(MarkerDialogInfo MarkerDialogInfo) {


    }

    public class MarkerAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MarkerAdapter()
        {
            myContentsView = getLayoutInflater().inflate(R.layout.device_info_dialog_cluster, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView vNumberTextView = ((TextView) myContentsView.findViewById(R.id.vNumberTextView));
            TextView deviceStatusTextView = ((TextView) myContentsView.findViewById(R.id.deviceStatusTextView));
            TextView lastLocationTextView = ((TextView) myContentsView.findViewById(R.id.lastLocationTextView));

            vNumberTextView.setText(clusterItem.getName());
            deviceStatusTextView.setVisibility(View.GONE);
            lastLocationTextView.setVisibility(View.GONE);
            deviceStatusTextView.setText(Html.fromHtml("<b>Status : </b>"+clusterItem.getStatus()));
            lastLocationTextView.setText(clusterItem.getAddress());
            /*deviceStatusTextView.setText(clusterItem.getVehicleStatus());

            LatLng latLngAddress = clusterItem.getPosition();
            String lastLocAddress = F.getAddress(latLngAddress.latitude,latLngAddress.longitude);

            if(lastLocAddress.equals("NA"))
            {
                lastLocationTextView.setText(vts.snystems.sns.vts.interfaces.Constants.uLocation);
            }
            else
            {
                lastLocationTextView.setText(lastLocAddress);
            }*/

            // ivPlace.setImageResource(clusterItem.getPlaceType() == PlaceType.RESTAURANT ? R.drawable.ic_food : R.drawable.ic_cafe);

            return myContentsView;
        }
    }


}
