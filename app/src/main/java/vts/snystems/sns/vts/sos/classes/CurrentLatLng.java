package vts.snystems.sns.vts.sos.classes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.interfaces.Constants;

public class CurrentLatLng implements LocationListener
{
    LocationManager locationManager;
    Location location;

    public void getCurrentLatLng(Context context)
    {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location == null)
        {
            M.t("Oops ! location not found, please check your location on/off setting.");
        }
        else
        {
            String latLong = getLatLong();

            String SOS_FC = MyApplication.prefs.getString(Constants.SOS_FC,"0");
            String SOS_SC = MyApplication.prefs.getString(Constants.SOS_SC,"0");
            String SOS_TC = MyApplication.prefs.getString(Constants.SOS_TC,"0");

            //first
            if(SOS_FC.contains("#") && SOS_FC != "0")
            {
                String [] SOS_FCdata = SOS_FC.split("#");
                F.sendSms(context,SOS_FCdata[1],latLong);
            }

            //second
            if(SOS_SC.contains("#") && SOS_SC != "0")
            {
                String [] SOS_SCdata = SOS_SC.split("#");
                F.sendSms(context,SOS_SCdata[1],latLong);
            }

            //third
            if(SOS_TC.contains("#") && SOS_TC != "0")
            {
                String [] SOS_TCdata = SOS_TC.split("#");
                F.sendSms(context,SOS_TCdata[1],latLong);
            }
        }
    }

    private String getLatLong()
    {

        double lt = location.getLatitude();
        double lng = location.getLongitude();
        return String.valueOf(lt)+","+String.valueOf(lng);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
