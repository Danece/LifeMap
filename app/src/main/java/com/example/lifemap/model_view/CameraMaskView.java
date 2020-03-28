package com.example.lifemap.model_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CameraMaskView extends View {

    /**
     * 遮罩顏色
     */
    private int maskColor = Color.argb(100, 0, 0, 0);

    /**
     * 鏤空矩形
     */
    private Rect frame = new Rect();

    /**
     * 鏤空邊框
     */
    private Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 鏤空區域
     */
    private Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    public CameraMaskView(Context context) {
        super(context);
        init();
    }

    public CameraMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 硬件加速不支持，圖層混合。
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 取景框顏色、線寬
        border.setColor(Color.WHITE);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(5);

        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = (int) (w * 0.7f);
        int height = (int) (h * 0.9f);
        Log.d("XX-onSizeChanged", w+","+h);
        int left = (w - width) / 2;
        int top = (h - height) / 2;
        int right = width + left;
        int bottom = h/2 - top ;

        frame.left = left;
        frame.top = top;
        frame.right = right;
        frame.bottom = bottom;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect frame = this.frame;

        int left = frame.left;
        int top = frame.top;
        int right = frame.right;
        int bottom = frame.bottom;

        fillRectRound(left, top, right, bottom, 30, 30);


        canvas.drawColor(maskColor);
        canvas.drawPath(path, border);
        canvas.drawPath(path, eraser);

    }

    private void fillRectRound(float left, float top, float right, float bottom, float rx, float ry) {
        path.reset();

        float width = right - left;
        float height = bottom - top;

        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry);
        path.rLineTo(0, heightMinusCorners);

        path.rQuadTo(0, ry, rx, ry);
        path.rLineTo(widthMinusCorners, 0);
        path.rQuadTo(rx, 0, rx, -ry);
        path.rLineTo(0, -heightMinusCorners);

        path.close();
    }

    public Rect getFrameRect() {
        return new Rect(frame);
    }

}
