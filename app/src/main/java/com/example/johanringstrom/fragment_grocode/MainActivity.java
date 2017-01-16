package com.example.johanringstrom.fragment_grocode;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Choose starting fragment.
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLists()).commit();
        setTitle(getString(R.string.title_section1));

        //Set toolbar(actionbar)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creates a Connection object
        con = new Connection(MainActivity.this,Connection.clientId);

        //Creates drawer to add the navigation links to.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Creates a ActionBarDrawerToggle object to handel the state of the drawer. Open or close.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Creates a navigation view.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Puts the clientID to the header
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.navClientId);
        name.setText("Gona M");

    }

    @Override
    //Pressing back button and drawer close
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Left empty to disable back button.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final MyLists ListName = new MyLists();

        //Delete list when delete is pressed and return to mylists view
        if (id == R.id.action_delete) {}

        //creating a popup dialog to be able to share list with another user
        if (id == R.id.action_share) {

//            final Dialog dialog = new Dialog(this,R.style.AppTheme_Dark_Dialog);
//            dialog.setContentView(R.layout.share_dialog);
//            dialog.setTitle("Custom Alert Dialog");
//
//            final EditText editText = (EditText) dialog.findViewById(R.id.editText);
//            Button btnShare = (Button) dialog.findViewById(R.id.share);
//            Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
//
//            dialog.show();
//
//            //button to handle email written in textfield and share with that user
//            btnShare.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    con.publish("items", new String[]{"invite",con.clientId,ListName.getListname() , editText.getText().toString()});
//                    Toast.makeText(getApplicationContext(),"List Shared",Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            });
//
//            //Cancel the sharing and dismiss dialog
//            btnCancel.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//
//            return true;

        }

        // unsub to list if Unsubsrcribe is pressed and return to sharedlist layout
        if (id == R.id.action_unsub) {
//            ShareLists sl = new ShareLists();
//            con.publish("items", new String[]{"reject-invite",con.clientId, sl.getListname() });
//            Toast.makeText(getApplicationContext(),"Unsubscribed to list",Toast.LENGTH_SHORT).show();
//            android.app.FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, new ShareLists()).commit();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.my_lists) {
            setTitle(getString(R.string.title_section1));
            //Goes to MyLists
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLists()).commit();
            //Checks if application is connected and logs the state. If not makes a toast.
            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");


            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to", Toast.LENGTH_LONG).show();

            }


        } if (id == R.id.share_lists) {
            setTitle("Community");
            //Goes to ShareLists
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ShareLists()).commit();

            //Checks if application is connected and logs the state and publish a fetch request.
            // If not connected makes a toast.
            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");
                con.publish("lists",new String[]{"fetch-SubscriptionList",con.clientId});//get lists
            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to the broker", Toast.LENGTH_LONG).show();

            }


        } if (id == R.id.notifications) {
            setTitle(getString(R.string.title_section3));
            //Moves to Notifications fragment.
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Notifications()).commit();

            //Checks if application is connected and logs the state and publish a fetch request.
            // If not connected makes a toast.
            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");
                con.publish("lists",new String[]{"fetch-Notifications",con.clientId});//get lists
            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to the broker", Toast.LENGTH_LONG).show();

            };
            // close connection of user
        } else if (id == R.id.logout) {
            Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);        }

        else if (id == R.id.deals) {
            //Moves to the deals fragment.
            setTitle("Search");
            Deals d = new Deals();
            fragmentManager.beginTransaction().replace(R.id.content_frame, d).commit();

        }

        //Close drawer when navigation click is made.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
