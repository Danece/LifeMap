package com.lifeMap.lifemap.DIY_Kit;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.lang.StrictMath.abs;

public class PickerView extends View {
    public static final String TAG = "PickerView";
    // Text 之間間距和 minTextSize 之比
    public static final float MARGIN_ALPHA = 3f;
    // 自動滾回中間的速度
    public static final float SPEED = 2;
    private List<String> mDataList;
    // 選中的位置，這位置是 mDataList 的中心位置，一直不變
    private int mCurrentSelected;
    private Paint mPaint;

    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mColorText = 0x333333;

    private int mViewHeight;
    private int mViewWidth;

    private int mLastDownY;
    // 滑動的距離
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    private Boolean flag = true;


    Handler updateHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }

        public void  handleMessage ( Message msg )
        {
            if ( Math . abs ( mMoveLen ) <  SPEED )
            {
                mMoveLen  = 0 ;
                if ( mTask  != null )
                {
                    mTask . cancel ();
                    mTask  = null ;
//                    performSelect ();
                }
            } else
                // 這裡mMoveLen / Math.abs(mMoveLen)是為了保有mMoveLen的正負號，以實現上滾或下滾
                mMoveLen  =  mMoveLen  -  mMoveLen  / Math . abs ( mMoveLen ) *  SPEED ;
            invalidate ();
        }
    };

    public PickerView ( Context context )
    {
        super ( context );
        init ();
    }

    public PickerView ( Context  context , AttributeSet attrs )
    {
        super ( context ,  attrs );
        init ();
    }

    public void  setOnSelectListener ( onSelectListener listener )
    {
        mSelectListener  =  listener ;
    }

    public void  performSelect () {
        if (mSelectListener != null)
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
    }

    public String getSelected() {
        String result = new String();
        result = mDataList . get ( mCurrentSelected );
        return result;
    }

    public void  setData ( List < String >  datas )
    {
        mDataList  =  datas ;
        mCurrentSelected  =  datas . size () / 2 ;
        invalidate ();
    }

    public void  setSelected ( int  selected )
    {
        mCurrentSelected  =  selected ;
    }

    private void  moveHeadToTail ()
    {
        String  head  =  mDataList . get ( 0 );
        mDataList . remove ( 0 );
        mDataList . add ( head );
    }

    private void  moveTailToHead ()
    {
        String  tail  =  mDataList . get ( mDataList . size () - 1 );
        mDataList . remove ( mDataList . size () - 1 );
        mDataList . add ( 0 ,  tail );
    }

    @Override
    protected void  onMeasure ( int  widthMeasureSpec , int  heightMeasureSpec )
    {
        super . onMeasure ( widthMeasureSpec ,  heightMeasureSpec );
        mViewHeight  =  getMeasuredHeight ();
        mViewWidth  =  getMeasuredWidth ();
        // 按照View的高度計算字體大小
        mMaxTextSize  =  mViewHeight  / 3.0f ;
        mMinTextSize  =  mMaxTextSize  / 2f ;
        isInit  = true ;
        invalidate ();
    }

    private void  init ()
    {
        AssetManager mgr=getContext().getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        timer  = new Timer ();
        mDataList  = new ArrayList< String >();
        mPaint  = new Paint ( Paint . ANTI_ALIAS_FLAG );
        mPaint . setStyle ( Paint.Style.FILL );
        mPaint . setTextAlign ( Paint.Align.CENTER );
        mPaint . setColor ( mColorText );
        mPaint . setTypeface(tf);

    }

    @Override
    protected void  onDraw ( Canvas canvas )
    {
        super . onDraw ( canvas );
        // 根據index繪製view
        if ( isInit )
            drawData ( canvas );
    }

    private void  drawData ( Canvas  canvas )
    {
        // 先繪製選中的text再往上往下繪製其餘的text
        float  scale  =  parabola ( mViewHeight  / 4.0f ,  mMoveLen );
        float  size  = ( mMaxTextSize  -  mMinTextSize ) *  scale  +  mMinTextSize ;
        mPaint . setTextSize ( size );
        mPaint . setAlpha (( int ) (( mMaxTextAlpha  -  mMinTextAlpha ) *  scale  +  mMinTextAlpha ));
        // text居中繪製，注意baseline的計算才能達到居中，y值是text中心坐標
        float  x  = ( float ) ( mViewWidth  / 2.0 );
        float  y  = ( float ) ( mViewHeight  / 2.0 +  mMoveLen );
        Paint.FontMetricsInt fmi  =  mPaint . getFontMetricsInt ();
        float  baseline  = ( float ) ( y  - ( fmi . bottom  / 2.0 +  fmi . top  / 2.0 ));

        canvas . drawText ( mDataList . get ( mCurrentSelected ),  x ,  baseline ,  mPaint );
        // 繪製上方data
        for ( int  i  = 1 ; ( mCurrentSelected  -  i ) >= 0 ;  i ++)
        {
            drawOtherText ( canvas ,  i , - 1 );
        }
        // 繪製下方data
        for ( int  i  = 1 ; ( mCurrentSelected  +  i ) <  mDataList . size ();  i ++)
        {
            drawOtherText ( canvas ,  i , 1 );
        }

    }

    /**
     * @param canvas
     * @param position
     * 距離mCurrentSelected的差值
     * @param type
     * 1表示向下繪製，-1表示向上繪製
     */
    private void  drawOtherText ( Canvas  canvas , int  position , int  type )
    {
        float  d  = ( float ) ( MARGIN_ALPHA  *  mMinTextSize  *  position  +  type
                *  mMoveLen );
        float  scale  =  parabola ( mViewHeight  / 4.0f ,  d );
        float  size  = ( mMaxTextSize  -  mMinTextSize ) *  scale  +  mMinTextSize ;
        mPaint . setTextSize ( size );
        mPaint . setAlpha (( int ) (( mMaxTextAlpha  -  mMinTextAlpha ) *  scale  +  mMinTextAlpha ));
        float  y  = ( float ) ( mViewHeight  / 2.0 +  type  *  d );
        Paint.FontMetricsInt fmi  =  mPaint.getFontMetricsInt ();
        float  baseline  = ( float ) ( y  - ( fmi.bottom  / 2.0 +  fmi.top  / 2.0 ));
        canvas . drawText ( mDataList.get ( mCurrentSelected  +  type  *  position ),
                ( float ) ( mViewWidth  / 2.0 ),  baseline ,  mPaint );
    }

    /**
     * 拋物線
     *
     * @param zero
     * 零點坐標
     * @param x
     * 偏移量
     * @return scale
     */
    private float  parabola ( float  zero , float  x )
    {
        float  f  = ( float ) ( 1 - Math . pow ( x  /  zero , 2 ));
        return  f  < 0 ? 0 :  f ;
    }

    @Override
    public boolean  onTouchEvent ( MotionEvent event )
    {
        switch ( event . getActionMasked ())
        {
            case MotionEvent . ACTION_DOWN :
                doDown ( event );
                break ;
            case MotionEvent . ACTION_MOVE :
                doMove ( event );
                break ;
            case MotionEvent . ACTION_UP :
                doUp ( event );
                break ;
        }
        return true ;
    }

    private void  doDown ( MotionEvent event )
    {
        if ( mTask  != null )
        {
            mTask . cancel ();
            mTask  = null ;
        }
        if (flag) {
            flag = false;
        }

        mLastDownY  = (int) event.getY();
    }

    private void  doMove ( MotionEvent event )
    {

        mMoveLen  += ( event.getY() -  mLastDownY );

        if ( mMoveLen  >  MARGIN_ALPHA  *  mMinTextSize  / 2 )
        {
            // 往下滑超過離開距離
            moveTailToHead ();
            mMoveLen  =  mMoveLen  -  MARGIN_ALPHA  *  mMinTextSize ;
        } else if ( mMoveLen  < - MARGIN_ALPHA  *  mMinTextSize  / 2 )
        {
            // 往上滑超過離開距離
            moveHeadToTail ();
            mMoveLen  =  mMoveLen  +  MARGIN_ALPHA  *  mMinTextSize ;
        }

        mLastDownY  = (int) event.getY();
        invalidate ();
    }

    private void  doUp ( MotionEvent event )
    {
        // 抬起手後mCurrentSelected的位置由當前位置move到中間選中位置
        if ( Math . abs ( mMoveLen ) < 0.0001 )
        {
            mMoveLen  = 0 ;
            return ;
        }
        if ( mTask  != null )
        {
            mTask . cancel ();
            mTask  = null ;
        }
        mTask  = new MyTimerTask ( updateHandler );
        timer . schedule ( mTask , 0 , 10 );

        flag = true;
        while(flag) {
            runCenter();
        }
    }

    /* 滾回中間位置 */
    private void runCenter() {
        if (abs(mMoveLen) < SPEED) {
            mMoveLen = 0f;
            if (flag) {
                flag = false;
                performSelect();
            }
        } else {
            // 這裡mMoveLen / Math.abs(mMoveLen)是為了保有mMoveLen的正負號，以實現上滾或下滾
            mMoveLen -= mMoveLen / abs(mMoveLen) * SPEED;
        }
        invalidate();
    }

    class MyTimerTask extends TimerTask {
        Handler handler;
        public MyTimerTask (Handler handler) {
            this.handler = handler;
        }
        @Override
        public void run() {
            handler.setErrorManager(handler.getErrorManager());
        }
    }

    public interface onSelectListener {
        void onSelect(String text);
    }
}
