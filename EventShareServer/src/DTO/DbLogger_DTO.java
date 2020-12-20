/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

/**
 *
 * @author Miteto
 */
public class DbLogger_DTO {
    
    private String fileName;
    private String city;
    private String place;
    
    public DbLogger_DTO()
    {
        
    }

    public DbLogger_DTO(String fileName, String city, String place) 
    {
        this.fileName = fileName;
        this.city = city;
        this.place = place;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getCity() 
    {
        return city;
    }

    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getPlace() 
    {
        return place;
    }

    public void setPlace(String place) 
    {
        this.place = place;
    }

    
    
    
    
}
