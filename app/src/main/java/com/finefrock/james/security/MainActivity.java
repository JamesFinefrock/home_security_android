package com.finefrock.james.security;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView mTextMessage;
    private ListView mList;
    private ToggleButton mToggleButton;
    private LinearLayout main_layout;
    private List<SecuritySwitch> securitySwitches;
    private DatabaseReference dbRef;
    ValueEventListener eventListener;
    ArrayAdapter<SecuritySwitch> listAdaper;
    private SharedPreferences preferences;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setVisibility(View.INVISIBLE);
                    main_layout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    main_layout.setVisibility(View.INVISIBLE);
                    mTextMessage.setVisibility(View.VISIBLE);
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

        preferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);

        mToggleButton = (ToggleButton) findViewById(R.id.notification_toggle);
        mToggleButton.setChecked(preferences.getBoolean(getString(R.string.notification_status), true));
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(getString(R.string.notification_status), mToggleButton.isChecked());
                editor.apply();
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        main_layout = (LinearLayout) findViewById(R.id.main_linear_layout);
        mList = findViewById(R.id.switch_list);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        securitySwitches = new ArrayList<>();
        listAdaper = new ArrayAdapter<SecuritySwitch>(this, android.R.layout.simple_list_item_1, securitySwitches);
        mList.setAdapter(listAdaper);

        initChannels(this.getBaseContext());
        FirebaseMessaging.getInstance().subscribeToTopic(getResources().getString(R.string.fcm_topic));

        // Create reference to the database
        dbRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.fdb_reference));

        // Create db event listener
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                securitySwitches.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SecuritySwitch temp = snapshot.getValue(SecuritySwitch.class);
                    securitySwitches.add(temp);
                }
                Collections.sort(securitySwitches, new SecuritySwitch.SecuritySwitchComparator());
                listAdaper.notifyDataSetChanged();
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
        NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.notification_channel),
                "Security Switch Notifications",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notification Channel for Security Switches");
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
    }
}
