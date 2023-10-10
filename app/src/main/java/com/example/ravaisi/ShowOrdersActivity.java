package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

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
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ShowOrdersActivity extends AppCompatActivity {

    String IP = "";
    String GET_ORDER_STRINGS = "http://" + IP + "/ravaisi/GetOrderStrings.php";
    String orderStrings[];
    String table;

    TextView orderHeader, orderText;

    SharedPreferences netPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_orders);
            orderHeader = findViewById(R.id.orderHeader);
            orderText = findViewById(R.id.orderText);
            orderText.setMovementMethod(new ScrollingMovementMethod());
            orderText.setTypeface(Typeface.MONOSPACE);
            table = getIntent().getStringExtra("TABLE");
            SharedPreferences netPreferences = getSharedPreferences("net", 0);
            IP = netPreferences.getString("IP", "");
            GET_ORDER_STRINGS = "http://" + IP + "/ravaisi/GetOrderStrings.php";
            if (IP.equals("")) {
                startActivity(new Intent(ShowOrdersActivity.this, SettingsActivity.class));
            }
        } catch (Exception ex) {
            Log.d("PaymentActivity", ex.getMessage());
        }
        new loadOrder().execute();
    }

    private String makeProductString(String quantity, String name, String price, String toppings, String comments) {
        String pdStr = "";
        if (toppings.equals("")) {
            pdStr += quantity + " " + name;
            for (int j = 0; j <= (20 - name.length() - quantity.length()); j++) {
                pdStr += "-";
            }
            pdStr += price + "0€" + "\n" + "Σχολια: " + comments;

        } else {
            pdStr += quantity + " " + name;
            for (int j = 0; j <= (21 - name.length()); j++) {
                pdStr += "-";
            }
            pdStr += price + "0€" + "\n" +toppings + "\n" + "Σχολια: " + comments;

        }
        return pdStr;
    }

    private String showOrder() {

        String orderText = "";
        String[] products;
        for (String orderString : orderStrings) {
            String productString = orderString.split("\\|")[1];
            products = productString.split("\\}");
            for (String product : products) {
                if (product.equals("&amp;"))
                    continue;
                product = product.split("\\{")[1].replace("&amp;", "+").replace("&lt;", "<").replace("&gt;", ">");
                Log.d("PaymentActivity", product);
                        Log.d("PaymentActivity", product.split("\\[")[1]);
                        if (product.split("\\[")[1].equals("]"))
                        {
                        if (product.split("<")[1].equals(">"))
                        orderText += makeProductString(product.split("\\+")[3].split(":")[1], product.split("\\+")[0].split(":")[1], product.split("\\+")[1].split(":")[1], "", "") + "\n";
                        else
                        {
                            orderText += makeProductString(product.split("\\+")[3].split(":")[1], product.split("\\+")[0].split(":")[1], product.split("\\+")[1].split(":")[1], "", product.split("<")[1].split(">")[0]) + "\n";
                        }
                        Log.d("PaymentActivity", makeProductString(product.split("\\+")[3].split(":")[1], product.split("\\+")[0].split(":")[1], product.split("\\+")[1].split(":")[1], "", ""));
                    }
                else {
                    Log.d("PaymentActivity", "with toppings");
                    for (String top : product.split("\\[")[1].split("]")[0].split("_")) {
                        Log.d("PaymentActivity", top);
                        if (top.split("<")[1].equals(">"))
                        orderText += makeProductString(top.split("\\(")[1].split("\\)")[0], product.split("\\+")[0].split(":")[1], product.split("\\+")[1].split(":")[1], top.split("<")[0],"") + "\n";
                        else
                            orderText += makeProductString(top.split("\\(")[1].split("\\)")[0],product.split("\\+")[0].split(":")[1], product.split("\\+")[1].split(":")[1], top.split("<")[0],top.split("<")[1].split(">")[0]) + "\n";
                    }
                }

            }
        }
        return orderText;
}
    private class loadOrder extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                     String data = URLEncoder.encode("order_table") + "=" + URLEncoder.encode(table);
                     Log.d("PaymentActivity", data);
                     URL url = new URL(GET_ORDER_STRINGS);
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
                     Log.d("PaymentActivity", "message: " + message);
                     InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                     StringBuilder stringBuilder = new StringBuilder();
                     for (int ch; (ch = reader.read()) != -1; )
                        stringBuilder.append((char) ch);
                     String response = stringBuilder.toString();
                     Log.d("PaymentActivity", "Server: " + response);
                     orderStrings = response.split("%");
                     for (String order:orderStrings)
                     {
                         Log.d("PaymentActivity", order);
                     }
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             try {
                                 orderHeader.setText("Τραπεζι " + table);
                                 orderText.setText(showOrder());
                             }
                             catch(Exception ex)
                             {
                                 Log.d("PaymentActivity", ex.getMessage());
                             }
                         }
                     });
                     return null;
                        }
                     catch (Exception ex) {
                    Log.d("PaymentActivity", "exception: " + ex.getMessage());
                    }
                    return null;
            }
        }
    }
