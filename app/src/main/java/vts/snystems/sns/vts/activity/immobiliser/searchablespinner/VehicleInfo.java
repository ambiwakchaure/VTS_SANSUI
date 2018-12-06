package vts.snystems.sns.vts.activity.immobiliser.searchablespinner;

public class VehicleInfo implements Searchable {

    private String vNumber;
    private String vImei;
    private String vName;

    public VehicleInfo(String vNumber,String vImei,String vName)
    {
        this.vNumber = vNumber;
        this.vImei = vImei;
        this.vName = vName;
    }

    @Override
    public String getVNumber() {
        return vNumber;
    }

    @Override
    public String getVimei() {
        return vImei;
    }

    @Override
    public String getVName() {
        return vName;
    }

    /*public VehicleInfo setTitle(String title){
        vNumber = title;
        return this;
    }*/
}
