package com.cpen391.userapp.dashboardFragments.car;

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

/** RecyclerView adapter that creates the list of cars
 **/
public class CarsRecycler extends RecyclerView.Adapter<CarsRecycler.ViewHolder> {

    private ArrayList<HashMap<String,String>> carsList = new ArrayList<>();
    private Context context;
    private View view;
    private OnItemListener mOnItemListener;

    /**
     * @param: Context c
     * @param: cars: ArrayList in which each element will be created as an item in the Recycler view
     * @param onItemListener: component that will implement the functionality when an item is clicked
     **/
    public CarsRecycler(Context c, ArrayList<HashMap<String,String>> cars, OnItemListener onItemListener){
        context = c;
        carsList =cars;
        this.mOnItemListener = onItemListener;
    }

    /**
     * Connects layout of each item to layout_carsitem
     * returns a new view holder for each item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carsitem, parent, false);
        return new ViewHolder(view, this.mOnItemListener);
    }

    /**
     * Sets the text values on the UI for each item
     * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.plateNo.setText(carsList.get(position).get(Constants.plateNo));
        holder.carNickName.setText(carsList.get(position).get(Constants.carNickName));

    }

    /**
     * @return number of items in the carsList
     */
    @Override
    public int getItemCount() {
        return carsList.size();
    }

    /**
     * Bind components on the UI to the view holder
     * implements onClickListener; each item is clickable
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView plateNo, carNickName;
        OnItemListener onItemListener;

        public ViewHolder(View itemView, OnItemListener onItemListener){
            super(itemView);
            plateNo = itemView.findViewById(R.id.plateNo);
            carNickName = itemView.findViewById(R.id.carNickName);
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
