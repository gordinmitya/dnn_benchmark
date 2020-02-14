#ifndef DNNBENCHMARK_MACE_H
#define DNNBENCHMARK_MACE_H

#include "mace/public/mace.h"
#include "mace/port/file_system.h"
#include "mace/port/env.h"
#include "mace/utils/memory.h"
#include <string>
#include <vector>
#include <map>
#include <numeric>
#include <algorithm>
#include <iostream>
#include <fstream>
#include "jni.h"
#include <android/log.h>

struct ModelInfo {
    std::string pb_path;
    std::string data_path;
    std::string input_name;
    std::string output_name;
    std::vector<int64_t> input_shape;
    std::vector<int64_t> output_shape;
};

struct MaceContext {
    std::shared_ptr<mace::MaceEngine> engine;
    std::shared_ptr<mace::GPUContext> gpu_context;
    mace::DeviceType device_type = mace::DeviceType::CPU;
    ModelInfo model_info;
};

#endif //DNNBENCHMARK_MACE_H