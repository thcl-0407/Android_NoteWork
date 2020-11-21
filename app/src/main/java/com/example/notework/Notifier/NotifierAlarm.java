package com.example.notework.Notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.example.notework.Models.Remind;
import com.example.notework.NotesActivity;
import com.example.notework.R;

public class NotifierAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("NhacNhoBundle");
        Remind remind = (Remind) bundle.getSerializable("NhacNho");

        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent ClickOpenIntent = new Intent(context, NotesActivity.class);
        ClickOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(NotesActivity.class);
        taskStackBuilder.addNextIntent(ClickOpenIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("NoteWork_Channel_01","Note Work", NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = builder.setContentTitle("Note Work")
                .setContentText(remind.getTitle()).setAutoCancel(true)
                .setSound(alarmsound).setSmallIcon(R.drawable.logo_app)
                .setContentIntent(pendingIntent)
                .setChannelId("NoteWork_Channel_01")
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, notification);
    }
}
