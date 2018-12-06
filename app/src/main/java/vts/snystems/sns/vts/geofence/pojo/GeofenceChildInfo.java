package vts.snystems.sns.vts.geofence.pojo;

public class GeofenceChildInfo
{
    private String id;
    private String imei;
    private String geofenceLatLng;
    private String geofenceRadius;
    private String geofenceName;
    private String geofenceActiveStatus;
    private String arrived;
    private String departure;
    private boolean geofenceInSwitch;
    private boolean geofenceOutSwitch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getGeofenceLatLng() {
        return geofenceLatLng;
    }

    public void setGeofenceLatLng(String geofenceLatLng) {
        this.geofenceLatLng = geofenceLatLng;
    }

    public String getGeofenceActiveStatus() {
        return geofenceActiveStatus;
    }

    public void setGeofenceActiveStatus(String geofenceActiveStatus) {
        this.geofenceActiveStatus = geofenceActiveStatus;
    }

    public String getArrived() {
        return arrived;
    }

    public void setArrived(String arrived) {
        this.arrived = arrived;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getGeofenceName() {
        return geofenceName;
    }

    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }

    public boolean isGeofenceInSwitch() {
        return geofenceInSwitch;
    }

    public void setGeofenceInSwitch(boolean geofenceInSwitch) {
        this.geofenceInSwitch = geofenceInSwitch;
    }

    public boolean isGeofenceOutSwitch() {
        return geofenceOutSwitch;
    }

    public void setGeofenceOutSwitch(boolean geofenceOutSwitch) {
        this.geofenceOutSwitch = geofenceOutSwitch;
    }

    public String getGeofenceRadius() {
        return geofenceRadius;
    }

    public void setGeofenceRadius(String geofenceRadius) {
        this.geofenceRadius = geofenceRadius;
    }
}
