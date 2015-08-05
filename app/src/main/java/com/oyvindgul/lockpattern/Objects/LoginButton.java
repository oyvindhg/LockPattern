package com.oyvindgul.lockpattern.Objects;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.oyvindgul.lockpattern.PatternStorage;

/**
 * Created by Øyvind on 7/14/2015.
 */
public class LoginButton extends TextView {

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public boolean cursorTouch(View cursor) {

        if (PatternStorage.isEmpty()) {
            return false;
        }

        int[] buttonPos = new int[2];
        int[] cursorPos = new int[2];

        this.getLocationOnScreen(buttonPos);
        cursor.getLocationOnScreen(cursorPos);


        cursorPos[0] += cursor.getWidth() / 2;
        cursorPos[1] += cursor.getHeight() / 2;

        if (buttonPos[0] < cursorPos[0] && cursorPos[0] < buttonPos[0] + this.getWidth()) {
            if (buttonPos[1] < cursorPos[1] && cursorPos[1] < buttonPos[1] + this.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public void makeVisible(){
        this.setTextColor(0xFF003390);
    }

    public void makeInvisible(){
        this.setTextColor(0x1F003390);
    }
}

