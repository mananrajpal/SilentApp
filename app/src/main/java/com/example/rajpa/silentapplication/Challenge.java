package com.example.rajpa.silentapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Challenge extends AppCompatActivity {
    String question="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_challenge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initializeUI();

    }

    private void initializeUI()
    {
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setLogo(R.drawable.silent);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Silent Application");

        TextView takeChallenge = (TextView) findViewById(R.id.takeChallenge);
        takeChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Bundle-data","Inside the take challenge intent");
                Intent challenge = new Intent(getApplicationContext(), ChallengeFragments.class);
                challenge.putExtra("View","take-challenge");
                startActivity(challenge);
            }
        });

        TextView viewScore = (TextView)findViewById(R.id.viewScore);
        viewScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Bundle-data","Inside the score intent");
                Intent score = new Intent(getApplicationContext(), ChallengeFragments.class);
                score.putExtra("View","view-score");
                startActivity(score);
            }
        });
    }


}
