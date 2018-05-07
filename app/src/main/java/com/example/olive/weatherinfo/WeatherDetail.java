package com.example.olive.weatherinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.olive.weatherinfo.data.City;
import com.example.olive.weatherinfo.data.weatherdata.WeatherResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDetail extends AppCompatActivity {

    private TextView cityName;
    private ImageView weatherIcon;
    private Button closeBtn;
    private TextView tempHi;
    private TextView tempLo;
    private TextView temp;
    private TextView humidity;
    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        String cityQuery = "";
        cityName = findViewById(R.id.cityName);
        closeBtn = findViewById(R.id.closeBtn);
        details = findViewById(R.id.description);
        weatherIcon = findViewById(R.id.weatherIcon);
        tempHi = findViewById(R.id.tempHi);
        tempLo = findViewById(R.id.tempLo);
        temp = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);

        if (getIntent().getSerializableExtra("city") != null) {
            final City city = (City) getIntent().getSerializableExtra("city");
            cityQuery = city.getCityName();

        }
        queryWeatherInfo(cityQuery);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void queryWeatherInfo(final String cityQuery) {
        MainActivity.weatherAPI.getCurrentWeather(MainActivity.apiKey, "metric", cityQuery).enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if (response.body() != null) {
                    String data =
                            ""+response.body().getWeather().get(0).getDescription();
                    details.setText(data);
                    cityName.setText(String.format("%s, %s", cityQuery, response.body().getSys().getCountry()));
                    temp.setText(String.format("%s (Celcius) ", response.body().getMain().getTemp()));
                    tempHi.setText(String.format("High: %s", response.body().getMain().getTempMax()));
                    tempLo.setText(String.format("Low: %s", response.body().getMain().getTempMin()));
                    humidity.setText(String.format("Humidity: %s", response.body().getMain().getHumidity()));
                    String imgURL = response.body().getWeather().get(0).getIcon();
                    Glide.with(WeatherDetail.this).load("http://openweathermap.org/img/w/" + imgURL +".png").into(weatherIcon);
                }
                else {
                    details.setText(R.string.invalid_city_error);
                }
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Toast.makeText(WeatherDetail.this, "Error: "+
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
