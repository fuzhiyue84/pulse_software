package cn.edu.fudan.blepulse.ndk;

/**
 * Created by dell on 2017-03-03.
 */
public class JniUtil {

    static {
        System.loadLibrary("NdkBlePulse");
    }

    public static native String logCalc(String logData);

    public static native double calcArea(int[] data);
}
