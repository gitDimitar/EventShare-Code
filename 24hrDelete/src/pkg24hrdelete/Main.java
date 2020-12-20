/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg24hrdelete;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Miteto
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void deleteFiles(File f)
    {
        try
                {
                    FileUtils.cleanDirectory(f);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        File[] dir = new File("C:\\Dundalk").listFiles();
        for(File f : dir)
        {
            if(f.isDirectory())
            {
                deleteFiles(f);
                
            }
        }
    }
}
