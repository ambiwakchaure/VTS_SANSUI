package vts.snystems.sns.vts.pojo;

public class AlertInfo
{

    private String vehicleNumber;
    private String alarmName;
    private String alarmDateTime;
    private String nType;
    private String nPriority;


    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmDateTime() {
        return alarmDateTime;
    }

    public void setAlarmDateTime(String alarmDateTime) {
        this.alarmDateTime = alarmDateTime;
    }

    public String getnType() {
        return nType;
    }

    public void setnType(String nType) {
        this.nType = nType;
    }

    public String getnPriority() {
        return nPriority;
    }

    public void setnPriority(String nPriority) {
        this.nPriority = nPriority;
    }
}
