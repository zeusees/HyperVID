package recognition;

import android.graphics.Bitmap;

import java.util.List;


public class VehicleRecognition { static {
        System.loadLibrary("vehicle-lib");
    }


    static final int FORMAT_NV21= 0 ;
    static final int FORMAT_RGBA= 1 ;
    static final int ERROR_CODE_FACE_NUM = -2;


    native long InitialInstance(String folder);
    native String RecognizeRGBA(long handle , int image[], int height, int width);
    private long handleSession;


    public VehicleRecognition(String modelFolder){
      handleSession = InitialInstance(modelFolder);
    }


    public String ZeuseesFaceDection(Bitmap bitmap)
    {
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, mWidth, 0, 0, mWidth, mHeight);

        String faceinfo = RecognizeRGBA(handleSession,argb,mHeight,mWidth);
        System.out.println(faceinfo);
        return faceinfo;
    }


}
