package project.own.hashratemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private Button startMain;
    private Button stopMain;
    private Button settingsMain;
    private TextView hashrateField;
    private TextView infoStatusText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickStartButton(View view){
        infoStatusText = (TextView)findViewById(R.id.infoStatusText);

        new WebServicesHandler()
                .execute("https://ethereum.miningpoolhub.com/index.php?page=api&action=getuserhashrate&api_key=userkey&id=userid");
        infoStatusText.setText("sledzenie w toku aby wtrzymać kliknij stop");
    }
    public void clickStopButton(View view){
        infoStatusText = (TextView)findViewById(R.id.infoStatusText);
        infoStatusText.setText("sledzenie wsztrzymane aby rozpocząć kliknij start");

    }
    public void clickSettingsButton(View view){
        Intent openSettingsScreen = new Intent(getApplicationContext(),Settings.class);
        startActivity(openSettingsScreen);
    }
    private class WebServicesHandler extends AsyncTask<String, Void, String>{
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            dialog.setMessage("Czekaj...");
            dialog.show();
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

            dialog.dismiss();

            try {

//                ((TextView) findViewById(R.id.test1)).setText(result);
                JSONObject json = new JSONObject(result);
                JSONObject getuserhashrate = json.getJSONObject("getuserhashrate");
                String hashrate = getuserhashrate.getString("data");
                int i =0;
                while (hashrate.charAt(i)!='.'){
                    i++;
                }
                if(i<2){
                    ((TextView) findViewById(R.id.hashrateField)).setText("0");
                }else if(i<3){
                    String endHashrate = hashrate.substring(0,i);
                    ((TextView) findViewById(R.id.hashrateField)).setText("0.0"+endHashrate);
                }else if(i<4){
                    String endHashrate = hashrate.substring(0,i);
                    ((TextView) findViewById(R.id.hashrateField)).setText("0."+endHashrate);
                }else {
                    String endHashrate = hashrate.substring(0,i-3);
                    ((TextView) findViewById(R.id.hashrateField)).setText(endHashrate);
                }

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
            // obsłuż wyjątek
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }

}
