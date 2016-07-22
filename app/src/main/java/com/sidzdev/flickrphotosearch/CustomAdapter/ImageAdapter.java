package com.sidzdev.flickrphotosearch.CustomAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sidzdev.flickrphotosearch.Model.Photo;
import com.sidzdev.flickrphotosearch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sidharth on 13-07-2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewholder> {

    private Activity activity;
    private List<Photo> photo = new ArrayList<Photo>();


    public class MyViewholder extends RecyclerView.ViewHolder {


        public final ImageView flickrPIC;

        public MyViewholder(View itemView) {
            super(itemView);
            flickrPIC = (ImageView) itemView.findViewById(R.id.flickrPIC);
        }
    }

    public ImageAdapter(Activity activity, List<Photo> photos1) {
        this.activity = activity;
        this.photo = photos1;
    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_grid, parent, false);


        return new MyViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewholder holder, final int position) {
        String id = photo.get(position).getId();
        String secret = photo.get(position).getSecret();
        String server = photo.get(position).getServer();
        String farm = photo.get(position).getFarm();

        final String url = "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";

        Glide.with(activity).load(url).into(holder.flickrPIC);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.custom_dialog);
                final ImageView img = (ImageView) dialog.findViewById(R.id.dialogImage);
                Glide.with(activity).load(url).into(img);

                Button btn = (Button) dialog.findViewById(R.id.setWallButton);
                Log.e("url", url);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final WallpaperManager myWallpaperManager = WallpaperManager
                                .getInstance(activity);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap myBitmap;
                                Point size = new Point();

                                int Measuredwidth = 0;
                                int Measuredheight = 0;

                                WindowManager w = activity.getWindowManager();
                                w.getDefaultDisplay().getSize(size);
                                Measuredwidth = size.x;
                                Measuredheight = size.y;

                                try {
                                    myBitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                                    myWallpaperManager.setBitmap(myBitmap);

//                                    myWallpaperManager.setBitmap(Bitmap.createScaledBitmap(myBitmap, 2 * Measuredwidth, Measuredheight, true));

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Wallpaper Set", Toast.LENGTH_LONG).show();
                                            Log.e("Wall", "Set as wallpaper");
                                            dialog.dismiss();
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();


                    }
                });

                Button dwnldBtn = (Button) dialog.findViewById(R.id.downloadButton);
                dwnldBtn.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        Log.e("download btn click", "clicked");
                        String root = (Environment.getExternalStorageDirectory() + "/Pictures");
                        Log.e("storage", root);
                        final File myDir = new File(root + "/PhotoSearch");
                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }

                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap myBitmap1;
                                try {
                                    myBitmap1 = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean success = false;
                                            try {


                                                OutputStream fOut = null;
                                                Random random = new Random();
                                                int randomVal = random.nextInt(100000);

                                                File file = new File(myDir, "mypic" + randomVal+".png");
                                                file.createNewFile();
                                                fOut = new FileOutputStream(file);

                                                myBitmap1.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                                fOut.flush();
                                                fOut.close();
                                                activity.sendBroadcast (
                                                        new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                                Uri.parse( "file://" + file.getAbsolutePath() ) )
                                                );

                                                success = true;
                                            } catch (Exception e) {

                                                e.printStackTrace();
                                            }

                                            if (success) {
                                                Toast.makeText(activity, "Image saved with success",
                                                        Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(activity,
                                                        "Error during image saving", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })).start();


                    }
                });
                dialog.show();


            }

        });


    }

    @Override
    public int getItemCount() {
        return photo.size();
    }


}
