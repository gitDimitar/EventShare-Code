package com.example.miteto.placer.Activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.miteto.placer.Adapters.FullScreenImageAdapter;
import com.example.miteto.placer.DTO.ImageDTO;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.Fragment.UploadDialogFragment;
import com.example.miteto.placer.Helper.Utils;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FullScreenImageActivity extends Activity implements UploadDialogFragment.UploadDialogListener
{
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;

    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    public static PlaceDTO place;
    private UserDTO userDTO;
    private static String absPath;
    private ProgressDialog progress;
    static final String [] nameArray = new String[] {"Current Name", "Anonymous"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        viewPager = (ViewPager) findViewById(R.id.pager);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading Images...");
        progress.setMessage("Loading Images...");

        utils = new Utils(getApplicationContext());
        setTitle("Image");

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        userDTO = i.getParcelableExtra("user");
        place = i.getParcelableExtra("place");
        Log.i("PLACE", place.getName());
        setTitle(place.getName() + " -> " + place.getCity());
        ArrayList<String> imagePaths = new ArrayList<String>();
        if(i.hasExtra("paths"))
        {
            imagePaths = i.getStringArrayListExtra("paths");
        }
        else
        {
            for (ImageDTO img : ((PlaceDTOArray) this.getApplication()).getImages())
            {
                imagePaths.add(img.getPath());
            }
        }

        adapter = new FullScreenImageAdapter(FullScreenImageActivity.this,
                imagePaths);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.action_camera)
        {
            if(((PlaceDTOArray)this.getApplication()).checkPlaceByName(place.getName()))
            {
                DialogFragment uploadDialog = new UploadDialogFragment();
                uploadDialog.show(getFragmentManager(),"UploadDialogFragment");
            }
            else
            {
                Toast.makeText(this, "You are not in " + place.getName(), Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        Log.i("OnActivityResult:", "INSIDE method");
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
        Log.i("captureImage:", "INSIDE");
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
        Log.i("GETOUTPUTMEDIAFILEURI:", "INSIDE");
        return Uri.fromFile(getOutputMediaFile(type, userDTO, personName));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type, UserDTO userDTO, String personName)
    {

        Log.i("getOutputMediaFile:", "getOutputMediaFile");
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
                Log.i("MEDEIAFILEPATH:", mediaFile.getAbsolutePath());
            }
            else
            {
                mediaFile = new File(mediaStorageDir, timeStamp + "_" + userDTO.getFname() + userDTO.getLname() + "_IMG" +  ".jpg");
                absPath = timeStamp + "_" + userDTO.getFname() + userDTO.getLname() + "_IMG" +  ".jpg";
                Log.i("MEDEIAFILEPATH:", mediaFile.getAbsolutePath());
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
        Log.i("CLICK CLICK", nameArray[i]);
        captureImage(nameArray[i]);

    }
}
