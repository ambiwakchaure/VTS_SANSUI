package vts.snystems.sns.vts.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by TEJ on 10/7/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{
        static final String DATABASE_NAME = "esales.db";
        //static final int DATABASE_VERSION = 1;
        static final int DATABASE_VERSION = 2;//TABLE_STORE_GEOFENCE created
        private static final String TAG = "Database";

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
            if(oldVersion == 1 && newVersion == 2)
            {
                db.execSQL(TABLE_STORE_GEOFENCE.CREATE_STORE_GEOFENCE);
                db.execSQL(TABLE_GEOFENCE_VEHICLE_LIST.CREATE_NEW_JOB_TABLE);

            }
        }

    }


