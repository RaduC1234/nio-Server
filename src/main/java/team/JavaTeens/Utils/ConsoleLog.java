package team.JavaTeens.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLog {

    public static void message(String message, String sender){
        System.out.println(getDate() + getThread("MESSAGE") + ": " + sender + " says " + message);
    }
    public static void info(String message){
        System.out.println(getDate() + getThread("INFO") + ": " + message);
    }
    public static void warn(String message){
        System.out.println(getDate() + getThread("WARN") + ": " + message);
    }
    public static void error(String message){
        System.err.println(getDate() + getThread("ERROR") + ": " + message);
    }  
    public static void fatalError(String message){
        System.err.println(getDate() + getThread("FATAL") + ": " +  message);
        System.exit(0);
    }
    private static String getDate(){

        SimpleDateFormat dt = new SimpleDateFormat("hh:mm:ss");
        return "[" + dt.format(new Date()) + "]";
    }
    private static String getThread(String Type){

        return "[" + Thread.currentThread().getName() + "/" + Type + "]";
    }

}
