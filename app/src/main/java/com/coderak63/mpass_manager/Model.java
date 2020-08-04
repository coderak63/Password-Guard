package com.coderak63.mpass_manager;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Model {

    String title,website,username,password,notes,date;
    int key;

    public Model(int key,String title,String website,String uname,String pass,String notes,String date)
    {
        this.key=key;
        this.title=title;
        this.website=website;
        this.username=uname;
        this.password=pass;
        this.notes=notes;
        this.date=date;
    }



    public static Comparator<Model> TitleComparator = new Comparator<Model>() {
        @Override
        public int compare(Model o1, Model o2) {
            String t1=o1.title.toUpperCase();
            String t2=o2.title.toUpperCase();


          return t1.compareTo(t2);
        }
    };


    public static Comparator<Model> DateComparator = new Comparator<Model>() {
        @Override
        public int compare(Model o1, Model o2) {
            try {
                Date date1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault()).parse(o1.date);
                Date date2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault()).parse(o2.date);

                if(date1==null|date2==null)
                    return 0;

                    return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

        }
    };
}
