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
                                               jint num_threads,
                                               jboolean use_gpu) {

    auto net = new ncnn::Net();
    if (use_gpu)
        ncnn::create_gpu_instance();

    ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = num_threads;
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
                                              jobject bitmap, jfloatArray output) {
    auto net = (ncnn::Net *) netPtr;

    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "bitmap format should be RGBA_8888");
        return JNI_FALSE;
    }

    // ncnn from bitmap
    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_RGB);

    // inference
    {
        float mean_vals[3] = {0.485f, 0.456f, 0.406f};
        float normal_vals[3] = {0.229f, 0.224f, 0.225f};
        for (int i = 0; i < 3; ++i) {
            mean_vals[i] = mean_vals[i] * 255.0f;
            normal_vals[i] = 1.0f / 255.0f / normal_vals[i];
        }

        in.substract_mean_normalize(mean_vals, normal_vals);

        ncnn::Extractor ex = net->create_extractor();

        ex.set_vulkan_compute(net->opt.use_vulkan_compute);

        ex.input("input", in);

        ncnn::Mat out;
        ex.extract("473", out);

        int outputLen = env->GetArrayLength(output);
        env->SetFloatArrayRegion(output, 0, outputLen, out.row(0));
    }

    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_ru_gordinmitya_ncnn_NCNNNative_nativeRelease(JNIEnv *env, jclass type, jlong netPtr) {
    auto net = (ncnn::Net *) netPtr;
    releaseNet(net);
}

}