package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import vts.snystems.sns.vts.adapter.AlertHistAdapter;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.AlertHistoryInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class YesterdayAlertHistroyFragment extends Fragment {


    private AlertHistAdapter vehicleListAdapter;

    private ArrayList<AlertHistoryInfo> VEHICLE_INFO = new ArrayList<>();

    @BindView(R.id.alertHistoryRecy)
    RecyclerView alertHistoryRecy;

    @BindView(R.id.txt_error_no_data)
    TextView txt_error_no_data;

    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    @BindView(R.id.dateDetails)
    TextView dateDetails;

    @BindView(R.id.dateLinearLayout)
    LinearLayout dateLinearLayout;

    @BindView(R.id.buttonRefresh)
    Button buttonRefresh;

    String dateData;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.today_alerthistroy_ragment, container, false);

        ButterKnife.bind(this,view);

        dateData = "Date : "+F.parseDate(F.displayYesterdasDate(-1),"Year");

        vehicleListAdapter = new AlertHistAdapter(getActivity());
        alertHistoryRecy.setLayoutManager(new LinearLayoutManager(getActivity()));
        alertHistoryRecy.setAdapter(vehicleListAdapter);

        if(VEHICLE_INFO.isEmpty())
        {

            if (F.checkConnection())
            {
                img_no_data.setImageResource(R.drawable.ic_car_wheel);
                txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.please_wait));
                //default three days data current date and previoust two days date -2
                buttonRefresh.setVisibility(View.GONE);
                fetchDefaultThreedaysAlert();

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
            alertHistoryRecy.setVisibility(View.VISIBLE);
            dateLinearLayout.setVisibility(View.VISIBLE);
            dateDetails.setText(dateData);
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
                    fetchDefaultThreedaysAlert();
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
    public void fetchDefaultThreedaysAlert()
    {



        String userName = MyApplication.prefs.getString(Constants.USER_NAME,"0");

        String fromDateTime  = null,toDateTime = null;
        try
        {
            fromDateTime = F.displayYesterdasDate(-1) + " " + "00:00:00";
            toDateTime = F.displayYesterdasDate(-1) + " " + "23:59:59";

            String[] parameters =
                    {

                            Constants.USER_NAME + "#" + userName,
                            Constants.FROM_DATE + "#" + fromDateTime,
                            Constants.TO_DATE + "#" + toDateTime

                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result) {


                            loginJson = result;
                            Log.e("DAAAAAAA",""+result);
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

                    Constants.webUrl + "" + Constants.getAlertHistory,
                    parameters,
                    getActivity(), "second");



        } catch (Exception e) {

        }

    }
    class ParseResponse extends AsyncTask<String,String,String>
    {

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
                    alertHistoryRecy.setVisibility(View.VISIBLE);
                    dateLinearLayout.setVisibility(View.VISIBLE);
                    dateDetails.setText(dateData);
                    vehicleListAdapter.setAllDeviceInfo(VEHICLE_INFO);
                    vehicleListAdapter.notifyDataSetChanged();
                }
                else if (success.equals("2"))
                {
                    alertHistoryRecy.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_alert_notf);
                    txt_error_no_data.setText(message);
                }
                else if (success.equals("3"))
                {
                    alertHistoryRecy.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_alert_notf);
                    txt_error_no_data.setText(message);
                }
                else if (success.equals("0"))
                {
                    alertHistoryRecy.setVisibility(View.GONE);
                    dateLinearLayout.setVisibility(View.GONE);
                    img_no_data.setImageResource(R.drawable.ic_alert_notf);
                    txt_error_no_data.setText(MyApplication.context.getResources().getString(R.string.yal_history));
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

                        }
                    }
                    catch (Exception e)
                    {

                    }
                }
            }



        }

        @Override
        protected String doInBackground(String... urls)
        {

            String imei = Constants.NA;
            String created_date = Constants.NA;
            String vehicle_number = Constants.NA;
            String sos_alert = Constants.NA;
            String overspeed_alert = Constants.NA;
            String power_cut_alert = Constants.NA;
            String low_device_battery_alert = Constants.NA;
            String target_name = Constants.NA;
            String vehicleType = Constants.NA;
            String success = null,message = null;
            try {

                if (loginJson != null || loginJson.length() > 0)
                {
                    Object json = new JSONTokener(loginJson).nextValue();
                    if (json instanceof JSONObject)
                    {

                        JSONObject loginJsonObject1 = new JSONObject(loginJson);

                        success = loginJsonObject1.getString("status");
                        message = loginJsonObject1.getString("message");

                        if (success.equals("1"))
                        {

                            if(!VEHICLE_INFO.isEmpty())
                            {
                                VEHICLE_INFO.clear();
                            }

                            JSONArray jsonArray = loginJsonObject1.getJSONArray("alerts");

                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject loginJsonObject = jsonArray.getJSONObject(i);

                                if(loginJsonObject.has("imei") && !loginJsonObject.isNull("imei"))
                                {
                                    imei = loginJsonObject.getString("imei");
                                }

                                if(loginJsonObject.has("created_date") && !loginJsonObject.isNull("created_date"))
                                {
                                    created_date = loginJsonObject.getString("created_date");
                                }

                                if(loginJsonObject.has("vehicle_number") && !loginJsonObject.isNull("vehicle_number"))
                                {
                                    vehicle_number = loginJsonObject.getString("vehicle_number");
                                }

                                if(loginJsonObject.has("sos_alert") && !loginJsonObject.isNull("sos_alert"))
                                {
                                    sos_alert = loginJsonObject.getString("sos_alert");
                                }

                                if(loginJsonObject.has("overspeed_alert") && !loginJsonObject.isNull("overspeed_alert"))
                                {
                                    overspeed_alert = loginJsonObject.getString("overspeed_alert");
                                }

                                if(loginJsonObject.has("power_cut_alert") && !loginJsonObject.isNull("power_cut_alert"))
                                {
                                    power_cut_alert = loginJsonObject.getString("power_cut_alert");
                                }

                                if(loginJsonObject.has("low_device_battery_alert") && !loginJsonObject.isNull("low_device_battery_alert"))
                                {
                                    low_device_battery_alert = loginJsonObject.getString("low_device_battery_alert");
                                }
                                if(loginJsonObject.has("target_name") && !loginJsonObject.isNull("target_name"))
                                {
                                    target_name = loginJsonObject.getString("target_name");
                                }

                                if(loginJsonObject.has("vehicleType") && !loginJsonObject.isNull("vehicleType"))
                                {
                                    vehicleType = loginJsonObject.getString("vehicleType");
                                }
                                AlertHistoryInfo historyInfo = new AlertHistoryInfo();

                                historyInfo.setvImei(imei);
                                historyInfo.setDateTime(created_date);
                                historyInfo.setvNumber(vehicle_number);
                                historyInfo.setvSos(sos_alert);
                                historyInfo.setOverSpeed(overspeed_alert);
                                historyInfo.setPowerCut(power_cut_alert);
                                historyInfo.setLowBattery(low_device_battery_alert);
                                historyInfo.setvTargetName(target_name);
                                historyInfo.setvType(vehicleType);

                                VEHICLE_INFO.add(historyInfo);
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
    }


}
