package com.example.ravaisi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import java.util.Collection;
import java.util.regex.Pattern;

public class AddProductsActivity extends AppCompatActivity {
    String IP = "";
    String ADD_PRODUCT = "http://" + IP + "/ravaisi/AddProduct.php";
    String GET_PRODUCTS = "http://" + IP + "/ravaisi/GetProducts.php";
    String GET_ITEMS = "http://" + IP + "/ravaisi/GetToppings.php";
    String GET_CATEGORIES = "http://"+ IP +"/ravaisi/GetCategories.php";

    String DELETE_PRODUCT = "http://" + IP + "/ravaisi/DeleteProduct.php";
    String product, price, category;

    String[] categories;

    String productTops;
    String selectedProduct;
    String[] rows;
    String[] products;
    String[] productCategories;

    MaterialButton deleteProductBtn;

    String[] toppings;

    LinearLayout existingProducts;
    CheckBox toppingsChBox;

    String PATTERN;
    String PRICE_PATTERN;
    ArrayList<CheckBox> topCheckBoxes = new ArrayList<CheckBox>();

    int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        SharedPreferences netPreferences = getSharedPreferences("net", 0);
        IP = netPreferences.getString("IP", "");
        if (IP.equals(""))
        {
            startActivity(new Intent(AddProductsActivity.this, SettingsActivity.class));
        }
        PATTERN = "[\\u0370-\\u03ff\\u1f00-\\u1fff\\ ]+";
        PRICE_PATTERN = "^[0-9\\.]+$";
        selectedProduct = "";
        EditText productTxBox = findViewById(R.id.productTxBox);
        EditText priceTxBox = findViewById(R.id.priceTxBox);
        EditText categoryTxBox = findViewById(R.id.categoryTxBox);
        MaterialButton addProductBtn = findViewById(R.id.addProduct);
        deleteProductBtn = findViewById(R.id.deleteProductBtn);
        existingProducts = findViewById(R.id.existingProducts);
        toppingsChBox = findViewById(R.id.toppingsChBox);

        ADD_PRODUCT = "http://" + IP + "/ravaisi/AddProduct.php";
        GET_PRODUCTS = "http://" + IP + "/ravaisi/GetProducts.php";
        GET_ITEMS = "http://" + IP + "/ravaisi/GetToppings.php";
        GET_CATEGORIES = "http://"+ IP +"/ravaisi/GetCategories.php";
        DELETE_PRODUCT = "http://" + IP + "/ravaisi/DeleteProduct.php";

        new getToppings().execute();
        new getProducts().execute();
        new getCategories().execute();
        Log.d("PaymentActivity", "Got thereeeeeeeeeee!!!!!!!!!!!!!!!");
        toppingsChBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LinearLayout linearLayout;
                    linearLayout = new LinearLayout(AddProductsActivity.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    Log.d("PaymentActivity", "Got thereeeeeeeeee!!!!!!!!!!!!!!!!!!!!");
                    for (int j = 0; j < topCheckBoxes.size(); j++) {
                        if (topCheckBoxes.get(j).getParent()!=null)
                        {
                            ((ViewGroup)topCheckBoxes.get(j).getParent()).removeView(topCheckBoxes.get(j));
                        }
                        linearLayout.addView(topCheckBoxes.get(j));
                    }
                    TextView textView = new TextView(AddProductsActivity.this);

                    linearLayout.addView(textView);
                    AlertDialog dialog = new AlertDialog.Builder(AddProductsActivity.this).create();
                    dialog.setView(linearLayout);
                    dialog.setTitle("Υλικα");
                    dialog.setButton(Dialog.BUTTON_POSITIVE, "Ετοιμο", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    CheckBox checkBox = (CheckBox) view;

                    if (checkBox.isChecked()) {
                        Log.d("PaymentActivity", "Showing dialog!");
                        dialog.show();
                    }
                } catch (Exception ex) {
                    Log.d("PaymentActivity", ex.getMessage());
                }
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean a = false;
                product = productTxBox.getText().toString();
                price = priceTxBox.getText().toString();
                category = categoryTxBox.getText().toString();
                productTops = "";
                if (product.isBlank() || price.isBlank() || category.isBlank())
                {
                    Toast.makeText(AddProductsActivity.this, "Ολα τα πεδια ειναι υποχρεωτικα!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Pattern.matches(PATTERN, product)||!Pattern.matches(PATTERN, category))
                {
                    Toast.makeText(AddProductsActivity.this, "Επιτρεπονται μονο ελληνικοι χαρακτηρες", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Pattern.matches(PRICE_PATTERN, price))
                {
                    Toast.makeText(AddProductsActivity.this, "Η τιμη πρεπει να ειναι στην μορφη \"1.00\"", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (String prod : products) {
                    if (prod.equals(product)) {
                        Toast.makeText(AddProductsActivity.this, "Το προιον υπαρχει ηδη!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (String categ:categories)
                {

                    if (categ.equals(category))
                    {
                        a = true;
                        break;
                    }
                }
                if (!a)
                {
                    Toast.makeText(AddProductsActivity.this, "Η κατηγορια δεν ειναι εγγεγραμμενη!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (CheckBox ch : topCheckBoxes) {
                    if (ch.isChecked()) {
                        productTops = productTops + ch.getText() + ",";
                    }
                }
                productTxBox.setText("");
                categoryTxBox.setText("");
                priceTxBox.setText("");
                new addProduct().execute();
                new getProducts().execute();
            }
        });
        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (selectedProduct.isEmpty()) {
                        Toast.makeText(AddProductsActivity.this, "Επιλεξτε ενα προιον για διαγραφη!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new deleteProduct().execute();
            }
        });

    }

    private class deleteProduct extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try
            {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(selectedProduct);
            Log.d("PaymentActivity", data);


                URL url = new URL(DELETE_PRODUCT);
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

                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddProductsActivity.this, response, Toast.LENGTH_SHORT).show();
                        new getProducts().execute();
                    }
                });

                return null;
            } catch (Exception ex) {
                Log.d("PaymentActivity", ex.getMessage());
                Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        }
    }

        private class addProduct extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(product)
                        + "&" + URLEncoder.encode("price") + "=" + URLEncoder.encode(price)
                        + "&" + URLEncoder.encode("category") + "=" + URLEncoder.encode(category)
                        + "&" + URLEncoder.encode(("toppings")) + "=" + URLEncoder.encode((productTops));
                Log.d("PaymentActivity", data);

                try {

                    URL url = new URL(ADD_PRODUCT);
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
                        Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddProductsActivity.this, "Product added successfuly!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;

                } catch (Exception ex) {
                    Log.d("PaymentActivity", ex.getMessage());
                    Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return null;
            }
        }

        private class getProducts extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    URL url = new URL(GET_PRODUCTS);
                    HttpURLConnection client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");
                    client.connect();
                    String message = client.getResponseMessage();
                    Log.d("PaymentActivity", "message: " + message);
                    if (!message.equals("OK"))
                    {
                        Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    InputStream inputStream = new BufferedInputStream(client.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int ch; (ch = reader.read()) != -1; )
                        stringBuilder.append((char) ch);
                    String response = stringBuilder.toString();
                    Log.d("PaymentActivity", "Server: " + response);
                    rows = response.split("_");
                    products = response.split("_");
                    productCategories = response.split(("_"));
                    if (response.equals(""))
                        return null;
                        for (int j = 0; j < products.length; j++) {
                            products[j] = products[j].split("/")[0];
                            productCategories[j] = productCategories[j].split("/")[2];
                        }

                    Log.d("PaymentActivity", "finished getProducts");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                existingProducts.removeAllViews();
                                for (String prod : products) {
                                    TextView textView;
                                    textView = new TextView(AddProductsActivity.this);
                                    textView.setText(prod);
                                    textView.setTextSize(25);
                                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            for (int j = 0; j < existingProducts.getChildCount(); j++) {
                                                TextView textView1;
                                                textView1 = (TextView) existingProducts.getChildAt(j);
                                                textView1.setBackgroundColor(Color.WHITE);
                                                textView1.setTextColor(Color.BLACK);
                                            }
                                            textView.setBackgroundColor(Color.GREEN);
                                            textView.setTextColor(Color.BLACK);
                                            TextView textView1 = (TextView) view;
                                            selectedProduct = textView1.getText().toString();

                                        }
                                    });

                                    existingProducts.addView(textView);
                                }

                            } catch (Exception ex) {
                                Log.d("PaymentActivity", ex.getMessage());
                            }
                        }

                    });

                    return null;
                } catch (Exception ex) {
                    Log.d("PaymentActivity", "exception: " + ex.getMessage());
                    Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return null;
            }
        }

        private class getToppings extends AsyncTask {
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
                        Toast.makeText(AddProductsActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
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
                    for (int j = 0; j < toppings.length; j++) {
                        CheckBox checkBox = new CheckBox(AddProductsActivity.this);
                        checkBox.setText(toppings[j]);
                        Log.d("PaymentActivity", "Adding: " + toppings[j]);
                        topCheckBoxes.add(checkBox);
                    }
                    return null;
                } catch (Exception ex) {
                    Log.d("PaymentActivity", "exception: " + ex.getMessage());
                }
                Log.d("PaymentActivity", "finished getToppings");
                return null;
            }
        }

        private class getCategories extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    URL url = new URL(GET_CATEGORIES);
                    HttpURLConnection client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");
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
                    categories = response.split("_");
                    Log.d("PaymentActivity", "finished getCategories");
                    return null;
                } catch (Exception ex) {
                    Log.d("PaymentActivity", "exception: " + ex.getMessage());
                }
                return null;
            }
        }
}