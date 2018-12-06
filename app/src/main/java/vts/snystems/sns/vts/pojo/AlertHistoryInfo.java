package vts.snystems.sns.vts.pojo;

public class AlertHistoryInfo
{
    private String vNumber;
    private String vImei;
    private String dateTime;
    private String vSos;
    private String overSpeed;
    private String powerCut;
    private String lowBattery;
    private String vTargetName;
    private String vType;

    public String getvNumber() {
        return vNumber;
    }

    public void setvNumber(String vNumber) {
        this.vNumber = vNumber;
    }

    public String getvImei() {
        return vImei;
    }

    public void setvImei(String vImei) {
        this.vImei = vImei;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getvSos() {
        return vSos;
    }

    public void setvSos(String vSos) {
        this.vSos = vSos;
    }

    public String getOverSpeed() {
        return overSpeed;
    }

    public void setOverSpeed(String overSpeed) {
        this.overSpeed = overSpeed;
    }

    public String getPowerCut() {
        return powerCut;
    }

    public void setPowerCut(String powerCut) {
        this.powerCut = powerCut;
    }

    public String getLowBattery() {
        return lowBattery;
    }

    public void setLowBattery(String lowBattery) {
        this.lowBattery = lowBattery;
    }

    public String getvTargetName() {
        return vTargetName;
    }

    public void setvTargetName(String vTargetName) {
        this.vTargetName = vTargetName;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }
}
