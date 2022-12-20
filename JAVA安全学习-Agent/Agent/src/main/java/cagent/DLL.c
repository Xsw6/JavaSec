#include "jvmti.h"
#include <iostream>

/*
 * java agent有2个启动函数分别为Agent_OnLoad和Agent_OnAttach
 * Agent_OnLoad在onload阶段被调用
 * Agent_OnAttach在live阶段被调用
 * 但是每个agent只有一个启动函数会被调用
 */

 /*
  * 此阶段JVM还没有初始化，所以能做的操作比较受限制
  * JVM参数都无法获取
  * The return value from Agent_OnLoad is used to indicate an error.
  * Any value other than zero indicates an error and causes termination of the VM.
  * 任何非零的返回值都会导致JVM终止。
  */

//JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
//    printf("Agent_OnLoad\n");
//     //system("calc");
//    return JNI_OK;
//}


JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char* options, void* reserved) {
    printf("Agent_OnAttach\n");
    system("calc");
    return JNI_OK;
}

/*
* This function can be used to clean-up resources allocated by the agent.
*/
//JNIEXPORT void JNICALL Agent_OnUnload(JavaVM* vm) {
//    printf("Agent_OnUnload\n");
//}