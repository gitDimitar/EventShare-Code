package com.example.miteto.placer.DTO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Miteto on 25/03/2015.
 */
public class PlaceDTO implements Parcelable
{
    private int iconId;
    private String name;
    private String time;
    private String city;
    private boolean atPlace = false;
    private double lat;
    private double lon;
    private float radius;

    public PlaceDTO()
    {
        super();
    }

    public PlaceDTO(int iconId, String name, String time, String city ,double lat, double lon, float radius)
    {
        this.iconId = iconId;
        this.name = name;
        this.time = time;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }

    public int getIconId()
    {
        return iconId;
    }

    public void setIconId(int iconId)
    {
        this.iconId = iconId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public boolean getAtPlace()
    {
        return atPlace;
    }

    public void setAtPlace(boolean atPlace)
    {
        this.atPlace = atPlace;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(iconId);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(city);
        dest.writeByte((byte) (atPlace ? 1 : 0));
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeFloat(radius);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PlaceDTO> CREATOR = new Parcelable.Creator<PlaceDTO>() {
        @Override
        public PlaceDTO createFromParcel(Parcel in) {
            return new PlaceDTO(in);
        }

        @Override
        public PlaceDTO[] newArray(int size) {
            return new PlaceDTO[size];
        }
    };

    protected PlaceDTO(Parcel in)
    {
        this.iconId = in.readInt();
        this.name = in.readString();
        this.time = in.readString();
        this.city = in.readString();
        this.atPlace = in.readByte() != 0;
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.radius = in.readFloat();
    }
}
