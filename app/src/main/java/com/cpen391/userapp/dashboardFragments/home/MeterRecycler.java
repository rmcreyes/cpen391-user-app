package com.cpen391.userapp.dashboardFragments.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.R;
import com.cpen391.userapp.dashboardFragments.car.CarsRecycler;

import java.util.ArrayList;
import java.util.HashMap;

/** RecyclerView adapter that creates the list of parking histories
 **/
public class MeterRecycler extends RecyclerView.Adapter<MeterRecycler.ViewHolder> {

    private ArrayList<HashMap<String, String>> meterList = new ArrayList<>();
    private Context context;
    private OnResetListener mOnResetListener;

    /**
     * @param: Context c
     * @param: meter: ArrayList in which each element will be created as an item in the Recycler view
     **/
    public MeterRecycler(Context c, ArrayList<HashMap<String, String>> meter, OnResetListener onResetListener) {
        context = c;
        meterList = meter;
        this.mOnResetListener = onResetListener;
    }

    /**
     * Connects layout of each item to layout_meteritem
     * returns a new view holder for each item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meteritem, parent, false);
        return new ViewHolder(view, this.mOnResetListener);
    }

    /**
     * Sets the text values on the UI for each item
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String meterNoStr = meterList.get(position).get(Constants.meterNo);
        holder.meterNo.setText(meterNoStr.substring(meterNoStr.length() - 6));
        holder.unitPrice.setText(meterList.get(position).get(Constants.unitPrice));
        holder.date.setText(meterList.get(position).get(Constants.updated));
        if (meterList.get(position).get(Constants.isOccupied).equals("true")) {
            holder.Occupied.setText(Constants.Occupied);
            holder.Occupied.setTextColor(Color.RED);
            if (meterList.get(position).get(Constants.isConfirmed).equals("true")) {
                holder.Confirmed.setText("(Confirmed)");
            } else {
                holder.Confirmed.setText("(Not Confirmed)");
            }
            holder.Confirmed.setVisibility(View.VISIBLE);
        } else {
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView meterNo, unitPrice, date, Occupied, Confirmed;
        ImageView resetBtn;
        OnResetListener onResetListener;

        /**
         * @param itemView
         */

        public ViewHolder(View itemView, OnResetListener onResetListener) {
            super(itemView);
            this.onResetListener = onResetListener;
            meterNo = itemView.findViewById(R.id.meterNo);
            unitPrice = itemView.findViewById(R.id.unitPrice);
            Occupied = itemView.findViewById(R.id.Occupied);
            date = itemView.findViewById(R.id.date);
            Confirmed = itemView.findViewById(R.id.Confirmed);
            resetBtn = itemView.findViewById(R.id.resetBtn);
            resetBtn.setOnClickListener(this);
        }
        /**
         * call the corresponding onItemClick function for the item
         */
        @Override
        public void onClick(View v) {
            this.onResetListener.onResetClick(getAdapterPosition(), this);
        }

    }

    public interface OnResetListener {
        void onResetClick(int position, ViewHolder holder);
    }
}
