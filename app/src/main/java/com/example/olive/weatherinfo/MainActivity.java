package com.example.olive.weatherinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.olive.weatherinfo.adapter.CityAdapter;
import com.example.olive.weatherinfo.data.AppDatabase;
import com.example.olive.weatherinfo.data.City;
import com.example.olive.weatherinfo.network.WeatherAPI;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CreateCityDialog.CityHandler {

    private static final String apiVersion = "2.5";
    public static final String apiKey = "7ef7ec0d25874950106230565611730c";

    private final String URL_BASE =
            "http://api.openweathermap.org/data/" + apiVersion + "/";

    private CityAdapter cityAdapter;
    private CoordinatorLayout layoutContent;
    private DrawerLayout drawerLayout;
    public static WeatherAPI weatherAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);


        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePlaceDialog();
            }
        });
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        setUpNavigationView();
        setUpToolBar();
        setUpRetrofit();
        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewCities);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(this));
        initCities(recyclerViewPlaces);

    }

    private void setUpRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI = retrofit.create(WeatherAPI.class);
    }

    private void setUpNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.add_city:
                                showCreatePlaceDialog();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.about:
                                Toast.makeText(MainActivity.this, R.string.author, Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }
                        return false;
                    }
                });
    }

    private void initCities(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<City> cities =
                        AppDatabase.getAppDatabase(MainActivity.this).cityDao().getAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityAdapter = new CityAdapter(cities, MainActivity.this);
                        recyclerView.setAdapter(cityAdapter);
                    }
                });
            }
        }.start();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void showCreatePlaceDialog() {
        new CreateCityDialog().show(getSupportFragmentManager(), "CreateCityDialog");
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showCreatePlaceDialog();
                return true;
            default:
                showCreatePlaceDialog();
                return true;
        }
    }

    public void viewWeatherDetails(City city) {
        Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    @Override
    public void onNewPlaceCreated(final City city) {
        new Thread() {
            @Override
            public void run() {
                long id = AppDatabase.getAppDatabase(MainActivity.this).
                        cityDao().insertCity(city);
                city.setCityID(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityAdapter.addCity(city);
                    }
                });
            }
        }.start();

    }
}


