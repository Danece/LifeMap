package com.example.lifemap.DIY_Kit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;

import java.io.ByteArrayOutputStream;

public class BitmapCut {

    // 四邊角圓弧化
    public static Bitmap removeCorner(Bitmap bitmap, int pixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap creBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(creBitmap);

        Paint paint = new Paint();
        float roundPx = pixels;
        RectF rectF = new RectF(0, 0, width - roundPx, height - roundPx);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        /*if(!bitmap.isRecycled()) {
            bitmap.recycle();
        }*/

        return creBitmap;
    }

    // 裁成正方形
    public static Bitmap imageToSquare(Bitmap bitmap, boolean isRecycled) {
        if(null == bitmap) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 切割後所取的正方形邊長
//        int wh = width > height?height:width;
        Log.d("XX", Integer.toString(width)+","+Integer.toString(height));
//        Log.d("XX", "wh:"+Integer.toString(wh));
//        int wh = width > height?height:width;
        int wh = 175;
        // 基於原圖，取正方形左上角X座標
//        int retX = width > height ? (width - height) / 2 : 0;
        int retX = width > height ? 0 : (height - width) / 2;
        int retY = width > height ? 0 : (height - width) / 4;
//        int retX = width > height ? width : 450;
//        int retY = width > height ? 0 : width/4;

        Log.d("XX", Integer.toString(retX)+","+Integer.toString(retY));

        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
        /*if( isRecycled && null != bitmap && !bitmap.equals(bmp)) {
            bitmap.recycle();
            bitmap = null;
        }*/
        return bmp;
    }

    // Bitmap -> byte[]
    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        try{
            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    // 裁成圓形
    public static Bitmap imageToCircle(Bitmap bitmap) {
        if(null == bitmap) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float circlePx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

        if(width <= height) {
            circlePx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            circlePx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, circlePx, circlePx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        /*if(null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }*/
        return outputBitmap;
    }

    // 合併兩個 Bitmap
    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground, int type) {
        if(null == background) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        switch (type) {
            case 0:
                foreground = resizeBitmap(foreground,bgWidth+15,bgWidth+15);
                break;
            case 1:
                foreground = resizeBitmap(foreground,bgWidth-25,bgWidth-25);
                break;
        }
        Bitmap newBitmap = Bitmap.createBitmap(bgWidth,bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, bgWidth/16, bgWidth/16, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }

    // 改變大小
    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
