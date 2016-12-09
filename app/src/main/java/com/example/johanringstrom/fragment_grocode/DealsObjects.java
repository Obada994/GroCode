package com.example.johanringstrom.fragment_grocode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import java.util.ArrayList;

import static com.example.johanringstrom.fragment_grocode.R.color.base;

/**
 * Created by obada on 2016-12-08.
 */

public class DealsObjects {
    String name;
    String price;
    String description;
    String img;
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
