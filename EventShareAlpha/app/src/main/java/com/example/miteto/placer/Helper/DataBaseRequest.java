package com.example.miteto.placer.Helper;

import android.content.Context;
import android.util.Log;

import com.example.miteto.placer.PlaceDTOArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Miteto on 04/05/2015.
 */
public class DataBaseRequest
{
    String ip = "http://dpap.comxa.com/";

    public void setLike (String fileName, String userName)
    {
            try
            {
                HttpPost httppost;
                HttpClient httpclient;
                httpclient = new DefaultHttpClient();
                httppost = new HttpPost(ip + "updateLike.php?fileName=" + fileName.trim() + "&userName=" + userName.trim());
                //"http://" + ip + "/myphpFiles/getOrders.php?outDate=" + date);
                //ResponseHandler<String> responseHandler = new BasicResponseHandler();
                //final String response = httpclient.execute(httppost, responseHandler);
                HttpResponse response = httpclient.execute(httppost);


            } catch (Exception e)
            {
                Log.i("ERROR : ", e.getMessage());
            }
    }

    public void getLikes(Context _context)
    {
        try
        {
            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(ip + "getLikes.php?");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            parseJSON(response, _context);

        } catch (Exception e)
        {
            Log.i("ERROR : ", e.getMessage());
        }
    }

    public void getLikesForUser(Context _context, String userName)
    {
        try
        {
            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(ip + "getLikesForUser.php?userName=" + userName);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            parseJSONLikesForUser(response, _context);

        } catch (Exception e)
        {
            Log.i("ERROR : ", e.getMessage());
        }
    }

    private void parseJSON(String result, Context _context) {
        try {
            JSONArray jArray = new JSONArray(result);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                ((PlaceDTOArray)_context.getApplicationContext()).setLikeForImage(json_data.getString("fileName") + ".jpg", json_data.getInt("imgLike"));
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private void parseJSONLikesForUser(String result, Context _context) {
        try {
            JSONArray jArray = new JSONArray(result);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                ((PlaceDTOArray)_context.getApplicationContext()).setIsLikedForImage(json_data.getString("fileName") + ".jpg");
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }


}
