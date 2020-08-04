package com.coderak63.mpass_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowActivity extends AppCompatActivity {

    private ActionMode mActionMode;

    TextInputEditText title,website,username,password,notes;
    int id;
    TextView lastUpdate;
    String date;
    ImageButton title_copy,website_send,username_copy,password_copy,notes_copy;
    TextInputLayout title_layout;

    private ClipboardManager myClipboard;
    private ClipData myClip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));

        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }

        setContentView(R.layout.activity_show);

        id=getIntent().getIntExtra("id",0);

        title=(TextInputEditText)findViewById(R.id.title_show);
        website=(TextInputEditText)findViewById(R.id.website_show);
        username=(TextInputEditText)findViewById(R.id.username_show);
        password=(TextInputEditText)findViewById(R.id.password_show);
        notes=(TextInputEditText)findViewById(R.id.notes_show);


        title_copy=(ImageButton)findViewById(R.id.title_copy);
        website_send=(ImageButton)findViewById(R.id.website_send);
        username_copy=(ImageButton)findViewById(R.id.username_copy);
        password_copy=(ImageButton)findViewById(R.id.password_copy);
        notes_copy=(ImageButton)findViewById(R.id.notes_copy);

        title_layout = (TextInputLayout) findViewById(R.id.title_layout);


        title.setText(getIntent().getStringExtra("title"));
        website.setText(getIntent().getStringExtra("website"));
        username.setText(getIntent().getStringExtra("username"));
        password.setText(getIntent().getStringExtra("password"));
        notes.setText(getIntent().getStringExtra("notes"));

        lastUpdate = (TextView)findViewById(R.id.lastUpdate);
        lastUpdate.setText("Last updated: "+getIntent().getStringExtra("date"));

        title.setEnabled(false);
        website.setEnabled(false);
        username.setEnabled(false);
        password.setEnabled(false);
        notes.setEnabled(false);




        Toolbar toolbar = findViewById(R.id.toolbar_show);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        title_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = title.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        website_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = website.getText().toString();
                text=text.trim();
                if(!text.startsWith("http://")&&!text.startsWith("https://"))
                    text="http://"+text;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(text));
                startActivity(i);
            }
        });

        username_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = username.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        password_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = password.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        notes_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = notes.getText().toString();
                myClip = ClipData.newPlainText("text",text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

/*
    @Override
    public void onBackPressed() {

        Intent mainactivity = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(mainactivity);
        finish();
    }

 */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if(mActionMode!=null){
                return false;
            }
            //getSupportActionBar().hide();
            title.setEnabled(true);
            website.setEnabled(true);
            username.setEnabled(true);
            password.setEnabled(true);
            notes.setEnabled(true);

            mActionMode=startSupportActionMode(mActionModeCallback);
            return true;
        }else if (id == R.id.action_share) {
            String txt="Title: "+title.getText().toString()+"\n\nWebsite: "+website.getText().toString()+"\nUsername: "+username.getText().toString()+"\nPassword: "+password.getText().toString()+"\nNotes: "+notes.getText().toString()+"\n-Shared by Password Guard app";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,txt);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode,menu);
            mode.setTitle("Edit");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.option1:
                    mode.finish();
                    return true;
                case R.id.option2:
                    if(title.getText().toString().isEmpty()){
                        title_layout.setError("Title can't be empty.");
                        Toast.makeText(getApplicationContext(),"Title can't be empty.",Toast.LENGTH_LONG).show();
                        return false;
                    }

                    title_layout.setError(null);


                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a",new Locale("en","IN"));
                    date=sdf.format(new Date());
                    lastUpdate.setText("Last updated: "+date);

                    DbHandler db = DbHandler.getInstance(ShowActivity.this);
                    db.update(id,title.getText().toString(),website.getText().toString(),username.getText().toString(),password.getText().toString(),notes.getText().toString(),date);

                    mode.finish();
                    return true;

                    default:
                        return false;

            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            title.setEnabled(false);
            website.setEnabled(false);
            username.setEnabled(false);
            password.setEnabled(false);
            notes.setEnabled(false);

            //lastUpdate.setText("Last updated: "+date);
           // getSupportActionBar().show();
            mActionMode=null;
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        //finish();

    }
}
