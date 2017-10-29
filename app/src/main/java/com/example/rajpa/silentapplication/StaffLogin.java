package com.example.rajpa.silentapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StaffLogin extends Fragment {

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

        /*-------------------getting the associated views from xml file---------------------------*/
        TextView nameEntered = (TextView) v.findViewById(R.id.userName);
        TextView passEntered = (TextView) v.findViewById(R.id.userPass);
        Button checkCredentials = (Button) v.findViewById(R.id.submitLogin);
        /*----------------------------------------------------------------------------------------*/

        /*--------------------Converting the input to string---------------------------------------*/
        String userName = nameEntered.getText().toString();
        String userPass = nameEntered.getText().toString();
        String type = "login"; //Operation type is login
        /*-----------------------------------------------------------------------------------------*/
        BackgroundWorker br = new BackgroundWorker(v);
        br.execute(type, userName,userPass);
        return v;
    }

    class BackgroundWorker extends AsyncTask<String, Void, Void>
    {
        View v;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... params) {
            //getting the operation type
            String type = params[0];
            if(type=="login")
            {

            }

            return null;
        }

        public BackgroundWorker(View context)
        {
            v = context;
        }

    }









}
