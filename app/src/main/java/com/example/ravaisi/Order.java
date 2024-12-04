package com.example.ravaisi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class Order {
    private String orderStr;
    double price;
    String IP = "";
    private String SEND_ORDER = "http://"+IP+"/ravaisi/SendOrder.php";
    String table;
    ArrayList<Product> products = new ArrayList<Product>();

    public Order(String table, String IP)
    {

        this.IP = IP;
        Log.d("PaymentActivity", "orderTable set =======================>" + this.table);
        this.SEND_ORDER = "http://"+IP+"/ravaisi/SendOrder.php";
        this.table = table;
    }
    void addProduct(Product product)
    {
        products.add(product);
    }
    void updateProduct(Product product)
    {

    }

    double getPrice()
    {
        double price_ = 0;
        for(Product product:products)
        {
            price_ = price_ + product.getPrice();
        }
        Log.d("PaymentActivity", "Order getPrice()");
        this.price = price_;
        return price_;
    }
    String createOrderString() {
            String order = "{table: " + this.table + "}&{price: "+ String.valueOf(this.price) + "|";
            for (Product product : products) {
                order = order + "{" + product.getProductString() + "[";
                for (Item item : product.items) {
                    if (item.comments.equals(null))
                        item.comments = "";
                    order += item.getItemString().split("\n")[1] + "<" + item.comments +">" +"(" + item.getQuantity() + ")" + "$" + item.calculatePrice().toString() + "$" + "_";
                }
                order = order + "]";
                order = order + "}";
                order = order + "&";
            }
            order.replace("\n", "-");
            order.replace("&}", "");
            order.replace("&\0", "");
            return order;
        }

    JSONObject createOrderJSON() throws JSONException {
        //This function parses the order from Order object to JSON
        //JSON format scheme
        //
        // [table][price][[products]
        //                 \
        //                  \
        //                   [name][items]
        //                          \
        //                           \
        //                            [toppings][quantity][comments][price]
        //
        //
        JSONObject orderJSON = new JSONObject();
        orderJSON.put("table", this.table);
        orderJSON.put("price",this.price);
        for (Product product:products)
        {
            JSONObject productJSON = new JSONObject();
            productJSON.put("name",product.name);
            for (Item item:product.items)
            {
                 JSONObject itemsJSON = new JSONObject();
                 itemsJSON.put("toppings",item.toppings);
                 itemsJSON.put("quantity", item.quantity);
                 itemsJSON.put("comments", item.comments);
                 itemsJSON.put("price", item.calculatePrice());
                 productJSON.put("item" + String.valueOf(product.items.indexOf(item)), itemsJSON);
            }
            orderJSON.put("product" + String.valueOf(products.indexOf(product)),productJSON);
        }
        return orderJSON;
    }




    void send()
    {
        addOrder sendOrder;
        getPrice();
        Log.d("PaymentActivity", this.table);
        sendOrder = new addOrder(this.table);
        sendOrder.execute();
    }


    private class addOrder extends AsyncTask
    {
        String order_table;
        public addOrder(String order_table)
        {
            Log.d("PaymentActivity", "Constructing add order");
            this.order_table = order_table;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
try
{
            Log.d("PAymentActivity", createOrderString() + " " + " " + this.order_table + " " + " " + String.valueOf(price));
            Log.d("PaymentActivity", "Starting addOrder");
            String data = URLEncoder.encode("order_string") + "=" + URLEncoder.encode(createOrderString())
                +"&" + URLEncoder.encode("order_table") + "=" + URLEncoder.encode(this.order_table)
                +"&" + URLEncoder.encode("price") + "=" + String.valueOf(price);
            Log.d("PaymentActivity", "Got there!!!");
            Log.d("PaymentActivity", data);
    Log.d("PaymentActivity", "Got thereeeeeeeee!!!!!");
                URL url = new URL(SEND_ORDER);
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
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                for (int ch; (ch = reader.read()) != -1; )
                    stringBuilder.append((char) ch);
                String response = stringBuilder.toString();
                Log.d("PaymentActivity", "Server: " + response);

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
