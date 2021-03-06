package com.cpen391.userapp.dashboardFragments.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.MainActivity;
import com.cpen391.userapp.R;

import java.util.HashMap;

public class AccountFragment extends Fragment {

    private View v;
    private Bundle bundle = new Bundle();

    /**
     * To create a new instance of the AccountFragment
     * @return A new instance of fragment AccountFragment.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        v = inflater.inflate(R.layout.fragment_account,container,false);

        /* Hide the Admin specific functions if this is not an Admin account */
        if(!MainActivity.sp.getBoolean(Constants.admin,false)){
            CardView meterStatus = v.findViewById(R.id.meterStatus);
            TextView adminText = v.findViewById(R.id.adminText);
            meterStatus.setVisibility(v.GONE);
            adminText.setVisibility(v.GONE);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController= Navigation.findNavController(v);

        /* Set the user information with the given arguments */
        TextView fullname = v.findViewById(R.id.fullname);
        TextView email = v.findViewById(R.id.email);
        email.setText(MainActivity.sp.getString(Constants.email, null));
        fullname.setText(MainActivity.sp.getString(Constants.firstName, null) + " " +MainActivity.sp.getString(Constants.lastName, null));

        /* navigation for the Back button in the header */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment_to_homeFragment);
            }
        });

        /* Navigation for the Logout Button */
        Button logoutBtn = v.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.tokenExpired();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        /* Navigation to the payment method page */
        CardView payment = v.findViewById(R.id.paymentBtn);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment_to_paymentFragment);
            }
        });

        /* Navigation to the personal info page */
        CardView personalInfo = v.findViewById(R.id.info);
        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment_to_personalInfoFragment);
            }
        });

        /* Navigation to the payment method page */
        CardView meterStatus = v.findViewById(R.id.meterStatus);
        meterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment_to_adminFragment);
            }
        });
    }
}