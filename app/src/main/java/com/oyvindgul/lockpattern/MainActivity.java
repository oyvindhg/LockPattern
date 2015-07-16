package com.oyvindgul.lockpattern;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.oyvindgul.lockpattern.Objects.MotionCursor;

import jp.epson.moverio.bt200.SensorControl;

import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrixFromVector;


public class MainActivity extends Activity implements SensorEventListener {



    //Variables used for sensor control
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int sensorCounter;

    private float xref;
    private float yref;
    private float xprev;
    private float yprev;
    int width;
    int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "My Service CREATED", Toast.LENGTH_SHORT).show();

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, "My Service START", Toast.LENGTH_SHORT).show();

        SensorControl mSensorControl = new SensorControl(this);
        mSensorControl.setMode(SensorControl.SENSOR_MODE_HEADSET);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }


    @Override
    protected void onResume() {
        super.onResume();

        View myBackground = (View) findViewById(R.id.loginbutton);
        myBackground.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        Toast.makeText(this, "My Service RESUME", Toast.LENGTH_SHORT).show();

        mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();

        Toast.makeText(this, "My Service PAUSE", Toast.LENGTH_SHORT).show();

        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Toast.makeText(this, "My Service STOP", Toast.LENGTH_SHORT).show();

        mSensorManager.unregisterListener((SensorEventListener) this);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if accuracy changes?
        Toast.makeText(this, "ACCURACY CHANGED", Toast.LENGTH_SHORT).show();
    }

    public void onSensorChanged(SensorEvent event) {


        MotionCursor cursor = (MotionCursor) findViewById(R.id.cursor);

        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float rotationM[] = new float[9];
            float orientation[] = new float[3];
            float rotationVector[] = {event.values[0], event.values[1], event.values[2]};

            getRotationMatrixFromVector(rotationM, rotationVector);
            SensorManager.remapCoordinateSystem(rotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, rotationM);
            getOrientation(rotationM, orientation);
            float x = (float) Math.toDegrees(orientation[0]);
            float y = (float) Math.toDegrees(orientation[1]);

            if (sensorCounter == 0) {
                xref = x;
                yref = y;
                sensorCounter++;
            }
            else {


//                if ( (xref > 100 && x < 0)  ){
//                    x = 180 - x;
//                }
//                else if ( (xref < -100 && x > 0))


//                Log.d("MainActivity", "RotVector: x = " + x + ", y = " + y);
//                Log.d("MainActivity", "RotVector: xref = " + xref + ", yref = " + yref);


                cursor.moveCursorAnim(x, y, xprev, yprev, xref, yref, width, height);

                if (cursor.getX() < 30) {
                    xref = x + 20;
                } else if (cursor.getX() > width - 120) {
                    xref = x - 20;
                } else if (cursor.getY() < 0 ){
                    yref = y + 10;
                } else if (cursor.getY() > height - 50){
                    yref = y - 10;
                }
            }

            xprev = x;
            yprev = y;
        }
    }

}
