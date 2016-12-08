package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;


public class Deals extends Fragment {

    private android.widget.ListView ListView ;
    private static ArrayAdapter<String> listAdapter;
    ArrayList<String> gogoDeals;

    private View view;
    private Button b;
    private LocationManager locationManager;
    private LocationListener listener;
    Connection con;
    double longitude,latitude;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_deals, container, false);

        //Creat connection object to get accsess to publish and subscribe
        con = new Connection(getActivity(),Connection.clientId);
        con.subscribeToDeals();

        ListView = (ListView) view.findViewById(R.id.dealsList);
        gogoDeals = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.simplerow, gogoDeals);
        ListView.setAdapter(listAdapter);


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewz) {

                startReceivingLocationUpdates();

                Location update = new Location("");
                update.setLatitude(latitude);
                update.setLongitude(longitude);

                listener.onLocationChanged(update);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        b = (Button) getActivity().findViewById(R.id.button);

        b.setText("update");
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
                startReceivingLocationUpdates();
            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }
    //Gets listadapter
    public ArrayAdapter<String> getListAdapter(){
        return listAdapter;
    }

    private void startReceivingLocationUpdates() {

        if (locationManager == null) {

            locationManager = (android.location.LocationManager)
                    getActivity().getSystemService(Context.LOCATION_SERVICE);

        }

        if (locationManager != null) {

            try {

                locationManager.requestLocationUpdates(

                        android.location.LocationManager.NETWORK_PROVIDER,
                        0,
                        0,
                        listener);

            }
            catch (SecurityException ex)
            {
                Log.i(TAG, "fail to request location update, ignore", ex);

            }

            catch (IllegalArgumentException ex)
            {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }

            try {

                locationManager.requestLocationUpdates(

                        android.location.LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        listener);
            }
            catch (SecurityException ex) {

                Log.i(TAG, "fail to request location update, ignore", ex); }

            catch (IllegalArgumentException ex) {

                Log.d(TAG, "provider does not exist " + ex.getMessage());  }

            Log.d(TAG, "startReceivingLocationUpdates");
        }
    }
}
