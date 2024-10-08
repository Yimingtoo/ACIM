package com.yiming;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author : top
 * e-mail : Top_Huang1@human-horizons.com
 * time   : 2024/02/01
 * desc   : This is CheckHasDialogUtil
 * version: 1.0
 */
public class CheckHasDialogUtil {
    public static boolean hasDialogOnActivity(Activity activity) {
        if (activity == null) {
            return false;
        } else {
            try {
                View targetDecorView = activity.getWindow().getDecorView();
                IBinder targetSubToken = targetDecorView.getWindowToken();
                List<View> mViews = getWindowViews("mViews");

                if (mViews == null) {
                    System.out.println("CheckHasDialogUtil mViews is null");
                    return false;
                } else {
                    System.out.println("CheckHasDialogUtil mViews " + mViews.size());
                    int targetIndex = -1;
                    for (int i = 0; i < mViews.size(); ++i) {
                        if (mViews.get(i) == targetDecorView) {
                            targetIndex = i;
                        }
                    }
                    if (targetIndex == -1) {
                        System.out.println("CheckHasDialogUtil targetIndex == -1 ");
                        return false;
                    } else {
                        ArrayList<WindowManager.LayoutParams> mParams = getWindowViews("mParams");
                        if (mParams == null) {
                            System.out.println("CheckHasDialogUtil mParams == null");
                            return false;
                        } else {
                            IBinder targetToken = ((WindowManager.LayoutParams) mParams.get(targetIndex)).token;
                            long size;
                            size = mParams.stream().map((layoutParams) -> {
                                Object[] result = new Object[]{layoutParams.token};
                                return result;
                            }).filter((o) -> {
                                Object[] result = (Object[]) o;
                                IBinder token = result[0] != null ? (IBinder) result[0] : null;
//                                boolean ret = (token == targetSubToken || token == targetToken || token == null);
                                boolean ret = (token == null);
                                return ret;
                            }).count();
                            System.out.println("CheckHasDialogUtil  size > 1L " + size);
                            return size > 1L;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static <T> ArrayList<T> getWindowViews(String param) {
        if (TextUtils.isEmpty(param)) {
            return null;
        } else {
            try {
                Class<?> clz = Class.forName("android.view.WindowManagerGlobal");
                Method mGetInstance = clz.getMethod("getInstance");
                Object instance = mGetInstance.invoke((Object) null);
                Field declaredField = clz.getDeclaredField(param);
                declaredField.setAccessible(true);
                Object objFieldGet = declaredField.get(instance);
                return (ArrayList) objFieldGet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @SuppressLint("PrivateApi")
    public static boolean isImeDialogShowing(InputMethodManager imm) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = null;
        clazz = Class.forName("android.view.inputmethod.InputMethodManager");
        Method method = clazz.getMethod("isInputMethodPickerShown");
        method.setAccessible(true);
        return (Boolean) method.invoke(imm);

    }





    static Thread thread;

    public static void readLog(boolean flag) throws IOException {
        //第一个是Logcat ，也就是我们想要获取的log日志
        //第二个是 -s 也就是表示过滤的意思
        //第三个就是 我们要过滤的类型 W表示warm ，我们也可以换成 D ：debug， I：info，E：error等等

        String[] running = new String[]{"logcat", "-s", "adb logcat *: I"};
        Process process = Runtime.getRuntime().exec("logcat -s HandWritingStubImpl");

        final InputStream is = process.getInputStream();
        if (flag) {
            thread = new Thread() {
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
                            Log.w("len1111","len = "+len );
                            if (-1 == len) {
                                break;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            thread.start();
            Log.w("test", "dsljfksjd");
        }else {
            thread.interrupt();
            System.out.println("thread stop: "+thread.isInterrupted());
        }
    }
}
