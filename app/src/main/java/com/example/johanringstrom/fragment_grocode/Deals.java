package com.example.johanringstrom.fragment_grocode;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;


public class Deals extends Fragment {

    private android.widget.ListView ListView ;
    static ArrayAdapter<String> listAdapter;
    //dialog to show when you click on an offer
    private Dialog dialog;
    private TextView name;
    private TextView price;
    private TextView description;
    private ImageView image;

    private Button b;
    private LocationManager locationManager;
    private LocationListener listener;
    Connection con;
    double longitude,latitude;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_deals, container, false);

        //Creates a listview to show the deals in. The adapter wraps the data displayed in the lis.
        ListView = (ListView) view.findViewById(R.id.listView);
        ArrayList<String> gogoDeals = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.simplerow, gogoDeals);
        ListView.setAdapter(listAdapter);
        listAdapter.add("no available deals :(");

        //Creat connection object to get accsess to publish and subscribe
        con = new Connection(getActivity(),Connection.clientId);
        //subscribe to the deals topic
        con.subscribeToDeals();

        //Creates a dialog(popup) to put the information of a dealsobject in.
        dialog = new Dialog(getActivity(),R.style.AppTheme_Dark_Dialog);
        dialog.setContentView(R.layout.deal_dialog);
        dialog.setTitle("Deal information");
        name = (TextView) dialog.findViewById(R.id.nameText);
        price = (TextView) dialog.findViewById(R.id.priceText);
        description = (TextView) dialog.findViewById(R.id.descriptionText);
        image = (ImageView) dialog.findViewById(R.id.dealImage);

        //Set action listeners
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object item = ListView.getItemAtPosition(position);
                DealsObjects tmp=null;
                if(DealsObjects.list.size()!=0)
                    //find the deal object that matches the name we clicked on
                    tmp = DealsObjects.findByName(item.toString());
                //if we have no deals
                if(tmp==null)
                    //create this "deal"
                    tmp = new DealsObjects(new String[]{"PLEASE CLICK UPDATE","OR MOVE YOUR ASS TO A NEW LOCATION"," "},null);
                name.setText("Name: "+item.toString()+" ");
                price.setText("Price: "+tmp.getPrice()+" SEK ");
                description.setText("Description: "+tmp.getDescription()+" ");
                image.setImageBitmap(tmp.getImage());
                dialog.show();
            }
        });

        return view;
    }

    @Override
    //OnRequestPermissionsResult is the callback for the result from requesting permissions for using some
    // type of resource. In this case the location service.
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

                //send this location as a test if we have no deals in our location
                Location update = new Location("");
                update.setLatitude(57.7071734);
                update.setLongitude(11.9391119);

                listener.onLocationChanged(update);
            }
        });
    }

    @Override
    //init button listener, and the location manager
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
                JSONArray arr = new JSONArray();
                JSONObject arrObj = new JSONObject();
                try {
                    toSend.put("id",con.clientId);
                    data.put("longitude",longitude);
                    data.put("latitude",latitude);
                    arrObj.put("filter","food");
                    arr.put(0,arrObj);
                    data.put("filters",arr);
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
            }
        };

        configure_button();
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
