package vts.snystems.sns.vts.fragments.monitor;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerDialogInfo implements ClusterItem
{

    private final LatLng mPosition;
    private final String name;
    private final String status;
    private final String address;
    private BitmapDescriptor icon;

    public MarkerDialogInfo(double lat, double lng, String name,String status,String address,MarkerOptions markerOptions)
    {
        mPosition = new LatLng(lat, lng);
        this.name = name;
        this.status = status;
        this.address = address;
        //this.placeType = placeType;
        this.icon = markerOptions.getIcon();
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getName() {
        return name;
    }
    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }
    /*private LatLng mPosition;
    private String vehicleNumber;
    private String vehicleStatus;
    private String vehicleLatLng;
    private BitmapDescriptor icon;

    MarkerDialogInfo(LatLng mPosition,String vehicleNumber,String vehicleStatus,String vehicleLatLng,BitmapDescriptor icon)
    {
        this.mPosition = mPosition;
        this.vehicleNumber = vehicleNumber;
        this.vehicleStatus = vehicleStatus;
        this.vehicleLatLng = vehicleLatLng;
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle()
    {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }


    public LatLng getmPosition() {
        return mPosition;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleLatLng() {
        return vehicleLatLng;
    }

    public void setVehicleLatLng(String vehicleLatLng) {
        this.vehicleLatLng = vehicleLatLng;

        String [] latlngData = vehicleLatLng.split(",");
        mPosition = new LatLng(Double.parseDouble(latlngData[0]),Double.parseDouble(latlngData[1]));
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }*/
}
