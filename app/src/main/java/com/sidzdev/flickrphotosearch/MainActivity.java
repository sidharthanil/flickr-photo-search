package com.sidzdev.flickrphotosearch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sidzdev.flickrphotosearch.CustomAdapter.ImageAdapter;
import com.sidzdev.flickrphotosearch.Model.FlickrAPI;
import com.sidzdev.flickrphotosearch.Model.FlickrResponse;
import com.sidzdev.flickrphotosearch.Model.Photo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<FlickrResponse> {


    LocationManager locationManager;
    LocationListener locationListener;
    private TextView latitude;
    private TextView longitude;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private String api_key = "b6811f62c4187d5241e5f9cd82b5d960";
    private String lon;
    private String lat;
    private FlickrResponse photos;
    private ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        imageAdapter = new ImageAdapter(MainActivity.this,new ArrayList<Photo>());
        recyclerView.setAdapter(imageAdapter);


        locationManager =
                (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.flickr.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                FlickrAPI flickrAPI = retrofit.create(FlickrAPI.class);

                Call<FlickrResponse> photosCallback = flickrAPI.loadPhotos(api_key, lat, lon);
                photosCallback.enqueue(MainActivity.this);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String networkProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(networkProvider, 100000, 10000, locationListener);



    }



    @Override
    public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
        mProgress.setVisibility(View.GONE);
        imageAdapter = new ImageAdapter(MainActivity.this,response.body().getPhoto());
        recyclerView.setAdapter(imageAdapter);
        Log.e("tesr","code: "+response.code());
        Log.e("tesr","count: "+response.body().getPhoto().size());


    }

    @Override
    public void onFailure(Call<FlickrResponse> call, Throwable t) {
        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

    }
}