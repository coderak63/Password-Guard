package com.coderak63.mpass_manager;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.opencsv.CSVReader;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int FILE_EXPORT_REQUEST_CODE = 12;

    private static final int  CHOOSE_CSV_FILE = 15;
    private BroadcastReceiver receiver;
    SharedPreferences pref;

    public static String PASS_PHRASE;

    //Fragment frag_search;
    Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences shPr = EncryptedSharedPreferences.create("secret_shared_prefs",masterKeyAlias,this,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            SharedPreferences.Editor myEdit = shPr.edit();
            if(!shPr.contains("database_key"))
            {
                String rand = generateRandomPassword(20);
                myEdit.putString("database_key",rand);
                myEdit.apply();
                PASS_PHRASE=rand;
                //Toast.makeText(this, "Not contain: "+PASS_PHRASE, Toast.LENGTH_LONG).show();
            }
            else{
                PASS_PHRASE = shPr.getString("database_key","0");
                //Toast.makeText(this, "contains: "+PASS_PHRASE, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


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

        setContentView(R.layout.activity_main);


        SQLiteDatabase.loadLibs(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //add this line to display menu1 when the activity is loaded
        displaySelectedScreen(R.id.nav_menu1);




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this,ActivityForm.class);
                //startActivityForResult(intent,FORM_ACTIVITY_REQUEST_CODE);
                startActivity(intent);
            }
        });


    }





/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent aboutPage = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(aboutPage);
            return true;
        }else if (id == R.id.settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }else if (id == R.id.action_search)
        {

        }



        return super.onOptionsItemSelected(item);
    }





 */

    @Override
    protected void onResume() {
        super.onResume();
     //finish();

        /*
        NavigationView nav = findViewById(R.id.nav_view);
        Menu menu = nav.getMenu();
        MenuItem item = menu.findItem(R.id.nav_menu1);
        item.setEnabled(true);

         */
        /*
        SharedPreferences s1 = getSharedPreferences("secret_shared_prefs",MODE_PRIVATE);
        String st = s1.getString("database_key","0");
        Toast.makeText(this, "Database key: "+st, Toast.LENGTH_LONG).show();

         */

    }




    @Override
    protected void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();

    }






    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        //Fragment fragment = null;
        fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_menu1:
                fragment = new MainFragment();
                break;
            case R.id.nav_menu2:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    //fragment = new Menu2();
                break;

            case R.id.nav_share:
                try{
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"NearbyCOVID-19");
                    String shareMessage = "\nTry out this amazing application\n\n";
                    shareMessage+="https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID+"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT,shareMessage);
                    startActivity(Intent.createChooser(shareIntent,"Choose one"));

                }catch (Exception e)
                {

                }
                break;
            case R.id.nav_rate:
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

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case FILE_EXPORT_REQUEST_CODE:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        DbHandler db = DbHandler.getInstance(this);
                        ArrayList<Model> arraylist=db.get();
                        new ExportCSV(this,arraylist).execute(uri);
                    }
                }
                break;

            case CHOOSE_CSV_FILE:
                if(data.getData().getPath()!=null) {
                    //Toast.makeText(this, data.getData().getPath(), Toast.LENGTH_LONG).show();
                    importCSV(data.getData());
                }

                break;
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }



    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        Boolean b;
        if(fragment==null) {
            b=true;
        }else{
        b=((MainFragment)fragment).myOnKeyDown();
       }
        if(b) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }



    private void selectCSVFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), CHOOSE_CSV_FILE);
    }

    private void importCSV(Uri uri){
        if (isValidCSVForImport(uri)) {
        try {
            //String f = from.getAbsolutePath();
            //String path="Internal storage/app test/backup1.csv";
            //Toast.makeText(this,from.getAbsolutePath() , Toast.LENGTH_LONG).show();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

                String txt = "", mLine;
                String[] nextLine;
                while ((mLine = r.readLine()) != null) {
                    // nextLine[] is an array of values from the line
                    //txt+=nextLine[0]+" "+nextLine[1]+" "+nextLine[2]+" "+nextLine[3]+" "+nextLine[4]+" "+nextLine[5]+" "+nextLine[6]+"\n";

                    nextLine = mLine.split(",");
                    txt += nextLine[0] + " " + nextLine[1] + " " + nextLine[2] + " " + nextLine[3] + " " + nextLine[4] + " " + nextLine[5] + " " + nextLine[6] + "\n";
                    //txt+=mLine;
                }

                Toast.makeText(this, txt, Toast.LENGTH_LONG).show();


            } catch(Exception e){
                e.printStackTrace();
                //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Invalid file for import", Toast.LENGTH_SHORT).show();
        }
    }


    boolean isValidCSVForImport(Uri uri)
    {
        try{
        InputStream inputStream=getContentResolver().openInputStream(uri);
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

        String mLine;
        String[] nextLine;
            mLine = r.readLine();
            if(mLine==null)
                return false;
            nextLine=mLine.split(",");
            if(nextLine.length!=7)
            {
                return false;
            }

            while ((mLine = r.readLine()) != null) {
                nextLine=mLine.split(",");
                if(nextLine.length!=7)
                {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }




    public static String generateRandomPassword(int len)
    {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*(){}[]_-+";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }


}



class ExportCSV extends AsyncTask<Uri, Void, Boolean> {
    private final WeakReference<Context> context;

    ArrayList<Model> arraylist;
    ExportCSV(Context c, ArrayList<Model> arraylist) {
        context = new WeakReference<>(c);
        this.arraylist=arraylist;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        Uri uri = uris[0];
        Context c = context.get();

        if( c == null ) {
            return false;
        }

        String data = "id,title,website,username,password,notes,date\n";
        for(Model model:arraylist)
        {
            data+=model.key+","+model.title+","+model.website+","+model.username+","+model.password+","+model.notes+","+model.date+"\n";
        }
        boolean success = false;

        try {
            ParcelFileDescriptor pfd = c.getContentResolver().openFileDescriptor(uri, "w");
            if( pfd != null ) {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                fileOutputStream.write(data.getBytes());
                fileOutputStream.close();
                success = true;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}
