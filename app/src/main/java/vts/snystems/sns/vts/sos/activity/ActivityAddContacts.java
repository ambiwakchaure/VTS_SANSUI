package vts.snystems.sns.vts.sos.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.Validate;
import vts.snystems.sns.vts.interfaces.Constants;

public class ActivityAddContacts extends AppCompatActivity {

    @BindView(R.id.first_name_EditText)
    EditText firstNameEditText;

    @BindView(R.id.first_number_EditText)
    EditText firstNumberEditText;

    @BindView(R.id.second_name_EditText)
    EditText secondNameEditText;

    @BindView(R.id.second_number_EditText)
    EditText secondNumberEditText;

    @BindView(R.id.third_name_EditText)
    EditText thirdNameEditText;

    @BindView(R.id.third_number_EditText)
    EditText thirdNumberEditText;



    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        //set sos contact
        //MyApplication.editor.putString(Constants.SOS_SC,name+"#"+number).commit();
        setContacts();
    }

    private void setContacts()
    {

        String SOS_FC = MyApplication.prefs.getString(Constants.SOS_FC,"0");
        String SOS_SC = MyApplication.prefs.getString(Constants.SOS_SC,"0");
        String SOS_TC = MyApplication.prefs.getString(Constants.SOS_TC,"0");

        if(SOS_FC != "0")
        {

            String [] data = SOS_FC.split("#");

            firstNameEditText.setText(data[0]);
            firstNumberEditText.setText(data[1]);
        }

        if(SOS_SC != "0")
        {

            String [] data = SOS_SC.split("#");

            secondNameEditText.setText(data[0]);
            secondNumberEditText.setText(data[1]);
        }

        if(SOS_TC != "0")
        {

            String [] data = SOS_TC.split("#");

            thirdNameEditText.setText(data[0]);
            thirdNumberEditText.setText(data[1]);
        }

    }


    public void addThirdContactPreference(View view)
    {

        if ( F.checkPermission(ActivityAddContacts.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "third";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }
        else F.askPermission(ActivityAddContacts.this,Manifest.permission.READ_CONTACTS);
    }

    public void addSecondContactPreference(View view)
    {

        if ( F.checkPermission(ActivityAddContacts.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "second";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }
        else F.askPermission(ActivityAddContacts.this,Manifest.permission.READ_CONTACTS);
    }

    public void addFirstContactPreference(View view)
    {
        if ( F.checkPermission(ActivityAddContacts.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "first";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }
        else F.askPermission(ActivityAddContacts.this,Manifest.permission.READ_CONTACTS);
    }

    public void goBack(View view)
    {
        finish();
    }
    private Uri uriContact;
    String status;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {

            uriContact = data.getData();

            try
            {
//                String name = F.retrieveContactName(ActivityAddContacts.this,uriContact);
//                String number = F.retrieveContactNumber(ActivityAddContacts.this,uriContact);

                String name = retrieveContactName();
                String number = retrieveContactNumber().replaceAll("[^a-zA-Z0-9_-]","").replace("-","");

                if(number.length() == 12)
                {
                    number = "+"+number;
                }
                if(!Validate.validateMobileNumber(number,"Warning ! invalid mobile number found"))
                {
                    return;
                }
                if(!Validate.checkNumberExist(number,"Warning ! same number exists"))
                {
                    return;
                }
                if(status.equals("first"))
                {

                    firstNameEditText.setText(name);
                    firstNumberEditText.setText(number);

                    //add sos first number in storage
                    MyApplication.editor.putString(Constants.SOS_FC,name+"#"+number).commit();
                    M.t("First contact successfully added.");
                }
                else if(status.equals("second"))
                {
                    secondNameEditText.setText(name);
                    secondNumberEditText.setText(number);

                    //add sos second number in storage
                    MyApplication.editor.putString(Constants.SOS_SC,name+"#"+number).commit();
                    M.t("Second contact successfully added.");
                }
                else if(status.equals("third"))
                {
                    thirdNameEditText.setText(name);
                    thirdNumberEditText.setText(number);

                    //add sos third number in storage
                    MyApplication.editor.putString(Constants.SOS_TC,name+"#"+number).commit();
                    M.t("Third contact successfully added.");
                }

            }
            catch (Exception e)
            {
                Log.e("Exception",">>"+e);
            }



        }
    }
    String contactID;
    private String  retrieveContactNumber() {

        String contactNumber = null;
        try
        {

            // getting contacts ID
            Cursor cursorID = getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);

            if (cursorID.moveToFirst()) {

                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }

            cursorID.close();



            // Using the contact ID now we will get contact phone number
            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
            M.t("Contact Number Not Found");
        }

        return contactNumber.replace(" ","");
    }
    private String retrieveContactName() {

        String contactName = null;
        try
        {


            // querying contact data store
            Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

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
            M.t("Name not found");
        }

        return contactName;

    }

    public void saveFirstContact(View view)
    {
        if(!Validate.validateEmptyField(firstNameEditText,"Warning ! enter first contact name"))
        {
            return;
        }
        if(!Validate.validateEmptyField(firstNumberEditText,"Warning ! enter first contact number"))
        {
            return;
        }
        if(!Validate.validateMobileNumber(firstNumberEditText,"Warning ! invalid first contact number"))
        {
            return;
        }
        if(!Validate.checkNumberExist(firstNumberEditText.getText().toString().trim(),"Warning ! same number exists"))
        {
            return;
        }

        String name = firstNameEditText.getText().toString();
        String number = firstNumberEditText.getText().toString();
        //add sos first number in storage
        MyApplication.editor.putString(Constants.SOS_FC,name+"#"+number).commit();
        M.t("First contact successfully added.");

    }
    public void saveSecondContact(View view) {

        if(!Validate.validateEmptyField(secondNameEditText,"Warning ! enter second contact name"))
        {
            return;
        }
        if(!Validate.validateEmptyField(secondNumberEditText,"Warning ! enter second contact number"))
        {
            return;
        }
        if(!Validate.validateMobileNumber(secondNumberEditText,"Warning ! invalid second contact number"))
        {
            return;
        }
        if(!Validate.checkNumberExist(secondNumberEditText.getText().toString().trim(),"Warning ! same number exists"))
        {
            return;
        }

        String name = secondNameEditText.getText().toString();
        String number = secondNumberEditText.getText().toString();
        //add sos first number in storage
        MyApplication.editor.putString(Constants.SOS_SC,name+"#"+number).commit();
        M.t("Second contact successfully added.");
    }
    public void saveThirdContact(View view) {

        if(!Validate.validateEmptyField(thirdNameEditText,"Warning ! enter third contact name"))
        {
            return;
        }
        if(!Validate.validateEmptyField(thirdNumberEditText,"Warning ! enter third contact number"))
        {
            return;
        }
        if(!Validate.validateMobileNumber(thirdNumberEditText,"Warning ! invalid third contact number"))
        {
            return;
        }
        if(!Validate.checkNumberExist(thirdNumberEditText.getText().toString().trim(),"Warning ! same number exists"))
        {
            return;
        }


        String name = thirdNameEditText.getText().toString();
        String number = thirdNumberEditText.getText().toString();
        //add sos first number in storage
        MyApplication.editor.putString(Constants.SOS_TC,name+"#"+number).commit();
        M.t("Third contact successfully added.");
    }


    public void deleteFirstContact(View view)
    {

        MyApplication.editor.remove(Constants.SOS_FC).commit();
        firstNameEditText.setText("");
        firstNumberEditText.setText("");
        M.t("Success ! First contact deleted.");

    }

    public void deleteSecondContact(View view)
    {
        MyApplication.editor.remove(Constants.SOS_SC).commit();
        secondNameEditText.setText("");
        secondNumberEditText.setText("");
        M.t("Success ! Second contact deleted.");

    }

    public void deleteThirdContact(View view)
    {
        MyApplication.editor.remove(Constants.SOS_TC).commit();
        thirdNameEditText.setText("");
        thirdNumberEditText.setText("");
        M.t("Success ! Third contact deleted.");
    }


}
