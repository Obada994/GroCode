package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by johanringstrom on 10/11/16.
 */
public class DealsTest extends Fragment{
    View myView;
    private ExpandableHeightListView mListView, mListView2;
    private static  ArrayAdapter<String> mListAdapter ;
    private static  ArrayAdapter<String> mListAdapterBought ;
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
        EditText = (EditText) myView.findViewById(R.id.editText);



        EditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. Handles what happens when you click enter on keyboard and
                        // enter on soft keyboard on phones, emulator.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            con.publish("items", new String[]{"add",con.clientId,ListName, EditText.getText().toString()});
                            con.publish("items", new String[]{"fetch",con.clientId,ListName});
                            EditText.setText("");
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });


        //Create myList object to get accsess to its methods
        MyLists myItems = new MyLists();
        ListName = myItems.getListname();
        getActivity().setTitle(myItems.getListname());


        //List view to display list
        final ExpandableHeightListView mListView = (ExpandableHeightListView) myView.findViewById(R.id.listView);
        final ExpandableHeightListView mListView2 = (ExpandableHeightListView) myView.findViewById(R.id.listView2);
        //mListView = (ListView) myView.findViewById(R.id.listView);
        //mListView2 = (ListView) myView.findViewById(R.id.listView2);

        //Create a adapter to listview
        GroList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(getActivity(),R.layout.simplerow,GroList);
        mListView.setAdapter(mListAdapter);
        mListView.setExpanded(true);

        GroList2 = new ArrayList<>();
        mListAdapterBought = new ArrayAdapter<>(getActivity(),R.layout.simplerow, GroList2);
        mListView2.setAdapter(mListAdapterBought);
        mListView2.setExpanded(true);


        //Set what to do when a list item is clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object item = mListView.getItemAtPosition(position);
                //text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                con.publish("items", new String[]{"setToBought",con.clientId,ListName, item.toString()});
                con.publish("items", new String[]{"fetch-bought",con.clientId,ListName});
                con.publish("items", new String[]{"fetch",con.clientId,ListName});
                Toast.makeText(getActivity(),
                        item.toString()+" bought", Toast.LENGTH_LONG).show();
            }
        });

        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = mListView2.getItemAtPosition(position);
                /*TextView text = (TextView) view;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                con.publish("items", new String[]{"delete",con.clientId,ListName,item.toString() });
                con.publish("items", new String[]{"fetch-bought",con.clientId,ListName.toString()});
                Toast.makeText(getActivity(),
                        item.toString()+" deleted", Toast.LENGTH_LONG).show();
            }
        });

        //What to do when the add button is pressed
        final Button btnAdd = (Button) myView.findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
        return this.mListAdapter;
    }
    public ArrayAdapter<String> getListAdapterBought(){
        return this.mListAdapterBought;
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
                    EditText.setText(result.get(0));
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