package agam.w.myproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationHelper {

    public static final String CHANNEL_ID = "general_channel";
    public static final String CHANNEL_NAME = "General Notifications";
    public static void createNotificationChannel(Context context)
    {

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        channel.setDescription("This is the general notification channel for the app");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
