package com.cpen391.userapp.dashboardFragments.home;

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

/**
 * Fragment for editing basic user information
 */
public class PersonalInfoFragment extends Fragment {

    private View v;
    private Bundle bundle = new Bundle();
    private EditText lastNameEdit;
    private EditText firstNameEdit;
    private EditText emailEdit;
    public Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_personal_info, container, false);

        /* Connect to UI components, and set values to current values saved in the database */
        lastNameEdit = v.findViewById(R.id.lastNameEdit);
        firstNameEdit = v.findViewById(R.id.firstNameEdit);
        emailEdit = v.findViewById(R.id.emailEdit);
        emailEdit.setText(getArguments().getString(Constants.email));
        firstNameEdit.setText(getArguments().getString(Constants.firstName));
        lastNameEdit.setText(getArguments().getString(Constants.lastName));

        /* save arguments in a bundle first, in case user doesn't save new changes */
        bundle.putString(Constants.firstName, getArguments().getString(Constants.firstName));
        bundle.putString(Constants.lastName, getArguments().getString(Constants.lastName));
        bundle.putString(Constants.email, getArguments().getString(Constants.email));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Set up navigation back to account page */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                navController.navigate(R.id.action_personalInfoFragment_to_accountFragment, bundle);
            }
        });

        /* When saved button is clicked, save changes stored in the database */
        Button saveBtn = v.findViewById(R.id.saveChanges);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                retrofitInterface = retrofit.create(RetrofitInterface.class);
                putMe();
            }
        });
    }

    /**
     * Update the new user information in the database
     */
    private void putMe(){
        /* put log in values in a hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.email, emailEdit.getText().toString());
        map.put(Constants.lastName, lastNameEdit.getText().toString());
        map.put(Constants.firstName, firstNameEdit.getText().toString());


        Call<Void> call = retrofitInterface.updateProfile(map, "Bear "+ MainActivity.sp.getString(Constants.token, ""));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){
                /* API call success */
                if (response.code() == 200) {
                    Toast.makeText(getActivity(), Constants.saveSuccess, Toast.LENGTH_LONG).show();

                    bundle.putString(Constants.firstName, firstNameEdit.getText().toString());
                    bundle.putString(Constants.lastName, lastNameEdit.getText().toString());
                    bundle.putString(Constants.email, emailEdit.getText().toString());

                }
                else if (response.code() == 422){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), "Edit failed: "+ jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if (response.code() == 401){
                    // for now just log out if token expires or if we get other API errors codes
                    MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
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