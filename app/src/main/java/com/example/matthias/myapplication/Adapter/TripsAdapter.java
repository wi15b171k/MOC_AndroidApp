package com.example.matthias.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.R;

import java.util.List;

/**
 * Created by FH on 24.02.2018.
 */

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsItemViewHolder> {
    private List<Trip> mTrips;
    private ITripsItemClickListener mainClickListener;

    public TripsAdapter(List<Trip> mTrips, ITripsItemClickListener mainClickListener) {
        this.mTrips = mTrips;
        this.mainClickListener = mainClickListener;
    }
    public void swapList(List<Trip> newList){
        mTrips = newList;
        notifyDataSetChanged();
    }
    public class TripsItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView mTripName;
        public TripsItemViewHolder(View itemView) {
            super(itemView);
            mTripName = itemView.findViewById(R.id.tv_trips);
            itemView.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            mTripName.setText(trip.name);
        }

        @Override
        public void onClick(View view) {
            int clickedItem = getAdapterPosition();
            if(clickedItem < mTrips.size()){
                mainClickListener.onListItemClick(mTrips.get(clickedItem));
            }

        }
    }
    public interface ITripsItemClickListener{
        void onListItemClick(Trip clickedTrip);
    }
    @Override
    public TripsAdapter.TripsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trips_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new TripsAdapter.TripsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripsAdapter.TripsItemViewHolder holder, int position) {
        if (position < mTrips.size())
            holder.bind(mTrips.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

}
