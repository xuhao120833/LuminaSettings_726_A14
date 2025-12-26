#include <jni.h>
#include <string>
#include <android/log.h>
#include <linux/agpgart.h>
#include "scdefine.h"


////////////////////////////////////////JNIEXPORT///////////////////////////////////////////////////
extern void ratio_tra_point(int *pRet, int *px4, int *py4, int oldRatio, int newRatio, float scale, int w, int h);
extern int check_bd_data(char *pBuf);
////////////////////////////////xtouch///////////////////////////////////////////////

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_htc_luminasettings_utils_PxScale_getpxRatioxy(JNIEnv *env, jobject thiz, jintArray px4, jintArray py4, jint oldRatio, jint newRatio, jfloat scale, jint w, jint h) {
    jintArray intArray = env->NewIntArray(20);
    jint *intdata = env->GetIntArrayElements(intArray, NULL);
    jint *tpx4 = NULL;
    jint *tpy4 = NULL;
    if(px4!=NULL){
        tpx4 = (jint *) env->GetIntArrayElements(px4, 0);
    }else{
        tpx4 = NULL;
    }
    if(py4!=NULL){
        tpy4 = (jint *) env->GetIntArrayElements(py4, 0);
    }else{
        tpy4 = NULL;
    }
    LOGD("CPP: Java_com_htc_luminasettingsdebug_PxScale_getpxRatioxy");
    memset(intdata,0,sizeof(int)*20);
    ratio_tra_point(intdata, tpx4, tpy4, oldRatio,newRatio, scale, w, h);
    env->ReleaseIntArrayElements(intArray, intdata, 0);
    return intArray;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_htc_luminasettings_utils_PxScale_checkbddata(JNIEnv *env, jobject thiz, jstring data) {
	jint ret = 0;
	int i;
	char c,checkbuf[2048];
	const char *str;
	//LOGD("CPP: Java_com_htc_luminasettings_util_PxScale_checkbddata");
	str = env->GetStringUTFChars(data, NULL);
	if(str == NULL){
		return 0;
	}else{
		memset(checkbuf, 0, 2048);
		for(i=0;i<2048;i++){
			c = str[i];
			if((c>='0' && c<='9') || (c>='a' && c<='z') || (c>='A' && c<='Z')){
                checkbuf[i] = c;
			}else{
				break;
			}
		}
		ret = check_bd_data(checkbuf);
	}
	return ret;
}