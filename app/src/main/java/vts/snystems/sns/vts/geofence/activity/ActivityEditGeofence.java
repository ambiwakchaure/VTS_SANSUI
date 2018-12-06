package vts.snystems.sns.vts.geofence.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.MyApplication;
import vts.snystems.sns.vts.classes.V;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.interfaces.Constants;


public class ActivityEditGeofence extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap map;

    DiscreteSeekBar seekBar;
    TextView txt_radius;

    @BindView(R.id.geofenceAddressTextView)
    TextView geofenceAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_geofence);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_geo_fence_edit);

        mapFragment.getMapAsync(this);

        seekBar = (DiscreteSeekBar) findViewById(R.id.radiusSeekbar_edit);
        txt_radius = (TextView) findViewById(R.id.edit_geo_radius);

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {

                if(map != null)
                {

                    String lat_long = getIntent().getExtras().getString("edit_lat_long");
                    String id = getIntent().getExtras().getString("edit_id");
                    txt_radius.setText("Radius : "+progress + " km");
                    String[] a = lat_long.toString().split(",");
                    String lat_value = a[0];
                    String long_value = a[1];

                    TABLE_STORE_GEOFENCE.updateGeofence(id, progress);
                    Circle circle = null;
                    if(circle != null)
                    {
                        circle.setRadius(0);
                    }
                    F.createGeofence(map,Double.valueOf(lat_value),Double.valueOf(long_value),progress * 1000);
                   // setVehicleMarker();
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String radius = getIntent().getExtras().getString("edit_radius");
        txt_radius.setText("Radius : "+radius + " km");
        seekBar.setProgress(Integer.valueOf(radius));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;


        String lat_long = getIntent().getExtras().getString("edit_lat_long");
        //String lat_long = "46.163670,-86.521150";
        String radius = getIntent().getExtras().getString("edit_radius");
        String geofence_address = getIntent().getExtras().getString("geofence_address");
        String vLatLong = getIntent().getExtras().getString(Constants.LAT_LONG);
        String vType = getIntent().getExtras().getString(Constants.VTYPE);

        geofenceAddressTextView.setVisibility(View.VISIBLE);
        geofenceAddressTextView.setText(geofence_address);

        String[] a = lat_long.toString().split(",");
        String lat_value = a[0];
        String long_value = a[1];

        LatLng india = new LatLng(Double.valueOf(lat_value), Double.valueOf(long_value));
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(india,13f));

        map.addMarker(new MarkerOptions().position(india));

        F.createGeofence(map,Double.valueOf(lat_value), Double.valueOf(long_value), Integer.valueOf(radius) * 1000);


        if(vLatLong.contains(","))
        {
            String[] dataltlng = vLatLong.split(",");

            if (dataltlng.length == 2)
            {
                String lat = dataltlng[0];
                String lng = dataltlng[1];

                if(!V.checkNull(lat))
                {
                    return;
                }
                if(!V.checkNull(lng))
                {
                    return;
                }
                try
                {
                    LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    F.setMarkerVehicleIconType(vType,map,latlng,Float.valueOf(MyApplication.prefs.getString(Constants.COURSE,"0")));




                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(india);
                    builder.include(latlng);

                    LatLngBounds bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (height * 0.25); // offset from edges of the map 12% of screen


                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    map.moveCamera(cu);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }

    }

    public void goBack(View view) {

        finish();
    }
}
