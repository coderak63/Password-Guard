package com.coderak63.mpass_manager;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Map;
import java.util.zip.Inflater;

public class CustomListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Model> arrayList;
    LayoutInflater inflter;
    Model model;
    Map<Integer,Integer> checkMap;

    public CustomListAdapter(Context applicationContext, ArrayList<Model> arrayList,Map<Integer,Integer> checkMap) {
        this.context = context;
        this.arrayList = arrayList;
        inflter = (LayoutInflater.from(applicationContext));
        this.checkMap=checkMap;
    }

    @Override
    public int getCount() {
        return arrayList.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        model=arrayList.get(position);
        return model.key;
        //return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_item, null);
        TextView title = (TextView) view.findViewById(R.id.txtTitle);
        TextView website = (TextView) view.findViewById(R.id.txtWebsite);
        TextView date = (TextView) view.findViewById(R.id.txtDate);
        ImageView icon = (ImageView) view.findViewById(R.id.txtImage);
        model=arrayList.get(i);
        title.setText(model.title);
        website.setText(model.website);


        /*
        long c = getItemId(i);
        int checkedState = checkMap.get((int)c);
        if(checkedState==1){
            view.setBackgroundColor(Color.LTGRAY);
        }else{
            view.setBackgroundColor(Color.TRANSPARENT);
        }




        char first='A';
        if(model.title.length()>0)
        first = model.title.toUpperCase().charAt(0);

        ColorGenerator gen = ColorGenerator.MATERIAL;
        int color = gen.getColor(first);
        TextDrawable drawable=TextDrawable.builder().buildRound(first+"",color);
        icon.setImageDrawable(drawable);


         */


        long c = getItemId(i);
        int checkedState = checkMap.get((int)c);
        if(checkedState==1){
            icon.setImageResource(R.drawable.ic_check);
        }else{
            char first='A';
            if(model.title.length()>0)
                first = model.title.toUpperCase().charAt(0);

            ColorGenerator gen = ColorGenerator.MATERIAL;
            int color = gen.getColor(first);
            TextDrawable drawable=TextDrawable.builder().buildRound(first+"",color);
            icon.setImageDrawable(drawable);
        }


        String d="unknown";
        try {
            String dd[] = model.date.split("-");
            //date.setText(dd[0] + " " + dd[1]);
            d=dd[0] + " " + dd[1];
        }catch (Exception e) {
            e.printStackTrace();

        }
        date.setText(d);

        return view;
    }

}










