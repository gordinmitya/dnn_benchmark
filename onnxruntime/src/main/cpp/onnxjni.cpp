//
// Created by rohith on 7/18/18.
//


#include <jni.h>
#include <string>

#include <android/bitmap.h>
#include "onnxruntime_inference.h"
#include "imageresizer.h"

#include "logs.h"


extern "C"
{

JNIEXPORT jlong JNICALL
Java_ru_gordinmitya_onnxruntime_ONNXNative_newSelf(JNIEnv *env, jclass clazz, jstring model_path,
                                                   jint num_threads, jint img_height,
                                                   jint img_width) {


    Ort::Env environment(ORT_LOGGING_LEVEL_WARNING, "test");
    const char *model_path_ch = env->GetStringUTFChars(model_path, 0);

    Inference *self = new Inference(environment, model_path_ch, num_threads, img_height, img_width);
    return (jlong) self;
}

JNIEXPORT void JNICALL
Java_ru_gordinmitya_onnxruntime_ONNXNative_deleteSelf(JNIEnv *env, jclass clazz,
                                                      jlong selfAddr) {
    if (selfAddr != 0) {
        Inference *self = (Inference *) selfAddr;
        LOGE("deleted c++ object");
        delete self;

    }
}

JNIEXPORT jboolean JNICALL
Java_ru_gordinmitya_onnxruntime_ONNXNative_run(JNIEnv *env, jclass clazz, jlong selfAddr,
                                               jobject inputbitmap,
                                               jfloatArray output) {
    AndroidBitmapInfo info;
    Inference *self = (Inference *) selfAddr;

    uint8_t *inputpixel;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, inputbitmap, &info)) < 0) {
        LOGE("Input AndroidBitmap_getInfo() failed ! error=%d", ret);
        return false;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("InputBitmap format is not RGBA_8888 !");
        return false;
    }
    if ((ret = AndroidBitmap_lockPixels(env, inputbitmap, (void **) &inputpixel)) < 0) {
        LOGE("Input AndroidBitmap_lockPxels() failed ! error=%d", ret);
    }

    LOGD("bitmap width %d , bitmap heigth %d, bitmap stride %d", info.width, info.height,
         info.stride);


    AndroidBitmap_unlockPixels(env, inputbitmap);

    float *prediction = self->run(inputpixel);


    env->SetFloatArrayRegion(output, 0, env->GetArrayLength(output), prediction);
    return true;
}

}