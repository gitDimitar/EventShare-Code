package com.example.miteto.placer.Activity;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miteto.placer.Adapters.TabPagerAdapter;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.Fragment.UploadDialogFragment;
import com.example.miteto.placer.Helper.Utils;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlaceChooserActivity extends FragmentActivity implements ActionBar.TabListener, UploadDialogFragment.UploadDialogListener
{

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Utils utils;
    private ProgressDialog progress;
    public static UserDTO userDTO;
    private static String absPath;
    private Bundle temp;

    private ViewPager viewPager;
    private TabPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Daytime", "Nightime", "ViewAll"};

    static final String [] nameArray = new String[] {"Current Name", "Anonymous"};


    public static String cityName ;
    public static PlaceDTO place;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_chooser);
        temp = savedInstanceState;

        progress = new ProgressDialog(this);
        progress.setMessage("Please wait");
        progress.setTitle("Uploading Image...");

        utils = new Utils(this);

        Intent intent = getIntent();
        if(intent.hasExtra("city") && intent.hasExtra("user"))
        {
            cityName = intent.getStringExtra("city");
            userDTO = intent.getParcelableExtra("user");
        }
        else
        {
            cityName = ((PlaceDTOArray)this.getApplication()).getCity();
            userDTO = ((PlaceDTOArray)this.getApplication()).getUser();
        }

        setTitle(cityName);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY );
        Log.i("HOUR", hour + "");
        if(hour >= 20 || hour <= 8)
        {
            viewPager.setCurrentItem(1);

        }
        else
        {
            viewPager.setCurrentItem(0);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_place_chooser, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.action_logout)
        {
            //Toast.makeText(this, "Log-out registers", Toast.LENGTH_LONG).show();
            logOut();
        }
        else if(id == R.id.action_search)
        {
            //TODO: Logic behind the search
            Toast.makeText(this, "Search", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut()
    {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("logout", "logout");
        startActivity(i);
    }

    public void camera(View view)
    {
        TextView cur =(TextView) findViewById(R.id.placeId);

        if(cur.getText().equals("Unknown"))
        {
            Toast.makeText(this, "Please go to a recognized place or check notifications", Toast.LENGTH_LONG).show();
        }
        else
        {
            place = ((PlaceDTOArray)getApplication()).getPlace(cur.getText().toString());
            if(place.getAtPlace())
            {
                DialogFragment uploadDialog = new UploadDialogFragment();
                uploadDialog.show(getFragmentManager(),"UploadDialogFragment");
            }
            else
            {
                Toast.makeText(this, "Somehow change the text on both fragments", Toast.LENGTH_LONG).show();
            }

        }
    }





    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                progress.show();
                Utils utils = new Utils(this);
                Log.i("PATH",absPath );
                Log.i("CITY",place.getCity() );
                Log.i("NAME",place.getName());
                boolean rdy = utils.sendImageToServer(absPath, place.getCity(), place.getName());

                if(rdy)
                {
                    progress.hide();
                    progress.dismiss();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture

                Intent i = new Intent(this,this.getClass());
                i.putExtra("city", place.getCity());
                i.putExtra("place", place);
                startActivity(i);

                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void captureImage(String personName)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, userDTO, personName);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

	/*
	 * Creating file uri to store image/video
	 */
    public Uri getOutputMediaFileUri(int type, UserDTO userDTO,String personName) {
        return Uri.fromFile(getOutputMediaFile(type, userDTO, personName));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type, UserDTO userDTO, String personName)
    {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"eventshare");


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyMMddHHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            if(personName.equals(nameArray[1]))
            {
                mediaFile = new File(mediaStorageDir, timeStamp + "_"+ nameArray[1] + "_IMG" + ".jpg");
                absPath = timeStamp + "_"+ nameArray[1] + "_IMG" + ".jpg";
            }
            else
            {
                mediaFile = new File(mediaStorageDir, timeStamp + "_" + userDTO.getFname() + userDTO.getLname() + "_IMG" +  ".jpg");
                absPath = timeStamp + "_" + userDTO.getFname() + userDTO.getLname() + "_IMG" +  ".jpg";
            }
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(Environment.getExternalStorageDirectory(), "VID_" + userDTO.getFname() + userDTO.getLname() + "_" + timeStamp + ".mp4");
        } else {
            return null;
        }


        return mediaFile;
    }

    @Override
    public void onClick(int i)
    {
        captureImage(nameArray[i]);
    }

}