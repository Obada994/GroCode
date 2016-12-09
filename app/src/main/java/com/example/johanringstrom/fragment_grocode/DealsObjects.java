package com.example.johanringstrom.fragment_grocode;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by obada on 2016-12-08.
 */

public class DealsObjects {
    String name;
    String price;
    String description;
    ImageView image;
    static ArrayList<DealsObjects> list = new ArrayList<>();

public DealsObjects(String[] args)
{
    name =  args[0];
    price = args[1];
    description =  args[2];
    list.add(this);
}
/*
    Search list for a specific DealsObject
     */
static DealsObjects findByName(String name)
{
    for(DealsObjects Obj: list)
    {
        if(Obj.name.equals(name))
            return Obj;
    }
    //return null if the Obkect doesn't exists
    return null;
}

}
