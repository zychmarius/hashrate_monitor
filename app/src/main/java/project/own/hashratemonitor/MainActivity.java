package project.own.hashratemonitor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button startMain;
    private Button stopMain;
    private Button settingsMain;
    private TextView hashrateField;
    private TextView infoStatusText;
    private WorkService workService;
    private HashrateReciever hashrateReciever = new HashrateReciever();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            WorkService.MyBinder binder = (WorkService.MyBinder) service;
            workService = binder.getworkService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            workService = null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hashrateField = (TextView) findViewById(R.id.hashrateField);
        registerReceiver(hashrateReciever, new IntentFilter(WorkService.HASHRATE_SEND));


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showHashrate();
//                handler.postDelayed(this, 5000);
//            }
//        }, 1500);


    }

    public void clickStartButton(View view) {
        infoStatusText = (TextView) findViewById(R.id.infoStatusText);
        startBackgroundWorkService();

        infoStatusText.setText("sledzenie w toku aby wtrzymać kliknij stop");

    }


    public void clickStopButton(View view) {
        stopBackgroundWorkService();
        infoStatusText = (TextView) findViewById(R.id.infoStatusText);
        infoStatusText.setText("sledzenie wsztrzymane aby rozpocząć kliknij start");
    }

    private void startBackgroundWorkService() {
        Intent serviceIntent = new Intent(this, WorkService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void showHashrate() {
        if (workService != null) {
            ((TextView) findViewById(R.id.hashrateField)).setText(workService.showHashrate());
        }
    }

    private void stopBackgroundWorkService() {
        Intent serviceIntent = new Intent(this, WorkService.class);
        unbindService(serviceConnection);
        stopService(serviceIntent);
    }

    public void clickSettingsButton(View view) {
        Intent openSettingsScreen = new Intent(getApplicationContext(), Settings.class);
        startActivity(openSettingsScreen);
    }

    public class HashrateReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WorkService.HASHRATE_SEND)) {
                String hashrateMessage = intent.getExtras().getString(WorkService.HASHRATE_VALUE);
                hashrateField.setText(hashrateMessage);
            }
        }
    }



}
