package com.example.johanringstrom.fragment_grocode;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;

/**
 * Created by johanringstrom on 10/11/16.
 */
public class Notifications extends Fragment{


    View myView;
    private ListView ListView ;
    private static  ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    Connection con;
    private MqttAndroidClient client;
    private static Object list;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.sharelist_layout, container, false);
        setHasOptionsMenu(true);


        //Creat connection object to get accsess to publish and subscribe
        con = new Connection(getActivity());

        //List view to display list
        ListView = (ListView) myView.findViewById(R.id.listView);

        //Create a adapter to listview
        GroList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.simplerow, GroList);
        ListView.setAdapter( listAdapter );


        //Set what to do when a list item is clicked
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final Dialog dialog = new Dialog(getActivity(),R.style.AppTheme_Dark_Dialog);
                dialog.setContentView(R.layout.notification_dialog);
                dialog.setTitle("Notification Dialog");
                list = ListView.getItemAtPosition(position);
                android.app.FragmentManager fragmentManager = getFragmentManager();

                Button btnAccept = (Button) dialog.findViewById(R.id.Accept);
                Button btnReject = (Button) dialog.findViewById(R.id.Decline);

                dialog.show();


                btnAccept.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        con.publish("items", new String[]{"confirmShare",con.clientId,list.toString()});
                        Toast.makeText(getActivity(),"List Accepted",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                btnReject.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        con.publish("items", new String[]{"reject-invite",con.clientId,list.toString()});
                        Toast.makeText(getActivity(),"List Declined",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });




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
