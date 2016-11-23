package com.example.johanringstrom.fragment_grocode;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by johanringstrom on 05/11/16.
 */
public class Connection extends AppCompatActivity implements MqttCallback {
    protected static MqttAndroidClient  client;
    private static String clientId;
    private int qos = 1;
    private static String currentTodo;
    private String TAG;

    public Connection(final Context context, String clientId){

        if(client == null) {
            this.clientId = clientId;
            //Set clientId and create new create a MqttAndroid client
            //String clientId = MqttClient.generateClientId();
            this.client =
                    new MqttAndroidClient(context, "tcp://test.mosquitto.org:1883",
                            //Tryes to connect this client to a the  broker. test.mosquitto.org
                            clientId);//"tcp://192.168.43.185:1883
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    public String TAG;

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();

            }
            client.setCallback(this);
        }

    }

    public Connection(final Context context){

        if(client == null) {
            //Set clientId and create new create a MqttAndroid client
            //String clientId = MqttClient.generateClientId();
            this.client =
                    new MqttAndroidClient(context, "tcp://test.mosquitto.org:1883",
            //Tryes to connect this client to a the  broker. test.mosquitto.org
                            clientId);//"tcp://192.168.43.185:1883
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    public String TAG;

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            client.setCallback(this);
        }

    }
    //publish to get/add/delete lists
    void publish(String request, String listName)
    {
        currentTodo = request;
        String str;
       if(!request.equals("fetch-lists"))
            str = "{\"client_id\":\""+clientId+"\",\"request\":\""+request+"\",\"list\":\""+listName+"\"}";
        else
            str = "{\"client_id\":\""+clientId+"\",\"request\":\""+request+"\"}";
        MqttMessage itemMsg = new MqttMessage(str.getBytes());
        try {
            client.publish("Gro/"+clientId,itemMsg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //add/del/fetch items from a list
    public void publish(String request, String listName, String item) {
        //Make  a Jsonobject following our RFC. Waiting to get it aproved
        currentTodo = request;
        String topic = "Gro/"+ clientId;
        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();

        try {
            obj.put("client_id", clientId);
            obj.put("list", listName);
            obj.put("request", request);
            if(!request.equals("fetch"))
            obj.put("data", obj2.put("item", item));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject payload = obj;

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.toString().getBytes("UTF-8");
            MqttMessage itemMsg = new MqttMessage(encodedPayload);
            client.publish(topic, itemMsg);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
        //login/register
        void publish(String email, String pass, String name,String request) {
        //Make  a Jsonobject following our RFC. Waiting to get it aproved
        currentTodo = request;
        String topic = "Gro/"+ clientId;
        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();

        try {
            obj.put("request", request);
            obj2.put("email", email);
            obj2.put("password",pass);
            if (request.equals("register"))
                obj2.put("name",name);
            obj.put("data", obj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject payload = obj;

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.toString().getBytes("UTF-8");
            System.out.println("is it null: "+ payload.toString());
            MqttMessage itemMsg = new MqttMessage(encodedPayload);
            client.publish(topic, itemMsg);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    public void publish(String request) {
        //Make  a Jsonobject following our RFC. Waiting to get it aproved
        currentTodo = request;
        String topic = "Gro/"+ clientId;
        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();

        try {
            obj.put("clientId", clientId);
            obj.put("list", "listName");
            obj.put("request", request);
            obj.put("data", obj2.put("item", "item"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject payload = obj;

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.toString().getBytes("UTF-8");
            MqttMessage itemMsg = new MqttMessage(encodedPayload);
            client.publish(topic, itemMsg);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    //Subscribe to a predefined topic
    public  void subscribeToTopic() {
        //Subscribe to root client + client
        String topic = "Gro/"+ clientId+ "/#";
        Log.d("TopicTest", topic);
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    //Unsubscribe to predefined list.
    public void unSubscribe(){
        String topic = "RootClient/"+ clientId+ "/#";
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
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        // An application may choose to implement reconnection
        // logic at this point. This sample simply exits.
        Log.d(TAG, "Connection to " + "broker." + " lost!");
        System.exit(1);
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a message has been delivered to the hhahaha test
        // server. The token passed in here is the same one
        // that was passed to or returned from the original call to publish.
        // This allows applications to perform asynchronous
        // delivery without blocking until delivery completes.
        //
        // This sample demonstrates asynchronous deliver and
        // uses the token.waitForCompletion() call in the main thread which
        // blocks until the delivery has completed.
        // Additionally the deliveryComplete method will be called if
        // the callback is set on the client
        //
        // If the connection to the server breaks before delivery has completed
        // delivery of a message will complete after the client has re-connected.
        // The getPendingTokens method will provide tokens for any messages
        // that are still to be delivered.
    }

    //Get messages on the subscribed message. Clears listview and add the received message split up to a array.
    public void messageArrived(String topic, MqttMessage message ) throws Exception {
        //Create a jason object that does not fullfill the rfc exactly jet. It takes a objecct like this “data”:[”item1”, ”item2”…]

        JSONObject Obj = new JSONObject(new String(message.getPayload()));
        JSONArray itemArr = Obj.getJSONArray("data");
        /*try {*/
        /*    Obj = new JSONObject(new String(message.getPayload()));*/
        /*} catch (JSONException e) {*/
        /*    e.printStackTrace();*/
        /*}*/
        /*if(Obj.has("data"))*/
        /*    try {*/
        /*        itemArr = Obj.getJSONArray("data");*/
        /*    } catch (JSONException e) {*/
        /*        e.printStackTrace();*/
        /*    }*/



        /*if (currentTodo.equals("fetch")) {*/
        /*    MyLists myLists = new MyLists();*/
        /*    myLists.getListAdapter().clear();*/
        /*    for (int i = 0; i < itemArr.length(); i++)*/
        /*       myLists.getListAdapter().add(itemArr.get(i).toString());*/
        /*}*/
/*
*/

        /*if (currentTodo.equals("getList")) {*/
        /*    ItemsList myItems = new ItemsList();*/
        /*    myItems.getListAdapter().clear();*/
        /*    for (int i = 0; i < itemArr.length(); i++)*/
        /*        myItems.getListAdapter().add(itemArr.get(i).toString());*/
        /*}*/
        /*if (currentTodo.equals("getSubscriptionLists")) {*/
        /*    ShareLists mySubLists = new ShareLists();*/
        /*    mySubLists.getListAdapter().clear();*/
        /*    for (int i = 0; i < itemArr.length(); i++)*/
        /*        mySubLists.getListAdapter().add(itemArr.get(i).toString());*/
        /*}*/
        /*if (currentTodo.equals("getSubList")) {*/
        /*    ItemsSubList myItems = new ItemsSubList();*/
        /*    myItems.getListAdapter().clear();*/
        /*    for (int i = 0; i < itemArr.length(); i++)*/
        /*        myItems.getListAdapter().add(itemArr.get(i).toString());*/
        /*}*/
        Log.d("currentTodo", currentTodo);
        String reply;
        switch(currentTodo)
        {   //fetch items from a list/or list names
            case "fetch":
                try {
                    ItemsList myItems = new ItemsList();
                    myItems.getListAdapter().clear();
                    if(Obj.get("reply").equals("error"))
                        ;//update activity with an empty list
                    else
                        for (int i = 0; i < itemArr.length(); i++) {
                            JSONObject jsonobject = itemArr.getJSONObject(i);
                            myItems.getListAdapter().add(jsonobject.getString("item"));
                        }
                        //read data items and update activity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            //fetch list
            case "fetch-lists":
                try {
                     MyLists myLists = new MyLists();
                    myLists.getListAdapter().clear();
                    if(Obj.get("reply").equals("error"))
                        ;//update activity with an empty list
                    else
                        for (int i = 0; i < itemArr.length(); i++) {
                            JSONObject jsonobject = itemArr.getJSONObject(i);
                            myLists.getListAdapter().add(jsonobject.getString("item"));

                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            //it's just a reply
            default:
                try {
                    //either done or error
                    String res = Obj.getString("reply");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        //read data update activities and read the reply
    }
    //Get client
    public MqttAndroidClient getClient(){
        return client;
    }
}


