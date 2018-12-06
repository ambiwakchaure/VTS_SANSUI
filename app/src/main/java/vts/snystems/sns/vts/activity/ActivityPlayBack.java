package vts.snystems.sns.vts.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.fonts.MyTextViewNormal;
import vts.snystems.sns.vts.fonts.MyTextViewSmiBold;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.PlaybackSpeedInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class ActivityPlayBack extends AppCompatActivity implements OnMapReadyCallback {


    SupportMapFragment mapFragment;

    @BindView(R.id.vNumber)
    MyTextViewSmiBold vNumber;
    @BindView(R.id.lastDateTime)
    MyTextViewNormal lastDateTime;
    private GoogleMap mMap;

    @BindView(R.id.speedoLayout)
    LinearLayout speedoLayout;

    @BindView(R.id.speedLayout)
    FrameLayout speedLayout;

    @BindView(R.id.speedFlotButton)
    FloatingActionButton speedFlotButton;


    @BindView(R.id.playbackSpeedText)
    TextView playbackSpeedText;


    @BindView(R.id.playbackDistance)
    TextView playbackDistance;

    @BindView(R.id.playbackDateTime)
    TextView playbackDateTime;


    @BindView(R.id.buttonSpeedLive)
    Button buttonSpeedLive;

    @BindView(R.id.buttonMapType)
    Button buttonMapType;

    @BindView(R.id.speedTextView)
    TextView speedTextView;

    Marker sourceMarker, destinationMarker;

    LatLng newPos;
    private Marker marker;
    private float v;
    private double lat, lng;
    private LatLng startPosition, endPosition;
    private int index, next;
    private int internl;
    private LatLng first, second;
    ArrayList<PlaybackSpeedInfo> PLAYBACK_SPEED_DATA = new ArrayList<>();
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    double route_distance = 0;
    boolean isMarkerRotating = false;
    String DEVICE_STATUS,plDistance;

    Handler mHandlerStartTimer;
    Handler mHandlersetZhoom;
    Handler mHandlerdistanceTimer;
    Handler mHandlerspeedTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        mHandlerStartTimer = new Handler();
        mHandlersetZhoom = new Handler();
        mHandlerdistanceTimer = new Handler();
        mHandlerspeedTimer = new Handler();

        internl = 100;
        ButterKnife.bind(this);
        speedTextView.setText("x8");
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_playback);
        mapFragment.getMapAsync(this);
        setListners();
        String VEHICLE_NUMBER = MyApplication.prefs.getString(Constants.VEHICLE_NUMBER, "0");
        String LAST_UPDATE_DATE_TIME = MyApplication.prefs.getString(Constants.LAST_UPDATE_DATE_TIME, "0");
        String IMEI = MyApplication.prefs.getString(Constants.IMEI, "0");

        DEVICE_STATUS = MyApplication.prefs.getString(Constants.DEVICE_STATUS, "CR");

        vNumber.setText(VEHICLE_NUMBER);
        String [] data = LAST_UPDATE_DATE_TIME.split(" ");
        lastDateTime.setText("Last Track : "+F.parseDate(data[0],"Year")+" "+data[1]);

        Bundle bundle = getIntent().getExtras();

        String FROM_DATE = bundle.getString(Constants.FROM_DATE);
        String TO_DATE = bundle.getString(Constants.TO_DATE);
        plDistance = bundle.getString(Constants.PLAY_DIST);


        if (F.checkConnection())
        {
            //default three days data current date and previoust two days date -2
            getDateWisePlayBack(IMEI,FROM_DATE, TO_DATE);

        } else {
            M.t(VMsg.connection);
        }
    }
    private void setListners()
    {
        buttonMapType.setText("Normal");
        buttonMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(ActivityPlayBack.this, buttonMapType);
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

                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            buttonMapType.setText(getResources().getString(R.string.normal));
                        }
                        else if(itemTitle.equals("Satellite") || itemTitle.equals("साटलाईट"))
                        {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            buttonMapType.setText(getResources().getString(R.string.satellite));
                        }
                        else if(itemTitle.equals("Hybrid") || itemTitle.equals("हायब्रीड"))
                        {
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            buttonMapType.setText(getResources().getString(R.string.hybrid));
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        //set google map camera position by default on india
        mMap = googleMap;


        F.setDefaultMap(mMap);

        //set Map type
        String mType = MyApplication.prefs.getString(Constants.MAP_TYPE,Constants.MAP_TYPE_NORMAL);

        if(mType.equals(Constants.MAP_TYPE_NORMAL)|| mType.equals("साधारण") || mType.equals("सामान्य"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            buttonMapType.setText(getResources().getString(R.string.normal));
        }
        else if(mType.equals(Constants.MAP_TYPE_SAT) || mType.equals("साटलाईट"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            buttonMapType.setText(getResources().getString(R.string.satellite));
        }
        else if(mType.equals(Constants.MAP_TYPE_HY) || mType.equals("हायब्रीड"))
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            buttonMapType.setText(getResources().getString(R.string.hybrid));
        }
    }

    String response_lat_lang;
    KProgressHUD progressDialog;
    private void getDateWisePlayBack(final String imei, final String fromDateTime, final String toDateTime) {

        try
        {




            String[] parameters =
                    {
                            //Constants.IMEI + "#" +imei,

                            Constants.IMEI + "#" +imei,
                            Constants.FROM_DATE + "#" + fromDateTime,
                            Constants.TO_DATE + "#" + toDateTime

                    };

           /* for (int i = 0; i < parameters.length; i++)
            {
                Log.e("PARAMS",""+parameters[i]);
            }*/

            progressDialog = KProgressHUD.create(ActivityPlayBack.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDimAmount(0.5f);
            progressDialog.show();

            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {


                            response_lat_lang = result;
                            //response_lat_lang = Constants.JSON;
                            new ParseResponse().execute();

                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                            progressDialog.dismiss();
                            //Log.e("RESLTLAT_LONG",""+volleyErrr);
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Warning ! Server not responding or no connection.", "Timeout Error", fromDateTime, toDateTime,imei);

                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityPlayBack.this,"Webservice : Constants.getLatLong1,Function : getDateWisePlayBack(final String imei, final String fromDateTime, final String toDateTime)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.getAllLatLng,
                    parameters,
                    ActivityPlayBack.this, "first");


        } catch (Exception e) {
            Log.d("playback", "----error" + e);
        }
    }
    ArrayList<LatLng> received_markers = new ArrayList<>();
    class ParseResponse extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... urls)
        {


            String successString = null;
            try
            {
                String latitude = Constants.NA;
                String longitude = Constants.NA;
                String speed = Constants.NA;
                String created_date = Constants.NA;
                String course = Constants.NA;

                if (response_lat_lang != null || response_lat_lang.length() > 0)
                {
                    Object json = new JSONTokener(response_lat_lang).nextValue();
                    if (json instanceof JSONObject)
                    {
                        JSONObject jsonResponse = new JSONObject(response_lat_lang);

                        successString = jsonResponse.getString("status");
                        if (successString.equals("1"))
                        {
                            JSONArray jsonArray = jsonResponse.getJSONArray("lat_long");
                            //markers_route.clear();

                            if (jsonArray.length() > 0)
                            {

                                for (int i = 0; i < jsonArray.length(); i++)
                                {

                                    JSONObject tempJson1 = jsonArray.getJSONObject(i);

                                    if(tempJson1.has("latitude") && !tempJson1.isNull("latitude"))
                                    {
                                        latitude = tempJson1.getString("latitude");
                                    }

                                    if(tempJson1.has("longitude") && !tempJson1.isNull("longitude"))
                                    {
                                        longitude = tempJson1.getString("longitude");
                                    }

                                    if(tempJson1.has("speed") && !tempJson1.isNull("speed"))
                                    {
                                        speed = tempJson1.getString("speed");
                                    }
                                    if(tempJson1.has("created_date") && !tempJson1.isNull("created_date"))
                                    {
                                        created_date = tempJson1.getString("created_date");
                                    }
                                    if(tempJson1.has("course") && !tempJson1.isNull("course"))
                                    {
                                        course = tempJson1.getString("course");
                                    }

                                    PlaybackSpeedInfo speedInfo = new PlaybackSpeedInfo();

                                    if(latitude != Constants.NA && longitude != Constants.NA)
                                    {

                                        Double speed_d = Double.valueOf(speed);
                                        if(speed_d > 0)
                                        {
                                            LatLng p = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                            received_markers.add(p);
                                        }


                                    }


                                    if(latitude != Constants.NA && longitude != Constants.NA  && speed != Constants.NA)
                                    {
                                        if(speed != null && speed != "NA" && speed != "")
                                        {
                                            Double speed_d = Double.valueOf(speed);
                                            if(speed_d > 0)
                                            {

                                                speedInfo.setLatLng(latitude + "," + longitude);
                                                speedInfo.setSpeed(String.valueOf(Math.round(speed_d)));
                                                speedInfo.setCreated_date(created_date);
                                                speedInfo.setCourse(course);
                                                PLAYBACK_SPEED_DATA.add(speedInfo);
                                            }
                                        }
                                    }

                                }
                                //markers_route = F.getWayPoints(received_markers);

                            }
                        }
                    }
                    else
                    {
                        successString = "2";
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return successString;
        }
        @Override
        protected void onPostExecute(String successString)
        {
            super.onPostExecute(successString);

            if(successString.equals("1"))
            {
                //speedButton.setEnabled(true);
                //speedoDistanceLAyout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                speedoLayout.setVisibility(View.VISIBLE);
                playbackDateTime.setVisibility(View.VISIBLE);
                speedLayout.setVisibility(View.VISIBLE);
                playbackDistance.setText(plDistance+" km");
                playRoute();
            }
            else if(successString.equals("0"))
            {
                F.displayDialog(ActivityPlayBack.this, "Oops...", "Warning ! playback details not found.");
            }
            else if(successString.equals("2"))
            {
                try
                {
                    String errorReport = response_lat_lang.replaceAll("\\<.*?>","").replaceAll("\n","");
                    String errorLog = F.getErrorJson(errorReport);
                    if(errorLog != null)
                    {
                        Intent i = new Intent(ActivityPlayBack.this, ErrorActivity.class);
                        i.putExtra("error",errorReport);
                        i.putExtra("json","Location :  TodayDistFragment.java getAlldata() Exception : "+errorLog);
                        startActivity(i);
                        finish();
                    }
                }
                catch (Exception e)
                {

                }
            }




        }
    }

    public void sR(String message, String error, final String fromDateTime, final String toDateTime, final String imei) {
        new MaterialDialog.Builder(ActivityPlayBack.this)
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        getDateWisePlayBack(imei,fromDateTime, toDateTime);
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


    private void distanceTimer()
    {
        mIsRunningdistanceTimer = true;
        mStatusCheckerdistanceTimer.run();


    }

    boolean mIsRunningdistanceTimer;
    Runnable mStatusCheckerdistanceTimer = new Runnable() {
        @Override
        public void run()
        {
            if (!mIsRunningdistanceTimer) {
                return; // stop when told to stop
            }
            setDistance();
            LatLng destinationMarker = received_markers.get(received_markers.size() - 1);
            LatLng currenMarker = marker.getPosition();

            String destinationMarkerr = destinationMarker.latitude + "," + destinationMarker.longitude;
            String currenMarkerr = currenMarker.latitude + "," + currenMarker.longitude;


            // Log.e("MARKER_DATA","DEST : "+destinationMarker+" CURR : "+currenMarker);


            if (destinationMarkerr.equals(currenMarkerr))
            {

                //stop camera zhoom timer
                cancelHandlermHandlersetZhoom();
                cancelHandlerspeedTimer();
                //gauge1.moveToValue(0);
                //gauge1.setLowerText("0.0 km/h");
                //playbackSpeedText.setText("000");
                String overSpeeed = MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED,"0");
                F.setSpeedo("000",overSpeeed,speedoLayout,playbackSpeedText);
                //playbackDistance.setText("000 km");

            }
            /*if (currenMarkerr.equals(currenMarkerr))
            {
                //stop camera zhoom timer
                cancelHandlermHandlersetZhoom();
                cancelHandlerspeedTimer();

                //gauge1.moveToValue(0);
                //gauge1.setLowerText("0.0 km/h");
                //playbackSpeedText.setText("000");
                String overSpeeed = MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED,"0");
                F.setSpeedo("000",overSpeeed,speedoLayout,playbackSpeedText);
                //playbackDistance.setText("000 km");
            }*/

            String markerLatLong = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            String speed = returnSpeed(markerLatLong);
            Log.e("SPEEDDATAA", "" + speed);
            buttonSpeedLive.setText(speed);
            mHandlerdistanceTimer.postDelayed(mStatusCheckerdistanceTimer, 1000);
        }
    };



    public void cancelHandlerdistanceTimer()
    {
        mIsRunningdistanceTimer = false;
        mHandlerdistanceTimer.removeCallbacks(mStatusCheckerdistanceTimer);
    }


    private void speedTimer()
    {
        mIsRunningspeedTimer = true;
        mStatusCheckerspeedTimer.run();
    }

    boolean mIsRunningspeedTimer;
    Runnable mStatusCheckerspeedTimer = new Runnable()
    {
        @Override
        public void run()
        {
            if (!mIsRunningspeedTimer)
            {
                return; // stop when told to stop
            }
            String markerLatLong1 = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            String markerLatLong = markerLatLong1;

            //if (markerLatLong.length() == 7)
           // {
                if(markerLatLong != null && markerLatLong != "NA" && markerLatLong.length() > 0)
                {
                    String speed = returnSpeed(markerLatLong);

                    if (speed != null)
                    {
                        //staticGuage(Float.valueOf(speed));

                        String overSpeeed = MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED,"0");
                        F.setSpeedo(speed,overSpeeed,speedoLayout,playbackSpeedText);
                        //playbackSpeedText.setText(speed);
                    }
                }
                /*else
                {
                    playbackSpeedText.setText("000");
                }*/

           // }

            mHandlerspeedTimer.postDelayed(mStatusCheckerspeedTimer, 500);
        }
    };
    public void cancelHandlerspeedTimer()
    {
        mIsRunningspeedTimer = false;
        mHandlerspeedTimer.removeCallbacks(mStatusCheckerspeedTimer);
    }

    private String returnSpeed(String markerLatLong)
    {
        String speedData = null;
        try
        {
            for (int i = 0; i < PLAYBACK_SPEED_DATA.size(); i++)
            {
                PlaybackSpeedInfo info = PLAYBACK_SPEED_DATA.get(i);
                //String latlong = info.getLatLng().substring(0, 7);
                String latlong = info.getLatLng();
                String speed = info.getSpeed();
                String createdDate = info.getCreated_date();



                    if (markerLatLong.contains(latlong))
                    {
                        String [] data = createdDate.split(" ");
                       // String [] latLongdata = latlong.split(",");

                        String date_tiem = F.parseDate(data[0],"Year")+"  "+data[1]+"  ";
                        playbackDateTime.setText(date_tiem);
                        //Log.e("PLAY_BACK_TIME","latlong : "+latlong+" markerLatLong : "+markerLatLong+" date time  : "+date_tiem);
                        speedData = speed;

                    }



        }
        }
        catch (Exception e)
        {

            Log.e("ADDRESS DATA","Address : "+e);
        }
        return speedData;
    }
    private void setZhoom()
    {
        mIsRunningsetZhoom = true;
        mStatusCheckersetZhoom.run();
    }

    boolean mIsRunningsetZhoom;
    Runnable mStatusCheckersetZhoom = new Runnable()
    {
        @Override
        public void run()
        {
            if (!mIsRunningsetZhoom)
            {
                return; // stop when told to stop
            }

            Log.e("CAM", "" + mMap.getCameraPosition().zoom);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom);
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newPos, Constants.plaback_cam_zhoom_lvl);
            mMap.animateCamera(cameraUpdate);

            mHandlersetZhoom.postDelayed(mStatusCheckersetZhoom, 3000);
        }
    };

    public void cancelHandlermHandlersetZhoom()
    {
        mIsRunningsetZhoom = false;
        mHandlersetZhoom.removeCallbacks(mStatusCheckersetZhoom);
    }

    private void startTimer()
    {
        mIsRunning = true;
        mStatusChecker.run();
        setZhoom();
        distanceTimer();
        //speedTimer();
    }

    boolean mIsRunning;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run()
        {
            if (!mIsRunning) {
                return; // stop when told to stop
            }
            if (index < received_markers.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < received_markers.size() - 1)
            {
                startPosition = received_markers.get(index);
                endPosition = received_markers.get(next);

                Log.d("newPos>>>>>", "nnnn>1>>" + startPosition);
                Log.d("newPos>>>>>", "nnnn>2>>" + endPosition);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(internl);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                    newPos = new LatLng(lat, lng);

                    Log.d("newPos>>>>>", "newPos>lng>>" + lng + "lat>>" + lat);

                    if (mMap != null)
                    {



                        try
                        {
                            PlaybackSpeedInfo playbackSpeedInfo = PLAYBACK_SPEED_DATA.get(index);
                            marker.setPosition(newPos);
                            marker.setAnchor(0.5f, 0.5f);
                            //float bearing = (float) F.getBearing(startPosition,newPos);

                            String overSpeeed = MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED,"0");
                            F.setSpeedo(playbackSpeedInfo.getSpeed(),overSpeeed,speedoLayout,playbackSpeedText);
                            String [] data = playbackSpeedInfo.getCreated_date().split(" ");
                            // String [] latLongdata = latlong.split(",");
                            String date_tiem = F.parseDate(data[0],"Year")+"  "+data[1]+"  ";
                            playbackDateTime.setText(date_tiem);
                            marker.setRotation(Float.valueOf(playbackSpeedInfo.getCourse()));
                        }
                        catch (Exception e)
                        {

                        }
                    }
                    else
                    {
                        M.t("Null map");
                    }

//                                marker.setPosition(newPos);
//                                marker.setAnchor(0.5f, 0.5f);
//                              //  rotateMarker(marker,getBearing(startPosition, newPos));
//                                marker.setRotation(getBearing(startPosition, newPos));

                }
            });
            if (received_markers.size() > 0) {
                if (received_markers.get(received_markers.size() - 1) == endPosition) {
                    valueAnimator.cancel();
                } else {
                    valueAnimator.start();
                }
            }
            mHandlerStartTimer.postDelayed(mStatusChecker, internl);
        }
    };

    public void cancelHandler()
    {
        mIsRunning = false;
        mHandlerStartTimer.removeCallbacks(mStatusChecker);
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

    private void rotateMarker(final Marker marker, final float toRotation)
    {
        if (!isMarkerRotating)
        {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }
    double distanceD;
    private void setDistance() {


        try {
            first = second;
            second = marker.getPosition();


            if (!(first == null || second == null))
            {
                distanceD = F.getDistance(first, second);
            }

            double distanceKm = distanceD / 1000;

            route_distance += distanceKm;

            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat decimalFormat = (DecimalFormat)nf;
            decimalFormat.applyPattern("##.#");

            double rrdgf = Double.valueOf(decimalFormat.format(route_distance));

            //gauge1.setUpperText(new DecimalFormat("##.##").format(route_distance)+"Km");

            // buttonDistance.setTypeface(tfDistance);


            String distance = decimalFormat.format(rrdgf);
            //buttonDistance.setText("" + distance);
            //playbackSpeedText.setText(""+distance);
            playbackDistance.setText(distance+" km");
        } catch (Exception e)
        {
            Log.e("DAAAAAA","Exception : "+e);
        }
    }



    public void playRoute()
    {
        mMap.clear();
        //Zoom in, animating the camera.
        mMap.moveCamera(CameraUpdateFactory.zoomIn());

        //Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        //Adjusting bounds
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : received_markers)
        {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.moveCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(7);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(received_markers);
        greyPolyLine = mMap.addPolyline(polylineOptions);

        blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(7);
        blackPolylineOptions.color(Color.BLUE);
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);

        //source marker
        sourceMarker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0)));
        //destination marker
        destinationMarker = mMap.addMarker(new MarkerOptions().position(received_markers.get(received_markers.size() - 1)));

        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(1000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();


        String vehicleType = MyApplication.prefs.getString(Constants.VTYPE, "0");
        if (vehicleType.equals("MC"))//motorcycle
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
        }
        else if (vehicleType.equals("CR"))//car
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
        }
        else if (vehicleType.equals("TR"))//tractor
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        else if (vehicleType.equals("TK"))//trucks
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
        }
        else if (vehicleType.equals("CE"))//crane
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))

                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        else if (vehicleType.equals("CN"))//container
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))

                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        else if (vehicleType.equals("BL"))//bacoloader
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))


                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        else if (vehicleType.equals("TN"))//tanker
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
        }
        else if (vehicleType.equals("BS"))//bus
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
        }
        else if (vehicleType.equals("MR"))//marker
        {
            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        else
        {

            marker = mMap.addMarker(new MarkerOptions().position(received_markers.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
        }
        //set map zhoom level
        zoomRoute(mMap,received_markers);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sourceMarker.getPosition(), 14);
        //mMap.moveCamera(cameraUpdate);

        //setFirstZhoom();
        index = -1;
        next = 1;
        //animateMarker();
        startTimer();
    }
    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }
    public void goBack(View view)
    {
        //cancelTimer();
        cancelHandler();
        cancelHandlermHandlersetZhoom();
       // cancelHandlerspeedTimer();
        cancelHandlerdistanceTimer();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //cancelTimer();
        cancelHandler();
        cancelHandlermHandlersetZhoom();
       // cancelHandlerspeedTimer();
        cancelHandlerdistanceTimer();
        finish();
    }

    public void speedSet(View view)
    {

        PopupMenu popup = new PopupMenu(ActivityPlayBack.this, speedFlotButton);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.speed_menue, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {

                if(item.getTitle().equals("x1"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x1");
                    internl = 800;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();

                }
                else if(item.getTitle().equals("x2"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x2");
                    internl = 700;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();
                }
                else if(item.getTitle().equals("x3"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    // buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x3");
                    internl = 600;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();
                }
                else if(item.getTitle().equals("x4"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x4");
                    internl = 500;
                    route_distance = 0;

                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();

                    playRoute();
                }
                else if(item.getTitle().equals("x5"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x5");
                    internl = 400;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();
                }
                else if(item.getTitle().equals("x6"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x6");
                    internl = 300;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();
                }
                else if(item.getTitle().equals("x7"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x7");
                    internl = 200;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();
                }
                else if(item.getTitle().equals("x8"))
                {
                    //mainLinearLayout.setVisibility(View.VISIBLE);
                    //buttonStart.setVisibility(View.GONE);
                    speedTextView.setText("x8");
                    internl = 100;
                    route_distance = 0;
                    //cancelTimer();
                    cancelHandler();
                    cancelHandlermHandlersetZhoom();
                    cancelHandlerspeedTimer();
                    cancelHandlerdistanceTimer();
                    playRoute();

                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }
}
