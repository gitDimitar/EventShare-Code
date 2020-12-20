package com.example.miteto.placer;

import android.app.Application;

import com.example.miteto.placer.DTO.ImageDTO;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;

import java.util.ArrayList;

/**
 * Created by Miteto on 24/04/2015.
 */
public class PlaceDTOArray extends Application
{
    private ArrayList<PlaceDTO> places;
    private ArrayList<ImageDTO> images;
    private UserDTO user;
    private String city;

    public void onCreate()
    {
        places = new ArrayList<PlaceDTO>();
        images = new ArrayList<ImageDTO>();
        user = new UserDTO();

    }
    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public UserDTO getUser()
    {
        return user;
    }

    public void setUser(UserDTO user)
    {
        this.user = user;
    }

    public void setPlaces(ArrayList<PlaceDTO> places)
    {
        this.places = places;
    }

    public ArrayList<PlaceDTO> getPlaces()
    {
        return places;
    }

    public void addPlace(PlaceDTO p)
    {
        places.add(p);

    }

    public PlaceDTO getPlace(String name)
    {
        for(PlaceDTO p : places)
        {
            if(p.getName().equals(name))
            {
                return p;
            }
        }

        return null;
    }

    public void removePlace(PlaceDTO p)
    {
        places.remove(p);
    }

    public boolean checkPlaceByName(String name)
    {
        for(PlaceDTO p: places)
        {
            if(p.getName().equals(name))
            {
                return p.getAtPlace();
            }
        }
        return false;
    }

    public ArrayList<PlaceDTO> getDaytimePlaces()
    {
        ArrayList<PlaceDTO> daytime = new ArrayList<PlaceDTO>();

        for(PlaceDTO p: places)
        {
            if(p.getTime().equals("day"))
            {
                daytime.add(p);
            }
        }
        return daytime;
    }

    public ArrayList<PlaceDTO> getNighttimePlaces()
    {
        ArrayList<PlaceDTO> nighttime = new ArrayList<PlaceDTO>();

        for(PlaceDTO p: places)
        {
            if(p.getTime().equals("night"))
            {
                nighttime.add(p);
            }
        }
        return nighttime;
    }

    public void clear()
    {
        places.clear();
    }

    public void setAtPlace(String place, String transition)
    {
        for(PlaceDTO p: places)
        {
            if(p.getName().equals(place))
            {
                if(transition.equals("Entered"))
                {
                    p.setAtPlace(true);
                }
                else
                {
                    p.setAtPlace(false);
                }
                break;
            }
        }

    }

/*
--------------------------------------------------------------------------------------------------

                                ImageDTOArray Methods

--------------------------------------------------------------------------------------------------
*/

    public void addImage(ImageDTO img)
    {
        images.add(img);
    }

    public void removeImage(ImageDTO img)
    {
        images.remove(img);
    }

    public ArrayList<ImageDTO> getImages()
    {
        return images;
    }

    public void setImages(ArrayList<ImageDTO> images)
    {
        this.images = images;
    }

    public void clearImages()
    {
        images.clear();
    }

    public String getPlaceName(int pos)
    {
        return images.get(pos).getPlaceName();
    }

    public ImageDTO getImageAtPos(int pos)
    {
        return images.get(pos);
    }

    public ImageDTO getImageByName(String name)
    {
        for(ImageDTO img : images)
        {
            if(img.getName().equals(name))
            {
                return img;
            }
        }
        return null;
    }

    public void setLikeForImage(String name, int likes)
    {
        for(ImageDTO img : images)
        {
            if(img.getName().equals(name))
            {
                img.setLikes(likes);
            }
        }
    }

    public void setIsLikedForImage(String name)
    {
        for(ImageDTO img : images)
        {
            if(img.getName().equals(name))
            {
                img.setUserLiked(true);
            }
        }
    }
}
