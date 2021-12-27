package com.example.cometimplementation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {


    public static boolean isNumberValid(String number){
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(number);
        return (m.matches());
    }


}
