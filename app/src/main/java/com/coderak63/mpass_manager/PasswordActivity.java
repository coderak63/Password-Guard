package com.coderak63.mpass_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordActivity extends AppCompatActivity {
    Button enter,set,use_fingerprint;
    TextInputEditText passcode,setpasscode1,setpasscode2;

    TextInputLayout setpasscode1_layout,setpasscode2_layout,passcode_layout;

    SharedPreferences sp;
    SharedPreferences.Editor myEdit,self_destruct_count_Edit;
    Boolean contains=false,errorless=false;
    String pw;

    private BroadcastReceiver receiver;
    SharedPreferences pref,self_destruct_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }

        IntentFilter filter= new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(pref.getBoolean("key_auto_exit",false)){
                    finish();
                    System.exit(0);
                }
                //Toast.makeText(context, "Broadcast Detected.", Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(receiver,filter);

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sp = EncryptedSharedPreferences.create("setPw", masterKeyAlias, PasswordActivity.this, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            myEdit = sp.edit();
            contains = sp.contains("passcode");
        }catch (Exception e)
        {
            Toast.makeText(PasswordActivity.this, "Unexpected Error occured", Toast.LENGTH_LONG).show();
        }
        if(contains)
        {
            self_destruct_count = getSharedPreferences("self_destruct_count",MODE_PRIVATE);
            self_destruct_count_Edit = self_destruct_count.edit();
            if(!self_destruct_count.contains("count")){
                self_destruct_count_Edit.putInt("count",5);
                self_destruct_count_Edit.apply();
            }


            setContentView(R.layout.activity_password);
            pw = sp.getString("passcode","0");

            enter=findViewById(R.id.enter);
            passcode=findViewById(R.id.passcode);
            passcode_layout=findViewById(R.id.passcode_layout);

            use_fingerprint=findViewById(R.id.use_fingerprint);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M || !pref.getBoolean("key_use_fingerprint",false))
            {
                use_fingerprint.setClickable(false);
                use_fingerprint.setVisibility(View.GONE);
            }

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String code = passcode.getText().toString();

                        if (code.equals(pw)) {
                            self_destruct_count_Edit.putInt("count",5);
                            self_destruct_count_Edit.apply();
                            Intent i = new Intent(PasswordActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            passcode_layout.setError("Incorrect passcode!");

                            if(pref.getBoolean("key_self_destruct",false))
                            {
                                int temp=self_destruct_count.getInt("count",5);
                                temp--;
                                self_destruct_count_Edit.putInt("count",temp);
                                self_destruct_count_Edit.apply();
                                temp=self_destruct_count.getInt("count",5);

                                if(temp>0) {
                                    String st = "Your have " + temp + " attempts remaining.\nAfter " + temp + " incorrect attempts, all data will be deleted.";
                                    Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
                                }else if(temp<=0)
                                {
                                    deleteAllData();
                                    String st = "All data have been deleted.";
                                    Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                }
            });


            use_fingerprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PasswordActivity.this,FingerprintActivity.class);
                    startActivity(i);
                    finish();
                }
            });

        }else
        {
            setContentView(R.layout.activity_set_password);

            set=findViewById(R.id.set);
            setpasscode1=findViewById(R.id.setpasscode1);
            setpasscode2=findViewById(R.id.setpasscode2);
            setpasscode1_layout=findViewById(R.id.setpasscode1_layout);
            setpasscode2_layout=findViewById(R.id.setpasscode2_layout);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String code1 = setpasscode1.getText().toString();
                    String code2 = setpasscode2.getText().toString();
                    if(code1.length()==6&&code2.length()!=6){
                        setpasscode1_layout.setError(null);
                        setpasscode2_layout.setError("Enter 6 digit passcode!");
                        errorless=false;
                    }else if(code1.length()!=6&&code2.length()==6){
                        setpasscode2_layout.setError(null);
                        setpasscode1_layout.setError("Enter 6 digit passcode!");
                        errorless=false;
                    }else if(code1.length()!=6&&code2.length()!=6){
                        setpasscode1_layout.setError("Enter 6 digit passcode!");
                        setpasscode2_layout.setError("Enter 6 digit passcode!");
                        errorless=false;
                    }else {
                        setpasscode1_layout.setError(null);
                        setpasscode2_layout.setError(null);
                        errorless = true;

                        if (code1.equals(code2) && errorless) {
                            myEdit.putString("passcode", code1);
                            myEdit.apply();


                            Intent i = new Intent(PasswordActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            setpasscode2_layout.setError("Passcode not matched!");
                        }
                    }
                }
            });
        }



    }


    @Override
    protected void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();

    }

    private void deleteAllData()
    {
        DbHandler db = DbHandler.getInstance(PasswordActivity.this);
        db.deleteAll();
    }
}
