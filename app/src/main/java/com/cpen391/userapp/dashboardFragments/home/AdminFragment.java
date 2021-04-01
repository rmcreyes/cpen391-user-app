package com.cpen391.userapp.dashboardFragments.home;

import android.graphics.Color;
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
import com.cpen391.userapp.MainActivity;
import com.cpen391.userapp.R;
import com.cpen391.userapp.RetrofitInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/** Fragment for the allowing ADMINs to view meter status
 * (and reset meter if error occurs during parking session)
 * */
public class AdminFragment extends Fragment implements MeterRecycler.OnResetListener {

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

        /* refresh status for meters */
        ImageButton refreshBtn = v.findViewById(R.id.refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createList();
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
            MeterRecycler adapter = new MeterRecycler(getContext(), meterList, this::onResetClick);
            recyclerView.setAdapter(adapter);
        }
    }

    /** Create a list from the meter information
     * Queries the database to get all info
     * Currently also adds pre-populated information to show format
     * */
    private void createList(){
        meterList.clear();

        /* Query database */
        getAllMeters();
    }

    /**
     * API call: Get all meter status
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
                        meterMap.put(Constants.meterNo, m.getMeterNo());
                        meterMap.put(Constants.unitPrice, m.getUnitPrice());
                        meterMap.put(Constants.isOccupied,m.getIsOccupied());
                        meterMap.put(Constants.isConfirmed,m.getIsConfirmed());

                        /* Format the timestamp for the last time meter was updated */
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

    /**
     * API call: Reset the clicked meter
     */
    private void resetMeter (HashMap<String, String> meter, MeterRecycler.ViewHolder holder){
        Call<meterResult> call = retrofitInterface.resetMeter(meter.get(Constants.meterNo));
        call.enqueue(new Callback<meterResult>() {
            @Override
            public void onResponse(Call <meterResult> call, Response<meterResult> response){
                /* API call success */
                if (response.code() == 200) {
                    meterResult result = response.body();
                    /* Add all meter items to the list for the recycler view */

                    meter.put(Constants.meterNo, result.getMeterNo());
                    meter.put(Constants.unitPrice, result.getUnitPrice());
                    meter.put(Constants.isOccupied,result.getIsOccupied());
                    meter.put(Constants.isConfirmed,result.getIsConfirmed());

                    /* Format the timestamp */
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date date = format.parse(result.getUpdated());
                        meter.put(Constants.updated,date.toString());
                    } catch (ParseException e) {
                        meter.put(Constants.updated,result.getUpdated());
                    }

                    /* Update the meter status card with the new status*/
                    String meterNoStr = meter.get(Constants.meterNo);
                    holder.meterNo.setText(meterNoStr.substring(meterNoStr.length() - 6));
                    holder.unitPrice.setText(meter.get(Constants.unitPrice));
                    holder.date.setText(meter.get(Constants.updated));

                    /* Update the occupied /confirmed message accordingly  */
                    if (meter.get(Constants.isOccupied).equals("true")) {
                        holder.Occupied.setText(Constants.Occupied);
                        holder.Occupied.setTextColor(Color.RED);
                        if (meter.get(Constants.isConfirmed).equals("true")) {
                            holder.Confirmed.setText(Constants.confirmed);
                        } else {
                            holder.Confirmed.setText(Constants.not_confirmed);
                        }
                        holder.Confirmed.setVisibility(View.VISIBLE);
                    } else {
                        holder.Occupied.setText(Constants.Empty);
                        holder.Occupied.setTextColor(Color.BLUE);
                    }
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 401){
                    Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                }
                /* any other return codes (unexpected) */
                else {
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<meterResult> call, Throwable t){
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  On click method when the reset button is clicked for each meter card
     **/
    @Override
    public void onResetClick(int position, MeterRecycler.ViewHolder holder) {
        HashMap<String, String> meter = meterList.get(position);
        resetMeter(meter, holder);
    }
}
