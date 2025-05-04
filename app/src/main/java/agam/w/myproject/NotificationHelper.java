package agam.w.myproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
//This class helps manage notification channels for the app.
public class NotificationHelper {

    // Unique ID for the notification channel
    public static final String CHANNEL_ID = "general_channel";

    // User-visible name of the notification channel
    public static final String CHANNEL_NAME = "General Notifications";

    public static void createNotificationChannel(Context context)
    {
    // Create a new NotificationChannel object with ID, name, and importance level
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        // Set a description for the channel (visible in system settings)
        channel.setDescription("This is the general notification channel for the app");
        // Get the system NotificationManager to register the channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel);
    }
}
