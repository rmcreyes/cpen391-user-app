package com.cpen391.userapp.dashboardFragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.cpen391.userapp.R;
import com.cpen391.userapp.RetrofitInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminFragment extends Fragment {

    private ArrayList<HashMap<String,String>> meterList = new ArrayList<>();
    private View v;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_admin,container,false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* navigation for the Back button in the header */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_adminFragment_to_accountFragment);
            }
        });
    }

    /** Function to initialize the recycler view list of the parking history
     *  Show message if list is empty
     * */
    private void initRecyclerView(){
        RecyclerView recyclerView = v.findViewById(R.id.meterList);
        TextView emptyView = v.findViewById(R.id.empty_view);
        if(meterList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            MeterRecycler adapter = new MeterRecycler(getContext(), meterList);
            recyclerView.setAdapter(adapter);
        }
    }

    /** Create a list from the meter information
     * Queries the database to get all info
     * Currently also adds pre-populated information to show format
     * */
    private void createList(){

        /* pre populated list */
        HashMap<String,String> map1 = new HashMap<String,String>();
        map1.put(Constants.meterNo, "100c91");
        map1.put(Constants.unitPrice, "5");
        map1.put(Constants.isOccupied,"true");
        map1.put(Constants.updated, "Mon Mar 08 19:12:33 PST 2021");
        meterList.add(map1);

        HashMap<String,String> map2 = new HashMap<String,String>();
        map2.put(Constants.meterNo, "4a92c1");
        map2.put(Constants.unitPrice, "3.5");
        map2.put(Constants.isOccupied,"true");
        map2.put(Constants.updated, "Mon Mar 08 08:12:33 PST 2021");
        meterList.add(map2);

        /* Query database */
        getAllMeters();
    }

    /**
     * API call: Get all meters in the database registered under this account
     */
    private void getAllMeters (){
        Call<List<meterResult>> call = retrofitInterface.getMeter();
        call.enqueue(new Callback<List<meterResult>>() {
            @Override
            public void onResponse(Call <List<meterResult>> call, Response<List<meterResult>> response){
                /* API call success */
                if (response.code() == 200) {
                    List<meterResult> result = response.body();
                    /* Add all meter items to the list for the recycler view */
                    for( meterResult m : result) {

                        HashMap<String, String> meterMap = new HashMap<String, String>();
                        meterMap.put(Constants.meterNo, m.getMeterNo().substring(m.getMeterNo().length() - 6));
                        meterMap.put(Constants.unitPrice, m.getUnitPrice());
                        meterMap.put(Constants.isOccupied,m.getIsOccupied());

                        /* Format the timestamp */
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            Date date = format.parse(m.getUpdated());
                            meterMap.put(Constants.updated,date.toString());
                        } catch (ParseException e) {
                            meterMap.put(Constants.updated,m.getUpdated());
                        }
                        meterList.add(meterMap);
                    }
                    initRecyclerView();
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 401){
                    /* meter list is empty, just initialize empty list */
                    initRecyclerView();
                }
                else {
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();
                    initRecyclerView();
                }
            }
            @Override
            public void onFailure(Call<List<meterResult>> call, Throwable t){
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                initRecyclerView();
            }
        });
    }
}
