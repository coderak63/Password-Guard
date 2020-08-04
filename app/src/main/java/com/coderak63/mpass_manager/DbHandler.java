package com.coderak63.mpass_manager;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
/*
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 */
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.coderak63.mpass_manager.MainActivity.PASS_PHRASE;

public class DbHandler extends SQLiteOpenHelper {
    Boolean openedReadable=false;


    private static DbHandler sInstance;
    Context context;

    //private String PASS_PHRASE;
    //SharedPreferences sp;


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "passwordsdb";
    private static final String TABLE_Passwords = "passworddetails";
    private static final String KEY_ID = "id";
    private static final String TITLE = "title";
    private static final String WEBSITE = "website";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NOTES = "notes";
    private static final String DATE = "date";


    public static synchronized DbHandler getInstance(Context context)
    {
        if(sInstance==null)
        {
            sInstance=new DbHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
        this.context=context;


    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Passwords + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT,"
                + WEBSITE + " TEXT,"
                + USERNAME + " TEXT,"
                + PASSWORD + " TEXT,"
                + NOTES + " TEXT,"
                + DATE + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Passwords);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //


    void insert(String title, String website, String username, String password, String notes, String date){
        //Get the Data Repository in write mode


        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(TITLE, title);
        cValues.put(WEBSITE, website);
        cValues.put(USERNAME, username);
        cValues.put(PASSWORD, password);
        cValues.put(NOTES, notes);
        cValues.put(DATE, date);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Passwords,null, cValues);

        //Toast.makeText(context,"insert db: "+newRowId+"",Toast.LENGTH_LONG).show();
        db.close();
    }

    public ArrayList<Model> get(){

        SQLiteDatabase db = this.getReadableDatabase(PASS_PHRASE);
        //openedReadable=true;
        ArrayList<Model> passList = new ArrayList<Model>();
        String query = "SELECT * FROM "+ TABLE_Passwords;
        Cursor cursor = db.rawQuery(query,null);
        //Toast.makeText(context,"get db: "+cursor.getCount()+"",Toast.LENGTH_LONG).show();

        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){

            int id=cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String title=cursor.getString(cursor.getColumnIndex(TITLE));
            String website=cursor.getString(cursor.getColumnIndex(WEBSITE));
            String username=cursor.getString(cursor.getColumnIndex(USERNAME));
            String password=cursor.getString(cursor.getColumnIndex(PASSWORD));
            String notes=cursor.getString(cursor.getColumnIndex(NOTES));
            String date=cursor.getString(cursor.getColumnIndex(DATE));

            //test+=id+" "+title+" "+website+" "+username+" "+password+" "+notes+" "+date+"\n";

            Model temp_model = new Model(id,title,website,username,password,notes,date);
            passList.add(temp_model);
            cursor.moveToNext();
        }

        //openedReadable=false;
        db.close();

        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String value=pref.getString("key_sort","0");
        if(value.equals("0"))
        {
            Collections.reverse(passList);
        }else if(value.equals("1"))
        {
            Collections.sort(passList,Model.DateComparator);
            Collections.reverse(passList);
        }else if(value.equals("2"))
        {
            Collections.sort(passList,Model.TitleComparator);
        }else{
            Collections.reverse(passList);
        }

        return  passList;
    }

    /*
    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_Users;
        Cursor cursor = db.query(TABLE_Users, new String[]{KEY_NAME, KEY_LOC, KEY_DESG}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("designation",cursor.getString(cursor.getColumnIndex(KEY_DESG)));
            user.put("location",cursor.getString(cursor.getColumnIndex(KEY_LOC)));
            userList.add(user);
        }
        return  userList;
    }

     */



    public void delete(int id){

        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);
        db.delete(TABLE_Passwords, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        db.close();
    }


    public int update(int id,String title, String website, String username, String password, String notes, String date){

        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);
        ContentValues cValues = new ContentValues();
        cValues.put(TITLE, title);
        cValues.put(WEBSITE, website);
        cValues.put(USERNAME, username);
        cValues.put(PASSWORD, password);
        cValues.put(NOTES, notes);
        cValues.put(DATE, date);
        int count = db.update(TABLE_Passwords, cValues, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        //Toast.makeText(context,"update db: "+count+"",Toast.LENGTH_LONG).show();
        db.close();
        return  count;
    }


    public void deleteAll(){

        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);
        db.execSQL("DELETE FROM "+TABLE_Passwords);
        db.close();
    }

/*
    public synchronized void close()
    {
        if(!openedReadable)
            close();
    }




    public void changePassPhrase(String old_pass, String new_pass)
    {
        String PRAGMA_REKEY = String.format("PRAGMA rekey = \"%s\";",new_pass);
        SQLiteDatabase db = this.getWritableDatabase(old_pass);
        db.rawExecSQL(PRAGMA_REKEY);
        db.close();
    }

 */

    public void importToDatabase(ArrayList<Model> arr)
    {
        //Toast.makeText(context, PASS_PHRASE, Toast.LENGTH_LONG).show();
        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);
        //Create a new map of values, where column names are the keys


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", new Locale("en", "IN"));
        String date;
        for(Model model : arr) {
            date = sdf.format(new Date());
            ContentValues cValues = new ContentValues();
            cValues.put(TITLE, model.title);
            cValues.put(WEBSITE, model.website);
            cValues.put(USERNAME, model.username);
            cValues.put(PASSWORD, model.password);
            cValues.put(NOTES, model.notes);
            cValues.put(DATE, date);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TABLE_Passwords, null, cValues);


            //Toast.makeText(context,"insert db: "+newRowId+"",Toast.LENGTH_LONG).show();
        }
        db.close();
    }



    public ArrayList<Model> deleteSelected(ArrayList<Integer> userSelection)
    {

        SQLiteDatabase db = this.getWritableDatabase(PASS_PHRASE);

        for(int id:userSelection)
        {
            db.delete(TABLE_Passwords, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        }

        ArrayList<Model> passList = new ArrayList<Model>();
        String query = "SELECT * FROM "+ TABLE_Passwords;
        Cursor cursor = db.rawQuery(query,null);
        //Toast.makeText(context,"get db: "+cursor.getCount()+"",Toast.LENGTH_LONG).show();

        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){

            int id=cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String title=cursor.getString(cursor.getColumnIndex(TITLE));
            String website=cursor.getString(cursor.getColumnIndex(WEBSITE));
            String username=cursor.getString(cursor.getColumnIndex(USERNAME));
            String password=cursor.getString(cursor.getColumnIndex(PASSWORD));
            String notes=cursor.getString(cursor.getColumnIndex(NOTES));
            String date=cursor.getString(cursor.getColumnIndex(DATE));

            //test+=id+" "+title+" "+website+" "+username+" "+password+" "+notes+" "+date+"\n";

            Model temp_model = new Model(id,title,website,username,password,notes,date);
            passList.add(temp_model);
            cursor.moveToNext();
        }

        //openedReadable=false;
        db.close();

        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String value=pref.getString("key_sort","0");
        if(value.equals("0"))
        {
            Collections.reverse(passList);
        }else if(value.equals("1"))
        {
            Collections.sort(passList,Model.DateComparator);
            Collections.reverse(passList);
        }else if(value.equals("2"))
        {
            Collections.sort(passList,Model.TitleComparator);
        }else{
            Collections.reverse(passList);
        }

        return  passList;
    }

}