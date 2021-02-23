package com.cpen391.userapp.dashboardFragments.history;

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
import com.cpen391.userapp.R;

/**
 * Fragment for showing details of a past parking instance
 */
public class ReceiptFragment extends Fragment {

    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_receipt,container,false);

        /* connect to the UI components */
        TextView plateNo = v.findViewById(R.id.plateNo);
        TextView meterNo = v.findViewById(R.id.meterNo);
        TextView cost = v.findViewById(R.id.cost);
        TextView date = v.findViewById(R.id.date);
        TextView duration = v.findViewById(R.id.duration);
        TextView startTime = v.findViewById(R.id.startTime);

        /* Set up value based on arguments passed in */
        plateNo.setText(getArguments().getString(Constants.plateNo));
        meterNo.setText(getArguments().getString(Constants.meterNo));
        cost.setText(getArguments().getString(Constants.cost));
        date.setText(getArguments().getString(Constants.date));
        duration.setText(getArguments().getString(Constants.duration));
        startTime.setText(getArguments().getString(Constants.startTime));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController= Navigation.findNavController(v);

        /* Navigation back to the history page */
        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_receiptFragment_to_historyFragment);
            }
        });
    }
}