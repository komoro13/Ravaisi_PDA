package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonyfdev.dropdownview.DropDownView;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class OrderActivity extends AppCompatActivity {


    //Global variables
    String IP = ""; //ip of the user

    //url to php files
    String GET_CATEGORIES = "http://"+ IP +"/ravaisi/GetCategories.php";
     String GET_PRODUCTS = "http://"+ IP +"/ravaisi/GetProducts.php";

     String GET_ORDER = "http://" + IP + "/ravaisi/GetOrder.php";
     String GET_ORDERS = "http://" + IP + "/ravaisi/GetOrders.php";
    String GET_TOPPINGS = "http://" + IP + "/ravaisi/GetProductToppings.php";



    ListView existingCategories;

    LinearLayout UI;

    ArrayAdapter arrayAdapter;


    String[] rows;

    String[] categories;
    boolean[] itemsChecked;
    String[] items;
    String[] itemPrices;

    MaterialButton sendOrderBtn;
    Order order1;

    String selectedProduct;
    String selectedItem;
    ArrayList<TextView> productsTextViews = new ArrayList<TextView>();
    ArrayList<TextView> orderTextViews = new ArrayList<TextView>();
    ArrayList<Product>  products= new ArrayList<>();
    ArrayList arrayList;

    String toppings;

    String table;

    String TYPE;

    String orderString;
    EditText tableTxBox;
    ArrayList<CheckBox> chBoxes = new ArrayList<CheckBox>();
    ArrayList<Product> order = new ArrayList<Product>();

    LinearLayout orderList;
    LinearLayout orderLayout;

    LinearLayout innerLayout, layout1;
    TextView title;
    TextView orderTitle;
    String[] itemList;
    String[] tables;
    boolean[] itemListChecked;
    int x;
    int y;
    int orderProductClicked;
    String PATTERN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        SharedPreferences netPreferences = getSharedPreferences("net", 0);
        IP = netPreferences.getString("IP", "");
        if (IP.equals(""))
        {
            startActivity(new Intent(OrderActivity.this, SettingsActivity.class));
        }
        PATTERN = "[\\u0370-\\u03ff\\u1f00-\\u1fff0-9]+$";
            TYPE = "NEW";
            orderTitle = findViewById(R.id.order);
            tableTxBox = findViewById(R.id.tableTxBox);
            sendOrderBtn = findViewById(R.id.sendOrderBtn);
            items = new String[]{"Σως", "Τζατζικι", "Πατατες", "Ντοματα"};
            itemsChecked = new boolean[items.length];
            orderList = findViewById(R.id.orderList);


            GET_CATEGORIES = "http://"+ IP +"/ravaisi/GetCategories.php";
            GET_PRODUCTS = "http://"+ IP +"/ravaisi/GetProducts.php";
            GET_ORDER = "http://" + IP + "/ravaisi/GetOrder.php";
            GET_ORDERS = "http://" + IP + "/ravaisi/GetOrders.php";
            GET_TOPPINGS = "http://" + IP + "/ravaisi/GetProductToppings.php";

            new loadProducts().execute();
            new getOrders().execute();
            orderLayout = findViewById(R.id.orderLayout);
            toppings = "";

            if (getIntent().getStringExtra("TYPE") != null && getIntent().getStringExtra("TYPE").equals("OPEN")) {
                table = getIntent().getStringExtra("TABLE");
                TYPE = "OPEN";
                orderTitle.setText("Τραπεζι: " + table);
                tableTxBox.setVisibility(View.INVISIBLE);
                new getOrder().execute();
            }

        //new getCategories().execute();
        sendOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TYPE.equals("OPEN"))
                {
                    for (Product product:order)
                    {
                        order1 = new Order(table, IP);
                        order1.addProduct(product);
                        order1.send();
                        orderLayout.removeAllViews();
                        order = new ArrayList<>();
                        orderTextViews = new ArrayList<>();
                        for (int j = 0; j < orderList.getChildCount();j++) {

                            TextView txView;
                            txView = (TextView) orderList.getChildAt(j);
                            if (txView.getCurrentTextColor()==-11975345)
                                txView.setBackgroundColor(Color.WHITE);
                        }
                        return;

                    }
                }
                if (tableTxBox.getText().toString().isBlank())
                {
                    Toast.makeText(OrderActivity.this, "Παρακαλω εισαγετε τραπεζι", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Pattern.matches(PATTERN, tableTxBox.getText().toString()))
                {
                    Toast.makeText(OrderActivity.this, "Το τραπεζι επιτρεπεται να αποτελειαι μονο απο ελληνικους χαρακτηρες και αριθμους", Toast.LENGTH_SHORT).show();
                    return;
                }
                order1 = new Order(tableTxBox.getText().toString(), IP);
                try {
                    if (order.size()==0)
                    {
                        Toast.makeText(OrderActivity.this, "Παρακαλω προσθεστε προιοντα στην παραγγελια!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tableTxBox.getText().toString().isBlank())
                    {
                        Toast.makeText(OrderActivity.this, "Εισαγετε τραπεζι!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Product product:order)
                    {
                        for (Item item:product.items)
                        {
                            Log.d("PaymentActivity", item.name + ": " + item.quantity);
                        }
                        order1.addProduct(product);
                    }
                    for (String tbl:tables)
                    {
                        Log.d("PaymentActivity", tbl);
                        if (tbl.equals(tableTxBox.getText().toString()))
                        {
                            table = tableTxBox.getText().toString();
                            showDialog(3);
                            return;
                        }
                    }
                    Log.d("PaymentActivity", "Order string --------------------- " + order1.createOrderString());
                    table = tableTxBox.getText().toString();
                    order1.table = table;
                    order1.send();
                    orderLayout.removeAllViews();
                    order = new ArrayList<>();
                    orderTextViews = new ArrayList<>();
                    for (int j = 0; j < orderList.getChildCount();j++) {

                        TextView txView;
                        txView = (TextView) orderList.getChildAt(j);
                        if (txView.getCurrentTextColor()==-11975345)
                            txView.setBackgroundColor(Color.WHITE);
                    }
                }
                catch (Exception ex)
                {
                    Log.d("PaymentActivity", "sendOrder error: " + ex.getMessage());
                }
            }
        });
    }




    @Override
    protected Dialog onCreateDialog(int id)
    {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView text = new TextView(this);
        text.setText("Σχολια");
        text.setTextSize(20);

        EditText editText = new EditText(this);
        EditText quantityEditText = new EditText(OrderActivity.this);
        editText.setHint("Σχολια");
        quantityEditText.setHint("Ποσοτητα");
        LinearLayout innerLayout1;
        innerLayout1 = new LinearLayout(OrderActivity.this);
        innerLayout1.setOrientation(LinearLayout.VERTICAL);
        ArrayList<String> productItems = new ArrayList<String>();


        EditText editText1;
        editText1 = new EditText(OrderActivity.this);
        editText1.setHint("Σχολια");
        LinearLayout layout2 = new LinearLayout(this);
        layout2.setOrientation(LinearLayout.VERTICAL);
        EditText qEditText = new EditText(OrderActivity.this);
        qEditText.setHint("Ποσοτητα");
        layout2.addView(editText1);
        layout2.addView(qEditText);
        switch (id) {
            case 0:

                for(String item:items)
                {
                    CheckBox checkBox;
                    checkBox = new CheckBox(OrderActivity.this);
                    checkBox.setText(item);
                    innerLayout1.addView(checkBox);
                }
                layout.addView(innerLayout1);
                layout.addView(editText);
                layout.addView(quantityEditText);
                Log.d("PaymentActivity", "selectedProduct ===>" + selectedProduct);
                return new AlertDialog.Builder(this)
                        .setTitle("Υλικα")
                        .setPositiveButton("Ετοιμο", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    if (!Pattern.matches("^[0-9]+$|^$", quantityEditText.getText().toString())||Pattern.matches("^0.*", qEditText.getText().toString()))
                                    {
                                        Toast.makeText(OrderActivity.this, "Η ποσοτητα μπορει να αποτελειτε μονο απο αριθμους!", Toast.LENGTH_SHORT).show();
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity - 1;
                                        if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0)
                                        {
                                            deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                            deleteProductFromOrder(getProductIndexByName(selectedProduct));
                                            ((TextView)orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                        }
                                        return;
                                    }

                                    toppings = "";
                                    Log.d("PaymentActivity", "selectedProduct: " + selectedProduct);
                                    Log.d("PaymentActivity", "index: " + String.valueOf(getOrderIndexByName(selectedProduct)));

                                    for (int j = 0; j < innerLayout1.getChildCount(); j++) {
                                        CheckBox checkBox = (CheckBox) innerLayout1.getChildAt(j);
                                        if (checkBox.isChecked()) {
                                            toppings = toppings + checkBox.getText().toString() + ",";
                                        }

                                    }
                                    if (toppings.equals(""))
                                    {
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity - 1;
                                        Toast.makeText(OrderActivity.this, "Παρακαλω επιλεξτε υλικα!", Toast.LENGTH_SHORT).show();
                                        if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0) {
                                            deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                            deleteProductFromOrder(getProductIndexByName(selectedProduct));
                                            ((TextView) orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                        }
                                        for (int j = 0; j < innerLayout1.getChildCount(); j++) {
                                            CheckBox checkBox = (CheckBox) innerLayout1.getChildAt(j);
                                            if (checkBox.isChecked()) {
                                                checkBox.setChecked(false);
                                            }

                                        }
                                        return;
                                    }
                                    toppings = toppings.substring(0, toppings.length() - 1);
                                    Log.d("PaymentActivity", "toppings: " + toppings);

                                    if (order.get(getOrderIndexByName(selectedProduct)).getItemByToppings(toppings) == null) {
                                        Log.d("PaymentActivity", "orderActivity getitemExtra: " + getItemExtra(toppings) + "getProductPrice: " + getProductPrice(selectedProduct));
                                        if (quantityEditText.getText().toString().equals(""))
                                        {
                                            order.get(getOrderIndexByName(selectedProduct)).addItem(toppings, editText.getText().toString(), getItemExtra(toppings), getProductPrice(selectedProduct), 1);
                                        }
                                        else
                                        {
                                            order.get(getOrderIndexByName(selectedProduct)).addItem(toppings, editText.getText().toString(), getItemExtra(toppings), getProductPrice(selectedProduct), Integer.parseInt(quantityEditText.getText().toString()));
                                        }
                                    }
                                    else {
                                        if (quantityEditText.getText().toString().equals(""))
                                        {
                                            order.get(getOrderIndexByName(selectedProduct)).getItemByToppings(toppings).quantity += 1;
                                        }
                                        else
                                        {
                                            order.get(getOrderIndexByName(selectedProduct)).getItemByToppings(toppings).quantity = order.get(getOrderIndexByName(selectedProduct)).getItemByToppings(toppings).quantity + Integer.parseInt(quantityEditText.getText().toString());
                                        }
                                    }
                                    if (quantityEditText.getText().toString().equals(""))
                                    {
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity + 1 - 1;
                                    }
                                    else
                                    {
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity + Integer.parseInt(quantityEditText.getText().toString()) - 1;
                                    }

                                    updateItems(selectedProduct);
                                    for (int j = 0; j >= innerLayout1.getChildCount(); j++)
                                    {
                                        ((CheckBox)innerLayout1.getChildAt(j)).setChecked(false);
                                    }
                                    editText.setText("");
                                    quantityEditText.setText("");
                                    for (int j = 0; j < innerLayout1.getChildCount(); j++) {
                                        CheckBox checkBox = (CheckBox) innerLayout1.getChildAt(j);
                                        if (checkBox.isChecked()) {
                                            checkBox.setChecked(false);
                                        }

                                    }
                                }
                                catch (Exception ex)
                                {
                                    Log.d("PaymentActivity", ex.getMessage());
                                }
                            }

                        })
                        .setNegativeButton("Ακυρωση", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity - 1;
                            if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0)
                            {
                                try {
                                    Log.d("PaymentActivity",String.valueOf(getProductIndexByName(selectedProduct)));
                                    deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                    ((TextView) orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                    deleteProductFromOrder(getProductIndexByName(selectedProduct));

                                }
                                catch (Exception ex)
                                {
                                    Log.d("PaymentActivity", ex.getMessage());
                                }
                            }
                                updateItems(selectedProduct);
                            }
                        })
                        .setCancelable(false)
                        .setView(layout)
                        .create();
            case 1:

                return new AlertDialog.Builder(this)
                        .setTitle("Σχολια")
                        .setPositiveButton("Ετοιμο", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if ((!Pattern.matches("^[0-9\\^&]+$|^$", qEditText.getText().toString()))||Pattern.matches("^0.*", qEditText.getText().toString()))
                                {
                                    Toast.makeText(OrderActivity.this, "Η ποσοτητα μπορει να αποτελειτε μονο απο αριθμους και δε γινεται να ξεκιναει με 0!", Toast.LENGTH_SHORT).show();
                                    order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity - 1;
                                    if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0)
                                    {
                                        deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                        deleteProductFromOrder(getProductIndexByName(selectedProduct));
                                        ((TextView)orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                    }
                                    updateItems(selectedProduct);
                                    editText1.setText("");
                                    qEditText.setText("");
                                    return;
                                }
                                try {
                                    if (order.get(getOrderIndexByName(selectedProduct)).comments != null)
                                    order.get(getOrderIndexByName(selectedProduct)).addComments(order.get(getOrderIndexByName(selectedProduct)).comments + " & " +  editText1.getText().toString());
                                    else order.get(getOrderIndexByName(selectedProduct)).addComments(editText1.getText().toString());
                                    if (qEditText.getText().toString().equals("")) {
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity + 1 - 1;
                                    }
                                    else {
                                        order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity + Integer.parseInt(qEditText.getText().toString()) - 1;
                                    }
                                    editText1.setText("");
                                    qEditText.setText("");
                                    updateItems(selectedProduct);
                                }
                                catch(Exception ex)
                                {
                                    Log.d("PaymentActivity", ex.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("Ακυρωση", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (order.get(getOrderIndexByName(selectedProduct)).comments != null)
                                order.get(getOrderIndexByName(selectedProduct)).addComments(order.get(getOrderIndexByName(selectedProduct)).comments);
                                else order.get(getOrderIndexByName(selectedProduct)).addComments(order.get(getOrderIndexByName(selectedProduct)).comments);

                                order.get(getOrderIndexByName(selectedProduct)).quantity = order.get(getOrderIndexByName(selectedProduct)).quantity - 1;
                                if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0)
                                {
                                    try {
                                        Log.d("PaymentActivity",String.valueOf(getProductIndexByName(selectedProduct)));
                                        deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                        ((TextView) orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                        deleteProductFromOrder(getProductIndexByName(selectedProduct));
                                        editText1.setText("");
                                        qEditText.setText("");

                                    }
                                    catch (Exception ex)
                                    {
                                        Log.d("PaymentActivity", ex.getMessage());
                                    }
                                }
                                updateItems(selectedProduct);
                            }
                        }).setView(layout2)
                        .create();
            case 2:

                Log.d("PaymentActivity", "Got there!!!!!!!!!!!!!!");

                return new AlertDialog.Builder(this)
                        .setPositiveButton("Ετοιμο", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Ακυρο", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setView(layout1)
                        .create();
            case 3:
                return new AlertDialog.Builder(this)
                        .setTitle(table)
                        .setMessage("Το τραπεζι " + table + " εχει ηδη ανοιχτη παραγγελια. Θελετε να προσθεσετε συμπληρωματικη παραγγελια;")
                        .setPositiveButton("ΝΑΙ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                table = tableTxBox.getText().toString();
                                order1.table = table;
                                order1.send();
                                orderLayout.removeAllViews();
                                order = new ArrayList<>();
                                orderTextViews = new ArrayList<>();
                                for (int j = 0; j < orderList.getChildCount();j++) {

                                        TextView txView;
                                        txView = (TextView) orderList.getChildAt(j);
                                        if (txView.getCurrentTextColor()==-11975345)
                                            txView.setBackgroundColor(Color.WHITE);
                                }
                            }
                        })
                        .setNegativeButton("ΟΧΙ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
        }
        return null;
    };

    private float getItemExtra(String topStr)
    {
        String[] tops = topStr.split(",");
        float price = 0;
        for (String tp: tops)
        {
            price = price + getToppingExtraFromName(tp);
            Log.d("PaymentActivity", "getItemExtra: " + tp + " "+ getToppingExtraFromName(tp));
        }
        Log.d("PaymentActivity", "getItemExtra price:" + price );
        return price;
    }
    private float getToppingExtraFromName(String tp)
    {
        for (int j=0; j<items.length;j++)
        {
            if (items[j].equals(tp))
                return Float.parseFloat(itemPrices[j]);
        }
        return -1;
    }

    private int getOrderIndexByName(String name)
    {
        for (int j = 0; j < order.size();j++)
        {
            if (order.get(j).name.equals(name))
                return j;
        }
        return -1;
    }


    private void updateItems(String name)
    {
        Log.d("PaymentActivity", "Starting update items");
        TextView textView;
        for (int j = 0; j<orderLayout.getChildCount();j++)
        {
            textView = (TextView) orderLayout.getChildAt(j);
            if (textView.getText().toString().split(":")[0].equals(name))
            {
                textView.setText(order.get(getOrderIndexByName(name)).name + ": " + String.valueOf(order.get(getOrderIndexByName(name)).quantity));
            }
        }
    }
    private String[] getItems(String name)
    {
        Log.d("PaymentActivity", "getItems: " + selectedProduct);
        Product product;
        product = order.get(getOrderIndexByName(name));
        ArrayList<String> items = new ArrayList<String>();
        Log.d("PaymentActivity", "Starting getItems");
        Log.d("PaymentActivity", "items: " + product.items.size());
        for ( int j = 0; j < product.items.size();j++)
        {
            items.add(product.items.get(j).getQuantity() + " " + product.items.get(j).getItemString());
            Log.d("PaymentActivity", product.items.get(j).getItemString());
            Log.d("PaymentActivity", "getItems: " + j + " " + product.items.get(j).toppings);
        }
        String[] items_arr = new String[items.size()];
        return items.toArray(items_arr);
    }
    private void addProductInLayout(int i)
    {
        TextView textView;
        textView = (TextView) orderList.getChildAt(i);

        try
        {
            Log.d("PaymentActivity", String.valueOf(orderTextViews.size()));
            for (int j = 0; j < orderTextViews.size(); j++) {
                Log.d("PaymentActivity", orderTextViews.get(j).getText().toString().split(":")[0] + " ?= " + textView.getText().toString().split("\n")[0]);

                if (orderTextViews.get(j).getText().toString().split(":")[0].equals(textView.getText().toString().split("\n")[0])) {
                    Log.d("PaymentActivity", String.valueOf("Q: " + getProductQuantity(order.get(getOrderIndex(i)).name)));
                    if (getProductQuantity(order.get(getOrderIndex(i)).name) != 0)
                    {
                        orderTextViews.get(j).setText(orderTextViews.get(j).getText().toString().split(":")[0] + ": " + String.valueOf(getProductQuantity(order.get(getOrderIndex(i)).name)));
                        Log.d("PaymentActivity", "Got there!");
                        return;
                    }
                }
            }

            orderTextViews.add(new TextView(OrderActivity.this));
            orderTextViews.get(orderTextViews.size() - 1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            orderTextViews.get(orderTextViews.size() - 1).setText(order.get(getOrderIndex(i)).name + ": " + order.get(getOrderIndex(i)).quantity);
            orderTextViews.get(orderTextViews.size() - 1).setTextSize(30);
            orderTextViews.get(orderTextViews.size() - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        orderProductClicked = orderLayout.indexOfChild(view);
                        layout1 = new LinearLayout(OrderActivity.this);
                        layout1.setOrientation(LinearLayout.VERTICAL);
                        title = new TextView(OrderActivity.this);
                        TextView textView1;
                        textView1 = (TextView) view;
                        selectedProduct = textView1.getText().toString().split(":")[0];
                        title.setText(selectedProduct);
                        Log.d("PaymentActivity", "Got there!!!!!!!!!!!!!!!!!!!!!!!!");
                        Log.d("PaymentActivity", "selectedProduct =======================> " + selectedProduct);
                        Log.d("PaymentActivity", "itemsDialog");

                        innerLayout = new LinearLayout(OrderActivity.this);
                        innerLayout.setOrientation(LinearLayout.VERTICAL);

                        title.setTextSize(25);
                        title.setTypeface(title.getTypeface(), Typeface.BOLD);

                        layout1.addView(title);

                        itemList = getItems(selectedProduct);

                        chBoxes = new ArrayList<CheckBox>();

                        if (order.get(getOrderIndexByName(selectedProduct)).items.size()>0)
                        {

                            for (String item : itemList)
                            {
                                chBoxes.add(new CheckBox(OrderActivity.this));
                                chBoxes.get(chBoxes.size() - 1).setText(item);
                            }
                        }
                        else
                        {
                            for (Product product:order)
                            {
                                if (product.name.equals(selectedProduct))
                                {
                                    chBoxes.add(new CheckBox(OrderActivity.this));
                                    chBoxes.get(chBoxes.size()-1).setText(product.name);
                                }
                            }
                        }
                        for (CheckBox checkBox:chBoxes)
                        {
                            innerLayout.addView(checkBox);
                        }
                        layout1.addView(innerLayout);

                        MaterialButton delBtn;
                        delBtn = new MaterialButton(OrderActivity.this);
                        delBtn.setText("Διαγραφη");
                        delBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (order.get(getOrderIndexByName(selectedProduct)).items.size() != 0)
                                        Log.d("PaymentActivity", order.get(getOrderIndexByName(selectedProduct)).getItemsString());
                                    for (int j = 0; j < innerLayout.getChildCount(); j++) {
                                        CheckBox checkBox;
                                        checkBox = (CheckBox) innerLayout.getChildAt(j);
                                        Log.d("PaymentActivity","got there!");
                                        if (checkBox.isChecked())
                                        {
                                            Log.d("PaymentActivity", "Item " + checkBox.getText().toString() + " is checked");
                                            selectedItem = checkBox.getText().toString().split("\n")[1];
                                            Log.d("PaymentActivity", "Product: " + order.get(getOrderIndexByName(selectedProduct)).quantity + " item: " + order.get(getOrderIndexByName(selectedProduct)).getItemByToppings(selectedItem).quantity);
                                            order.get(getOrderIndexByName(selectedProduct)).deleteItem(selectedItem);
                                            Log.d("PaymentActivity", "Got there!!!");
                                            Log.d("PaymentActivity", "SelectedItem: " + selectedItem);

                                            Log.d("PaymentActivity", String.valueOf(itemList.length));
                                        }
                                    }
                                    if (selectedItem.isEmpty())
                                    {

                                    }
                                    itemList = new String[getItems(selectedProduct).length];
                                    itemList = getItems(selectedProduct);
                                    chBoxes = new ArrayList<CheckBox>();
                                    innerLayout.removeAllViews();
                                    for (String item: itemList) {
                                        chBoxes.add(new CheckBox(OrderActivity.this));
                                        chBoxes.get(chBoxes.size() - 1).setText(item);
                                    }
                                    for (CheckBox checkBox:chBoxes)
                                    {
                                        innerLayout.addView(checkBox);
                                    }
                                    if (order.get(getOrderIndexByName(selectedProduct)).quantity == 0)
                                    {
                                        deleteProductFromLayout(getProductIndexByName(selectedProduct));
                                        deleteProductFromOrder(getProductIndexByName(selectedProduct));
                                        ((TextView)orderList.getChildAt(getProductIndexByName(selectedProduct))).setBackgroundColor(Color.WHITE);
                                    }
                                    updateItems(selectedProduct);
                                    Log.d("PaymentActivity", "SelectedProduct: " + selectedProduct);
                                }
                                catch (Exception ex)
                                {
                                    Log.d("PaymentActivity", "Dialog exception: " + ex.getMessage());
                                }
                            }
                        });

                        layout1.addView(delBtn);
                        AlertDialog.Builder itemsDialog = new AlertDialog.Builder(OrderActivity.this);
                        itemsDialog.setView(layout1);
                        itemsDialog.create();
                        itemsDialog.show();
                    }
                    catch(Exception ex)
                    {
                        Log.d("PaymentActivity", ex.getMessage());
                    }
                }
            });
            Log.d("PaymentActivity", "got thereeeeee");
            orderLayout.addView(orderTextViews.get(orderTextViews.size() - 1));
            Log.d("PaymentActivity", "got thereeeeeeeeee");
        }
        catch(Exception ex)
        {
            Log.d("PaymentActivity", ex.getMessage());
        }
    }


    public void deleteItem(String itemString)
    {

    }

    private void loadExistingOrder()
    {

    }

    private void orderStringDecode()
    {

    }

    private int getProductIndexByName(String name)
    {
        TextView textView1;
        for (int j  =0 ; j < orderList.getChildCount(); j++)
        {
            textView1 = (TextView) orderList.getChildAt(j);
            Log.d("PaymentActivity", textView1.getText().toString() + "?=" + name);
            if (textView1.getText().toString().equals(name))
                return j;
        }
        return -1;
    }

    private int getOrderIndex(int i)
    {
        TextView textView;
        textView = (TextView) orderList.getChildAt(i);
        for (int j = 0; j < order.size(); j++)
        {
            Log.d("PaymentActivity", "getOrderIndex: " + textView.getText().toString().split("\n")[0] + "?=" + order.get(j).name );
            Log.d("PaymentActivity", String.valueOf(j));
            if (textView.getText().toString().split("\n")[0].equals(order.get(j).name))
            {
                return j;
            }
        }
        return -1;
    }
    private int getProductQuantity(String name)
    {
        for (int s=0; s < order.size(); s++)
        {
            if (order.get(s).name.equals(name))
            {
               return order.get(s).quantity;
            }
        }
        return 0;
    }
    private float getProductPrice(String name)
    {
        for (int s=0; s < rows.length; s++)
        {
            if (rows[s].split("/")[0].equals(name))
                return Float.parseFloat(rows[s].split("/")[1]);
        }
        return -1;
    }
    private String getProductCategory(String name)
    {
        for (int s=0; s < rows.length; s++)
        {
            Log.d("PaymentActivity", "getProductCategory: " + rows[s].split("/")[0] + "?=" + name);
            if (rows[s].split("/")[0].equals(name))
            {
                return rows[s].split("/")[2];
            }
        }
        return null;
    }


    private void addProductInOrder(int i)
    {

        TextView textView;
        textView = (TextView) orderList.getChildAt(i);
            for (int j = 0; j < order.size(); j++)
            {
                if (order.get(j).name.equals(textView.getText().toString().split("\n")[0]))
                {

                    Log.d("PaymentActivity", "testing");
                    order.get(j).quantity = order.get(j).quantity + 1;
                    Log.d("PaymentActivity", String.valueOf(order.get(j).quantity));
                    for (int s = 0; s < order.size(); s++)
                    {
                        Log.d("PaymentActivity", order.get(s).name + " index: " + s);
                    }
                    return;
                }
            }


            order.add(new Product(textView.getText().toString().split("\n")[0],getProductPrice(textView.getText().toString().split("\n")[0]),getProductCategory(textView.getText().toString().split("\n")[0]), 1));
        Log.d("PaymentActivity", "Got there!");
        Log.d("PaymentActivity", String.valueOf(order.size()));
        for (int j = 0; j < order.size(); j++)
        {
            Log.d("PaymentActivity", order.get(j).name + " index: " + j);
        }

            for (int j = 0; i < arrayList.size(); i++)
            {

                if (order.get(j).name.equals(orderList.getChildAt(i).toString())) {

                    //order.add(new Product(orderList.getChildAt(i).toString(), getProductPrice(orderList.getChildAt(i).toString()),getProductCategory(orderList.getChildAt(i).toString()),1));
                    arrayList.set(j, arrayList.get(j) + "           " + String.valueOf(order.get(j).quantity));

                    //arrayAdapter.notifyDataSetChanged();
                }
            }

        }


    private void deleteProductFromLayout(int i)
    {
        TextView textV;
        textV = (TextView) orderList.getChildAt(i);

        try {
            TextView textView;
            Log.d("PaymentActivity", String.valueOf(orderLayout.getChildCount()));
            for (int j = 0; j < orderLayout.getChildCount(); j++) {
                Log.d("PaymentActivity","Index: " + j);
                 textView = (TextView) orderLayout.getChildAt(j);
                Log.d("PaymentActivity",  textView.getText().toString().split(":")[0] + "?=" + textV.getText().toString().split("\n")[0]);
                if (textView.getText().toString().split(":")[0].equals(textV.getText().toString().split("\n")[0])) {
                    orderLayout.removeView(orderLayout.getChildAt(j));
                }
            }
            for (int j = 0; j<orderTextViews.size();j++)
            {
                Log.d("PaymentActivity", "orderTextViews: " + orderTextViews.get(j).getText().toString().split(":")[0] + "?=" + textV.getText().toString().split("\n")[0]);
                if (orderTextViews.get(j).getText().toString().split(":")[0].equals(textV.getText().toString().split("\n")[0]))
                    orderTextViews.remove(j);
            }
        }
        catch(Exception ex)
        {
            Log.d("PaymentActivity", ex.getMessage());
        }
    }
    private void deleteProductFromOrder(int i) {
        TextView textView;
        textView = (TextView) orderList.getChildAt(i);
        for (int j = 0; j < order.size(); j++)
        {
            if (textView.getText().toString().split("\n")[0].equals(order.get(j).name))
            {
                order.remove(j);
                Log.d("PaymentActivity", "Removed product from order");
                return;
            }
        }
        Log.d("PaymentActivity", "Could not find product");

    }

    private int getArrayListIndex(int i)
    {
        TextView tx;
        tx = (TextView) orderList.getChildAt(i);

        for (int j = 0; j<rows.length; j++)
        {
            Log.d("PaymentActivity", rows[j].split("/")[0] + "?=" + tx.getText().toString());
            if (rows[j].split("/")[0].equals(tx.getText().toString()))
            {
                Log.d("PaymentActivity", String.valueOf(j) + " " + String.valueOf(rows.length));
                return j;
            }
        }
        return -1;
    }

    private class getOrders extends AsyncTask {
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
                    Toast.makeText(OrderActivity.this, "Υπηρξε προβλημα με την συνδεση στη βαση δεδομενων. Παρακαλω ελεγξτε την διευθυνση IP η επικοινωνηστε με τον προραμμαστστη.", Toast.LENGTH_SHORT).show();
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

                return null;
            } catch (Exception ex) {
                Log.d("PaymentActivity", "exception: " + ex.getMessage());
            }
            return null;
        }
    }
    private class loadProducts extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(GET_PRODUCTS);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                arrayList = new ArrayList();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                rows = new String[0];
                rows = response.split("_");
                x = 0;
                for (String row:rows)
                {
                    products.add(new Product(row.split("/")[0], DecimalFormat.getNumberInstance().parse(row.split("/")[1]).floatValue(),row.split("/")[2] ,0));
                    Log.d("PaymentActivity", "Name: " + products.get(x).name + " Price: " + products.get(x).price + " Category: " + products.get(x).category);
                    x = x + 1;
                }

                url = new URL(GET_CATEGORIES);
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                 inputStream = new BufferedInputStream(client.getInputStream());
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                 stringBuilder = new StringBuilder();

                for (int ch; (ch = rd.read()) != -1; )
                    stringBuilder.append((char) ch);
                response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                categories = new String[0];
                categories = response.split("_");

                arrayList.removeAll(arrayList);
                for (String category:categories)
                {
                    arrayList.add(category);
                    y = 0;
                    for (Product prod: products)
                    {
                        if (prod.category.equals(category))
                        {
                            arrayList.add(prod.name + "\n" + prod.price);
                            Log.d("PaymentActivity", "Product " + y + " :" + prod.name);
                            y = y + 1;
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            orderList.removeAllViews();
                            for (int j = 0; j <= arrayList.size(); j++) {

                                TextView textView = new TextView(OrderActivity.this);

                                productsTextViews.add(textView);

                                //Log.d("PaymentActivity", String.valueOf(productsTextViews.size()-1));

                                productsTextViews.get(productsTextViews.indexOf(textView)).setText(arrayList.get(j).toString().split("\n")[0]);
                                productsTextViews.get(productsTextViews.indexOf(textView)).setTextSize(40);

                                if( !arrayList.get(j).toString().contains("\n")) {
                                    textView.setBackgroundColor(Color.BLACK);
                                    textView.setTextColor(Color.WHITE);
                                }

                                orderList.addView(productsTextViews.get(productsTextViews.indexOf(textView)), productsTextViews.size()-1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                                productsTextViews.get(productsTextViews.indexOf(textView)).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        try {

                                            TextView textView1;
                                            textView1 = (TextView) view;
                                            selectedProduct = textView1.getText().toString();
                                            for (String category:categories) {
                                                if (((TextView) view).getText().toString().equals(category)) return;
                                            }
                                            //if (!textView1.getText().toString().contains("\n"))
                                              //  return;
                                            view.setBackgroundColor(Color.GREEN);
                                            Log.d("PaymentActivity",String.valueOf("index: " + orderList.indexOfChild(view)));
                                            addProductInOrder(orderList.indexOfChild(view));
                                            addProductInLayout(orderList.indexOfChild(view));
                                            Log.d("PaymentActivity", "addProductInLayout()");

                                            Log.d("PaymentActivity", String.valueOf(rows.length));
                                            Log.d("PaymentActivity", "ShowDialog(1)");
                                            if (rows[getArrayListIndex(orderList.indexOfChild(view))].split("/").length<4)
                                            {
                                             showDialog(1);
                                            }
                                            else
                                            {
                                                new getToppings().execute();
                                            }
                                            Log.d("PaymentActivity", "Order products: " + String.valueOf(order.size()));
                                        }
                                        catch (Exception ex)
                                        {
                                            Log.d("PaymentActivity", "exception: " + ex.getMessage());
                                        }
                                    }
                                });
                                productsTextViews.get(productsTextViews.indexOf(textView)).setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        TextView textView1;
                                        textView1 = (TextView) view;
                                        //if (!textView1.getText().toString().contains("\n"))
                                          //  return true;
                                        view.setBackgroundColor(Color.WHITE);
                                        deleteProductFromOrder(productsTextViews.indexOf(textView));
                                        deleteProductFromLayout(productsTextViews.indexOf(textView));
                                        return true;
                                    }
                                });

                            }


                        }
                        catch (Exception ex)
                        {
                            Log.d("PaymentActivity", ex.getMessage());
                        }
                    }

                });}


            catch (Exception ex)
            {
                Log.d("PaymentActivity", "exception: " + ex.getMessage());
            }


            return null;
        }
    }
    private class getOrder extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL url = new URL(GET_PRODUCTS);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.connect();
                String message = client.getResponseMessage();
                Log.d("PaymentActivity", "message: " + message);
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                arrayList = new ArrayList();
                for (int ch; (ch = inputStream.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);

            }
            catch (Exception ex)
            {
                Log.d("PaymentActivity", ex.getMessage());
            }
            return null;
        }
    }
    private class getToppings extends AsyncTask
    {
        protected Object doInBackground(Object[] objects) {
            String data = URLEncoder.encode("name") + "=" + URLEncoder.encode(selectedProduct);
            Log.d("PaymentActivity", data);
            try {

                URL url = new URL(GET_TOPPINGS);
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
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                stringBuilder = new StringBuilder();
                for (int ch; (ch = rd.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);
                items =response.split(",");
                itemPrices = response.split(",");
                Log.d("PaymentActivity", "Got thereeeee!!!!!!");
                for(int j = 0;j<items.length;j++)
                {
                    Log.d("PaymentActivity", items[j]);
                    itemPrices[j] = items[j].split("/")[1];
                    items[j] = items[j].split("/")[0];



                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog(0);
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

}