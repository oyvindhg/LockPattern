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
    private SensorControl mSensorControl;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int sensorCounter;
    private float savedValues[][] = new float[2][5];

    private float x;
    private float y;
    private float xref;
    private float yref;
    private float xprev = 0;
    private float yprev = 0;
    int width;
    int height;

    private float rotationM[] = new float[9];
    float orientation[] = new float[3];

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

        mSensorControl = new SensorControl(this);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        sensorCounter = 0;
        return super.onTouchEvent(event);

    }

    private float avg(float[] array){
        float sum = 0;
        for (float elem:array){
            sum += elem;
        }
        return sum / array.length;
    }

    public void moveCursor(float x, float y){
        MotionCursor cursor = (MotionCursor) findViewById(R.id.cursor);
        cursor.setTranslationX(20 * (x - xref));
        cursor.setTranslationY(20 * (y - yref));

//        if (cursor.get...)
    }

    public float[] moveCursorAnim(float x, float y, float xprev, float yprev, float xref, float yref){
        MotionCursor cursor = (MotionCursor) findViewById(R.id.cursor);

        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator animY = ObjectAnimator.ofFloat(cursor, "translationY", 20 * (yprev - yref), 20 * (y - yref));

        ObjectAnimator animX = ObjectAnimator.ofFloat(cursor, "translationX", 20 * (xprev - xref), 20 * (x - xref));

        animSetXY.playTogether(animX, animY);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(10);
        animSetXY.start();

        xprev = x;
        yprev = y;


        if (cursor.getX() < 30){
            xref = x + 20;
//            Log.d("Getwidth", String.valueOf(Math.toDegrees(width / 2)));
        }
        else if (cursor.getX() > width - 120){
//            Log.d("MainActivity", "xpos: " + cursor.getX() + "width: " + width);
            xref = x - 20;
        }
        else if (cursor.getY() < 0 ){
            yref = y + 10;
        }
        else if (cursor.getY() > height - 50){
            yref = y - 10;
        }

        Log.d("MainActivity", "MOUSEFUNC" + xprev);

        return new float[]{xref, yref};

//        Circle.checkHovered(cursor);



    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if accuracy changes?
        Toast.makeText(this, "ACCURACY CHANGED", Toast.LENGTH_SHORT).show();
    }

    public void onSensorChanged(SensorEvent event) {

        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float rotationVector[] = {event.values[0], event.values[1], event.values[2]};

            getRotationMatrixFromVector(rotationM, rotationVector);

            SensorManager.remapCoordinateSystem(rotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, rotationM);

            getOrientation(rotationM, orientation);


            x = (float) Math.toDegrees(orientation[0]);
            y = (float) Math.toDegrees(orientation[1]);

            if (sensorCounter < 5) {
                savedValues[0][sensorCounter] = x;
                savedValues[1][sensorCounter] = y;

                sensorCounter++;
            }
            else {
                if (sensorCounter == 5) {
                    xref = avg(savedValues[0]);
                    yref = avg(savedValues[1]);

                    sensorCounter++;
                }
                /*
                if ( (xref > 100 && x < 0)  ){
                    x = 180 - x;
                }
                else if ( (xref < -100 && x > 0))
                */


//                Log.d("MainActivity", "RotVector: x = " + x + ", y = " + y);
//                Log.d("MainActivity", "RotVector: xref = " + xref + ", yref = " + yref);

                Log.d("MainActivity", "SENSORFUNC" + xprev);

                float[] ref= moveCursorAnim(x, y, xprev, yprev, xref, yref);

                xref = ref[0];
                yref = ref[1];

            }

            xprev = x;
            yprev = y;
        }
    }

}
