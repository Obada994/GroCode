package com.example.johanringstrom.fragment_grocode;

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
public class ShareLists extends Fragment{

    View myView;
    private ListView ListView ;
    private static  ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private EditText EditText;
    Connection con;
    private MqttAndroidClient client;
    private static Object list;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.first_layout, container, false);

        //Creat connection object to get accsess to publish and subscribe
        con = new Connection(getActivity());

        //List view to display list
            ListView = (ListView) myView.findViewById(R.id.listView);
            EditText = (EditText) myView.findViewById(R.id.editText);

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
                list = ListView.getItemAtPosition(position);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ItemsSubList()).commit();
//                con.publish("getSubList", list.toString(), "Test");

            }
        });

        //What to do when the add button is pressed
        final Button btnAdd = (Button) myView.findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



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
