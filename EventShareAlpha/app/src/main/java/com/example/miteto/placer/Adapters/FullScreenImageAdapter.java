package com.example.miteto.placer.Adapters;

/**
 * Created by Miteto on 31/03/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.miteto.placer.Activity.FullScreenImageActivity;
import com.example.miteto.placer.DTO.ImageDTO;
import com.example.miteto.placer.Helper.TouchImageView;
import com.example.miteto.placer.Helper.Utils;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FullScreenImageAdapter extends PagerAdapter {

    private static Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private Utils utils;
    private Bitmap placeHolder;

    // constructor
    public FullScreenImageAdapter(Activity activity, ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.utils = new Utils(_activity);
        placeHolder = BitmapFactory.decodeResource(_activity.getResources(), R.drawable.img_placeholder_dark);
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;
        TextView userText;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        userText = (TextView) viewLayout.findViewById(R.id.userText);


        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        //imgDisplay.setImageBitmap(bitmap);
        loadBitmap(position, imgDisplay, userText);

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    public void loadBitmap(int pos, ImageView imageView, TextView userText)
    {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, userText);
            final AsyncDrawable asyncDrawable =  new AsyncDrawable(_activity.getResources(), placeHolder, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(_imagePaths.get(pos));



    }

    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT, final TextView userText)
    {
        try
        {

            File f = new File(filePath);
            String rawName = f.getName();
            final String name = rawName.substring(rawName.indexOf("_")+ 1,  rawName.lastIndexOf("_"));
            _activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    userText.setText(name);
                    //stuff that updates ui

                }
            });


            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";
        private TextView userText;

        public BitmapWorkerTask(ImageView imageView,TextView userText)
        {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.userText = userText;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Log.i("ImagePath", data);
            String imgName = data.substring(data.lastIndexOf("/") +1 , data.indexOf("."));
            Log.i("ImagePath", imgName);
            ImageDTO img = ((PlaceDTOArray)_activity.getApplication()).getImageByName(imgName + ".jpg");
            utils.getImageByName(FullScreenImageActivity.place.getCity(), img.getPlaceName() ,imgName);

            return decodeFile(data, utils.getScreenWidth(), utils.getScreenHeight(), userText );
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                //final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable
    {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =  new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}

