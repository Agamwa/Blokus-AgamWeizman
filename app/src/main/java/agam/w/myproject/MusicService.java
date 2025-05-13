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
        String action = intent.getAction();
        if(action.equals("STOP"))
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
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

    private int getSongResouceId(String name){
        String charName = name.toLowerCase().replaceAll("\\s+" , "_");
        int resId = this.getResources().getIdentifier(charName,"raw",this.getPackageName());
        if(resId != 0){
            return resId;
        }
        return R.drawable.music_icon;
    }

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