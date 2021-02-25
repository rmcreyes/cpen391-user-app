package com.cpen391.userapp.dashboardFragments.car;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class CarFragment extends Fragment implements CarsRecycler.OnItemListener {

    private ArrayList<HashMap<String,String>> carList = new ArrayList<>();
    private View v;

    /**
     * To create a new instance of the CarFragment
     * @return A new instance of fragment CarFragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        v = inflater.inflate(R.layout.fragment_car,container,false);
        /* Set up the list of registered cars */
        createList();
        /* initiate the recycler view of the car list */
        initRecyclerView();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        /* Navigation to adding a car to the account */
        Button addBtn = v.findViewById(R.id.addbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NavController navController= Navigation.findNavController(v);
                navController.navigate(R.id.action_carFragment_to_addPlateFragment);
            }
        });
    }


    /* initiate the recycler view of the car list.
    *  Show the "EMPTY" message if list is empty
    * */
    private void initRecyclerView(){
        RecyclerView recyclerView = v.findViewById(R.id.carList);
        TextView emptyView = v.findViewById(R.id.empty_view);
        if(carList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            CarsRecycler adapter = new CarsRecycler(getContext(), carList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    /* Create a list from Car information
    *  Currently just adds pre-populated information,
    *  Will be connected to database when backend car API is ready
    * */
    private void createList(){
        HashMap<String,String> map1 = new HashMap<String,String>();
        map1.put("plateNo", "123ABC");
        map1.put("carNickName", "Honda");

        HashMap<String,String> map2 = new HashMap<String,String>();
        map2.put("plateNo", "456EFG");
        map2.put("carNickName", "Toyota");

        HashMap<String,String> map3 = new HashMap<String,String>();
        map3.put("plateNo", "789HIJ");
        map3.put("carNickName", "Nissan");

        carList.add(map1);
        carList.add(map2);
        carList.add(map3);
    }

    /* Navigation to the edit page of each cardView item (car item), when the card is clicked */
    @Override
    public void onItemClick(int position) {
        final NavController navController= Navigation.findNavController(v);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.plateNo, carList.get(position).get(Constants.plateNo));
        bundle.putString(Constants.carNickName, carList.get(position).get(Constants.carNickName));
        navController.navigate(R.id.action_carFragment_to_editPlateFragment, bundle);
    }
}

