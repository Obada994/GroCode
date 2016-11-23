package com.example.johanringstrom.fragment_grocode;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by johanringstrom on 18/11/16.
 */
public class ItemsSubList extends android.app.Fragment {
    View myView;
    private android.widget.ListView ListView ;
    private static ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private android.widget.EditText EditText;
    String ListName;
    Connection con;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //ListName =getArguments().getString("ListName");
        myView = inflater.inflate(R.layout.items_layout, container, false);

        //Create connection object to get access to publish and subscribe
        con = new Connection(getActivity());

        //Create myList object to get accsess to its methods
        ShareLists myItems = new ShareLists();
        ListName = myItems.getListname();

        //List view to display list
        ListView = (ListView) myView.findViewById(R.id.listView);
        EditText = (EditText) myView.findViewById(R.id.editText);

        //Create a adapter to listview
        GroList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.checkitemrow, GroList);
        ListView.setAdapter(listAdapter);


        //Set what to do when a list item is clicked
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView text = (TextView) view;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }
        });

        //What to do when the add button is pressed
        final Button btnAdd = (Button) myView.findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // con.publish("addToSubList", ListName, EditText.getText().toString());
               // con.publish("getSubList", ListName);

            }
        });


        return myView;
    }
    //Gets listadapter
    public ArrayAdapter<String> getListAdapter(){
        return this.listAdapter;
    }
}
