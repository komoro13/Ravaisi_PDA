package com.example.ravaisi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    EditText ipTxBox;
    MaterialButton setIpButton;
    String IP;

    SharedPreferences netPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ipTxBox = findViewById(R.id.ipTxBox);

        netPreferences = getSharedPreferences("net", 0);
        ipTxBox.setText(netPreferences.getString("IP", ""));
        setIpButton = findViewById(R.id.setIpBtn);

            setIpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        netPreferences = getSharedPreferences("net", 0);
                        IP = ipTxBox.getText().toString();
                        SharedPreferences.Editor editor = netPreferences.edit();
                        editor.putString("IP", IP).commit();
                        if (netPreferences.getString("IP", "").equals(IP)) {
                            Toast.makeText(SettingsActivity.this, "Η διέυθυνση IP αποθηκέυτηκε επιτυχως!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Υπήρξε πρόβλημα με την αποθήκευση της διέυθυνσης.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception ex)
                    {
                        Log.d("PaymentActivity", ex.getMessage());
                    }

                }
            });


        }

    }
