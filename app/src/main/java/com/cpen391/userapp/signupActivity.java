package com.cpen391.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class signupActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /* Get components on view */
        Button signupBtn = findViewById(R.id.signup);
        EditText emailEdit = findViewById(R.id.emailEdit);
        EditText passwordEdit = findViewById(R.id.passwordEdit);
        EditText firstName = findViewById(R.id.firstNameEdit);
        EditText lastName = findViewById(R.id.lastNameEdit);

        /* Create retrofit interface API connection */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        /* On click function when user clicks the "signup" button to register the user */
        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Constants.closeKeyboard(signupActivity.this);

                // debug message
                Toast.makeText(signupActivity.this, Constants.signup_debug_msg, Toast.LENGTH_SHORT).show();

                /* Put all fields in a hashmap*/
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.firstName, firstName.getText().toString());
                map.put(Constants.lastName, lastName.getText().toString());
                map.put(Constants.email, emailEdit.getText().toString());
                map.put(Constants.password, passwordEdit.getText().toString());

                /* check that all fields are filled */
                if (checkEntryValid(map)!= true){
                    return;
                }

                /* send API Post request and wait for the response*/
                Call<loginResult> call = retrofitInterface.executeSignup(map);
                call.enqueue(new Callback<loginResult>() {
                    @Override
                    public void onResponse(Call<loginResult> call, Response<loginResult> response){
                        /* Registered successfully */
                        if (response.code() == 201) {
                            /* save the token, userID, and email to the shared preference */
                            loginResult result = response.body();
                            MainActivity.sp.edit().putString(Constants.userId,result.getUserId()).apply();
                            MainActivity.sp.edit().putString(Constants.token,result.getToken()).apply();
                            MainActivity.sp.edit().putString(Constants.email,result.getEmail()).apply();
                            signupSuccess();
                        }
                        /* unsuccessful due to invalid credentials */
                        else if (response.code() == 422){
                            Toast.makeText(signupActivity.this, Constants.signup_Invalid_msg, Toast.LENGTH_LONG).show();
                        }
                        /* unsuccessful due to internal server error */
                        else {
                            Toast.makeText(signupActivity.this, Constants.signup_error_msg +response.message(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<loginResult> call, Throwable t){
                        Toast.makeText(signupActivity.this, Constants.sign_failed+t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /*
     * Check that all entries in the registration from are filled
     * */
    private void signupSuccess (){
        //debug message
        Toast.makeText(signupActivity.this, Constants.signup_success_msg, Toast.LENGTH_LONG).show();

        /* Set the sharedPreference "logged" field to "true" to keep users logged in */
        MainActivity.sp.edit().putBoolean(Constants.sp_logged,true).apply();

        /* transition to the dashboard activity*/
        Intent intent = new Intent(signupActivity.this, dashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
    * Check that all entries in the registration from are filled
    * */
    private boolean checkEntryValid(HashMap<String,String> map){
        boolean valid = true;

        if(map.get(Constants.firstName) == null || map.get(Constants.firstName).trim().isEmpty()){
            valid = false;
            Toast.makeText(signupActivity.this, "First Name cannot be empty!", Toast.LENGTH_LONG).show();
        }
        else if(map.get(Constants.lastName) == null || map.get(Constants.lastName).trim().isEmpty()){
            valid = false;
            Toast.makeText(signupActivity.this, "Last name cannot be empty!", Toast.LENGTH_LONG).show();
        }
        else if(map.get(Constants.email) == null || map.get(Constants.email).trim().isEmpty()){
            valid = false;
            Toast.makeText(signupActivity.this, "Email cannot be empty!", Toast.LENGTH_LONG).show();
        }
        else if(map.get(Constants.password) == null || map.get(Constants.password).trim().isEmpty()){
            valid = false;
            Toast.makeText(signupActivity.this, "password cannot be empty!", Toast.LENGTH_LONG).show();
        }
        return valid;
    }

}