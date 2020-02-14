cmake_minimum_required(VERSION 3.4.1)

set(lib_DIR "${CMAKE_SOURCE_DIR}/../jniLibs")
include_directories(${CMAKE_SOURCE_DIR}/includes)

add_library( MACE SHARED IMPORTED )
set_target_properties(
        MACE
        PROPERTIES IMPORTED_LOCATION
        ${lib_DIR}/${ANDROID_ABI}/libmace.so
)

add_library( macecore SHARED
        mace.cc)

find_library( log-lib log )

target_link_libraries( macecore MACE ${log-lib})
