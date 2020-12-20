/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventshareserver;

import DTO.PathHolder;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

/**
 *
 * @author Miteto
 */
public class ImageThread extends Thread {
    
    Socket client;
    int imgNum;
    
    public ImageThread(Socket client, int imgNum)
    {
        this.client = client;
        this.imgNum = imgNum;
    }
    
    public void run()
    {
        try
        {
            InputStream in = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //String opt = br.readLine();
          
            String opt;
            opt = br.readLine();//dis.readUTF();
            
            if(opt.equals("send"))
            {
                BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
                DataInputStream dis = new DataInputStream(bis);
            
                String city = dis.readUTF();
                
                String place = dis.readUTF();
                
                String fileName = dis.readUTF();
                
                long fileLength = dis.readLong();
                
                OutputStream out = new FileOutputStream("C:\\" + city + "\\" + place + "\\" + fileName);
                
                BufferedOutputStream bos = new BufferedOutputStream(out);
                
                for(int j = 0; j < fileLength; j++)
                {
                    bos.write(bis.read());
                }
                
                bos.close();
                dis.close();
               
                BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                img.createGraphics().drawImage(ImageIO.read(new File("C:\\" + city + "\\" + place + "\\" + fileName)).getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);
                File dir = new File("C:\\" + city + "\\" + place, "thumbs");
                if(!dir.exists())
                {
                    dir.mkdir();
                }
                ImageIO.write(img, "jpg", new File(dir + "\\" + fileName)); 
                HttpThread t = new HttpThread(fileName.substring(0, fileName.indexOf(".")), city, place);
                t.start();
            }
            else if(opt.equals("receive"))
            {
                
                String city  = br.readLine();// dis.readUTF();
                
                String place  = br.readLine();// dis.readUTF();

                File[] files = new File("C:\\" + city + "\\" + place).listFiles();
                int filesLength = 0;
                
                for(File f : files)
                {
                    if(!f.isDirectory())
                    {
                        filesLength++;
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeInt(filesLength);

                for(File file : files)
                {
                    if(file.isDirectory())
                    {
                        continue;
                    }
                    long length = file.length();
                    dos.writeLong(length);

                    String name = file.getName();
                    dos.writeUTF(name);

                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    int theByte = 0;
                    while((theByte = bis.read()) != -1) bos.write(theByte);

                    bis.close();
                }

                dos.close();
                //in.close();
                
            }
            else if(opt.equals("receiveThumbs"))
            {
                
                String city  = br.readLine();// dis.readUTF();
                
                String place  = br.readLine();// dis.readUTF();
                
                File dir = new File("C:\\" + city + "\\" + place, "thumbs");
                if(!dir.exists())
                {
                    dir.mkdir();
                }

                File[] files = new File("C:\\" + city + "\\" + place + "\\thumbs").listFiles();
                int filesLength = 0;
                
                for(File f : files)
                {
                    if(!f.isDirectory())
                    {
                        filesLength++;
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeInt(filesLength);

                for(File file : files)
                {
                    if(file.isDirectory())
                    {
                        continue;
                    }
                    long length = file.length();
                    dos.writeLong(length);

                    String name = file.getName();
                    dos.writeUTF(name);

                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    int theByte = 0;
                    while((theByte = bis.read()) != -1) bos.write(theByte);

                    bis.close();
                }

                dos.close();
                //in.close();
                //logDAO.logging(city, place);
                
            }
            else if(opt.equals("receiveAllThumbs"))
            {
                ArrayList<PathHolder> paths = new ArrayList<PathHolder>();
                String city  = br.readLine();// dis.readUTF();
                
                File dir = new File("C:\\" + city);
                if(!dir.exists())
                {
                    dir.mkdir();
                }

                File[] files = new File("C:\\" + city).listFiles();
                int filesLength = 0;
                System.out.println(city);               
                for(File f : files)
                {
                    if(f.isDirectory())
                    {
                        File[] thumbsFiles = new File(f.getPath() + "\\" + "thumbs").listFiles();
                        for(File ff : thumbsFiles)
                        {
                            if(!ff.isDirectory())
                            {
                                paths.add(new PathHolder(ff.getPath(),f.getName()));
                                filesLength++;
                            }
                        }                       
                    }
                }
                PathComparator comp = new PathComparator();
                Collections.sort(paths, comp);
                System.out.println(filesLength);
                BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);

                dos.writeInt(filesLength);
                
                for(PathHolder path : paths)
                {
                    File cur = new File(path.getPath());
                    
                    long length = cur.length();
                    dos.writeLong(length);

                    String name = cur.getName();
                    //System.out.println(name);
                    dos.writeUTF(name);

                    String placeName = path.getPlace();
                    //System.out.println(placeName);
                    dos.writeUTF(placeName);
                    
                    FileInputStream fis = new FileInputStream(cur);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    int theByte = 0;
                    while((theByte = bis.read()) != -1) bos.write(theByte);

                    bis.close();
                }
                dos.close();
                
            }
            else if(opt.equals("getByName"))
            {                
                String city  = br.readLine();// dis.readUTF();
                
                String place  = br.readLine();// dis.readUTF();
                
                String name  = br.readLine();

                File file = new File("C:\\" + city + "\\" + place + "\\" + name + ".jpg");
                if(!file.exists())
                {
                    File[] searchFiles = new File("C:\\" + city).listFiles();
                    for(File f : searchFiles)
                    {
                        if(f.isDirectory())
                        {
                            File[] imgs = f.listFiles();
                            for(File ff : imgs)
                            {
                                if(!ff.isDirectory())
                                {
                                    if(ff.getName().equals(name))
                                    {
                                        file = ff;
                                        break;
                                    }
                                }
                            }                       
                        }
                    }
                }
                

                BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);
            
                long length = file.length();
                dos.writeLong(length);

                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                int theByte = 0;
                while((theByte = bis.read()) != -1) bos.write(theByte);

                bis.close();
                dos.close();             
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
}
