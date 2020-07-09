cmake_minimum_required(VERSION 3.6)
project(zeuseesTracking-lib)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CMAKE_C_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")


#SET(HOME_TVM /Users/yujinke/downloads/tvm-0.5)
include_directories(/Users/yujinke/Downloads/OpenCV-android-sdk/sdk/native/jni/include)


#include_directories(${HOME_TVM})
#include_directories(${HOME_TVM}/include)
#include_directories(${HOME_TVM}/3rdparty/dmlc-core/include)
#include_directories(${HOME_TVM}/3rdparty/dlpack/include)


find_library( # Sets the name of the path variable.
              log-lib
              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

set(SOURCE_FILES native.cpp vehicle_recg.cpp)
set(OpenCV_DIR "/Users/yujinke/Downloads/OpenCV-android-sdk/sdk/native/jni")
find_package(OpenCV REQUIRED)

add_library(vehicle-lib SHARED ${SOURCE_FILES})
target_link_libraries(vehicle-lib  ${OpenCV_LIBS} z jnigraphics)



