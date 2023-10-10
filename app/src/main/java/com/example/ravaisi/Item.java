package com.example.ravaisi;

import android.util.Log;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Item {
    int id;
    String name;
    float extra;
    public String toppings;
    String comments;
    float productPrice;
    int quantity;

    public Item(int id, String name, float extra, String toppings, String comments, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.toppings = toppings;
        this.comments = comments;
        this.extra = extra;
        this.quantity = quantity;
        this.productPrice = price;
    }

    public BigDecimal calculatePrice()
    {
        //multiplication error
        BigDecimal price;
        BigDecimal product_price = new BigDecimal(String.valueOf(this.productPrice));
        BigDecimal extra = new BigDecimal(String.valueOf(this.extra));
        BigDecimal quantity_ = new BigDecimal(this.quantity);
         price = quantity_.multiply(product_price.add(extra));
        Log.d("PaymentActivity", "calculatePrice product price:" + String.valueOf(this.productPrice) + "extra: " + String.valueOf(this.extra) + " :" + price.toString());
        return price;
    }
    public String getItemString()
    {
       return this.name + "\n" + this.toppings; //+ "<" + this.comments + ">";
    }
    public String getQuantity()
    {
        return String.valueOf(quantity);
    }

    public String getItemToppings()
    {return this.toppings;}

}



