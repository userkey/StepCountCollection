package com.example.stepcountcollection;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by haro on 2017/2/20.
 */
public class LoginThread extends Thread {
    String url;
    String param;
    Context context;

    LoginThread(String url, String param, Context context){
        this.url = url;
        this.param = param;
        this.context = context;
    }

    @Override
    public void run() {
        Intent intent = new Intent();
        if(HttpTools.sendPost(url, param)) {
            intent.setAction("LOGIN_SUCESS");
        }else{
            intent.setAction("LOGIN_FAILED");
        }
        context.sendBroadcast(intent);
        super.run();
    }
}
