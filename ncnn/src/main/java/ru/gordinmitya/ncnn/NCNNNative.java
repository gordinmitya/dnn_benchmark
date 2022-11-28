package ru.gordinmitya.ncnn;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import ru.gordinmitya.common.NativeHelper;

public class NCNNNative {
    private long netPtr = 0;

    public boolean init(AssetManager mgr, String paramFile, String binFile, int threads, boolean gpu) {
        netPtr = NCNNNative.nativeInit(mgr, paramFile, binFile, threads, gpu);
        return netPtr != 0;
    }

    public boolean run(Bitmap bitmap, float[] output) {
        checkPtr();
        return nativeRun(netPtr, bitmap, output);
    }

    public void release() {
        if (netPtr == 0) return;
        nativeRelease(netPtr);
        netPtr = 0;
    }

    private void checkPtr() {
        if (netPtr == 0)
            throw new RuntimeException("init wasn't called or failed");
    }

    public static native boolean isGpuAvailable();

    private static native long nativeInit(AssetManager mgr, String paramFile, String binFile, int threads, boolean gpu);

    private static native boolean nativeRun(long netPtr, Bitmap bitmap, float[] output);

    private static native void nativeRelease(long netPtr);

    static {
        NativeHelper.loadLibrary("ncnncore");
    }
}
