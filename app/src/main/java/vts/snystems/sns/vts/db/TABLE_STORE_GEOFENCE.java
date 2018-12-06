package vts.snystems.sns.vts.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.geofence.pojo.GeofenceChildInfo;


public class TABLE_STORE_GEOFENCE
{

    public static String NAME = "table_STORE_GEOFENCE";
    public static final String LOG_TAG = "TABLE_NOTIFICATION";
    public static final String RETURN_COUNT = "returnCountt";

    public static String
            ID = "id",
            IMEI="imei",
            VEHICLE_NUMBER="vNumber",
            ADDRESS_LATLONG ="latlong",
            RADIOUS ="radious",
            GEOFENCE_NAME="geofence_name",
            ACTIVE_STATUS="status",
            DEPARTURE="departure",
            IN_STATUS="inStatus",
            OUT_STATUS="outStatus",
            ARRIVED="arrive";


    public static String CREATE_STORE_GEOFENCE = "CREATE TABLE " + NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMEI + " TEXT , "
            + VEHICLE_NUMBER + " TEXT , "
            + ADDRESS_LATLONG + " TEXT,"
            + RADIOUS + " TEXT,"
            + GEOFENCE_NAME + " TEXT,"
            + ACTIVE_STATUS + " TEXT,"
            + ARRIVED + " TEXT  DEFAULT 'false',"
            + DEPARTURE + " TEXT DEFAULT 'false',"
            + IN_STATUS + " TEXT DEFAULT '0',"
            + OUT_STATUS + " TEXT DEFAULT '0')";



    public static void updateGeofence(String id, int radius)
    {
        SQLiteDatabase dbb = MyApplication.db.getReadableDatabase();
        dbb.execSQL("UPDATE " + NAME + " SET " + RADIOUS + "='" + radius + "' WHERE " + ID + "='" + id + "'");
    }
    public static  void updateStatus(String id, String inStatus,String outStatus)
    {
        SQLiteDatabase dbb = MyApplication.db.getReadableDatabase();
        dbb.execSQL("UPDATE " + NAME + " SET " + IN_STATUS + "='" + inStatus + "' ,"+OUT_STATUS+" = '"+outStatus+"' WHERE " + ID + "='" + id + "'");

    }

    public static void updateArriveStatus(String id,String status)
    {

        SQLiteDatabase dbb = MyApplication.db.getReadableDatabase();
        dbb.execSQL("UPDATE " + NAME + " SET " + ARRIVED + "='" + status + "' WHERE " + ID + "='" + id + "'");
    }
    public static void updateDepartStatus(String id,String status)
    {

        SQLiteDatabase dbb = MyApplication.db.getReadableDatabase();
        dbb.execSQL("UPDATE " + NAME + " SET " + DEPARTURE + "='" + status + "' WHERE " + ID + "='" + id + "'");
    }

    public static void deleteGeofence(String id)
    {

        try
        {
            SQLiteDatabase dbb = MyApplication.db.getReadableDatabase();
            dbb.execSQL(" delete from " + NAME + " where " + ID + "='" + id + "'");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String selectOutStatus(String id)
    {
        String status = "2";
        try
        {
            SQLiteDatabase db = MyApplication.db.getWritableDatabase();
            String selectQuery = "SELECT "+OUT_STATUS+" from " + NAME+" WHERE "+ID+"='"+id+"' AND "+OUT_STATUS+" = '0'";
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0)
            {

                    //String outStatus = cursor.getString(cursor.getColumnIndex(OUT_STATUS));
                    status = "1";


            }
            else
            {
                status = "0";
            }
            db.close();
            cursor.close();
        }
        catch (Exception e)
        {

        }
        return status;
    }
    public static String selectInStatus(String id)
    {
        String status = "2";
        try
        {
            SQLiteDatabase db = MyApplication.db.getWritableDatabase();
            String selectQuery = "SELECT "+IN_STATUS+" from " + NAME+" WHERE "+ID+"='"+id+"' AND "+IN_STATUS+" = '0'";
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0)
            {
               status = "1";
            }
            else
            {
                status = "0";
            }
            db.close();
            cursor.close();
        }
        catch (Exception e)
        {

        }
        return status;
    }
    public static String checkGeofenceStatus()
    {
        String status = "2";
        try
        {
            SQLiteDatabase db = MyApplication.db.getWritableDatabase();
            String selectQuery = "SELECT * from " + NAME+" WHERE "+ARRIVED+" = 'true' OR "+DEPARTURE+" = 'true'";
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0)
            {
                status = "1";
            }
            else
            {
                status = "0";
            }
            db.close();
            cursor.close();
        }
        catch (Exception e)
        {

        }
        return status;
    }
    public static ArrayList<GeoFenceObjectClass> selectGeofence(String imei)
    {

        SQLiteDatabase db = MyApplication.db.getWritableDatabase();
        String selectQuery = "SELECT * from " + NAME + " WHERE "+IMEI+" = '"+imei+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<GeoFenceObjectClass> tax_list = new ArrayList<GeoFenceObjectClass>();
        if(cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                GeoFenceObjectClass geoFenceObjectClass = new GeoFenceObjectClass();

                geoFenceObjectClass.setGeo_fence_id(cursor.getString(cursor.getColumnIndex(ID)));
                geoFenceObjectClass.setGeo_fence_name(cursor.getString(cursor.getColumnIndex(GEOFENCE_NAME)));
                geoFenceObjectClass.setGeo_fence_radius(cursor.getString(cursor.getColumnIndex(RADIOUS)));
                geoFenceObjectClass.setGetGeo_fence_lat_long(cursor.getString(cursor.getColumnIndex(ADDRESS_LATLONG)));
                geoFenceObjectClass.setGetGeo_fence_arrive_alert(cursor.getString(cursor.getColumnIndex(ARRIVED)));
                geoFenceObjectClass.setGetGeo_fence_depart_alert(cursor.getString(cursor.getColumnIndex(DEPARTURE)));
                geoFenceObjectClass.setVehicleNumber(cursor.getString(cursor.getColumnIndex(VEHICLE_NUMBER)));

                tax_list.add(geoFenceObjectClass);
            }
        }
        return tax_list;
    }
    public static void storeGeofence(String imei,
                              String vNumber,
                              String latlong,
                              String radious,
                              String status,
                              String geofence_name) {


        try
        {
            SQLiteDatabase db = MyApplication.db.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(IMEI, imei);
            values.put(VEHICLE_NUMBER, vNumber);
            values.put(ADDRESS_LATLONG, latlong);
            values.put(RADIOUS, radious);
            values.put(ACTIVE_STATUS, status);
            values.put(GEOFENCE_NAME, geofence_name);

            db.insert(NAME, null, values);

        } catch (Exception e) {
            // TODO Auto-generated catch block
        }

    }

    public static Cursor getGeofenceDetailsimei(String imei)
    {


        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT * FROM "+NAME+" WHERE "+IMEI+" = '"+imei+"'";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static ArrayList<GeofenceChildInfo> getGeofenceDetails(String imei)
    {
        ArrayList<GeofenceChildInfo> geofenceChildInfos = new ArrayList<>();
        Cursor cursor = null;

        try
        {


            cursor = getGeofenceDetailsimei(imei);

            while (cursor.moveToNext())
            {
                GeofenceChildInfo geoFenceObjectClass = new GeofenceChildInfo();

                geoFenceObjectClass.setId(cursor.getString(cursor.getColumnIndex(ID)));
                geoFenceObjectClass.setImei(cursor.getString(cursor.getColumnIndex(IMEI)));
                geoFenceObjectClass.setGeofenceLatLng(cursor.getString(cursor.getColumnIndex(ADDRESS_LATLONG)));
                geoFenceObjectClass.setGeofenceRadius(cursor.getString(cursor.getColumnIndex(RADIOUS)));
                geoFenceObjectClass.setGeofenceName(cursor.getString(cursor.getColumnIndex(GEOFENCE_NAME)));
                geoFenceObjectClass.setGeofenceActiveStatus(cursor.getString(cursor.getColumnIndex(ACTIVE_STATUS)));
                //geoFenceObjectClass.setArrived(cursor.getString(cursor.getColumnIndex(ARRIVED)));
                //geoFenceObjectClass.setDeparture(cursor.getString(cursor.getColumnIndex(DEPARTURE)));

                if(cursor.getString(cursor.getColumnIndex(ARRIVED)).equals("true"))
                {
                    geoFenceObjectClass.setGeofenceInSwitch(true);
                }
                else
                {
                    geoFenceObjectClass.setGeofenceInSwitch(false);
                }

                if(cursor.getString(cursor.getColumnIndex(DEPARTURE)).equals("true"))
                {
                    geoFenceObjectClass.setGeofenceOutSwitch(true);
                }
                else
                {
                    geoFenceObjectClass.setGeofenceOutSwitch(false);
                }

                geofenceChildInfos.add(geoFenceObjectClass);

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return geofenceChildInfos;
    }

}
