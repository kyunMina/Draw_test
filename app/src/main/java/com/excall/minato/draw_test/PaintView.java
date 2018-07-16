package com.excall.minato.draw_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.Float;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by minat on 2017/11/16.
 */

public class PaintView extends View {

    private Paint paint;
    private Path path;

    private ArrayList<Float>    PointX;
    private ArrayList<Float>    PointY;
    private ArrayList<Float>    PointX_sort;
    private ArrayList<Float>    PointY_sort;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);

        PointX = new ArrayList<Float>();
        PointY = new ArrayList<Float>();
        PointX_sort = new ArrayList<Float>();
        PointY_sort = new ArrayList<Float>();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //path.lineTo(x, y);
                path.addCircle(x, y, 10, Path.Direction.CCW);
                invalidate();
                Log.d("getEventX",event.getX()+"");
                Log.d("getEventY",event.getY()+"");
                PointX.add(event.getX());
                PointY.add(event.getY());
                Log.d("popopoX",PointX.size()-1+"");
                break;
        }
        return true;
    }

    public void clear(){
        int i,p;
        p = PointX.size();

        //  Path要素削除処理
        path.reset();
        invalidate();

        //  要素削除処理
        if(p > 0) {
            for (i = 0; i < p; i++) {
                PointX.remove(0);
            }
        }
    }


    //  計算部
    public void math(){

        //  点がないときのバグ回避
        if(PointX.size() == 0)  return;

        int i,j = 0,min_num = 0,min_rad_num = 100;
        float px_low, py_low;
        double radian = 0,radian2 = 1000, radian_old = 1000;


        //  x座標最小値の導出
        px_low = PointX.get(0);
        py_low = PointY.get(0);
        for(i = 1;i < PointX.size();i++){
            if(PointX.get(i) < px_low){
                px_low = PointX.get(i);
                min_num = i;
            }
        }
        Log.d("min_num",min_num+"");
        //  end
        //  px_lowを原点とみなし、その座標が(0,0)となるようにすべての座標を移動させる
        for(i = 0;i < PointX.size();i++){
            PointX.set(i,PointX.get(i));
            PointY.set(i,PointY.get(i));
        }
        //  end
        //  y軸の負の方向から各点までの偏角が最小の点を求める。
        for(i = 0;i < PointX.size();i++){
            if (i != min_num) {
                radian = Math.cos((PointX.get(i)*px_low + PointY.get(i)*py_low)/(Math.sqrt(PointX.get(i)*PointX.get(i)+PointY.get(i)*PointY.get(i))*Math.sqrt(px_low*px_low+py_low*py_low)));
                //  偏角が小さいときの番号を記録
                if(radian < radian_old) {
                    radian_old = radian;
                    min_rad_num = i;
                }
            }
        }
        Log.d("rad",radian+"");
        Log.d("min_rad_num",min_rad_num+"");
        Log.d("min_rad_num",radian_old*180/3.14+"");

        //PointX.set(min_num,PointX.get(min_num));
        //PointY.set(min_num,PointY.get(min_num));
        //PointX.set(min_rad_num,PointX.get(min_rad_num));
        //PointY.set(min_rad_num,PointY.get(min_rad_num));

        path.moveTo(PointX.get(min_num), PointY.get(min_num));
        invalidate();
        path.lineTo(PointX.get(min_rad_num), PointY.get(min_rad_num));
        invalidate();

        Log.d("start_x",PointX.get(min_num)+"");
        Log.d("end_x",PointX.get(min_rad_num)+"");
        Log.d("start_y",PointY.get(min_num)+"");
        Log.d("end_y",PointY.get(min_rad_num)+"");
        //  end
        //  p1p2を基準とし、偏角が最小の点を求める。
        for(j=0;j<PointX.size();j++) {
            for (i = 0; i < PointX.size(); i++) {
                radian_old = 1000;
                radian = Math.cos((PointX.get(i)*PointX.get(min_rad_num) + PointY.get(i)*PointY.get(min_rad_num))/(Math.sqrt(PointX.get(i)*PointX.get(i)+PointY.get(i)*PointY.get(i))*Math.sqrt(PointX.get(min_rad_num)*PointX.get(min_rad_num)+PointY.get(min_rad_num)*PointY.get(min_rad_num))));
                //radian = Math.atan2(PointY.get(j) - PointY.get(min_rad_num), PointX.get(j) - PointX.get(min_rad_num)) - radian_old;
                if (radian < radian_old) {
                    radian_old = radian;
                    min_num = i;
                    //Log.d("min_num",min_num+"");
                }
            }
            path.moveTo(PointX.get(min_rad_num), PointY.get(min_rad_num));
            invalidate();
            path.lineTo(PointX.get(min_num), PointY.get(min_num));
            invalidate();
            Log.d("start_x",PointX.get(min_num)+"");
            Log.d("end_x",PointX.get(min_rad_num)+"");
            Log.d("start_y",PointY.get(min_num)+"");
            Log.d("end_y",PointY.get(min_rad_num)+"");
            min_rad_num = min_num;
            Log.d("min_rad_num",min_rad_num+"");
            //j++;
        }

    }
}
