package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddToppingsActivity extends AppCompatActivity {


    String IP = "";
    String ADD_ITEM = "http://"+IP+"/ravaisi/AddTopping.php";
    String GET_ITEMS = "http://"+IP+"/ravaisi/GetToppings.php";
    String DELETE_TOPPING = "http://" + IP + "/ravaisi/DeleteTopping.php";
    EditText nameEditText;
    EditText priceEditText;
    String name, price;
    LinearLayout existingToppings;

    String selectedTopping;
    MaterialButton addToppingBtn;
    MaterialButton deleteToppingBtn;
    int x;
    String toppings[];
    String PATTERN, PRICE_PATTERN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences netPreferences = getSharedPreferences("net", 0);
        IP = netPreferences.getString("IP", "");
        if (IP.equals(""))
        {
            startActivity(new Intent(AddToppingsActivity.this, SettingsActivity.class));
        }
        setContentView(R.layout.activity_add_toppings);
        PATTERN = "[\\u0370-\\u03ff\\u1f00-\\u1fff\\ ]+";
        PRICE_PATTERN = "^[0-9\\.]+$";
        try {
            selectedTopping = "";
            addToppingBtn = findViewById(R.id.addToppingBtn);
            deleteToppingBtn = findViewById(R.id.deleteToppingBtn);
            existingToppings = findViewById(R.id.existingToppings);
            nameEditText = findViewById(R.id.toppingName);
            priceEditText = findViewById(R.id.extraCost);

             ADD_ITEM = "http://"+IP+"/ravaisi/AddTopping.php";
             GET_ITEMS = "http://"+IP+"/ravaisi/GetToppings.php";
             DELETE_TOPPING = "http://" + IP + "/ravaisi/DeleteTopping.php";

            new getToppings().execute();


            addToppingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = nameEditText.getText().toString();
                    price = priceEditText.getText().toString();
                    if (name.isBlank()) {
                        Toast.makeText(AddToppingsActivity.this, "Το ονομα δεν μπορει να ειναι κενο!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Pattern.matches(PATTERN, name))
                    {
                        Toast.makeText(AddToppingsActivity.this, "Επιτρεπονται μονο ελληνικοι χαρακτηρες", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (String topping : toppings) {
                        if (topping.equals(name)) {
                            Toast.makeText(AddToppingsActivity.this, "Αυτο το υλικο υπαρχει ηδη!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    nameEditText.setText("");
                    priceEditText.setText("");
                    new addTopping().execute();
                    new getToppings().execute();
                }
            });
            deleteToppingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedTopping.isEmpty())
                    {
                        Toast.makeText(AddToppingsActivity.this, "Επιλεξτε ενα υλικο για διαγραφη!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new deleteTopping().execute();
                }
            });
        }
        catch (Exception ex)
        {
            Log.d("PaymentActivity", ex.getMessage());
        }
    }


    private class addTopping extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(name)
                    + "&" + URLEncoder.encode("price") + "=" + URLEncoder.encode(price);
            Log.d("PaymentActivity", data);
            try {

                URL url = new URL(ADD_ITEM);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoOutput(true);
                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputPost, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                outputPost.close();
                client.connect();

                String message = client.getResponseMessage();
                Log.d("PaymentActivity", message);
                if (!message.equals("OK"))
                {
                    Toast.makeText(AddToppingsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = inputStream.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddToppingsActivity.this, "Item added successfuly!", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;

            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
            }
            return null;
        }
    }
    private class deleteTopping extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(selectedTopping);
            Log.d("PaymentActivity", data);
            try {

                URL url = new URL(DELETE_TOPPING);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoOutput(true);
                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputPost, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                outputPost.close();
                client.connect();

                String message = client.getResponseMessage();
                Log.d("PaymentActivity", message);
                if (!message.equals("OK"))
                {
                    Toast.makeText(AddToppingsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddToppingsActivity.this, response, Toast.LENGTH_SHORT).show();
                        new getToppings().execute();
                    }
                });

                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
                Toast.makeText(AddToppingsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }
    private class getToppings extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(GET_ITEMS);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                if (!message.equals("OK"))
                {
                    Toast.makeText(AddToppingsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                toppings = response.split("_");
                for (int j=0;j<toppings.length;j++)
                {
                    toppings[j] = toppings[j].split("/")[0];
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        existingToppings.removeAllViews();
                        for (String topping:toppings)
                        {
                            TextView textView;
                            textView = new TextView(AddToppingsActivity.this);
                            textView.setText(topping);
                            textView.setTextSize(25);
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    TextView textView1;
                                    textView1 = new TextView(AddToppingsActivity.this);
                                    textView1 = (TextView)view;
                                    for (int j = 0; j < existingToppings.getChildCount();j++)
                                    {
                                        ((TextView)existingToppings.getChildAt(j)).setBackgroundColor(Color.WHITE);
                                        ((TextView)existingToppings.getChildAt(j)).setTextColor(Color.BLACK);
                                    }
                                    textView1.setBackgroundColor(Color.GREEN);
                                    textView1.setTextColor(Color.BLACK);
                                    selectedTopping = textView1.getText().toString();
                                }
                            });
                            existingToppings.addView(textView);
                        }
                    }
                });
                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", "exception: " + ex.getMessage());
                Toast.makeText(AddToppingsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }


}