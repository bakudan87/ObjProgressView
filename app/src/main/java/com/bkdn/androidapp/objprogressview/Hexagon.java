package com.bkdn.androidapp.objprogressview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Victor Souza on 11/05/2016.
 */
class Hexagon implements Shape, Scalable {

    private RectF mBounds;
    private Path mPath;
    private float mScale;
    private int x;
    private int y;

    private Scalator mScalator;

    public Hexagon(int size) {
        final int halfSize = size / 2;
        final int v = (int) (halfSize / 1.89f);
        final int h = (int) (halfSize * 0.89f);

        mPath = new Path();
        mPath.moveTo(0, v);
        mPath.rLineTo(0, halfSize);
        mPath.rLineTo(h, v);
        mPath.rLineTo(h, -v);
        mPath.rLineTo(0, -halfSize);
        mPath.rLineTo(-h, -v);
//        mPath.moveTo(size/2,0);
//        mPath.rLineTo(-size/2,size);
//        mPath.rLineTo(size,0);
        mPath.close();

        RectF bounds = new RectF();
        mPath.computeBounds(bounds, true);

        mBounds = new RectF();
        mPath.computeBounds(mBounds, false);
        mScalator = new Scalator();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        final RectF bounds = new RectF();
        mScale = mScalator.scale();
        mPath.computeBounds(bounds, false);
        final int halfWidth = (int) (bounds.width() / 2);

        canvas.save();
        canvas.translate(x, y);
        canvas.scale(mScale, mScale, halfWidth, bounds.height() / 2);
        canvas.drawPath(mPath, paint);
        canvas.restore();
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setScale(float scale) {
        mScale = scale;
    }

    @Override
    public int getWidth() {
        return (int) mBounds.width();
    }

    @Override
    public int getHeight() {
        return (int) mBounds.height();
    }

    @Override
    public void grow(int duration, int startOffset) {
        mScalator.grow(duration, startOffset);
    }

    @Override
    public void shrink(int duration, int startOffset) {
        mScalator.shrink(duration, startOffset);
    }

    @Override
    public void startAnimation() {
        Log.w(getClass().getName(), "Call grow() or shrink()");
        // no-op
    }

    @Override
    public void stopAnimation() {
        mScalator.stopAnimators();
    }
}
