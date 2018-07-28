package com.example.ljh.sleep.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 压缩图片工具
 */
public class CompressUtils{
    private  final int maxSize = 200;   //限定图片最大200kb
    private  int screenWidth;           //屏幕X大小
    private  int screenHeight;          //Y大小
//    private Handler handler = new Handler(Looper.getMainLooper());
    private static CompressUtils compressUtils;

    public CompressUtils(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static CompressUtils getInstance(Context context){
        if(compressUtils == null){
            synchronized(CompressUtils.class){
                compressUtils = new CompressUtils(context);
                return compressUtils;
            }
        }
        return compressUtils;
    }

    public Bitmap compress(Bitmap bitmap,int width,int height){
        if(width == 0){
            width = screenWidth;
        }
        if(height == 0){
            height = screenHeight;
        }
        Log.i("aaa","width = " + width + "  height = " + height + "bitmap = " + bitmap);
        Bitmap bitmapResult = null;
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        int scale = 1;
        int quality = 100;

        if(bWidth > width && bWidth >= bHeight){
            scale = bWidth / width;
        }else if(bHeight > height && bHeight >= bWidth){
            scale = bHeight / height;
        }
        if(scale <= 0){
            scale = 1;
        }
        bitmapResult = Bitmap.createBitmap(bWidth/scale,bHeight/scale,Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapResult);
        Rect rectDst = new Rect(0,0,bWidth/scale,bHeight/scale);
        canvas.drawBitmap(bitmap,null,rectDst,null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapResult.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        while (byteArrayOutputStream.toByteArray().length > maxSize){
            byteArrayOutputStream.reset();
            quality -= 10;
            bitmapResult.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
            if(quality <= 30){
                break;
            }
        }
        if(bitmap != null){
            bitmap.recycle();
        }

        try {
            byteArrayOutputStream.reset();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("mainActivity","compress.size = " + bitmapResult.getByteCount() / 1024);
        return bitmapResult;
    }
}
