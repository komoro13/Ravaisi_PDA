package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        MaterialButton addCategotyBtn = findViewById(R.id.categoryBtn);
        MaterialButton addProductBtn = findViewById(R.id.productBtn);
        MaterialButton addToppings = findViewById((R.id.addToppings));
        MaterialButton settingsBtn = findViewById(R.id.settingsBtn);

        addCategotyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( SetupActivity.this, AddCategoryActivity.class));
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    startActivity(new Intent(SetupActivity.this, AddProductsActivity.class));
                }
                catch(Exception ex)
                {
                    Log.d("PaymentActivity", ex.getMessage());
                }
            }
        });
        addToppings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupActivity.this, AddToppingsActivity.class));
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupActivity.this, SettingsActivity.class));
            }
        });
    }
}