/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventshareserver;

import DTO.PathHolder;
import java.util.Comparator;

/**
 *
 * @author Miteto
 */
public class PathComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) 
    {
        PathHolder path1 = (PathHolder) o1;
        PathHolder path2 = (PathHolder) o2;
        
        long num1 = Long.parseLong(path1.getPath().substring(path1.getPath().lastIndexOf("\\") + 1, path1.getPath().indexOf("_")));
        long num2 = Long.parseLong(path2.getPath().substring(path2.getPath().lastIndexOf("\\") + 1, path2.getPath().indexOf("_")));
        
        if((num1 - num2) > 0)
        {
            return -1;
        }
        else if((num1 - num2) < 0)
        {
            return 1;
        }
        else
        {
            return 0;
        }
        
    }
    
}
