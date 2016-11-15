package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by johanringstrom on 10/11/16.
 */
public class SecondFragmant extends Fragment{


    View myView;
    private ListView ListView ;
    private static  ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private EditText EditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.items_layout, container, false);

        //List view to display list
        ListView = (ListView) myView.findViewById(R.id.listView);
        EditText = (EditText) myView.findViewById(R.id.editText);

        //Create a adapter to listview

        GroList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.checkitemrow, GroList);
        ListView.setAdapter( listAdapter );



        //Set what to do when a list item is clicked
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

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
                listAdapter.add(EditText.getText().toString());
                EditText.setText("");

            }
        });


        return myView;


    }
}
