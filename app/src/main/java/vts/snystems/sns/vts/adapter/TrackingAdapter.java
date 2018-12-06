package vts.snystems.sns.vts.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.AlertInfo;
import vts.snystems.sns.vts.pojo.TrackingInfo;
import vts.snystems.sns.vts.pojo.VehicleInfo;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolderCarLog>
{

    private ArrayList<TrackingInfo> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;
    Context context;

    public TrackingAdapter(Context context)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    public void setAllDeviceInfo(ArrayList<TrackingInfo> countryInformationsdata)
    {
//        this.carlogInformation = countryInformationsdata;
//        notifyItemRangeChanged(0, countryInformationsdata.size());

        try
        {
            if(!carlogInformation.isEmpty())
            {
                carlogInformation.clear();
            }

            int initialSize = countryInformationsdata.size();
            this.carlogInformation.addAll(countryInformationsdata);
            notifyItemRangeInserted(initialSize, countryInformationsdata.size()-1); //Correct position
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.tracking_row, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {

            TrackingInfo carLogInformation = carlogInformation.get(position);
            holder.vNumberTxt.setText(carLogInformation.getvNumber());
            holder.vCurrAdress.setText(Html.fromHtml("<b>"+context.getString(R.string.address)+" : </b>"+carLogInformation.getvAddress()));
            String lastDtTime = carLogInformation.getvLastTrackTime();
            String [] data = lastDtTime.split(" ");
            if(data.length > 0)
            {
                holder.vLastDt.setText(Html.fromHtml("<b>"+context.getString(R.string.trtime)+": </b>"+F.parseDate(data[0],"Year")+" "+data[1]));

            }
            else
            {
                holder.vLastDt.setText(Html.fromHtml("<b>"+context.getString(R.string.trtime)+" : </b> NA "));
            }


            holder.vTargetName.setText("("+carLogInformation.getvTargetName()+")");

            //set vehicle status
            String vStatus = carLogInformation.getvStatus();
            if(vStatus != null)
            {
                setVehicleStatus(vStatus,holder.vStatus,holder.vStatusColLi);
            }

            //set vehicle icon
            String vehicleType = carLogInformation.getvType();
            Log.e("vehicleType",vehicleType);
            if(vehicleType != null)
            {
                F.setVehicleIconType(holder.img_vehicles,vehicleType);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setVehicleStatus(String vStatus,TextView textView,LinearLayout vStatusColLi)
    {
        if(vStatus.equals("MV"))
        {

            //textView.setText("Running");
            textView.setText(view.getResources().getString(R.string.running));
            vStatusColLi.setBackgroundColor(Color.parseColor("#43a047"));
        }
        else if(vStatus.equals("ST"))
        {
            //textView.setText("Idle");
            textView.setText(view.getResources().getString(R.string.idle));
            vStatusColLi.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if(vStatus.equals("SP"))
        {
           // textView.setText("Parking");
            textView.setText(view.getResources().getString(R.string.parking));
            vStatusColLi.setBackgroundColor(Color.parseColor("#f9a825"));
        }
        else if(vStatus.equals("OFF"))
        {
            //textView.setText("Offline");
            textView.setText(view.getResources().getString(R.string.offline));
            vStatusColLi.setBackgroundColor(Color.parseColor("#616161"));
        }
    }

    @Override
    public int getItemCount()
    {
        return carlogInformation.size();
    }

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{


        @BindView(R.id.img_vehicles)
        ImageView img_vehicles;

        @BindView(R.id.vNumberTxt)
        TextView vNumberTxt;

        @BindView(R.id.vCurrAdress)
        TextView vCurrAdress;

        @BindView(R.id.vLastDt)
        TextView vLastDt;

        @BindView(R.id.vTargetName)
        TextView vTargetName;

        @BindView(R.id.vStatus)
        TextView vStatus;

        @BindView(R.id.vStatusColLi)
        LinearLayout vStatusColLi;

        @BindView(R.id.liveTrackingLi)
        LinearLayout liveTrackingLi;

        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);
            try
            {
                ButterKnife.bind(this,itemView);

                /*deviceDateTimeTextView = (TextView) itemView.findViewById(R.id.deviceDateTimeTextView);
                deviceIdTextView  = (TextView) itemView.findViewById(R.id.deviceIdTextView);
                deviceIddTextView  = (TextView) itemView.findViewById(R.id.deviceIddTextView);

                goNextLinearLayout  = (LinearLayout) itemView.findViewById(R.id.goNextLinearLayout);
                deviceStatusImage  = (ImageView) itemView.findViewById(R.id.deviceStatusImage);

                itemView.setOnClickListener(this);*/
                liveTrackingLi.setOnClickListener(this);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(final View v)
        {
            try
            {

                if (v.getId() == liveTrackingLi.getId())
                {
                    if (F.checkConnection())
                    {
                        TrackingInfo allDevicesInfo = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                        getAlldata(v.getContext(),allDevicesInfo.getvImei(),allDevicesInfo.getvStatus());
                    }
                    else
                        {
                        M.t(VMsg.connection);
                    }
                }

                /*if (v.getId() == goNextLinearLayout.getId())
                {


                    Intent i = new Intent(v.getContext(), TrackPlayInfoSettingActivity.class);

                    MyApplication.editor.putString(Constants.DEVICE_ID,allDevicesInfo.getDeviceId());
                    String [] dateData = allDevicesInfo.getLastTrackedTime().split(" ");
                    MyApplication.editor.putString(Constants.LAST_UPDATE_DATE_TIME,F.parseDate(dateData[0])+" "+dateData[1]);
                    MyApplication.editor.putString(Constants.VEHICLE_TYPE,allDevicesInfo.getVehicleType());
                    MyApplication.editor.putString(Constants.VEHICLE_STAUS,allDevicesInfo.getDeviceStatus());
                    MyApplication.editor.putString(Constants.IMEI,allDevicesInfo.getVehicleIMEI());
                    MyApplication.editor.putString(Constants.SIM,allDevicesInfo.getSim());
                    MyApplication.editor.putString(Constants.LICENCE,"NA");
                    MyApplication.editor.putString(Constants.MODEL,allDevicesInfo.getVehicleModel());
                    MyApplication.editor.putString(Constants.CO_ORDINATE,allDevicesInfo.getCoOrdinate());
                    MyApplication.editor.putString(Constants.DUE_DATE,"NA");
                    MyApplication.editor.putString(Constants.SPEED,allDevicesInfo.getVehicleSpeed());
                    MyApplication.editor.putString(Constants.GPS_TIME,allDevicesInfo.getGpsTime());
                    MyApplication.editor.putString(Constants.ACC_STATUS,allDevicesInfo.getAccStatus());
                    MyApplication.editor.putString(Constants.LAST_TRACK_TIME,allDevicesInfo.getLastTrackedTime());
                    MyApplication.editor.putString(Constants.VEHICLE_NUMBER,allDevicesInfo.getVehicleNumber());
                    MyApplication.editor.putString(Constants.LOCATION_VISIBLE,allDevicesInfo.getLocationVisibleStatus());
                    MyApplication.editor.commit();

                    v.getContext().startActivity(i);
                }*/
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getAlldata(final Context context, final String imei,final String vStatus)
    {
        try
        {

            String[] parameters =
                    {
                            Constants.IMEI + "#" + imei

                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {
                           parseResponse(result,context,vStatus);

                        }
                    },
                    new VolleyErrorCallback()
                    {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {
                            if (volleyErrr instanceof TimeoutError)
                            {
                               sR(context.getString(R.string.req_t), context.getString(R.string.tout),context,imei,vStatus);
                            }
                            else
                            {
                               F.handleError(volleyErrr,context,"Webservice : Constants.trackingDeviceInfo,Function : getAlldata(final Context context, final String imei)");
                            }

                        }
                    },
                    Constants.webUrl + "" + Constants.getDeviceDetail,
                    parameters,
                    context, "first");

        }
        catch (Exception e)
        {
            F.displayDialog(context,"Server Error",e.toString());
        }

    }

    private void parseResponse(String loginJson,Context context,String vStatus) {


        String imei = Constants.NA;
        String vehicleNo = Constants.NA;
        String target_name = Constants.NA;
        String speed = Constants.NA;
        String last_dt = Constants.NA;
        //String total_duration = Constants.NA;
        //String total_stop = Constants.NA;
        String gpsS = Constants.NA;
        String powerS = Constants.NA;
        String ignS = Constants.NA;
        String odometer = Constants.NA;
        //String total_dist = Constants.NA;
        String fuel = Constants.ZERO;
        String lt_dist = Constants.NA;
        String lt_stop = Constants.NA;
        String vehicleType = Constants.NA;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String iconColor = Constants.NA;
        String course = "0";
        String overspeed_value = "0";
        try {

            if (loginJson != null || loginJson.length() > 0)
            {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject)
                {

                    JSONObject loginJsonObject1 = new JSONObject(loginJson);

                    String success = loginJsonObject1.getString("success");
                    String message = loginJsonObject1.getString("message");

                    if (success.equals("1"))
                    {


                        JSONArray jsonArray = loginJsonObject1.getJSONArray("deviceDetails");


                        JSONObject loginJsonObject = jsonArray.getJSONObject(0);

                        if(loginJsonObject.has("imei") && !loginJsonObject.isNull("imei"))
                        {
                            imei = loginJsonObject.getString("imei");
                        }

                        if(loginJsonObject.has("vehicleNo") && !loginJsonObject.isNull("vehicleNo"))
                        {
                            vehicleNo = loginJsonObject.getString("vehicleNo");
                        }

                        if(loginJsonObject.has("speed") && !loginJsonObject.isNull("speed"))
                        {
                            speed = loginJsonObject.getString("speed");
                        }



                        /*if(loginJsonObject.has("total_duration") && !loginJsonObject.isNull("total_duration"))
                        {
                            total_duration = loginJsonObject.getString("total_duration");
                        }

                        if(loginJsonObject.has("total_stop") && !loginJsonObject.isNull("total_stop"))
                        {
                            total_stop = loginJsonObject.getString("total_stop");
                        }*/

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

                        if(loginJsonObject.has("odometer") && !loginJsonObject.isNull("odometer"))
                        {
                            odometer = loginJsonObject.getString("odometer");
                        }
                        /*if(loginJsonObject.has("total_dist") && !loginJsonObject.isNull("total_dist"))
                        {
                            total_dist = loginJsonObject.getString("total_dist");
                        }
*/
                        if(loginJsonObject.has("fuel") && !loginJsonObject.isNull("fuel"))
                        {
                            fuel = loginJsonObject.getString("fuel");
                        }
                        if(loginJsonObject.has("last_dt") && !loginJsonObject.isNull("last_dt"))
                        {
                            last_dt = loginJsonObject.getString("last_dt");
                        }
                        if(loginJsonObject.has("lt_dist") && !loginJsonObject.isNull("lt_dist"))
                        {
                            lt_dist = loginJsonObject.getString("lt_dist");
                        }
                        if(loginJsonObject.has("lt_stop") && !loginJsonObject.isNull("lt_stop"))
                        {
                            lt_stop = loginJsonObject.getString("lt_stop");
                        }

                        if(loginJsonObject.has("vehicleType") && !loginJsonObject.isNull("vehicleType"))
                        {
                            vehicleType = loginJsonObject.getString("vehicleType");
                        }

                        if(loginJsonObject.has("latitude") && !loginJsonObject.isNull("latitude"))
                        {
                            latitude = loginJsonObject.getString("latitude");
                        }

                        if(loginJsonObject.has("longitude") && !loginJsonObject.isNull("longitude"))
                        {
                            longitude = loginJsonObject.getString("longitude");
                        }

                        if(loginJsonObject.has("iconColor") && !loginJsonObject.isNull("iconColor"))
                        {
                            iconColor = loginJsonObject.getString("iconColor");
                        }
                        if(loginJsonObject.has("course") && !loginJsonObject.isNull("course"))
                        {
                            course = loginJsonObject.getString("course");
                        }
                        if(loginJsonObject.has("overspeed_value") && !loginJsonObject.isNull("overspeed_value"))
                        {
                            overspeed_value = loginJsonObject.getString("overspeed_value");
                        }
                        if(loginJsonObject.has("target_name") && !loginJsonObject.isNull("target_name"))
                        {
                            target_name = loginJsonObject.getString("target_name");
                        }


                           MyApplication.editor.putString(Constants.FRG_FLAG,"first");

                            Intent i = new Intent(context, ActivityPlaybackTrackInfo.class);

                            i.putExtra(Constants.IMEI,imei);
                            i.putExtra(Constants.SPEED,speed);
                            i.putExtra(Constants.VEHICLE_NUMBER,vehicleNo);
                            i.putExtra(Constants.VEHICLE_NAME,target_name);
                            i.putExtra(Constants.VEHICLE_OVER_SPEED,overspeed_value);
                            i.putExtra(Constants.LAST_UPDATE_DATE_TIME,last_dt);
                            i.putExtra(Constants.LATITUDE,latitude);
                            i.putExtra(Constants.LONGITUDE,longitude);
                            i.putExtra(Constants.ODOMETER,odometer);
                            i.putExtra(Constants.FUEL,fuel);
                            i.putExtra(Constants.IGN_STATUS,ignS);
                            i.putExtra(Constants.POWER_STATUS,powerS);
                            i.putExtra(Constants.GPS_STATUS,gpsS);
                            i.putExtra(Constants.DIST_LAST,lt_dist);
                            i.putExtra(Constants.PARK_LAST,lt_dist);//
                            i.putExtra(Constants.DUR_LAST,lt_stop);//
                            /*i.putExtra(Constants.DIST_TOTAL,total_dist);
                            i.putExtra(Constants.PARK_TOTAL,total_stop);
                            i.putExtra(Constants.DUR_TOTAL,total_duration);*/
                            i.putExtra(Constants.ICON_COLOR,iconColor);
                            i.putExtra(Constants.VTYPE,vehicleType);
                            i.putExtra(Constants.DEVICE_STATUS,vStatus);
                            i.putExtra(Constants.COURSE,course);

                            context.startActivity(i);


                    }
                    else if (success.equals("2"))
                    {
                        M.t( message);

                    }
                    else if (success.equals("3"))
                    {
                        M.t( message);
                    }
                    else if (success.equals("0"))
                    {
                        M.t( message);

                    }
                }
                else
                {
                    M.t("Invalid Json found");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sR(String message, String error, final Context context, final String imei,final String vStatus)
    {
        new MaterialDialog.Builder(context)
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        getAlldata(context,imei,vStatus);
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
    public void setFilter(ArrayList<TrackingInfo> countryModels)
    {
        carlogInformation = new ArrayList<>();


        carlogInformation.addAll(countryModels);



        notifyDataSetChanged();
    }

}
