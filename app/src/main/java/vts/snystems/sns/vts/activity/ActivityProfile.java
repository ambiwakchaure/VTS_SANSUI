package vts.snystems.sns.vts.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.Validate;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.interfaces.VMsg;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;
import vts.snystems.sns.vts.volley.Rc;
import vts.snystems.sns.vts.volley.VolleyCallback;
import vts.snystems.sns.vts.volley.VolleyErrorCallback;


public class ActivityProfile extends AppCompatActivity {

    @BindView(R.id.txt_first_name)
    EditText txtFirstName;

    @BindView(R.id.txt_last_name)
    EditText txtLastName;

    @BindView(R.id.txt_email)
    EditText txtEmail;

    @BindView(R.id.txt_mobile)
    EditText txtMobile;

    @BindView(R.id.txt_address)
    EditText txtAddress;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.imge_profile)
    CircleImageView imge_profile;


    private static int
            RESULT_LOAD_IMAGE = 1;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setListner();
        //set profile image if exists
        F.setImage(imge_profile);

        if (F.checkConnection())
        {
            //default three days data current date and previoust two days date -2
            getProfile("first");

        }
        else {
            M.t(VMsg.connection);
        }


         
    }
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS =
            {
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            };

    private void setListner()
    {

        sosFloating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String SOS_FC = MyApplication.prefs.getString(Constants.SOS_FC,"0");
                String SOS_SC = MyApplication.prefs.getString(Constants.SOS_SC,"0");
                String SOS_TC = MyApplication.prefs.getString(Constants.SOS_TC,"0");

                if(SOS_FC.equals("0") && SOS_SC.equals("0") && SOS_TC.equals("0"))
                {
                    F.addSoScontacts(ActivityProfile.this);
                }
                else
                {
                    //get curr lat long and send sms
                    //checkSmsPermission();
                    if(!F.hasPermissions(ActivityProfile.this, PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions(ActivityProfile.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    else
                    {
                        new CurrentLatLng().getCurrentLatLng(ActivityProfile.this);
                    }

                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        String language = MyApplication.prefs.getString(Constants.APP_LANGUAGE,"en");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Configuration config = newBase.getResources().getConfiguration();
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            config.setLocale(locale);
            newBase = newBase.createConfigurationContext(config);
        }
        super.attachBaseContext(newBase);
    }


    public void getProfile(final String status)
    {

        try
        {

            MyApplication.editor.commit();
            String userName = MyApplication.prefs.getString(Constants.USER_NAME, "0");
            MyApplication.editor.commit();

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {

                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.", "Timeout Error",status);

                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityProfile.this,"Webservice : Constants.selectProfile,Function : getProfile(final String status)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.getProfile,
                    parameters,
                    ActivityProfile.this, "first");

        } catch (Exception e) {

        }

    }

    private void parseResponse(String loginJson) {

        String name = Constants.NA;
        String email_id = Constants.NA;
        String contact_number = Constants.NA;
        String address1 = Constants.NA;

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
                        JSONObject jsonObject = loginJsonObject1.getJSONObject("profile");


                        if(jsonObject.has("name") && !jsonObject.isNull("name"))
                        {
                            name = jsonObject.getString("name");
                        }
                        if(jsonObject.has("email_id") && !jsonObject.isNull("email_id"))
                        {
                            email_id = jsonObject.getString("email_id");
                        }
                        if(jsonObject.has("contact_number") && !jsonObject.isNull("contact_number"))
                        {
                            contact_number = jsonObject.getString("contact_number");
                        }
                        if(jsonObject.has("address1") && !jsonObject.isNull("address1"))
                        {
                            address1 = jsonObject.getString("address1");
                        }
                        if(name.contains(" "))
                        {
                            String [] data = name.split(" ");

                            txtFirstName.setText(data[0]);
                            txtLastName.setText(data[1]);
                        }
                        else
                        {
                            txtFirstName.setText(name);
                            txtLastName.setText("");
                        }
                        txtEmail.setText(email_id);
                        txtMobile.setText(contact_number);
                        txtAddress.setText(address1);



                    }
                    else if (success.equals("2"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("3"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("0"))
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
    }


    public void sR(String message, String error, final String status)
    {
        new MaterialDialog.Builder(ActivityProfile.this)
                .title(error)
                .content(message)
                .positiveText("Try again")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                        getProfile(status);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        dialog.dismiss();
                    }
                })
                .show();


    }

    public void goBack(View view) {

        finish();
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked()
    {

        if (!Validate.validateEmptyField(txtFirstName, "Warning ! enter first name"))
        {
            return;
        }
        if (!Validate.validateEmptyField(txtLastName, "Warning ! enter last name"))
        {
            return;
        }
        if (!Validate.validateEmptyField(txtAddress, "Warning ! enter address"))
        {
            return;
        }

        if (F.checkConnection())
        {
            //default three days data current date and previoust two days date -2
            updateProfile("second");

        }
        else {
            M.t(VMsg.connection);
        }
    }
    public void updateProfile(final String status)
    {

        try
        {

            MyApplication.editor.commit();
            String userName = MyApplication.prefs.getString(Constants.USER_NAME, "0");
            MyApplication.editor.commit();

            String[] parameters =
                    {
                            Constants.USER_NAME + "#" + userName,
                            Constants.LAST_NAME + "#" + txtFirstName.getText().toString()+" "+txtLastName.getText().toString(),
                            Constants.ADDRESS + "#" + txtAddress.getText().toString()

                    };
            Rc.withParamsProgress(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            parseUpdateResponse(result);
                        }
                    },
                    new VolleyErrorCallback() {

                        @Override
                        public void onError(VolleyError volleyErrr) {


                            if (volleyErrr instanceof TimeoutError)
                            {
                                sR("Oops ! request timed out.", "Timeout Error",status);

                            }
                            else
                            {
                                F.handleError(volleyErrr,ActivityProfile.this,"Webservice : Constants.updateProfile,Function : updateProfile(final String status)");
                            }

                        }
                    },

                    Constants.webUrl + "" + Constants.updateProfile,
                    parameters,
                    ActivityProfile.this, "first");

        } catch (Exception e) {

        }

    }

    private void parseUpdateResponse(String loginJson) {




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
                        M.t("Success ! changes save sucessfully");


                    }
                    else if (success.equals("2"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("3"))
                    {
                        M.t(message);
                    }
                    else if (success.equals("0"))
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

    }

    public void captureImage(View view)
    {



        new MaterialDialog.Builder(this)
                .title("Choose Image")
                .items(R.array.imageChoose)
                .itemsCallback(new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        if(text.toString().equals("Camera"))
                        {
                            if (ActivityCompat.checkSelfPermission(ActivityProfile.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED)
                            {
                                // CAMERA permission has not been granted.
                                dialog.dismiss();
                                requestReadPhoneStatePermission();
                            }
                            else {
                                // CAMERA permission is already been granted.
                                doPermissionGrantedStuffs();
                            }



                        }
                        else if(text.toString().equals("Gallery"))
                        {

                            dialog.dismiss();
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                        }
                    }
                })
                .show();
    }
    public void doPermissionGrantedStuffs() {


        //Get IMEI Number of Phone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


    }
    final private static int PERMISSIONS_REQUEST_CAMERA = 0;
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityProfile.this,
                Manifest.permission.CAMERA))
        {



                    ActivityCompat.requestPermissions(ActivityProfile.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                    doPermissionGrantedStuffs();


        } else {
            // CAMERA permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(ActivityProfile.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
    }
    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActivityProfile.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }
    private static final int CAMERA_REQUEST = 1888;

    private Bitmap
            bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {

            try
            {
                bitmap = (Bitmap) data.getExtras().get("data");
                imge_profile.setImageBitmap(bitmap);
                F.storeImage(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
        {
            Uri filePath = data.getData();
            String imageType = GetMimeType(ActivityProfile.this, filePath);

            if(imageType.contains("png") || imageType.contains("jpg") || imageType.contains("jpeg") || imageType.contains("jpe") || imageType.contains("jfif"))
            {
                try
                {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imge_profile.setImageBitmap(bitmap);
                    F.storeImage(bitmap);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                M.t(getString(R.string.in_image));
            }
        }
    }
    public static String GetMimeType(Context context, Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[] { MediaStore.MediaColumns.MIME_TYPE },
                null, null, null);

        if (cursor != null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }
}
