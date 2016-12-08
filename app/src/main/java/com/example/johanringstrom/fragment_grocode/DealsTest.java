package com.example.johanringstrom.fragment_grocode;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by obada on 2016-12-07.
 */

public class DealsTest extends Fragment {
    View myView;
    private android.widget.ListView ListView ;
    private static ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private static Object list;

    private LocationManager locationManager;
    private LocationListener listener;
    private Connection con;
    private double latitude;
    private double longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.first_layout, container, false);


        //Creat connection object to get accsess to publish and subscribe
        con = new Connection(getActivity(),Connection.clientId);
        //List view to display list
        ListView = (ListView) myView.findViewById(R.id.listView);

        //Create a adapter to listview
        GroList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.simplerow, GroList);
        ListView.setAdapter(listAdapter);

        //................................................................................

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                JSONObject toSend=new JSONObject();
                JSONObject data=new JSONObject();
                try {
                    toSend.put("id",con.clientId);
                    data.put("longitude",longitude);
                    data.put("latitude",latitude);
                    data.put("filters","food");
                    toSend.put("data",data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    con.getClient().publish("deal/gogodeals/deal/fetch",new MqttMessage(toSend.toString().getBytes()));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},10);
            }
            locationManager.requestLocationUpdates("gps", 500, 0, listener);

        }

        return myView;
    }
    //Gets listadapter
    public ArrayAdapter<String> getListAdapter(){
        return this.listAdapter;
    }

    //Get listname
    public String getListname(){
        return this.list.toString();
    }
}
