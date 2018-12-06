package vts.snystems.sns.vts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.pojo.AlertHistoryInfo;


public class AlertHistAdapter extends RecyclerView.Adapter<AlertHistAdapter.ViewHolderCarLog>
{

    private ArrayList<AlertHistoryInfo> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public AlertHistAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setAllDeviceInfo(ArrayList<AlertHistoryInfo> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.alert_history_row, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {

            AlertHistoryInfo carLogInformation = carlogInformation.get(position);

            holder.txt_vehicles_no.setText(carLogInformation.getvNumber());
            holder.txtImei.setText(carLogInformation.getvImei());
            holder.txtSos.setText(carLogInformation.getvSos());
            holder.vTargetName.setText("("+carLogInformation.getvTargetName()+")");
            holder.txtOverSpeed.setText(carLogInformation.getOverSpeed());
            holder.txtPowerCut.setText(carLogInformation.getPowerCut());
            holder.txtLowBattery.setText(carLogInformation.getLowBattery());

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

        @BindView(R.id.txtImei)
        TextView txtImei;



        @BindView(R.id.txtSos)
        TextView txtSos;

        @BindView(R.id.vTargetName)
        TextView vTargetName;

        @BindView(R.id.txtOverSpeed)
        TextView txtOverSpeed;

        @BindView(R.id.txtPowerCut)
        TextView txtPowerCut;

        @BindView(R.id.txtLowBattery)
        TextView txtLowBattery;



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

                itemView.setOnClickListener(this);
                goNextLinearLayout.setOnClickListener(this);*/

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

                /*if (v.getId() == goNextLinearLayout.getId())
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
    public void setFilter(ArrayList<AlertHistoryInfo> countryModels)
    {
        carlogInformation = new ArrayList<>();


        carlogInformation.addAll(countryModels);



        notifyDataSetChanged();
    }



}
