package vts.snystems.sns.vts.geofence.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.db.TABLE_STORE_GEOFENCE;
import vts.snystems.sns.vts.geofence.pojo.GeoFenceObjectClass;
import vts.snystems.sns.vts.geofence.adapter.ViewGeofenceAdapter;

public class ActivityViewGeofence extends AppCompatActivity
{

    @BindView(R.id.recyclerview_geofence)
    RecyclerView recyclerviewGeofence;

    private ViewGeofenceAdapter viewGeofenceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geofence_new);
        ButterKnife.bind(this);
        initialise();
    }
    public void goBack(View view)
    {
        finish();
    }
    public void add_places(View view)
    {

        if (!permissionsGranted())
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        else
        {
            ArrayList<GeoFenceObjectClass> GEOFENCE_DATA = TABLE_STORE_GEOFENCE.selectGeofence("");
            if (GEOFENCE_DATA.size() < 3)
            {
                Intent intent_add_places = new Intent(ActivityViewGeofence.this, ActivityCreateGeofence.class);
                intent_add_places.putExtra("edit_radius", "5");
                startActivity(intent_add_places);
            }
            else
            {
                new MaterialDialog.Builder(ActivityViewGeofence.this)
                        .title("Oops !")
                        .content(R.string.s_go)
                        .positiveText(R.string.delete)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }


    }

    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        getGeoData();

    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //doLocationAccessRelatedJob();
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
                //startInstalledAppDetailsActivity();
            }
        }
    }

    private void getGeoData()
    {

        ArrayList<GeoFenceObjectClass> GEOFENCE_DATA = TABLE_STORE_GEOFENCE.selectGeofence("");

        if(GEOFENCE_DATA.isEmpty())
        {
            new MaterialDialog.Builder(ActivityViewGeofence.this)
                    .title(R.string.geofence)
                    .content(R.string.d_geofence)
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback()
                    {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                        {
                            dialog.dismiss();
                        }
                    }).show();
            viewGeofenceAdapter.setGeofenceData(GEOFENCE_DATA,ActivityViewGeofence.this);
            viewGeofenceAdapter.notifyDataSetChanged();
        }
        else
        {
            viewGeofenceAdapter.setGeofenceData(GEOFENCE_DATA,ActivityViewGeofence.this);
            viewGeofenceAdapter.notifyDataSetChanged();
        }
    }

    private void initialise()
    {

        recyclerviewGeofence.setLayoutManager(new LinearLayoutManager(ActivityViewGeofence.this));
        viewGeofenceAdapter = new ViewGeofenceAdapter(ActivityViewGeofence.this);
        recyclerviewGeofence.setAdapter(viewGeofenceAdapter);

    }

    public void deleteGeofence(String geo_fence_id)
    {
        TABLE_STORE_GEOFENCE.deleteGeofence(geo_fence_id);
        getGeoData();
    }

}
