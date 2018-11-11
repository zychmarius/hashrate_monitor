package project.own.hashratemonitor;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class WorkService extends Service{

    public static final String HASHRATE_SEND = "hashrateValueSend";
    public static final String HASHRATE_VALUE = "hashrateValueMessage";

    private String hashrateMessage;
    private Timer timer;
    private TimerTask timerTask;
    private Integer hashrateAlarmValue;

    public String showHashrate() {
        return hashrateMessage;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            webStart();

        }
    }
    public void testNotification(){
        if(hashrateMessage!= null && Integer.parseInt(hashrateMessage)<hashrateAlarmValue){
            createNotification();
        }
    }

    private final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        WorkService getworkService(){
            return WorkService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
        hashrateAlarmValue = Integer.valueOf(preferences.getString(Settings.HASHRATE_ALARM_VALUE, "0"));
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        clearTimerSchedule();
        initTask();
        timer.scheduleAtFixedRate(timerTask, 0 * 1000, 60 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }
    private void clearTimerSchedule() {
        if(timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }
    private void initTask() {
        timerTask = new MyTimerTask();
    }

    @Override
    public void onDestroy(){
        clearTimerSchedule();
        super.onDestroy();
    }
    public  void webStart(){
        new WebServicesHandler()
                .execute("https://ethereum.miningpoolhub.com/index.php?page=api&action=getuserhashrate&api_key=3e32dafdb817ef7add17f2dd0db0ef05e50a4d3780cabb03830cfd2ae21fc944&id=150097");

    }

    private void createNotification() {
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Low Hashrate")
                .setContentText("Low hasrate")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(uri)
                .setVibrate(new long[] {10, 500});

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void sendHashrateBroadcast(String hashrateValue){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(HASHRATE_SEND);
        broadcastIntent.putExtra(HASHRATE_VALUE, hashrateValue);
        sendBroadcast(broadcastIntent);

    }

    private class WebServicesHandler extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... urls){

            try{
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());

                return streamToString(in);
            }catch (Exception e){
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){



            try {


                JSONObject json = new JSONObject(result);
                JSONObject getuserhashrate = json.getJSONObject("getuserhashrate");
                String hashrate = getuserhashrate.getString("data");

                Double value = Double.valueOf(hashrate);
                Integer integer = value.intValue()/1000;

                sendHashrateBroadcast(String.valueOf(integer));

                testNotification();
            }catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }
        }
    }
    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }



}
