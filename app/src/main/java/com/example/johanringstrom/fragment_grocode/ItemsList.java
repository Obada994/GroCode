package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static  ArrayAdapter<String> listAdapter ;
    ArrayList<String> GroList;
    private EditText EditText;
    String ListName;
    Connection con;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView voiceText;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //ListName =getArguments().getString("ListName");
        myView = inflater.inflate(R.layout.first_layout, container, false);

        //Create connection object to get access to publish and subscribe
        con = new Connection(getActivity());

        //Create myList object to get accsess to its methods
        MyLists myItems = new MyLists();
        ListName = myItems.getListname();

        //List view to display list
        btnSpeak = (ImageButton) myView.findViewById(R.id.btnSpeak);
        editText = (EditText) myView.findViewById(R.id.editText);
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
                con.publish("add", ListName, EditText.getText().toString());
                con.publish("fetch", ListName,"Nothing");
                EditText.setText("");

            }
        });

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

}