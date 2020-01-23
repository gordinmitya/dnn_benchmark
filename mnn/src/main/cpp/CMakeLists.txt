cmake_minimum_required(VERSION 3.4.1)

set(lib_DIR "${CMAKE_SOURCE_DIR}/../jniLibs")
include_directories(${CMAKE_SOURCE_DIR}/includes)

set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp -static-openmp")
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp -static-openmp")

set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99 -fvisibility=hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math -flax-vector-conversions")
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fvisibility=hidden -fvisibility-inlines-hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math -fno-rtti -fno-exceptions -flax-vector-conversions")
set (CMAKE_LINKER_FLAGS "${CMAKE_LINKER_FLAGS} -Wl,--gc-sections")

add_library( MNN SHARED IMPORTED )
set_target_properties(
        MNN
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libMNN.so
)

add_library( mnncore SHARED
        mnnnetnative.cpp)

find_library( log-lib log )
find_library( jnigraphics-lib jnigraphics )

add_definitions(-DMNN_USE_LOGCAT)
target_link_libraries( mnncore MNN ${log-lib} ${jnigraphics-lib})
