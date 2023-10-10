package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OpenOrdersActivity extends AppCompatActivity {

    LinearLayout openOrders;
    MaterialButton refreshBtn;

    String[] tables;
    ArrayList<TextView> orders = new ArrayList<TextView>();

    private String IP = "";
    private String GET_ORDERS = "http://" + IP + "/ravaisi/GetOrders.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_orders);
            SharedPreferences netPreferences = getSharedPreferences("net", 0);
            IP = netPreferences.getString("IP", "");
            if (IP.equals(""))
            {
                startActivity(new Intent(OpenOrdersActivity.this, SettingsActivity.class));
            }
                openOrders = findViewById(R.id.openOrders);
                refreshBtn = findViewById(R.id.refreshBtn);
                GET_ORDERS = "http://" + IP + "/ravaisi/GetOrders.php";
                new getOrders().execute();
                refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new getOrders().execute();
                    }
                });

        }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
            }
    }


    private class getOrders extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(GET_ORDERS);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                if (!message.equals("OK"))
                {
                    Toast.makeText(OpenOrdersActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                tables = response.split("_");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                openOrders.removeAllViews();
                                for (String row : tables) {
                                    TextView textView = new TextView(OpenOrdersActivity.this);
                                    textView.setText(row);
                                    textView.setTextSize(25);
                                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(OpenOrdersActivity.this, OrderActivity.class);
                                                    intent.putExtra("TYPE", "OPEN");
                                                    intent.putExtra("TABLE", ((TextView)view).getText().toString());
                                                    startActivity(intent);
                                                }
                                            });

                                        }
                                    });
                                    textView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {
                                            try {
                                                {

                                                    Intent intent = new Intent(OpenOrdersActivity.this, ShowOrdersActivity.class);
                                                    intent.putExtra("TABLE", ((TextView) view).getText().toString());
                                                    startActivity(intent);
                                                }
                                            } catch (Exception ex) {
                                                Log.d("PaymentActivity", ex.getMessage());
                                                Toast.makeText(OpenOrdersActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            return  true;
                                        }

                                    });
                                    openOrders.addView(textView);
                                }
                            }
                            catch (Exception ex)
                            {
                                Log.d("PaymentActivity", ex.getMessage());
                            }
                        }
                    });


                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", "exception: " + ex.getMessage());
            }
            return null;
        }
    }

}