package com.sidzdev.flickrphotosearch.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sidharth on 13-07-2016.
 */
public interface FlickrAPI {
    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrResponse> loadPhotos(@Query("api_key")String api_key, @Query("lat")String lat, @Query("lon")String lon);
}
