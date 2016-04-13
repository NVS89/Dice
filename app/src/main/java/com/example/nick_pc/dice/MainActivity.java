package com.example.nick_pc.dice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //константы
    //максимальное число очков
    private static final int DICE_MAX_NUMBER =  6;
    //интевал вибрации
    private static final int VIBRATE_INTERVAL = 200;

    //переменные
    private Vibrator m_vibrator;
    private ImageView m_dice1;
    private ImageView m_dice2;
    private long lastShow;
    private SensorManager m_sensorManager;

    // обьект обработчика событьй акселератора
    private AppEventHandlers m_handlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //определение компонентов приложения
        m_vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        m_dice1 = (ImageView) this.findViewById(R.id.dice1);
        m_dice2 = (ImageView) this.findViewById(R.id.dice2);

        this.rollDice(true);

        final FrameLayout rollDiceArea = (FrameLayout) findViewById(R.id.rollDice);
       //обработчик нажатия
        rollDiceArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollDice();
            }
        });

        m_sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor accleromrter = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_handlers = new AppEventHandlers(this);
    }

    public void rollDice()
    {
        this.rollDice(false);
    }
    // анимация еффекта встряхивания костей
    public void shakeDice()
    {
        Animation dice1Animation = AnimationUtils.loadAnimation(this, R.anim.dice1);
        m_dice1.startAnimation(dice1Animation);

        ImageView dice2 = (ImageView)this.findViewById(R.id.dice2);
        Animation dice2Animation = AnimationUtils.loadAnimation(this, R.anim.dice2);
        m_dice2.startAnimation(dice2Animation);
    }
    //определение показа картинки костей
    public int getImageByDiceNumber(int  diceValue)
    {
        switch (diceValue)
        {
            case 1:
                return R.drawable.img1;
            case 2:
                return R.drawable.img2;
            case 3:
                return R.drawable.img3;
            case 4:
                return R.drawable.img4;
            case 5:
                return R.drawable.img5;
            case 6:
                return R.drawable.img6;
            default:
                throw new  IllegalArgumentException("dice");
        }
    }

    public void rollDice(boolean isStart)
    {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastShow) < 400)
        {
            return;
        }

        lastShow = curTime;

        if (!isStart)
        {
            m_dice1.setImageResource(R.drawable.empty);
            m_dice2.setImageResource(R.drawable.empty);

            m_vibrator.vibrate(VIBRATE_INTERVAL);
            shakeDice();
        }
        // генератор случайних чисел
        Random generator = new Random();
        int firstDiceValue = generator.nextInt(DICE_MAX_NUMBER ) + 1;
        int secondDiceValue = generator.nextInt(DICE_MAX_NUMBER) + 1;
        // показ нужной картинки костей
        m_dice1.setImageResource(getImageByDiceNumber(firstDiceValue));
        m_dice2.setImageResource(getImageByDiceNumber(secondDiceValue));

        TextView total = (TextView)this.findViewById(R.id.tbTotal);
        total.setText(String.valueOf(firstDiceValue + secondDiceValue));
    }

    @Override
    protected void onPause() {
        super.onPause();

        m_sensorManager.unregisterListener(m_handlers);
    }

    protected void onResume() {
        super.onResume();

        Sensor accelerometer =  m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_sensorManager.registerListener(m_handlers, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}