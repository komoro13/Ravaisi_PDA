package com.example.ravaisi;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String IP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MaterialButton orderBtn = findViewById(R.id.orderBtn);

        MaterialButton ordersBtn = findViewById(R.id.ordersBtn);
        MaterialButton setupBtn = findViewById(R.id.setupBtn);
        SharedPreferences netPrefs = getSharedPreferences("net", 0);
        IP = netPrefs.getString("IP", "");
        if (IP.equals(""))
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        new checkConnection().execute();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
            }
        });


        ordersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(MainActivity.this, OpenOrdersActivity.class));
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        startActivity(new Intent(MainActivity.this, SetupActivity.class));
                        Log.d("PaymentActivity", "Got there!!!");
            }
        });

        
    }

    private class checkConnection extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL("http://"+IP+"/");
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                if (!message.equals("OK")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                    });
                }
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity","MainActivity error: " + ex.getMessage());
            }
            return null;}}
}