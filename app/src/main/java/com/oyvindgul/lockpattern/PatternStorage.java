package com.oyvindgul.lockpattern;

import android.util.Log;

import com.oyvindgul.lockpattern.Objects.Circle;

/**
 * Created by Øyvind on 7/14/2015.
 */
public class PatternStorage {

    private static String password = "423";
    private static String pattern = "";


    public static void addCircle(Circle circle){

        if (! pattern.contains((String) circle.getTag())) {
            pattern += circle.getTag();
        }
    }

    public static void resetPattern(){
      pattern = "";
    }

    public static boolean isEmpty(){
        if (pattern.length() > 0){
            return false;
        }
        return true;
    }

    public static boolean correctPassword(){
        return pattern.equals(password);
    }
}
