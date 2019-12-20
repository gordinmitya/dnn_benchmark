/*
 * Credits to https://github.com/nihui/ncnn-android-squeezenet
 */

#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

// ncnn
#include "ncnn/net.h"
#include "ncnn/benchmark.h"

#define TAG "NCNN_native"


extern "C" {

void releaseNet(ncnn::Net *net) {
    bool use_gpu = net->opt.use_vulkan_compute;
    delete net->opt.blob_allocator;
    delete net->opt.workspace_allocator;
    delete net;
    if (use_gpu)
        ncnn::destroy_gpu_instance();
}

JNIEXPORT jlong JNICALL
Java_ru_gordinmitya_ncnn_NCNNNative_nativeInit(JNIEnv *env, jclass type, jobject assetManager,
                                               jstring paramFile_,
                                               jstring binFile_,
                                               jboolean use_gpu) {

    auto net = new ncnn::Net();
    if (use_gpu)
        ncnn::create_gpu_instance();

    ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = 4;
    opt.blob_allocator = new ncnn::UnlockedPoolAllocator();
    opt.workspace_allocator = new ncnn::PoolAllocator();
    opt.use_vulkan_compute = ncnn::get_gpu_count() != 0 && (bool) use_gpu;
    net->opt = opt;

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    const char *paramFile = env->GetStringUTFChars(paramFile_, nullptr);
    const char *binFile = env->GetStringUTFChars(binFile_, nullptr);

    bool success = true;
    // init param
    {
        int ret = net->load_param(mgr, paramFile);
        if (ret != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, TAG, "load_param failed");
            success = false;
        }
    }

    // init bin
    {
        int ret = net->load_model(mgr, binFile);
        if (ret != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, TAG, "load_model failed");
            success = false;
        }
    }

    env->ReleaseStringUTFChars(paramFile_, paramFile);
    env->ReleaseStringUTFChars(binFile_, binFile);

    if (success) {
        return (jlong) net;
    } else {
        releaseNet(net);
        return 0;
    }
}

JNIEXPORT jboolean JNICALL
Java_ru_gordinmitya_ncnn_NCNNNative_nativeRun(JNIEnv *env, jclass type, jlong netPtr,
                                              jobject bitmap) {
    auto net = (ncnn::Net *) netPtr;

    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);

    const int EXPECTED_SIZE = 224;
    int width = info.width;
    int height = info.height;
    if (width != EXPECTED_SIZE || height != EXPECTED_SIZE) {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "bitmap size should be 224x224");
        return JNI_FALSE;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "bitmap format should be RGBA_8888");
        return JNI_FALSE;
    }

    // ncnn from bitmap
    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_BGR);

    // inference
    {
        const float mean_vals[3] = {104.F, 117.F, 123.F};
        in.substract_mean_normalize(mean_vals, nullptr);

        ncnn::Extractor ex = net->create_extractor();

        ex.set_vulkan_compute(net->opt.use_vulkan_compute);

        ex.input("input.1", in);

        ncnn::Mat out;
        ex.extract("465", out);
    }

    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_ru_gordinmitya_ncnn_NCNNNative_nativeRelease(JNIEnv *env, jclass type, jlong netPtr) {
    auto net = (ncnn::Net *) netPtr;
    releaseNet(net);
}

}