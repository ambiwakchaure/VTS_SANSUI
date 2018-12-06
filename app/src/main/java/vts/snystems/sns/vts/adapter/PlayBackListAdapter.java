package vts.snystems.sns.vts.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import vts.snystems.sns.vts.R;
import vts.snystems.sns.vts.activity.ActivityPlayBack;
import vts.snystems.sns.vts.activity.ActivityPlaybackTrackInfo;
import vts.snystems.sns.vts.classes.F;
import vts.snystems.sns.vts.classes.M;
import vts.snystems.sns.vts.interfaces.Constants;
import vts.snystems.sns.vts.pojo.PlaybackList;


/**
 * Created by Tej on 20/07/2018.
 */

public class PlayBackListAdapter extends RecyclerView.Adapter<PlayBackListAdapter.ViewHolderCarLog>
{

    private ArrayList<PlaybackList> playbackLists = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public PlayBackListAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setAllDeviceInfo(ArrayList<PlaybackList> playbackListsInformations)
    {
        this.playbackLists = playbackListsInformations;
        notifyItemRangeChanged(0, playbackListsInformations.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType)
    {

        view = layoutInflater.inflate(R.layout.row_playback_list, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }


    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position)
    {
        try
        {

            final PlaybackList playbackList = playbackLists.get(position);



            String [] startDate =  playbackList.getStartDate().split(" ");
            String [] endDate =  playbackList.getEndDate().split(" ");

            if(startDate.length > 0)
            {
                holder.txt_start_date.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.time_duration)+" : </b>"+F.parseDate(startDate[0],"Year")+" "+startDate[1]));
            }
            else
            {
                holder.txt_start_date.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.time_duration)+" : </b> 0000-00-00 00:00:00"));
            }

            if(endDate.length > 0)
            {
                holder.txt_end_date.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.end_duration)+" : </b>"+F.parseDate(endDate[0],"Year")+" "+endDate[1]));
            }
            else
            {
                holder.txt_end_date.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.end_duration)+" : </b> 0000-00-00 00:00:00"));
            }

            holder.txt_dist_travel.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.distance)+" : </b>"+playbackList.getDist()+" Km"));

            String startAddress = F.getAddress(Double.parseDouble(playbackList.getLat1()),Double.parseDouble(playbackList.getLong1()));

            if(startAddress.equals("NA"))
            {
                holder.txt_start_address.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.start_location)+" : </b> "+Constants.uLocation));
            }
            else
            {
                holder.txt_start_address.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.start_location)+" : </b>"+startAddress));
            }

            String endAddress = F.getAddress(Double.parseDouble(playbackList.getLat2()),Double.parseDouble(playbackList.getLong2()));
            if(endAddress.equals("NA"))
            {
                holder.txt_end_address.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.end_loaction)+" : </b>"+Constants.uLocation));
            }
            else
            {
                holder.txt_end_address.setText(Html.fromHtml("<b>"+view.getContext().getResources().getString(R.string.end_loaction)+" : </b>"+endAddress));
            }


            holder.btn_playback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {


                       Intent i = new Intent(view.getContext(), ActivityPlayBack.class);

                            i.putExtra(Constants.FROM_DATE,playbackList.getStartDate());
                            i.putExtra(Constants.TO_DATE,playbackList.getEndDate());
                            i.putExtra(Constants.PLAY_DIST,playbackList.getDist());

                        view.getContext().startActivity(i);

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return playbackLists.size();
    }

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{



        @BindView(R.id.txt_start_date)
        TextView txt_start_date;

        @BindView(R.id.txt_end_date)
        TextView txt_end_date;

        @BindView(R.id.btn_playback)
        ImageView btn_playback;

        @BindView(R.id.txt_dist_travel)
        TextView txt_dist_travel;

        @BindView(R.id.txt_start_address)
        TextView txt_start_address;

        @BindView(R.id.txt_end_address)
        TextView txt_end_address;

        @BindView(R.id.txt_tot_stop)
        TextView txt_tot_stop;







        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);
            try
            {

                ButterKnife.bind(this,itemView);
                //btn_playback.setOnClickListener(this);


                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        M.t("hello");
                        //v.getContext().startActivity(new Intent(v.getContext(), ActivityPlaybackTrackInfo.class));
                        PlaybackList vehicleInfo = playbackLists.get(Integer.valueOf(getAdapterPosition()));

                        Intent i = new Intent(itemView.getContext(), ActivityPlayBack.class);

                        i.putExtra(Constants.FROM_DATE,vehicleInfo.getStartDate());
                        i.putExtra(Constants.TO_DATE,vehicleInfo.getEndDate());

                        itemView.getContext().startActivity(i);

                    }
                });
*/
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(final View v)
        {
            try
            {

                /*if (v.getId() == itemView.getId())
                {

                }
*/

            }
            catch (Exception e)
            {
                M.t("hello"+e);
                e.printStackTrace();
            }
        }
    }


}
