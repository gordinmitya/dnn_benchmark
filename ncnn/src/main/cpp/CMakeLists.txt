cmake_minimum_required(VERSION 3.4.1)

set(lib_DIR "${CMAKE_SOURCE_DIR}/../jniLibs")
include_directories(${CMAKE_SOURCE_DIR}/includes)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp -static-openmp")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp -static-openmp")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99 -fvisibility=hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math -flax-vector-conversions")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fvisibility=hidden -fvisibility-inlines-hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math -fno-rtti -fno-exceptions -flax-vector-conversions")
set(CMAKE_LINKER_FLAGS "${CMAKE_LINKER_FLAGS} -Wl,--gc-sections")

add_library(
        ncnncore
        SHARED
        ncnnnative.cpp
)

add_library(libncnn STATIC IMPORTED)
set_target_properties(
        libncnn
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libncnn.a
)
add_library(libglslang STATIC IMPORTED)
set_target_properties(
        libglslang
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libglslang.a
)
add_library(libOGLCompiler STATIC IMPORTED)
set_target_properties(
        libOGLCompiler
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libOGLCompiler.a
)
add_library(libOSDependent STATIC IMPORTED)
set_target_properties(
        libOSDependent
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libOSDependent.a
)
add_library(libSPIRV STATIC IMPORTED)
set_target_properties(
        libSPIRV
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libSPIRV.a
)

target_link_libraries(
        ncnncore

        libncnn
        libglslang
        libOGLCompiler
        libOSDependent
        libSPIRV

        log
        jnigraphics
        android
        vulkan
)
