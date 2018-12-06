package vts.snystems.sns.vts.classes;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.pojo.DeviceInfo;

public class LiveTrackInfoDialog implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private TextView
            vNumberTextView,
            deviceStatusTextView,
            lastLocationTextView;

    public LiveTrackInfoDialog(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.device_info_dialog_cluster, null);

        vNumberTextView = (TextView)view.findViewById(R.id.vNumberTextView);
        deviceStatusTextView = (TextView)view.findViewById(R.id.deviceStatusTextView);;
        lastLocationTextView = (TextView)view.findViewById(R.id.lastLocationTextView);

        DeviceInfo infoWindowData = (DeviceInfo) marker.getTag();

        String deviceStatus = infoWindowData.getDeviceStatus();

        if(deviceStatus.equals("MV"))//moving
        {
            deviceStatusTextView.setText(Html.fromHtml("<b>Device Status : </b>Running"));
        }
        else if(deviceStatus.equals("ST"))//static
        {
            deviceStatusTextView.setText(Html.fromHtml("<b>Device Status : </b>Idle"));
        }
        else if(deviceStatus.equals("SP"))//static(parking)
        {
            deviceStatusTextView.setText(Html.fromHtml("<b>Device Status : </b>Parking"));
        }
        else if(deviceStatus.equals("OFF"))//offline
        {
            deviceStatusTextView.setText(Html.fromHtml("<b>Device Status : </b>Offline"));
        }
        vNumberTextView.setText(MyApplication.prefs.getString(Constants.VEHICLE_NUMBER,""));
        lastLocationTextView.setVisibility(View.GONE);
        return view;
    }
}
