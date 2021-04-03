package com.cpen391.userapp.dashboardFragments.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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


/** Fragment for the allowing users to save and update payment information
 * */
public class paymentFragment extends Fragment {

    private View v;
    private EditText cvv;
    private EditText expDate;
    private EditText cardNum;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;


    /**
     * create a new instance of the paymentFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        v = inflater.inflate(R.layout.fragment_payment,container,false);
        /* create Retrofit component for REST API communication */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController= Navigation.findNavController(v);

        /* Connect to UI components, and set values to current values saved in the database */
        cardNum = v.findViewById(R.id.cardNumEdit);
        expDate = v.findViewById(R.id.expDateEdit);
        cvv = v.findViewById(R.id.cvvEdit);
        cardNum.setText(MainActivity.sp.getString(Constants.cardNum, null));
        expDate.setText(MainActivity.sp.getString(Constants.expDate, null));
        cvv.setText(MainActivity.sp.getString(Constants.cvv,null));

        /* Navigate back to the account page when back button is pressed */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                navController.navigate(R.id.action_paymentFragment_to_accountFragment);
            }
        });

        /* When user saves the payment information */
        Button saveBtn = v.findViewById(R.id.saveChanges);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* set if the user has their payment information set */
                Constants.closeKeyboard(getActivity());
                if( MainActivity.sp.getBoolean(Constants.paymentSet, false)){
                    putPayment();
                }else{
                    postPayment();
                }
            }
        });
    }

    /**
     * API CALL: Create/setup user payment information for the first time
     */
    private void postPayment(){
        /* put log in values in a hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.cardNum, cardNum.getText().toString());
        map.put(Constants.expDate, expDate.getText().toString());
        map.put(Constants.cvv, cvv.getText().toString());

        Call<Void> call = retrofitInterface.setPayment(MainActivity.sp.getString(Constants.userId, ""), map, "Bear "+ MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 201) {
                    Toast.makeText(getActivity(), Constants.saveSuccess, Toast.LENGTH_LONG).show();

                    /* save card information */
                    MainActivity.sp.edit().putString(Constants.cardNum, cardNum.getText().toString()).apply();
                    MainActivity.sp.edit().putString(Constants.expDate, expDate.getText().toString()).apply();
                    MainActivity.sp.edit().putString(Constants.cvv, cvv.getText().toString()).apply();
                    MainActivity.sp.edit().putBoolean(Constants.paymentSet, true).apply();
                }
                /* Error creating payment object, print error*/
                else if (response.code() == 422){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), "Adding Payment Info failed: "+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                /* authentication token expired */
                else if (response.code() == 401){
                    // Log out if token expires or if we get other API errors codes
                    Constants.tokenExpired();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                /* Show error if we received any other error codes */
                else {
                    Toast.makeText(getActivity(), "Server Error, please try again later", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t){
                Toast.makeText(getActivity(), "System Error, please try again later", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * API CALL: update User payment info
     */
    private void putPayment(){
        /* put log in values in a hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.cardNum, cardNum.getText().toString());
        map.put(Constants.expDate, expDate.getText().toString());
        map.put(Constants.cvv, cvv.getText().toString());


        Call<Void> call = retrofitInterface.updatePayment(MainActivity.sp.getString(Constants.userId, ""), map, "Bear "+ MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 200) {
                    Toast.makeText(getActivity(), Constants.saveSuccess, Toast.LENGTH_LONG).show();

                    /* save new information */
                    MainActivity.sp.edit().putString(Constants.cardNum, cardNum.getText().toString()).apply();
                    MainActivity.sp.edit().putString(Constants.expDate, expDate.getText().toString()).apply();
                    MainActivity.sp.edit().putString(Constants.cvv, cvv.getText().toString()).apply();

                }
                /* Error updating payment object, print error*/
                else if (response.code() == 422){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), "Updating Payment Info failed: "+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                /* authentication token expired */
                else if (response.code() == 401){
                    // Log out if token expires or if we get other API errors codes
                    Constants.tokenExpired();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                /* Show error if we received any other error codes */
                else {
                    Toast.makeText(getActivity(), "Server Error, please try again later", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t){
                Toast.makeText(getActivity(), "System Error, please try again later", Toast.LENGTH_LONG).show();
            }
        });
    }
}