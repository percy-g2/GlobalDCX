cmake_minimum_required(VERSION 3.4.1)

add_library(globaldcx-jni SHARED
            globaldcx-jni.c)

# Include libraries needed for mimba-jni lib
target_link_libraries(globaldcx-jni
                      android
log)
