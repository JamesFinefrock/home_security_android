package com.finefrock.james.security;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView mTextMessage;
    private ArrayList<Door> doors;
    private DatabaseReference dbRef;
    ValueEventListener eventListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initChannels(this.getBaseContext());
        FirebaseMessaging.getInstance().subscribeToTopic("security");

        // Create reference to the database
        dbRef = FirebaseDatabase.getInstance().getReference("door_sensors");

        // Create db event listener
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                doors = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Door temp = snapshot.getValue(Door.class);
                    doors.add(temp);
                }
                Collections.sort(doors, new Door.DoorComparator());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        dbRef.addValueEventListener(eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbRef.removeEventListener(eventListener);
    }

    protected void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("switches",
                "Security Switch Notifications",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notification Channel for Security Switches");
        notificationManager.createNotificationChannel(channel);
    }
}
