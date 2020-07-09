cmake_minimum_required(VERSION 3.6)
project(test_)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
SET(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
SET(CMAKE_LIBRARY_OUTPUT_DIRECTORY  ${CMAKE_CURRENT_SOURCE_DIR})

set(SOURCE_FILES main.cpp)

include_directories( ${OpenCV_INCLUDE_DIRS})
set(OpenCV_DIR /Users/yujinke/Downloads/opencv-4.0.0/build)

find_package(OPENCV REQUIRED)
#find_package(OPENCV 3.3.1 REQUIRED)

add_executable(test_ mobilenet-ssd.cpp vehicle_recg.cpp )
target_link_libraries(test_ ${OpenCV_LIBS})