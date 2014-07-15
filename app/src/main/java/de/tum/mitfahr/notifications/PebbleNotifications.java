package de.tum.mitfahr.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amr on 15.07.14.
 */
public class PebbleNotifications {

    public static void sendNotificationToPebble(String title, String body, Context context){
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map<String, String> data = new HashMap<String, String>();
        data.put("title", title);
        data.put("body", body);

        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "TUMitfahrerBackend");
        i.putExtra("notificationData", notificationData);

        Log.d("Test", "Sending to Pebble: " + notificationData);
        context.sendBroadcast(i);
    }
}
