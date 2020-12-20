package com.example.miteto.placer.DTO;

import java.util.ArrayList;

/**
 * Created by Miteto on 29/04/2015.
 */
public class ImageDTO
{
    private String path;
    private String name;
    private String placeName;
    private int likes = 0;
    private boolean userLiked  = false;
    private ArrayList<String> comments;

    public ImageDTO(String path, String name, String placeName)
    {
        this.path = path;
        this.name = name;
        this.placeName = placeName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPlaceName()
    {
        return placeName;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public void setLikes(int likes)
    {
        this.likes = likes;
    }

    public int getLikes()
    {
        return likes;
    }

    public void modifyLikes()
    {
        if(userLiked)
        {
            likes--;
            userLiked = false;
        }
        else
        {
            likes++;
            userLiked = true;
        }
    }

    public void setUserLiked(boolean userLiked)
    {
        this.userLiked = userLiked;
    }

    public boolean getUserLiked()
    {
        return userLiked;
    }

}
