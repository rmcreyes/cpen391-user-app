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

public class loginActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Button loginBtn = findViewById(R.id.login);
        EditText emailEdit = findViewById(R.id.emailEdit);
        EditText passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Constants.closeKeyboard(loginActivity.this);

                /* put log in values in a hashmap*/
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.email, emailEdit.getText().toString());
                map.put(Constants.password, passwordEdit.getText().toString());

                Call<loginResult> call = retrofitInterface.executeLogin(map);
                call.enqueue(new Callback <loginResult>() {
                    @Override
                    public void onResponse(Call<loginResult> call, Response<loginResult> response){
                        if (response.code() == 200) {
                            loginResult result = response.body();
                            MainActivity.sp.edit().putString(Constants.userId,result.getUserId()).apply();
                            MainActivity.sp.edit().putString(Constants.token,result.getToken()).apply();
                            MainActivity.sp.edit().putString(Constants.email,result.getEmail()).apply();
                            userValid();
                        }
                        else if (response.code() == 401){
                            Toast.makeText(loginActivity.this, Constants.login_Incorrect_msg, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(loginActivity.this, Constants.login_error_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<loginResult> call, Throwable t){
                        Toast.makeText(loginActivity.this, Constants.login_failed+t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /* Log the user into the application */
    private void userValid (){

        /* Mark user as logged-in in the shared Preference, to keep user logged-in */
        MainActivity.sp.edit().putBoolean(Constants.sp_logged,true).apply();
        /* Transition to dashboard page */
        Intent intent = new Intent(loginActivity.this, dashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}