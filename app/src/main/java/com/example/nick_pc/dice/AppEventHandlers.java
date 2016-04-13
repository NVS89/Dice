package com.example.nick_pc.dice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Nick-PC on 13.01.2016.
 */
public class AppEventHandlers implements SensorEventListener {

    private static final int SHAKE_TRESHOLD = 800;

    private float last_x;
    private float last_y;
    private float last_z;

    private long lastUpdate;
    private MainActivity m_activity;

    private long lastShow;

    public AppEventHandlers(MainActivity activity){
        if(activity == null){
            throw new IllegalArgumentException("activity");
        }
        m_activity=activity;
    }
        // обработчик событий акселератора (реагирует на встряхивание устройства и меняет кости)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            long curTime = System.currentTimeMillis();
            // не более одного обновление в 100ms.
            if((curTime-lastUpdate)>100){
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float currVal = x+y+z;
                currVal = currVal -last_x-last_y-last_z;
                float speed = Math.abs(currVal)/diffTime*10000;

                if (speed>SHAKE_TRESHOLD){
                    if((curTime - lastShow)>1500){
                        lastShow = curTime;
                        m_activity.rollDice();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ничего не делает
    }
}
