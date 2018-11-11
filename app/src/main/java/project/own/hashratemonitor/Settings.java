package project.own.hashratemonitor;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    public static final String SETTINGS = "application.settings";
    public static final String HASHRATE_ALARM_VALUE = "hashrate";
    public static final String ETHEREUM_APIKEY = "ethereumApikey";
    public static final String USER_ID = "userID";
    EditText hashrateAlarm;
    EditText ethApikey;
    EditText userID;
    Button setHashrate;
    Button setApikey;
    Button setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences preferences = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        hashrateAlarm = (EditText) findViewById(R.id.hasrateValue);
        hashrateAlarm.setText(preferences.getString(HASHRATE_ALARM_VALUE, "0"));
        ethApikey = (EditText) findViewById(R.id.apikeySet);
        ethApikey.setText(preferences.getString(ETHEREUM_APIKEY, "brak"));
        userID = (EditText) findViewById(R.id.setID);
        userID.setText(preferences.getString(USER_ID, "bral"));
        setHashrate = (Button) findViewById(R.id.setHashrateButton);
        setHashrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(HASHRATE_ALARM_VALUE, hashrateAlarm.getText().toString());
                editor.commit();
            }
        });
        setApikey = (Button) findViewById(R.id.apikeySetButton);
        setApikey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               editor.putString(ETHEREUM_APIKEY, ethApikey.getText().toString());
               editor.commit();
            }
        });
        setID = (Button) findViewById(R.id.setIDButton);
        setID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(USER_ID, userID.getText().toString());
                editor.commit();
            }
        });
    }
}
