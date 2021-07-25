//
// Created by Administrator on 2021/7/9.
//
//
// Created by Administrator on 2021/7/9.
//
#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <stdio.h>
#include <sys/mman.h>
#include <unistd.h>
#include <android/log.h>
#include <string>
#include <sys/ioctl.h>


#define TAG "myDemo-jni" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

void delay10ms() {
    unsigned int a,b,c;
    for(c=1;c>0;c--)
        for(b=38;b>0;b--)
            for(a=1300000;a>0;a--);
}

extern "C"
JNIEXPORT jint  JNICALL
Java_com_example_naviwake_activity_MainActivity_ledon(JNIEnv *env, jobject thiz) {
    // TODO: implement ledon()
    int len;
    int fd;

    char buffer[1800];
/*
 * O_RDONLY 以只读方式打开文件
  O_WRONLY 以只写方式打开文件
   O_RDWR 以可读写方式打开文件。上述三种旗标是互斥的，也就是不可同时使用
 * */
    //__android_log_print(ANDROID_LOG_ERROR, "TAG", "lsx_init");
    fd = open("/sys/devices/virtual/misc/mtgpio/pin", O_RDWR);
    if(fd<0){
        __android_log_print(ANDROID_LOG_ERROR, "TAG", "fail");
    }
    //write(fd,gpio87,sizeof(gpio87));
    read(fd,buffer,sizeof(buffer));
    //LOGE("########## len = %d", len);
    //__android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1276]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1277]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1278]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1279]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1280]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1281]);
    // __android_log_print(ANDROID_LOG_ERROR, "TAG", "%c", buffer[1282]);
    /*if(buffer[1281]=='0') {
        delay10ms();
        if(buffer[1281]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "jian");
            return 1;
            //system("input keyevent 24");
        }
    }
    if(buffer[1761]=='0') {
        delay10ms();
        if(buffer[1761]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "add");
            return 2;
        }
    }*/
    if(buffer[1281]=='0') {
        delay10ms();
        if(buffer[1281]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "jian");
            return 1;
            //system("input keyevent 24");
        }
    }
    if(buffer[1761]=='0') {
        delay10ms();
        if(buffer[1761]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "add");
            return 2;
        }
    }
    if(buffer[917]=='0') {
        delay10ms();
        if(buffer[917]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "jian");
            return 1;
        }
    }
    if(buffer[930]=='0') {
        delay10ms();
        if(buffer[930]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "add");
            return 2;
        }
    }
    if(buffer[135]=='0') {
        delay10ms();
        if(buffer[135]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "jian");
            return 1;
        }
    }
    if(buffer[148]=='0') {
        delay10ms();
        if(buffer[148]=='0') {
            __android_log_print(ANDROID_LOG_ERROR, "TAG1", "add");
            return 2;
        }
    }
    close(fd);
    //__android_log_print(ANDROID_LOG_ERROR, "TAG", "lsx_init");
    return 0;
}

