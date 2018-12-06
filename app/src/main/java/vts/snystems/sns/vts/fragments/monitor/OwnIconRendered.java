package vts.snystems.sns.vts.fragments.monitor;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OwnIconRendered extends DefaultClusterRenderer<MarkerDialogInfo> {

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MarkerDialogInfo> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerDialogInfo item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
        //markerOptions.snippet(item.getSnippet());
       // markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

   /* @Override
    public void onClustersChanged(Set<? extends Cluster<MarkerDialogInfo>> clusters) {
        if(clusters.size()>10){
            List<? extends Cluster<MarkerDialogInfo>> list = new ArrayList<>(clusters);

            Set<? extends Cluster<MarkerDialogInfo>> subSet = new LinkedHashSet<>(list.subList(0, 10));

            super.onClustersChanged(subSet);
        } else{
            super.onClustersChanged(clusters);
        }
    }*/
}
