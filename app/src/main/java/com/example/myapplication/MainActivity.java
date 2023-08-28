package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    private RelativeLayout Home;
    private ProgressBar PBLoading;
    private ImageView themeImg, searchImg, Icon;
    private TextView TVCityName, Temperature, conditionTV;
    private TextInputEditText EdtCity;
   // public  GPSTracker gpsTracker;
    private RecyclerView RVWeather;
    private WeatherRVAdapter weatherRVAdapter;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    double lat, lon;
    private LocationManager locationManager;
    private Location location;
    private int PERMISSION_CODE = 1;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        //gpsTracker = new GPSTracker(this);

        Home = findViewById(R.id.idHome);
        PBLoading = findViewById(R.id.idPBLoading);
        themeImg = findViewById(R.id.idthemeImg);
        searchImg = findViewById(R.id.idIVSearch);
        Icon = findViewById(R.id.idIVIcon);
        TVCityName = findViewById(R.id.idTVCityName);
        Temperature = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        EdtCity = findViewById(R.id.idEDTCity);
        RVWeather = findViewById(R.id.idRVWeather);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        RVWeather.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(LocationManager.NETWORK_PROVIDER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Toast.makeText(this,"Fetching Last known location",Toast.LENGTH_SHORT).show();
            lat = location.getLatitude();
            lon = location.getLongitude();
            cityName = getCityName(lat, lon);
            Log.d("TAGweather", "cityName: " + cityName);
            getWeatherInfo(cityName);
        }
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = EdtCity.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                } else {
                    TVCityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private String getCityName(double lat,double lon) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(lat, lon, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=96809032fde84e07ac574717232308&q=" + cityName + "&days=1&aqi=no&alerts=no";
        TVCityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        Log.d("TAGweather", "url: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(JSONObject response) {
                PBLoading.setVisibility(View.GONE);
                Home.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    Temperature.setText(temperature + "°c");
                    int isDay = response.getJSONObject(("current")).getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(Icon);
                    conditionTV.setText(condition);
                    if (isDay == 1) {
                        Picasso.get().load("https://img.freepik.com/free-photo/sun-sunlight-bright-outdoor-sky_1127-2391.jpg?w=1060&t=st=1693208696~exp=1693209296~hmac=aa08418f7f426d72023622331f9c25150a3ee9ed87bf17867ef3847790663c31").into(themeImg);
                    } else {
                        Picasso.get().load("https://img.freepik.com/free-photo/storm-clouds_1122-2748.jpg?w=1060&t=st=1693208669~exp=1693209269~hmac=a14e46896169c2c719091d3ca377bb0873d44f1fe7219ca8f86129262dc94a16").into(themeImg);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind));
                    }

                    weatherRVAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}