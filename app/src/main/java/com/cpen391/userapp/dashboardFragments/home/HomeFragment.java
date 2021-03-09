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

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private ArrayList<HashMap<String,String>> parkedCarsList = new ArrayList<>();
    private View v;
    private  HashMap<String, String> userInfo = new HashMap<>();
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;

    /**
     * To create a new instance of the HomeFragment
     * @return A new instance of fragment HomeFragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        v = inflater.inflate(R.layout.fragment_home,container,false);
        /* initialize the list for all currently parked cars */
        createList();
        /* create the horizontal recycler view of the list */
        initRecyclerView();

        /* create Retrofit component for REST API communication */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        /* Get the user information from the database */
        getMe();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController= Navigation.findNavController(v);

        /* Navigation to the Account settings page */
        ImageButton accountBtn = v.findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.firstName, userInfo.get(Constants.firstName));
                bundle.putString(Constants.lastName, userInfo.get(Constants.lastName));
                bundle.putString(Constants.email, userInfo.get(Constants.email));
                navController.navigate(R.id.action_homeFragment_to_accountFragment, bundle);
            }
        });
    }

    /** Function to initialize the recycler view list of the parked cars
    *  Show message if list is empty
    **/
    private void initRecyclerView(){
        RecyclerView recyclerView = v.findViewById(R.id.parkedList);
        TextView  emptyView = v.findViewById(R.id.empty_view);
        if(parkedCarsList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            ParkedCarsRecycler adapter = new ParkedCarsRecycler(getContext(), parkedCarsList);
            recyclerView.setAdapter(adapter);
        }
    }

    /** Create a list from parked Car information
     *  Currently just adds pre-populated information,
     *  Will be connected to database when backend car API is ready
     * */
    private void createList(){
        HashMap<String,String> map1 = new HashMap<String,String>();
        map1.put("plateNo", "123ABC");
        map1.put("carNickName", "Honda");
        map1.put("startTime", "10:00AM");
        map1.put("meterNo", "0001");
        map1.put("duration", "00:15:33");
        map1.put("cost", "10.75");

        HashMap<String,String> map2 = new HashMap<String,String>();
        map2.put("plateNo", "EFG345");
        map2.put("carNickName", "Toyota");
        map2.put("startTime", "10:00AM");
        map2.put("meterNo", "0015");
        map2.put("duration", "00:15:33");
        map2.put("cost", "20.75");

        HashMap<String,String> map3 = new HashMap<String,String>();
        map3.put("plateNo", "789HIJ");
        map3.put("carNickName", "Nissan");
        map3.put("startTime", "3:00PM");
        map3.put("meterNo", "0016");
        map3.put("duration", "00:15:33");
        map3.put("cost", "17.75");

        parkedCarsList.add(map1);
        parkedCarsList.add(map2);
        parkedCarsList.add(map3);
    }

    /* Get the user information of the account from database (name + email) */
    private void getMe(){
        Call<meResult> call = retrofitInterface.executeMe(Constants.Bear + " " + MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<meResult>() {
            @Override
            public void onResponse(Call<meResult> call, Response<meResult> response){
                /* save userInformation that can be passed to other pages */
                if (response.code() == 200) {
                    meResult result = response.body();
                    userInfo.put(Constants.firstName, result.getFirstName());
                    userInfo.put(Constants.lastName, result.getLastName());
                    userInfo.put(Constants.email, result.getEmail());

                    TextView nameText = v.findViewById(R.id.Welcome);
                    nameText.setText(Constants.welcome + userInfo.get(Constants.firstName) + Constants.exclamation);
                }
                /* Unsuccessful API call handling */
                else if (response.code() == 401){
                    /* Authentication error: token expired */
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    /* server error, print generic welcome message but prompt user to re login */
                    Toast.makeText(getContext(), Constants.serverError, Toast.LENGTH_SHORT).show();
                    TextView nameText = v.findViewById(R.id.Welcome);
                    nameText.setText(Constants.welcome + Constants.exclamation);
                }
            }
            @Override
            public void onFailure(Call<meResult> call, Throwable t){
                Toast.makeText(getContext(), Constants.systemError, Toast.LENGTH_SHORT).show();
                TextView nameText = v.findViewById(R.id.Welcome);
                nameText.setText(Constants.welcome + Constants.exclamation);
            }
        });
    }
}
