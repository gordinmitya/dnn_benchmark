cmake_minimum_required(VERSION 3.10.2)

include_directories(include)

add_library(native-lib
        SHARED
        native-lib.cpp
)