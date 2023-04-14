package com.example.financeko;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationFragment extends Fragment {
    //Initialize fields
    private EditText username, password, confirmPassword, firstName, lastName, email;
    private Button registerBtn, backBtn;
    DBHelper DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        backBtn = view.findViewById(R.id.backBtn);

        DB = new DBHelper(getActivity());
        registerBtn = view.findViewById(R.id.registerBtn);
        username = view.findViewById(R.id.newusername_edittext);
        password = view.findViewById(R.id.newpassword_edittext);
        confirmPassword = view.findViewById(R.id.confirmpassword_edittext);
        firstName = view.findViewById(R.id.NewFName_edittext);
        lastName = view.findViewById(R.id.NewLName_edittext);
        email = view.findViewById(R.id.Newemail_edittext);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get inputs from fields
                String usernameCheck = username.getText().toString().trim();
                String passwordCheck = password.getText().toString().trim();
                String confirmPasswordCheck = confirmPassword.getText().toString().trim();
                String firstNameCheck = firstName.getText().toString().trim();
                String lastNameCheck = lastName.getText().toString().trim();
                String emailCheck = email.getText().toString().trim();

                //If fields are empty
                if(usernameCheck.equals("")||
                    passwordCheck.equals("")||
                    confirmPasswordCheck.equals("")||
                    firstNameCheck.equals("")||
                    lastNameCheck.equals("")||
                    emailCheck.equals(""))

                    Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                else if(!passwordCheck.equals(confirmPasswordCheck)){
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkUser = DB.checkUsername(usernameCheck);
                    if(!checkUser){
                        Boolean checkEmail = DB.checkEmail(emailCheck);
                        if(!checkEmail){
                            Boolean insert = DB.insertDataUser(usernameCheck,passwordCheck, firstNameCheck, lastNameCheck, emailCheck);
                            if(insert){
                                Toast.makeText(getActivity(), "Registered successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getActivity(), "Registered failed", Toast.LENGTH_SHORT).show();
                        }
                        else if (checkEmail)
                            Toast.makeText(getActivity(), "Email already taken", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), "Username already taken", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish(); // finish current activity to prevent the user from returning to it using the back button
            }
        });


        return view;
    }
}