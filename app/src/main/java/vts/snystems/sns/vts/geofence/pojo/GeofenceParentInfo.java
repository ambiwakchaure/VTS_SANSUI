package vts.snystems.sns.vts.geofence.pojo;

public class GeofenceParentInfo
{
    private String id;
    private String vNumber;
    private String vTargetName;
    private String vImei;
    private String vlatlng;
    private String vType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getvNumber() {
        return vNumber;
    }

    public void setvNumber(String vNumber) {
        this.vNumber = vNumber;
    }

    public String getvTargetName() {
        return vTargetName;
    }

    public void setvTargetName(String vTargetName) {
        this.vTargetName = vTargetName;
    }

    public String getvImei() {
        return vImei;
    }

    public void setvImei(String vImei) {
        this.vImei = vImei;
    }

    public String getVlatlng() {
        return vlatlng;
    }

    public void setVlatlng(String vlatlng) {
        this.vlatlng = vlatlng;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }
}
