package vts.snystems.sns.vts.geofence.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.db.TABLE_GEOFENCE_VEHICLE_LIST;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.adapter.GeofenceListAdapter;
import vts.snystems.sns.vts.geofence.pojo.GeofenceChildInfo;
import vts.snystems.sns.vts.geofence.pojo.GeofenceParentInfo;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class ActivityGeofenceList extends AppCompatActivity
{
    ArrayList<GeofenceParentInfo> listDataHeader;
    HashMap<String, ArrayList<GeofenceChildInfo>> listDataChild;

    @BindView(R.id.geofenceListExp)
    ExpandableListView geofenceListExp;

    private GeofenceListAdapter geofenceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_list);

        ButterKnife.bind(this);

        listDataHeader = new ArrayList<GeofenceParentInfo>();

        listDataChild = new HashMap<String, ArrayList<GeofenceChildInfo>>();

        setListner();
        //prepareListData();

        if(F.checkConnection())
        {
            getVehicleDetails();
        }
        else
        {
            //M.t("Please check internet connection or wifi connection.");
            getStoredData();
        }

    }
    public void getVehicleDetails()
    {

        try
        {
            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + MyApplication.prefs.getString(Constants.USER_NAME,"0")
                            //Constants.USER_NAME + "#" + "benosyssales"

                    };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @Override
                        public void onSuccess(String result)
                        {
                            parseResponse(result);
                        }
                    },
                    new VolleyErrorCallback()
                    {

                        @Override
                        public void onError(VolleyError volleyErrr)
                        {
                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.", "Server Time out");
                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityGeofenceList.this,"Webservice : Constants.login,Function : proceedLogin()");
                            }
                        }
                    },
                    Constants.webUrl + "" + Constants.getVehicleGeofence,
                    parameters,
                    ActivityGeofenceList.this, "first");
        }
        catch (Exception e)
        {

        }
    }
    private void parseResponse(String loginJson)
    {
        String vehicle_number = Constants.NA;
        String vehicle_name = Constants.NA;
        String imei = Constants.NA;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String vehicle_type = Constants.NA;
        try
        {

            if (loginJson != null || loginJson.length() > 0)
            {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject)
                {

                    JSONObject loginJsonObject = new JSONObject(loginJson);

                    String success = loginJsonObject.getString("status");
                    String message = loginJsonObject.getString("message");

                    if (success.equals("1"))
                    {
                        //delete existing record
                        TABLE_GEOFENCE_VEHICLE_LIST.deleteData();

                        JSONArray jsonArray = loginJsonObject.getJSONArray("vehicle_data");

                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jjJsonObject = jsonArray.getJSONObject(i);

                            if(jjJsonObject.has("vehicle_number") && !jjJsonObject.isNull("vehicle_number"))
                            {
                                vehicle_number = jjJsonObject.getString("vehicle_number");
                            }
                            if(jjJsonObject.has("vehicle_name") && !jjJsonObject.isNull("vehicle_name"))
                            {
                                vehicle_name = jjJsonObject.getString("vehicle_name");
                            }
                            if(jjJsonObject.has("imei") && !jjJsonObject.isNull("imei"))
                            {
                                imei = jjJsonObject.getString("imei");
                            }
                            if(jjJsonObject.has("latitude") && !jjJsonObject.isNull("latitude"))
                            {
                                latitude = jjJsonObject.getString("latitude");
                            }
                            if(jjJsonObject.has("longitude") && !jjJsonObject.isNull("longitude"))
                            {
                                longitude = jjJsonObject.getString("longitude");
                            }
                            if(jjJsonObject.has("vehicle_type") && !jjJsonObject.isNull("vehicle_type"))
                            {
                                vehicle_type = jjJsonObject.getString("vehicle_type");
                            }

                            TABLE_GEOFENCE_VEHICLE_LIST.addVehicls(imei,vehicle_number,vehicle_name,latitude+","+longitude,vehicle_type);
                        }
                        getStoredData();
                    }
                    else if (success.equals("2"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("3"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("0"))
                    {
                        M.t(message);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /*private void prepareListData()
    {

        for (int i = 0; i < 5; i++)
        {
            GeofenceParentInfo geofenceParentInfo = new GeofenceParentInfo();

            geofenceParentInfo.setId(String.valueOf(i));
            geofenceParentInfo.setvNumber("MH 14 GL 1315");
            geofenceParentInfo.setvTargetName("Honda City");
            geofenceParentInfo.setvImei("8526532145");

            listDataHeader.add(geofenceParentInfo);

            ArrayList<GeofenceChildInfo> listDataChildItems = new ArrayList<GeofenceChildInfo>();

            for(int j = 0; j < 3; j++)
            {
                GeofenceChildInfo geofenceChildInfo = new GeofenceChildInfo();

                geofenceChildInfo.setGeofenceName("Geofence "+(j+1));
                geofenceChildInfo.setGeofenceInSwitch(true);
                geofenceChildInfo.setGeofenceOutSwitch(false);
                geofenceChildInfo.setGeofenceRadius("1");

                listDataChildItems.add(geofenceChildInfo);
            }

            listDataChild.put(String.valueOf(i),listDataChildItems);

        }

        geofenceListAdapter = new GeofenceListAdapter(this,listDataHeader,listDataChild);
        geofenceListExp.setAdapter(geofenceListAdapter);
    }*/
    public void getStoredData()
    {
        Cursor cursor = null;
        try
        {
            cursor = TABLE_GEOFENCE_VEHICLE_LIST.getVehicleInfo();

            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext())
                {
                    String ID = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.ID));
                    String IMEI = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.IMEI));
                    String VEHICLE_NUMBER = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.VEHICLE_NUMBER));
                    String TARGET_NAME = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.TARGET_NAME));
                    String LAT_LNG = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.LAT_LNG));
                    String TYPE = cursor.getString(cursor.getColumnIndex(TABLE_GEOFENCE_VEHICLE_LIST.TYPE));


                    GeofenceParentInfo geofenceParentInfo = new GeofenceParentInfo();

                    geofenceParentInfo.setId(ID);
                    geofenceParentInfo.setvNumber(VEHICLE_NUMBER);
                    geofenceParentInfo.setvTargetName(TARGET_NAME);
                    geofenceParentInfo.setvImei(IMEI);
                    geofenceParentInfo.setVlatlng(LAT_LNG);
                    geofenceParentInfo.setvType(TYPE);

                    listDataHeader.add(geofenceParentInfo);
                    ArrayList<GeofenceChildInfo> listDataChildItems = TABLE_STORE_GEOFENCE.getGeofenceDetails(IMEI);
                    if(listDataChildItems.isEmpty())
                    {
                        ArrayList<GeofenceChildInfo> geofenceChildInfosd = setStaticList();
                        listDataChild.put(ID,geofenceChildInfosd);
                    }
                    else
                    {
                        listDataChild.put(ID,listDataChildItems);
                    }


                }
                geofenceListAdapter = new GeofenceListAdapter(this,listDataHeader,listDataChild,ActivityGeofenceList.this);
                geofenceListExp.setAdapter(geofenceListAdapter);
            }
            else
            {
                M.t("Vehicle list not found");
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 4 || requestCode == 3)
        {
            refreshList();
        }
    }
    public void refreshList()
    {
        if(!listDataHeader.isEmpty())
        {
            listDataHeader.clear();
        }

        if(!listDataChild.isEmpty())
        {
            listDataChild.clear();
        }
        getStoredData();
    }

    private ArrayList<GeofenceChildInfo> setStaticList()
    {
        ArrayList<GeofenceChildInfo> listDataChildItems = new ArrayList<GeofenceChildInfo>();
        try
        {

            GeofenceChildInfo geofenceChildInfo = new GeofenceChildInfo();

            geofenceChildInfo.setGeofenceName("NA");
            listDataChildItems.add(geofenceChildInfo);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return listDataChildItems;
    }

    public void sR(String message, String error)
    {
        new MaterialDialog.Builder(ActivityGeofenceList.this)
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
                        getVehicleDetails();
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
    private void setListner() {

        geofenceListExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            // TODO Colapse Here Using this... in android
            int previousGroup = -1;
            boolean flag = false;

            @Override
            public void onGroupExpand(int groupPosition) {



                if (groupPosition != previousGroup && flag) {
                    geofenceListExp.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;

                flag = true;

            }
        });
    }



    public void goBack(View view) {

        finish();
    }


}
