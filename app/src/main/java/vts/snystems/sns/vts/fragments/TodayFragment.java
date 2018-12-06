package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;

import vts.snystems.sns.vts.activity.ActivityAlertHistory;
import vts.snystems.sns.vts.adapter.PlayBackListAdapter;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;

import vts.snystems.sns.vts.pojo.PlaybackList;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


public class TodayFragment extends Fragment {

    private PlayBackListAdapter playbackListAdapter;

    private ArrayList<PlaybackList> PLAYBACK_LIST = new ArrayList<>();
    @BindView(R.id.playbackListRecycle)
    RecyclerView playbackListRecycle;

    @BindView(R.id.txt_error_no_data)
    TextView txt_error_no_data;

    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    @BindView(R.id.buttonRefresh)
    Button buttonRefresh;


    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this,view);
        initialize();
        setListner();

        if(PLAYBACK_LIST.isEmpty())
        {
            if (F.checkConnection())
            {
                img_no_data.setImageResource(R.drawable.ic_car_wheel);
                txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                buttonRefresh.setVisibility(View.GONE);
                Log.e("dAAAAA","Today : "+F.displayYesterdasDate(0));
                getDateWisePlayBack(F.displayYesterdasDate(0), MyApplication.prefs.getString(Constants.IMEI,"0"));
            }
            else
            {
                img_no_data.setImageResource(R.drawable.ic_noconnection);
                txt_error_no_data.setText(VMsg.connection);
                buttonRefresh.setVisibility(View.GONE);
            }
        }
        else
        {
            playbackListRecycle.setVisibility(View.VISIBLE);
            playbackListAdapter.setAllDeviceInfo(PLAYBACK_LIST);
            playbackListAdapter.notifyDataSetChanged();
        }

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(F.checkConnection())
                {
                    img_no_data.setImageResource(R.drawable.ic_car_wheel);
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                    buttonRefresh.setVisibility(View.GONE);
                    getDateWisePlayBack(F.displayYesterdasDate(0), MyApplication.prefs.getString(Constants.IMEI,"0"));
                }
                else
                {
                    img_no_data.setImageResource(R.drawable.ic_noconnection);
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
    @Override
    public void onStart() {
        super.onStart();



    }

    private void initialize()
    {

        playbackListAdapter = new PlayBackListAdapter(getActivity());
        playbackListRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        playbackListRecycle.setAdapter(playbackListAdapter);


    }
    String loginJson;
    private void getDateWisePlayBack(final String toDateTime,String imei) {

        try
        {

            Log.e("DATAAAAAAA"," imei : "+imei+" toDateTime : "+toDateTime);

            String[] parameters =
                    {
                            Constants.IMEI + "#" +imei,
                            Constants.DATE + "#" + toDateTime

//                            Constants.IMEI + "#" +"861359034104776",
//                            Constants.DATE + "#" + "2018-07-20"

                    };

            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result)
                        {


                            loginJson = result;
                            new ParseResponse().execute();


                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {


                            if (volleyErrr instanceof TimeoutError)
                            {
                                img_no_data.setImageResource(R.drawable.ic_av_timer_black_48dp);
                                txt_error_no_data.setText(R.string.req_t);
                                buttonRefresh.setVisibility(View.VISIBLE);
                                // sR("Oops ! request timed out.", "Timeout Error",vehicleStatus);

                            }
                            else
                            {
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.devicesDetails,Function : getAlldata(final String vehicleStatus)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.getPlayBack,
                    parameters,
                    getActivity(), "second");


        } catch (Exception e) {
            Log.d("playback","----error"+e);
        }
    }

    class ParseResponse extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... urls)
        {


            String start_date = Constants.LTDATE_TIME;
            String end_date = Constants.LTDATE_TIME;
            String lat1 = Constants.NA;
            String long1 = Constants.NA;
            String lat2 = Constants.NA;
            String long2 = Constants.NA;
            String dist = Constants.NA;
            String success = null,message = null;

            try
            {

                if (loginJson != null || loginJson.length() > 0)
                {
                    Object json = new JSONTokener(loginJson).nextValue();
                    if (json instanceof JSONObject)
                    {

                        JSONObject loginJsonObject1 = new JSONObject(loginJson);

                        success = loginJsonObject1.getString("success");
                        message = loginJsonObject1.getString("message");

                        if (success.equals("1"))
                        {

                            if(!PLAYBACK_LIST.isEmpty())
                            {
                                PLAYBACK_LIST.clear();
                            }

                            JSONArray jsonArray = loginJsonObject1.getJSONArray("details");

                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                                if(loginJsonObject.has("start_date") && !loginJsonObject.isNull("start_date"))
                                {
                                    start_date = loginJsonObject.getString("start_date");
                                }

                                if(loginJsonObject.has("end_date") && !loginJsonObject.isNull("end_date"))
                                {
                                    end_date = loginJsonObject.getString("end_date");
                                }

                                if(loginJsonObject.has("lat1") && !loginJsonObject.isNull("lat1"))
                                {
                                    lat1 = loginJsonObject.getString("lat1");
                                }

                                if(loginJsonObject.has("long1") && !loginJsonObject.isNull("long1"))
                                {
                                    long1 = loginJsonObject.getString("long1");
                                }

                                if(loginJsonObject.has("lat2") && !loginJsonObject.isNull("lat2"))
                                {
                                    lat2 = loginJsonObject.getString("lat2");
                                }

                                if(loginJsonObject.has("long2") && !loginJsonObject.isNull("long2"))
                                {
                                    long2 = loginJsonObject.getString("long2");
                                }

                                if(loginJsonObject.has("dist") && !loginJsonObject.isNull("dist"))
                                {
                                    dist = loginJsonObject.getString("dist");
                                }

                                PlaybackList playbackList = new PlaybackList();

                                playbackList.setStartDate(start_date);
                                playbackList.setEndDate(end_date);
                                playbackList.setLat1(lat1);
                                playbackList.setLong1(long1);
                                playbackList.setLat2(lat2);
                                playbackList.setLong2(long2);
                                playbackList.setDist(dist);

                                PLAYBACK_LIST.add(playbackList);
                            }


                        }

                    }
                    else
                    {
                        success = "4";
                        message = "Warning ! incorrect json found.";

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("para>>", "loginJson>>E" +e);
            }

            return success+"#"+message;
        }
        @Override
        protected void onPostExecute(String dataSm)
        {
            super.onPostExecute(dataSm);
            if(dataSm != null)
            {
                String [] data = dataSm.split("#");
                String success = data[0];
                String message = data[1];

                if(success.equals("1"))
                {
                    playbackListRecycle.setVisibility(View.VISIBLE);
                    playbackListAdapter.setAllDeviceInfo(PLAYBACK_LIST);
                    playbackListAdapter.notifyDataSetChanged();
                }
                else if (success.equals("2"))
                {
                    playbackListRecycle.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(message);
                    //  M.s(serachDataEditText, message);
                }
                else if (success.equals("3"))
                {
                    playbackListRecycle.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(message);
                    //  M.s(serachDataEditText, message);
                }
                else if (success.equals("0"))
                {
                    playbackListRecycle.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.today_playback));
                    //  M.s(serachDataEditText, message);
                }
                else if (success.equals("4"))
                {
                    try
                    {
                    String errorReport = loginJson.replaceAll("\\<.*?>","").replaceAll("\n","");
                    String errorLog = F.getErrorJson(errorReport);
                    if(errorLog != null)
                    {
                        Intent i = new Intent(getActivity(), ErrorActivity.class);
                        i.putExtra("error",errorReport);
                        i.putExtra("json","Location :  TodayDistFragment.java getAlldata() Exception : "+errorLog);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }
                }
                catch (Exception e)
                {

                }
                }
            }


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
