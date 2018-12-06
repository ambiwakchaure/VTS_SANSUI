package vts.snystems.sns.vts.activity.immobiliser.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import vts.snystems.sns.vts.activity.immobiliser.searchablespinner.BaseSearchDialogCompat;
import vts.snystems.sns.vts.activity.immobiliser.searchablespinner.SearchResultListener;
import vts.snystems.sns.vts.activity.immobiliser.searchablespinner.SimpleSearchDialogCompat;
import vts.snystems.sns.vts.activity.immobiliser.searchablespinner.VehicleInfo;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.V;
import vts.snystems.sns.vts.fonts.MyTextViewNormal;
import vts.snystems.sns.vts.fonts.MyTextViewSmiBold;
import vts.snystems.sns.vts.geofence.activity.ActivityGeofenceList;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class ActivityImmobiliser extends AppCompatActivity {
    ArrayList<VehicleInfo> VEHICLE_NUMBER = new ArrayList<>();

    @BindView(R.id.vInfoCardView)
    CardView vInfoCardView;

    @BindView(R.id.img_vehicles)
    ImageView imgVehicles;

    @BindView(R.id.vNumberTxt)
    TextView vNumberTxt;

    @BindView(R.id.vehicleNumberTxt)
    TextView vehicleNumberTxt;



    @BindView(R.id.vTargetName)
    TextView vTargetName;

    @BindView(R.id.vCurrAdress)
    TextView vCurrAdress;

    @BindView(R.id.vLastDt)
    TextView vLastDt;

    @BindView(R.id.liveTrackingLi)
    LinearLayout liveTrackingLi;

    String vehicleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immobiliser);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //get vehicle numbers
        if (F.checkConnection()) {
            getVehicleDetails();
        }
    }

    public void getVehicleDetails() {

        try {
            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + MyApplication.prefs.getString(Constants.USER_NAME, "0")
                            //Constants.USER_NAME + "#" + "benosyssales"

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            parseResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                        }
                    },
                    Constants.webUrl + "" + Constants.getVehicleGeofence,
                    parameters,
                    ActivityImmobiliser.this, "second");
        } catch (Exception e) {

        }

    }

    private void parseResponse(String loginJson) {
        String vehicle_number = Constants.NA;
        String vehicle_name = Constants.NA;
        String imei = Constants.NA;
        try {

            if (loginJson != null || loginJson.length() > 0) {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(loginJson);

                    String success = loginJsonObject.getString("status");
                    String message = loginJsonObject.getString("message");

                    if (success.equals("1")) {


                        JSONArray jsonArray = loginJsonObject.getJSONArray("vehicle_data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jjJsonObject = jsonArray.getJSONObject(i);

                            if (jjJsonObject.has("vehicle_number") && !jjJsonObject.isNull("vehicle_number")) {
                                vehicle_number = jjJsonObject.getString("vehicle_number");
                            }
                            if (jjJsonObject.has("vehicle_name") && !jjJsonObject.isNull("vehicle_name")) {
                                vehicle_name = jjJsonObject.getString("vehicle_name");
                            }
                            if (jjJsonObject.has("imei") && !jjJsonObject.isNull("imei")) {
                                imei = jjJsonObject.getString("imei");
                            }

                            VEHICLE_NUMBER.add(new VehicleInfo(vehicle_number+" ("+vehicle_name+")", imei, vehicle_name));

                        }

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

    public void getVehicleDetailsInfo(final String imei) {

        try {
            String[] parameters =
                    {
                            Constants.IMEI + "#" + imei
                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseResponseDetails(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                            vInfoCardView.setVisibility(View.GONE);
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.", "Server Time out",imei);
                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityImmobiliser.this,"Webservice : Constants.login,Function : proceedLogin()");
                            }
                        }
                    },
                    Constants.webUrl + "" + Constants.getVehicleStatusInfo,
                    parameters,
                    ActivityImmobiliser.this, "first");
        } catch (Exception e) {

        }

    }
    public void sR(String message, String error, final String imei)
    {
        new MaterialDialog.Builder(ActivityImmobiliser.this)
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {

                        dialog.dismiss();
                        getVehicleDetailsInfo(imei);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void parseResponseDetails(String loginJson) {


        String vehicleType = Constants.NA;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String last_dt = Constants.NA;
        String target_name = Constants.NA;
        try {

            if (loginJson != null || loginJson.length() > 0) {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject) {

                    JSONObject loginJsonObject = new JSONObject(loginJson);

                    String success = loginJsonObject.getString("success");
                    String message = loginJsonObject.getString("message");

                    if (success.equals("1")) {


                        JSONObject jjJsonObject = loginJsonObject.getJSONObject("deviceDetails");


                        if (jjJsonObject.has("vehicleType") && !jjJsonObject.isNull("vehicleType")) {
                            vehicleType = jjJsonObject.getString("vehicleType");
                        }
                        if (jjJsonObject.has("latitude") && !jjJsonObject.isNull("latitude")) {
                            latitude = jjJsonObject.getString("latitude");
                        }
                        if (jjJsonObject.has("longitude") && !jjJsonObject.isNull("longitude")) {
                            longitude = jjJsonObject.getString("longitude");
                        }
                        if (jjJsonObject.has("last_dt") && !jjJsonObject.isNull("last_dt")) {
                            last_dt = jjJsonObject.getString("last_dt");
                        }
                        if (jjJsonObject.has("target_name") && !jjJsonObject.isNull("target_name")) {
                            target_name = jjJsonObject.getString("target_name");
                        }

                        vInfoCardView.setVisibility(View.VISIBLE);

                        vNumberTxt.setText(vehicleNumber);
                        vTargetName.setText(" ("+target_name+")");
                        String address = F.getAddress(Double.valueOf(latitude), Double.valueOf(longitude));
                        if (address.equals("NA"))
                        {
                            vCurrAdress.setText(Constants.uLocation);
                        }
                        else
                        {
                            vCurrAdress.setText(address);
                        }
                        String [] dateData = last_dt.split(" ");
                        vLastDt.setText(Html.fromHtml("<b>Last track : </b>"+F.parseDate(dateData[0],"Year")+" "+dateData[1]));
                        if(vehicleType != null)
                        {
                            F.setVehicleIconType(imgVehicles,vehicleType);
                        }


                    } else if (success.equals("2")) {
                        vInfoCardView.setVisibility(View.GONE);
                        M.t(message);
                    } else if (success.equals("3")) {
                        vInfoCardView.setVisibility(View.GONE);
                        M.t(message);
                    } else if (success.equals("0")) {
                        vInfoCardView.setVisibility(View.GONE);
                        M.t(message);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        finish();
    }

    public void selectVehicleNumber(View view) {

        if (!VEHICLE_NUMBER.isEmpty()) {
            SimpleSearchDialogCompat dialog = new SimpleSearchDialogCompat
                    (
                            ActivityImmobiliser.this, "Vehicle Number",
                            "Search", null,
                            VEHICLE_NUMBER,
                            new SearchResultListener<VehicleInfo>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog, VehicleInfo item, int position) {

                                    String [] vNumData = item.getVNumber().split("\\(");
                                    vehicleNumber = vNumData[0];
                                    vehicleNumberTxt.setText(item.getVNumber());
                                    getVehicleDetailsInfo(item.getVimei());
                                    dialog.dismiss();
                                }
                            }
                    );
            dialog.show();
            //Typeface tf = Typeface.createFromAsset(ActivityImmobiliser.this.getAssets(), "TitilliumWeb-Regular.ttf");
           // dialog.getSearchBox().setTypeface(Typeface.SERIF);
        }

    }

}
