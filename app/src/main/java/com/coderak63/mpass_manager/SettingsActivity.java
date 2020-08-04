package com.coderak63.mpass_manager;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final int FILE_EXPORT_REQUEST_CODE = 12;

    private static final int  CHOOSE_CSV_FILE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");


        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flag=pref.getBoolean("key_allow_screenshot",true);
        if(!flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }




    public static class MainPreferenceFragment extends PreferenceFragment {
        FingerprintManager fingerprintManager;
        Boolean isFingerprintHardware=false;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.root_preferences);


            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);
                if(fingerprintManager!=null)
                isFingerprintHardware=fingerprintManager.isHardwareDetected();
            }

            // gallery EditText change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));

            // notification preference change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            Preference aboutApp = findPreference(getString(R.string.key_about_app));
            aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent aboutPage = new Intent(getActivity(),AboutActivity.class);
                    startActivity(aboutPage);
                    return true;
                }
            });

            Preference importCSV = findPreference(getString(R.string.key_import_csv));
            importCSV.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(Intent.createChooser(intent, "Open CSV"), CHOOSE_CSV_FILE);
                    return true;
                }
            });

            Preference exportCSV = findPreference(getString(R.string.key_export_csv));
            exportCSV.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy_hh:mm:ss", new Locale("en", "IN"));
                    String date = sdf.format(new Date());
                    String filename="mPass_export_"+date+".csv";

                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_TITLE,filename);
                    intent.setType("text/csv");
                    startActivityForResult(intent,FILE_EXPORT_REQUEST_CODE);
                    return true;
                }
            });


            Preference changePass = findPreference(getString(R.string.key_change_passcode));
            changePass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent changePass = new Intent(getActivity(),ChangePasswordActivity.class);
                    startActivity(changePass);
                    return true;
                }
            });


            Preference use_fingerprint = findPreference(getString(R.string.key_use_fingerprint));
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if(!isFingerprintHardware)
                {
                    use_fingerprint.setEnabled(false);
                    use_fingerprint.setShouldDisableView(true);
                    use_fingerprint.setSummary("Your device does not support fingerprint unlock");
                }
            }else{
                use_fingerprint.setEnabled(false);
                use_fingerprint.setShouldDisableView(true);
                use_fingerprint.setSummary("Your device does not support fingerprint unlock");

            }

        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_OK)
                return;

            switch (requestCode) {
                case FILE_EXPORT_REQUEST_CODE:
                   // Toast.makeText(getActivity(), "export", Toast.LENGTH_LONG).show();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            DbHandler db = DbHandler.getInstance(getActivity());
                            ArrayList<Model> arraylist=db.get();
                            new ExportToCSV(getActivity(),arraylist).execute(uri);
                        }
                    }
                    break;

                case CHOOSE_CSV_FILE:
                    //Toast.makeText(getActivity(), "import", Toast.LENGTH_LONG).show();
                    if(data.getData().getPath()!=null) {
                        //Toast.makeText(this, "import", Toast.LENGTH_LONG).show();
                        importCSV(data.getData());
                    }

                    break;
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
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

                    String mLine;
                    String[] nextLine;

                    ArrayList<Model> arr = new ArrayList<Model>();
                    r.readLine();

                    while ((mLine = r.readLine()) != null) {
                        // nextLine[] is an array of values from the line

                        nextLine = mLine.split(",");
                        //txt += nextLine[0] + " " + nextLine[1] + " " + nextLine[2] + " " + nextLine[3] + " " + nextLine[4] + " " + nextLine[5] + " " + nextLine[6] + "\n";

                        Model m =  new Model(0,nextLine[1],nextLine[2],nextLine[3],nextLine[4],nextLine[5],nextLine[6]);
                        arr.add(m);

                    }

                    //Toast.makeText(getActivity(), txt, Toast.LENGTH_LONG).show();
                    new insertImportedData(getActivity(),arr).execute();


                } catch(Exception e){
                    e.printStackTrace();
                    //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getActivity(), "Invalid file for import", Toast.LENGTH_SHORT).show();
            }
        }


        boolean isValidCSVForImport(Uri uri)
        {
            try{
                InputStream inputStream=getActivity().getContentResolver().openInputStream(uri);
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
                Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();


            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(R.string.summary_choose_ringtone);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_gallery_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:coderak63@gmail.com"));
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"abhishekrajauli@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query about Password Guard");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }


}




class ExportToCSV extends AsyncTask<Uri, Void, Boolean> {
    private final WeakReference<Context> context;

    ProgressDialog pd;
    Context con;

    ArrayList<Model> arraylist;
    ExportToCSV(Context c, ArrayList<Model> arraylist) {
        this.con=c;
        context = new WeakReference<>(c);
        this.arraylist=arraylist;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        /*
        pd = new ProgressDialog(con);
        pd.setCancelable(false);
        pd.setMessage("exporting...");
        pd.setTitle("Export CSV file");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

         */
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

    @Override
    protected void onPostExecute(Boolean b)
    {
        //pd.dismiss();
        if(b)
        {
            NotificationHelper not = new NotificationHelper(con);
            not.createNotification("Export CSV file","Successfully exported CSV file");
        }else{
            NotificationHelper not = new NotificationHelper(con);
            not.createNotification("Export CSV file","Export failed");
        }
    }
}



class insertImportedData extends AsyncTask<String, Void, Boolean> {

    Context context;
    ProgressDialog pd;

    ArrayList<Model> arraylist;
    insertImportedData(Context c, ArrayList<Model> arraylist) {

        this.context=c;
        this.arraylist=arraylist;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("importing...");
        pd.setTitle("Import CSV file");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }

    @Override
    protected Boolean doInBackground(String... str) {


        if( context == null ) {
            return false;
        }
        Collections.reverse(arraylist);



        boolean success = false;

        try {


            DbHandler db = DbHandler.getInstance(context);
            db.importToDatabase(arraylist);

                success = true;

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean b)
    {
        pd.dismiss();
        if(b)
        {
            NotificationHelper not = new NotificationHelper(context);
            not.createNotification("Import CSV file","Successfully imported CSV file");
        }else{
            NotificationHelper not = new NotificationHelper(context);
            not.createNotification("Import CSV file","Import failed");
        }
    }
}
