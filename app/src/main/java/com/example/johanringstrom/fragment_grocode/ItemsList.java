package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by johanringstrom on 10/11/16.
 */
public class ItemsList extends Fragment{
    View myView;
    private ListView ListView ;
    private ListView ListView2 ;
    private static  ArrayAdapter<String> listAdapter ;
    private static  ArrayAdapter<String> listAdapterBought ;
    private ArrayList<String> GroList;
    private ArrayList<String> GroList2;
    private EditText EditText;
    private EditText editText;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String ListName;
    private static Object item;
    private Connection con;
    private ImageButton btnSpeak;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //ListName =getArguments().getString("ListName");
        myView = inflater.inflate(R.layout.itemslist_layout, container, false);
        setHasOptionsMenu(true);

        //Create connection object to get access to publish and subscribe
        con = new Connection(getActivity());
        btnSpeak = (ImageButton) myView.findViewById(R.id.btnSpeak);
        editText = (EditText) myView.findViewById(R.id.editText);
        EditText = (EditText) myView.findViewById(R.id.editText);
       /* EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

        EditText = (EditText) myView.findViewById(R.id.editText);
        EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                    con.publish("items", new String[]{"add",con.clientId,ListName, EditText.getText().toString()});
                    con.publish("items", new String[]{"fetch",con.clientId,ListName});
                    EditText.setText("");
                }
                return true;
            }
        });*/

        //Create myList object to get accsess to its methods
        MyLists myItems = new MyLists();
        ListName = myItems.getListname();


        //List view to display list
        ListView = (ListView) myView.findViewById(R.id.listView);
        ListView2 = (ListView) myView.findViewById(R.id.listView2);


        //Create a adapter to listview
        GroList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.simplerow, GroList);
        ListView.setAdapter(listAdapter);

        GroList2 = new ArrayList<>();
        listAdapterBought = new ArrayAdapter<>(getActivity(), R.layout.simplerow, GroList2);
        ListView2.setAdapter(listAdapterBought);


        //Set what to do when a list item is clicked
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object item = ListView.getItemAtPosition(position);
                    //text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                con.publish("items", new String[]{"setToBought",con.clientId,ListName, item.toString()});
                con.publish("items", new String[]{"fetch-bought",con.clientId,ListName});
                con.publish("items", new String[]{"fetch",con.clientId,ListName});
            }
        });

        ListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = ListView2.getItemAtPosition(position);
                /*TextView text = (TextView) view;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                con.publish("items", new String[]{"delete",con.clientId,ListName,item.toString() });
                con.publish("items", new String[]{"fetch-bought",con.clientId,ListName.toString()});
            }
        });

        //What to do when the add button is pressed
        final Button btnAdd = (Button) myView.findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                con.publish("items", new String[]{"add",con.clientId,ListName, EditText.getText().toString()});//add item
                //fetch the updated list
                con.publish("items", new String[]{"fetch",con.clientId,ListName});

                EditText.setText("");

            }
        });

        /*final Button bought = (Button) myView.findViewById(R.id.Baught);
        bought.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
               // con.publish("boughtItems", new String[]{"fetch-bought",con.clientId,ListName.toString()});

            }
        });*/


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });



        return myView;
    }
    //Gets listadapter
    public ArrayAdapter<String> getListAdapter(){
        return this.listAdapter;
    }
    public ArrayAdapter<String> getListAdapterBought(){
        return this.listAdapterBought;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.list, menu);
    }
}
