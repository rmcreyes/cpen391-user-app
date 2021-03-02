package com.cpen391.userapp.dashboardFragments.car;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.MainActivity;
import com.cpen391.userapp.R;
import com.cpen391.userapp.RetrofitInterface;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class addPlateFragment extends Fragment {

    private View v;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private EditText plateNo;
    private EditText carNickName;
    private NavController navController;

    /**
     * To create a new instance of the editPlateFragment
     * @return A new instance of fragment editPlateFragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_plate,container,false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(v);


        /* Setting for the back button */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Make sure that keyboard is hidden when navigating to a new page */
                Constants.closeKeyboard(getActivity());
                navController.navigate(R.id.action_addPlateFragment_to_carFragment);
            }
        });

        plateNo = v.findViewById(R.id.plateNo);
        carNickName = v.findViewById(R.id.carNickName);
        Button addBtn = v.findViewById(R.id.addbtn);

        /* Submission of the new car information */
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                retrofitInterface = retrofit.create(RetrofitInterface.class);
                postCar();
            }
        });
    }

    /**
     * Add a new car to the database
     */
    private void postCar(){
        /* put log in values in a hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.licensePlate, plateNo.getText().toString());
        map.put(Constants.carName, carNickName.getText().toString());

        Call<Void> call = retrofitInterface.postCar(MainActivity.sp.getString(Constants.userId, ""), "Bear "+ MainActivity.sp.getString(Constants.token, ""), map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 201) {
                    /* navigate back to car page */
                    Toast.makeText(getActivity(), Constants.saveSuccess, Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_addPlateFragment_to_carFragment);
                }
                /* handle unsuccessful API calls */
                else if (response.code() == 422 ||response.code() == 400){
                    /* Print error message */
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), Constants.addPlateError+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                /* Authentication error: token expired*/
                else if (response.code() == 401){
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), Constants.serverError, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t){
                Toast.makeText(getActivity(), Constants.systemError, Toast.LENGTH_LONG).show();
            }
        });
    }
}