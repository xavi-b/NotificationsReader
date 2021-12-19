package com.xavib.notificationsreader;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {
    public static final String TAG = "NotificationsReader";

    public class NotificationData {
        public String app_name;
        public String text;
    }

    public ArrayList<NotificationData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<NotificationData>();

        FloatingActionButton b = (FloatingActionButton) findViewById(R.id.fab);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotifications();
            }
        });

        ListView simpleList = (ListView) findViewById(R.id.list_view);
        CustomAdapter customAdapter = new CustomAdapter(this);
        simpleList.setAdapter(customAdapter);
    }

    @Override
    public void onStart() {
        showNotifications();
        super.onStart();
    }

    @Override
    public void onResume() {
        showNotifications();
        super.onResume();
    }

    public void showNotifications() {
        if (isNotificationServiceEnabled()) {
            Log.i(TAG, "Notification enabled -- trying to fetch it");
            getNotifications();
        } else {
            Log.i(TAG, "Notification disabled -- Opening settings");
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }

    public void getNotifications() {
        Log.i(TAG, "Waiting for NotificationsService");
        NotificationsService notificationsService = NotificationsService.get();
        Log.i(TAG, "Active Notifications: [");
        data.clear();
        for (StatusBarNotification notification : notificationsService.getActiveNotifications()) {
            Log.i(TAG, "    " + notification.getPackageName() + " / " + notification.getTag());
            NotificationData e = new NotificationData();
            e.app_name = notification.getPackageName();
            e.text = notification.getNotification().extras.getString("android.title");
            data.add(e);
        }
        Log.i(TAG, "]");
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String allNames = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (allNames != null && !allNames.isEmpty()) {
            for (String name : allNames.split(":")) {
                if (getPackageName().equals(ComponentName.unflattenFromString(name).getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
}