package com.example.johanringstrom.fragment_grocode;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.Inflater;

import static android.app.Activity.RESULT_OK;

/**
 * Created by johanringstrom on 10/11/16.
 */
public class MyLists extends Fragment{

    View myView;
    TextView title,t_des,description;
    private static Object list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.listitem, container, false);

        title = (TextView) myView.findViewById(R.id.itemName);
        title.setText("Mimmi");
        t_des = (TextView) myView.findViewById(R.id.itemName2);
        t_des.setText("Very good dog for free");
        description = (TextView) myView.findViewById(R.id.itemDescr);
        description.setText("I'd even pay you");
        // hide the action bar

        return myView;
    }

}
