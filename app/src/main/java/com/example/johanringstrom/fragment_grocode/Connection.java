package com.example.johanringstrom.fragment_grocode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by johanringstrom on 05/11/16.
 */
public class Connection extends AppCompatActivity implements MqttCallback {


    static MqttAndroidClient client;

    static String clientId;
    private int qos = 1;
    private static String currentTodo;
    private String TAG;
    static boolean loggedin=true;

    //Connection constructer with two arguments
    public Connection(final Context context, String clientId){
          //If the client is already connected do nothing.
        if(client == null) {
            //Set clientId
            this.clientId = clientId;

            //Creates a new client that connects to the prata server
            this.client =
                    new MqttAndroidClient(context, "tcp://54.154.153.243:1883",
                            clientId);
            try {
                //the IMqttToken provides a mechanism for tracking the completion of an asynchronous task.
                //In this case a connection request.
                IMqttToken token = client.connect();
                //Register a listener to be notified when an action complete.
                token.setActionCallback(new IMqttActionListener() {
                    public String TAG;

                    @Override
                    //If the connection is succesfully connected to the broker we print a log message
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // Application is connected
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    //If the connection is a failiure connected to the broker we print a log message
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong
                        Log.d(TAG, "onFailure");

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            //Sets where the callback should be made. Where the message arrives.
            client.setCallback(this);
        }

    }
    //Connection constructer with one arguments
    public Connection(final Context context){
        //If the client is already connected do nothing.
        if(client == null) {
            //Creates a new client that connects to the prata server
            this.client =
                    new MqttAndroidClient(context, "tcp://54.154.153.243:1883",
                            clientId);
            try {
                //the IMqttToken provides a mechanism for tracking the completion of an asynchronous task.
                //In this case a connection request.
                IMqttToken token = client.connect();
                //Register a listener to be notified when an action complete.
                token.setActionCallback(new IMqttActionListener() {
                    public String TAG;

                    @Override
                    //If the connection is succesfully connected to the broker we print a log message
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    //If the connection is a failiure connected to the broker we print a log message
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong
                        Log.d(TAG, "onFailure");

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            //Sets where the callback should be made. Where the message arrives.
            client.setCallback(this);
        }

    }
    //A publish message that being send to the broker. The arguments is first the type of publish
    // being done a items, lists, login or register) and a string of arguments that
    // consists of what request is being done,(login, fetch, fetch-lists ...) and different arguments
    //depending on what type it is.
    void publish(String type,String[] args)
    {
        //save the request to help reading the reply from the server
        currentTodo = type;
        //What topic to receive back to.
        String topic="Gro/"+clientId;
        //Creates to jason objects to be filled with values.
        JSONObject toSend=new JSONObject();
        JSONObject data=new JSONObject();
        //Depending on what type that is coming in as a argument do the specific task in the matched
        //case statement.
        switch(type)
        {

            case "login":
                //args[0]=request,args[1]=email,args[2]=password
                try
                {
                    //{"request":"args[0]","data":{"email":"args[1]","password":"args[2]"}}
                    toSend.put("request",args[0]);
                    data.put("email",args[1]);
                    data.put("password",args[2]);
                    toSend.put("data",data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case "register":
                //args[0]=request,args[1]=email,args[2]=password,args[3]=name
                try
                {
                    //{"request":"args[0]","data":{"email":"args[1]","password":"args[2]","name":"args[3]"}}
                    toSend.put("request",args[0]);
                    data.put("email",args[1]);
                    data.put("password",args[2]);
                    data.put("name",args[3]);
                    toSend.put("data",data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case "lists":
                //args[0]=request, args[1]=email, args[2]=list
                try
                {
                    //{"client_id":args[1],"request":args[0],"list":args[2]} or {"client_id":args[2],"request":"fetch-lists"}
                    toSend.put("client_id",args[1]);
                    toSend.put("request",args[0]);
                    //if it's not the requests in the if statment then we need the key list added to our
                    //publish statement.
                    if(!(args[0].equals("fetch-lists") || args[0].equals("fetch-SubscriptionList")|| args[0].equals("fetch-Notifications")))
                    toSend.put("list",args[2]);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case "items":
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                try {
                    //{"client_id":"beroo75@gmail.com","list":"home","request":"add","data":{"item":"apple"}} or {"client_id":"beroo75@gmail.com","list":"home","request":"fetch"}
                    toSend.put("client_id", args[1]);
                    toSend.put("list", args[2]);
                    toSend.put("request",args[0]);
                    //if it's not the requests in the if statment then we need the key data added to our
                    //publish statement.
                    if (!(args[0].equals("fetch") || args[0].equals("fetch-bought")|| args[0].equals("fetch-bought")||
                            args[0].equals("fetch-SubItems")|| args[0].equals("reject-invite")|| args[0].equals("fetch-BoughtSubItem")) )
                    {
                        data.put("item",args[3]);
                        toSend.put("data",data);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;

        }
        //send here after we have defined the object
        try {
            client.publish(topic,new MqttMessage(toSend.toString().getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    //Subscribe to a predefined topic
    public void subscribeToTopic() {
        //Subscribe to root client + client
        String topic = "Gro/"+clientId;
        try {
            //The IMqttToken provides a mechanism for tracking the completion of an asynchronous task.
            //In this case a subscribe request.
            IMqttToken subToken = client.subscribe(topic, qos);
            //Register a listener to be notified when an action complete.
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscribe message was a success. We have not implemented a action here
                    // but futher developmnet and changes probably will.
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, We have not implemented a action here
                    // but futher developmnet and changes probably will.

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //Subscribe to the root topic gro / the client_id / the request made. This to make it easier to
    //handel incoming messages.
    public  void subscribeToTopic(String Request) {
        //Subscribe to root client + client
        String topic = "Gro/"+clientId+"/"+Request;
        try {
            //The IMqttToken provides a mechanism for tracking the completion of an asynchronous task.
            //In this case a subscribe request.
            IMqttToken subToken = client.subscribe(topic, qos);
            //Register a listener to be notified when an action complete.
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscribe message was a success. We have not implemented a action here
                    // but futher developmnet and changes probably will.

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, We have not implemented a action here
                    // but futher developmnet and changes probably will.

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //Subscribe to the gogodeals topic to receive food deals from there server.
    public void subscribeToDeals() {
        String topic = "deal/gogodeals/database/deals";
        try {
            //The IMqttToken provides a mechanism for tracking the completion of an asynchronous task.
            //In this case a subscribe request. The qos is set to one.
            IMqttToken subToken = client.subscribe(topic, qos);
            //Register a listener to be notified when an action complete.
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscribe message was a success. We have not implemented a action here
                    // but futher developmnet and changes probably will.

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, We have not implemented a action here
                    // but futher developmnet and changes probably will.

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Unsubscribe to predefined topic.
    public void unSubscribe(){
        String topic = "Gro/"+clientId;
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, when trying to unsubscribe.
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //If the application loses connection to the broker a toast is published and the system exits.
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        Log.d(TAG, "Connection to " + "broker." + " lost!");
        System.exit(1);
    }
    //Called when delivery for a message has been completed, and all acknowledgments have been received.
    // For QoS 1 it is called when PUBACK is received. We use quality of service 1 a message
    // will be delivered at least onece.
    public void deliveryComplete(IMqttDeliveryToken token) {}

    //This method is called when a message arrives from the server and isinvoked synchronously
    // by the MQTT client.
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        //create a JSON object out of the message
        JSONObject Obj = new JSONObject(new String(message.getPayload()));
        //Initiate a jason array
        JSONArray itemArr = null;
        //If the jason object has the key data the value connected to this key(a jason array)
        //will be assigned to the itemArr variable.
        if(Obj.has("data"))
            itemArr=Obj.getJSONArray("data");
        //quit if the message arrived is not a reply from our server nor the deal's server
        if(!Obj.has("reply") && !topic.equals("deal/gogodeals/database/deals"))
            return ;

        switch(topic)
        {
            //if it's a reply from the deals Server
            case "deal/gogodeals/database/deals":
                //remove old deals
                Deals.listAdapter.clear();
                if(DealsObjects.list.size()!=0)
                    //remove all the old deals from the old location
                    DealsObjects.list.clear();

                for(int i=0;i<itemArr.length(); i++)
                {
                    //read the picture first, the picture is a string separated with a ','
                    // and the first part is just info about the pic so we just ignore it
                    String str = (String)itemArr.getJSONObject(i).get("picture");
                    String[] strings = str.split(",");
                    //get the image bytes only without the info
                    str = strings[1];
                    //add the name of the deal to the activity
                    Deals.listAdapter.add((String) itemArr.getJSONObject(i).get("name"));
                    //add the new deals to the list
                    //new DealsObject with the name,price...etc
                    new DealsObjects(
                            new String[]{(String) itemArr.getJSONObject(i).get("name"),
                                    String.valueOf(itemArr.getJSONObject(i).getInt("price")),
                                    (String) itemArr.getJSONObject(i).get("description")},
                            //read a String into a bitmap
                            readImg(str)
                    );
                }
                break;
            default:
                if (topic.equals("Gro/"+clientId +"/fetch-lists"))
                {
                    //update the main activities with the lists we got
                    MyLists myLists = new MyLists();
                    //clear the old lists
                    myLists.getListAdapter().clear();
                    //add the lists to the main view
                    for (int i = 0; i < itemArr.length(); i++) {
                        myLists.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
                    }
                }
                else

                break;
        }
        //check if the currentToDo is a login/register then read the reply from the Server and
        // switch the boolean according to if it is error ore done.
        if (currentTodo.equals("login") || currentTodo.equals("register")) {
            if (message.toString().equals("{\"reply\":\"done\"}")) {
                loggedin = true;
            } else {
                loggedin = false;
            }
        }
        //check what topic the messaged is arrived to and update the activity that relates to it
        if (topic.equals("Gro/"+ clientId +"/fetch-lists"))
        {
            MyLists myLists = new MyLists();
            myLists.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++) {
                myLists.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
            }
        }
        // If the message coming in to the topic ".../fetch" put list with items
        //to the ItemsList view
        else if (topic.equals("Gro/" + clientId + "/fetch"))
        {
            ItemsList myItems = new ItemsList();
            myItems.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myItems.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));

        }
        // If the message coming in to the topic ".../fetch-bought" put list with items
        // that are bought to the ItemsList view
        else if (topic.equals("Gro/" + clientId + "/fetch-bought"))
        {
            ItemsList myBoughtItems = new ItemsList();
            myBoughtItems.getListAdapterBought().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myBoughtItems.getListAdapterBought().add((String) itemArr.getJSONObject(i).get("item"));

        }
        // If the message coming in to the topic ".../fetch-SubscriptionList" put list with lists
        // you subscribe on to the ShareLists view
        else if (topic.equals("Gro/" + clientId + "/fetch-SubscriptionList"))
        {
            ShareLists mySubLists = new ShareLists();
            mySubLists.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                mySubLists.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        // If the message coming in to the topic ".../fetch-Notifications" put list with lists
        // you have been invited to subscribe to in to the Notifications view
        else if (topic.equals("Gro/" + clientId + "/fetch-Notifications")) {
            Notifications myNotifications = new Notifications();
            myNotifications.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myNotifications.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        // If the message coming in to the topic ".../fetch-SubItems" put list with items that you subscribe to
        // in to the ItemsSubList view
        else if (topic.equals("Gro/" + clientId + "/fetch-SubItems")) {
            ItemsSubList mySubItems = new ItemsSubList();
            mySubItems.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                mySubItems.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        // If the message coming in to the topic ".../fetch-BoughtSubItem" put list with items that
        // you subscribe to and is bought in to the ItemsSubList view
        else if (topic.equals("Gro/" + clientId + "/fetch-BoughtSubItem")) {
            ItemsSubList myBoughtSubItems = new ItemsSubList();
            myBoughtSubItems.getListAdapterBought().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myBoughtSubItems.getListAdapterBought().add((String) itemArr.getJSONObject(i).get("item"));
        }
    }
    //Method used to publish a login request
    boolean loggedin(String email,String pass)
    {
        //publish a login request
        publish("login",new String[]{"login", email,pass});
        try {
            //The thread sleeps in order to wait for the login return message "{\"reply\":\"done\"}"
            //or "{\"reply\":\"error\"}"
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loggedin;
    }

    //Get client this mqtt client
    public MqttAndroidClient getClient(){
        return client;
    }
    //Decodes the pictures that arrives as strings and return them as a bitmap.
    public Bitmap readImg(String str)
    {
        byte[] decodedString = Base64.decode(str.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}
