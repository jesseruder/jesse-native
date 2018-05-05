#include <jni.h>
#include <string>
#include "duktape/duktape.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_jessenative_host_exp_jessenative_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    duk_context *ctx = duk_create_heap_default();
    duk_eval_string(ctx, "1+2*5");
    int value = (int) duk_get_int(ctx, -1);
    duk_destroy_heap(ctx);

    char numstr[21]; // enough to hold all numbers up to 64-bits
    sprintf(numstr, "%d", value);

    std::string hello = "Hello from C++";
    std::string result = hello + numstr;
    return env->NewStringUTF(result.c_str());
}
