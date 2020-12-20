package com.example.miteto.placer.Helper;

/**
 * Created by Miteto on 31/03/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.miteto.placer.DTO.ImageDTO;
import com.example.miteto.placer.PlaceDTOArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Utils {

    private Context _context;

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths(String place) {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "util-place-imgs");

        // check for directory
        if (directory.isDirectory())
        {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles != null && listFiles.length > 0)
            {
                for (ImageDTO img : ((PlaceDTOArray)_context.getApplicationContext()).getImages())
                {
                    if(img.getPlaceName().equals(place))
                    {
                        filePaths.add(img.getPath());
                    }
                }
                /*
                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();

                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
                */
            }
            else
            {
                // image directory is empty
                Toast.makeText(
                        _context,
                        Constants.PHOTO_ALBUM
                                + " is empty or null. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(Constants.PHOTO_ALBUM
                    + " directory path is not valid! Please set the image directory name Constants.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        return filePaths;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (Constants.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }

    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        int screenHeight;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        screenHeight = point.y;
        return screenHeight;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static void writePhotoJpg(Bitmap data, String pathName)
    {
        File file = new File(pathName);
        try {
            if(file.exists())
            {
                file.delete();
            }
            file.createNewFile();
            // BufferedOutputStream os = new BufferedOutputStream(
            // new FileOutputStream(file));

            FileOutputStream os = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendImageToServer(final String path,final String city, final String place)
    {
        boolean rdy = false;
        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Socket server = null;
                try
                {
                    server = new Socket("84.203.3.186", 1111);

                    String absPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator + "eventshare" + File.separator + path;


                        //System.out.println(toSend.length());


                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap large = BitmapFactory.decodeFile(absPath, options);
                    Bitmap small = getResizedBitmap(large, 600,800);
                    writePhotoJpg(small,absPath);
                    File toSend = new File(absPath);


                        OutputStream outputStream = server.getOutputStream();
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));

                        BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
                        DataOutputStream dos = new DataOutputStream(bos);

                        pw.println("send");
                        pw.flush();

                        dos.writeUTF(city);

                        dos.writeUTF(place);

                        dos.writeUTF(toSend.getName());

                        dos.writeLong(toSend.length());

                        FileInputStream fis = new FileInputStream(toSend);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                    int theByte = 0;
                    while((theByte = bis.read()) != -1)
                    {
                        bos.write(theByte);
                    }

                      bis.close();
                      dos.close();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

        });
        t.start();
        while(t.isAlive())
        {
            rdy = false;
        }
        rdy = true;
        return rdy;
    }

    public  boolean getImagesForPlace(final String city, final String place)
    {
        boolean rdy =false;

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Socket server = null;

                try
                {

                    server = new Socket("84.203.3.186", 1111);


                    OutputStream outputStream = server.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));

                    BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);

                    pw.println("receiveThumbs");
                    pw.flush();

                    pw.println(city);
                    pw.flush();

                    pw.println(place);
                    pw.flush();



                    File imgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "util-place-imgs");
                    if(!imgDir.exists())
                    {

                        imgDir.mkdir();
                    }
                    else
                    {
                        if (imgDir.isDirectory() )
                        {
                            File[] contents = imgDir.listFiles();
                            if(contents != null)
                            {
                                String[] children = imgDir.list();
                                for (int i = 0; i < children.length; i++)
                                {
                                    new File(imgDir, children[i]).delete();
                                }
                            }
                        }
                    }
                    int filesCount = dis.readInt();
                    File[] files = new File[filesCount];
                    Log.i("SOCKET TRY", filesCount + "FILES READ");

                    for(int i = 0; i < filesCount; i++)
                    {
                        Log.i("SOCKET TRY", "getImageForPlace WRITING IMAGES");
                        long fileLength = dis.readLong();
                        String fileName = dis.readUTF();

                        files[i] = new File(imgDir, fileName);

                        FileOutputStream fos = new FileOutputStream(files[i]);
                        BufferedOutputStream boss = new BufferedOutputStream(fos);

                        for(int j = 0; j < fileLength; j++) boss.write(bis.read());

                            boss.close();
                    }

                    dis.close();
                    final boolean rdy = true;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        while(t.isAlive())
        {
            rdy = false;
        }
        rdy = true;

        return rdy;
    }

    public  boolean getImageByName(final String city, final String place, final String imgName)
    {
        boolean rdy =false;

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Socket server = null;

                try
                {

                    server = new Socket("84.203.3.186", 1111);


                    OutputStream outputStream = server.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));

                    BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);

                    pw.println("getByName");
                    pw.flush();

                    pw.println(city);
                    pw.flush();

                    pw.println(place);
                    pw.flush();

                    pw.println(imgName);
                    pw.flush();

                    File imgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "util-place-imgs");
                    if (!imgDir.exists())
                    {

                        imgDir.mkdir();
                    }


                    long fileLength = dis.readLong();

                    OutputStream out = new FileOutputStream(imgDir + File.separator + imgName + ".jpg", false);

                    BufferedOutputStream bos = new BufferedOutputStream(out);

                    for (int j = 0; j < fileLength; j++)
                    {
                        bos.write(bis.read());
                    }

                    bos.close();
                    dis.close();

                    final boolean rdy = true;
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        });
        t.start();

        while(t.isAlive())
        {
            rdy = false;
        }
        rdy = true;

        return rdy;
    }

    public  boolean getAllThumbs(final String city)
    {
        boolean rdy =false;

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Socket server = null;

                try
                {

                    server = new Socket("84.203.3.186", 1111);


                    OutputStream outputStream = server.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream));

                    BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);

                    pw.println("receiveAllThumbs");
                    pw.flush();

                    pw.println(city);
                    pw.flush();


                    File imgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "util-place-imgs");
                    if (!imgDir.exists())
                    {

                        imgDir.mkdir();
                    }
                    /*
                    else
                    {
                        if (imgDir.isDirectory())
                        {
                            File[] contents = imgDir.listFiles();
                            if (contents != null)
                            {
                                String[] children = imgDir.list();
                                for (int i = 0; i < children.length; i++)
                                {
                                    new File(imgDir, children[i]).delete();
                                }
                            }
                        }
                    }
                    */
                    int filesCount = dis.readInt();
                    Log.i("FILECOUNT", filesCount + "");
                    File[] files = new File[filesCount];

                    for (int i = 0; i < filesCount; i++)
                    {
                        long fileLength = dis.readLong();

                        String fileName = dis.readUTF();

                        String placeName = dis.readUTF();

                        files[i] = new File(imgDir, fileName);
                        ImageDTO img = new ImageDTO(files[i].getPath(),fileName,placeName);
                        ((PlaceDTOArray) _context.getApplicationContext()).addImage(img);

                        FileOutputStream fos = new FileOutputStream(files[i]);
                        BufferedOutputStream boss = new BufferedOutputStream(fos);

                        for (int j = 0; j < fileLength; j++) boss.write(bis.read());

                        boss.close();
                    }
                    getLikes();
                    dis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        t.start();

        while(t.isAlive())
        {
            rdy = false;
        }
        rdy = true;

        return rdy;
    }

    public void getLikes()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                DataBaseRequest dbRequest = new DataBaseRequest();
                dbRequest.getLikes(_context);
                dbRequest.getLikesForUser(_context, ((PlaceDTOArray)_context.getApplicationContext()).getUser().getFullName());
            }
        });
        t.start();
    }
}
