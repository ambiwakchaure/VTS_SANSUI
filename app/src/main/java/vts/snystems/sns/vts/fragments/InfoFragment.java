package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import vts.snystems.sns.vts.activity.ActivityDistanceSummary;
import vts.snystems.sns.vts.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.Validate;
import vts.snystems.sns.vts.fonts.MyEditextNormal;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


public class InfoFragment extends Fragment {


    View view;
    @BindView(R.id.imeiTxt)
    TextView imeiTxt;

    @BindView(R.id.deviceidTxt)
    TextView deviceidTxt;

    @BindView(R.id.devicestatusTxt)
    TextView devicestatusTxt;

    @BindView(R.id.lastlocationTxt)
    TextView lastlocationTxt;

    @BindView(R.id.lasttimeTxt)
    TextView lasttimeTxt;

    @BindView(R.id.ignTxt)
    TextView ignTxt;

    @BindView(R.id.powerTxt)
    TextView powerTxt;

    @BindView(R.id.gpsTxt)
    TextView gpsTxt;

    @BindView(R.id.fuelTxt)
    TextView fuelTxt;

    @BindView(R.id.updateVNuBtn)
    Button updateVNuBtn;

    ActivityPlaybackTrackInfo activityPlaybackTrackInfo;

    @BindView(R.id.overSpeedTxt)
    EditText overSpeedTxt;

    @BindView(R.id.targetNameTxt)
    EditText targetNameTxt;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.info_fragment, container, false);

        ButterKnife.bind(this, view);
        setListner();

        updateVNuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!Validate.validateEmptyField(targetNameTxt, MyApplication.context.getResources().getString(R.string.val_vnumber))) {
                    return;
                }
                if (!Validate.validateEmptyField(overSpeedTxt, MyApplication.context.getResources().getString(R.string.val_spped_lim))) {
                    return;
                }
                if (!Validate.validateOverSpeed(overSpeedTxt, MyApplication.context.getResources().getString(R.string.val_inval_ov))) {
                    return;
                }
                /*if (!Validate.validateVehicleNumber(deviceidTxt.getText().toString(),getActivity())) {
                    return;
                }*/

                //M.t("Success");
                if (F.checkConnection())
                {
                    updateVNumber();
                    //Log.d("login","----S");
                } else {
                    M.t(VMsg.connection);
                }

            }
        });

        imeiTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.imei) + " </b>" + MyApplication.prefs.getString(Constants.IMEI, "0")));
        deviceidTxt.setText(MyApplication.prefs.getString(Constants.VEHICLE_NUMBER, "0"));
        targetNameTxt.setText(MyApplication.prefs.getString(Constants.VEHICLE_NAME, "0"));
        overSpeedTxt.setText(MyApplication.prefs.getString(Constants.VEHICLE_OVER_SPEED, "0"));
        fuelTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.fuel) + "</b> : " + MyApplication.prefs.getString(Constants.FUEL, "0 ") + "Ltr"));


        String deviceStatus = MyApplication.prefs.getString(Constants.DEVICE_STATUS, "0");

        if (deviceStatus.equals("MV"))//moving
        {
            devicestatusTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.vStatus) + " </b>" + MyApplication.context.getResources().getString(R.string.running) + ""));
        } else if (deviceStatus.equals("ST"))//static
        {
            devicestatusTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.vStatus) + " </b>" + MyApplication.context.getResources().getString(R.string.stop) + ""));
        } else if (deviceStatus.equals("SP"))//static(parking)
        {
            devicestatusTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.vStatus) + " </b>" + MyApplication.context.getResources().getString(R.string.parking) + ""));
        } else if (deviceStatus.equals("OFF"))//offline
        {
            devicestatusTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.vStatus) + " </b>" + MyApplication.context.getResources().getString(R.string.offline) + ""));
        }
        lastlocationTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.lastLoc) + "</b>" + F.getAddress(Double.valueOf(MyApplication.prefs.getString(Constants.LATITUDE, "0")), Double.valueOf(MyApplication.prefs.getString(Constants.LONGITUDE, "0")))));
        String lastUpTime = MyApplication.prefs.getString(Constants.LAST_UPDATE_DATE_TIME, "0");

        if (!lastUpTime.equals("NA")) {
            String[] data = lastUpTime.split(" ");

            lasttimeTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.lastUTime) + " </b>" + F.parseDate(data[0], "" + MyApplication.context.getResources().getString(R.string.year) + "") + " " + data[1]));
        }
        String ignStatus = MyApplication.prefs.getString(Constants.IGN_STATUS, "0");
        if (ignStatus.equals("0")) {
            ignTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.ignStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.off) + ""));
        } else if (ignStatus.equals("1")) {
            ignTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.ignStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.on) + ""));
        }

        String powerStatus = MyApplication.prefs.getString(Constants.POWER_STATUS, "0");
        if (powerStatus.equals("0")) {
            powerTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.powerStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.off) + ""));
        } else if (powerStatus.equals("1")) {
            powerTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.powerStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.on) + ""));
        }

        String gpsStatus = MyApplication.prefs.getString(Constants.GPS_STATUS, "0");
        if (gpsStatus.equals("0")) {
            gpsTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.gpsStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.off) + ""));
        } else if (gpsStatus.equals("1")) {
            gpsTxt.setText(Html.fromHtml("<b>" + MyApplication.context.getResources().getString(R.string.gpsStatus) + "</b> " + MyApplication.context.getResources().getString(R.string.on) + ""));
        }

        return view;

    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS =
            {
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE
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
    private void updateVNumber() {

        try {


            String userName = MyApplication.prefs.getString(Constants.USER_NAME, "0");
            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName,
                            Constants.vehicle_no + "#" + deviceidTxt.getText().toString(),
                            Constants.overspeed_value + "#" + overSpeedTxt.getText().toString(),
                            Constants.target_name + "#" + targetNameTxt.getText().toString(),
                            Constants.IMEI + "#" + MyApplication.prefs.getString(Constants.IMEI, "0")

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseLoginResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                            if (volleyErrr instanceof TimeoutError) {
                                sR("Oops ! request timed out.", "Timeout Error");

                            } else {
                                F.handleError(volleyErrr, getActivity(), "Webservice : Constants.vehicleUpdate,Function : updateVNumber()");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.updateVehicleInfo,
                    parameters,
                    getActivity(), "first");

        } catch (Exception e) {

        }


    }

    private void parseLoginResponse(String loginJson) {
        try {

            if (loginJson != null || loginJson.length() > 0) {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(loginJson);

                    String success = loginJsonObject.getString("success");
                    String message = loginJsonObject.getString("message");

                    if (success.equals("1")) {
                        M.t(getString(R.string.success_vn));
                        MyApplication.editor.putString(Constants.VEHICLE_NUMBER, deviceidTxt.getText().toString()).commit();
                        MyApplication.editor.putString(Constants.VEHICLE_NAME, targetNameTxt.getText().toString()).commit();
                        MyApplication.editor.putString(Constants.VEHICLE_OVER_SPEED, overSpeedTxt.getText().toString()).commit();
                        activityPlaybackTrackInfo.updateVNumber();


                    } else if (success.equals("2")) {
                        M.t(message);
                    } else if (success.equals("3")) {
                        M.t(message);
                    } else if (success.equals("0")) {
                        M.t(message);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void sR(String message, String error) {
        new MaterialDialog.Builder(getActivity())
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        updateVNumber();
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


    public void newInsatance(ActivityPlaybackTrackInfo activityPlaybackTrackInfo) {

        this.activityPlaybackTrackInfo = activityPlaybackTrackInfo;
    }


}
