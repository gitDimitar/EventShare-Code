package com.example.miteto.placer.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.miteto.placer.Activity.FullScreenImageActivity;
import com.example.miteto.placer.Activity.PlaceImagesActivity;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Miteto on 30/03/2015.
 */
public class GridViewImageAdapter extends BaseAdapter
{
    //private Context mContext;
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;
    private Bitmap placeHolder;
    private PlaceDTO place;

    public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths, int imageWidth, PlaceDTO place)
    {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        this.place = place;
        placeHolder = BitmapFactory.decodeResource(_activity.getResources(), R.drawable.empty_photo);
    }

    public int getCount()
    {
        return this._filePaths.size();
    }

    public Object getItem(int position)
    {
        return this._filePaths.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public void loadBitmap(int pos, ImageView imageView)
    {

        if (cancelPotentialWork(_filePaths.get(pos), imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =  new AsyncDrawable(_activity.getResources(), placeHolder, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(_filePaths.get(pos));
        }


    }

    public static boolean cancelPotentialWork(String id, ImageView imageView)
    {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null)
        {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.equals("") || bitmapData != id)
            {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            }
            else
            {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;

        if (convertView == null)
        {
            imageView = new ImageView(_activity);
        }
        else
        {
            imageView = (ImageView) convertView;
        }

        loadBitmap(position, imageView);
        //Bitmap image = decodeFile(_filePaths.get(position), imageWidth,imageWidth);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
        //imageView.setImageBitmap(image);

        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));

        return imageView;
    }

    class OnImageClickListener implements OnClickListener
    {

        int position;

        // constructor
        public OnImageClickListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, FullScreenImageActivity.class);
            i.putExtra("position", position);
            i.putExtra("user",PlaceImagesActivity.userDTO);
            i.putExtra("place", place);
            i.putStringArrayListExtra("paths", _filePaths);
            _activity.startActivity(i);
        }

    }

    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT)
    {
        try
        {

            File f = new File(filePath);

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

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            return decodeFile(data, imageWidth, imageWidth );
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
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
}
