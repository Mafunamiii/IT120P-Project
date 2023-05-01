package com.example.financeko;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class HomePageFragment extends Fragment {

    // Declare the logout button
    private Button logoutBtn, addTransactionBtn, transactionHistoryBtn, viewSummaryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        //Instantiate buttons
        logoutBtn = view.findViewById(R.id.logout_button);
        addTransactionBtn = view.findViewById((R.id.AddT_button));
        transactionHistoryBtn = view.findViewById((R.id.TransactH_button));
        viewSummaryBtn = view.findViewById((R.id.ViewS_button));

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear stored credentials here
                // ...

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish(); // finish current activity to prevent the user from returning to it using the back button
            }
        });

        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTransaction.class);
                Log.d("myTag",  "The activity is: "+ intent.getComponent().getClassName());
                startActivity(intent);
                getActivity().finish();
            }
        });

        transactionHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransactionHistory.class);
                Log.d("myTag",  "The activity is: "+ intent.getComponent().getClassName());
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Summary.class);
                Log.d("myTag",  "The activity is: "+ intent.getComponent().getClassName());
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


}
