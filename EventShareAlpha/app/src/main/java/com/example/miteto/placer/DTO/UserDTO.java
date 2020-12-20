package com.example.miteto.placer.DTO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Miteto on 28/03/2015.
 */
public class UserDTO implements Parcelable
{
    private String fName;
    private String lName;
    private String profilePic;

    public UserDTO(String fName,String lName, String profilePic)
    {
        this.fName = fName;
        this.lName = lName;
        this.profilePic = profilePic;
    }

    public UserDTO()
    {

    }

    public String getFname()
    {
        return fName;
    }

    public void setFname(String fName)
    {
        this.fName = fName;
    }

    public String getLname()
    {
        return lName;
    }

    public void setLname(String lName)
    {
        this.lName = lName;
    }

    public String getProfilePic()
    {
        return profilePic;
    }

    public void setProfilePic(String profilePic)
    {
        this.profilePic = profilePic;
    }

    public String getFullName()
    {
        return fName + lName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(profilePic);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    protected UserDTO(Parcel in)
    {
        this.fName = in.readString();
        this.lName = in.readString();
        this.profilePic = in.readString();
    }
}
