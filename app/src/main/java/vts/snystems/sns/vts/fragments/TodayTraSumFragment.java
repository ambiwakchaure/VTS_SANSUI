package vts.snystems.sns.vts.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.adapter.VehicleListAdapter;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.VehicleInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class TodayTraSumFragment extends Fragment {


    private VehicleListAdapter vehicleListAdapter;
    private ArrayList<VehicleInfo> VEHICLE_INFO = new ArrayList<>();

    @BindView(R.id.vehicleListRecycle)
    RecyclerView vehicleListRecycle;

    @BindView(R.id.txt_error_no_data)
    TextView txt_error_no_data;

    @BindView(R.id.img_no_data)
    ImageView img_no_data;


    @BindView(R.id.dateDetails)
    TextView dateDetails;

    @BindView(R.id.buttonRefresh)
    Button buttonRefresh;

    @BindView(R.id.dateLinearLayout)
    LinearLayout dateLinearLayout;

    String dateData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.today_travel_sum, container, false);
        ButterKnife.bind(this, view);

        dateData = F.parseDate(F.getCurDate(),"Year");

        vehicleListAdapter = new VehicleListAdapter(getActivity());
        vehicleListRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        vehicleListRecycle.setAdapter(vehicleListAdapter);

        if(VEHICLE_INFO.isEmpty())
        {
            if (F.checkConnection())
            {
                img_no_data.setImageResource(R.drawable.ic_car_wheel);
                txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
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
            vehicleListRecycle.setVisibility(View.VISIBLE);
            dateLinearLayout.setVisibility(View.VISIBLE);
            dateDetails.setText("Date : "+dateData);
            vehicleListAdapter.setAllDeviceInfo(VEHICLE_INFO);
            vehicleListAdapter.notifyDataSetChanged();
        }

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(F.checkConnection())
                {
                    img_no_data.setImageResource(R.drawable.ic_car_wheel);
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                    buttonRefresh.setVisibility(View.GONE);
                    getAlldata();
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

    String loginJson;
    public void getAlldata()
    {

        try
        {


            String userName = MyApplication.prefs.getString(Constants.USER_NAME,"0");


            String currDate = F.getCurDate();

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName,
                            Constants.VEHICLE_STATUS + "#" + "All",
                            Constants.DAY_FLAG + "#" + "today",
                            Constants.FROM_DATE + "#" + currDate+" 00:00:00",
                            Constants.TO_DATE + "#" + currDate+" 23:59:59"

                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
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
                    Constants.webUrl + "" + Constants.getTravelSummary,
                    parameters,
                    getActivity(), "second");
        } catch (Exception e) {

        }

    }
    class ParseResponse extends AsyncTask<String,String,String>
    {



        @Override
        protected String doInBackground(String... urls)
        {
            String vehicleNo = Constants.NA;//
            String total_duration = Constants.NA;//
            String total_idle = Constants.NA;//
            String total_stop = Constants.NA;//
            String avg_speed = Constants.NA;//
            String max_speed = Constants.NA;//
            String parking_ct = Constants.NA;//
            String vehicleType = Constants.NA;//
            String target_name = Constants.NA;//
            String success = null,message = null;


            try {

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

                            if(!VEHICLE_INFO.isEmpty())
                            {
                                VEHICLE_INFO.clear();
                            }


                            JSONArray jsonArray = loginJsonObject1.getJSONArray("deviceDetails");

                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                                if(loginJsonObject.has("vehicleNo") && !loginJsonObject.isNull("vehicleNo"))
                                {
                                    vehicleNo = loginJsonObject.getString("vehicleNo");
                                }
                                if(loginJsonObject.has("total_duration") && !loginJsonObject.isNull("total_duration"))
                                {
                                    total_duration = loginJsonObject.getString("total_duration");
                                }

                                if(loginJsonObject.has("total_idle") && !loginJsonObject.isNull("total_idle"))
                                {
                                    total_idle = loginJsonObject.getString("total_idle");
                                }

                                if(loginJsonObject.has("total_stop") && !loginJsonObject.isNull("total_stop"))
                                {
                                    total_stop = loginJsonObject.getString("total_stop");
                                }
                                if(loginJsonObject.has("avg_speed") && !loginJsonObject.isNull("avg_speed"))
                                {
                                    avg_speed = loginJsonObject.getString("avg_speed");
                                }

                                if(loginJsonObject.has("max_speed") && !loginJsonObject.isNull("max_speed"))
                                {
                                    max_speed = loginJsonObject.getString("max_speed");
                                }

                                if(loginJsonObject.has("vehicleType") && !loginJsonObject.isNull("vehicleType"))
                                {
                                    vehicleType = loginJsonObject.getString("vehicleType");
                                }
                                if(loginJsonObject.has("parking_ct") && !loginJsonObject.isNull("parking_ct"))
                                {
                                    parking_ct = loginJsonObject.getString("parking_ct");
                                }

                                if(loginJsonObject.has("target_name") && !loginJsonObject.isNull("target_name"))
                                {
                                    target_name = loginJsonObject.getString("target_name");
                                }

                                /*if(loginJsonObject.has("odometer") && !loginJsonObject.isNull("odometer"))
                                {
                                    odometer = loginJsonObject.getString("odometer");
                                }
                                if(loginJsonObject.has("total_dist") && !loginJsonObject.isNull("total_dist"))
                                {
                                    total_dist = loginJsonObject.getString("total_dist");
                                }

                                if(loginJsonObject.has("idle_ct") && !loginJsonObject.isNull("idle_ct"))
                                {
                                    idle_ct = loginJsonObject.getString("idle_ct");
                                }
                                if(loginJsonObject.has("speed") && !loginJsonObject.isNull("speed"))
                                {
                                    speed = loginJsonObject.getString("speed");
                                }
                                if(loginJsonObject.has("imei") && !loginJsonObject.isNull("imei"))
                                {
                                    imei = loginJsonObject.getString("imei");
                                }

                                if(loginJsonObject.has("last_dt") && !loginJsonObject.isNull("last_dt"))
                                {
                                    last_dt = loginJsonObject.getString("last_dt");
                                }
                                if(loginJsonObject.has("total_off") && !loginJsonObject.isNull("total_off"))
                                {
                                    total_off = loginJsonObject.getString("total_off");
                                }

                                if(loginJsonObject.has("gpsS") && !loginJsonObject.isNull("gpsS"))
                                {
                                    gpsS = loginJsonObject.getString("gpsS");
                                }

                                if(loginJsonObject.has("powerS") && !loginJsonObject.isNull("powerS"))
                                {
                                    powerS = loginJsonObject.getString("powerS");
                                }

                                if(loginJsonObject.has("ignS") && !loginJsonObject.isNull("ignS"))
                                {
                                    ignS = loginJsonObject.getString("ignS");
                                }

                                if(loginJsonObject.has("fuel") && !loginJsonObject.isNull("fuel"))
                                {
                                    fuel = loginJsonObject.getString("fuel");
                                }

                                if(loginJsonObject.has("lt_dist") && !loginJsonObject.isNull("lt_dist"))
                                {
                                    lt_dist = loginJsonObject.getString("lt_dist");
                                }

                                if(loginJsonObject.has("lt_stop") && !loginJsonObject.isNull("lt_stop"))
                                {
                                    lt_stop = loginJsonObject.getString("lt_stop");
                                }

                                if(loginJsonObject.has("lt_duration") && !loginJsonObject.isNull("lt_duration"))
                                {
                                    lt_duration = loginJsonObject.getString("lt_duration");
                                }

                                if(loginJsonObject.has("latitude") && !loginJsonObject.isNull("latitude"))
                                {
                                    latitude = loginJsonObject.getString("latitude");
                                }

                                if(loginJsonObject.has("longitude") && !loginJsonObject.isNull("longitude"))
                                {
                                    longitude = loginJsonObject.getString("longitude");
                                }

                                if(loginJsonObject.has("acc_status") && !loginJsonObject.isNull("acc_status"))
                                {
                                    acc_status = loginJsonObject.getString("acc_status");
                                }

                                if(loginJsonObject.has("iconColor") && !loginJsonObject.isNull("iconColor"))
                                {
                                    iconColor = loginJsonObject.getString("iconColor");
                                }

                                if(loginJsonObject.has("deviceStatus") && !loginJsonObject.isNull("deviceStatus"))
                                {
                                    deviceStatus = loginJsonObject.getString("deviceStatus");
                                }*/





                                VehicleInfo vehicleInfo = new VehicleInfo();

                                //vehicleInfo.setvImei(imei);
                                vehicleInfo.setvNumber(vehicleNo);//
                               // vehicleInfo.setSpeed(speed);
                               // vehicleInfo.setLastDateTime(last_dt);
                                vehicleInfo.setDurationTotal(total_duration);//
                                vehicleInfo.setIdleTime(total_idle);//
                                vehicleInfo.setStopTime(total_stop);//
//                                vehicleInfo.setOffTime(total_off);
//                                vehicleInfo.setGpsStatus(gpsS);
//                                vehicleInfo.setPowerStatus(powerS);
//                                vehicleInfo.setIgnStatus(ignS);
                                vehicleInfo.setAvgSpeed(avg_speed);//
                                vehicleInfo.setMaxSpeed(max_speed);
//                                vehicleInfo.setOdometer(odometer);
//                                vehicleInfo.setDistanceTotal(total_dist);
//                                vehicleInfo.setIdle_ct(idle_ct);
                                vehicleInfo.setTotalStop(parking_ct);//
                                //vehicleInfo.setFuel(fuel);
//                                vehicleInfo.setDistanceLastStop(lt_dist);
//                                vehicleInfo.setParkingLastStop(lt_stop);
//                                vehicleInfo.setDurationLastStop(lt_duration);
                                vehicleInfo.setvType(vehicleType);
//                                vehicleInfo.setLatitude(latitude);
//                                vehicleInfo.setLongitude(longitude);
//                                vehicleInfo.setvAccStatus(acc_status);
//                                vehicleInfo.setvColor(iconColor);
//                                vehicleInfo.setvStatus(deviceStatus);
                                vehicleInfo.setvTargetName(target_name);//

                                VEHICLE_INFO.add(vehicleInfo);
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
                    vehicleListRecycle.setVisibility(View.VISIBLE);
                    dateLinearLayout.setVisibility(View.VISIBLE);
                    dateDetails.setText("Date : "+dateData);
                    vehicleListAdapter.setAllDeviceInfo(VEHICLE_INFO);
                    vehicleListAdapter.notifyDataSetChanged();
                }
                else if (success.equals("2"))
                {
                    vehicleListRecycle.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(message);
                }
                else if (success.equals("3"))
                {
                    vehicleListRecycle.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(message);
                }
                else if (success.equals("0"))
                {
                    vehicleListRecycle.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_gesture_black_48dp);
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.t_trsum));
                }
                else if (success.equals("4"))
                {
                    try {


                        String errorReport = loginJson.replaceAll("\\<.*?>", "").replaceAll("\n", "");
                        String errorLog = F.getErrorJson(errorReport);
                        if (errorLog != null) {
                            Intent i = new Intent(getActivity(), ErrorActivity.class);
                            i.putExtra("error", errorReport);
                            i.putExtra("json", "Location :  TodayDistFragment.java getAlldata() Exception : " + errorLog);
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

}
