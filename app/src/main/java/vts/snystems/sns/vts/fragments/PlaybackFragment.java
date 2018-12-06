package vts.snystems.sns.vts.fragments;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nitri.gauge.Gauge;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.V;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.PlaybackSpeedInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


import static com.google.android.gms.maps.model.JointType.ROUND;

/**
 * Created by sns003 on 22-Mar-18.
 */

public class PlaybackFragment extends Fragment implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    private GoogleMap mMap;



    @BindView(R.id.mainLinearLayout)
    LinearLayout mainLinearLayout;


    @BindView(R.id.buttonSpeed)
    LinearLayout buttonSpeed;

    @BindView(R.id.buttonStart)
    LinearLayout buttonStart;

    @BindView(R.id.buttonStop)
    LinearLayout buttonStop;

//    @BindView(R.id.mapTypeLinearLayout)
//    RelativeLayout mapTypeLinearLayout;
//
//    @BindView(R.id.mapTypeImageView)
//    ImageView mapTypeImageView;
//
//    @BindView(R.id.mapTypeTextView)
//    TextView mapTypeTextView;

    @BindView(R.id.buttonDistance)
    Button buttonDistance;

    /*@BindView(R.id.buttonVehiclePos)
    Button buttonVehiclePos;*/

    @BindView(R.id.buttonSpeedLive)
    Button buttonSpeedLive;

    @BindView(R.id.gauge1)
    Gauge gauge1;
    String addressLine;
    Marker sourceMarker,destinationMarker;

    LatLng newPos;
    private List<LatLng> polyLineList;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;
    private int internl, external;
    private LatLng first,second,temp;
    ArrayList<PlaybackSpeedInfo> PLAYBACK_SPEED_DATA = new ArrayList<>();
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    private static ArrayList<LatLng> markers_route = new ArrayList<LatLng>();
    private boolean isMapChangeRequired = true;
    double route_distance = 0;
    boolean isMarkerRotating=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.playback_fragment, container, false);

       // tfDistance = Typeface.createFromAsset(getActivity().getAssets(),"digital-7.ttf");

        internl = 500;
        external = 1000;

        ButterKnife.bind(this, view);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_playback);
        mapFragment.getMapAsync(this);

        polyLineList = new ArrayList<>();

        setListners();

       // staticGuage();
        //displayDistance();
        //openDialog();

        return view;
    }

    private void staticGuage(final Float value) {





        /*gauge1.moveToValue(800);

        HandlerThread thread = new HandlerThread("GaugeDemoThread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gauge1.moveToValue(300);
            }
        }, 2800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gauge1.moveToValue(550);
            }
        }, 5600);*/

        gauge1.setLowerText(value+" Km/h");
        HandlerThread gauge3Thread = new HandlerThread("Gauge3DemoThread");
        gauge3Thread.start();
        Handler gauge3Handler = new Handler(gauge3Thread.getLooper());
        gauge3Handler.post(new Runnable() {
            @Override
            public void run()
            {
                for (float x = 0; x <= 6; x += .1) {
                    //float value = (float) Math.atan(x) * 20;
                    gauge1.moveToValue(value);

                    /*try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        });

      // gauge4.setValue(333);
    }

    private void displayDistance()
    {

       // LatLng first = null,second = null;
        double route_distance = 0;
        ArrayList<LatLng> DATAA = new ArrayList<>();

        DATAA.add(new LatLng(18.489910,73.857444));
        DATAA.add(new LatLng(18.490760,73.857578));
        DATAA.add(new LatLng(18.491943,73.857621));
        DATAA.add(new LatLng(18.493277,73.857739));
        DATAA.add(new LatLng(18.494480,73.857911));
        DATAA.add(new LatLng(18.496195,73.857986));
        DATAA.add(new LatLng(18.498173,73.858168));
        DATAA.add(new LatLng(18.499971,73.858468));
        DATAA.add(new LatLng(18.500551,73.858045));
        DATAA.add(new LatLng(18.500892,73.856312));
        DATAA.add(new LatLng(18.501235,73.854692));
        DATAA.add(new LatLng(18.501606,73.853925));
        DATAA.add(new LatLng(18.502794,73.853818));
        DATAA.add(new LatLng(18.504590,73.853603));
        DATAA.add(new LatLng(18.505889,73.853614));
        DATAA.add(new LatLng(18.507308,73.853732));
        DATAA.add(new LatLng(18.509205,73.853753));
        DATAA.add(new LatLng(18.511008,73.853796));
        DATAA.add(new LatLng(18.513010,73.853844));
        DATAA.add(new LatLng(18.514530,73.853775));
        DATAA.add(new LatLng(18.516040,73.853796));
        DATAA.add(new LatLng(18.517690,73.853986));
        DATAA.add(new LatLng(18.518428,73.854142));


        try
        {
            for (int i = 0; i < DATAA.size(); i++)
            {
                //first = second;
               // second = DATAA.get(i);

                double distanceD = F.getDistance(DATAA.get(i),DATAA.get(i+1));

                // distanceDfd = distanceDfd + distanceD;

                //route_distance = route_distance + distanceD;

                double distanceKm = distanceD / 1000;

                route_distance = route_distance + distanceKm;

                Log.e("DISTANCE_DATA",""+route_distance);
            }





        }
        catch (Exception e)
        {
            Log.e("DISTANCE_DATA","amol");
        }
    }

    private void parseDeviceLatLang(String response_lat_lang) {
        Log.e("PLAYBACK_RESPONSE",response_lat_lang);
        ArrayList<LatLng> received_markers = new ArrayList<>();
        try {
            if (response_lat_lang != null || response_lat_lang.length() > 0) {
                Object json = new JSONTokener(response_lat_lang).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonResponse = new JSONObject(response_lat_lang);

                    String successString = jsonResponse.getString("status");
                    if (successString.equals("1")) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("lat_long");
                        markers_route.clear();

                        //buttonVehiclePos.setVisibility(View.VISIBLE);

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++)
                            {

                                JSONObject tempJson1 = jsonArray.getJSONObject(i);

                                LatLng p = new LatLng(Double.parseDouble(tempJson1.getString("latitude")),
                                        Double.parseDouble(tempJson1.getString("longitude")));

                                received_markers.add(p);

                                PlaybackSpeedInfo speedInfo = new PlaybackSpeedInfo();

                                 String latitude = tempJson1.getString("latitude");
                                 String longitude = tempJson1.getString("longitude");
                                 String speed = tempJson1.getString("speed");

                                 if(latitude != null || latitude.length() > 0 || latitude != "")
                                 {
                                     if(longitude != null || longitude.length() > 0 || longitude != "")
                                     {
                                         if(speed != null || speed.length() > 0 || speed != "")
                                         {
                                             speedInfo.setLatLng(latitude+","+longitude);
                                             speedInfo.setSpeed(speed);

                                             PLAYBACK_SPEED_DATA.add(speedInfo);
                                         }
                                     }
                                 }

                                //markers_route.add(p);
                            }


                            Log.e("route_distance","route_distance : "+route_distance);
                            markers_route = F.getWayPoints(received_markers);

                            mainLinearLayout.setVisibility(View.VISIBLE);
                            buttonStart.setVisibility(View.GONE);
                            playRoute();
                        }
                    } else if (successString.equals("0")) {
                        F.displayDialog(getActivity(), "Oops...", "Warning ! playback details not found.");
                    }
                } else {
                    M.t("Incorrect JSON found");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMapsApiDirectionsUrl() {
        StringBuilder urlString = new StringBuilder();


        for (int i = 2; i < markers_route.size() - 1; i++) {
            urlString.append('|');
            urlString.append(markers_route.get(i).latitude);
            urlString.append(',');
            urlString.append(markers_route.get(i).longitude);
        }


        String OriDest = "origin=" + markers_route.get(0).latitude + "," + markers_route.get(0).longitude + "&destination="
                + markers_route.get(markers_route.size() - 1).latitude + "," + markers_route.get(markers_route.size() - 1).longitude;

        String sensor = "sensor=false&mode=driving";
        //String sensor = "sensor=false&mode=walking";
        String params = OriDest + "&" + "waypoints=optimize:true" + urlString + "&"
                + sensor + "&key=" + MyApplication.context.getResources().getString(R.string.google_maps_key);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+ output + "?" + params;
        Log.d("url>>>", ">>" + url);
        return url;
    }

    int cnt = 0;

    private void setListners() {


        /*buttonVehiclePos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {



                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom);
                // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newPos, Constants.plaback_cam_zhoom_lvl);
                mMap.animateCamera(cameraUpdate);
            }
        });*/


       /* mapTypeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cnt == 0) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapTypeTextView.setText("Map");
                    mapTypeImageView.setImageResource(R.drawable.ic_map_view);
                    mapTypeTextView.setTextColor(Color.parseColor("#000000"));
                    cnt = 1;
                } else if (cnt == 1) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapTypeTextView.setText("Satellite");
                    mapTypeImageView.setImageResource(R.drawable.ic_stattelite_view);
                    mapTypeTextView.setTextColor(Color.parseColor("#ffffff"));
                    cnt = 0;
                }
            }
        });*/


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonStop.setVisibility(View.VISIBLE);
                buttonStart.setVisibility(View.GONE);
                startTimer();
                //handler.removeMessages(0);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonStop.setVisibility(View.GONE);
                buttonStart.setVisibility(View.VISIBLE);
                stopTimer();

                //handler.removeMessages(0);
            }
        });

        buttonSpeed.setOnClickListener(new View.OnClickListener() {
            String[] speedType = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8"};

            @Override
            public void onClick(View viewq) {
                new MaterialDialog.Builder(getActivity())
                        .title("Select Speed")
                        .items(speedType)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try {

                                    if (mTimer1 != null) {
                                        mTimer1.cancel();
                                    }

                                    String sppedd = text.toString();
                                    if (sppedd.equals("x1")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 800;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if(sppedd.equals("x2")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 700;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x3")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 600;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x4")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 500;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x5")) {

                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 400;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x6")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 300;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x7")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 200;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    } else if (sppedd.equals("x8")) {
                                        mainLinearLayout.setVisibility(View.VISIBLE);
                                        buttonStart.setVisibility(View.GONE);
                                        internl = 100;
                                        external = 1000;
                                        route_distance = 0;
                                        cancelTimer();
                                        playRoute();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                return true;
                            }
                        })
                        .show();
            }
        });



    }

    private void showToast(String s) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //set google map camera position by default on india
        mMap = googleMap;
//        LatLng india = new LatLng(28.704059, 77.102490);
//        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));
//        setVehiclePositions();

        openDialog();
        //playRoute();


    }

    private int selected;

    private RadioGroup
            radioGroup;

    private RadioButton
            radioButtonToday,
            radioButtonYesterday,
            radioButtonUserTime;

    private TextView
            textFromDate,
            textFromTime,
            textToDate,
            textToTime;

    private LinearLayout
            linearLayoutfromDate,
            linearLayoutFromTime,
            linearLayoutToDate,
            linearLayoutToTime,
            dateTimeLinearLayout;

    private void openDialog() {

        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Select playback time")
                        .customView(R.layout.play_back_dialog, true)
                        .positiveText("Ok")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                if (radioButtonToday.isChecked())
                                {


                                 // parseDeviceLatLang(MyApplication.context.getResources().getString(R.string.dfes));
                                    //parseDeviceLatLang(Constants.JSON);

                                    dialog.dismiss();
                                    String fromDateTime = F.displayYesterdasDate(0) + " " + "00:00:00";
                                    String toDateTime = "" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                                    getDateWisePlayBack(fromDateTime, toDateTime);

                                } else if (radioButtonYesterday.isChecked()) {
                                    dialog.dismiss();
                                    String fromDateTime = F.displayYesterdasDate(-1) + " " + "00:00:00";
                                    String toDateTime = F.displayYesterdasDate(0) + " " + "23:59:59";

                                    getDateWisePlayBack(fromDateTime, toDateTime);

                                } else if (radioButtonUserTime.isChecked()) {

                                    if (!V.emptyTextView(textFromDate, "Date", "Warning ! select from date")) {
                                        return;
                                    }
                                    if (!V.emptyTextView(textFromTime, "Time", "Warning ! select from time")) {
                                        return;
                                    }
                                    if (!V.emptyTextView(textToDate, "Date", "Warning ! select to date")) {
                                        return;
                                    }
                                    if (!V.emptyTextView(textToTime, "Time", "Warning ! select to time")) {
                                        return;
                                    }

                                    dialog.dismiss();

                                    String fromDateTime = textFromDate.getText().toString() + " " + textFromTime.getText().toString();
                                    String toDateTime = textToDate.getText().toString() + " " + textToTime.getText().toString();

                                    getDateWisePlayBack(fromDateTime, toDateTime);
                                }

                              /*  radioButtonToday,
                                        radioButtonYesterday,
                                        radioButtonUserTime;*/
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                            }
                        })
                        .build();

        radioGroup = dialog.getCustomView().findViewById(R.id.radioGroup);

        radioButtonToday = dialog.getCustomView().findViewById(R.id.radioButtonToday);
        radioButtonYesterday = dialog.getCustomView().findViewById(R.id.radioButtonYesterday);
        radioButtonUserTime = dialog.getCustomView().findViewById(R.id.radioButtonUserTime);

        textFromDate = dialog.getCustomView().findViewById(R.id.textFromDate);
        textFromTime = dialog.getCustomView().findViewById(R.id.textFromTime);
        textToDate = dialog.getCustomView().findViewById(R.id.textToDate);
        textToTime = dialog.getCustomView().findViewById(R.id.textToTime);

        linearLayoutfromDate = dialog.getCustomView().findViewById(R.id.linearLayoutfromDate);
        linearLayoutFromTime = dialog.getCustomView().findViewById(R.id.linearLayoutFromTime);
        linearLayoutToDate = dialog.getCustomView().findViewById(R.id.linearLayoutToDate);
        linearLayoutToTime = dialog.getCustomView().findViewById(R.id.linearLayoutToTime);

        dateTimeLinearLayout = dialog.getCustomView().findViewById(R.id.dateTimeLinearLayout);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId == R.id.radioButtonToday) {

                    dateTimeLinearLayout.setVisibility(View.GONE);

                    textFromDate.setText("Date");
                    textFromTime.setText("Time");
                    textToDate.setText("Date");
                    textToTime.setText("Time");


                } else if (checkedId == R.id.radioButtonYesterday) {

                    dateTimeLinearLayout.setVisibility(View.GONE);

                    textFromDate.setText("Date");
                    textFromTime.setText("Time");
                    textToDate.setText("Date");
                    textToTime.setText("Time");


                } else if (checkedId == R.id.radioButtonUserTime) {

                    dateTimeLinearLayout.setVisibility(View.VISIBLE);


                }

            }
        });

        linearLayoutfromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePicker("fromDate");

            }
        });
        linearLayoutFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePicker("fromTime");

            }
        });
        linearLayoutToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePicker("toDate");
            }
        });
        linearLayoutToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePicker("toTime");

            }
        });

        dialog.show();

    }

    private void getDateWisePlayBack(final String fromDateTime, final String toDateTime) {

        try {

            Log.d("playback","----S");
           /* String[] parameters =
                    {
                            Constants.IMEI + "#" + "861359033875434",
                            Constants.FROM_DATE + "#" + "2018-03-02 11:37:17",
                            Constants.TO_DATE + "#" + "2018-03-02 13:44:59"

                    };*/
            String[] parameters =
                    {
                            Constants.IMEI + "#" + MyApplication.prefs.getString(Constants.IMEI, "0"),
                            Constants.FROM_DATE + "#" + fromDateTime,
                            Constants.TO_DATE + "#" + toDateTime
                    };

            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {
                            parseDeviceLatLang(result);
                        }
                    },
                    new VolleyErrorCallback()
                    {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {
                            handleError(volleyErrr, fromDateTime, toDateTime);
                        }
                    },
                    Constants.webUrl + "" + Constants.getAllLatLng,
                    parameters,
                    getActivity(), "first");


        }
        catch (Exception e)
        {

        }
    }

    public void handleError(VolleyError error, String fromDateTime, String toDateTime) {
        try {

            if (error instanceof TimeoutError) {
                sR("Warning ! Server not responding or no connection.", "Timeout Error", fromDateTime, toDateTime);

            } else if (error instanceof NoConnectionError) {

                sR(VMsg.connection, "No Connection Error", fromDateTime, toDateTime);

            } else if (error instanceof AuthFailureError) {
                sR("Warning ! Remote server returns (401) Unauthorized?.", "AuthFailure Error", fromDateTime, toDateTime);

            } else if (error instanceof ServerError) {
                sR("Warning ! Wrong webservice call or wrong webservice url.", "Server Error", fromDateTime, toDateTime);

            } else if (error instanceof NetworkError) {
                sR("Warning ! You doesn't have a data connection or wi-fi Connection.", "Network Error", fromDateTime, toDateTime);

            } else if (error instanceof ParseError) {
                sR("Warning ! Incorrect json response.", "Parse Error", fromDateTime, toDateTime);

            }

        } catch (Exception e) {

        }

    }

    public void sR(String message, String error, final String fromDateTime, final String toDateTime) {
        new MaterialDialog.Builder(getActivity())
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        getDateWisePlayBack(fromDateTime, toDateTime);
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

    private void showTimePicker(final String timeStaus) {

        Calendar now = Calendar.getInstance();
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {

                if (timeStaus.equals("fromTime")) {
                    textFromTime.setText(String.format("%02d", hourOfDay) +
                            ":" + String.format("%02d", minute) +
                            ":" + String.format("%02d", seconds));
                } else if (timeStaus.equals("toTime")) {
                    textToTime.setText(String.format("%02d", hourOfDay) +
                            ":" + String.format("%02d", minute) +
                            ":" + String.format("%02d", seconds));
                }


            }


        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        mTimePicker.show();

        // Get Current Time
        /*final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute)
                    {
                        if(timeStaus.equals("fromTime"))
                        {
                            textFromTime.setText(hourOfDay + ":" + minute);
                        }
                        else if(timeStaus.equals("toTime"))
                        {
                            textToTime.setText(hourOfDay + ":" + minute);
                        }


                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();*/

    }

    String dateStatuss;

    private void showDatePicker(String dateStatus)
    {
        DatePickerFragment date = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        dateStatuss = dateStatus;

        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");

    }

    android.app.DatePickerDialog.OnDateSetListener ondate = new android.app.DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            if (dateStatuss.equals("fromDate")) {
                int a = monthOfYear + 1;
                String month = null;
                if (a < 10) {
                    month = "0" + (monthOfYear + 1);
                } else {
                    month = "" + (monthOfYear + 1);
                }

                int b = dayOfMonth;
                String day = null;
                if (b < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                textFromDate.setText(String.valueOf(year) + "-" + month + "-" + day);
            } else if (dateStatuss.equals("toDate")) {
                int a = monthOfYear + 1;
                String month = null;
                if (a < 10) {
                    month = "0" + (monthOfYear + 1);
                } else {
                    month = "" + (monthOfYear + 1);
                }

                int b = dayOfMonth;
                String day = null;
                if (b < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                textToDate.setText(String.valueOf(year) + "-" + month + "-" + day);
            }
            //   M.t(""+String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
            //dateButton.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
        }
    };

    private void cancelTimer()
    {
        if(timerTaskDistance!=null)
        {
            timerTaskDistance.cancel();
        }
        //stop camera zhoom timer
        if(timerTaskZhoomCam!=null)
        {
            timerTaskZhoomCam.cancel();
        }
        if(timerTaskSpeed!=null)
        {
            timerTaskSpeed.cancel();
        }
    }

    private Timer mTimer1;
    private TimerTask mTt1;

    private void stopTimer() {
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    TimerTask timerTaskZhoomCam;
    Timer tZhoomCam;

    private void setZhoom()
    {


        tZhoomCam = new Timer();
        timerTaskZhoomCam = new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.e("CAM",""+mMap.getCameraPosition().zoom);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom);
                        // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newPos, Constants.plaback_cam_zhoom_lvl);
                        mMap.animateCamera(cameraUpdate);
                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        tZhoomCam.schedule(timerTaskZhoomCam, 10, Constants.plaback_delayed_sec);  //

        /*final Handler someHandler = new Handler(getActivity().getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {


                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom);
               // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newPos, Constants.plaback_cam_zhoom_lvl);
                mMap.animateCamera(cameraUpdate);
                someHandler.postDelayed(this, Constants.plaback_delayed_sec);
            }
        }, 10);*/
    }

    TimerTask timerTaskDistance;
    Timer tDist;
    private void distanceTimer()
    {


        tDist = new Timer();
        timerTaskDistance = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run()
                    {
                        setDistance();
                        LatLng  destinationMarker = polyLineList.get(polyLineList.size() - 1);
                        LatLng  currenMarker = marker.getPosition();

                        String destinationMarkerr = destinationMarker.latitude+","+destinationMarker.longitude;
                        String currenMarkerr = currenMarker.latitude+","+currenMarker.longitude;


                       // Log.e("MARKER_DATA","DEST : "+destinationMarker+" CURR : "+currenMarker);


                        if(destinationMarkerr.equals(currenMarkerr))
                        {
                            //stop distance timer
                             if(timerTaskDistance!=null)
                             {
                                timerTaskDistance.cancel();
                             }
                             //stop camera zhoom timer
                             if(timerTaskZhoomCam!=null)
                             {
                                 timerTaskZhoomCam.cancel();
                             }
                            if(timerTaskSpeed!=null)
                            {
                                timerTaskSpeed.cancel();
                            }
                            gauge1.moveToValue(0);
                            gauge1.setLowerText("0.0 km/h");
                            //buttonDistance.setText("0.0 Km");

                        }

                        String markerLatLong = marker.getPosition().latitude+","+marker.getPosition().longitude;
                        String speed = returnSpeed(markerLatLong.substring(0,7));
                        Log.e("SPEEDDATAA",""+speed);
                        buttonSpeedLive.setText(speed);



                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        tDist.schedule(timerTaskDistance, 10, Constants.km_delayed_sec);  //


    }
    TimerTask timerTaskSpeed;
    Timer tSpeed;
    private void speedTimer()
    {


        tSpeed = new Timer();
        timerTaskSpeed = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run()
                    {

                        String markerLatLong1 = marker.getPosition().latitude+","+marker.getPosition().longitude;
                        String markerLatLong = markerLatLong1.substring(0,7);

                        if(markerLatLong.length() == 7)
                        {
                            String speed = returnSpeed(markerLatLong);

                            if(speed != null)
                            {
                                staticGuage(Float.valueOf(speed));
                                buttonSpeedLive.setText(speed+"Kmph");
                            }
                            else
                            {
                                buttonSpeedLive.setText("0Kmph");
                            }


                        }



                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        tSpeed.schedule(timerTaskSpeed, 10, 500);  //


    }

    private String returnSpeed(String markerLatLong)
    {
        String speedData = null;
        try
        {
            for(int i = 0; i < PLAYBACK_SPEED_DATA.size(); i++)
            {

                PlaybackSpeedInfo info = PLAYBACK_SPEED_DATA.get(i);

                String latlong = info.getLatLng().substring(0,7);
                String speed = info.getSpeed();

                if(latlong.length() == 7)
                {
                    if(latlong.equals(markerLatLong))
                    {
                        speedData = speed;

                    }
                }



            }
        }
        catch (Exception e)
        {

        }
        return speedData;
    }

    int cntCo = 0;
    TimerTask mTimerTaskZhoom;
    Timer t;
    private void setZhoeeom()
    {

        /*final Handler someHandler = new Handler(getActivity().getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run()
            {

                if(cntCo == 0)
                {
                    setZhoom();
                    cnt = 1;
                }


                someHandler.postDelayed(this, 3000);
            }
        }, 3000);*/



        t = new Timer();
        mTimerTaskZhoom = new TimerTask() {
            public void run()
            {
                handler.post(new Runnable() {
                    public void run()
                    {
                        setZhoom();

                        if(mTimerTaskZhoom!=null)
                        {
                            mTimerTaskZhoom.cancel();
                        }
                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTaskZhoom, 3000, 3000);  //


    }





    public void animateMarker() {

        setZhoeeom();
        distanceTimer();
        speedTimer();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        //Projection proj = mMap.getProjection();
        //Point startPoint = proj.toScreenLocation(marker.getPosition());
        //final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;


        if (index < polyLineList.size() - 1) {
            index++;
            next = index + 1;
        }
        if (index < polyLineList.size() - 1) {
            startPosition = polyLineList.get(index);
            endPosition = polyLineList.get(next);

            Log.d("newPos>>>>>", "nnnn>1>>" + startPosition);
            Log.d("newPos>>>>>", "nnnn>2>>" + endPosition);
        }



        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * endPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * endPosition.latitude + (1 - t)
                        * startPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (false) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void startTimer()
    {

        //proceedGetAddress();
        setZhoeeom();
        distanceTimer();
        speedTimer();
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        if (index < polyLineList.size() - 1) {
                            index++;
                            next = index + 1;
                        }
                        if (index < polyLineList.size() - 1) {
                            startPosition = polyLineList.get(index);
                            endPosition = polyLineList.get(next);

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

                                if(mMap != null)
                                {
                                    marker.setPosition(newPos);
                                    marker.setAnchor(0.5f, 0.5f);
                                    marker.setRotation(getBearing(startPosition, newPos));
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
                        if (polyLineList.size() > 0) {
                            if (polyLineList.get(polyLineList.size() - 1) == endPosition) {
                                valueAnimator.cancel();
                            }
                            else
                                {
                                valueAnimator.start();
                            }
                        }
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 1, internl);
    }

    /*private void setAddressDetails()
    {
        try
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            MarkerDetails deviceInfoSource = new MarkerDetails();
            String vehicleaddressSource = addressLine;
            Log.e("DAAAAAAA",""+sourceMarker.getPosition().latitude+","+sourceMarker.getPosition().longitude);
            deviceInfoSource.setAddressLoc(vehicleaddressSource);
            MarkerDetails deviceInfoDesination = new MarkerDetails();
            String vehicleaddressDestination = addressLine;
            deviceInfoDesination.setAddressLoc(vehicleaddressDestination);
           *//* //source marker
            mMap.addMarker( new MarkerOptions().position(polyLineList.get(0)));
            //destination marker
            mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)));*//*
            sourceMarker.setTag(deviceInfoSource);
            sourceMarker.showInfoWindow();
            builder.include(sourceMarker.getPosition());
            destinationMarker.setTag(deviceInfoDesination);
            destinationMarker.showInfoWindow();
            builder.include(destinationMarker.getPosition());

            MarkerInfo customInfoWindow = new MarkerInfo(getActivity());
            mMap.setInfoWindowAdapter(customInfoWindow);

            *//*LatLngBounds bounds = builder.build();

            int width = MyApplication.context.getResources().getDisplayMetrics().widthPixels;
            int height = MyApplication.context.getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);*//*
        }
        catch (Exception e)
        {

        }

    }
    public void proceedGetAddress()
    {
        try
        {
            vts.snsystems.sns.vts.volley.Rc.withoutParams(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result) {

                            parseAddressResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                        }
                    },
                    "http://maps.googleapis.com/maps/api/geocode/json?latlng=19.12521,74.73092&sensor=true");

        } catch (Exception e) {

        }

    }

    private void parseAddressResponse(String result) {

        try {

            if (result != null || result.length() > 0) {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(result);

                    JSONArray jsonArray = loginJsonObject.getJSONArray("results");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    addressLine = jsonObject.getString("formatted_address");



                    //setAddressDetails();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
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
    private void setDistance() {


        try
        {
            first = second;
            second = marker.getPosition();

                                   /*String sourceLat =  String.valueOf(first.latitude).substring(0,7);
                                   String sourceLng =  String.valueOf(first.longitude).substring(0,7);

                                   String destLat =  String.valueOf(second.latitude).substring(0,7);
                                   String destLng =  String.valueOf(second.longitude).substring(0,7);

                                   first = new LatLng(Double.valueOf(sourceLat),Double.valueOf(sourceLng));
                                   second = new LatLng(Double.valueOf(destLat),Double.valueOf(destLng));*/

            double distanceD = F.getDistance(first,second);

            // distanceDfd = distanceDfd + distanceD;

            //route_distance = route_distance + distanceD;

            double distanceKm = distanceD / 1000;

            route_distance +=  distanceKm;
            double rrdgf = Double.valueOf(new DecimalFormat("##.##").format(route_distance));

            //gauge1.setUpperText(new DecimalFormat("##.##").format(route_distance)+"Km");

           // buttonDistance.setTypeface(tfDistance);


            buttonDistance.setText(""+Double.valueOf(new DecimalFormat("##.#").format(rrdgf))+" Km");
        }
        catch (Exception e)
        {

        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
//    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {
//
//        double PI = 3.14159;
//        double lat1 = latLng1.latitude * PI / 180;
//        double long1 = latLng1.longitude * PI / 180;
//        double lat2 = latLng2.latitude * PI / 180;
//        double long2 = latLng2.longitude * PI / 180;
//
//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        brng = (brng + 360) % 360;
//
//        return brng;
//    }

    private float bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        float PI = 3.14f;
        float lat1 = (float) (latLng1.latitude * PI / 180);
        float long1 = (float) (latLng1.longitude * PI / 180);
        float lat2 = (float) (latLng2.latitude * PI / 180);
        float long2 = (float) (latLng2.longitude * PI / 180);

        float dLon = (long2 - long1);

        float y = (float) (Math.sin(dLon) * Math.cos(lat2));
        float x = (float) (Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                        * Math.cos(lat2) * Math.cos(dLon));

        float brng = (float) Math.atan2(y, x);

        brng = (float) Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        Log.d("bearingBetweenLocations","-> "+brng+"");
        return brng;

    }
    public void playRoute()
    {
        mMap.clear();
        //Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        //Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        String requestUrl = null;
        try {
            requestUrl = getMapsApiDirectionsUrl();

            Log.e("requestUrl",""+requestUrl);
            if (polyLineList != null) {
                polyLineList.clear();
            }
            Log.d(">>>>", "requestUrl>>" + requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, (JSONObject) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(">>>>", "response>>" + response);
                            try
                            {
                                JSONArray jsonArray = response.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                }

                                //Adjusting bounds
                                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList)
                                {
                                    builder.include(latLng);
                                }

                                final LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.BLUE);
                                polylineOptions.width(7);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyLine = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.width(7);
                                blackPolylineOptions.color(Color.BLUE);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                //source marker


                                sourceMarker =  mMap.addMarker( new MarkerOptions().position(polyLineList.get(0)));
                                //destination marker
                                destinationMarker =  mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)));


//                              //comment below part
                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(1000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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

                                MyApplication.editor.commit();
                                String vehicleType = MyApplication.prefs.getString(Constants.VTYPE, "0");
                                MyApplication.editor.commit();

                                if (vehicleType.equals("MC"))//motorcycle
                                {
                                    marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(0))
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                                }
                                else if (vehicleType.equals("CR"))//car
                                {
                                    marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(0))
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                                }
                                else
                                {
                                    marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(0))
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
                                }

//                                marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(0))
//                                        .flat(true)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_white)));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polyLineList.get(0), Constants.plaback_cam_zhoom_lvl);
                                mMap.animateCamera(cameraUpdate);

                                handler = new Handler();
                                index = -1;
                                next = 1;

                                //animateMarker();
                                startTimer();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(">>>>", "error>>" + error);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private void setVehiclePositions() {
        Marker marker1, marker2, marker3, marker4;
        LatLng pune = new LatLng(18.520430, 73.856744);
        marker1 = mMap.addMarker(new MarkerOptions()
                .position(pune)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("OFF"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        LatLngBounds bounds = builder.build();

        int width = MyApplication.context.getResources().getDisplayMetrics().widthPixels;
        int height = MyApplication.context.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }
}
