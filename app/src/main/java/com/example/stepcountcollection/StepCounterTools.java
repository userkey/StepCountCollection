package com.example.stepcountcollection;

/**
 * Created by haro on 2017/1/16.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounterTools implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private float mCount;//步行总数
    private Context context;

    public StepCounterTools() {
    }

    public StepCounterTools(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void register(){
        register(mStepCount, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegister(){
        mSensorManager.unregisterListener(this);
    }

    private void register(Sensor sensor, int rateUs) {
        mSensorManager.registerListener(this, sensor, rateUs);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_STEP_COUNTER) {
            setStepCount(event.values[0]);
//            Log.i("wangshifu", String.valueOf(event.values[0]));
        }
    }

    public float getStepCount() {
        return mCount;
    }

    private void setStepCount(float count) {
        this.mCount = count;
    }

}
