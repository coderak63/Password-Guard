package com.coderak63.mpass_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("About App");


        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_about);


        LinearLayout contact_us = findViewById(R.id.about_contact_us);
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.setData(Uri.parse("mailto:coderak63@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Query about Password Guard");
                startActivity(Intent.createChooser(intent, getString(R.string.choose_email_client)));
            }
        });



        LinearLayout report_bug = findViewById(R.id.about_report_bug);
        report_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String body = null;
                try {
                    body = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                            Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                            "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
                } catch (PackageManager.NameNotFoundException e) {
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.setData(Uri.parse("mailto:coderak63@gmail.com"));
                //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"abhishekrajauli@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report of Password Guard");
                intent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(intent,getString(R.string.choose_email_client)));
            }
        });


        LinearLayout rate_us = findViewById(R.id.about_rate_us);
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri =Uri.parse("market://details?id="+ getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW,uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NEW_DOCUMENT|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try{
                    startActivity(goToMarket);
                }catch(ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName())));
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
