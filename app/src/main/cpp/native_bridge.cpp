#include <jni.h>
#include <string>
#include "geometry.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_dataminde_app_NativeBridge_engineInfo(JNIEnv* env, jclass) {
    const std::string info = "Dataminde Native Core · C++17";
    return env->NewStringUTF(info.c_str());
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_dataminde_app_NativeBridge_distance(
        JNIEnv*, jclass,
        jfloat x1, jfloat y1,
        jfloat x2, jfloat y2) {
    return static_cast<jfloat>(dm::distance(x1, y1, x2, y2));
}
