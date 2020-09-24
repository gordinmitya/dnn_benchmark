cmake_minimum_required(VERSION 3.4.1)

set(lib_DIR "${CMAKE_SOURCE_DIR}/../jniLibs")
include_directories(${CMAKE_SOURCE_DIR}/includes)
include_directories(${CMAKE_SOURCE_DIR}/includes/onnxruntime/core/session)

add_library(
        onnxcore
        SHARED
        onnxjni.cpp  onnxruntime_inference.cpp
)

add_library(libonnxruntime STATIC IMPORTED)
set_target_properties(
        libonnxruntime
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libonnxruntime.so
)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

target_link_libraries(
        onnxcore
        libonnxruntime

        ${log-lib}
        ${jnigraphics-lib}
)
