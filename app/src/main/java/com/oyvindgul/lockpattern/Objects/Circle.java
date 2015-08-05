package com.oyvindgul.lockpattern.Objects;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import static java.lang.Math.sqrt;

import com.oyvindgul.lockpattern.PatternStorage;
import com.oyvindgul.lockpattern.R;

/**
 * Created by Øyvind on 7/14/2015.
 */

public class Circle extends ImageView {

    private String state = "Untouched";

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public boolean cursorTouch(View cursor){

        if (state.equals("Touched")) {
            return false;
        }

        int[] circlePos = new int[2];
        int[] cursorPos = new int[2];

        this.getLocationOnScreen(circlePos);
        cursor.getLocationOnScreen(cursorPos);

        cursorPos[0] += cursor.getWidth() / 2;
        cursorPos[1] += cursor.getHeight() / 2;

        circlePos[0] += this.getWidth() / 2;
        circlePos[1] += this.getHeight() / 2;

        double distance =  (cursorPos[0] - circlePos[0])*(cursorPos[0] - circlePos[0]) + (cursorPos[1] - circlePos[1])*(cursorPos[1] - circlePos[1]);

        int size = 160 - (int)(0.004 * distance);
        if (size < 60){
            size = 60;
        }

        this.getLayoutParams().height = size;
        this.getLayoutParams().width = size;
        this.requestLayout();


        if (distance < this.getWidth() * this.getWidth() / 2) {

            return true;

        }

        return false;
    }

    public void onTouched(){

        this.setImageResource(R.drawable.pattern_circle_blue);
        state = "Touched";
        this.getLayoutParams().height = 80;
        this.getLayoutParams().width = 80;
        this.requestLayout();
        PatternStorage.addCircle(this);

    }

    public void turnWhite(){

        this.setImageResource(R.drawable.pattern_circle_white);
        state = "Untouched";
    }

}