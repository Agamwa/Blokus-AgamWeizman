package agam.w.myproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public MusicService() {
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent == null)
            return START_STICKY; // If intent is null, keep service running
        String action = intent.getAction();
        // If the action is STOP, stop the media player if it is playing
        if(action.equals("STOP"))
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
        // If the action is PLAY, play the music
        if(action.equals("PLAY")) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            else {
                mediaPlayer = MediaPlayer.create(this,R.raw.daylylife);
                mediaPlayer.start();
            }

        }

        return START_STICKY;

    }
    // Service will be restarted if terminated by the system
    private int getSongResouceId(String name){
        String charName = name.toLowerCase().replaceAll("\\s+" , "_");
        int resId = this.getResources().getIdentifier(charName,"raw",this.getPackageName());
        if(resId != 0){
            return resId;// Return the resource ID if found
        }
        return R.drawable.music_icon;// Return default icon resource if not found
    }
    // Called when the service is destroyed, release MediaPlayer resources
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}