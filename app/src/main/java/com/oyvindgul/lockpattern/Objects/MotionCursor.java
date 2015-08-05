package com.oyvindgul.lockpattern.Objects;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.oyvindgul.lockpattern.R;

/**
 * Created by Øyvind on 7/14/2015.
 */

public class MotionCursor extends ImageView {


    public MotionCursor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void moveCursor(float x, float y, float xprev, float yprev, float xref, float yref, float width, float height){

        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator animY = ObjectAnimator.ofFloat(this, "translationY", 20 * (yprev - yref), 20 * (y - yref));

        ObjectAnimator animX = ObjectAnimator.ofFloat(this, "translationX", 20 * (xprev - xref), 20 * (x - xref));

        animSetXY.playTogether(animX, animY);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(200);
        animSetXY.start();

    }

    public void fill(){
        this.setImageResource(R.drawable.diamond_full);
    }

    public void empty(){
        this.setImageResource(R.drawable.diamond);
    }

}
