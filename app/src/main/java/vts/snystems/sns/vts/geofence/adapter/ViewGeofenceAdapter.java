package vts.snystems.sns.vts.geofence.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.activity.ActivityEditGeofence;
import vts.snystems.sns.vts.geofence.activity.ActivityViewGeofence;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;

public class ViewGeofenceAdapter extends RecyclerView.Adapter<ViewGeofenceAdapter.ViewHolderCarLog> {

    private ArrayList<GeoFenceObjectClass> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ActivityViewGeofence activityViewGeofenceNew;
    View view;

    public ViewGeofenceAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setGeofenceData(ArrayList<GeoFenceObjectClass> countryInformationsdata,ActivityViewGeofence activityViewGeofenceNew)
    {
        this.carlogInformation = countryInformationsdata;
        this.activityViewGeofenceNew = activityViewGeofenceNew;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_geofence, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position)
    {
        try
        {

            GeoFenceObjectClass carLogInformation = carlogInformation.get(position);

            holder.txt_geo_fence_name.setText(carLogInformation.getGeo_fence_name());
            holder.geofenceRadious.setText(carLogInformation.getGeo_fence_radius());
            holder.switch_arrive.setChecked(Boolean.parseBoolean(carLogInformation.getGetGeo_fence_arrive_alert()));
            holder.switch_depart.setChecked(Boolean.parseBoolean(carLogInformation.getGetGeo_fence_depart_alert()));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return carlogInformation.size();
    }

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{


        @BindView(R.id.txt_geo_fence_name)
        TextView txt_geo_fence_name;

        @BindView(R.id.infoTextIn)
        TextView infoTextIn;

        @BindView(R.id.infoTextOut)
        TextView infoTextOut;

        @BindView(R.id.geofenceRadious)
        TextView geofenceRadious;

        @BindView(R.id.switch_arrive)
        SwitchCompat switch_arrive;

        @BindView(R.id.switch_depart)
        SwitchCompat switch_depart;

        @BindView(R.id.btn_geo_fence_view)
        Button btn_geo_fence_view;

        @BindView(R.id.btn_geo_fence_delete)
        Button btn_geo_fence_delete;


        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);
            try
            {
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
                btn_geo_fence_view.setOnClickListener(this);
                btn_geo_fence_delete.setOnClickListener(this);
                infoTextIn.setOnClickListener(this);
                infoTextOut.setOnClickListener(this);

                switch_arrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                    {
                        if(b)
                        {
                            GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                            TABLE_STORE_GEOFENCE.updateArriveStatus(geoFenceObjectClass.getGeo_fence_id(),"true");
                        }
                        else
                        {
                            GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                            TABLE_STORE_GEOFENCE.updateArriveStatus(geoFenceObjectClass.getGeo_fence_id(),"false");
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
                            GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                            TABLE_STORE_GEOFENCE.updateDepartStatus(geoFenceObjectClass.getGeo_fence_id(),"true");
                        }
                        else
                        {
                            GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                            TABLE_STORE_GEOFENCE.updateDepartStatus(geoFenceObjectClass.getGeo_fence_id(),"false");
                        }

                    }
                });

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(final View v) {
            try {

                if (v.getId() == btn_geo_fence_view.getId())
                {
                    GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

                    Intent intent = new Intent(v.getContext(), ActivityEditGeofence.class);

                    intent.putExtra("edit_lat_long", geoFenceObjectClass.getGetGeo_fence_lat_long());
                    intent.putExtra("edit_radius", geoFenceObjectClass.getGeo_fence_radius());
                    intent.putExtra("edit_radius", geoFenceObjectClass.getGeo_fence_radius());
                    intent.putExtra("edit_id", geoFenceObjectClass.getGeo_fence_id());

                    v.getContext().startActivity(intent);
                }
                if (v.getId() == btn_geo_fence_delete.getId())
                {

                    new MaterialDialog.Builder(view.getContext())
                            .title("Delete Geofence")
                            .content("Do you want to delete geofence ?")
                            .positiveText("Delete")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                {


                                    GeoFenceObjectClass geoFenceObjectClass = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                                    activityViewGeofenceNew.deleteGeofence(geoFenceObjectClass.getGeo_fence_id());
                                    dialog.dismiss();

                                }
                            })
                            .show();


                }
                if (v.getId() == infoTextIn.getId())
                {
                    showInfo(v);
                }
                if (v.getId() == infoTextOut.getId())
                {
                    showInfo(v);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private void showInfo(View view) {

        new MaterialDialog.Builder(view.getContext())
                .title("Geofence Alert Setting")
                .content("Set switch-buton to receive vehicle's Arrive-Departure in/out Geofence ")
                .positiveText("OK")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                        dialog.dismiss();

                    }
                })
                .show();
    }

}
