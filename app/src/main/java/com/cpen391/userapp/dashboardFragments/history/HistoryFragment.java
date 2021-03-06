package com.cpen391.userapp.dashboardFragments.history;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.cpen391.userapp.dashboardFragments.home.currParkResult;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/** Fragment for the allowing users to view past parking history
 * */
public class HistoryFragment extends Fragment implements HistoryRecycler.OnItemListener {

    private ArrayList<HashMap<String,String>> historyList = new ArrayList<>();
    private View v;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private ShimmerFrameLayout shimmer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        /* Inflate the layout for this fragment */
        v = inflater.inflate(R.layout.fragment_history,container,false);
        shimmer = v.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        /* create Retrofit component for REST API communication */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

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
        shimmer.stopShimmer();
        shimmer.hideShimmer();
        shimmer.setVisibility(View.GONE);

        /* set the empty message if the list is empty */
        if(historyList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            HistoryRecycler adapter = new HistoryRecycler(getContext(), historyList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    /** Create a list from the parking History information
     * Queries the database to get all info
     * Currently also adds pre-populated information to show format
     * */
    private void createList(){
        historyList.clear();

        /* Query database */
        getPastParking();
    }

    /**
     * API call to get the all past parking instances
     **/
    private void getPastParking() {
        Call<historyResult> call = retrofitInterface.getPastParking(MainActivity.sp.getString(Constants.userId, ""), (Constants.Bear + " " + MainActivity.sp.getString(Constants.token, "")));
        call.enqueue(new Callback<historyResult>() {
            @Override
            public void onResponse(Call<historyResult> call, Response<historyResult> response) {
                /* save userInformation that can be passed to other pages */
                if (response.code() == 200) {
                    historyResult result = response.body();
                    for (HashMap<String, String> m : result.getPreviousParkings()) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(Constants.plateNo, m.get(Constants.licensePlate));
                        map.put(Constants.meterNo, m.get("meterId").substring(m.get("meterId").length() - 6));
                        map.put(Constants.cost, m.get(Constants.cost));

                        /* Parse timestamps for start time and date and duration */
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            /* Convert start time to date and time components*/
                            Date startDate = format.parse(m.get(Constants.startTime));

                            /* calculate duration */
                            Date endDate = format.parse(m.get(Constants.endTime));
                            map.put(Constants.duration, duration(startDate, endDate));

                            /* Convert start time into current time zone*/
                            TimeZone tz = Calendar.getInstance().getTimeZone();
                            int offset = tz.getOffset(Calendar.DST_OFFSET) + tz.getDSTSavings();
                            Calendar c = Calendar.getInstance();
                            c.setTime(startDate);
                            c.add(Calendar.SECOND, offset/1000);
                            startDate = c.getTime();

                            /* Save start date and time */
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            map.put(Constants.startTime, timeFormat.format(startDate));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            map.put(Constants.date, dateFormat.format(startDate));


                        } catch (ParseException e) {
                            map.put(Constants.startTime, (m.get(Constants.startTime)));
                        }
                        /* add item to list */
                        historyList.add(map);
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
            public void onFailure(Call<historyResult> call, Throwable t) {
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                TextView nameText = v.findViewById(R.id.Welcome);
                nameText.setText(Constants.welcome + Constants.exclamation);
            }
        });
    }

    /**
     *  Calculate the duration in Hours and Minutes between two date objects
     * @param start_date Start time of the instance
     * @param end_date End time of the instance
     * @return a String in HH:MM format as the duration between the two date objects
     */
    public static String duration(Date start_date, Date end_date) {
        /* calculate time difference in miliseconds*/
        long difference_In_Time
                = end_date.getTime() - start_date.getTime();

        /* convert differences into hours and minutes*/
        /* Adding one minute rounds up to the nearest minute (from the seconds)
         * takes care of case when parking session is in the first minute of the hour (like 00:00:30), this already counts as parking for the hour
         * */
        difference_In_Time += 1000*60; // add one minute
        long difference_In_Minutes = (difference_In_Time / (1000 * 60)%60);
        long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60));

        /* Form a string in HH:MM format */
        return (String.format("%02d", difference_In_Hours)+ ":" + String.format("%02d", difference_In_Minutes));
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
