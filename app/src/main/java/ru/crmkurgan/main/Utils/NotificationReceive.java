package ru.crmkurgan.main.Utils;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Activity.MainActivity;
import ru.crmkurgan.main.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class NotificationReceive extends FirebaseMessagingService {

    String  pic;
    String  title;
    String  message;
    String senderid;
    String receiverid;
    SharedPreferences sharedPreferences;

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            sharedPreferences=getSharedPreferences(Constants.pref_name,MODE_PRIVATE);
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            pic=remoteMessage.getData().get("icon");
            senderid=remoteMessage.getData().get("senderid");
            receiverid=remoteMessage.getData().get("receiverid");
            assert receiverid != null;
            if(receiverid.equals(sharedPreferences.getString("uid",""))) {
                ssd(title, senderid, receiverid, message, pic);
            }
        }
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sharedPreferences= getSharedPreferences(Constants.pref_name,MODE_PRIVATE);
        if(sharedPreferences.getString(Constants.device_token,"null").equals("null"))
        sharedPreferences.edit().putString(Constants.device_token,s).apply();

    }



    private void ssd(String title, String senderid, String receiverid, String mes, String pic) {


        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                sendNotification(title,senderid,receiverid,mes,pic,bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                sendNotification(title,senderid,receiverid,mes);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> Picasso.get()
                .load(pic)
                .into(target));
    }


    private void sendNotification(String title, String senderid, String receiverid, String mes, String pic, Bitmap bitmap) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("KEY", "457927");
        intent.putExtra("senderid", senderid);
        intent.putExtra("receiverid", receiverid);
        intent.putExtra("name", sharedPreferences.getString("f_name", ""));
        intent.putExtra("pic", pic);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        String channelId = getResources().getString(R.string.namechannel);
        if (Build.VERSION.SDK_INT < 26) {
            NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon)
                        .setLargeIcon(bitmap)
                        .setContentTitle(title)
                        .setContentText(mes)
                        .setAutoCancel(true)
                        .setDefaults(DEFAULT_SOUND)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    } else {
            Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.namechannel),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setSound(ringURI, attributes);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .setOngoing(false)
                            .setSmallIcon(R.drawable.icon)
                            .setLargeIcon(bitmap)
                            .setContentTitle(title)
                            .setContentText(mes)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void sendNotification(String title, String senderid, String receiverid, String mes) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("KEY", "457927");
        intent.putExtra("senderid", senderid);
        intent.putExtra("receiverid", receiverid);
        intent.putExtra("name", sharedPreferences.getString("f_name", ""));
        intent.putExtra("pic", pic);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        String channelId = getResources().getString(R.string.namechannel);
        if (Build.VERSION.SDK_INT < 26) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle(title)
                            .setContentText(mes)
                            .setAutoCancel(true)
                            .setDefaults(DEFAULT_SOUND)
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.namechannel),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setSound(ringURI, attributes);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .setOngoing(false)
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle(title)
                            .setContentText(mes)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
            notificationManager.notify(0, notificationBuilder.build());
        }

    }
}
