package com.example.johanringstrom.fragment_grocode;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
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
    static boolean loggedin;

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
                    new MqttAndroidClient(context, "tcp://broker.hivemq.com:1883",
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
    //publish
    void publish(String type,String[] args)
    {
        //save the request to help reading the reply from the server
        currentTodo = type;
        String topic="Gro/"+clientId;
        JSONObject toSend=new JSONObject();
        JSONObject data=new JSONObject();
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
                    //if it's not fetch-lists then we need this key (list)
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
                    Log.d("??args0>>>", args[0]);
                    //if it's not fetch then we need this key (data)
                    if (!(args[0].equals("fetch") || args[0].equals("fetch-bought")|| args[0].equals("fetch-bought")||
                            args[0].equals("fetch-SubItems")|| args[0].equals("reject-invite")|| args[0].equals("fetch-BoughtSubItem")) )
                    {
                        Log.d("??args0>>>", args[0]);
                        data.put("item",args[3]);
                        toSend.put("data",data);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;

            /*case "boughtItems":
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                try {
                    //{"client_id":"beroo75@gmail.com","list":"home","request":"add","data":{"item":"apple"}} or {"client_id":"beroo75@gmail.com","list":"home","request":"fetch"}
                    toSend.put("client_id", args[1]);
                    toSend.put("list", args[2]);
                    toSend.put("request",args[0]);
                    //if it's not fetch then we need this key (data)
                    if (!args[0].equals("fetch-bought"))
                    {
                        data.put("item",args[3]);
                        toSend.put("data",data);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;*/
            /*case "subLists":
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                //args[0]=request, args[1]=email, args[2]=list
                try
                {
                    //{"client_id":args[1],"request":args[0],"list":args[2]} or {"client_id":args[2],"request":"fetch-lists"}
                    toSend.put("client_id",args[1]);
                    toSend.put("request",args[0]);
                    //if it's not fetch-lists then we need this key (list)
                    if(!args[0].equals("fetch-SubscriptionList"))
                        toSend.put("list",args[2]);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;*/
            /*case "notifications":
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                //args[0]=request, args[1]=email, args[2]=list
                try
                {
                    //{"client_id":args[1],"request":args[0],"list":args[2]} or {"client_id":args[2],"request":"fetch-lists"}
                    toSend.put("client_id",args[1]);
                    toSend.put("request",args[0]);
                    //if it's not fetch-lists then we need this key (list)
                    if(!args[0].equals("fetch-Notifications"))
                        toSend.put("list",args[2]);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case "subListItems":
                //args[0]=request, args[1]=email, args[2]=list, args[3]=item
                try {
                    //{"client_id":"beroo75@gmail.com","list":"home","request":"add","data":{"item":"apple"}} or {"client_id":"beroo75@gmail.com","list":"home","request":"fetch"}
                    toSend.put("client_id", args[1]);
                    toSend.put("list", args[2]);
                    toSend.put("request",args[0]);
                    //if it's not fetch then we need this key (data)
                    if (!args[0].equals("fetch-SubItems"))
                    {
                        data.put("item",args[3]);
                        toSend.put("data",data);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            default:*/

        }
        //send here after we have defined the object
        try {
            client.publish(topic,new MqttMessage(toSend.toString().getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    //Subscribe to a predefined topic
    public  void subscribeToTopic() {
        //Subscribe to root client + client
        String topic = "Gro/"+clientId;
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
    public  void subscribeToTopic(String Request) {
        //Subscribe to root client + client
        String topic = "Gro/"+clientId+"/"+Request;
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
    public void deliveryComplete(IMqttDeliveryToken token) {}

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        JSONObject Obj = new JSONObject(new String(message.getPayload()));
        //if it's not a reply from the server then just ignore it

        if (!Obj.has("reply"))
            return;
        JSONArray itemArr = null;
        //get the array if the key data exsits
        if (Obj.has("data"))
            itemArr = Obj.getJSONArray("data");
        Log.d("currentTodo", currentTodo);
        // {"reply":"done","data":[{"item":"home"}]}
        //if the data are list names then update the list activities

        if (currentTodo.equals("login") || currentTodo.equals("register")) {

            if (message.toString().equals("{\"reply\":\"done\"}")) {
                loggedin = true;
            } else
            {
                loggedin=false;
               // sub=false;

            }
        }
        Log.d(">>Topic??", topic);
        if (topic.equals("Gro/"+clientId+"/fetch-lists")) {
            MyLists myLists = new MyLists();
            myLists.getListAdapter().clear();
            for(int i=0; i<itemArr.length(); i++)
            {
                myLists.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
            }
        }
        // if the data are items update the items activities

        if (topic.equals("Gro/"+clientId+"/fetch")) {
            ItemsList myItems = new ItemsList();
            myItems.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myItems.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));

        }
        if (topic.equals("Gro/"+clientId+"/fetch-bought")) {
            ItemsList myBoughtItems = new ItemsList();
            myBoughtItems.getListAdapterBought().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myBoughtItems.getListAdapterBought().add((String) itemArr.getJSONObject(i).get("item"));

        }
        if (topic.equals("Gro/"+clientId+"/fetch-SubscriptionList")) {//TODO
            ShareLists mySubLists = new ShareLists();
            mySubLists.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                mySubLists.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        if (topic.equals("Gro/"+clientId+"/fetch-Notifications")) {//TODO
            Notifications myNotifications = new Notifications();
            myNotifications.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myNotifications.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        if (topic.equals("Gro/"+clientId+"/fetch-SubItems")) {//TODO
            ItemsSubList mySubItems = new ItemsSubList();
            mySubItems.getListAdapter().clear();
            for (int i = 0; i < itemArr.length(); i++)
                mySubItems.getListAdapter().add((String) itemArr.getJSONObject(i).get("item"));
        }
        if (topic.equals("Gro/"+clientId+"/fetch-BoughtSubItem")) {//TODO
            ItemsSubList myBoughtSubItems = new ItemsSubList();
            myBoughtSubItems.getListAdapterBought().clear();
            for (int i = 0; i < itemArr.length(); i++)
                myBoughtSubItems.getListAdapterBought().add((String) itemArr.getJSONObject(i).get("item"));
        }

    }
    boolean loggedin(String email,String pass)
    {
        publish("login",new String[]{"login", email,pass});
        try {
            client.publish("Gro/"+clientId,new MqttMessage(new String("loggedIn is: "+loggedin).getBytes()));
            Thread.sleep(1000);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loggedin;
    }

    //Get client
    public MqttAndroidClient getClient(){
        return client;
    }
}