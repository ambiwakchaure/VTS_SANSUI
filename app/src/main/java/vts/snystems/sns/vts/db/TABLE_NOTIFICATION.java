package vts.snystems.sns.vts.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;


public class TABLE_NOTIFICATION
{

    public static String NAME = "table_NOTIFICATION";
    public static final String LOG_TAG = "TABLE_NOTIFICATION";
    public static final String RETURN_COUNT = "returnCountt";

    public static String
            ID = "id",
            vNumber="vNumber",
            vDateTime ="vDateTime",
            createdDateTime ="createdDateTime",
            nType="nType",
            nPriority="nPriority",
            seenStatus="seenStatus";


    public static String CREATE_NEW_JOB_TABLE = "CREATE TABLE " + NAME
            + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + vNumber + " TEXT, "
            + vDateTime + " TEXT, "
            + createdDateTime + " TEXT, "
            + nType + " TEXT, "
            + nPriority + " TEXT, "
            + seenStatus + " TEXT)";


    public static void addNotification(String vNumberr,
                                       String vDateTimer,
                                       String createdDateTimer,
                                       String nTyper,
                                       String nPriorityr)
    {

        SQLiteDatabase db = MyApplication.db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(vNumber,vNumberr);
        values.put(vDateTime,vDateTimer);
        values.put(createdDateTime,createdDateTimer);
        values.put(nType,nTyper);
        values.put(nPriority,nPriorityr);
        values.put(seenStatus,"0");

        db.insert(NAME, null, values);

    }
//
    public static void deleteDateWise(String todayDate,String yesterdayDate,String thirdDate)
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
                                                  String type,
                                                  String priority)
    {


        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT COUNT(*) AS  "+RETURN_COUNT+" FROM "+NAME+" WHERE "+vNumber+" = '"+vehicle_number+"' AND "+vDateTime+" LIKE '%"+created_date+"%' AND "+nType+" = '"+type+"' AND "+nPriority+" = '"+priority+"'";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static String checkAlreadyNotificationn(String vehicle_number,
                                                  String created_date,
                                                  String type,
                                                  String priority)
    {
        String status = "2";
        Cursor cursor = null;

        try
        {


            cursor = checkAlreadyNotification(vehicle_number,created_date,type,priority);

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

    public static Cursor checkAlreadyNotificationIGN(String vehicle_number,
                                                  String created_date,
                                                  String type,
                                                  String priority)
    {


        SQLiteDatabase db = MyApplication.db.getReadableDatabase();

        Cursor cursor;

        String uQuery = "SELECT COUNT(*) AS  "+RETURN_COUNT+" FROM "+NAME+" WHERE "+vNumber+" = '"+vehicle_number+"' AND "+vDateTime+" LIKE '%"+created_date+"%' AND "+nType+" = '"+type+"' AND "+nPriority+" = '"+priority+"' ORDER BY "+ID+" DESC LIMIT 1";
        cursor = db.rawQuery(uQuery,null);
        return cursor;



    }
    public static String checkAlreadyNotificationnIGN(String vehicle_number,
                                                   String created_date,
                                                   String type,
                                                   String priority)
    {
        String status = "2";
        Cursor cursor = null;

        try
        {


            cursor = checkAlreadyNotificationIGN(vehicle_number,created_date,type,priority);

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
    }



}
