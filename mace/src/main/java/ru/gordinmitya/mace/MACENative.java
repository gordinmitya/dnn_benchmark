package ru.gordinmitya.mace;

import android.content.Context;
import android.util.Log;

import ru.gordinmitya.common.Constants;

public class MACENative {
    static {
        System.loadLibrary("mace");
        System.loadLibrary("macecore");
    }

    private long contextPtr;

    public MACENative(ModelInfoNative modelInfo, MACEInferenceType inferenceType, String cacheDir) {
        contextPtr = createMaceContext(
                modelInfo,
                Constants.NUM_THREADS,
                inferenceType.getType(),
                cacheDir
        );
        if (contextPtr == 0) {
            throw new RuntimeException("Failed to create MACE net instance");
        }
    }

    public float[] run(float[] inputData) {
        checkValid();
        return run(contextPtr, inputData);
    }

    public void release() {
        checkValid();
        release(contextPtr);
        contextPtr = 0;
    }

    private static native long createMaceContext(
            ModelInfoNative modelInfo,
            int numThreads,
            int inferenceType,
            String storagePath
    );

    private static native float[] run(
            long maceContextPtr,
            float[] inputData
    );

    private static native void release(
            long maceContextPtr
    );

    private void checkValid() {
        if (contextPtr == 0) {
            throw new RuntimeException("MACE native pointer is null, it may has been released");
        }
    }
}
