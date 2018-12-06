package vts.snystems.sns.vts.geofence.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.V;
import vts.snystems.sns.vts.classes.Validate;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.classes.AppUtils;
import vts.snystems.sns.vts.geofence.classes.FetchIntentAddressService;
import vts.snystems.sns.vts.interfaces.Constants;


public class ActivityCreateGeofence extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    @BindView(R.id.floatingAddGeofence)
    FloatingActionButton floatingAddGeofence;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION",imei,vNumber;
    Context mContext;
//    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    private Circle circle;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput,vLatLong,vType;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    PlaceAutocompleteFragment placeAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        ButterKnife.bind(this);

        mContext = this;



        /*getSupportActionBar().setTitle("Select Location ");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {

                LatLng queriedLocation = place.getLatLng();
                //String sourceLatLong = queriedLocation.latitude + "," + queriedLocation.longitude;

                //M.t(sourceLatLong);

                if(mMap != null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(queriedLocation, mMap.getCameraPosition().zoom));
                }
            }

            @Override
            public void onError(Status status)
            {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_geo_fence);

        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            imei = bundle.getString(Constants.IMEI);
            vNumber = bundle.getString(Constants.VEHICLE_NUMBER);
            vLatLong = bundle.getString(Constants.LAT_LONG);
            vType = bundle.getString(Constants.VTYPE);
        }

        if (!permissionsGranted())
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices())
        {

            if (!AppUtils.isLocationEnabled(mContext))
            {

                MaterialDialog md = new MaterialDialog.Builder(ActivityCreateGeofence.this)
                        .title("Location Setting")
                        .content("Oops ! location not enabled, please click on  Open Location Setting button to enable it.")
                        .positiveText("Open location settings")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {
                                dialog.dismiss();
                            }
                        }).show();
                Typeface tf = Typeface.createFromAsset(ActivityCreateGeofence.this.getAssets(), "TitilliumWeb-Regular.ttf");
                md.getTitleView().setTypeface(tf);
                md.getContentView().setTypeface(tf);
                md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
                md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
                md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
                md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);



            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }
    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //doLocationAccessRelatedJob();
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
                //startInstalledAppDetailsActivity();
            }
        }
    }
    public void markerclick(final double lat_value, final double long_value) {


        MaterialDialog md = new MaterialDialog.Builder(ActivityCreateGeofence.this)
                .title(R.string.select_location)
                .content(R.string.geo_perm)
                .positiveText(R.string.ok)
                .negativeText(R.string.c_loc)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {


                        set_geo_fence_name(lat_value, long_value);


                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {

                        dialog.dismiss();
                    }
                }).show();
        Typeface tf = Typeface.createFromAsset(ActivityCreateGeofence.this.getAssets(), "TitilliumWeb-Regular.ttf");
        md.getTitleView().setTypeface(tf);
        md.getContentView().setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
        md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
        md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);

    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition cameraPosition)
            {
                mCenterLatLong = cameraPosition.target;
                try
                {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
        {
            public void onMapLoaded()
            {
                //do stuff here
                floatingAddGeofence.setVisibility(View.VISIBLE);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            return;
        }



        if(vLatLong.contains(","))
        {
            String [] dataltlng = vLatLong.split(",");

            if(dataltlng.length == 2)
            {
                String lat = dataltlng[0];
                String lng = dataltlng[1];

                if(!V.checkNull(lat))
                {
                    return;
                }
                if(!V.checkNull(lng))
                {
                    return;
                }
                LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                F.setMarkerVehicleIconType(vType,mMap,latlng,Float.valueOf(MyApplication.prefs.getString(Constants.COURSE,"0")));

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(latlng, 15));
            }
            else
            {
                M.t("Vehicle latitude longitude not found");
            }

        }
        else
        {
            M.t("Vehicle latitude longitude not found");
        }

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try
        {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location)
    {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }

        if (mMap != null)
        {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(15f).build();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            startIntentService(location);


        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void select_this_location(View view)
    {
        markerclick(mCenterLatLong.latitude, mCenterLatLong.longitude);
    }

    public void goBack(View view) {

        finish();
    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
            }
        }

    }

    protected void displayAddressOutput() {
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                Log.d(">>", ">>" + mAreaOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawGeofence(LatLng latLng, int radiousInMtr) {

        CircleOptions circleOptions = new CircleOptions()

                .center(latLng)
                .radius(radiousInMtr)
                .fillColor(Color.parseColor("#51000000"))
                .strokeColor(Color.RED)
                .strokeWidth(2);
        circle = mMap.addCircle(circleOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    protected void startIntentService(Location mLocation) {

        Intent intent = new Intent(this, FetchIntentAddressService.class);
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        startService(intent);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

                    strReturnedAddress.append(returnedAddress.getAddressLine(i));

                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current", "Canont get Address!" + e.getMessage());
        }
        return strAdd;
    }

    public void set_geo_fence_name(final double lat_value, final double long_value) {
        LayoutInflater li = LayoutInflater.from(ActivityCreateGeofence.this);
        View promptsView = li.inflate(R.layout.activity_set_geo_fence_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityCreateGeofence.this);

        alertDialogBuilder.setView(promptsView);

        final EditText et_geofence_name = (EditText) promptsView.findViewById(R.id.et_geofence_name);
        final CheckBox checkbox_home = (CheckBox) promptsView.findViewById(R.id.checkbox_home);
        final CheckBox checkbox_school = (CheckBox) promptsView.findViewById(R.id.checkbox_school);
        final CheckBox checkbox_work = (CheckBox) promptsView.findViewById(R.id.checkbox_work);

        et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

        checkbox_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked()) {

                    et_geofence_name.setText("Home");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_school.setEnabled(false);
                    checkbox_work.setEnabled(false);
                }
                else {

                    et_geofence_name.getText().clear();
                    checkbox_school.setEnabled(true);
                    checkbox_work.setEnabled(true);
                }
            }
        });

        checkbox_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked()) {

                    et_geofence_name.setText("School");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_home.setEnabled(false);
                    checkbox_work.setEnabled(false);

                } else {

                    et_geofence_name.getText().clear();
                    checkbox_home.setEnabled(true);
                    checkbox_work.setEnabled(true);
                }
            }
        });

        checkbox_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked())
                {

                    et_geofence_name.setText("Work");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_school.setEnabled(false);
                    checkbox_home.setEnabled(false);
                }
                else
                {

                    et_geofence_name.getText().clear();
                    checkbox_school.setEnabled(true);
                    checkbox_home.setEnabled(true);
                }
            }
        });

             alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(!Validate.validateEmptyField(et_geofence_name,"Warning ! please enter geofence name"))
                        {
                            return;
                        }

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(lat_value, long_value));
                        markerOptions.title(lat_value + " : " + long_value);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mCenterLatLong.latitude, mCenterLatLong.longitude)));
                        mMap.addMarker(markerOptions);
                        drawGeofence(new LatLng(lat_value, long_value), 1000);

                        TABLE_STORE_GEOFENCE.storeGeofence
                                (
                                        imei,
                                        vNumber,
                                        lat_value + "," + long_value,
                                        String.valueOf(1),
                                        "on",
                                        et_geofence_name.getText().toString()
                                );
                        finish();



                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Typeface tf = Typeface.createFromAsset(ActivityCreateGeofence.this.getAssets(), "TitilliumWeb-Regular.ttf");
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(tf);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(tf);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);

    }
}
