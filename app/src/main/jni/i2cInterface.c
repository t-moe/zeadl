/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        Basic i2c communication interface.
 *	        Only a minimal error handling is implemented.
 * \file    i2Interface.c
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 * \remark V2.0 langt1 8.3.2015
 ***************************************************************************
 */

#undef __cplusplus

#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>


#include <linux/i2c.h>
#include <memory.h>
#include <malloc.h>

#include <jni.h>

#include <android/log.h>

#include "ch_bfh_android_zeadl_I2C.h"

#define JNIEXPORT __attribute__ ((visibility ("default")))

/* Define Log macros */
#define  LOG_TAG    "i2c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/********************************************************************************************************************************/

JNIEXPORT jboolean JNICALL Java_ch_bfh_android_zeadl_I2C_open(JNIEnv *env, jobject that)
{
    //Get the necessary field form the instance
    jclass jclass_i2c = (*env)->GetObjectClass(env, that);

    jfieldID jfield_devicename=(*env)->GetFieldID(env,jclass_i2c, "mDeviceName" , "Ljava/lang/String;");
    jfieldID jfield_i2cadress= (*env)->GetFieldID(env,jclass_i2c, "mI2CAdress" , "I");
    jfieldID jfield_filehandle= (*env)->GetFieldID(env,jclass_i2c, "mFileHandle" , "I");

    jstring jstr_devicename = (*env)->GetObjectField(env, that, jfield_devicename);
    jint jint_i2cadress = (*env)->GetIntField(env,that,jfield_i2cadress);

    const char* devicename = (*env)->GetStringUTFChars(env,jstr_devicename, 0);

    //Try to open the device
    int i2c_fd = open(devicename, O_RDWR);
    if(i2c_fd<0) {
        LOGE("I2C: Can't open device node %s. %s",devicename,strerror(errno));
        (*env)->ReleaseStringUTFChars(env,jstr_devicename, devicename);
        (*env)->SetIntField(env,that,jfield_filehandle,-1);
        return 0;
    }

    (*env)->ReleaseStringUTFChars(env,jstr_devicename, devicename);

    //Try to set the slave adress
    int res = ioctl(i2c_fd, I2C_SLAVE, jint_i2cadress);
    if (res != 0)
    {
        LOGE("I2C: Can't set slave address! %s",strerror(errno));
        (*env)->SetIntField(env,that,jfield_filehandle,-1);
        return 0;
    }
    (*env)->SetIntField(env,that,jfield_filehandle,i2c_fd);
     LOGI("I2C: Device opened. Dev: %d, Addr: 0x%x",i2c_fd,jint_i2cadress);

    return 1;
}

JNIEXPORT jint JNICALL Java_ch_bfh_android_zeadl_I2C_read(JNIEnv * env, jobject that, jbyteArray arr, jint count)
{
    //Get the necessary field form the instance
    jclass jclass_i2c = (*env)->GetObjectClass(env, that);

    jfieldID jfield_filehandle= (*env)->GetFieldID(env,jclass_i2c, "mFileHandle" , "I");
    jint jint_filehandle = (*env)->GetIntField(env,that,jfield_filehandle);

    //Checks
    if(jint_filehandle < 0) {
        LOGE("I2C: Device not opened?");
        return -1;
    }

    if (count <= 0)
    {
  	    LOGE("I2C: array size <= 0");
        return -1;
    }

    //Buffer allocation
    char* bufByte = malloc(count);
    if (bufByte == 0)
    {
        LOGE("I2C: Out of memory!");
        return -1;
    }
    memset(bufByte, '\0', count);


    //Reading
    char bytesRead;
    if ((bytesRead = read(jint_filehandle, bufByte, count)) != count)
    {
        LOGE("I2C read failed! %s",strerror(errno));
        free(bufByte);
        return -1;
    }
    else
    {
        //LOGI("I2C: using dev %d, received %d bytes ",jint_filehandle,bytesRead);
        //LOGI("I2C: first two bytes are: 0x%x%x ",bufByte[0],bufByte[1]);
        (*env)->SetByteArrayRegion(env, arr, 0, count, bufByte);
    }
    free(bufByte);
    return count;
}


JNIEXPORT jint JNICALL Java_ch_bfh_android_zeadl_I2C_write(JNIEnv * env, jobject that, jbyteArray arr, jint count) {
    //Get the necessary field form the instance
    jclass jclass_i2c = (*env)->GetObjectClass(env, that);

    jfieldID jfield_filehandle= (*env)->GetFieldID(env,jclass_i2c, "mFileHandle" , "I");
    jint jint_filehandle = (*env)->GetIntField(env,that,jfield_filehandle);

    //Checks
    if ((count <= 0) || (count > 255))
    {
        LOGE("I2C: array size <= 0 | > 255");
        return -1;
    }

    if(jint_filehandle < 0) {
        LOGE("I2C: Device not opened?");
        return -1;
    }

    jbyte * byteArr = (*env)->GetByteArrayElements(env, arr, NULL);

    if (byteArr==NULL) return 0;

    jsize arrsize = (*env)->GetArrayLength(env, arr);
    if(arrsize<count) {
        LOGE("I2C: not enough elements in array");
        (*env)->ReleaseByteArrayElements(env, arr, byteArr, 0);
        return -1;
    }

    //Writing
    int bytesWritten = write(jint_filehandle, byteArr, count);
    if (bytesWritten != count)
    {
        LOGE("Write to the i2c device failed! %s",strerror(errno));
        (*env)->ReleaseByteArrayElements(env, arr, byteArr, 0);
        return -1;
    }

    //LOGI("I2C: using dev %d, wrote %d bytes ",jint_filehandle,bytesWritten);
    //LOGI("I2C: first two bytes are: 0x%x%x ",byteArr[0],byteArr[1]);

    (*env)->ReleaseByteArrayElements(env, arr, byteArr, 0);
    return bytesWritten;
}

JNIEXPORT void JNICALL Java_ch_bfh_android_zeadl_I2C_close(JNIEnv * env, jobject that)
{
    //Get the necessary field form the instance
    jclass jclass_i2c = (*env)->GetObjectClass(env, that);
    jfieldID jfield_filehandle= (*env)->GetFieldID(env,jclass_i2c, "mFileHandle" , "I");
    jint jint_filehandle = (*env)->GetIntField(env,that,jfield_filehandle);

    close(jint_filehandle);

    (*env)->SetIntField(env,that,jfield_filehandle,-1);
}
