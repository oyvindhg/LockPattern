package com.oyvindgul.lockpattern;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oyvindgul.lockpattern.Objects.Circle;
import com.oyvindgul.lockpattern.Objects.LoginButton;
import com.oyvindgul.lockpattern.Objects.MotionCursor;

import java.util.Arrays;
import java.util.List;

import jp.epson.moverio.bt200.SensorControl;

import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrixFromVector;


public class MainActivity extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int sensorCounter;
    private int[] circleCounter = new int[8];
    private int buttonCounter;
    private float xref;
    private float yref;
    private float xprev;
    private float yprev;
    private int width;
    private int height;

    private MotionCursor cursor;
    private Circle c1;
    private Circle c2;
    private Circle c3;
    private Circle c4;
    private Circle c5;
    private Circle c6;
    private Circle c7;
    private Circle c8;
    private List<Circle> circleList;
    private RelativeLayout buttonBgr;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        cursor = (MotionCursor) this.findViewById(R.id.cursor);
        c1 = (Circle) findViewById(R.id.circle1);
        c2 = (Circle) findViewById(R.id.circle2);
        c3 = (Circle) findViewById(R.id.circle3);
        c4 = (Circle) findViewById(R.id.circle4);
        c5 = (Circle) findViewById(R.id.circle5);
        c6 = (Circle) findViewById(R.id.circle6);
        c7 = (Circle) findViewById(R.id.circle7);
        c8 = (Circle) findViewById(R.id.circle8);
        circleList = Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8);
        buttonBgr = (RelativeLayout) findViewById(R.id.buttonbackground);
        loginButton = (LoginButton) findViewById(R.id.loginbutton);

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();


    }

    @Override
    protected void onStart() {
        super.onStart();


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


        mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();


        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    protected void onStop() {
        super.onStop();


        mSensorManager.unregisterListener((SensorEventListener) this);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if accuracy changes?
        Toast.makeText(this, "ACCURACY CHANGED", Toast.LENGTH_SHORT).show();
    }

    public void onSensorChanged(SensorEvent event) {

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

                if (xref > 90 && x < 0) {
                    x += 360;
                } else if (xref < -90 && x > 0) {
                    x -= 360;
                }

                Log.d("MainActivity", "RotVector: x = " + x + ", y = " + y);
                Log.d("MainActivity", "RotVector: xref = " + xref + ", yref = " + yref);

                cursor.moveCursor(x, y, xprev, yprev, xref, yref, width, height);

                if (cursor.getX() < 30) {
                    xref = x + 20;
                } else if (cursor.getX() > width - 120) {
                    xref = x - 20;
                } else if (cursor.getY() < 0) {
                    yref = y + 10;
                } else if (cursor.getY() > height - 50) {
                    yref = y - 10;
                }

                for (int i = 0; i < circleList.size(); i++){

                    if (circleList.get(i).cursorTouch(cursor)){
                        cursor.fill();
                        circleCounter[i]++;
                        if (circleCounter[i]==6){
                            circleCounter[i] = 0;
                            circleList.get(i).onTouched();
                            loginButton.makeVisible();
                            buttonBgr.setBackgroundColor(0xFF99CCFF);
                            cursor.empty();
                        }
                    }
                    else{
                        if (circleCounter[i] != 0){
                            cursor.empty();
                        }
                        circleCounter[i] = 0;
                    }
                }

                if (loginButton.cursorTouch(cursor)) {
                    cursor.fill();
                    buttonCounter++;
                    if (buttonCounter == 5) {
                        buttonCounter = 0;
                        cursor.empty();
                        if (PatternStorage.correctPassword()) {
                            Toast.makeText(this, "Correct! Device unlocked", Toast.LENGTH_SHORT).show();
                            loginButton.makeInvisible();
                            buttonBgr.setBackgroundColor(0x1F99CCFF);
                        } else {
                            Toast.makeText(this, "Wrong password! Try again", Toast.LENGTH_SHORT).show();
                            PatternStorage.resetPattern();
                            for (int i = 0; i < circleList.size(); i++) {
                                circleList.get(i).turnWhite();
                            }
                            loginButton.makeInvisible();
                            buttonBgr.setBackgroundColor(0x1F99CCFF);
                        }
                    }
                }
                else{
                    if (buttonCounter != 0) {
                        cursor.empty();
                        buttonCounter = 0;
                    }
                }
            }

            xprev = x;
            yprev = y;
        }
    }

}
