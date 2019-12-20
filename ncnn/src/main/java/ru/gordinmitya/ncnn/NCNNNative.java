package ru.gordinmitya.ncnn;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class NCNNNative {
    private long netPtr = 0;

    public boolean init(AssetManager mgr, String paramFile, String binFile, boolean gpu) {
        netPtr = NCNNNative.nativeInit(mgr, paramFile, binFile, gpu);
        return netPtr != 0;
    }

    public boolean run(Bitmap bitmap) {
        checkPtr();
        return nativeRun(netPtr, bitmap);
    }

    public void release() {
        checkPtr();
        nativeRelease(netPtr);
        netPtr = 0;
    }

    private void checkPtr() {
        if (netPtr == 0)
            throw new RuntimeException("init wasn't called or failed");
    }

    private static native long nativeInit(AssetManager mgr, String paramFile, String binFile, boolean gpu);

    private static native boolean nativeRun(long netPtr, Bitmap bitmap);

    private static native void nativeRelease(long netPtr);

    static {
        System.loadLibrary("ncnncore");
    }
}
