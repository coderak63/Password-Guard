package com.coderak63.mpass_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputLayout old_layout,new1_layout,new2_layout;
    TextInputEditText old,new1,new2;
    Button cancel,change;

    SharedPreferences sp;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Change Passcode");
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_change_password);

        old_layout=findViewById(R.id.oldpasscode_layout);
        new1_layout=findViewById(R.id.newpasscode1_layout);
        new2_layout=findViewById(R.id.newpasscode2_layout);

        old=findViewById(R.id.oldpasscode);
        new1=findViewById(R.id.newpasscode1);
        new2=findViewById(R.id.newpasscode2);

        cancel=findViewById(R.id.cancel);
        change=findViewById(R.id.change);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                    sp = EncryptedSharedPreferences.create("setPw", masterKeyAlias, ChangePasswordActivity.this, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                    myEdit = sp.edit();
                    String pw = sp.getString("passcode", "0");

                    if (old.getText().toString().equals(pw)) {
                        old_layout.setError(null);
                        if (new1.getText().toString().length() == 6 && new2.getText().toString().length() != 6) {
                            new1_layout.setError(null);
                            new2_layout.setError("Enter 6 digit passcode!");
                        } else if (new1.getText().toString().length() != 6 && new2.getText().toString().length() == 6) {
                            new2_layout.setError(null);
                            new1_layout.setError("Enter 6 digit passcode!");
                        } else if (new1.getText().toString().length() != 6 && new2.getText().toString().length() != 6) {
                            new1_layout.setError("Enter 6 digit passcode!");
                            new2_layout.setError("Enter 6 digit passcode!");
                        } else {

                            if (new1.getText().toString().equals(new2.getText().toString())) {
                                myEdit.putString("passcode", new1.getText().toString());
                                myEdit.apply();

                                Toast toast = Toast.makeText(getApplicationContext(), "Passcode changed successfully!", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            } else {
                                old_layout.setError(null);
                                new1_layout.setError(null);
                                new2_layout.setError("Confirm Passcode not matched!");
                            }
                        }
                    } else {
                        new1_layout.setError(null);
                        new2_layout.setError(null);
                        old_layout.setError("Please Enter Correct Passcode!");
                    }


                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error while changing password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
