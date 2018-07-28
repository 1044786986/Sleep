package com.example.ljh.sleep.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ljh.sleep.R;

public class CircleImageDrawable extends Drawable{
    private Paint paint;
    private Matrix matrix;
    private Bitmap bitmap;
    private int r;

    public CircleImageDrawable(Context context,Bitmap bitmap){
        r = (int) context.getResources().getDimension(R.dimen.dp_30);
//        r = ConvertUtils.dp2px(context,r);

        float scale = (r * 2.0f) / Math.min(bitmap.getWidth(),bitmap.getHeight());
        matrix = new Matrix();
        matrix.setScale(scale,scale);

        paint = new Paint();
        paint.setAntiAlias(true);

        BitmapShader bitmapShader = new BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        Log.i("aaa","CircleImageDrawable.r = " + r);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(r,r,r,paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return r*2;
    }

    @Override
    public int getIntrinsicHeight() {
        return r*2;
    }

    public int getR(){
        return r;
    }
}
