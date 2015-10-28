package com.alarm.qwerty.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.alarm.qwerty.Activity.AlarmActivity;
import com.alarm.qwerty.R;
import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mManager;
    private SharedPreferences gpf = AlarmActivity.getMusic_gpf();

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent alarmIntent = new Intent(context,AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("TimeCalarm")
                .setContentText("Stand Up kids;")
                .setAutoCancel(true)
                .setTicker("comein=")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSound(Uri.fromFile(new File(gpf.getString("path", ""))))
                .setSmallIcon(R.drawable.comein)
                .setVibrate(new long[]{0, 2000, 1000, 2000});
        mManager.notify(1, mBuilder.build());
    }
}
