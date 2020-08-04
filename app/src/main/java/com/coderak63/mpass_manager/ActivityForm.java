package com.coderak63.mpass_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ActivityForm extends AppCompatActivity {

    Button save, cancel;
    TextInputEditText title, website, username, password, notes;
    TextInputLayout title_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add New");
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_form);


        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        title = (TextInputEditText) findViewById(R.id.title);
        website = (TextInputEditText) findViewById(R.id.website);
        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        notes = (TextInputEditText) findViewById(R.id.notes);

        title_layout = (TextInputLayout) findViewById(R.id.title_layout);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    title_layout.setError("Title can't be empty.");
                    Toast.makeText(getApplicationContext(), "Title can't be empty.", Toast.LENGTH_LONG).show();

                    return;
                }
                //Intent intent = new Intent();
                //intent.putExtra("title",title.getText().toString().trim());
                //intent.putExtra("website",website.getText().toString().trim());
                //intent.putExtra("username",username.getText().toString().trim());
                // intent.putExtra("password",password.getText().toString().trim());
                // intent.putExtra("notes",notes.getText().toString().trim());
                title_layout.setError(null);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", new Locale("en", "IN"));
                String date = sdf.format(new Date());
                //intent.putExtra("date",date);

                //setResult(RESULT_OK,intent);
                //finish();

                DbHandler db = DbHandler.getInstance(ActivityForm.this);
                db.insert(title.getText().toString(),website.getText().toString(),username.getText().toString(),password.getText().toString(),notes.getText().toString(),date);

                //Intent mainactivity = new Intent(ActivityForm.this, MainActivity.class);
                //mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(mainactivity);
                finish();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //setResult(RESULT_CANCELED,intent);
                //finish();
                //Intent mainactivity = new Intent(ActivityForm.this, MainActivity.class);
                //mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(mainactivity);
                finish();
            }
        });


    }


    /*
    @Override
    public void onBackPressed() {

        Intent mainactivity = new Intent(ActivityForm.this, MainActivity.class);
        //mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainactivity);
        finish();
    }

     */


    @Override
    protected void onPause() {
        super.onPause();
        //finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}



