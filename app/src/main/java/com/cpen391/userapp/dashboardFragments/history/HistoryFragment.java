package com.cpen391.userapp.dashboardFragments.history;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryFragment extends Fragment implements HistoryRecycler.OnItemListener {

    private ArrayList<HashMap<String,String>> historyList = new ArrayList<>();
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_history,container,false);
        /* initialize the list for all currently parking history */
        createList();
        /* create the vertical recycler view of the list */
        initRecyclerView();
        return v;
    }

    /** Function to initialize the recycler view list of the parking history
     *  Show message if list is empty
     * */
    private void initRecyclerView(){
        RecyclerView recyclerView = v.findViewById(R.id.historyList);
        TextView emptyView = v.findViewById(R.id.empty_view);
        if(historyList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            HistoryRecycler adapter = new HistoryRecycler(getContext(), historyList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    /** Create a list from the parking History information
     *  Currently just adds pre-populated information,
     *  Will be connected to database when backend API is ready
     * */
    private void createList(){
        for(int i =0; i < 10; i++){
            HashMap<String,String> map1 = new HashMap<String,String>();
            map1.put("plateNo", "123ABC");
            map1.put("carNickName", "Honda");
            map1.put("date", "2021-01-22");
            map1.put("cost", "10.75");
            map1.put("meterNo", "0020");
            map1.put("duration", "00:50:20");
            map1.put("startTime", "10:00 AM");
            historyList.add(map1);
        }
    }

    /**
     * Navigation to the receipt page of each cardView item, when the a specific history item is clicked
     **/
    @Override
    public void onItemClick(int position) {
        final NavController navController= Navigation.findNavController(v);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.plateNo, historyList.get(position).get(Constants.plateNo));
        bundle.putString(Constants.date, historyList.get(position).get(Constants.date));
        bundle.putString(Constants.cost, historyList.get(position).get(Constants.cost));
        bundle.putString(Constants.meterNo, historyList.get(position).get(Constants.meterNo));
        bundle.putString(Constants.duration, historyList.get(position).get(Constants.duration));
        bundle.putString(Constants.startTime, historyList.get(position).get(Constants.startTime));
        navController.navigate(R.id.action_historyFragment_to_receiptFragment, bundle);
    }
}
