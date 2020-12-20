/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventshareserver;


import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @author Miteto
 */
public class HttpThread extends Thread
{
    String fileName;
    String city;
    String place;
    
    public HttpThread(String fileName, String city, String place)
    {
        this.fileName = fileName;
        this.city = city;
        this.place = place;
    }
    @Override
    public void run()
    {
        String ip = "http://dpap.comxa.com/";
        try
        {
            HttpPost httppost;
            HttpClient httpclient;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(ip + "createLine.php?fileName=" + fileName.trim());
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String reponse = httpclient.execute(httppost,responseHandler);
            
            HttpPost httppost2;
            HttpClient httpclient2;
            httpclient2 = new DefaultHttpClient();
            httppost2 = new HttpPost(ip + "logging.php?fileName=" + fileName.trim() + "&city=" + city.trim() + "&place=" + place.trim());
            reponse = httpclient2.execute(httppost2, responseHandler);

        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
       
        
    }
    
}
