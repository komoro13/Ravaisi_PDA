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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddCategoryActivity extends AppCompatActivity {

    //Global variables
    String IP = "";
    String ADD_CATEGORY = "http://" + IP +"/ravaisi/AddCategory.php";
    String GET_CATEGORIES = "http://"+ IP +"/ravaisi/GetCategories.php";

    String DELETE_CATEGORY = "http://" + IP + "/ravaisi/DeleteCategory.php";

    LinearLayout existingCategories;

    MaterialButton deleteBtn;
    TextView[] exCategories;

    ArrayList<TextView>  categoriesList = new ArrayList<TextView>();

    String selectedCategory;
    String category;
    String PATTERN;

    String[] categories;

    int x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        exCategories = new TextView[1000];
        super.onCreate(savedInstanceState);
        SharedPreferences netPreferences = getSharedPreferences("net", 0);
        IP = netPreferences.getString("IP", "");
        if (IP.equals(""))  //if ip is null string go to settings to set it
        {
            startActivity(new Intent(AddCategoryActivity.this, SettingsActivity.class));
        }
        PATTERN = "[\\u0370-\\u03ff\\u1f00-\\u1fff\\ ]+"; //category has to be greek letters
        setContentView(R.layout.activity_add_category);
        selectedCategory = ""; //selected catefory is initially null
        deleteBtn = findViewById(R.id.deleteBtn);

        ADD_CATEGORY = "http://" + IP +"/ravaisi/AddCategory.php";
        GET_CATEGORIES =  "http://"+ IP +  "/ravaisi/GetCategories.php";
        DELETE_CATEGORY = "http://" + IP + "/ravaisi/DeleteCategory.php";

        EditText itemTxBox = findViewById(R.id.itemTxBox);
        MaterialButton addBtn  = findViewById(R.id.addBtn);
        existingCategories = findViewById(R.id.existingCategories);
        Log.d("PaymentActivity", "Got there!!!!!");
        new getCategories().execute();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                category = itemTxBox.getText().toString();
                for(String cat:categories)
                {
                    if (category.isBlank())
                    {
                        Toast.makeText(AddCategoryActivity.this, "Η κατηγορια δεν μπορει να ειναι κενη",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Pattern.matches(PATTERN, category))
                    {
                        Toast.makeText(AddCategoryActivity.this, "Επιτρεπονται μονο ελληνικα γραμματα", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (category.equals(cat))
                    {
                        Toast.makeText(AddCategoryActivity.this, "Η κατηγορια υπαρχει ηδη!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                   itemTxBox.setText("");
                   new addCategory().execute();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategory.isEmpty())
                {
                    Toast.makeText(AddCategoryActivity.this, "Επιλεξτε μια κατηγοτια για διαγραφη!", Toast.LENGTH_SHORT).show();
                    return;
                }
                 try
                {
                    new deleteCategory().execute();
                }
                catch (Exception ex)
                {
                    Log.d("PaymentActivity", ex.getMessage());
                }
            }
        });
    }



    private class addCategory extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(category);
            Log.d("PaymentActivity", data);
            try {

                URL url = new URL(ADD_CATEGORY);
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
                    Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                        new getCategories().execute();
                    }
                });

                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
                Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }


    private class deleteCategory extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(selectedCategory);
            Log.d("PaymentActivity", data);
            try {

                URL url = new URL(DELETE_CATEGORY);
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
                    Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = URLDecoder.decode(stringBuilder.toString(), "utf-8");
                Log.d("PaymentActivity", "Server: " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddCategoryActivity.this, response, Toast.LENGTH_SHORT).show();
                        new getCategories().execute();
                    }
                });

                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
                Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }
    private class getCategories extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(GET_CATEGORIES);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                if (!message.equals("OK"))
                {
                    Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                Log.d("PaymentActivity", "Encoding: " + client.getContentEncoding());
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                categories = response.split("_");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        existingCategories.removeAllViews();
                        for (String category: categories)
                        {
                            TextView textView = new TextView(AddCategoryActivity.this);
                            textView.setText(category);
                            textView.setTextSize(25);
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                selectedCategory = textView.getText().toString();
                                for (int j = 0; j<existingCategories.getChildCount();j++)
                                {
                                    TextView textView1 = new TextView(AddCategoryActivity.this);
                                    textView1 = (TextView) existingCategories.getChildAt(j);
                                    textView1.setBackgroundColor(Color.WHITE);
                                    textView1.setTextColor(Color.BLACK);
                                }
                                textView.setBackgroundColor(Color.GREEN);
                                textView.setTextColor(Color.BLACK);
                                }
                            });
                            existingCategories.addView(textView);
                        }
                    }
                });
                return null;
            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", "exception: " + ex.getMessage());
                Toast.makeText(AddCategoryActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }
}