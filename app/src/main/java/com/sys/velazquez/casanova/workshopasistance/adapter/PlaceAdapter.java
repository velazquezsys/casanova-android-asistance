package com.sys.velazquez.casanova.workshopasistance.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.sys.velazquez.casanova.workshopasistance.R;
import com.sys.velazquez.casanova.workshopasistance.iface.PlaceSelector;
import com.sys.velazquez.casanova.workshopasistance.model.Place;

/**
 * Created by EverNote on 08/07/16.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> implements Filterable{

    private Context context;
    private List<Place> placeList = new ArrayList<>();
    private PlaceSelector selector;

    public PlaceAdapter(List<Place> places, Context context, PlaceSelector selector) {
        this.placeList = places;
        this.context = context;
        this.selector = selector;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        final Place place = placeList.get(position);
        holder.tv_name.setText(place.getName());
        holder.tv_schedule.setText(place.getSchedule());
        holder.tv_distace.setText(place.getDistance());
        holder.rb_qualification.setEnabled(false);
        float numStarts = 0;

        try {
            numStarts = Float.parseFloat(place.getRanking());
            System.out.println("numStarts ::: " + numStarts);
            holder.rb_qualification.setRating(numStarts);
        } catch (Exception e) {
            System.out.println("numStarts ::: " + numStarts);
        } finally {
            holder.rb_qualification.setRating(numStarts);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.selector(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public void setFilter(List<Place> tmp_places){
        placeList = new ArrayList<>();
        placeList.addAll(tmp_places);
        notifyDataSetChanged();
    }

    class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_distace;
        private TextView tv_schedule;
        private RatingBar rb_qualification;

        public PlaceHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tv_distace = (TextView) itemView.findViewById(R.id.tvDistance);
            tv_schedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            rb_qualification = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }

}
