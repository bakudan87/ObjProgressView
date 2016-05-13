package com.bkdn.androidapp.objprogressview;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Victor Souza on 11/05/2016.
 */
interface Shape {
    void draw(Canvas canvas, Paint paint);
    
    void setPosition(int x, int y);
    
    void setScale(float scale);
    
    int getWidth();
    
    int getHeight();
}
