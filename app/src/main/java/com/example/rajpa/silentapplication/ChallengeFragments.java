package com.example.rajpa.silentapplication;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class ChallengeFragments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_fragments);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        initializeUI(savedInstanceState);
    }

    protected void initializeUI(Bundle state)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.drawable.silent);
        actionBar.setTitle("Silent Application");

        //
        Bundle bundle = getIntent().getExtras();
        String viewType = bundle.getString("View");
        Log.d("Store-question",viewType);

        if(viewType.equals("take-challenge"))
        {
            TakeChallenge takeChallenge = new TakeChallenge();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentView, takeChallenge);
            fragmentTransaction.commit();
        }
        if(viewType.equals("view-score"));
        {

        }
    }


}
