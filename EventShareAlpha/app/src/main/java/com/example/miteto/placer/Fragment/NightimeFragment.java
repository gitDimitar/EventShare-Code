package com.example.miteto.placer.Fragment;

/**
 * Created by Miteto on 12/04/2015.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.miteto.placer.Activity.PlaceChooserActivity;
import com.example.miteto.placer.Activity.PlaceImagesActivity;
import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;

import java.util.ArrayList;
import java.util.List;

public class NightimeFragment extends Fragment
{
    private List<PlaceDTO> placeDTOs;
    private ListView list;
    ProgressDialog progress;

    static final String KEY_PLACE = "place";
    static final String KEY_NAME = "name";
    static final String KEY_ICON = "icon";
    static final String KEY_TIME = "time";
    String cityName ;
    PlaceDTO place;
    UserDTO userDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading Images...");
        progress.setMessage("Loading Images...");
        progress.hide();
        /*
        FragmentManager fm = getSupportFragmentManager();
        logOutFragment = fm.findFragmentById(R.id.splashFragment1);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(logOutFragment);
        transaction.commit();
        **/
        placeDTOs = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("city") && intent.hasExtra("user"))
        {
            userDTO = intent.getParcelableExtra("user");
            cityName = intent.getStringExtra("city");
            getActivity().setTitle(PlaceChooserActivity.cityName);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_daytime, container, false);

        changeText();
        return rootView;
    }

    public void changeText(){
        for(PlaceDTO p : ((PlaceDTOArray)getActivity().getApplication()).getPlaces())
        {
            if(p.getAtPlace())
            {
                place = p;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        populateList();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        populateList();
        progress.hide();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        progress.hide();
        progress.dismiss();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        progress.hide();
        progress.dismiss();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        progress.hide();
        progress.dismiss();
    }


    private void populateList()
    {
        TextView cur =(TextView) getView().findViewById(R.id.placeId);
        if(place != null)
        {
            cur.setText(place.getName());
        }
        else
        {
            cur.setText("Unknown");
        }
        ArrayAdapter<PlaceDTO> adapter = new MyListAdapter();
        list = (ListView) getView().findViewById(R.id.place_ListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getActivity(), PlaceImagesActivity.class);
                i.putExtra("place", ((PlaceDTOArray)getActivity().getApplication()).getNighttimePlaces().get(position));
                i.putExtra("user", ((PlaceDTOArray)getActivity().getApplication()).getUser());
                i.putExtra("city", ((PlaceDTOArray)getActivity().getApplication()).getDaytimePlaces().get(position).getCity());
                startActivity(i);




            }
        });
    }



    private class MyListAdapter extends ArrayAdapter<PlaceDTO>
    {
        public MyListAdapter()
        {
            super(getActivity(), R.layout.item_view, ((PlaceDTOArray)getActivity().getApplication()).getNighttimePlaces());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemView = convertView;
            //make sure there is a view to work with
            if(itemView == null)
            {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //find place to work with
            PlaceDTO cur = ((PlaceDTOArray)getActivity().getApplication()).getNighttimePlaces().get(position);

            //fill the view
            ImageView imgView = (ImageView) itemView.findViewById(R.id.item_icon);
            imgView.setImageResource(cur.getIconId());

            TextView txtView = (TextView) itemView.findViewById(R.id.item_text);
            txtView.setText(cur.getName());

            return itemView;
        }

        @Override
        public boolean isEnabled(int position)
        {
            return true;
        }
    }
}

