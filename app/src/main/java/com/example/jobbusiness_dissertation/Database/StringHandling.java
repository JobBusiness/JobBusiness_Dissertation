package com.example.jobbusiness_dissertation.Database;

public class StringHandling {
    public static String checkusername (String username){
       return username.replace("."," ");
    }

    public static String defaultusername (String username){
        return username.replace(" ",".");
    }
}
