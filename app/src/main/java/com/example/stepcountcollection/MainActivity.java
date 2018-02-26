package com.example.stepcountcollection;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.OnGeoFenceListener;
import com.baidu.trace.OnEntityListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView textIMEI;
    TextView textPhoneModel;
    EditText edTextUserName;
    Button buttonStart;
    Button buttonStop;
    String imei;
    String phoneModel;
    String userName;
    SharedPreferences sp;
//    StepCounterTools stepCounterTools;
//    Trace trace;
//    LBSTraceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainReceiver mainReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOGIN_SUCESS");
        filter.addAction("LOGIN_FAILED");
        registerReceiver(mainReceiver, filter);

        sp = getSharedPreferences("mydata", MODE_PRIVATE);

        textIMEI = (TextView)findViewById(R.id.text_IMEI);
        textPhoneModel = (TextView)findViewById(R.id.text_phone_model);
        edTextUserName = (EditText)findViewById(R.id.edtext_username);
        buttonStart = (Button)findViewById(R.id.button_start);
        buttonStop = (Button)findViewById(R.id.button_stop);

        MyClickListener myClickListener = new MyClickListener(this);
        buttonStart.setOnClickListener(myClickListener);
        buttonStop.setOnClickListener(myClickListener);

        initView();

        Intent intent = new Intent(MainActivity.this, EmptyService.class);
        startService(intent);
    }

    private class MyClickListener implements View.OnClickListener{
        Context context;
        MyClickListener(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View view) {
//            if(!((MyApp)getApplicationContext()).getStepCounterTools().getState()){
//                ((MyApp)getApplicationContext()).getStepCounterTools().register();
//            }
            userName = edTextUserName.getText().toString();
            if(view.getId() == R.id.button_start) {
                if(userName.equals("") || userName == null)
                    Toast.makeText(context, "请输入测试者姓名", Toast.LENGTH_SHORT).show();
                else if(((MyApp)getApplicationContext()).getStepCounterTools().getStepCount() == 0){
                    Toast.makeText(context, "用前摇一摇", Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("imei",imei);
                    editor.putString("model", phoneModel);
                    editor.putString("userName", userName);
                    editor.putString("startTime", getNowTime());
                    editor.apply();

//                    ((MyApp)getApplicationContext()).getStepCounterTools().register();
                    ((MyApp)getApplicationContext()).initTracer(imei);

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(context, "开启记录", Toast.LENGTH_SHORT).show();
                }
            }else if(view.getId() == R.id.button_stop){
                String startTime = sp.getString("startTime", "");
                String data = "imei=" + imei + "&model=" + phoneModel + "&userName=" + userName + "&startTime=" + startTime + "&endTime=" + getNowTime();
                LoginThread loginThread = new LoginThread("http://219.245.186.230:8080/StepCount/login",data, getApplicationContext());
                loginThread.start();
            }
        }
    }

    private String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        return time;
    }

    public class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("LOGIN_SUCESS")) {
//                ((MyApp)getApplicationContext()).getStepCounterTools().unRegister();
                ((MyApp)getApplicationContext()).stopTracer();
                sp.edit().clear().apply();
                initView();
                Toast.makeText(context, "上传数据成功", Toast.LENGTH_SHORT).show();
            } else if (action.equals("LOGIN_FAILED")) {
                Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView(){
        imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        phoneModel = Build.MODEL;
        userName = sp.getString("userName", "");

        textIMEI.setText(imei);
        textPhoneModel.setText(phoneModel);
        edTextUserName.setText(userName);
        if(sp.contains("startTime")){
//            buttonStart.setClickable(false);
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);
        }else{
//            buttonStop.setClickable(false);
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
        }
    }
}
