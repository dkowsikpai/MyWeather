package com.example.myweather;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;


    public class DownloadTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char temp = (char) data;
                    result += temp;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        message += main + ": " + description + "\r\n";

                    }

                }

                if (message != "") {

                    textView.setText(message);

                } else {

                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

                }


            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.weather);

    }

    public void submit(View view) {
        if (editText.getText().length() > 0) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            DownloadTask task = new DownloadTask();
            try {
                String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                task.execute("http://api.openweathermap.org/data/2.5/weather?appid=964414f2657438c0646be66181a672d9&q=" + encodedCityName);
                textView.setText("Loading...");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else  {
            Toast.makeText(getApplicationContext(), "Enter the location", Toast.LENGTH_LONG).show();
        }

    }

}
