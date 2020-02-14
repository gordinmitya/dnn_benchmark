#include "mace.h"

using namespace mace;

std::string jni2StdString(JNIEnv *env, const jstring str) {
    const char *storage_path_ptr = env->GetStringUTFChars(str, nullptr);
    if (storage_path_ptr == nullptr) return 0;
    std::string result(storage_path_ptr);
    env->ReleaseStringUTFChars(str, storage_path_ptr);
    return result;
}

std::string parseString(JNIEnv *env, const jobject obj, jclass clazz, const char *field) {
    jfieldID fid = env->GetFieldID(clazz, field, "Ljava/lang/String;");
    jstring str = (jstring) env->GetObjectField(obj, fid);
    return jni2StdString(env, str);
}

std::vector<int> parseIntArray(JNIEnv *env, const jobject obj, jclass clazz, const char *field) {
    jfieldID fid = env->GetFieldID(clazz, field, "[I");
    jintArray arr = (jintArray) env->GetObjectField(obj, fid);
    jsize size = env->GetArrayLength(arr);
    std::vector<int> vector(size);
    env->GetIntArrayRegion(arr, 0, size, vector.data());
    return vector;
}

ModelInfo parseModelInfo(JNIEnv *env, const jobject obj) {
    jclass clazz = env->GetObjectClass(obj);

    ModelInfo info;
    info.pb_path = parseString(env, obj, clazz, "pbPath");
    info.data_path = parseString(env, obj, clazz, "dataPath");
    info.input_name = parseString(env, obj, clazz, "inputName");
    info.output_name = parseString(env, obj, clazz, "outputName");

    std::vector<int> inputShapeInt = parseIntArray(env, obj, clazz, "inputShape");
    std::vector<int64_t> inputShape(inputShapeInt.begin(), inputShapeInt.end());
    info.input_shape = inputShape;

    std::vector<int> outputShapeInt = parseIntArray(env, obj, clazz, "outputShape");
    std::vector<int64_t> outputShape(outputShapeInt.begin(), outputShapeInt.end());
    info.output_shape = outputShape;

    return info;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_ru_gordinmitya_mace_MACENative_createMaceContext(
        JNIEnv *env, jclass clazz,
        jobject _model_info,
        jint num_threads,
        jint inference_type,
        jstring _storage_path) {

    ModelInfo modelInfo = parseModelInfo(env, _model_info);

    MaceContext *mace_context = new MaceContext();
    mace_context->model_info = modelInfo;
    mace_context->device_type = static_cast<DeviceType>(inference_type);

    MaceEngineConfig config(mace_context->device_type);

    MaceStatus status;

    status = config.SetCPUThreadPolicy(
            num_threads,
            CPUAffinityPolicy::AFFINITY_HIGH_PERFORMANCE
    );
    if (status != MaceStatus::MACE_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "MACE native",
                            "message: %s",
                            status.information().c_str());
        return 0;
    }

    if (mace_context->device_type == DeviceType::GPU) {
        auto storagePath = jni2StdString(env, _storage_path);
        mace_context->gpu_context = GPUContextBuilder()
                .SetStoragePath(storagePath)
                .Finalize();
        config.SetGPUContext(mace_context->gpu_context);
        config.SetGPUHints(
                GPUPerfHint::PERF_HIGH,
                GPUPriorityHint::PRIORITY_HIGH);
    }

    //  load model input and output name
    std::vector<std::string> input_names = {modelInfo.input_name};
    std::vector<std::string> output_names = {modelInfo.output_name};

    std::unique_ptr<mace::port::ReadOnlyMemoryRegion> model_graph_data =
            make_unique<mace::port::ReadOnlyBufferMemoryRegion>();
    {
        auto fs = GetFileSystem();
        status = fs->NewReadOnlyMemoryRegionFromFile(modelInfo.pb_path.c_str(), &model_graph_data);
        if (status != MaceStatus::MACE_SUCCESS) {
            __android_log_print(ANDROID_LOG_ERROR,
                                "MACE native",
                                "failed to read file: %s",
                                modelInfo.pb_path.c_str());
            return 0;
        }
    }

    std::unique_ptr<mace::port::ReadOnlyMemoryRegion> model_weights_data =
            make_unique<mace::port::ReadOnlyBufferMemoryRegion>();
    {
        auto fs = GetFileSystem();
        status = fs->NewReadOnlyMemoryRegionFromFile(modelInfo.data_path.c_str(),
                                                     &model_weights_data);
        if (status != MaceStatus::MACE_SUCCESS) {
            __android_log_print(ANDROID_LOG_ERROR,
                                "MACE native",
                                "failed to read file: %s",
                                modelInfo.data_path.c_str());
            return 0;
        }
    }

    std::shared_ptr<mace::MaceEngine> engine;
    status = CreateMaceEngineFromProto(
            reinterpret_cast<const unsigned char *>(model_graph_data->data()),
            model_graph_data->length(),
            reinterpret_cast<const unsigned char *>(model_weights_data->data()),
            model_weights_data->length(),
            input_names,
            output_names,
            config,
            &engine);
    mace_context->engine = engine;
    if (status != MaceStatus::MACE_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "MACE native",
                            "message: %s",
                            status.information().c_str());
        return 0;
    }

    return reinterpret_cast<jlong>(mace_context);
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_ru_gordinmitya_mace_MACENative_run(
        JNIEnv *env, jclass thisObj,
        jlong _mace_context,
        jfloatArray _input_data) {
    MaceContext *maceContext = reinterpret_cast<MaceContext *>(_mace_context);
    ModelInfo &modelInfo = maceContext->model_info;

    // INPUT BUFFER
    std::map<std::string, mace::MaceTensor> inputs;
    int64_t input_size = std::accumulate(modelInfo.input_shape.begin(),
                                         modelInfo.input_shape.end(),
                                         1,
                                         std::multiplies<int64_t>());
    auto buffer_in = std::shared_ptr<void>(new float[input_size],
                                           std::default_delete<float[]>());
    inputs[modelInfo.input_name] = mace::MaceTensor(
            modelInfo.input_shape,
            buffer_in,
            DataFormat::NHWC);

    // INPUT DATA
    jsize length = env->GetArrayLength(_input_data);
    if (length != input_size) return nullptr;
    env->GetFloatArrayRegion(_input_data,
                             0,
                             static_cast<jsize>(input_size),
                             static_cast<jfloat *>(buffer_in.get()));

    // OUTPUT BUFFER
    std::map<std::string, mace::MaceTensor> outputs;
    int64_t output_size = std::accumulate(modelInfo.output_shape.begin(),
                                          modelInfo.output_shape.end(),
                                          1,
                                          std::multiplies<int64_t>());
    auto buffer_out = std::shared_ptr<void>(new float[output_size],
                                            std::default_delete<float[]>());
    outputs[modelInfo.output_name] = mace::MaceTensor(
            modelInfo.output_shape,
            buffer_out);

    // RUN THE MODEL
    MaceStatus status = maceContext->engine->Run(inputs, &outputs);
    if (status != MaceStatus::MACE_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "MACE native",
                            "message: %s",
                            status.information().c_str());
        return 0;
    }

    // COPY OUTPUT
    jfloatArray jOutputData = env->NewFloatArray(static_cast<jsize>(output_size));
    if (jOutputData == nullptr) return nullptr;
    env->SetFloatArrayRegion(jOutputData, 0, static_cast<jsize>(output_size),
                             outputs[modelInfo.output_name].data().get());

    return jOutputData;
}

extern "C"
JNIEXPORT void JNICALL
Java_ru_gordinmitya_mace_MACENative_release(
        JNIEnv *env, jclass thisObj,
        jlong _mace_context) {
    MaceContext *maceContext = reinterpret_cast<MaceContext *>(_mace_context);
//    maceContext->engine.reset();
//    maceContext->gpu_context.reset();
    delete maceContext;
}