package com.cityzipcorp.customer.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MsgService extends FirebaseMessagingService {

    private static final String TAG = "DriverFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getNotification().getBody());
            String body = jsonObject.getString("t");
            if (body.equalsIgnoreCase("nbp")) {
                createNotification("New BoardingPass ");
            } else if (body.equalsIgnoreCase("am")) {
                createNotification("Attendance Marked");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNotification( String messageBody) {
        Intent intent = new Intent( this , HomeActivity. class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
        PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                .setSmallIcon(R.drawable.default_thumb)
                        .setContentTitle("Driver App")
                        .setContentText(messageBody)
                        .setAutoCancel( true )
                        .setSound(notificationSoundURI)
                        .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
        updateMyActivity(this, messageBody);
    }

    static void updateMyActivity(Context context, String message) {
        Intent intent = new Intent("fcm_data");
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }
}
