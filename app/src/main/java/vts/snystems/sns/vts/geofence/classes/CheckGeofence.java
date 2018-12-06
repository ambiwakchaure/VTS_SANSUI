package vts.snystems.sns.vts.geofence.classes;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


public class CheckGeofence
{

    public static void checkGeofence(Context context,String username,String MY_ACTION)
    {
        if(F.checkConnection())
        {
            String status = TABLE_STORE_GEOFENCE.checkGeofenceStatus();
            Log.e("imei service","status : "+status);
            if(status.equals("1"))
            {
                inOutGeofence(context,username,MY_ACTION);
            }

        }
    }
    public static  void inOutGeofence(final Context context, String username, final String MY_ACTION)
    {
        try
        {

            String[] parameters =
            {
                 Constants.USER_NAME + "#" + username
            };
            Rc.withParamsProgress(
                    new VolleyCallback()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onSuccess(String result) {

                            Log.e("imei result",""+result);
                            parseResponse(result,context,MY_ACTION);

                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {


                        }
                    },

                    Constants.webUrl + "" + Constants.getLastLatLngByUsername,
                    parameters,
                    context, "second");



        } catch (Exception e) {

        }

    }
    private static void parseResponse(String loginJson,Context context,String MY_ACTION)
    {

        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String imei = Constants.NA;

        try
        {

            if (loginJson != null || loginJson.length() > 0)
            {
                Object json = new JSONTokener(loginJson).nextValue();
                if (json instanceof JSONObject)
                {

                    JSONObject loginJsonObject1 = new JSONObject(loginJson);

                    String success = loginJsonObject1.getString("status");
                    String message = loginJsonObject1.getString("message");

                    if (success.equals("1"))
                    {
                        JSONArray notiJsonArray = loginJsonObject1.getJSONArray("lat_long");

                        for(int ii = 0; ii < notiJsonArray.length(); ii++)
                        {
                            JSONObject loginJsonObject2 =  notiJsonArray.getJSONObject(ii);

                            if (loginJsonObject2.has("latitude") && !loginJsonObject2.isNull("latitude"))
                            {
                                latitude = loginJsonObject2.getString("latitude");
                            }
                            if (loginJsonObject2.has("longitude") && !loginJsonObject2.isNull("longitude"))
                            {
                                longitude = loginJsonObject2.getString("longitude");
                            }
                            if (loginJsonObject2.has("imei") && !loginJsonObject2.isNull("imei"))
                            {
                                imei = loginJsonObject2.getString("imei");
                            }

                            double distance,radius;

                            ArrayList<GeoFenceObjectClass> GEOFENCE_DATA = TABLE_STORE_GEOFENCE.selectGeofence(imei);

                            Log.e("imei size",""+GEOFENCE_DATA.size()+" imei : "+imei);


                            if(!GEOFENCE_DATA.isEmpty())
                            {
                                for (int i = 0; i < GEOFENCE_DATA.size(); i++)
                                {

                                    GeoFenceObjectClass geoFenceObjectClass = GEOFENCE_DATA.get(i);

                                    String [] sqlLatLng =  geoFenceObjectClass.getGetGeo_fence_lat_long().split(",");

                                    String sqlLat = sqlLatLng[0];
                                    String sqlLng = sqlLatLng[1];

                                /*Log.e("imei size",""+ sqlLat);
                                Log.e("imei sqlLng",""+ sqlLng);
                                Log.e("imei latitude",""+ latitude);
                                Log.e("imei longitude",""+ longitude);*/

                                    try
                                    {
                                        if(latitude != "NA" && longitude != "NA")
                                        {
                                            Log.e("imei exp","if ok");
                                            distance = F.getDistance(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)),new LatLng(Double.valueOf(sqlLat), Double.valueOf(sqlLng))) / 1000;
                                            radius = (Double.parseDouble(geoFenceObjectClass.getGeo_fence_radius()));

                                            String arriveSt = geoFenceObjectClass.getGetGeo_fence_arrive_alert().toString();
                                            String departSt = geoFenceObjectClass.getGetGeo_fence_depart_alert().toString();
                                            String geofenceName = geoFenceObjectClass.getGeo_fence_name().toString();


                                            //for out geofence
                                            if (distance > radius)
                                            {
                                                if(departSt.equals("true"))
                                                {
                                                    String outStatus = TABLE_STORE_GEOFENCE.selectOutStatus(geoFenceObjectClass.getGeo_fence_id());
                                                    Log.e("STAUSss","outStatus : "+outStatus);
                                                    if(outStatus.equals("1"))
                                                    {
                                                        F.generateNotification(
                                                                geoFenceObjectClass,
                                                                MY_ACTION,
                                                                context,
                                                                "Departure : " + geoFenceObjectClass.getGeo_fence_name(),
                                                                "OutSide of Geofence at " + F.getSystemDateTime(),
                                                                i,
                                                                "OutSide of Geofence");
                                                        TABLE_STORE_GEOFENCE.updateStatus(geoFenceObjectClass.getGeo_fence_id(),"0","1");



                                                    }

                                                }
                                            }
                                            //for in geofence
                                            else if (distance < radius)
                                            {
                                                if (arriveSt.equals("true"))
                                                {
                                                    //get inside status
                                                    String inStatus = TABLE_STORE_GEOFENCE.selectInStatus(geoFenceObjectClass.getGeo_fence_id());
                                                    Log.e("STAUSss","inStatus : "+inStatus);

                                                    if(inStatus.equals("1"))
                                                    {
                                                        F.generateNotification(
                                                                geoFenceObjectClass,
                                                                MY_ACTION,
                                                                context,
                                                                "Arrive : " + geoFenceObjectClass.getGeo_fence_name(),
                                                                "Inside of Geofence at " + F.getSystemDateTime(),
                                                                i,
                                                                "Inside of Geofence");
                                                        TABLE_STORE_GEOFENCE.updateStatus(geoFenceObjectClass.getGeo_fence_id(),"1","0");
                                                    }

                                                }
                                            }
                                        }
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        Log.e("imei exp",""+e);
                                    }
                                }

                            }

                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
