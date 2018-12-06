package vts.snystems.sns.vts.geofence.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.activity.ActivityCreateGeofence;
import vts.snystems.sns.vts.geofence.activity.ActivityEditGeofence;
import vts.snystems.sns.vts.geofence.activity.ActivityGeofenceList;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.geofence.pojo.GeofenceChildInfo;
import vts.snystems.sns.vts.geofence.pojo.GeofenceParentInfo;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;


public class GeofenceListAdapter extends BaseExpandableListAdapter
{

    private Context _context;
    private ArrayList<GeofenceParentInfo> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<GeofenceChildInfo>> _listDataChild;
    private ActivityGeofenceList activityGeofenceList;


    public GeofenceListAdapter(Context context,
                               ArrayList<GeofenceParentInfo> listDataHeader,
                               HashMap<String, ArrayList<GeofenceChildInfo>> listChildData,
                               ActivityGeofenceList activityGeofenceList)
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.activityGeofenceList = activityGeofenceList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        GeofenceParentInfo geofenceParentInfo = _listDataHeader.get(groupPosition);
        return this._listDataChild.get(geofenceParentInfo.getId()).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final GeofenceChildInfo geofenceChildInfo = (GeofenceChildInfo) getChild(groupPosition, childPosition);
        final GeofenceParentInfo geofenceParentInfo = (GeofenceParentInfo) getGroup(groupPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.geofence_child_info_row, null);
        }

        TextView txt_geo_fence_name = (TextView) convertView.findViewById(R.id.txt_geo_fence_name);
        TextView geofenceRadious = (TextView) convertView.findViewById(R.id.geofenceRadious);
        final TextView txt_geo_fence_address = (TextView) convertView.findViewById(R.id.txt_geo_fence_address);
        //LinearLayout geoNotFoundLinear = (LinearLayout) convertView.findViewById(R.id.geoNotFoundLinear);
        Button btn_geo_fence_view = (Button) convertView.findViewById(R.id.btn_geo_fence_view);
        Button btn_geo_fence_delete = (Button) convertView.findViewById(R.id.btn_geo_fence_delete);
        CardView cardVievGeofence = (CardView) convertView.findViewById(R.id.cardVievGeofence);

        final SwitchCompat switch_arrive = (SwitchCompat) convertView.findViewById(R.id.switch_arrive);
        final SwitchCompat switch_depart = (SwitchCompat) convertView.findViewById(R.id.switch_depart);

        String geofenceName = geofenceChildInfo.getGeofenceName();
        if(geofenceName.equals("NA"))
        {
            // geoNotFoundLinear.setVisibility(View.VISIBLE);
            cardVievGeofence.setVisibility(View.GONE);
            F.displayDialog(_context,"Geofence List","Geofence not created, please click on Create button to create new geofence.");

        }
        else
        {
            ///geoNotFoundLinear.setVisibility(View.GONE);
            cardVievGeofence.setVisibility(View.VISIBLE);
            txt_geo_fence_name.setText(geofenceChildInfo.getGeofenceName());

            switch_arrive.setChecked(geofenceChildInfo.isGeofenceInSwitch());
            switch_depart.setChecked(geofenceChildInfo.isGeofenceOutSwitch());
            geofenceRadious.setText(geofenceChildInfo.getGeofenceRadius());
            String [] addressData =  geofenceChildInfo.getGeofenceLatLng().split(",");
            txt_geo_fence_address.setText(Html.fromHtml("<b>Address : </b>"+F.getAddress(Double.valueOf(addressData[0]),Double.valueOf(addressData[1]))));

            Log.e("AMOLAA","geofenceName : "+geofenceName);
        }


        //create geofence here

        btn_geo_fence_view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                if(F.checkConnection())
                {
                    Intent intent = new Intent(_context, ActivityEditGeofence.class);

                    intent.putExtra("edit_lat_long",geofenceChildInfo.getGeofenceLatLng());
                    intent.putExtra("edit_radius", geofenceChildInfo.getGeofenceRadius());
                    intent.putExtra("edit_id", geofenceChildInfo.getId());
                    intent.putExtra("geofence_address", txt_geo_fence_address.getText().toString());
                    intent.putExtra(Constants.LAT_LONG,geofenceParentInfo.getVlatlng());
                    intent.putExtra(Constants.VTYPE,geofenceParentInfo.getvType());
                    ((Activity)_context).startActivityForResult(intent,3);
                }
                else
                {
                    M.t(VMsg.connection);
                }

            }
        });
        btn_geo_fence_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //HashMap<String, ArrayList<GeofenceChildInfo>> _listDataChild;
                TABLE_STORE_GEOFENCE.deleteGeofence(geofenceChildInfo.getId());
                activityGeofenceList.refreshList();
                M.t(geofenceChildInfo.getGeofenceName()+" geofence deleted.");

            }
        });
        switch_arrive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(switch_arrive.isChecked())
                {
                    Log.e("EXPANDABLE_LIST","switch_arrive : checked");
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceInSwitch(true);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();

                    TABLE_STORE_GEOFENCE.updateArriveStatus(geofenceChildInfo.getId(),"true");
                }
                else
                {
                    Log.e("EXPANDABLE_LIST","switch_arrive : unchecked");
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceInSwitch(false);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();
                    TABLE_STORE_GEOFENCE.updateArriveStatus(geofenceChildInfo.getId(),"false");
                }
            }
        });
        switch_depart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(switch_depart.isChecked())
                {
                    Log.e("EXPANDABLE_LIST","switch_depart : checked");
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceOutSwitch(true);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();
                    TABLE_STORE_GEOFENCE.updateDepartStatus(geofenceChildInfo.getId(),"true");


                }
                else
                {
                    Log.e("EXPANDABLE_LIST","switch_depart : unchecked");
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceOutSwitch(false);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();
                    TABLE_STORE_GEOFENCE.updateDepartStatus(geofenceChildInfo.getId(),"false");

                }
            }
        });
        /*switch_arrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                //M.t(geofenceChildInfo.getId());
                if(b)
                {
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    *//*GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceInSwitch(true);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();*//*

                    TABLE_STORE_GEOFENCE.updateArriveStatus(geofenceChildInfo.getId(),"true");

                }
                else
                {
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    *//*GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceInSwitch(false);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();*//*
                    TABLE_STORE_GEOFENCE.updateArriveStatus(geofenceChildInfo.getId(),"false");
                }

            }
        });
        switch_depart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                if(b)
                {
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                    *//*GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceOutSwitch(true);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();*//*
                    TABLE_STORE_GEOFENCE.updateDepartStatus(geofenceChildInfo.getId(),"true");
                }
                else
                {
                    ArrayList<GeofenceChildInfo> tempGeofenceChildInfos = _listDataChild.get(geofenceParentInfo.getId());
                    Log.e("EXPANDABLE_LIST","tempGeofenceChildInfos.size() : "+tempGeofenceChildInfos.size());

                   *//* GeofenceChildInfo tempGeofenceChildInfo1 = tempGeofenceChildInfos.get(childPosition);
                    tempGeofenceChildInfo1.setGeofenceOutSwitch(false);
                    _listDataChild.put(geofenceParentInfo.getId(),tempGeofenceChildInfos);

                    notifyDataSetChanged();*//*
                    TABLE_STORE_GEOFENCE.updateDepartStatus(geofenceChildInfo.getId(),"false");
                }

            }
        });*/

        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        GeofenceParentInfo geofenceParentInfo = _listDataHeader.get(groupPosition);
        return this._listDataChild.get(geofenceParentInfo.getId())
                .size();

    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
    {
        GeofenceParentInfo geofenceParentInfo = (GeofenceParentInfo) getGroup(groupPosition);

        TextView
                vTargetNameTxt,
                vNumberTxt;

        Button
                createGeofenceBtn;



        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.geofence_parent_info_row, null);
        }

        //init views
        vTargetNameTxt = (TextView) convertView.findViewById(R.id.vTargetNameTxt);
        vNumberTxt = (TextView) convertView.findViewById(R.id.vNumberTxt);
        createGeofenceBtn = (Button) convertView.findViewById(R.id.createGeofenceBtn);
        createGeofenceBtn.setFocusable(false);


        String id = geofenceParentInfo.getId();
        final String vNumber = geofenceParentInfo.getvNumber();
        String vTargetName = geofenceParentInfo.getvTargetName();
        final String vImei = geofenceParentInfo.getvImei();
        final String latlng = geofenceParentInfo.getVlatlng();
        final String vType = geofenceParentInfo.getvType();

        vNumberTxt.setText(vNumber);
        vTargetNameTxt.setText(vTargetName);


        createGeofenceBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (F.checkPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    ArrayList<GeoFenceObjectClass> GEOFENCE_DATA = TABLE_STORE_GEOFENCE.selectGeofence(vImei);
                    if (GEOFENCE_DATA.size() < 3)
                    {
                        Intent i = new Intent(_context, ActivityCreateGeofence.class);
                        i.putExtra(Constants.IMEI,vImei);
                        i.putExtra(Constants.VEHICLE_NUMBER,vNumber);
                        i.putExtra(Constants.LAT_LONG,latlng);
                        i.putExtra(Constants.VTYPE,vType);
                        ((Activity)_context).startActivityForResult(i,4);
                    }
                    else
                    {
                        new MaterialDialog.Builder(_context)
                                .title("Oops !")
                                .content(R.string.s_go)
                                .positiveText(R.string.delete)
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback()
                                {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                    {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
                else F.askPermission(_context,Manifest.permission.ACCESS_FINE_LOCATION);


            }
        });
        /*btn_geo_fence_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {



            }
        });*/
        return convertView;
    }


    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
