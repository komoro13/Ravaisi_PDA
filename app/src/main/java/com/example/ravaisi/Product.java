package com.example.ravaisi;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Product {
    public String name;
    public float price;
    public String category;

    public int quantity;
    String comments;
    String toppings;

    ArrayList<Item> items = new ArrayList<Item>();

    public Product(String name, float price, String category, int quantity)
    {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public void addItem(String toppings, String comments ,float extra, float price, int quantity)
    {
        Log.d("PaymentActivity", "addItem price: " + String.valueOf(this.price) + "extra: " + extra);
        items.add(new Item(items.size(), this.name, extra,toppings , comments, quantity, this.price));
        this.price = this.price + extra;
        Log.d("PaymentActivity", items.get(items.size()-1).toppings);
        this.comments = comments;
        if (this.comments.equals(null))
            this.comments = "";
    }


    public void addToppings(String toppings)
    {
        this.toppings = toppings;
    }
    public void addComments(String comments)
    {
        this.comments = comments;
    }

    public String getProductString()
    {
        return "Name:" + this.name + "& price:" + this.getPrice() + "&category:" + this.category + "&quantity:" + this.quantity + "&<" + comments + ">";
    }

    public String getItemsString()
    {
        String itemsString = "";
        for (Item item:items)
        {
            itemsString = itemsString + "id:" + item.id + "&toppings:" + item.toppings + "&comments:" + item.comments + "_";
        }
        return itemsString;
    }

    public float getPrice()
    {
        float price_ = 0;
        if (this.items.size()==0)
        {
            BigDecimal sum = new BigDecimal(String.valueOf(this.price));
            Log.d("PaymentActivity", "price: " + sum.toString());
            BigDecimal quant = new BigDecimal(this.quantity);
            Log.d("PaymentActivity", "quantity: " + quant.toString());
            Log.d("PaymentActivity", "No toppings");
            Log.d("PaymentActivity", "result: " + sum.multiply(quant).toString());
            return sum.multiply(quant).floatValue();
        }
        for (Item item:this.items)
        {
            price_ += item.calculatePrice().floatValue();
            Log.d("PaymentActivity","Product item price: " +  String.valueOf(price_));
        }
        Log.d("PaymentActivity", "Product getPrice()");
        this.price = price_;
        return price_;
    }
    public Item getItemByToppings
            (String name)
    {
        Log.d("PaymentActivity", "Starting getItemByToppings");
        for (int j=0; j<items.size();j++)
        {
            Log.d("PaymentActivity", "getItemByToppings: " + items.get(j).getItemToppings() + "?=" + name);
            if (items.get(j).getItemToppings().equals(name))
            {
                Log.d("PaymentActivity", "Item found");
                return items.get(j);
            }
        }
        Log.d("PaymentActivity", "Item not found!");
        return null;
    }
    public void deleteItem(String name)
    {
        Log.d("PaymentActivity", "Starting delete item");
        for (int j = 0; j < items.size();j++)
        {
            if (items.get(j).toppings.equals(name))
            {
                Log.d("PaymentActivity", "Deleting: " + getItemByToppings(name).toppings);
                this.quantity += - getItemByToppings(name).quantity;
                items.remove(items.indexOf(getItemByToppings(name)));
                Log.d("PaymentActivity", "itemString: " + getItemsString());

                return;

            }
        }
        Log.d("PaymentActivity", "Couldnt find item to delete");
    }
}
