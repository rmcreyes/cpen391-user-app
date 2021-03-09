package com.cpen391.userapp.dashboardFragments.history;

import android.content.Context;
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
public class HistoryRecycler extends RecyclerView.Adapter<HistoryRecycler.ViewHolder> {

    private ArrayList<HashMap<String,String>> historyList = new ArrayList<>();
    private Context context;
    private OnItemListener mOnItemListener;

    /**
    * @param: Context c
    * @param: history: ArrayList in which each element will be created as an item in the Recycler view
     * @param onItemListener: component that will implement the functionality when an item is clicked
    **/
    public HistoryRecycler(Context c, ArrayList<HashMap<String,String>> history,  OnItemListener onItemListener){
        context = c;
        historyList = history;
        this.mOnItemListener = onItemListener;
    }

    /**
     * Connects layout of each item to layout_historyitem
     * returns a new view holder for each item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historyitem, parent, false);
        return new ViewHolder(view, this.mOnItemListener);
    }

    /**
     * Sets the text values on the UI for each item
     * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.plateNo.setText(historyList.get(position).get(Constants.plateNo));
        holder.date.setText(historyList.get(position).get(Constants.date));
        holder.cost.setText(historyList.get(position).get(Constants.cost));
    }

    /**
     * @return number of items in the historyList
     */
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * Bind components on the UI to the view holder
     * implements onClickListener; each item is clickable
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView plateNo, date, cost;
        OnItemListener onItemListener;

        /**
         * @param itemView
         * @param onItemListener
         */

        public ViewHolder(View itemView, OnItemListener onItemListener){
            super(itemView);
            plateNo = itemView.findViewById(R.id.plateNo);
            cost = itemView.findViewById(R.id.cost);
            date = itemView.findViewById(R.id.date);

            /* connect to the item listener that specifies the clicking actions */
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        /**
         * call the corresponding onItemClick function for the item
         */
        @Override
        public void onClick(View v) {
            this.onItemListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnItemListener{
        void onItemClick(int position);
    }
}
