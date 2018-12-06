package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import vts.snystems.sns.vts.adapter.DistanceSumAdapter;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.DistanceSummary;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class MonthDistFragment extends Fragment {


    @BindView(R.id.vehicleListRecycle)
    RecyclerView vehicleListRecycle;

    ArrayList<DistanceSummary> DIST_DATA = new ArrayList<>();
    DistanceSumAdapter distanceSumAdapter;

    @BindView(R.id.txt_error_no_data)
    TextView txt_error_no_data;

    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    @BindView(R.id.dateDetails)
    TextView dateDetails;

    @BindView(R.id.dateLinearLayout)
    LinearLayout dateLinearLayout;

    String  dateRange;

    @BindView(R.id.buttonRefresh)
    Button buttonRefresh;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frg_today, container, false);
        ButterKnife.bind(this, view);


        dateRange = "Date : "+F.parseDate(F.getMonthDate(),"Year")+" - "+F.parseDate(F.getCurDate(),"Year");

        distanceSumAdapter = new DistanceSumAdapter(getActivity());
        vehicleListRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        vehicleListRecycle.setAdapter(distanceSumAdapter);

        if(DIST_DATA.isEmpty())
        {
            if (F.checkConnection()) {

                img_no_data.setImageResource(R.drawable.ic_car_wheel);
                txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                buttonRefresh.setVisibility(View.GONE);
                getAlldata();
            } else {
                img_no_data.setImageResource(R.drawable.ic_noconnection);
                txt_error_no_data.setText(VMsg.connection);
                buttonRefresh.setVisibility(View.GONE);
            }
        }
        else
        {
            vehicleListRecycle.setVisibility(View.VISIBLE);
            dateLinearLayout.setVisibility(View.VISIBLE);
            dateDetails.setText(dateRange);
            distanceSumAdapter.setAllDeviceInfo(DIST_DATA);
            distanceSumAdapter.notifyDataSetChanged();
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

    public void getAlldata() {

        try {

            MyApplication.editor.commit();
            String userName = MyApplication.prefs.getString(Constants.USER_NAME, "0");
            MyApplication.editor.commit();

            String from_to_date = F.getMonthDate();
            String  currDate = F.getCurDate();

//            Log.e("DATE_DATA","fisrt_month_date : "+from_to_date);
//            Log.e("DATE_DATA","last_month_date : "+currDate);

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName,
                            Constants.FROM_DATE + "#" + from_to_date,
                            Constants.TO_DATE + "#" + currDate

                    };


            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

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
                                img_no_data.setImageResource(R.drawable.ic_av_timer_black_48dp);
                                txt_error_no_data.setText(R.string.req_t);
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.devicesDetails,Function : getAlldata(final String vehicleStatus)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.getDistanceSummary,
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


            String imei = Constants.NA;
            String distance = Constants.NA;
            String vehicle_no = Constants.NA;
            String vehicle_type = Constants.NA;
            //String vehicle_color = Constants.NA;
            //String vehicle_status = Constants.NA;
            String target_name = Constants.NA;
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
                        //success = "4";
                        message = loginJsonObject1.getString("message");

                        if (success.equals("1"))
                        {

                            if(!DIST_DATA.isEmpty())
                            {
                                DIST_DATA.clear();
                            }

                            JSONArray jsonArray = loginJsonObject1.getJSONArray("distanceData");

                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                                if(loginJsonObject.has("imei") && !loginJsonObject.isNull("imei"))
                                {
                                    imei = loginJsonObject.getString("imei");
                                }
                                if(loginJsonObject.has("distance") && !loginJsonObject.isNull("distance"))
                                {
                                    distance = loginJsonObject.getString("distance");
                                }
                                if(loginJsonObject.has("vehicle_no") && !loginJsonObject.isNull("vehicle_no"))
                                {
                                    vehicle_no = loginJsonObject.getString("vehicle_no");
                                }
                                if(loginJsonObject.has("vehicle_type") && !loginJsonObject.isNull("vehicle_type"))
                                {
                                    vehicle_type = loginJsonObject.getString("vehicle_type");
                                }
                                /*if(loginJsonObject.has("vehicle_color") && !loginJsonObject.isNull("vehicle_color"))
                                {
                                    vehicle_color = loginJsonObject.getString("vehicle_color");
                                }
                                if(loginJsonObject.has("vehicle_status") && !loginJsonObject.isNull("vehicle_status"))
                                {
                                    vehicle_status = loginJsonObject.getString("vehicle_status");
                                }*/
                                if(loginJsonObject.has("target_name") && !loginJsonObject.isNull("target_name"))
                                {
                                    target_name = loginJsonObject.getString("target_name");
                                }

                                DistanceSummary vehicleInfo = new DistanceSummary();

                                vehicleInfo.setvImei(imei);
                                vehicleInfo.setvDistance(distance);
                                vehicleInfo.setvNumber(vehicle_no);
                                vehicleInfo.setvType(vehicle_type);
                               // vehicleInfo.setvColor(vehicle_color);
                               // vehicleInfo.setvStatus(vehicle_status);
                                vehicleInfo.setvTargetName(target_name);

                                DIST_DATA.add(vehicleInfo);
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
                    dateDetails.setText(dateRange);
                    distanceSumAdapter.setAllDeviceInfo(DIST_DATA);
                    distanceSumAdapter.notifyDataSetChanged();
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
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.m_dsum));
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

}