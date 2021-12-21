package com.xavib.notificationsreader;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static android.service.notification.NotificationListenerService.requestRebind;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
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
        public String app_name = "";
        public String text = "";
    }

    public ArrayList<NotificationData> data;
    private ListView simpleList;

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

        simpleList = (ListView) findViewById(R.id.list_view);
        CustomAdapter customAdapter = new CustomAdapter(this);
        simpleList.setAdapter(customAdapter);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        showNotifications();
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        showNotifications();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
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
        if (notificationsService == null) {
            Log.i(TAG, "Null NotificationsService");

            ComponentName componentName = new ComponentName(getApplicationContext(), NotificationsService.class);
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            requestRebind(componentName);

            return;
        }

        Log.i(TAG, "Active Notifications: [");
        data.clear();
        for (StatusBarNotification notification : notificationsService.getActiveNotifications()) {
            NotificationData e = new NotificationData();
            e.app_name = notification.getPackageName();
            CharSequence[] textLines = notification.getNotification().extras.getCharSequenceArray("android.textLines");
            try {
                String text = notification.getNotification().extras.getString("android.text");
                if (textLines != null) {
                    e.text = String.join("\n", textLines);
                } else if (text != null) {
                    e.text = text;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //e.text = notification.getNotification().extras.getString("android.title");
            Log.i(TAG, "    " + e.app_name + " / " + e.text);
            if(!e.text.isEmpty() && e.text != null)
            data.add(e);
        }
        Log.i(TAG, "]");
        ((BaseAdapter) simpleList.getAdapter()).notifyDataSetChanged();
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