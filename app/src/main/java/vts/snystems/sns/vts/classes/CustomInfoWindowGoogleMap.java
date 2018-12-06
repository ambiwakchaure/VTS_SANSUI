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


public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private TextView
            vNumberTextView,
            vSpeedTextView,
            deviceStatusTextView,
            accStatusTextView,
            //coOrdinateTextView,
            lastLocationTextView,
            lastTrackTimeTextView,
            deviceImeiTextView;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.device_info_dialog, null);

        vNumberTextView = (TextView)view.findViewById(R.id.vNumberTextView);
        vSpeedTextView = (TextView)view.findViewById(R.id.vSpeedTextView);
        deviceStatusTextView = (TextView)view.findViewById(R.id.deviceStatusTextView);
        accStatusTextView = (TextView)view.findViewById(R.id.accStatusTextView);
        //coOrdinateTextView = (TextView)view.findViewById(R.id.coOrdinateTextView);
        lastLocationTextView = (TextView)view.findViewById(R.id.lastLocationTextView);
        lastTrackTimeTextView = (TextView)view.findViewById(R.id.lastTrackTimeTextView);
        deviceImeiTextView = (TextView)view.findViewById(R.id.deviceImeiTextView);

       // vNumberTextView.setText(marker.getTitle());
       // details_tv.setText(marker.getSnippet());

        DeviceInfo infoWindowData = (DeviceInfo) marker.getTag();

        String [] lastDateTime = infoWindowData.getLastTrackedTime().split(" ");
        String tmpAccStatus = infoWindowData.getAccStatus();
        String locationVisible = infoWindowData.getLocationVisibleStatus();

        if(locationVisible.equals("yes"))
        {
            lastLocationTextView.setVisibility(View.GONE);
        }
        String acc_status = null;
        if(tmpAccStatus.equals("1"))
        {
            acc_status = "ON";
        }
        else if(tmpAccStatus.equals("0"))
        {
            acc_status = "OFF";
        }

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
        String [] addresData = infoWindowData.getLastLocation().split(" ");
        String lastLocAddress = F.getAddress(Double.parseDouble(addresData[0]),Double.parseDouble(addresData[1]));

        if(lastLocAddress.equals("NA"))
        {
            lastLocationTextView.setText(Constants.uLocation);
        }
        else
        {
            lastLocationTextView.setText(lastLocAddress);
        }



        deviceImeiTextView.setText(infoWindowData.getVehicleNumber());
        vNumberTextView.setText(Html.fromHtml("<b> IMEI : "+infoWindowData.getVehicleIMEI()+"</b>"));
        vSpeedTextView.setText(Html.fromHtml("<b>Speed : </b>"+infoWindowData.getVehicleSpeed()+" kmph"));
        accStatusTextView.setText(Html.fromHtml("<b>Acc Status : </b>"+acc_status));
        //String [] data = infoWindowData.getCoOrdinate().split(" ");

        /*try {


            if (data.length > 0) {
                String lat = data[0];
                String lng = data[1];
                coOrdinateTextView.setText(Html.fromHtml("<b>Co-ordinate : </b>" + lat + "," + lng));
            }

        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }*/
        lastTrackTimeTextView.setText(Html.fromHtml("<b>Last Time : </b>"+ F.parseDate(lastDateTime[0],"Year")+" "+lastDateTime[1]));

        return view;
    }
}
