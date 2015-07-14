package com.strachansphoto.printorderwatch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Created by admin on 13/7/2015.
 */
public class LogFile {
    public void log(Level level, String message){
        try {
            java.io.File currentDir = new java.io.File("");
            FileOutputStream fileOutputStream = new FileOutputStream(currentDir+"log.txt", true);
            PrintStream printStream = new PrintStream(fileOutputStream);
            printStream.println(requiredDate() + " " + level + " - " + message);
            printStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Logfile error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Logfile error: " + e.getMessage());
        }
    }

    private String requiredDate () {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return format.format(now);
    }
}
