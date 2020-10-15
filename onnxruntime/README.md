https://github.com/microsoft/onnxruntime.git

```bash
./build.sh --android \
    --android_sdk_path $ANDROID_SDK \
    --android_ndk_path $ANDROID_NDK \
    --android_api 27 \
    --use_nnapi \
    --config MinSizeRel \
    --build_shared_lib \
    --parallel \
    --android_abi armeabi-v7a
```
Pick up `build/Android/MinSizeRel/libonnxruntime.so`.

The same for `--android_abi arm64-v8a`.

https://github.com/microsoft/onnxruntime/blob/master/BUILD.md#android-build-instructions

https://github.com/microsoft/onnxruntime/blob/master/docs/ONNX_Runtime_for_Mobile_Platforms.md

Many thanks to Rohithkvsp for his repo https://github.com/Rohithkvsp/OnnxRuntimeAndorid