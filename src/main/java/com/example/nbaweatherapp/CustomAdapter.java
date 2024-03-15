package com.example.nbaweatherapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<JSONObject> {
    List<JSONObject> list;
    Context context;
    int xmlResource;
    String currentTemp;

    public CustomAdapter(Context context, int resource, List<JSONObject> objects) {
        super(context, resource, objects);
        xmlResource = resource;
        list = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);


        TextView date = adapterLayout.findViewById(R.id.Date);
        ImageView image = adapterLayout.findViewById(R.id.imageView);
        TextView des = adapterLayout.findViewById(R.id.Description);
        TextView minMax = adapterLayout.findViewById(R.id.Max);


        try {
            String saveTemp = "Min: " + getItem(position).getJSONObject("main").getString("temp_min");
            saveTemp += "\nMax: " + getItem(position).getJSONObject("main").getString("temp_max");

            String dateTime = getItem(position).getString("dt_txt");
            dateTime = dateTime.substring(5, 10).toString();

            currentTemp = "Current Temperature: " + getItem(0).getJSONObject("main").getString("temp");

            date.setText(dateTime);
            minMax.setText(saveTemp);

            List<JSONObject> j = new ArrayList<JSONObject>();
            for(int i=0; i<list.size(); i++){
                JSONObject ja = list.get(i);
                j.add(ja.getJSONArray("weather").getJSONObject(0));
            }
            String finalO = j.get(position).getString("description");
            des.setText(finalO);

            List<JSONObject> im = new ArrayList<JSONObject>();
            String imagee = "";
            for(int i=0; i<list.size(); i++){
                JSONObject ja = list.get(i);
                im.add(ja.getJSONArray("weather").getJSONObject(0));
            }
            imagee = (im.get(position).getString("main")).toLowerCase();

            String currentImage = (im.get(0).getString("main")).toLowerCase();

            Log.d("", currentImage);

            if(imagee.equals("rain"))
                image.setImageResource(R.drawable.rainy);
            if(imagee.equals("clouds"))
                image.setImageResource(R.drawable.cloudy);
            if(imagee.equals("thunderstorm"))
                image.setImageResource(R.drawable.thunderstorm);
            if(imagee.equals("snow"))
                image.setImageResource(R.drawable.snowy);
            if(imagee.equals("clear"))
                image.setImageResource(R.drawable.sunny);
            if(imagee.equals("Rain"))
                image.setImageResource(R.drawable.rainy);

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        return adapterLayout;
    }

}