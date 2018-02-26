package com.example.stepcountcollection;

import android.app.Application;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haro on 2017/3/9.
 */
public class MyApp extends Application {

    Trace trace;
    LBSTraceClient client;
    StepCounterTools stepCounterTools;

    @Override
    public void onCreate() {
        stepCounterTools = new StepCounterTools(this);
        stepCounterTools.register();
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        stepCounterTools.unRegister();
        super.onTerminate();
    }

    public StepCounterTools getStepCounterTools(){
        return stepCounterTools;
    }

    public void initTracer(String imei){
        // 轨迹服务ID
        long serviceId = 135038;
        // 设备名称
        String entityName = imei;
        // 轨迹服务类型，traceType必须设置为UPLOAD_LOCATION才能追踪轨迹
        int traceType = 2;
        // 初始化轨迹服务
        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);
        // 初始化轨迹服务客户端
        client = new LBSTraceClient(getApplicationContext());
        // 采集周期
        int gatherInterval = 10;
        // 打包周期
        int packInterval = 60;
        // http协议类型
        int protocolType = 1;
        // 设置采集和打包周期
        client.setInterval(gatherInterval, packInterval);
        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);
        // 设置http协议类型
        client.setProtocolType(protocolType);

        OnTrackListener myOnTrackListener = new MyOnTrackListener();
        client.setOnTrackListener(myOnTrackListener);
        //开始轨迹追踪
        client.startTrace(trace);
    }

    public void stopTracer(){
        client.stopTrace(trace, new MyOnStopTraceListener());
    }

    private class MyOnTrackListener extends OnTrackListener {

        @Override
        public void onRequestFailedCallback(String s) {

        }

        @Override
        public Map onTrackAttrCallback() {
            Map<String, String> trackAttrs = new HashMap<String, String>();
//            float stepCount = stepCounterTools.getStepCount();
            float stepCount = ((MyApp)getApplicationContext()).getStepCounterTools().getStepCount();
            trackAttrs.put("stepCount", String.valueOf(stepCount));
            return trackAttrs;
        }
    }

    private class MyOnStopTraceListener implements OnStopTraceListener {
        // 轨迹服务停止成功
        @Override
        public void onStopTraceSuccess() {
        }
        // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
        @Override
        public void onStopTraceFailed(int arg0, String arg1) {
        }
    };
}
