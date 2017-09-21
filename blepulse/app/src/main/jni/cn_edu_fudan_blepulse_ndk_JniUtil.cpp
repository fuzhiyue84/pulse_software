#include "cn_edu_fudan_blepulse_ndk_JniUtil.h"

JNIEXPORT jstring JNICALL Java_cn_edu_fudan_blepulse_ndk_JniUtil_logCalc
        (JNIEnv * env, jclass type, jstring content){

    return env->NewStringUTF("Enter my app!");
}
