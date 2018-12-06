package vts.snystems.sns.vts.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.geofence.pojo.GeofenceChildInfo;


public class TABLE_GEOFENCE_VEHICLE_LIST
{

    public static String NAME = "table_GEOFENCE_VEHICLE_LIST";
    public static final String LOG_TAG = "TABLE_GEOFENCE_VEHICLE_LIST";

    public static String
            ID = "id",
            IMEI="imei",
            VEHICLE_NUMBER ="vNumber",
            TARGET_NAME ="targetName",
            LAT_LNG ="latLng",
            TYPE ="vType";


    public static String CREATE_NEW_JOB_TABLE = "CREATE TABLE " + NAME
            + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMEI + " TEXT, "
            + VEHICLE_NUMBER + " TEXT, "
            + TARGET_NAME + " TEXT, "
            + LAT_LNG + " TEXT, "
            + TYPE + " TEXT)";


    public static void addVehicls(String imei,String vNumber,String targetName,String latlng,String vtype)
    {

        SQLiteDatabase db = MyApplication.db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(IMEI,imei);
        values.put(VEHICLE_NUMBER,vNumber);
        values.put(TARGET_NAME,targetName);
        values.put(LAT_LNG,latlng);
        values.put(TYPE,vtype);

        db.insert(NAME, null, values);

    }
    public static Cursor getVehicleInfo()
    {


        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT * FROM "+NAME;
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }

    public static void deleteData()
    {
        try
        {
            SQLiteDatabase db = MyApplication.db.getReadableDatabase();
            String deleteQuery = "delete from "+NAME;
            db.execSQL(deleteQuery);

        }
        catch (Exception e)
        {

        }

    }
//
    /*public static void deleteDateWise(String todayDate,String yesterdayDate,String thirdDate)
    {
        try
        {
            SQLiteDatabase db = MyApplication.db.getReadableDatabase();
            String deleteQuery = "delete from "+NAME+" WHERE "+createdDateTime+" <> '"+todayDate+"' AND "+createdDateTime+" <> '"+yesterdayDate+"' AND "+createdDateTime+" <> '"+thirdDate+"'" ;
            db.execSQL(deleteQuery);

        }
        catch (Exception e)
        {

        }

    }
    public static void deleteDateWise()
    {
        try
        {
            SQLiteDatabase db = MyApplication.db.getReadableDatabase();
            String deleteQuery = "delete from "+NAME;
            db.execSQL(deleteQuery);

        }
        catch (Exception e)
        {

        }

    }
    public static void updateSeenStatus()
    {
        try
        {
            SQLiteDatabase db = MyApplication.db.getReadableDatabase();

            String uQuery = "UPDATE "+NAME+" SET "+seenStatus+" = '1'" ;
            db.execSQL(uQuery);

        }
        catch (Exception e)
        {

        }

    }

    public static Cursor checkAlreadyNotification(String vehicle_number,
                                                  String created_date,
                                                  String systemDate,
                                                  String type,
                                                  String priority)
    {


        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT COUNT(*) AS  "+RETURN_COUNT+" FROM "+NAME+" WHERE "+vNumber+" = '"+vehicle_number+"' AND "+vDateTime+" = '"+created_date+"' AND "+createdDateTime+" = '"+systemDate+"' AND "+nType+" = '"+type+"' AND "+nPriority+" = '"+priority+"'";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static String checkAlreadyNotificationn(String vehicle_number,
                                                  String created_date,
                                                  String systemDate,
                                                  String type,
                                                  String priority)
    {
        String status = "2";
        Cursor cursor = null;

        try
        {


            cursor = checkAlreadyNotification(vehicle_number,created_date,systemDate,type,priority);

            if(cursor.moveToNext())
            {
                String counterCheck = cursor.getString(cursor.getColumnIndex(RETURN_COUNT));

                Integer ddd = Integer.valueOf(counterCheck);

                if(ddd > 0)
                {
                    status = "1";
                }
                else
                {
                    status = "0";
                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    public static Cursor selectNotification()
    {

        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;
        String uQuery = "SELECT * FROM "+NAME+" ORDER BY "+ID+" DESC";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static Cursor checkNotiExists()
    {

        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;
        String uQuery = "SELECT COUNT(*) AS  "+RETURN_COUNT+" FROM "+NAME;
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }

    public static String checkNotiExistss()
    {
        String status = "2";
        Cursor cursor = null;

        try
        {


            cursor = checkNotiExists();

            if(cursor.moveToNext())
            {
                String counterCheck = cursor.getString(cursor.getColumnIndex(RETURN_COUNT));

                Integer ddd = Integer.valueOf(counterCheck);

                if(ddd > 0)
                {
                    status = "1";
                }
                else
                {
                    status = "0";
                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }
    public static Cursor checkCounterVisible()
    {

        String status = "0";
        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT COUNT("+seenStatus+") AS couterN FROM "+NAME+" WHERE "+seenStatus+" = '"+status+"'";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static ArrayList<String> getNoticount()
    {
        ArrayList<String> notiData = new ArrayList<>();
        Cursor cursor = null;

        try
        {
            cursor = checkCounterVisible();

            if(cursor.getCount() > 0)
            {
                if(cursor.moveToNext())
                {
                    String counterCheck = cursor.getString(cursor.getColumnIndex("couterN"));

                    if(Integer.valueOf(counterCheck) > 0)
                    {
                        notiData.add(counterCheck);
                    }

                }


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return notiData;
    }*/



}
