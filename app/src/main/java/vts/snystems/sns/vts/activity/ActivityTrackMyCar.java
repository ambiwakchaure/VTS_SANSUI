package vts.snystems.sns.vts.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;

public class ActivityTrackMyCar extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap mMap;

    SupportMapFragment mapFragment;

    @BindView(R.id.buttonMapType)
    Button buttonMapType;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_my_car);

        ButterKnife.bind(this);




        buttonMapType.setText("Normal");
        buttonMapType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                PopupMenu popup = new PopupMenu(ActivityTrackMyCar.this, buttonMapType);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {

                        if(item.getTitle().equals("Restaurants"))
                        {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            buttonMapType.setText("Normal");
                        }
                        else if(item.getTitle().equals("Hospitals"))
                        {

                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            buttonMapType.setText("Satellite");
                        }
                        else if(item.getTitle().equals("Schools"))
                        {

                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            buttonMapType.setText("Hybrid");


                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;


    }



    public void goBack(View view) {

        finish();
    }


}
