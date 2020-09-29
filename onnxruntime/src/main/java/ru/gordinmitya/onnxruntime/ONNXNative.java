package ru.gordinmitya.onnxruntime;

import android.graphics.Bitmap;

public class ONNXNative {


    private long selfAddr;

    static {
        System.loadLibrary("onnxcore");
    }

    /**
     * makes jni call to create c++ reference
     */

    public ONNXNative(String model_path, boolean use_nnapi, int num_threads, int img_height, int img_width)
    {
        selfAddr = newSelf(model_path, use_nnapi, num_threads, img_height, img_width); //jni call to create c++ reference and returns address
    }

    /**
     * makes jni call to delete c++ reference
     */

    public void delete()
    {
        deleteSelf(selfAddr);//jni call to delete c++ reference
        selfAddr = 0;//set address to 0
    }

    @Override
    protected void finalize() throws Throwable {
        delete();
    }

    /**
     * return address of c++ reference
     */
    public long getselfAddr() {

        return selfAddr; //return address
    }

    /**
     * //makes jni call to proces frames
     */

    public boolean run(Bitmap input, float[] output) {
        return run(selfAddr, input, output);
    }


    private static native long newSelf(String model_path, boolean use_nnapi, int num_threads, int img_height, int img_width);
    private static native void deleteSelf(long selfAddr);
    private static native boolean run(long selfAddr, Bitmap inbitmap, float[] output);
}
