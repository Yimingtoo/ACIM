package com.yiming;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class LogThread extends Thread {

    Process process;
    final InputStream is;

    public boolean closeFlag = false;

    public LogThread() throws IOException {
        process = Runtime.getRuntime().exec("logcat -s HandWritingStubImpl");
        is = process.getInputStream();
    }

    @Override
    public void run() {
        System.out.println("thread start");
        byte[] buffer = new byte[1024];
        long len = 0;
        String rec_str = "";
        while (true) {
            try {
                len = is.read(buffer);
                System.out.println(rec_str);
                rec_str = new String(buffer);
                if (rec_str.contains("getCurrentKeyboardType") && closeFlag) {
                    runInsert();
                }
                if (-1 == len) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setCloseFlag(boolean closeFlag1) {
        closeFlag = closeFlag1;
    }
    public void runInsert(){
        System.out.println("runInsert");
    }

}
