package com.example.weatherappi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView temperaturetext, descriptiontext, humidityamount, windspeedamount,citynametext;
    private EditText cityinput;
    private Button fetchdata;
    private ImageView weathericon;
    private static final String API_KEY = "YOUR_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weathericon = findViewById(R.id.image);

        temperaturetext = findViewById(R.id.temp);
        descriptiontext = findViewById(R.id.descriptiontext);
        humidityamount = findViewById(R.id.humidityamount);
        windspeedamount = findViewById(R.id.windamount);
        citynametext = findViewById(R.id.cityname);
        cityinput = findViewById(R.id.editTextText);
        fetchdata = findViewById(R.id.fetchcity);

        FetchWeatherdata("london");

       fetchdata.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String cityName = cityinput.getText().toString();
               if(!cityinput.getText().toString().isEmpty()){
                   FetchWeatherdata(cityName);
               }
               else {
                   cityinput.setError("Pls Enter a city name");
               }
           }
       });
    }

    private void FetchWeatherdata(String cityName) {
         String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+ "&appid=" +API_KEY + "&units=metric";
         ExecutorService executorService = Executors.newSingleThreadExecutor();
         executorService.execute(()->{
                     OkHttpClient client = new OkHttpClient();
                     Request request = new Request.Builder().url(url).build();
                     try {
                         Response response = client.newCall(request).execute();
                         String result = response.body().string();
                         runOnUiThread(() -> updateUI(result));
                     }catch (IOException e){
                         e.printStackTrace();
                     }
                 }

                 );
    }

    private void updateUI(String result) {
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main =  jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                weathericon.setImageResource(resId);

                citynametext.setText(jsonObject.getString("name"));
                temperaturetext.setText(String.format("%.0f°", temperature));
                humidityamount.setText(String.format("%.0f%%", humidity));
                windspeedamount.setText(String.format("%.0f km/h", windSpeed));
                descriptiontext.setText(description);


            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}