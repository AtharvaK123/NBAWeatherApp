package com.example.nbaweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lV;
    EditText eT;
    TextView tv, tv2, textQuote;
    Button search;
    ImageView home, mainImage;
    JSONObject son, son1;
    String s, s2, s3, s4;
    ArrayList<JSONObject> forecastDays;
    Context c;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eT = findViewById(R.id.editTextNumber);
        lV = findViewById(R.id.list_1);
        tv = findViewById(R.id.textView);
        search = findViewById(R.id.button);
        home = findViewById(R.id.imageView2);
        tv2 = findViewById(R.id.textView2);

        mainImage = findViewById(R.id.imageView4);
        textQuote = findViewById(R.id.textView3);

        lV.setVisibility(View.INVISIBLE);
        home.setVisibility(View.VISIBLE);
        tv.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        textQuote.setVisibility(View.INVISIBLE);

        c = this;



        eT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String zip = eT.getText().toString();
                        AsyncThread task = new AsyncThread();
                        task.execute(zip);

                        lV.setVisibility(View.VISIBLE);
                        home.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        textQuote.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class AsyncThread extends AsyncTask<String, Void, Void> {


        String result = "";
        @Override
        protected Void doInBackground(String...params) {
            forecastDays = new ArrayList<>();
            forecastDays.clear();

            URL url;
            URLConnection urlConnection = null;
                try {
                    url = new URL("https://api.openweathermap.org/geo/1.0/zip?zip=" + params[0] + "&appid=87f3478c431d8c39e77ea0016763f23f");

                    urlConnection = (URLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    BufferedReader bR = new BufferedReader(reader);
                    String input = null;

                    result = "";

                    while ((input = bR.readLine()) != null) {
                        result += input;

                    }
                    bR.close();
                    son = new JSONObject(result);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException | UnsupportedEncodingException | JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                try {
                    s = son.getString("lat");
                    s2 = son.getString("lon");
                    s3 = "\nLocation: " + son.getString("name");
                    s4 = son.getString("country");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //5-day Forecast
                StringBuffer result1 = new StringBuffer();
                URL url1;
                URLConnection urlConnection1 = null;

                final String[] setURL = new String[1];

                try {
                    url1 = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + s + "&lon=" + s2 + "&units=imperial&appid=87f3478c431d8c39e77ea0016763f23f");
                    urlConnection1 = (URLConnection) url1.openConnection();

                    Log.d("", url1.toString());
                    InputStream in = urlConnection1.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    BufferedReader bR = new BufferedReader(reader);
                    String input1;

                    while ((input1 = bR.readLine()) != null) {
                        result1.append(input1);

                    }
                    in.close();
                    son1 = new JSONObject(result1.toString());
                    JSONArray arr = son1.getJSONArray("list");
                    for (int i = 0; i < 40; i += 8) {
                        forecastDays.add(arr.getJSONObject(i));
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<JSONObject> im = new ArrayList<JSONObject>();
                try {
                    for (int i = 0; i < forecastDays.size(); i++) {
                        JSONObject ja = forecastDays.get(i);
                        im.add(ja.getJSONArray("weather").getJSONObject(0));
                    }
                    String currentImage = (im.get(0).getString("main")).toLowerCase();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setMainImage(currentImage);
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            tv.setText("Lat: " + s + "\t\tLong: " + s2 + "\n" + s3 + ", " + s4);

            CustomAdapter adapter = new CustomAdapter(c, R.layout.adapterlayout, forecastDays);
            lV.setAdapter(adapter);
            List<JSONObject> li = forecastDays;
            tv2.setText("Current Temperature: " + (li.get(0)).toString().substring(32, 37));


        }
    }
    public void setMainImage(String strang){
        Quotes q = new Quotes();

        if(strang.equals("rain")) {
            mainImage.setImageResource(R.drawable.rainy);
            textQuote.setText(q.getRain());
        }
        if(strang.equals("clouds")){
            mainImage.setImageResource(R.drawable.cloudy);
            textQuote.setText(q.getCloud());
        }
        if(strang.equals("thunderstorm")){
            mainImage.setImageResource(R.drawable.thunderstorm);
            textQuote.setText(q.getThunder());
        }
        if(strang.equals("snow")){
            mainImage.setImageResource(R.drawable.snowy);
            textQuote.setText(q.getSnow());
        }
        if(strang.equals("clear")){
            mainImage.setImageResource(R.drawable.sunny);
            textQuote.setText(q.getSun());
        }
        if(strang.equals("wind")){
            mainImage.setImageResource(R.drawable.windy);
            textQuote.setText(q.getWind());
        }
    }

}