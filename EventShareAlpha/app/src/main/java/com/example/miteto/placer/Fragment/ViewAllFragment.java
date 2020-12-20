package com.example.miteto.placer.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.miteto.placer.Activity.FullScreenImageActivity;
import com.example.miteto.placer.DTO.ImageDTO;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.Helper.DataBaseRequest;
import com.example.miteto.placer.Helper.Utils;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Miteto on 29/04/2015.
 */
public class ViewAllFragment extends Fragment
{

    private ProgressDialog progress;
    private UserDTO userDTO;
    private String cityName;
    private PlaceDTO place;
    private ListView list;
    private Bitmap placeHolder;
    public static Utils utils;
    private ArrayList<String> _filePaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading Images...");
        progress.setMessage("Loading Images...");
        progress.hide();
        _filePaths = new ArrayList<String>();
        /*
        FragmentManager fm = getSupportFragmentManager();
        logOutFragment = fm.findFragmentById(R.id.splashFragment1);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(logOutFragment);
        transaction.commit();
        **/

        placeHolder = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img_placeholder_dark);

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("city") && intent.hasExtra("user"))
        {
            userDTO = intent.getParcelableExtra("user");
            cityName = intent.getStringExtra("city");

        }
        else
        {
            userDTO = ((PlaceDTOArray)getActivity().getApplication()).getUser();
            cityName = ((PlaceDTOArray)getActivity().getApplication()).getPlaces().get(0).getCity();
        }

        utils = new Utils(this.getActivity());
        boolean rdy = utils.getAllThumbs(cityName);
        if(rdy)
        {
            // loading all image paths from SD card
            for(ImageDTO img : ((PlaceDTOArray)getActivity().getApplication()).getImages())
            {
                _filePaths.add(img.getPath());
            }
            //progress.dismiss();
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_daytime, container, false);

        changeText();
        return rootView;
    }

    public void changeText(){
        //this textview should be bound in the fragment onCreate as a member variable
        for(PlaceDTO p : ((PlaceDTOArray)getActivity().getApplication()).getPlaces())
        {

            if(p.getAtPlace())
            {
                place = p;
            }

        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        for(ImageDTO img : ((PlaceDTOArray)getActivity().getApplication()).getImages())
        {
            _filePaths.add(img.getPath());
        }
        populateList();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //populateList();
        progress.hide();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        progress.hide();
        progress.dismiss();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        progress.hide();
        progress.dismiss();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        progress.hide();
        progress.dismiss();
    }


    private void populateList()
    {
        TextView cur =(TextView) getView().findViewById(R.id.placeId);
        for(ImageDTO img : ((PlaceDTOArray)getActivity().getApplication()).getImages())
        {
            _filePaths.add(img.getPath());
        }
        if(place != null)
        {
            cur.setText(place.getName());
        }
        else
        {
            cur.setText("Unknown");
        }
        ArrayAdapter<ImageDTO> adapter = new MyListAdapter();
        list = (ListView) getView().findViewById(R.id.place_ListView);
        list.setAdapter(adapter);

        /*
        list.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount)
            {
                LinearLayout ll = (LinearLayout) getView().findViewById(R.id.topBarId);
                if (firstVisibleItem == 0)
                {
                    ll.setVisibility(View.VISIBLE);
                    list.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
                }
                else
                {
                    ll.setVisibility(View.GONE);
                    list.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT ));
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }
        });
         */
    }

/*
--------------------------------------------------------------------------------------------------------------------

                                        BITMAP HANDLING CODE

---------------------------------------------------------------------------------------------------------------------
*/

    public void loadBitmap(int pos, ImageView imageView)
    {
            Log.i("POS", pos +"");
            Log.i("STRING", _filePaths.get(pos));
        if (cancelPotentialWork(_filePaths.get(pos), imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =  new AsyncDrawable(getActivity().getResources(), placeHolder, task);
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

    public static Bitmap decodeFile(String filePath)
    {
        try
        {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = utils.getScreenWidth();
            final int REQUIRED_HIGHT = utils.getScreenHeight() /2;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale  >= REQUIRED_HIGHT)
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
            return decodeFile(data);
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



/*
--------------------------------------------------------------------------------------------------------------------

                                LIST ADAPTER AND ONCLICKLISTENER

---------------------------------------------------------------------------------------------------------------------
 */
    private class MyListAdapter extends ArrayAdapter<ImageDTO>
    {
        public MyListAdapter()
        {
            super(getActivity(), R.layout.item_large_view, ((PlaceDTOArray)getActivity().getApplication()).getImages());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemView = convertView;
            //make sure there is a view to work with
            if(itemView == null)
            {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_large_view, parent, false);
            }

            //find place to work with
            ImageDTO cur = ((PlaceDTOArray)getActivity().getApplication()).getImageAtPos(position);
            //_filePaths.add(cur.getPath());
            //fill the view
            ImageView imgView = (ImageView) itemView.findViewById(R.id.viewAllImage);
            loadBitmap(position, imgView);
            imgView.setOnClickListener(new OnImageClickListener(position));

            TextView txtView = (TextView) itemView.findViewById(R.id.viewAllName);
            txtView.setTypeface(null, Typeface.BOLD);
            final String rawName = cur.getName();
            final String name = rawName.substring(rawName.indexOf("_")+ 1,  rawName.lastIndexOf("_"));
            txtView.setText(name + " -> " + cur.getPlaceName());

            final TextView likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            likeCount.setText(String.valueOf(cur.getLikes()));

            final ImageView likeButton = (ImageView) itemView.findViewById(R.id.likeButton);
            if(((PlaceDTOArray) getActivity().getApplication()).getImageByName(rawName).getUserLiked())
            {
                likeButton.setImageResource(R.drawable.ic_action_liked);
            }
            else
            {
                likeButton.setImageResource(R.drawable.ic_action_like);
            }
            likeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Thread t = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            DataBaseRequest dbReq = new DataBaseRequest();
                            ((PlaceDTOArray)getActivity().getApplication()).getImageByName(rawName).modifyLikes();

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    likeCount.setText(String.valueOf(((PlaceDTOArray) getActivity().getApplication()).getImageByName(rawName).getLikes()));
                                    if(((PlaceDTOArray) getActivity().getApplication()).getImageByName(rawName).getUserLiked())
                                    {
                                        likeButton.setImageResource(R.drawable.ic_action_liked);
                                    }
                                    else
                                    {
                                        likeButton.setImageResource(R.drawable.ic_action_like);
                                    }
                                }
                            });

                            dbReq.setLike(rawName.substring(0, rawName.indexOf(".")), ((PlaceDTOArray) getActivity().getApplication()).getUser().getFullName());

                        }
                    });
                    t.start();
                }
            });

            return itemView;
        }

        @Override
        public boolean isEnabled(int position)
        {
            return true;
        }
    }

    class OnImageClickListener implements View.OnClickListener
    {

        int position;

        // constructor
        public OnImageClickListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting view image
            // launch full screen activity
            Intent i = new Intent(getActivity(), FullScreenImageActivity.class);
            i.putExtra("position", position);
            i.putExtra("user",userDTO);
            String pName = ((PlaceDTOArray) getActivity().getApplication()).getPlaceName(position);
            PlaceDTO p = ((PlaceDTOArray) getActivity().getApplication()).getPlace(pName);
            i.putExtra("place", p);

            startActivity(i);
        }

    }
}
