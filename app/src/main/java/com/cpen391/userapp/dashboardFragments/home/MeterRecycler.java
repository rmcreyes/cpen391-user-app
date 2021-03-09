package com.cpen391.userapp.dashboardFragments.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/** RecyclerView adapter that creates the list of parking histories
 **/
public class MeterRecycler extends RecyclerView.Adapter<MeterRecycler.ViewHolder> {

    private ArrayList<HashMap<String,String>> meterList = new ArrayList<>();
    private Context context;

    /**
    * @param: Context c
    * @param: meter: ArrayList in which each element will be created as an item in the Recycler view
    **/
    public MeterRecycler(Context c, ArrayList<HashMap<String,String>> meter){
        context = c;
        meterList = meter;
    }

    /**
     * Connects layout of each item to layout_meteritem
     * returns a new view holder for each item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meteritem, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the text values on the UI for each item
     * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.meterNo.setText(meterList.get(position).get(Constants.meterNo));
        holder.unitPrice.setText(meterList.get(position).get(Constants.unitPrice));
        holder.date.setText(meterList.get(position).get(Constants.updated));
        if(meterList.get(position).get(Constants.isOccupied).equals("true")){
            holder.Occupied.setText(Constants.Occupied);
            holder.Occupied.setTextColor(Color.RED);
        }
        else{
            holder.Occupied.setText(Constants.Empty);
            holder.Occupied.setTextColor(Color.BLUE);
        }
    }

    /**
     * @return number of items in the historyList
     */
    @Override
    public int getItemCount() {
        return meterList.size();
    }

    /**
     * Bind components on the UI to the view holder
     * implements onClickListener; each item is clickable
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView meterNo, unitPrice, date, Occupied;

        /**
         * @param itemView
         */

        public ViewHolder(View itemView){
            super(itemView);
            meterNo = itemView.findViewById(R.id.meterNo);
            unitPrice = itemView.findViewById(R.id.unitPrice);
            Occupied = itemView.findViewById(R.id.Occupied);
            date = itemView.findViewById(R.id.date);
        }

    }

}
