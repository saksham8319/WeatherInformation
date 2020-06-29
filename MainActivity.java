package com.example.weatherinformation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);

    }

    public void getWeather(View view) {

        try {
            WeatherInfo weatherInfo = new WeatherInfo();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            weatherInfo.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            //This will hide the keyboard after the user inputs text .
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();
        }
    }

    public class WeatherInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char currrent = (char) data;
                    result.append(currrent);
                    data = reader.read();
                }
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();
                return null;

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherinfo = jsonObject.getString("weather");
                Log.i("weather", weatherinfo);
                JSONArray arr = new JSONArray(weatherinfo);
                StringBuilder message = new StringBuilder();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message.append(main).append(" : ").append(description).append("\r\n");
                    }

                }
                if (!message.toString().equals("")) {
                    textView.setText(message.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}