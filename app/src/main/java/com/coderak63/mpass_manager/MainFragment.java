package com.coderak63.mpass_manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;


public class MainFragment extends Fragment {

    ListView listView;
    //ArrayList<Model> arraylist = new ArrayList<Model>();
    ArrayList<Integer> userSelection = new ArrayList<Integer>();
    CustomListAdapter adapter;
    ActionMode mode_temp;

    Map<Integer,Integer> checkMap = new HashMap<Integer, Integer>();

    ArrayList<Model> arraylist=new ArrayList<Model>();
    ArrayList<Model> temp_arraylist;

    ArrayList<Model> universalArraylist = new ArrayList<Model>();

    SearchView searchView;


  View main_view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");

        //setHasOptionsMenu(true);


        main_view=view;



        /*
        arraylist.add(0,new Model(1,"Google","google.com","user@gmail.com","1234","note","13/05/2020"));
        arraylist.add(1,new Model(2,"Fb","google.com","user@gmail.com","1234","note","13/05/2020"));
        arraylist.add(2,new Model(3,"Bing","google.com","user@gmail.com","1234","note","13/05/2020"));
        arraylist.add(3,new Model(4,"Yahoo","google.com","user@gmail.com","1234","note","13/05/2020"));
        arraylist.add(4,new Model(5,"MNNIT","google.com","user@gmail.com","1234","note","13/05/2020"));
         */

        //DbHandler db = DbHandler.getInstance(getContext());
        //arraylist = db.get();


        listView = (ListView) getView().findViewById(R.id.listview);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(modeListener);

        /*
        DbHandler db = DbHandler.getInstance(getContext());
        temp_arraylist=db.get();
        arraylist.clear();
        arraylist.addAll(temp_arraylist);
        for(Model model:arraylist)
        {
            checkMap.put(model.key,0);
        }
         */
        adapter = new CustomListAdapter(getContext(), arraylist,checkMap);
        listView.setAdapter(adapter);

        /*
        universalArraylist.clear();
        universalArraylist.addAll(temp_arraylist);

         */





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Model temp = arraylist.get(position);

                //String all="Title: "+temp.title+"\nWebsite: "+temp.website+"\nUsername: "+temp.username+"\nPassword: "+temp.password+"\nNotes: "+temp.notes;

                //Toast.makeText(getApplicationContext(),all,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),ShowActivity.class);
                intent.putExtra("id",temp.key);
                intent.putExtra("title",temp.title);
                intent.putExtra("website",temp.website);
                intent.putExtra("username",temp.username);
                intent.putExtra("password",temp.password);
                intent.putExtra("notes",temp.notes);
                intent.putExtra("date",temp.date);
                startActivity(intent);

            }
        });




    }





    @Override
    public void onResume()
    {
        super.onResume();
        //Toast.makeText(getApplicationContext(),"on resume",Toast.LENGTH_LONG).show();

        /*
        DbHandler db = DbHandler.getInstance(getContext());
        temp_arraylist=db.get();
        arraylist.clear();
        arraylist.addAll(temp_arraylist);

        for(Model model:arraylist)
        {
            checkMap.put(model.key,0);
        }

        adapter.notifyDataSetChanged();
        //adapter = new CustomListAdapter(getContext(), arraylist,checkMap);
        //listView.setAdapter(adapter);

        universalArraylist.clear();
        universalArraylist.addAll(temp_arraylist);

         */


        new loadFromDB(getActivity(),adapter,arraylist,universalArraylist,checkMap,main_view).execute();



    }






    AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

           // Toast.makeText(getActivity(),"ID: "+id,Toast.LENGTH_LONG).show();




            Model temp = arraylist.get(position);
            if(checked)
            {
                if(!userSelection.contains(temp.key)) {
                    userSelection.add(temp.key);
                    //listView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                    checkMap.put((int)id,1);
                    adapter.notifyDataSetChanged();

                }
            }else{
                if(userSelection.contains(temp.key)) {
                    userSelection.remove(Integer.valueOf(temp.key));
                    //listView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    checkMap.put((int)id,0);
                    adapter.notifyDataSetChanged();
                }
            }



            mode.setTitle(userSelection.size()+"");
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_select_contextual_mode,menu);

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
                case R.id.delete:
                    mode_temp=mode;
                    AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to delete these "+userSelection.size()+" items?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //deleteSelected();
                                    ArrayList<Integer> tempUserSelection = new ArrayList<Integer>();
                                    tempUserSelection.addAll(userSelection);
                                    new deleleSelectedFromDB(getActivity(),adapter,tempUserSelection,arraylist,universalArraylist,checkMap,main_view).execute();
                                    mode_temp.finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                    return true;
                case R.id.select_all:
                    userSelection.clear();
                    /*
                    for(int i=0;i<arraylist.size();i++)
                    {
                        //listView.setItemChecked(i,true);
                        Model temp = arraylist.get(i);
                        if(!userSelection.contains(temp.key)) {
                            userSelection.add(temp.key);
                            //listView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                            checkMap.put(temp.key,1);
                            adapter.notifyDataSetChanged();
                        }

                    }

                     */
                    for(int i=0;i<listView.getAdapter().getCount();i++)
                    {
                        listView.setItemChecked(i,true);
                    }
                    mode.setTitle(userSelection.size()+"");
                    return true;
                default:
                    return false;
            }


        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            userSelection.clear();
           // refresh();

            for(int i=0;i<arraylist.size();i++)
            {
                Model temp = arraylist.get(i);
                    checkMap.put(temp.key,0);
                    adapter.notifyDataSetChanged();


            }

        }
    };



    /*
    void deleteSelected()
    {
        DbHandler db = DbHandler.getInstance(getContext());

        for(int key:userSelection)
        {
            db.delete(key);
        }
        refresh();
    }

    void refresh()
    {
        DbHandler db = DbHandler.getInstance(getContext());
        temp_arraylist=db.get();
        arraylist.clear();
        arraylist.addAll(temp_arraylist);

        for(Model model:arraylist)
        {
            checkMap.put(model.key,0);
        }
        adapter.notifyDataSetChanged();
        //adapter = new CustomListAdapter(getContext(), arraylist,checkMap);
        //listView.setAdapter(adapter);

        universalArraylist.clear();
        universalArraylist.addAll(temp_arraylist);
    }

     */


    void filter(String charText)
    {
        charText=charText.toLowerCase(Locale.getDefault());
        ArrayList<Model> tempList = new ArrayList<Model>();

        tempList.addAll(arraylist);
        arraylist.clear();


        if(charText.length()==0)
        {
            arraylist.addAll(tempList);
        }
        else{
            for(Model model:tempList)
            {
                if(model.title.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    arraylist.add(model);

                }
            }
        }

        adapter.notifyDataSetChanged();


    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*
                DbHandler db = DbHandler.getInstance(getContext());
                temp_arraylist=db.get();
                arraylist.clear();
                arraylist.addAll(temp_arraylist);

                 */
                arraylist.clear();
                arraylist.addAll(universalArraylist);
                if(TextUtils.isEmpty(newText))
                {
                    filter("");
                    listView.clearTextFilter();

                }else{
                    filter(newText);
                }
                return true;
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent aboutPage = new Intent(getActivity(),AboutActivity.class);
            startActivity(aboutPage);
            return true;
        }else if (id == R.id.settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }else if (id == R.id.action_search)
        {

        }



        return super.onOptionsItemSelected(item);
    }



    public boolean myOnKeyDown()
 {
     if(searchView!=null){
         if(!searchView.isIconified())
         {
             searchView.setIconified(true);
             return false;
         }
         else{
             return true;
         }
     }
     else
     {
         return true;
     }
 }

}



class loadFromDB extends AsyncTask<String, Void, Boolean> {

    Context context;
    ProgressDialog progressDialog;

    ArrayList<Model> arraylist,universalArraylist;
    ArrayList<Model> temp_arraylist;
    Map<Integer,Integer> checkMap;
    CustomListAdapter adapter;
    View main_view;
    LinearLayout linlaHeaderProgress;

    loadFromDB(Context c,CustomListAdapter adapter, ArrayList<Model> arraylist,ArrayList<Model> universalArraylist,Map<Integer,Integer> checkMap,View main_view) {

        this.context=c;
        this.arraylist=arraylist;
        this.universalArraylist=universalArraylist;
        this.checkMap=checkMap;
        this.adapter=adapter;
        this.main_view=main_view;

    }




    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

/*
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

 */


        linlaHeaderProgress = (LinearLayout)main_view.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);



    }

    @Override
    protected Boolean doInBackground(String... str) {


        if( context == null ) {
            return false;
        }


        boolean success = false;

        try {

            DbHandler db = DbHandler.getInstance(context);
            temp_arraylist=db.get();
            arraylist.clear();
            arraylist.addAll(temp_arraylist);

            for(Model model:arraylist)
            {
                checkMap.put(model.key,0);
            }


            universalArraylist.clear();
            universalArraylist.addAll(temp_arraylist);

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

        if(b)
        {
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context, "Error while loading Database", Toast.LENGTH_LONG).show();
        }
        //progressDialog.dismiss();

        linlaHeaderProgress.setVisibility(View.GONE);





    }
}


class deleleSelectedFromDB extends AsyncTask<String, Void, Boolean> {

    Context context;
    ArrayList<Integer> userSelection;

    ArrayList<Model> arraylist,universalArraylist;
    ArrayList<Model> temp_arraylist;
    Map<Integer,Integer> checkMap;
    CustomListAdapter adapter;
    View main_view;
    LinearLayout linlaHeaderProgress;

    deleleSelectedFromDB(Context c,CustomListAdapter adapter,ArrayList<Integer> userSelection, ArrayList<Model> arraylist,ArrayList<Model> universalArraylist,Map<Integer,Integer> checkMap,View main_view) {

        this.context=c;
        this.arraylist=arraylist;
        this.universalArraylist=universalArraylist;
        this.checkMap=checkMap;
        this.adapter=adapter;
        this.main_view=main_view;
        this.userSelection=userSelection;

    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        linlaHeaderProgress = (LinearLayout)main_view.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

    }

    @Override
    protected Boolean doInBackground(String... str) {


        if( context == null ) {
            return false;
        }


        boolean success = false;

        try {

            DbHandler db = DbHandler.getInstance(context);
            temp_arraylist=db.deleteSelected(userSelection);
            arraylist.clear();
            arraylist.addAll(temp_arraylist);

            for(Model model:arraylist)
            {
                checkMap.put(model.key,0);
            }


            universalArraylist.clear();
            universalArraylist.addAll(temp_arraylist);

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
        userSelection.clear();
        if(b)
        {
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context, "Error while loading Database", Toast.LENGTH_LONG).show();
        }
        //progressDialog.dismiss();

        linlaHeaderProgress.setVisibility(View.GONE);





    }
}



