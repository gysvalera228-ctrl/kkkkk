cmake_minimum_required(VERSION 3.22.1)
project(dataminde)

add_library(
    dataminde
    SHARED
    native_bridge.cpp
    geometry.cpp
)

find_library(log-lib log)

target_compile_features(dataminde PRIVATE cxx_std_17)
target_compile_options(dataminde PRIVATE -Wall -Wextra)

target_link_libraries(
    dataminde
    ${log-lib}
)
