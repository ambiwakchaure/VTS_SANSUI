package vts.snystems.sns.vts.pojo;

/**
 * Created by sns003 on 22-Mar-18.
 */

public class AllDevicesInfo
{

    private String deviceStatus;
    private String deviceId;
    private String dateTime;

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
