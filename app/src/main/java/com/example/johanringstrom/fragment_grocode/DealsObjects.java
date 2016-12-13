package com.example.johanringstrom.fragment_grocode;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by Obada on 2016-12-08.
 */

class DealsObjects {
    private String name;
    private String price;
    private String description;
    private Bitmap image;
    static ArrayList<DealsObjects> list = new ArrayList<>();

DealsObjects(String[] args,Bitmap bit)
{
    name =  args[0];
    price = args[1];
    description =  args[2];
    image = bit;
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
    //return null if the Object doesn't exists
    return null;
}
String getName()
{return name;}

String getPrice()
{return price;}

String getDescription()
{return description;}

Bitmap getImage()
{return image;}
}
