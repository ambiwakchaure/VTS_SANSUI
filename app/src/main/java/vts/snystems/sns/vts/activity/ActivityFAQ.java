package vts.snystems.sns.vts.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.adapter.FaqExpandableAdapter;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.errorHandler.ExceptionHandler;
import vts.snystems.sns.vts.interfaces.Constants;

public class ActivityFAQ extends AppCompatActivity {

    FaqExpandableAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
   // TextView txt_vehicles_no, txt_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_faq);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
//        txt_vehicles_no = (TextView) findViewById(R.id.txt_vehicles_no);
//        txt_date = (TextView) findViewById(R.id.txt_date);
//        txt_vehicles_no.setText("FAQ");
//        txt_date.setVisibility(View.GONE);


        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            // TODO Colapse Here Using this... in android
            int previousGroup = -1;
            boolean flag = false;

            @Override
            public void onGroupExpand(int groupPosition) {



                if (groupPosition != previousGroup && flag) {
                    expListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;

                flag = true;

            }
        });

        // preparing list data
        prepareListData();

        listAdapter = new FaqExpandableAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


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

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(" 1.The map shows my vehicle to be stopped, but the vehicle is moving.");
        listDataHeader.add(" 2.The system shows my vehicle to be hundred meters from its actual location.");
        listDataHeader.add(" 3.I logged in, and I get is map of India. My vehicle doesn’t show.");


        // Adding child data
        List<String> list1 = new ArrayList<String>();
        list1.add(getResources().getString(R.string.faq1));

        List<String> list2 = new ArrayList<String>();
        list2.add(getResources().getString(R.string.faq2));

        List<String> list3 = new ArrayList<String>();
        list3.add(getResources().getString(R.string.faq3));



        listDataChild.put(listDataHeader.get(0), list1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), list2);
        listDataChild.put(listDataHeader.get(2), list3);

    }

    public void goBack(View view) {

        finish();
    }
}