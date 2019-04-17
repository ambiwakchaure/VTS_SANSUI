package vts.snystems.sns.sansui.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by TEJ on 10/7/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{
        static final String DATABASE_NAME = "sansui.db";
        static final int DATABASE_VERSION = 1;
        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                db.execSQL(TABLE_NOTIFICATION.CREATE_NEW_JOB_TABLE);
                db.execSQL(TABLE_STORE_GEOFENCE.CREATE_STORE_GEOFENCE);
                db.execSQL(TABLE_GEOFENCE_VEHICLE_LIST.CREATE_NEW_JOB_TABLE);
            }
            catch (SQLiteException e)
            {

            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }

    }


