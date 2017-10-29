package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class StaffLogin extends Fragment {
    Boolean validated = false;
    loginListener mListener;
    public interface loginListener
    {
        public void loginStaff(String name, String pass);
    }
    public StaffLogin() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try
        {
            mListener = (loginListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement ToolbarListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_staff_login, container, false);
        Button checkCredentials = (Button) v.findViewById(R.id.submitLogin);


        /*----------------------Starting the threading of login with onClickListener--------------*/
        checkCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*-------------------getting the associated views from xml file-------------------*/
                EditText nameEntered = (EditText) getActivity().findViewById(R.id.userName);
                EditText passEntered = (EditText) getActivity().findViewById(R.id.userPass);
                /*----------------Converting the input to string------------------------------*/
                String userName = nameEntered.getText().toString();
                String userPass = passEntered.getText().toString();
                String type = "login"; //Operation type is login
                /*--------------------------------------------------------------------------------*/
                /*---------------Validating data using TextUtils Library--------------------------*/
                if(TextUtils.isEmpty(userName))
                {
                    nameEntered.setError("Enter name");
                    return;
                }
                if(TextUtils.isEmpty(userPass))
                {
                    passEntered.setError("Enter password");
                    return;
                }
                else
                {
                    //performing the async threading of getting records from database
                    BackgroundWorker br = new BackgroundWorker(v);
                    br.execute(type, userName,userPass);
                }

            }
        });



        return v;
    }

}
