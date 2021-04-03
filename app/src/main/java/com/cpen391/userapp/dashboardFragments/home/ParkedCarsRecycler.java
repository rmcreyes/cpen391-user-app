package com.cpen391.userapp.dashboardFragments.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.userapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Create recycler view for the currently parked cars
 */
public class ParkedCarsRecycler extends RecyclerView.Adapter<ParkedCarsRecycler.ViewHolder> {

    private ArrayList<HashMap<String,String>> parkedCarsList;
    private Context context;

    /**
     * @param: Context c
     * @param: history: ArrayList in which each element will be created as an item in the Recycler view
     **/
    public ParkedCarsRecycler(Context c, ArrayList<HashMap<String,String>> parkedCars){
        context = c;
        parkedCarsList = parkedCars;
    }

    /**
     * Connects layout of each item to layout_parkedcarsitem
     * returns a new view holder for each item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_parkedcarsitem, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the text values on the UI for each item
     * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.plateNo.setText(parkedCarsList.get(position).get("plateNo"));
        holder.carNickName.setText(parkedCarsList.get(position).get("carNickName"));
        holder.meterNo.setText(parkedCarsList.get(position).get("meterNo"));
        holder.startTime.setText(parkedCarsList.get(position).get("startTime"));
        holder.duration.setText(parkedCarsList.get(position).get("duration"));
        holder.cost.setText(parkedCarsList.get(position).get("cost"));
    }

    /**
     * @return number of items in the historyList
     */
    @Override
    public int getItemCount() {
        return parkedCarsList.size();
    }

    /**
     * Bind components on the UI to the view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView plateNo, carNickName, startTime, meterNo, duration, cost;

        public ViewHolder(View itemView){
            super(itemView);
            plateNo = itemView.findViewById(R.id.plateNo);
            carNickName = itemView.findViewById(R.id.carNickName);
            startTime = itemView.findViewById(R.id.startTime);
            meterNo = itemView.findViewById(R.id.meterNo);
            duration = itemView.findViewById(R.id.duration);
            cost = itemView.findViewById(R.id.cost);
        }
    }
}
