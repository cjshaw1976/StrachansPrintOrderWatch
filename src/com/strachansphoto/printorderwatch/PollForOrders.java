package com.strachansphoto.printorderwatch;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Created by admin on 10/7/2015.
 */
public class PollForOrders {
    String mBranchKey;
    OkHttpClient client = new OkHttpClient();
    LogFile logFile = new LogFile();
    Ini mIni = new Ini();

    public PollForOrders () {
        java.io.File mCurrentDir = new java.io.File("");
        try {
            mIni.load(new FileReader(mCurrentDir + "settings.ini"));
            Ini.Section section = mIni.get("branch");
            mBranchKey = section.get("key");
        } catch (IOException e) {
            logFile.log(Level.SEVERE, e.getMessage());
            //todo exit program with message
        }
    }

    public boolean mark(String orderId) throws IOException, NoSuchAlgorithmException {
        String url = "http://orders.strachansphoto.com/poll/"
                + sha1(orderId + "printedSaltonthewoundispainful" + requiredDate());

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    public String poll() throws IOException, NoSuchAlgorithmException {
        String url = "http://orders.strachansphoto.com/poll/"
                + sha1(mBranchKey + "branchSaltonthewoundispainful" + requiredDate());

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        } else {
            logFile.log(Level.WARNING, "Poll error: " + response.message());
            return response.message();
        }
    }

    //http://www.sha1-online.com/sha1-java/
    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    private String requiredDate () {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        Date now = new Date();
        return format.format(now);
    }
}
