package com.xavib.notificationsreader;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationsService extends NotificationListenerService {
    private static final String TAG = "NotificationsService";
    static NotificationsService _this;

    public static NotificationsService get() {
        NotificationsService ret = _this;
        return ret;
    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Connected");
        _this = this;
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "Disconnected");
        _this = null;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
