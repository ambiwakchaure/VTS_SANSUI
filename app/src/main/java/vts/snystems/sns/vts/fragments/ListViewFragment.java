package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class ListViewFragment extends Fragment {


    @BindView(R.id.txt_total_vehicles)
    TextView txt_total_vehicles;

    @BindView(R.id.txt_running_count)
    TextView txt_running_count;

    @BindView(R.id.txt_idle_count)
    TextView txt_idle_count;

    @BindView(R.id.txt_inactive_count)
    TextView txt_inactive_count;

    @BindView(R.id.txt_stop_count)
    TextView txt_stop_count;

    @BindView(R.id.progressbar_running)
    ProgressBar progressbarRunning;

    @BindView(R.id.progressbar_idle)
    ProgressBar progressbarIdle;

    @BindView(R.id.progressbar_parking)
    ProgressBar progressbarParking;

    @BindView(R.id.progressbar_stop)
    ProgressBar progressbarStop;
    @BindView(R.id.runningRl)
    RelativeLayout runningRl;
    @BindView(R.id.idleRl)
    RelativeLayout idleRl;
    @BindView(R.id.parkingRl)
    RelativeLayout parkingRl;
    @BindView(R.id.offlineRl)
    RelativeLayout offlineRl;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    int counterCall = 0;
    Handler mHandler;
    View view;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.listview_fragment, container, false);

        ButterKnife.bind(this, view);

        mHandler = new Handler();

        setListner();

        if (F.checkConnection())
        {
            getListData("first");
            //Log.d("login","----S");
        }
        else
        {
            networkDialod();
        }
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


    private void networkDialod() {

        MaterialDialog md = new MaterialDialog.Builder(getActivity())
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
                            getListData("first");
                            //Log.d("login","----S");
                        }
                        else
                        {
                            dialog.dismiss();
                        }

                    }
                })
                .show();
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "TitilliumWeb-Regular.ttf");
        md.getTitleView().setTypeface(tf);
        md.getContentView().setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
        md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
        md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);
    }

    String loginJson;
    public void getListData(final String status) {

        MyApplication.editor.commit();
        String userName = MyApplication.prefs.getString(Constants.USER_NAME, "0");
        MyApplication.editor.commit();

        try {

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            loginJson = result;
                            //parseResponse(result);
                            new ParseResponse().execute();
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {


                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR(getString(R.string.req_t), getString(R.string.tout),status);

                            }
                            else
                            {
                                F.handleError(volleyErrr,getActivity(),"Webservice : Constants.listView,Function : getListData(final String status)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.getListView,
                    parameters,
                    getActivity(), status);


        } catch (Exception e) {

        }

    }

    String totalVehicleCount = Constants.ZERO;
    String runningVehicleCount = Constants.ZERO;
    String idleVehicleCount = Constants.ZERO;
    String parkingVehicleCount = Constants.ZERO;
    String offlineVehicleCount = Constants.ZERO;

    class ParseResponse extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            txt_total_vehicles.setText(view.getContext().getResources().getString(R.string.tVehicles) +"-"+ totalVehicleCount);

            txt_running_count.setText(runningVehicleCount);
            progressbarRunning.setSecondaryProgress(Integer.valueOf(runningVehicleCount));

            txt_idle_count.setText(idleVehicleCount);
            progressbarIdle.setSecondaryProgress(Integer.valueOf(idleVehicleCount));

            txt_inactive_count.setText(parkingVehicleCount);
            progressbarParking.setSecondaryProgress(Integer.valueOf(parkingVehicleCount));

            txt_stop_count.setText(offlineVehicleCount);
            progressbarStop.setSecondaryProgress(Integer.valueOf(offlineVehicleCount));

            if(counterCall == 0)
            {
                refreshFragment();
            }
            counterCall = 1;

        }

        @Override
        protected String doInBackground(String... urls)
        {


            try {

                if (loginJson != null || loginJson.length() > 0) {
                    Object json = new JSONTokener(loginJson).nextValue();
                    if (json instanceof JSONObject) {

                        JSONObject loginJsonObject = new JSONObject(loginJson);

                        String success = loginJsonObject.getString("status");
                        String message = loginJsonObject.getString("message");

                        if (success.equals("1")) {


                            if (loginJsonObject.has("totalVehicleCount") && !loginJsonObject.isNull("totalVehicleCount")) {
                                totalVehicleCount = loginJsonObject.getString("totalVehicleCount");
                            }

                            if (loginJsonObject.has("runningVehicleCount") && !loginJsonObject.isNull("runningVehicleCount")) {
                                runningVehicleCount = loginJsonObject.getString("runningVehicleCount");
                            }

                            if (loginJsonObject.has("idleVehicleCount") && !loginJsonObject.isNull("idleVehicleCount")) {
                                idleVehicleCount = loginJsonObject.getString("idleVehicleCount");
                            }

                            if (loginJsonObject.has("parkingVehicleCount") && !loginJsonObject.isNull("parkingVehicleCount")) {
                                parkingVehicleCount = loginJsonObject.getString("parkingVehicleCount");
                            }

                            if (loginJsonObject.has("offlineVehicleCount") && !loginJsonObject.isNull("offlineVehicleCount")) {
                                offlineVehicleCount = loginJsonObject.getString("offlineVehicleCount");
                            }

                        } else if (success.equals("2")) {
                            uiUpdate(message);
                        } else if (success.equals("3")) {
                            uiUpdate(message);
                        } else if (success.equals("0")) {
                           uiUpdate(message);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public  void uiUpdate(final String message)
    {
        new Runnable(){

            @Override
            public void run(){
                M.t(message);

            }
        };
    }

    public void sR(String message, String error, final String status)
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

                            if(status.equals("first"))
                            {
                                getListData(status);
                            }

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



    @OnClick(R.id.runningRl)
    public void onRunningRlClicked()
    {
        if(F.checkConnection())
        {
            String runningCount = txt_running_count.getText().toString();
            if(runningCount.equals("0"))
            {
                M.t(MyApplication.context.getResources().getString(R.string.run_not_found));
            }
            else
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                VehicleTrackingFragment fragment = new VehicleTrackingFragment();
                MyApplication.editor.putString(Constants.FRG_STATUS,"Moving").commit();
                fm.beginTransaction().replace(R.id.main_contenier, fragment).commit();
            }

        }
        else
        {
            M.t("Please check internet connection or wifi connection.");
        }



    }

    @OnClick(R.id.idleRl)
    public void onIdleRlClicked() {

        if(F.checkConnection())
        {
            String runningCount = txt_idle_count.getText().toString();
            if(runningCount.equals("0"))
            {
                M.t(MyApplication.context.getResources().getString(R.string.idle_vehicle));
            }
            else
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                VehicleTrackingFragment fragment = new VehicleTrackingFragment();
                MyApplication.editor.putString(Constants.FRG_STATUS,"Idle").commit();
                fm.beginTransaction().replace(R.id.main_contenier, fragment).commit();
            }

        }
        else
        {
            M.t("Please check internet connection or wifi connection.");
        }


    }

    @OnClick(R.id.parkingRl)
    public void onParkingRlClicked() {


        if(F.checkConnection())
        {
            String runningCount = txt_inactive_count.getText().toString();
            if (runningCount.equals("0")) {
                M.t(MyApplication.context.getResources().getString(R.string.parking_vehicle));
            } else {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                VehicleTrackingFragment fragment = new VehicleTrackingFragment();
                MyApplication.editor.putString(Constants.FRG_STATUS, "Parking").commit();
                fm.beginTransaction().replace(R.id.main_contenier, fragment).commit();
            }

        }
        else {


            M.t("Please check internet connection or wifi connection.");
        }

    }

    @OnClick(R.id.offlineRl)
    public void onOfflineRlClicked()
    {

        if(F.checkConnection())
        {
            String runningCount = txt_stop_count.getText().toString();
            if (runningCount.equals("0")) {
                M.t(MyApplication.context.getResources().getString(R.string.off_vehicle));
            } else {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                VehicleTrackingFragment fragment = new VehicleTrackingFragment();
                MyApplication.editor.putString(Constants.FRG_STATUS, "Offline").commit();
                fm.beginTransaction().replace(R.id.main_contenier, fragment).commit();

            }

        }
        else {

            M.t("Please check internet connection or wifi connection.");
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

                getListData("second");

                //callHandler();
            }
            mHandler.postDelayed(mStatusChecker, Constants.timerDelay);
        }
    };

    void startRepeatingTask() {
        mIsRunning = true;
        mStatusChecker.run();
    }

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
}
