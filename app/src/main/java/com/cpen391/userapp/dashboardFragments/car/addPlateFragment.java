package com.cpen391.userapp.dashboardFragments.car;

import android.app.Activity;
import android.content.Context;
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
import com.cpen391.userapp.R;


public class addPlateFragment extends Fragment {

    private View v;

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
        final NavController navController= Navigation.findNavController(v);


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

        EditText plateNo = v.findViewById(R.id.plateNo);
        EditText carNickName = v.findViewById(R.id.carNickName);
        Button addBtn = v.findViewById(R.id.addbtn);

        /* Submission of the new car information */
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                Toast.makeText(getContext(), Constants.licensePlate+ "= " +plateNo.getText()+ ", " + Constants.carNickName + "= " +carNickName.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}