package vts.snystems.sns.vts.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.HomeActivity;
import vts.snystems.sns.vts.db.TABLE_NOTIFICATION;
import vts.snystems.sns.vts.errorHandler.ErrorActivity;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.pojo.DeviceInfo;
import vts.snystems.sns.vts.sos.activity.ActivityAddContacts;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class F
{
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void generateNotification(
            GeoFenceObjectClass geoFenceObjectClass,
            String MY_ACTION,
            Context context,
            String notification_title,
            String notification_message,
            int id,String message)
    {

        Log.e("NOTIFICATION","in");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.e("NOTIFICATION","o");
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(notification_title)
                    .setContentText(notification_message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();


            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(id , notification);
            mNotificationManager.createNotificationChannel(mChannel);



            //add geofence notification
            TABLE_NOTIFICATION.addNotification(
                    geoFenceObjectClass.getVehicleNumber(),
                    F.getSystemDateTime(),
                    F.getSystemDate(),
                    geoFenceObjectClass.getGeo_fence_name()+" : "+message,
                    "NA");


            //update dashboard count
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra("DATAPASSED", "update");
            context.sendBroadcast(intent);
        }
        else
        {
            Log.e("NOTIFICATION","not o");
            //for notification icon
            int icon = R.mipmap.ic_launcher;
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);


            Notification.Builder builder = new Notification.Builder(context);

            builder.setSmallIcon(icon)
                    .setContentTitle(notification_title)
                    .setAutoCancel(true)
                    //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/amol"))
                    .setContentText(notification_message);
//              .setContentIntent(intent);
            Notification notification1 = builder.getNotification();
            notificationManager.notify(id, notification1);

            //add geofence notification
            TABLE_NOTIFICATION.addNotification(
                    geoFenceObjectClass.getVehicleNumber(),
                    F.getSystemDateTime(),
                    F.getSystemDate(),
                    geoFenceObjectClass.getGeo_fence_name()+" : "+message,
                    "NA");

            Log.e("NOTIFICATION_ALERT","TABLE_NOTIFICATION.addNotification 1");


            //update dashboard count
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra("DATAPASSED", "update");
            context.sendBroadcast(intent);
        }

    }

    public static Circle createGeofence(GoogleMap mMap, double latitude, double longitude, int radious)
    {
        Circle circle = null;
        try
        {
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(radious)
                    .fillColor(Color.parseColor("#51000000"))
                    .strokeColor(Color.RED)
                    .strokeWidth(2));
        }
        catch (Exception e)
        {

        }

        return circle;


    }
    public static void addSoScontacts(final Context context)
    {


        MaterialDialog md = new MaterialDialog.Builder(context)
                    .title(R.string.add_sost)
                    .content(R.string.sos_add)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();

                            Intent i = new Intent(context, ActivityAddContacts.class);
                            context.startActivity(i);

                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                        }
                    })
                    .show();
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "TitilliumWeb-Regular.ttf");
        md.getTitleView().setTypeface(tf);
        md.getContentView().setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
        md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
        md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);

    }
    public static void sendSms(Context context, String mobile,String latLong)
    {
       String [] data = latLong.split(",");
        //String smsText = "Welcome in benosys SoS";
        String link = "http://maps.google.com/maps?q=loc:"+data[0]+","+data[1];
        String smsText = "User Name : "+MyApplication.prefs.getString(Constants.USER_NAME,"0")+",Location:"+link+"";
        Intent intent =new Intent();
        PendingIntent pi=PendingIntent.getActivity(context, 0, intent,0);

        SmsManager sms= SmsManager.getDefault();
        sms.sendTextMessage(mobile, null, smsText, pi,null);


        M.t("Message Sent successfully!");

    }
    public static boolean checkPermission(Context context,String permission)
    {

        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED );

    }
    public static void askPermission(Context context,String permission)
    {

        ActivityCompat.requestPermissions((Activity) context,new String[]{permission},12);
    }

    public static String  retrieveContactNumber(Context context,Uri uriContact) {

        String contactNumber = null;
        String contactID = null;
        try
        {

            // getting contacts ID
            Cursor cursorID = context.getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);

            if (cursorID.moveToFirst()) {

                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }

            cursorID.close();



            // Using the contact ID now we will get contact phone number
            Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                    new String[]{contactID},
                    null);

            if (cursorPhone.moveToFirst())
            {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //  contactName = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            cursorPhone.close();

            //Log.d("Contact", "Number:" + contactNumber.replace(" ",""));
        }
        catch (Exception e)
        {
        }

        return contactNumber.replace(" ","");
    }
    public static String retrieveContactName(Context context,Uri uriContact)
    {

        String contactName = null;
        try
        {


            // querying contact data store
            Cursor cursor = context.getContentResolver().query(uriContact, null, null, null, null);

            if (cursor.moveToFirst()) {

                // DISPLAY_NAME = The display name for the contact.
                // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            cursor.close();

            //Log.d("Contact", "Name:" + contactName);
        }
        catch (Exception e)
        {

        }

        return contactName;

    }
    public static void setSpeedo(String speed, String overSpeed, LinearLayout linearLayout, TextView textView)
    {
        Float speedD = Float.valueOf(speed);
        Float overSpeedd = Float.valueOf(overSpeed);

        if(speedD == 0.0)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_onenormal);
            textView.setText(speed);
        }
        else if(speedD > 0 && speedD < 10)
        {

            linearLayout.setBackgroundResource(R.drawable.ic_onenormal);
            textView.setText(speed);
        }
        else if(speedD > 10 && speedD < 20)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_sone);
            textView.setText(speed);
        }
        else if(speedD > 20 && speedD < 30)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_stwo);
            textView.setText(speed);
        }
        else if(speedD > 30 && speedD < 40)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_sthree);
            textView.setText(speed);
        }
        else if(speedD > 40 && speedD < 50)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_sfour);
            textView.setText(speed);
        }
        else if(speedD > 50 && speedD < 60)
        {
            linearLayout.setBackgroundResource(R.drawable.ic_sfive);
            textView.setText(speed);
        }
        else if(speedD > 60 && speedD < 70)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_ssixo);
                textView.setText(speed);

            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_ssix);
                textView.setText(speed);
            }

        }
        else if(speedD > 70 && speedD < 80)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_sseveno);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_sseven);
                textView.setText(speed);
            }

        }
        else if(speedD > 80 && speedD < 90)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_eighto);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_eight);
                textView.setText(speed);
            }

        }
        else if(speedD > 90 && speedD < 100)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_nineo);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_nine);
                textView.setText(speed);
            }

        }
        else if(speedD > 100 && speedD < 110)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_teno);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_ten);
                textView.setText(speed);
            }

        }
        else if(speedD > 110 && speedD < 120)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_eleveno);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_eleven);
                textView.setText(speed);
            }

        }
        else if(speedD > 120)
        {
            if(speedD >= overSpeedd)
            {
                linearLayout.setBackgroundResource(R.drawable.ic_twelveo);
                textView.setText(speed);
            }
            else
            {
                linearLayout.setBackgroundResource(R.drawable.ic_twelve);
                textView.setText(speed);
            }
        }
    }
    public static void setDefaultMap(GoogleMap mMap)
    {
        LatLng sydney = new LatLng(20.593684, 78.962880);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public static String getErrorJson(String exceptionLog)
    {
        String errorJson = null;
        try
        {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("exception",exceptionLog);
            jsonObject.put("Brand",Build.BRAND);
            jsonObject.put("Device",Build.DEVICE);
            jsonObject.put("Model",Build.MODEL);
            jsonObject.put("Id",Build.ID);
            jsonObject.put("Product",Build.PRODUCT);
            jsonObject.put("SDK",Build.VERSION.SDK_INT);
            jsonObject.put("Release",Build.VERSION.RELEASE);
            jsonObject.put("Incremental",Build.VERSION.INCREMENTAL);


            jsonArray.put(jsonObject);

            errorJson = jsonArray.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return errorJson;
    }
    public static void handleError(VolleyError error, Context context,String expLocation)
    {
        try
        {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            String errorReport = responseBody.replaceAll("\\<.*?>","").replaceAll("\n","");

            if (error instanceof NoConnectionError) {


                try
                {

                }
                catch (Exception e)
                {
                    String errorLog = getErrorJson(errorReport);
                    if(errorLog != null)
                    {
                        Intent i = new Intent(context, ErrorActivity.class);
                        i.putExtra("error",responseBody);
                        i.putExtra("json","Location : "+expLocation+"Exception : "+errorLog);
                        context.startActivity(i);
                    }
                }

               // Log.e("responseBody",""+errorLog);
                //F.displayDialog(context,"NoConnectionError",""+Html.fromHtml(responseBody));

            }
            else if (error instanceof AuthFailureError)
            {

                try
                {


                    String errorLog = getErrorJson(errorReport);
                    if(errorLog != null)
                    {
                        Intent i = new Intent(context, ErrorActivity.class);
                        i.putExtra("error",responseBody);
                        i.putExtra("json","Location : "+expLocation+"Exception : "+errorLog);
                        context.startActivity(i);
                    }
                }
                catch (Exception e)
                {

                }
                //Log.e("responseBody",""+errorLog);
                //F.displayDialog(context,"AuthFailureError",""+Html.fromHtml(responseBody));
                // sR("Warning ! Remote server returns (401) Unauthorized?.", "AuthFailure Error",context,imei);

            }
            else if (error instanceof ServerError)
            {

                try
                {
                String errorLog = getErrorJson(errorReport);
                if(errorLog != null)
                {
                    Intent i = new Intent(context, ErrorActivity.class);
                    i.putExtra("error",responseBody);
                    i.putExtra("json","Location : "+expLocation+"Exception : "+errorLog);
                    context.startActivity(i);
                }
            }
                catch (Exception e)
            {

            }


                // Log.e("responseBody",""+errorReport);
                //F.displayDialog(context,"ServerError",""+ Html.fromHtml(responseBody));

            }
            else if (error instanceof NetworkError)
            {
                try
                {

                String errorLog = getErrorJson(errorReport);
                if(errorLog != null)
                {
                    Intent i = new Intent(context, ErrorActivity.class);
                    i.putExtra("error",responseBody);
                    i.putExtra("json","Location : "+expLocation+"Exception : "+errorLog);
                    context.startActivity(i);
                }
            }
                catch (Exception e)
            {

            }
               // Log.e("responseBody",""+errorLog);
                //F.displayDialog(context,"NetworkError",""+Html.fromHtml(responseBody));
                // sR("Warning ! You doesn't have a data connection or wi-fi Connection.", "Network Error",context,imei);

            }
            else if (error instanceof ParseError)
            {

                try
                {

                String errorLog = getErrorJson(responseBody);
                if(errorLog != null)
                {
                    Intent i = new Intent(context, ErrorActivity.class);
                    i.putExtra("error",responseBody);
                    i.putExtra("json","Location : "+expLocation+"Exception : "+errorLog);
                    context.startActivity(i);
                }
                }
                catch (Exception e)
                {

                }
               // Log.e("responseBody",""+errorLog);
                //F.displayDialog(context,"ParseError",""+Html.fromHtml(responseBody));
                //sR("Warning ! Incorrect json response.", "Parse Error",context,imei);

            }

        } catch (Exception e) {

        }

    }

    public static Marker setMarkerVehicleIconType(String vehicleType,GoogleMap mMap, LatLng latLng,Float course)
    {
        Marker marker;
        if(vehicleType.equals("MC"))
        {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("CR"))
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("TR"))//tractor
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("TK"))//trucks
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("CE"))//crane
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("CN"))//container
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("BL"))//bacoloader
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("TN"))//tanker
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tanker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("BS"))//bus
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("MR"))//marker
        {

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);


        }
        else if(vehicleType.equals("GC"))
        {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);
        }
        else
        {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
            marker.setRotation(course);
        }

        return marker;
    }

    public static double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159f;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1) * sin(lat2) - sin(lat1)
                * cos(lat2) * cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    private static double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    private static double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    public static double getBearing(LatLng latLng1,LatLng latLng2) {

//Source

        double lat1 = latLng1.latitude;
        double lng1 = latLng1.longitude;

// destination
        //JSONObject destination = step.getJSONObject("end_location");
        double lat2 = latLng2.latitude;
        double lng2 = latLng2.longitude;

        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        double degree = radiansToDegree(Math.atan2(sin(dLon) * cos(tLat),
                cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }
    public static String getMonthDate()
    {
        Calendar c = Calendar.getInstance();

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_MONTH, 1);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);


        return df.format(c.getTime());
    }

    public static String getWeekDate()
    {
        Calendar c = Calendar.getInstance();

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);


        return df.format(c.getTime());

    }
    public static String getSevenDayDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        return df.format(cal.getTime());

    }
    public static List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    /*public static String getMapsApiDirectionsUrl(ArrayList<LatLng> markers_route,Context context)
    {
        StringBuilder urlString = new StringBuilder();
        for (int i = 2; i < markers_route.size() - 1; i++)
        {
            urlString.append('|');
            urlString.append(markers_route.get(i).latitude);
            urlString.append(',');
            urlString.append(markers_route.get(i).longitude);
        }
        String OriDest = "origin=" + markers_route.get(0).latitude + "," + markers_route.get(0).longitude + "&destination="
                + markers_route.get(markers_route.size() - 1).latitude + "," + markers_route.get(markers_route.size() - 1).longitude;

        String sensor = "sensor=false&mode=driving";
        //String sensor = "sensor=false&mode=walking";
        String params = OriDest + "&" + "waypoints=optimize:true" + urlString + "&"
                + sensor + "&key=" + context.getResources().getString(R.string.google_maps_key);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
        Log.d("url>>>", ">>" + url);
        return url;
    }*/
    public static  void cancelTimer(TimerTask mTimerTaskZhoom)
    {
        if(mTimerTaskZhoom!=null)
        {
            mTimerTaskZhoom.cancel();
        }
    }
    public static void genNotification(Context context,
                                       int i,
                                       String vehicle_number,
                                       String type,
                                       String created_date,
                                       String priority,
                                       String MY_ACTION)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {


            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(vehicle_number)
                    .setContentText(type)
                    .setSmallIcon(R.drawable.ic_car)
                    .setChannelId(CHANNEL_ID)
                    .build();


            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(i , notification);
            mNotificationManager.createNotificationChannel(mChannel);


            TABLE_NOTIFICATION.addNotification(
                    vehicle_number,
                    created_date,
                    F.getSystemDate(),
                    type,
                    priority);

            M.e("NOTIFICATION","call");
            //update dashboard count
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra("DATAPASSED", "update");
            context.sendBroadcast(intent);
        }
        else
        {
            F.generateNoticesNotification(i,vehicle_number,priority,type,context);

            TABLE_NOTIFICATION.addNotification(
                    vehicle_number,
                    created_date,
                    F.getSystemDate(),
                    type,
                    priority);

            M.e("NOTIFICATION","call");
            //update dashboard count
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra("DATAPASSED", "update");
            context.sendBroadcast(intent);
        }
    }
    public static void setLanguage(String language)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            //config.locale = locale;
            setSystemLocale(config, locale);
            MyApplication.context.getResources().updateConfiguration(config,MyApplication.context.getResources().getDisplayMetrics());

        }
        else
        {
            //String languageToLoad  = "mr"; // your language
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            MyApplication.context.getResources().updateConfiguration(config,MyApplication.context.getResources().getDisplayMetrics());
        }

    }
    public static void setVehicleIconType(ImageView imageView,String vehicleType)
    {
        if(vehicleType.equals("MC"))
        {
            imageView.setImageResource(R.drawable.ic_bike);//bike
        }
        else if(vehicleType.equals("CR"))//car
        {
            imageView.setImageResource(R.drawable.ic_car);
        }
        else if(vehicleType.equals("TR"))//tractor
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else if(vehicleType.equals("TK"))//trucks
        {
            imageView.setImageResource(R.drawable.ic_truck);
        }
        else if(vehicleType.equals("CE"))//crane
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else if(vehicleType.equals("CN"))//container
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else if(vehicleType.equals("BL"))//bacoloader
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else if(vehicleType.equals("TN"))//tanker
        {
            imageView.setImageResource(R.drawable.ic_tanker);
        }
        else if(vehicleType.equals("BS"))//bus
        {
            imageView.setImageResource(R.drawable.ic_bus);
        }
        else if(vehicleType.equals("MR"))//marker
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else if(vehicleType.equals("GC"))
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
        else
        {
            imageView.setImageResource(R.drawable.ic_marker);
        }
    }
    public static void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

    public static void setImage(CircleImageView imge_profile) {

        String image = MyApplication.prefs.getString(Constants.IMAGE,"0");

        if(image.equals("0"))
        {
            imge_profile.setImageResource(R.drawable.ic_profile_demo);
        }
        else
        {
            Bitmap bitmap = F.readImage(image);
            imge_profile.setImageBitmap(bitmap);
        }

    }

    public static Bitmap readImage(String imageData)
    {

        byte[] b = Base64.decode(imageData, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public static void storeImage(Bitmap bitmap)
    {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50, baos);
        byte[] arr = baos.toByteArray();
        String imageData = Base64.encodeToString(arr, Base64.DEFAULT);

        MyApplication.editor.putString(Constants.IMAGE,imageData).commit();
    }

    public static String getSystemNextDate(String curDate)
    {

        String systemTime = "";

        try
        {
            final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
            final Date date = format.parse(curDate);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            systemTime= format.format(calendar.getTime());


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return systemTime;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void sendNotification(int NOTIFICATION_ID,String messageBody, Context context)
    {
        final String GROUP_KEY = "GROUP_KEY_RANDOM_NAME";

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent onCancelNotificationReceiver = new Intent(context, HomeActivity.class);
        PendingIntent onCancelNotificationReceiverPendingIntent = PendingIntent.getBroadcast(context, 0,onCancelNotificationReceiver, 0);
        String notificationHeader = context.getResources().getString(R.string.app_name);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = manager.getActiveNotifications();
        for (int i = 0; i < notifications.length; i++)
        {
            if (notifications[i].getPackageName().equals(context.getPackageName()))
            {
                //Log.d("Notification", notifications[i].toString());
                Intent startNotificationActivity = new Intent(context, HomeActivity.class);
                startNotificationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startNotificationActivity,PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car))
                        .setContentTitle(notificationHeader)
                        .setContentText("Tap to open")
                        .setAutoCancel(true)
                        .setStyle(getStyleForNotification(messageBody,context))
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                        .build();
                SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationData", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
                editor.apply();
                notificationManager.notify(NOTIFICATION_ID, notification);
                return;
            }
        }
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
        Notification notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_car)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car))
                .setContentTitle(notificationHeader)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                //.setContentIntent(pendingIntent)
                .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                .build();
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationData", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
        editor.apply();
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder);
    }

    public static NotificationCompat.InboxStyle getStyleForNotification(String messageBody,Context context)
    {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        SharedPreferences sharedPref = context.getSharedPreferences("NotificationData", 0);
        Map<String, String> notificationMessages = (Map<String, String>) sharedPref.getAll();
        Map<String, String> myNewHashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : notificationMessages.entrySet())
        {
            myNewHashMap.put(entry.getKey(), entry.getValue());
        }
        inbox.addLine(messageBody);
        for (Map.Entry<String, String> message : myNewHashMap.entrySet())
        {
            inbox.addLine(message.getValue());
        }
        inbox.setBigContentTitle(context.getResources().getString(R.string.app_name)).setSummaryText("Tap to open");
        return inbox;
    }
    public static void generateNoticesNotification(int notification_id,
                                                   String title,
                                                   String subtitle,
                                                   String content,
                                                   Context context)
    {

        try
        {
            // String title = "IISER";
            //String subtitle = str_notice_title;
            Intent intent;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title).setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            intent = new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            //intent.putExtra(IISERApp.NOTIFICATION_ID, notification_id);
            mBuilder.setTicker(subtitle);
            mBuilder.setContentText(content);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
//            {
//                mBuilder.setColor(getResources().getColor(R.color.tab_default_color));
//            }
//
            mBuilder.setSmallIcon(R.drawable.ic_car);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(notification_id, mBuilder.build());

        }
        catch (Exception e)
        {
            Log.e("NOTIFICATION","generateNoticesNotification : "+e);
            e.printStackTrace();
        }


    }

    public static ArrayList<LatLng> getWayPoints(ArrayList<LatLng> RECEIVED_MARKER)
    {

        ArrayList<LatLng> MARKER = new ArrayList<>();

        try
        {
            int size = RECEIVED_MARKER.size();
            if(size > 23)
            {
                int center_point = (size - 1) / 23;
                int indexValue = center_point;

                for(int i = 0; i < 23; i++)
                {
                    if(i == 0)
                    {
                        MARKER.add(RECEIVED_MARKER.get(0));
                    }
                    if(i == 22)
                    {
                        MARKER.add(RECEIVED_MARKER.get(size - 1));
                    }
                    else
                    {
                        MARKER.add(RECEIVED_MARKER.get(indexValue));
                        indexValue =  indexValue + center_point;
                    }
                }
                MARKER.remove(22);

            }
            else if(size < 23)
            {
                for(int i = 0; i < size; i++)
                {
                    MARKER.add(RECEIVED_MARKER.get(i));
                }

            }
        }
        catch (Exception e)
        {
            M.t("getWayPoints : "+e);
        }

        return MARKER;
    }
    public static Double getDistance(LatLng point1, LatLng point2)
    {
        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    public static String returnVersionName()
    {
        String version = null;
        try
        {
            PackageInfo pInfo = MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), 0);
            version = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
    public static void displayDialog(Context context,String title,String message)
    {
        MaterialDialog md = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();

                    }
                })

                .show();
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "TitilliumWeb-Regular.ttf");
        md.getTitleView().setTypeface(tf);
        md.getContentView().setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setTypeface(tf);
        md.getActionButton(DialogAction.POSITIVE).setAllCaps(false);
        md.getActionButton(DialogAction.NEGATIVE).setTypeface(tf);
        md.getActionButton(DialogAction.NEGATIVE).setAllCaps(false);
    }

    public static String displayYesterdasDate(int noofDays)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, noofDays);

        String datee = dateFormat.format(cal.getTime());

        return datee;


    }
    public static String getDate(int noofDays)
    {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, noofDays);

        String datee = dateFormat.format(cal.getTime());

        return datee;

    }

    public static ArrayList<DeviceInfo> parseJson(String monitorJson)
    {
        String id = Constants.NA;
        String imei = Constants.NA;
        String device_id = Constants.NA;
        String vehicleNumber = Constants.NA;
        String speed = Constants.NA;
        String lastTrackedDateTime = Constants.NA;
        String vehicleType = Constants.NA;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String acc_status = Constants.NA;
        String iconColor = Constants.NA;
        String deviceStatus = Constants.NA;

        String gpsTime = Constants.NA;
        String vehicleModel = Constants.NA;
        String sim = Constants.NA;
        String alarms = Constants.NA;

        ArrayList<DeviceInfo> DEVICE_DETAILS = new ArrayList<>();

        try
        {

            if (monitorJson != null || monitorJson.length() > 0)
            {
                Object json = new JSONTokener(monitorJson).nextValue();
                if (json instanceof JSONObject)
                {

                    JSONObject loginJsonObject = new JSONObject(monitorJson);

                    String success = loginJsonObject.getString("success");
                    String message = loginJsonObject.getString("message");
                    Log.d("login","All----E");
                    if(success.equals("1"))
                    {
                        JSONArray monitorJsonArray = loginJsonObject.getJSONArray("deviceDetails");

                        for(int i = 0; i < monitorJsonArray.length(); i++)
                        {

                            JSONObject mmJsonObject = monitorJsonArray.getJSONObject(i);

                            if(mmJsonObject.has("id") && !mmJsonObject.isNull("id"))
                            {
                                id = mmJsonObject.getString("id");
                            }
                            if(mmJsonObject.has("imei") && !mmJsonObject.isNull("imei"))
                            {
                                imei = mmJsonObject.getString("imei");
                            }
                            if(mmJsonObject.has("device_id") && !mmJsonObject.isNull("device_id"))
                            {
                                device_id = mmJsonObject.getString("device_id");
                            }
                            if(mmJsonObject.has("vehicleNumber") && !mmJsonObject.isNull("vehicleNumber"))
                            {
                                vehicleNumber = mmJsonObject.getString("vehicleNumber");
                            }
                            if(mmJsonObject.has("speed") && !mmJsonObject.isNull("speed"))
                            {
                                speed = mmJsonObject.getString("speed");
                            }
                            if(mmJsonObject.has("lastTrackedDateTime") && !mmJsonObject.isNull("lastTrackedDateTime"))
                            {
                                lastTrackedDateTime = mmJsonObject.getString("lastTrackedDateTime");
                            }
                            if(mmJsonObject.has("vehicleType") && !mmJsonObject.isNull("vehicleType"))
                            {
                                vehicleType = mmJsonObject.getString("vehicleType");
                            }
                            if(mmJsonObject.has("latitude") && !mmJsonObject.isNull("latitude"))
                            {
                                latitude = mmJsonObject.getString("latitude");
                            }
                            if(mmJsonObject.has("longitude") && !mmJsonObject.isNull("longitude"))
                            {
                                longitude = mmJsonObject.getString("longitude");
                            }
                            if(mmJsonObject.has("acc_status") && !mmJsonObject.isNull("acc_status"))
                            {
                                acc_status = mmJsonObject.getString("acc_status");
                            }
                            if(mmJsonObject.has("iconColor") && !mmJsonObject.isNull("iconColor"))
                            {
                                iconColor = mmJsonObject.getString("iconColor");
                            }
                            if(mmJsonObject.has("deviceStatus") && !mmJsonObject.isNull("deviceStatus"))
                            {
                                deviceStatus = mmJsonObject.getString("deviceStatus");
                            }

                            if(mmJsonObject.has("gpsTime") && !mmJsonObject.isNull("gpsTime"))
                            {
                                gpsTime = mmJsonObject.getString("gpsTime");
                            }
                            if(mmJsonObject.has("vehicleModel") && !mmJsonObject.isNull("vehicleModel"))
                            {
                                vehicleModel = mmJsonObject.getString("vehicleModel");
                            }
                            if(mmJsonObject.has("sim") && !mmJsonObject.isNull("sim"))
                            {
                                sim = mmJsonObject.getString("sim");
                            }
                            if(mmJsonObject.has("alarms") && !mmJsonObject.isNull("alarms"))
                            {
                                alarms = mmJsonObject.getString("alarms");
                            }

                            DeviceInfo deviceInfo = new DeviceInfo();

                            deviceInfo.setId(id);
                            deviceInfo.setVehicleIMEI(imei);
                            deviceInfo.setDeviceId(device_id);
                            deviceInfo.setVehicleNumber(vehicleNumber);
                            deviceInfo.setVehicleSpeed(speed);
                            deviceInfo.setLastTrackedTime(lastTrackedDateTime);
                            deviceInfo.setVehicleType(vehicleType);
                            deviceInfo.setCoOrdinate(latitude+" "+longitude);
                            deviceInfo.setAccStatus(acc_status);
                            deviceInfo.setLastLocation(latitude+" "+longitude);
                            deviceInfo.setDeviceStatus(deviceStatus);
                            deviceInfo.setColorName(iconColor);

                            deviceInfo.setGpsTime(gpsTime);
                            deviceInfo.setVehicleModel(vehicleModel);
                            deviceInfo.setSim(sim);
                            deviceInfo.setAlarms(alarms);

                            deviceInfo.setLocationVisibleStatus("yes");

                            DEVICE_DETAILS.add(deviceInfo);



                        }


                    }
                    else if(success.equals("3") || success.equals("0"))
                    {
                        M.t(message);
                    }

                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return DEVICE_DETAILS;
    }

    public static boolean checkConnection()
    {

        ConnectivityManager connectivity = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;

    }
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String getSystemDateTime()
    {

        String systemTime = null;

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            systemTime = df.format(cal.getTime());
        }
        catch (Exception e)
        {


        }
        return systemTime;

    }
    public static String getSystemDate()
    {

        String systemTime = null;

        try
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            systemTime = df.format(cal.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return systemTime;

    }
    public static String getCurDate()
    {

        String systemTime = null;

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            systemTime = df.format(cal.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return systemTime;

    }
    public static String parseDate(String dateInput,String status)
    {

        String parseDate = null;

        try
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = null;
            date = format.parse(dateInput);

            if(status.equals("Year"))
            {
                parseDate = new SimpleDateFormat("d MMM,yyyy",Locale.ENGLISH).format(date);
            }
            else
            {
                parseDate = new SimpleDateFormat("d MMM",Locale.ENGLISH).format(date);
            }


        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }


        return parseDate;

    }
    public static String getAddress(Double lat, Double landi)
    {


        String locationAddress = "NA";
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(MyApplication.context, Locale.ENGLISH);

            addresses = geocoder.getFromLocation(lat, landi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


           if(address.equals(null) || address == null || address.equals(""))
           {
               locationAddress = "NA";
           }
           else
           {
               locationAddress = address +".";
           }
           //locationAddress = address + ", " + city + ", " + state + ", " + country + ","+postalCode+".";
        }
        catch (Exception e)
        {
            locationAddress = "NA";
        }

        return locationAddress;
    }

}
