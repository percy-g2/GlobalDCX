#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_androdevlinux_percy_globaldcx_utils_NativeUtils_getBitfinexBaseUrl(JNIEnv *env, jclass type) {

    char * baseUrl ="https://api.bitfinex.com/";
    return (*env)->NewStringUTF(env, baseUrl);
}