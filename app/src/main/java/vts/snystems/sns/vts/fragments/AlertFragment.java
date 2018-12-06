package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import vts.snystems.sns.vts.activity.ActivityDistanceSummary;
import vts.snystems.sns.vts.adapter.AlertAdapter;
import vts.snystems.sns.vts.adapter.VehicleListAdapter;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.db.TABLE_NOTIFICATION;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.pojo.AlertHistoryInfo;
import vts.snystems.sns.vts.pojo.AlertInfo;
import vts.snystems.sns.vts.pojo.VehicleInfo;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;

public class AlertFragment extends Fragment
{
    String [] data = {

            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315",
            "MH14 GL1315"

    };

    private AlertAdapter vehicleListAdapter;



    View view;

    @BindView(R.id.alertRecycle)
    RecyclerView alertRecycle;

    @BindView(R.id.serachDataEditText)
    EditText serachDataEditText;

    @BindView(R.id.searchRela)
    RelativeLayout searchRela;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    ArrayList<AlertInfo> ALERT_INFO = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.alert_list, container, false);
        ButterKnife.bind(this,view);

        initialize();
        setListner();
        getLocalData();
        //setStaticData();

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
    private void getLocalData()
    {

        Cursor cursor = null;
        try
        {

            cursor = TABLE_NOTIFICATION.selectNotification();

            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext())
                {
                    String vNumber = cursor.getString(cursor.getColumnIndex(TABLE_NOTIFICATION.vNumber));
                    String vDateTime = cursor.getString(cursor.getColumnIndex(TABLE_NOTIFICATION.vDateTime));
                    String nType = cursor.getString(cursor.getColumnIndex(TABLE_NOTIFICATION.nType));
                    String nPriority = cursor.getString(cursor.getColumnIndex(TABLE_NOTIFICATION.nPriority));

                    AlertInfo alertInfo = new AlertInfo();

                    alertInfo.setVehicleNumber(vNumber);
                    alertInfo.setAlarmDateTime(vDateTime);
                    alertInfo.setnType(nType);
                    alertInfo.setnPriority(nPriority);

                    ALERT_INFO.add(alertInfo);
                }
                searchRela.setVisibility(View.VISIBLE);
                vehicleListAdapter.setAllDeviceInfo(ALERT_INFO);
                vehicleListAdapter.notifyDataSetChanged();
            }
            else
            {
                alertRecycle.setVisibility(View.GONE);

            }

        }
        catch (Exception e)
        {

        }


    }


    private void initialize()
    {

        vehicleListAdapter = new AlertAdapter(getActivity());
        alertRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        alertRecycle.setAdapter(vehicleListAdapter);

        serachDataEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence newText, int start,int before, int count)
            {
                //M.t(""+s);
                final ArrayList<AlertInfo> filteredModelList = filter(ALERT_INFO, ""+newText);
                vehicleListAdapter.setFilter(filteredModelList);
            }
        });




    }
    private ArrayList<AlertInfo> filter(ArrayList<AlertInfo> models, String query)
    {
        query = query.toLowerCase();
        final ArrayList<AlertInfo> filteredModelList = new ArrayList<>();
        filteredModelList.clear();

        if(query.length() == 0)
        {
            filteredModelList.addAll(models);
        }
        else
        {
            try
            {
                for (AlertInfo model : models)
                {

                    final String vehicleNumber = model.getVehicleNumber().toLowerCase();
//                    final String vImei = model.getvImei().toLowerCase();
//                    final String dateTime = model.getDateTime().toLowerCase();
//                    final String sos = model.getvSos().toLowerCase();
//                    final String overSpeed = model.getOverSpeed().toLowerCase();
//                    final String powerCut = model.getPowerCut().toLowerCase();
//                    final String lowBaterry = model.getLowBattery().toLowerCase();

                    //vehicleNumber
                    if (vehicleNumber.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //vImei
                    /*if (vImei.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //dateTime
                    if (dateTime.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //sos
                    if (sos.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //overSpeed
                    if (overSpeed.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //powerCut
                    if (powerCut.contains(query))
                    {
                        filteredModelList.add(model);
                    }
                    //lowBaterry
                    if (lowBaterry.contains(query))
                    {
                        filteredModelList.add(model);
                    }*/
                }
            }
            catch (Exception e)
            {

            }
        }
        return filteredModelList;
    }

}