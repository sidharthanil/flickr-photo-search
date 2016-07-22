package com.sidzdev.flickrphotosearch.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sidharth on 12-07-2016.
 */
public class FlickrResponse {
    Photos photos;

    public List<Photo> getPhoto() {
        if(photos.getPhoto()!=null)
        return photos.getPhoto();
        return new ArrayList<Photo>();
    }


    private class Photos {
        List<Photo> photo;

        public List<Photo> getPhoto() {
            return photo;
        }
    }
}
