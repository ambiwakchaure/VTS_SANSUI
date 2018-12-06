package vts.snystems.sns.vts.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.HomeActivity;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.geofence.activity.ActivityGeofenceList;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.sos.activity.ActivityAddContacts;
import vts.snystems.sns.vts.sos.classes.CurrentLatLng;

public class SettingFragment extends Fragment {


    View view;


    @BindView(R.id.selectLngLi)
    LinearLayout selectLngLi;

    @BindView(R.id.languageTxt)
    TextView languageTxt;

    public static HomeActivity activity1;

    @BindView(R.id.alertsLi)
    LinearLayout alertsLi;

    @BindView(R.id.alertsHideLi)
    LinearLayout alertsHideLi;

    @BindView(R.id.lngRadioGrp)
    RadioGroup lngRadioGrp;

    @BindView(R.id.lngHideLi)
    LinearLayout lngHideLi;

    /*@BindView(R.id.allChk)
    CheckBox allChk;*/

    @BindView(R.id.lowBatChk)
    CheckBox lowBatChk;

    @BindView(R.id.overSpeedtChk)
    CheckBox overSpeedtChk;

    @BindView(R.id.sosChk)
    CheckBox sosChk;

    @BindView(R.id.pCutChk)
    CheckBox pCutChk;

    @BindView(R.id.ignChk)
    CheckBox ignChk;

    @BindView(R.id.enRadioButton)
    RadioButton enRadioButton;

    @BindView(R.id.hiRadioButton)
    RadioButton hiRadioButton;

    @BindView(R.id.mrRadioButton)
    RadioButton mrRadioButton;

    @BindView(R.id.dashLi)
    LinearLayout dashLi;

    @BindView(R.id.dashLei)
    LinearLayout dashLei;

    @BindView(R.id.dashRadioGrp)
    RadioGroup dashRadioGrp;

    @BindView(R.id.mapRadioButton)
    RadioButton mapRadioButton;

    @BindView(R.id.listRadioButton)
    RadioButton listRadioButton;

    @BindView(R.id.vStatusRadioButton)
    RadioButton vStatusRadioButton;

    @BindView(R.id.sosSetting)
    LinearLayout sosSetting;

    @BindView(R.id.createGeofence)
    LinearLayout createGeofence;

    @BindView(R.id.sosFloating)
    FloatingActionButton sosFloating;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.location_frgment, container, false);
        view = inflater.inflate(R.layout.setting_layout, container, false);

        ButterKnife.bind(this, view);
        setData();
        setListner();
        return view;


    }


    private void setData() {

        String language = MyApplication.prefs.getString(Constants.APP_LANGUAGE, "0");
        if (language.equals("0")) {
            languageTxt.setText("Language ("+MyApplication.context.getResources().getString(R.string.english)+")");
            enRadioButton.setChecked(true);
        } else {
            if (language.equals("en")) {
                languageTxt.setText("Language ("+MyApplication.context.getResources().getString(R.string.english)+")");
                enRadioButton.setChecked(true);
            } else if (language.equals("hi")) {
                languageTxt.setText("" + MyApplication.context.getResources().getString(R.string.language) + " ("+MyApplication.context.getResources().getString(R.string.hindi)+")");
                hiRadioButton.setChecked(true);
            } else if (language.equals("mr")) {
                languageTxt.setText("" + MyApplication.context.getResources().getString(R.string.language) + " ("+MyApplication.context.getResources().getString(R.string.marathi)+")");
                mrRadioButton.setChecked(true);
            }
        }

        //String all = MyApplication.prefs.getString(Constants.ALL_C,"0");
        String lowBat = MyApplication.prefs.getString(Constants.LOW_BAT_C, "0");
        String oSpeed = MyApplication.prefs.getString(Constants.OVERSPEED_C, "0");
        String sosC = MyApplication.prefs.getString(Constants.SOS_C, "0");
        String pCut = MyApplication.prefs.getString(Constants.POWER_CUT_C, "0");
        String ignC = MyApplication.prefs.getString(Constants.IGNITION_C, "0");

       /* if(all.equals("on"))
        {
            allChk.setChecked(true);
        }*/
        if (lowBat.equals("on")) {
            lowBatChk.setChecked(true);
        }
        if (oSpeed.equals("on")) {
            overSpeedtChk.setChecked(true);
        }
        if (sosC.equals("on")) {
            sosChk.setChecked(true);
        }
        if (pCut.equals("on")) {
            pCutChk.setChecked(true);
        }
        if (ignC.equals("on")) {
            ignChk.setChecked(true);
        }

        String dashPref = MyApplication.prefs.getString(Constants.DASH_PREF, "0");
        if(dashPref.equals("map"))
        {
            mapRadioButton.setChecked(true);
        }
        else if(dashPref.equals("list"))
        {
            listRadioButton.setChecked(true);
        }
        else if(dashPref.equals("vStatus"))
        {
            vStatusRadioButton.setChecked(true);
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
        createGeofence.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), ActivityGeofenceList.class));
            }
        });
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
                    F.addSoScontacts(getActivity());
                }
                else
                {
                    //get curr lat long and send sms
                    //checkSmsPermission();
                    if(!F.hasPermissions(getActivity(), PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                    }
                    else
                    {
                        new CurrentLatLng().getCurrentLatLng(getActivity());
                    }

                }

            }
        });

        sosSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                startActivity(new Intent(getActivity(), ActivityAddContacts.class));


            }
        });

        dashLi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dashPref = MyApplication.prefs.getString(Constants.DASH_PREF, "0");
                if(dashPref.equals("map"))
                {
                    mapRadioButton.setChecked(true);
                }
                else if(dashPref.equals("list"))
                {
                    listRadioButton.setChecked(true);
                }

                lngHideLi.setVisibility(View.GONE);
                alertsHideLi.setVisibility(View.GONE);
                dashLei.setVisibility(View.VISIBLE);


            }
        });


        lowBatChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (b) {
                    lowBatChk.setChecked(true);
                    MyApplication.editor.putString(Constants.LOW_BAT_C, "on").commit();
                    MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
                } else {
                    lowBatChk.setChecked(false);
                    MyApplication.editor.putString(Constants.LOW_BAT_C, "off").commit();
                    MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                }
            }
        });
        overSpeedtChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//                if(allChk.isChecked())
//                {
//                    allChk.setChecked(false);
//                    MyApplication.editor.putString(Constants.ALL_C,"off").commit();
//                }
                if (b) {
                    overSpeedtChk.setChecked(true);
                    MyApplication.editor.putString(Constants.OVERSPEED_DEFLT, "0").commit();
                    MyApplication.editor.putString(Constants.OVERSPEED_C, "on").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
                } else {
                    overSpeedtChk.setChecked(false);
                    MyApplication.editor.putString(Constants.OVERSPEED_DEFLT, "1").commit();
                    MyApplication.editor.putString(Constants.OVERSPEED_C, "off").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                }
            }
        });
        sosChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//                if(allChk.isChecked())
//                {
//                    allChk.setChecked(false);
//                    MyApplication.editor.putString(Constants.ALL_C,"off").commit();
//                }
                if (b) {
                    sosChk.setChecked(true);
                    MyApplication.editor.putString(Constants.SOS_C, "on").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
                } else {
                    sosChk.setChecked(false);
                    MyApplication.editor.putString(Constants.SOS_C, "off").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                }
            }
        });
        pCutChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//                if(allChk.isChecked())
//                {
//                    allChk.setChecked(false);
//                    MyApplication.editor.putString(Constants.ALL_C,"off").commit();
//                }
                if (b) {
                    pCutChk.setChecked(true);
                    MyApplication.editor.putString(Constants.POWER_CUT_C, "on").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();
                } else {
                    pCutChk.setChecked(false);
                    MyApplication.editor.putString(Constants.POWER_CUT_C, "off").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                }

            }
        });
        ignChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (b)
                {
                    ignChk.setChecked(true);
                    MyApplication.editor.putString(Constants.IGNITION_DEFLT, "0").commit();
                    MyApplication.editor.putString(Constants.IGNITION_C, "on").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "on").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, false).commit();

                }
                else
                {
                    ignChk.setChecked(false);
                    MyApplication.editor.putString(Constants.IGNITION_DEFLT, "1").commit();
                    MyApplication.editor.putString(Constants.IGNITION_C, "off").commit();

                    MyApplication.editor.putString(Constants.NOTI_ALERT, "off").commit();
                    MyApplication.editor.putBoolean(Constants.SERVICE_FLAG, true).commit();
                }
            }
        });


        alertsLi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                alertsHideLi.setVisibility(View.VISIBLE);
                lngHideLi.setVisibility(View.GONE);
                dashLei.setVisibility(View.GONE);
            }
        });

        lngRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected

                if (checkedId == R.id.enRadioButton) {

                    MyApplication.editor.putString(Constants.APP_LANGUAGE, "en").commit();
                    F.setLanguage("en");
                    activity1.refreshActivity();
                } else if (checkedId == R.id.hiRadioButton) {
                    MyApplication.editor.putString(Constants.APP_LANGUAGE, "hi").commit();
                    F.setLanguage("hi");
                    activity1.refreshActivity();
                } else if (checkedId == R.id.mrRadioButton) {
                    MyApplication.editor.putString(Constants.APP_LANGUAGE, "mr").commit();
                    F.setLanguage("mr");
                    activity1.refreshActivity();
                }


            }


        });
        dashRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // find which radio button is selected
                if (checkedId == R.id.mapRadioButton)
                {
                    MyApplication.editor.putString(Constants.DASH_PREF, "map").commit();
                    //activity1.refreshActivity();
                }
                else if (checkedId == R.id.listRadioButton)
                {
                    MyApplication.editor.putString(Constants.DASH_PREF, "list").commit();
                    //activity1.refreshActivity();
                }
                else if (checkedId == R.id.vStatusRadioButton)
                {
                    MyApplication.editor.putString(Constants.DASH_PREF, "vStatus").commit();
                    //activity1.refreshActivity();
                }

            }
        });
    }
    public static void newInstance(HomeActivity activity)
    {
        activity1 = activity;
    }

    @OnClick(R.id.selectLngLi)
    public void onViewClicked()
    {

        lngHideLi.setVisibility(View.VISIBLE);
        alertsHideLi.setVisibility(View.GONE);
        dashLei.setVisibility(View.GONE);
       /* new MaterialDialog.Builder(getActivity())
                .title("Choose Language")
                .items(R.array.lang)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals("English"))
                        {
                            MyApplication.editor.putString(Constants.APP_LANGUAGE, "en").commit();
                            F.setLanguage("en");
                            activity1.refreshActivity();


                        } else if (text.toString().equals("Hindi")) {
                            MyApplication.editor.putString(Constants.APP_LANGUAGE, "hi").commit();
                            F.setLanguage("hi");
                            activity1.refreshActivity();
                        } else if (text.toString().equals("Marathi")) {
                            MyApplication.editor.putString(Constants.APP_LANGUAGE, "mr").commit();
                            F.setLanguage("mr");
                            activity1.refreshActivity();
                        }
                    }
                })
                .show();*/


    }



}
