cmake_minimum_required(VERSION 3.23)
project(redis-src C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(redis-src
        sds_test.c
        sds_test.h)
