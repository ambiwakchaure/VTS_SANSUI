package vts.snystems.sns.vts.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.pojo.VehicleInfo;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolderCarLog>
{

    private ArrayList<VehicleInfo> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public VehicleListAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setAllDeviceInfo(ArrayList<VehicleInfo> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_device_list, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position)
    {
        try
        {

            VehicleInfo carLogInformation = carlogInformation.get(position);

            holder.txt_vehicles_no.setText(carLogInformation.getvNumber());
            holder.vTargetName.setText("("+carLogInformation.getvTargetName()+")");

            String runTime = carLogInformation.getDurationTotal();
            String idleTime = carLogInformation.getIdleTime();
            String stopTime = carLogInformation.getStopTime();

            if(runTime.equals("0:0:0"))
            {
                runTime = "00:00:00";
            }
            if(idleTime.equals("0:0:0"))
            {
                idleTime = "00:00:00";
            }
            if(stopTime.equals("0:0:0"))
            {
                stopTime = "00:00:00";
            }

            holder.txt_running.setText(runTime);
            holder.txt_idle.setText(idleTime);
            holder.txt_stop.setText(stopTime);

            holder.txt_tot_stop.setText(carLogInformation.getTotalStop());
            holder.txt_avg_speed.setText(carLogInformation.getAvgSpeed()+"km/h");
            holder.txt_max_speed.setText(carLogInformation.getMaxSpeed()+"km/h");

            String vehicleType = carLogInformation.getvType();
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

    @Override
    public int getItemCount()
    {
        return carlogInformation.size();
    }

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.img_vehicles)
        ImageView img_vehicles;

        @BindView(R.id.txt_vehicles_no)
        TextView txt_vehicles_no;

        @BindView(R.id.txt_running)
        TextView txt_running;

        @BindView(R.id.txt_idle)
        TextView txt_idle;

        @BindView(R.id.txt_stop)
        TextView txt_stop;

        @BindView(R.id.txt_avg_speed)
        TextView txt_avg_speed;

        @BindView(R.id.txt_max_speed)
        TextView txt_max_speed;

        @BindView(R.id.txt_tot_stop)
        TextView txt_tot_stop;

        @BindView(R.id.vTargetName)
        TextView vTargetName;




        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);
            try
            {

                ButterKnife.bind(this,itemView);
                itemView.setOnClickListener(this);

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

                /*if (v.getId() == itemView.getId())
                {
                    //v.getContext().startActivity(new Intent(v.getContext(), ActivityPlaybackTrackInfo.class));
                    VehicleInfo vehicleInfo = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

                    Intent i = new Intent(v.getContext(), ActivityPlaybackTrackInfo.class);

                    i.putExtra(Constants.IMEI,vehicleInfo.getvImei());
                    i.putExtra(Constants.SPEED,vehicleInfo.getSpeed());
                    i.putExtra(Constants.VEHICLE_NUMBER,vehicleInfo.getvNumber());
                    i.putExtra(Constants.LAST_UPDATE_DATE_TIME,vehicleInfo.getLastDateTime());
                    i.putExtra(Constants.LATITUDE,vehicleInfo.getLatitude());
                    i.putExtra(Constants.LONGITUDE,vehicleInfo.getLongitude());
                    i.putExtra(Constants.ODOMETER,vehicleInfo.getOdometer());
                    i.putExtra(Constants.FUEL,vehicleInfo.getFuel());
                    i.putExtra(Constants.IGN_STATUS,vehicleInfo.getIgnStatus());
                    i.putExtra(Constants.POWER_STATUS,vehicleInfo.getPowerStatus());
                    i.putExtra(Constants.GPS_STATUS,vehicleInfo.getGpsStatus());
                    i.putExtra(Constants.DIST_LAST,vehicleInfo.getDistanceLastStop());
                    i.putExtra(Constants.PARK_LAST,vehicleInfo.getParkingLastStop());
                    i.putExtra(Constants.DUR_LAST,vehicleInfo.getDurationLastStop());
                    i.putExtra(Constants.DIST_TOTAL,vehicleInfo.getDistanceTotal());
                    i.putExtra(Constants.PARK_TOTAL,vehicleInfo.getParkingTotal());
                    i.putExtra(Constants.DUR_TOTAL,vehicleInfo.getDurationTotal());

                    i.putExtra(Constants.ICON_COLOR,vehicleInfo.getvColor());
                    i.putExtra(Constants.VTYPE,vehicleInfo.getvType());
                    i.putExtra(Constants.DEVICE_STATUS,vehicleInfo.getvStatus());

                    v.getContext().startActivity(i);
                }*/

               /* if (v.getId() == goNextLinearLayout.getId())
                {
                    DeviceInfo allDevicesInfo = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

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
    public void setFilter(ArrayList<VehicleInfo> countryModels)
    {
        carlogInformation = new ArrayList<>();


        carlogInformation.addAll(countryModels);



        notifyDataSetChanged();
    }


}
