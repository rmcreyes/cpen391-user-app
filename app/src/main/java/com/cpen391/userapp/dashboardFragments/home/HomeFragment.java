package com.cpen391.userapp.dashboardFragments.home;
import android.content.Intent;
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
import com.cpen391.userapp.dashboardFragments.car.ParkedCarsRecycler;
import com.cpen391.userapp.dashboardFragments.car.allCarsResult;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private ArrayList<HashMap<String, String>> parkedCarsList = new ArrayList<>();
    private View v;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private ShimmerFrameLayout shimmer;

    /**
     * To create a new instance of the HomeFragment
     *
     * @return A new instance of fragment HomeFragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);
        shimmer = v.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        /* create Retrofit component for REST API communication */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        /* Get the user information from the database */
        if(!MainActivity.sp.contains(Constants.firstName)){
            getMe();
        }
        else{
            TextView nameText = v.findViewById(R.id.Welcome);
            nameText.setText(Constants.welcome + MainActivity.sp.getString(Constants.firstName, null) + Constants.exclamation);
        }
        /* initialize the list for all currently parked cars */
        createList();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Navigation to the Account settings page */
        ImageButton accountBtn = v.findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_homeFragment_to_accountFragment);
            }
        });
    }

    /**
     * Function to initialize the recycler view list of the parked cars
     * Show message if list is empty
     **/
    private void initRecyclerView() {
        RecyclerView recyclerView = v.findViewById(R.id.parkedList);
        TextView emptyView = v.findViewById(R.id.empty_view);
        shimmer.stopShimmer();
        shimmer.hideShimmer();
        shimmer.setVisibility(View.GONE);
        if (parkedCarsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            ParkedCarsRecycler adapter = new ParkedCarsRecycler(getContext(), parkedCarsList);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Create a list from parked Car information
     * Queries the database to get all info
     * Currently also adds pre-populated information to show format
     */
    private void createList() {

        /* add pre-populated data */
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("plateNo", "123ABC");
        map1.put("carNickName", "Honda");
        map1.put("meterNo", "000001");

        String st = "2021-03-11T08:25:25.165Z";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date startDate = format.parse(st);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            map1.put(Constants.startTime, sdf.format(startDate));
            Date currentTime = Calendar.getInstance().getTime();
            String duration = duration(startDate, currentTime);
            map1.put(Constants.duration, duration);
            map1.put("cost", calcCost(5,duration));
        } catch (ParseException e) {
            map1.put(Constants.startTime, st);
        }

        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("plateNo", "EFG345");
        map2.put("carNickName", "Toyota");
        map2.put("startTime", "10:00:00");
        map2.put("meterNo", "000015");
        map2.put("duration", "00:15");
        map2.put("cost", "20.75");

        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put("plateNo", "789HIJ");
        map3.put("carNickName", "Nissan");
        map3.put("startTime", "15:00:00");
        map3.put("meterNo", "000016");
        map3.put("duration", "01:30");
        map3.put("cost", "17.75");

        parkedCarsList.add(map1);
        parkedCarsList.add(map2);
        parkedCarsList.add(map3);

        /* Query database */
        getCurrentParking();

        for ( HashMap<String, String> car: parkedCarsList){
            if (car.containsKey("carId")){
                getOneCar(car);
            }
        }
    }

    /**
     * API call:  Get the user information of the account from database (name + email)
     * */
    private void getMe() {
        Call<meResult> call = retrofitInterface.executeMe(Constants.Bear + " " + MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<meResult>() {
            @Override
            public void onResponse(Call<meResult> call, Response<meResult> response) {
                /* save userInformation that can be passed to other pages */
                if (response.code() == 200) {
                    meResult result = response.body();
                    MainActivity.sp.edit().putString(Constants.firstName, result.getFirstName()).apply();
                    MainActivity.sp.edit().putString(Constants.lastName, result.getLastName()).apply();
                    MainActivity.sp.edit().putString(Constants.email, result.getEmail()).apply();
                    MainActivity.sp.edit().putBoolean(Constants.admin, result.getAdmin()).apply();

                    TextView nameText = v.findViewById(R.id.Welcome);
                    nameText.setText(Constants.welcome + MainActivity.sp.getString(Constants.firstName, null) + Constants.exclamation);
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 401) {
                    /* Authentication error: token expired */
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    Constants.tokenExpired();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    /* server error, prompt user to re login */
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();

                    // for now, just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<meResult> call, Throwable t) {
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();

                // for now, just log out if token expires or if we get other API errors codes
                MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     *  API call: Get the all current parking instances */
    private void getCurrentParking() {
        Call<currParkResult> call = retrofitInterface.getCurrParking(MainActivity.sp.getString(Constants.userId, ""), (Constants.Bear + " " + MainActivity.sp.getString(Constants.token, "")));
        call.enqueue(new Callback<currParkResult>() {
            @Override
            public void onResponse(Call<currParkResult> call, Response<currParkResult> response) {
                /* save information of each parking instance  */
                if (response.code() == 200) {
                    currParkResult result = response.body();
                    for (HashMap<String, String> m : result.getCurrentParkings()) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(Constants.plateNo, m.get(Constants.licensePlate));
                        map.put(Constants.meterNo, m.get("meterId").substring(m.get("meterId").length() - 6));

                        /* save carId so that we can query for the car name later */
                        map.put("carId", m.get("carId"));

                        /* Format the time stamp for the parking start time */
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date startDate = format.parse(m.get(Constants.startTime));
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            /* Save the starting time */
                            map.put(Constants.startTime, sdf.format(startDate));
                            Date currentTime = Calendar.getInstance().getTime();

                            /* calculate the duration till now */
                            String duration = duration(startDate, currentTime);
                            map.put(Constants.duration, duration);

                            /* Estimate cost up till now */
                            map.put(Constants.cost, calcCost(Float.parseFloat(m.get(Constants.unitPrice)),duration));
                        } catch (ParseException e) {
                            map.put(Constants.startTime, (m.get(Constants.startTime)));
                        }
                        /* add item to list */
                        parkedCarsList.add(map);
                    }
                    /* create the horizontal recycler view of the list */
                    initRecyclerView();
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 404) {
                    /* empty list */
                    initRecyclerView();
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 401) {
                    /* Authentication error: token expired */
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    Constants.tokenExpired();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    /* server error, print generic welcome message but prompt user to re login */
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<currParkResult> call, Throwable t) {
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get  cars names for cars in the carlist
     */
    private void getOneCar (HashMap<String, String> car){
        Call<oneCarResult> call = retrofitInterface.getOneCar(MainActivity.sp.getString(Constants.userId, ""), car.get("carId"),Constants.Bear + " " + MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<oneCarResult>() {
            @Override
            public void onResponse(Call<oneCarResult> call, Response<oneCarResult> response){
                /* API call success */
                if (response.code() == 200) {
                    oneCarResult result = response.body();
                    car.put(Constants.carNickName, result.getCarName());
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 404){
                    car.put(Constants.carNickName, "Error");
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
                    car.put(Constants.carNickName, "Error");
                }
            }
            @Override
            public void onFailure(Call<oneCarResult> call, Throwable t){
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                car.put(Constants.carNickName, "Error");
            }
        });
    }

    /**
     *  Calculate the duration in Hours and Minutes between two date objects
     * @param start_date Start time of the instance
     * @param end_date End time of the instance
     * @return a String in HH:MM format as the duration between the two date objects
     */
    static String duration(Date start_date, Date end_date) {
        /* calculate time difference in miliseconds*/
        long difference_In_Time
                = end_date.getTime() - start_date.getTime();

        /* convert differences into hours and minutes*/
        long difference_In_Minutes = (difference_In_Time / (1000 * 60)%60);
        long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60));

        /* Form a string in HH:MM format */
        return (String.format("%02d", difference_In_Hours)+ ":" + String.format("%02d", difference_In_Minutes));
    }

    /**
     * Calculates the total cost based on the duration and hourly unit price
     * @param unitPrice hourly unit price
     * @param duration String indicating the duration in Hours and minutes
     * @return string of the total cost
     */
    static String calcCost(float unitPrice, String duration) {
        String delims = "[:]";
        String[] tokens = duration.split(delims);
        double timeHr = Math.ceil(Double.parseDouble(tokens[0]) + Double.parseDouble(tokens[1])/60);
        return(String.format("%.2f", timeHr*unitPrice));
    }
}
