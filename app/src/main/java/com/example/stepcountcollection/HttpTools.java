package com.example.stepcountcollection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by haro on 2017/2/15.
 */
public class HttpTools {
    public static boolean sendPost(String stringUrl, String params) {
        boolean res = false;
        byte[] data = params.getBytes();
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            ////设置连接属性
            httpConn.setConnectTimeout(3000);
            httpConn.setDoOutput(true);//使用 URL 连接进行输出
            httpConn.setDoInput(true);//使用 URL 连接进行输入
            httpConn.setUseCaches(false);//忽略缓存
            httpConn.setRequestMethod("POST");//设置URL请求方法

            //设置请求属性
            //获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            httpConn.setRequestProperty("Content-length", "" + data.length);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");

            //获得响应状态
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(data);

            int response = httpConn.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                res = true;
                Log.i("wangshifu", "HTTP_OK");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            return res;
        }
    }
}
