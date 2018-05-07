package com.example.olive.weatherinfo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.olive.weatherinfo.MainActivity;
import com.example.olive.weatherinfo.R;
import com.example.olive.weatherinfo.data.AppDatabase;
import com.example.olive.weatherinfo.data.City;
import com.example.olive.weatherinfo.data.weatherdata.WeatherResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by olive on 5/6/18.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder>{
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvCity;
        public TextView tvTemp;
        public ImageButton deleteBtn;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.btnDelete);
            tvTemp = (TextView) itemView.findViewById(R.id.tempDisplay);
            this.itemView = itemView;
        }

        public View getItemView () {
            return itemView;
        }
    }

    private List<City> citiesList;
    private Context context;
    private int lastPosition = -1;

    public CityAdapter(List<City> citiesList, Context context) {
        this.citiesList = citiesList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.city_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        String cityName = citiesList.get(position).getCityName();
        viewHolder.tvCity.setText(citiesList.get(position).getCityName());
        MainActivity.weatherAPI.getCurrentWeather(MainActivity.apiKey, "metric", cityName).enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if (response.body() != null) {
                    String imgURL = response.body().getWeather().get(0).getIcon();
                    viewHolder.tvTemp.setText(String.format("%s (C) ", response.body().getMain().getTemp()));
                    Glide.with(context).load("http://openweathermap.org/img/w/" + imgURL +".png").into(viewHolder.ivIcon);
                }
                else {
                    viewHolder.tvTemp.setText("No information found :(");
                }
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                //
            }
        });

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCity(viewHolder.getAdapterPosition());
                 }
    });
        viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).viewWeatherDetails(citiesList.get(viewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {

            return citiesList.size();
    }

    public void addCity(City city) {
        citiesList.add(city);
        notifyDataSetChanged();
    }

    public void removeCity(int position) {
        final City cityToDelete = citiesList.get(position);
        citiesList.remove(cityToDelete);
        notifyItemRemoved(position);
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).cityDao().delete(
                        cityToDelete);
            }
        }.start();
    }

}
