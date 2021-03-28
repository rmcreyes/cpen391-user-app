package com.cpen391.userapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;

public class Constants {

    /* API call constant strings */
    /* Base url for server, change if want to just connect to localhost */
    public final static String BASE_URL = "https://backend391.herokuapp.com/";
    //public final static String BASE_URL = "http://10.0.2.2:80";
    public final static String Bear = "Bear";
    public final static String failedMessage = "API call Failed";
    public final static String saveSuccess ="Success! Changes Saved";
    public final static String systemError ="System Error, please try again later";
    public final static String serverError ="Server error, please try re logging in";
    public final static String tokenError="Token Expired, please log in again";
    public final static String delError="Delete Failed: ";
    public final static String carDeleted="Car successfully deleted!";
    public final static String editPlateError="Edit plate failed: ";
    public final static String addPlateError= "Add plate failed: ";
    public final static String tokenRefresh= "tokenRefresh";

    /* log in messages*/
    public final static String login_Incorrect_msg = "Incorrect Email or Password";
    public final static String login_error_msg = "Log in Error, please try again later";
    public final static String login_failed = "Log in Failed, ";
    public final static String sp_logged = "logged";
    /* signup messages*/
    public final static String signup_debug_msg = "signing you up";
    public final static String signup_Invalid_msg = "Invalid email or email already in use";
    public final static String signup_error_msg = "Sign up Error, please try again later. Error is: ";
    public final static String signup_success_msg = "Account created! Logging you in now!";
    public final static String sign_failed = "sign up Failed, ";
    public final static String firstName = "firstName";
    public final static String lastName = "lastName";
    public final static String email = "email";
    public final static String password = "password";
    public final static String token = "token";
    public final static String userId = "userId";
    public final static String id = "id";
    /* Car messages */
    public final static String plateNo = "plateNo";
    public final static String licensePlate = "licensePlate";
    public final static String carNickName = "carNickName";
    public final static String carName = "carName";
    public final static String startTime = "startTime";
    public final static String meterNo = "meterNo";
    public final static String duration = "duration";
    public final static String cost = "cost";
    public final static String date = "date";
    public final static String endTime = "endTime";
    /* Home page messages */
    public final static String welcome = "Welcome Back, ";
    public final static String exclamation = "!";
    /* Admin */
    public final static String Occupied = "Occupied";
    public final static String isOccupied = "isOccupied";
    public final static String Empty = "Empty";
    public final static String unitPrice = "unitPrice";
    public final static String updated = "updated";
    public final static String admin= "admin";
    public final static String isConfirmed= "isConfirmed";


    public static void closeKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void tokenExpired(){
        MainActivity.sp.edit().putBoolean(Constants.sp_logged, false).apply();
        MainActivity.sp.edit().remove(Constants.email).apply();
        MainActivity.sp.edit().remove(Constants.firstName).apply();
        MainActivity.sp.edit().remove(Constants.lastName).apply();
    }


}
