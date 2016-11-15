package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
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
public class FirstFragmant extends Fragment{

    View myView;
    private ListView ListView ;
    private static  ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private EditText EditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.first_layout, container, false);

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
                Object list = ListView.getItemAtPosition(position);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SecondFragmant()).commit();

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
