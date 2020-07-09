#include <jni.h>
#include <string>
//#include "ssh_detector.h"
#include <android/log.h>
#include "vehicle_recg.h"



#define FEATURE_LEN  512

std::string jstring2str(JNIEnv* env, jstring jstr)
{
    char*   rtn   =   NULL;
    jclass   clsstring   =   env->FindClass("java/lang/String");
    jstring   strencode   =   env->NewStringUTF("GB2312");
    jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
    jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
    jsize   alen   =   env->GetArrayLength(barr);
    jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
    if(alen>0)
    {
        rtn   =   (char*)malloc(alen+1);
        memcpy(rtn,ba,alen);
        rtn[alen]=0;
    }
    env->ReleaseByteArrayElements(barr,ba,0);
    std::string stemp(rtn);
    free(rtn);
    return   stemp;
}




bool check()
{
    time_t timer;
    time(&timer);
    tm* t_tm = localtime(&timer);
    char log_info[40];
    __android_log_print(ANDROID_LOG_INFO,"time_info","year:%d month:%d",t_tm->tm_year,t_tm->tm_mon);// LOG类型:info
    if (t_tm->tm_mon==0&&t_tm->tm_year==119){
        return true;
    }
    return false;
}


extern "C" {

    int call_times = 0;

    JNIEXPORT jlong JNICALL Java_recognition_VehicleRecognition_InitialInstance
            (JNIEnv *env, jobject obj, jstring folder){

            std::string folder_ = jstring2str(env,folder);
            VEHICLE_RECG *vehicle_recog= new VEHICLE_RECG(folder_);
            return (long)vehicle_recog;

    }

    JNIEXPORT jstring JNICALL Java_recognition_VehicleRecognition_RecognizeRGBA
            (JNIEnv *env, jobject obj, jlong handle, jintArray rgba, jint height , jint width){

            VEHICLE_RECG *vehicle_recog = (VEHICLE_RECG*)handle;
            //FaceRecognition *recognition = (FaceRecognition*)handle;
            jint *pFrameData = env->GetIntArrayElements(rgba, 0);
            Mat mFrame = Mat(height,width,CV_8UC4,pFrameData).clone();
            Mat rgb ;
            cv::cvtColor(mFrame,rgb,cv::COLOR_RGBA2RGB);
            std::pair<std::string,float> res = vehicle_recog->detect(rgb);
            env->ReleaseIntArrayElements(rgba, pFrameData, 0);
            return  env->NewStringUTF(res.first.c_str());

    }




}




