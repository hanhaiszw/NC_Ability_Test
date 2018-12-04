package nc;

import android.util.Log;

/**
 * Created by mroot on 2018/4/7.
 */

public class NCUtils {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        //申请有限域
        InitGalois();
        Log.e("hanhai","载入native-cpp");
    }


    private NCUtils() {
    }

    public static byte[] mul(byte[] a, byte[] b) {
        byte[] ret = new byte[2 * 2];
        Multiply2(a, 2, 2, b, 2, 2, ret);
        return ret;
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //申请有限域
    private static native void InitGalois();

    //释放jni申请的空间
    public static native void UninitGalois();

    //矩阵相乘
    public static native byte[] Multiply(byte[] matrix1, int row1, int col1, byte[] matrix2, int row2, int col2);

    //矩阵相乘
    public static native void Multiply2(byte[] matrix1, int row1, int col1, byte[] matrix2, int row2, int col2, byte[] ret);

    //矩阵求逆
    public static native byte[] InverseMatrix(byte[] arrayData, int nK);

    //矩阵求秩
    private static native int GetRank(byte[] matrix, int nRow, int nCol);
}
