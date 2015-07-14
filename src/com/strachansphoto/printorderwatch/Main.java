package com.strachansphoto.printorderwatch;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        final PollForOrders pollForOrders = new PollForOrders();
        final ParseOrders parseOrders = new ParseOrders();
        final LogFile logFile = new LogFile();

        logFile.log(Level.INFO, "Program Started");

        //Start a timer
        int mDelay = 0;                  // no delay
        int mPeriod = 10000;             // repeat every 10 sec.
        Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    String response = pollForOrders.poll();
                    parseOrders.parseForOrders(response, pollForOrders);
                } catch (IOException e) {
                    logFile.log(Level.WARNING, "Connection error: " + e.getMessage());
                    //System.out.println("Connection error: " + e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    logFile.log(Level.WARNING, "Hash error: " + e.getMessage());
                    //System.out.println("Hash error: " + e.getMessage());
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, mDelay, mPeriod);
    }
}



