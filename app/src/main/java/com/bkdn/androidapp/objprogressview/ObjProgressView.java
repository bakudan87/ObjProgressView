package com.bkdn.androidapp.objprogressview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;


/**
 * Created by Victor Souza on 11/05/2016.
 */
public class ObjProgressView extends View {

    private static final float V_MULTIPLER = 0.75f;
    private static final int DEFAULT_COLOR = Color.parseColor("#FFB2B2B2");

    private Paint mPaint;
    private int mSize;
    private int mSpacing;
    private int mColor = DEFAULT_COLOR;
    private Shape[] mShapes;
    private int mGravity = android.view.Gravity.CENTER;  // TODO make it configurable
    private int[] mPattern = {3, 4, 5, 4, 3};  // TODO make it configurable
    private int mCount;
    private int mRows;
    private int mMaxCols;


    public ObjProgressView(Context context) {
        this(context, null);
    }

    public ObjProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObjProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final float density = getResources().getDisplayMetrics().density;
        final int defaultSize = (int) (60 * density);

        if (null != attrs) {
            TypedArray style = getResources().obtainAttributes(attrs, R.styleable.ObjProgressView);

            try {
                final String androidNamespace = "http://schemas.android.com/apk/res/android";
                final String colorResType = attrs.getAttributeValue(androidNamespace, "color");

                if (null != colorResType) {
                    if (colorResType.startsWith("#")) {
                        mColor = Color.parseColor(colorResType);
                    } else {
                        mColor = getResources().getColor(attrs.getAttributeResourceValue(
                                androidNamespace, "color", mColor));
                    }
                }

                mSize = style.getDimensionPixelSize(R.styleable.ObjProgressView_size, defaultSize);
            } finally {
                style.recycle();
            }
        } else {
            mSize = defaultSize;
        }

        mCount = 0;
        for (Integer cols : mPattern) {
            mCount += cols;
        }

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ObjProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);

        int shapes = mCount;
        int patternIndex = 0;
        int currPattern;
        while (shapes > 0) {
            currPattern = mPattern[patternIndex % mPattern.length];
            shapes -= currPattern;
            mRows++;
            patternIndex++;

            if (currPattern > mMaxCols) {
                mMaxCols = currPattern;
            }
        }

        mSpacing = (int) (mSize / 5f);
        mPaint.setPathEffect(new CornerPathEffect(mSize / 6f));
        mShapes = getShapes(mSize, mCount);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animateShapes();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation();
    }

    private void animateShapes() {
        post(grow(200, 50));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Shape shape = mShapes[0];
        int defaultWidth = getTotalWidth(shape);
        int defaultHeight = getTotalHeight(shape);

        boolean recalculateShapeSize = false;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                recalculateShapeSize = true;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = defaultWidth;
                break;
            case MeasureSpec.AT_MOST:
                if (width > defaultWidth) {
                    width = defaultWidth;
                } else {
                    recalculateShapeSize = true;
                }
                break;
        }

        int height = MeasureSpec.getSize(heightMeasureSpec);
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                recalculateShapeSize = true;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = defaultHeight;
                break;
            case MeasureSpec.AT_MOST:
                if (height > defaultHeight) {
                    height = defaultHeight;
                } else {
                    recalculateShapeSize = true;
                }
                break;
        }

        if (recalculateShapeSize) {
            recalculate(width, height);
            mShapes = getShapes(mSize, mCount);
            width = getTotalWidth(mShapes[0]);
            height = getTotalHeight(mShapes[0]);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        positionShapes(right - left, bottom - top, mShapes);
    }

    private int getTotalWidth(Shape shape) {
        int shapesWideSpace = (shape.getWidth() * mMaxCols) + (mSpacing * (mMaxCols - 1));
        return shapesWideSpace + (getPaddingLeft() + getPaddingRight());
    }

    private int getTotalHeight(Shape shape) {
        int shapesHighSpace = shape.getHeight() * mRows;
        return shapesHighSpace + (getPaddingTop() + getPaddingBottom());
    }


    private void recalculate(int width, int height) {
        final int wSize = (int) (width / (float) mMaxCols);
        final int hSize = (int) (height / (float) mRows);

        mSize = Math.min(wSize, hSize);
        mSpacing = (int) (mSize / 9f);
        mSize -= mSpacing;
        mPaint.setPathEffect(new CornerPathEffect(mSize / 6f));
    }

    private void cancelAnimation() {
        for (Shape path : mShapes) {
            if (path instanceof Scalable) {
                ((Scalable) path).stopAnimation();
            }
        }
    }

    private Runnable grow(final int duration, final int offset) {
        return new Runnable() {
            @Override
            public void run() {
                int index = 0;

                for (Shape path : mShapes) {
                    if (path instanceof Scalable) {
                        ((Scalable) path).grow(duration, index * offset);
                        index++;
                    }
                }

                long totalTime = duration + (offset * mShapes.length) + 750;
                postDelayed(shrink(duration, offset), totalTime);
            }
        };
    }

    private Runnable shrink(final int duration, final int offset) {
        return new Runnable() {
            @Override
            public void run() {
                int index = 0;

                for (Shape path : mShapes) {
                    if (path instanceof Scalable) {
                        ((Scalable) path).shrink(duration, offset * index);
                        index++;
                    }
                }

                postDelayed(grow(duration, offset), duration + (offset * mShapes.length) + 50);
            }
        };
    }

    private Shape[] getShapes(int size, int count) {
        Shape[] shapes = new Hexagon[count];

        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = new Hexagon(size);
        }

        return shapes;
    }

    private void positionShapes(int width, int height, Shape... shapes) {
        final Shape shapeRef = shapes[0];

        int col = 0;
        int row = 0;

        int x = getRowStartX(width, shapeRef, mPattern[0]);
        int y = getRowStartY(height, shapeRef, mRows);

        for (Shape shape : shapes) {
            shape.setPosition(x, y);

            x += shape.getWidth() + mSpacing;

            if (++col >= mPattern[row]) {
                row = ++row % mPattern.length;
                col = 0;
                x = getRowStartX(width, shapeRef, mPattern[row]);
                y += shape.getHeight() * V_MULTIPLER + mSpacing;
            }
        }
    }

    private int getRowStartY(int height, Shape shape, int rows) {
        int rowsHeight = (int) ((shape.getHeight() * V_MULTIPLER * mRows) + (mSpacing * mRows));
        return ((height - getPaddingTop() - getPaddingBottom() - rowsHeight) / 2);
    }

    private int getRowStartX(int width, Shape shape, int cols) {
        int x = 0;

        switch (mGravity) {
            case Gravity.LEFT:
                x = getPaddingLeft();
                break;
            case Gravity.CENTER:
                int rowWidth = (shape.getWidth() * cols) + (mSpacing * (cols - 1));
                x = (width - rowWidth - getPaddingRight() - getPaddingLeft()) / 2;
                break;
            case Gravity.RIGHT:
                x = width - getPaddingRight();
                break;
        }

        return x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Shape shape : mShapes) {
            shape.draw(canvas, mPaint);
        }
        invalidate();
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.size = mSize;
        state.color = mColor;
        state.count = mCount;
        state.gravity = mGravity;
        state.maxCols = mMaxCols;
        state.pattern = mPattern;
        state.rows = mRows;
        state.spacing = mSpacing;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            mSize = ss.size;
            mMaxCols = ss.maxCols;
            mSpacing = ss.spacing;
            mCount = ss.count;
            mColor = ss.color;
            mGravity = ss.gravity;
            mPattern = ss.pattern;
            mRows = ss.rows;
            init();
        }

        super.onRestoreInstanceState(state);
    }

    private static class SavedState extends BaseSavedState {

        int size;
        int spacing;
        int color;
        int gravity;
        int count;
        int rows;
        int maxCols;
        int[] pattern;

        public SavedState(Parcel source) {
            super(source);
            this.size = source.readInt();
            this.spacing = source.readInt();
            this.color = source.readInt();
            this.gravity = source.readInt();
            this.count = source.readInt();
            this.rows = source.readInt();
            this.maxCols = source.readInt();
            source.readIntArray(this.pattern);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.size);
            out.writeInt(this.spacing);
            out.writeInt(this.color);
            out.writeInt(this.gravity);
            out.writeInt(this.count);
            out.writeInt(this.rows);
            out.writeInt(this.maxCols);
            out.writeIntArray(this.pattern);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
