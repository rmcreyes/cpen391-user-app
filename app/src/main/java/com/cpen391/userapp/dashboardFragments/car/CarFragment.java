package com.cpen391.userapp.dashboardFragments.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.MainActivity;
import com.cpen391.userapp.R;
import com.cpen391.userapp.RetrofitInterface;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarFragment extends Fragment implements CarsRecycler.OnItemListener {

    private ArrayList<HashMap<String,String>> carList = new ArrayList<>();
    private View v;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private ShimmerFrameLayout shimmer;

    /**
     * To create a new instance of the CarFragment
     * @return A new instance of fragment CarFragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        v = inflater.inflate(R.layout.fragment_car,container,false);
        shimmer = v.findViewById(R.id.shimmer);
        shimmer.startShimmer();
        /* create Retrofit component for REST API communication */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        /* Set up the list of registered cars */
        createList();
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

    /** Create a list from Car information
     *  Currently just adds pre-populated information,
     *  Will be connected to database when backend car API is ready
     * */
    private void createList(){
        carList.clear();
        getAllCars();
    }

    /* initiate the recycler view of the car list.
    *  Show the "EMPTY" message if list is empty
    * */
    private void initRecyclerView(){
        RecyclerView recyclerView = v.findViewById(R.id.carList);
        TextView emptyView = v.findViewById(R.id.empty_view);
        shimmer.stopShimmer();
        shimmer.hideShimmer();
        shimmer.setVisibility(View.GONE);
        if(carList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        } else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            CarsRecycler adapter = new CarsRecycler(getContext(), carList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Get all cars in the database registered under this account
     */
    private void getAllCars (){
        Call<allCarsResult> call = retrofitInterface.getAllCars(MainActivity.sp.getString(Constants.userId, ""), Constants.Bear + " " + MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<allCarsResult>() {
            @Override
            public void onResponse(Call<allCarsResult> call, Response<allCarsResult> response){
                /* API call success */
                if (response.code() == 200) {
                    allCarsResult result = response.body();
                    /* Add all car items to the car list for the recycler view */
                    for( HashMap<String, String> map : result.getCars()) {
                        HashMap<String, String> carmap = new HashMap<String, String>();
                        carmap.put(Constants.plateNo, map.get(Constants.licensePlate));
                        carmap.put(Constants.carNickName, map.get(Constants.carName));
                        carmap.put(Constants.id, map.get(Constants.id));
                        carList.add(carmap);
                    }
                    initRecyclerView();
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 404){
                    /* Car list is empty, just initialize empty list */
                    initRecyclerView();
                }
                /* Authentication error: token expired */
                else if (response.code() == 401){
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    Constants.tokenExpired();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();
                    initRecyclerView();
                }
            }
            @Override
            public void onFailure(Call<allCarsResult> call, Throwable t){
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                initRecyclerView();
            }
        });
    }

    /* Navigation to the edit page of each cardView item (car item), when the card is clicked */
    @Override
    public void onItemClick(int position) {
        final NavController navController= Navigation.findNavController(v);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.plateNo, carList.get(position).get(Constants.plateNo));
        bundle.putString(Constants.carNickName, carList.get(position).get(Constants.carNickName));
        bundle.putString(Constants.id, carList.get(position).get(Constants.id));
        navController.navigate(R.id.action_carFragment_to_editPlateFragment, bundle);
    }
}

