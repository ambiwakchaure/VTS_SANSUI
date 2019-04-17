package vts.snystems.sns.sansui.interfaces;

/**
 * Created by sns003 on 22-Mar-18.
 */

public interface Constants
{
    int monitorTimerDelay = 30000;
    int timerDelay = 10000;
    int timerDelayFuelOdoTrip = 15000;
    float plaback_cam_zhoom_lvl = 13.10f;
    int plaback_delayed_sec = 3000;
    int km_delayed_sec = 1000;
    int TIME_PERIOD = 30000;
    String NA = "NA";
    String ZERO = "0";
    String LTDATE_TIME = "0000-00-00 00:00:00";
    String MAP_TYPE = "map_type";
    String MAP_TYPE_NORMAL = "Normal";
    String MAP_TYPE_SAT = "Satellite";
    String MAP_TYPE_HY = "Hybrid";
    String TYPE_DATA = "type_dd";
    String PLAY_DIST = "pDist";
    String LAST_UPDATE_DATE_TIME = "date_time";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String NOTI_ALERT = "notiAlert";
    String ODOMETER = "odo";
    String FUEL = "fuel";
    String IGN_STATUS = "ign";
    String POWER_STATUS = "power";
    String GPS_STATUS = "gps";
    String DIST_LAST = "dlast";
    String PARK_LAST = "plast";
    String DUR_LAST = "durlast";
    String ICON_COLOR = "iconColor";
    String VTYPE = "vehicleType";
    String VEHICEL_TARGET_NAME = "target_name";
    String LAT_LONG = "lat_long";
    String DEVICE_STATUS = "deviceStatus";
    String COURSE = "course";
    String FRG_FLAG = "fr_flag";
    String IMAGE = "imageData";

    String APP_LANGUAGE = "aLanguage";
    String IMEI = "imei";
    String CALL_STATUS = "call_status";
    String SPEED = "speed";
    String VEHICLE_NUMBER = "v_number";
    String VEHICLE_NAME = "v_namee";
    String VEHICLE_OVER_SPEED = "v_oSpeedl";
    String vehicle_no = "vehicle_no";
    String overspeed_value = "overspeed_value";
    String target_name = "target_name";
    String DASH_PREF = "dash_PREF";
    String FRG_STATUS = "frgStatus";
    //shared preferences
    String PREF_KEY = "pref_key";
    String REMEMBER_PASSWORD = "remember_pass";

    //webservice params
    String USER_NAME = "username";
    String PASSWORD = "password";
    String VEHICLE_STATUS = "vehicleStatus";
    String DAY_FLAG = "day_flag";
    String EXP_LOG = "exception";
    String createdDate = "created_date";
    String ALL_C = "allChk";
    String LOW_BAT_C = "lowBatChk";
    String OVERSPEED_C = "overSpeedtChk";
    String SOS_C = "sosChk";
    String POWER_CUT_C = "pCutChk";
    String IGNITION_C = "ignChk";
    String LAST_NAME = "name";
    String ADDRESS = "address";
    String DATE = "date";
    String SERVICE_FLAG = "service_flag";
    String FROM_DATE = "from_date";
    String TO_DATE = "to_date";
    String OVERSPEED_DEFLT = "overSpeedtChk_deflt";
    String IGNITION_DEFLT = "ignChk_deflt";
    String uLocation = "Unknown location";
    String SOS_FC = "fContact";
    String SOS_SC = "sContact";
    String SOS_TC = "tContact";

    //-----------------------------------webservice urls--------------------------------------------
    /*String webUrl = "http://13.127.249.10/vts/api/AndroidAPI/";//server

    String login = "login";
    String getVehicleGeofence = "getVehicleGeofence";
    String vehicleUpdateInfo = "vehicleUpdateInfo";
    String getLastLatLong = "getLastLatLong";
    String insertAndroidLog = "insertAndroidLog";
    String monitor = "monitor";
    String selectProfile = "selectProfile";
    String updateProfile = "updateProfile";
    String listView = "listView";
    String alerts = "alerts";
    String notification = "notification";
    String getVehicleStatusInfo = "getVehicleStatusInfo";
    String travel_summery = "travel_summery";
    String distanceCalculation = "distanceCalculation";
    String trackingDevices_V1_0_status_updation = "trackingDevices_V1_0_status_updation";// updated date 24 Jul 2018
    String trackingDeviceInfo = "trackingDeviceInfo";
    String getLatLong1 = "getLatLong1";*/

    //-----------------------------------webservice urls--------------------------------------------
    String webUrl = "http://13.127.249.10/vts_android_restapi/api/AndroidAPI/";//server

    String validateLogin = "validateLogin";
    String getVehicleGeofence = "getVehicleGeofence";
    String updateVehicleInfo = "updateVehicleInfo";
    String getLastLatLngByUsername = "getLastLatLngByUsername";
    String insertAndroidLog = "insertAndroidLog";
    String getVehicleInfo = "getVehicleInfo";
    String getProfile = "getProfile";
    String updateProfile = "updateProfile";
    String getListView = "getListView";
    String getAlertHistory = "getAlertHistory";
    String getTravelSummary = "getTravelSummary";
    String getDistanceSummary = "getDistanceSummary";
    String getPlayBack = "getPlayBack";
    String getVehicleNotification = "getVehicleNotification";
    String getVehicleStatusInfo = "getVehicleStatusInfo";
    String getVehicleStatus = "getVehicleStatus";
    String getDeviceDetail = "getDeviceDetail";
    String getAllLatLng = "getAllLatLng";
    String getDeviceLastData_v16 = "getDeviceLastData_v16";
    String getDeviceFuel_v16 = webUrl+"getDeviceFuel_v16";
    String getDeviceOdometer_v16 = webUrl+"getDeviceOdometer_v16";
    String getDeviceLastTrip_v16 = webUrl+"getDeviceLastTrip_v16";



    //String JSON = "{\"lat_long\":[{\"created_date\":\"2018-10-10 02:09:41\",\"latitude\":\"18.46177\",\"longitude\":\"73.82965\",\"speed\":\"3\",\"course\":\"188\"},{\"created_date\":\"2018-10-10 02:09:51\",\"latitude\":\"18.46165\",\"longitude\":\"73.82969\",\"speed\":\"9\",\"course\":\"201\"},{\"created_date\":\"2018-10-10 02:13:04\",\"latitude\":\"18.46272\",\"longitude\":\"73.83072\",\"speed\":\"1\",\"course\":\"144\"},{\"created_date\":\"2018-10-10 02:13:24\",\"latitude\":\"18.46256\",\"longitude\":\"73.83080\",\"speed\":\"3\",\"course\":\"146\"},{\"created_date\":\"2018-10-10 02:14:25\",\"latitude\":\"18.46211\",\"longitude\":\"73.8296\",\"speed\":\"0\",\"course\":\"86\"},{\"created_date\":\"2018-10-10 02:15:46\",\"latitude\":\"18.46171\",\"longitude\":\"73.82998\",\"speed\":\"0\",\"course\":\"60\"},{\"created_date\":\"2018-10-10 02:15:56\",\"latitude\":\"18.46160\",\"longitude\":\"73.83005\",\"speed\":\"9\",\"course\":\"177\"},{\"created_date\":\"2018-10-10 02:22:01\",\"latitude\":\"18.47819\",\"longitude\":\"73.84758\",\"speed\":\"0\",\"course\":\"55\"},{\"created_date\":\"2018-10-10 02:22:11\",\"latitude\":\"18.47808\",\"longitude\":\"73.84755\",\"speed\":\"10\",\"course\":\"204\"},{\"created_date\":\"2018-10-10 02:23:12\",\"latitude\":\"18.47728\",\"longitude\":\"73.84705\",\"speed\":\"3\",\"course\":\"48\"},{\"created_date\":\"2018-10-10 02:30:07\",\"latitude\":\"18.46235\",\"longitude\":\"73.83090\",\"speed\":\"10\",\"course\":\"225\"},{\"created_date\":\"2018-10-10 02:30:18\",\"latitude\":\"18.46216\",\"longitude\":\"73.83056\",\"speed\":\"23\",\"course\":\"252\"},{\"created_date\":\"2018-10-10 02:31:39\",\"latitude\":\"18.46171\",\"longitude\":\"73.83016\",\"speed\":\"2\",\"course\":\"205\"},{\"created_date\":\"2018-10-10 02:31:49\",\"latitude\":\"18.46157\",\"longitude\":\"73.83009\",\"speed\":\"13\",\"course\":\"206\"},{\"created_date\":\"2018-10-10 02:31:59\",\"latitude\":\"18.46121\",\"longitude\":\"73.82955\",\"speed\":\"27\",\"course\":\"229\"},{\"created_date\":\"2018-10-10 02:33:00\",\"latitude\":\"18.46209\",\"longitude\":\"73.83070\",\"speed\":\"0\",\"course\":\"280\"},{\"created_date\":\"2018-10-10 02:33:10\",\"latitude\":\"18.46208\",\"longitude\":\"73.83062\",\"speed\":\"7\",\"course\":\"251\"},{\"created_date\":\"2018-10-10 02:33:20\",\"latitude\":\"18.46183\",\"longitude\":\"73.83043\",\"speed\":\"14\",\"course\":\"238\"},{\"created_date\":\"2018-10-10 02:34:31\",\"latitude\":\"18.45863\",\"longitude\":\"73.82632\",\"speed\":\"5\",\"course\":\"238\"},{\"created_date\":\"2018-10-10 02:34:41\",\"latitude\":\"18.45861\",\"longitude\":\"73.82612\",\"speed\":\"11\",\"course\":\"239\"},{\"created_date\":\"2018-10-10 02:35:12\",\"latitude\":\"18.45901\",\"longitude\":\"73.82679\",\"speed\":\"0\",\"course\":\"88\"},{\"created_date\":\"2018-10-10 02:35:22\",\"latitude\":\"18.45909\",\"longitude\":\"73.82690\",\"speed\":\"3\",\"course\":\"59\"},{\"created_date\":\"2018-10-10 02:36:53\",\"latitude\":\"18.45920\",\"longitude\":\"73.82679\",\"speed\":\"2\",\"course\":\"235\"},{\"created_date\":\"2018-10-10 02:37:34\",\"latitude\":\"18.45123\",\"longitude\":\"73.816\",\"speed\":\"26\",\"course\":\"57\"},{\"created_date\":\"2018-10-10 02:37:44\",\"latitude\":\"18.45153\",\"longitude\":\"73.81637\",\"speed\":\"12\",\"course\":\"50\"},{\"created_date\":\"2018-10-10 02:42:28\",\"latitude\":\"18.45315\",\"longitude\":\"73.81756\",\"speed\":\"3\",\"course\":\"255\"},{\"created_date\":\"2018-10-10 02:42:38\",\"latitude\":\"18.45305\",\"longitude\":\"73.81742\",\"speed\":\"13\",\"course\":\"215\"},{\"created_date\":\"2018-10-10 02:46:41\",\"latitude\":\"18.45373\",\"longitude\":\"73.81844\",\"speed\":\"0\",\"course\":\"328\"},{\"created_date\":\"2018-10-10 02:46:51\",\"latitude\":\"18.45393\",\"longitude\":\"73.81823\",\"speed\":\"9\",\"course\":\"303\"}],\"status\":\"1\"}";
}
