package com.oyvindgul.lockpattern;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

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


    //Variables used for positioning the cursor
    float dt = 1/5f;
    float oldSpeed[] = new float[3];
    float newSpeed[] = new float[3];
    float oldPosition[] = new float[3];
    float newPosition[] = new float[3];



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

    public float avg(float[] array){
        float sum = 0;
        for (float elem:array){
            sum += elem;
        }
        return sum / array.length;
    }

    public void moveCursor(float x, float y){
        ImageView cursor = (ImageView) findViewById(R.id.cursor);
        cursor.setTranslationX(20 * (x - xref));
        cursor.setTranslationY(20 * (y - yref));

//        if (cursor.get...)
    }

    public void moveCursorAnim(float x, float y){
        ImageView cursor = (ImageView) findViewById(R.id.cursor);
//        ObjectAnimator anim = ObjectAnimator.ofFloat(cursor, "translationX", 500 * (xref - xprev), 500 * (xref - x) );
//        ObjectAnimator anim = ObjectAnimator.ofFloat(cursor, "translationX", 500 * (xref - xprev), 500 * (xref - x));
//
//        ObjectAnimator anim = ObjectAnimator.ofFloat(cursor, "translationX", cursor.getX(), 500 * (xref - x));
//        anim.setDuration(500);
//        anim.start();


//        Log.d("MainActivity", "getX" + cursor.getX());

        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator animY = ObjectAnimator.ofFloat(cursor, "translationY", 20 * (yprev - yref), 20 * (y - yref));

        ObjectAnimator animX = ObjectAnimator.ofFloat(cursor, "translationX", 20 * (xprev - xref), 20 * (x - xref));

        animSetXY.playTogether(animX, animY);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(200);
        animSetXY.start();

        xprev = x;
        yprev = y;


        if (cursor.getX() < 30){
            xref = x + 20;
            Log.d("Getwidth", String.valueOf(Math.toDegrees(width / 2)));
        }
        else if (cursor.getX() > width - 120){
            Log.d("MainActivity", "xpos: " + cursor.getX() + "width: " + width);
            xref = x - 20;
        }
        else if (cursor.getY() < 0 ){
            yref = y + 10;
        }
        else if (cursor.getY() > height - 50){
            yref = y - 10;
        }

        Circle.checkHovered(cursor);

    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if accuracy changes?
        Toast.makeText(this, "ACCURACY CHANGED", Toast.LENGTH_SHORT).show();
    }

    public void onSensorChanged(SensorEvent event) {
        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float rotationVector[] = {event.values[0],event.values[1],event.values[2]};

            getRotationMatrixFromVector(rotationM, rotationVector);

            SensorManager.remapCoordinateSystem(rotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, rotationM);

//            Log.d("MainActivity", "RotMatrix: \n\t\t" + rotationM[0] + " " + rotationM[1] + " " + rotationM[2] + "\n\t\t" + rotationM[3] + " " + rotationM[4] + " " + rotationM[5] + "\n\t\t" + rotationM[6] + " " + rotationM[7] + " " + rotationM[8]);


            getOrientation(rotationM, orientation);


            x = (float) Math.toDegrees(orientation[0]);
            y = (float) Math.toDegrees(orientation[1]);

//            Log.d("MainActivity", "Orientation: " + orientation[0] + " " + orientation[1] + " " + orientation[2]);


            if (sensorCounter < 5){
                savedValues[0][sensorCounter] = x;
                savedValues[1][sensorCounter] = y;

                sensorCounter++;
            }
            else {
                if (sensorCounter == 5) {
                    xref = avg(savedValues[0]);
                    yref = avg(savedValues[1]);
                    Log.d("I AM HERE", String.valueOf(0));


                    xprev = x;
                    yprev = y;

                    sensorCounter ++;
                }
                /*
                if ( (xref > 100 && x < 0)  ){
                    x = 180 - x;
                }
                else if ( (xref < -100 && x > 0))
*/
//                Log.d("MainActivity", "Scalar: " + event.values[3]);
//                Log.d("MainActivity", "Orientationr:  horizontal = " + orientation[0] + ", vertical = " + orientation[1]);
//
                Log.d("MainActivity", "RotVector: x = " + x + ", y = " + y );
                Log.d("MainActivity", "RotVector: xref = " + xref + ", yref = " + yref );
//                Log.d("MainActivity", "RotVector: xnew = " + xnew + ", ynew = " + ynew + ", znew = " + znew);

                moveCursorAnim(x, y);
            }

        }

        /*
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            Log.d("MainActivity", "GyroValues: x = " + event.values[0] + ", y = " + event.values[1] + ", z = " + event.values[2]);

        }
        else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            float acceleration[] = {event.values[0], event.values[1], event.values[2]};

            // update speed
            newSpeed[0] = oldSpeed[0] + dt * acceleration[0];
            newSpeed[1] = oldSpeed[1] + dt * acceleration[1];
            newSpeed[2] = oldSpeed[2] + dt * acceleration[2];

            // update position
            newPosition[0] = oldPosition[0] + dt * newSpeed[0];
            newPosition[1] = oldPosition[1] + dt * newSpeed[1];
            newPosition[2] = oldPosition[2] + dt * newSpeed[2];

            // assign directions
            float x = newPosition[0];
            float y = newPosition[1];
            float z = newPosition[2];

            Log.d("MainActivity", "Acceleration: ax = " + acceleration[0] + ", ay = " + acceleration[1] + ", az = " + acceleration[2]);
            Log.d("MainActivity", "Speed: vx = " + newSpeed[0] + ", vy = " + newSpeed[1] + ", vz = " + newSpeed[2]);
            Log.d("MainActivity", "Position: X = " + x+ ", Y = " + y + ", Z = " + z);

            oldSpeed = newSpeed;
            oldPosition = newPosition;
        }


        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            final float alpha = 0.9f;
            float gravity[] = new float[3];
            float linear[] = new float[3];

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear[0] = event.values[0] - gravity[0];
            linear[1] = event.values[1] - gravity[1];
            linear[2] = event.values[2] - gravity[2];

            // assign directions
            float x = linear[0];
            float y = linear[1];
            float z = linear[2];

            Log.d("MainActivity", "AccelValues: x = " + x + ", y = " + y + ", z = " + z);
            }
            */
    }



    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
