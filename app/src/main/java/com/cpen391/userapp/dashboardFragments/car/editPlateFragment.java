package com.cpen391.userapp.dashboardFragments.car;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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


public class editPlateFragment extends Fragment {

    private View v;
    private TextView plateNo;
    private EditText carNickName;
    private Retrofit retrofit;
    private NavController navController;
    private static RetrofitInterface retrofitInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_edit_plate,container,false);
        plateNo = v.findViewById(R.id.plateNo);
        plateNo.setText(getArguments().getString(Constants.plateNo));
        carNickName = v.findViewById(R.id.carNickName);
        carNickName.setText(getArguments().getString(Constants.carNickName));
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
         navController= Navigation.findNavController(v);

         /* Set the components based on arguments passed in */
        plateNo = v.findViewById(R.id.plateNo);
        plateNo.setText(getArguments().getString(Constants.plateNo));
        carNickName = v.findViewById(R.id.carNickName);
        carNickName.setText(getArguments().getString(Constants.carNickName));

        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                navController.navigate(R.id.action_editPlateFragment_to_carFragment);

            }
        });
        /* set up retrofit interface*/
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        /* Set up onclick even for save button */
        Button saveBtn = v.findViewById(R.id.saveChanges);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                updateCar();
            }
        });

        /* Set up onclick even for remove button */
        Button delBtn = v.findViewById(R.id.remove);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                deleteCar();
            }
        });
    }

    /**
     * Update the car item to database
     */
    private void updateCar(){
        /* put log in values in a hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.licensePlate, plateNo.getText().toString());
        map.put(Constants.carName, carNickName.getText().toString());

        Call<Void> call = retrofitInterface.updateCar(MainActivity.sp.getString(Constants.userId, ""), getArguments().getString(Constants.id), "Bear "+ MainActivity.sp.getString(Constants.token, ""), map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 200) {
                    Toast.makeText(getActivity(), Constants.saveSuccess, Toast.LENGTH_LONG).show();
                }
                /* Handle unsuccessful API call */
                else if (response.code() == 422 ||response.code() == 400){
                    /* Print error message */
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), Constants.editPlateError+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                /* Authentication failed: token expired */
                else if (response.code() == 401){
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                /* server error, prompt user to log out and retry*/
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

    /**
     * Delete the car item
     */
    private void deleteCar(){

        Call<Void> call = retrofitInterface.deleteCar(MainActivity.sp.getString(Constants.userId, ""), getArguments().getString(Constants.id), "Bear "+ MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 200) {
                    /* inform user that item delete was successful, navigate back to car page */
                    Toast.makeText(getActivity(), Constants.carDeleted, Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_editPlateFragment_to_carFragment);
                }
                /* Handel unsuccessful API call */
                else if (response.code() == 422 ||response.code() == 404){
                    /* Report the error message */
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), Constants.delError+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                /* Authentication failed: token expired */
                else if (response.code() == 401){
                    Toast.makeText(getActivity(), Constants.tokenError, Toast.LENGTH_LONG).show();
                    // for now, just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                /* server error, prompt user to log out and retry*/
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