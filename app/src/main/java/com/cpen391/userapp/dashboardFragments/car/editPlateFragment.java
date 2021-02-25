package com.cpen391.userapp.dashboardFragments.car;

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
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.userapp.Constants;
import com.cpen391.userapp.R;


public class editPlateFragment extends Fragment {

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_edit_plate,container,false);
        EditText plateNo = v.findViewById(R.id.plateNo);
        plateNo.setText(getArguments().getString("plateNo"));
        EditText carNickName = v.findViewById(R.id.carNickName);
        carNickName.setText(getArguments().getString("carNickName"));
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController= Navigation.findNavController(v);

        ImageButton backBtn = v.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                navController.navigate(R.id.action_editPlateFragment_to_carFragment);

            }
        });

        Button saveBtn = v.findViewById(R.id.saveChanges);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.closeKeyboard(getActivity());
                Toast.makeText(getContext(), "changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}