package com.bkdn.androidapp.objprogressview;

import android.animation.ValueAnimator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Victor Souza on 11/05/2016.
 */
class Scalator {

    private float mScale;
    private ValueAnimator mGrowAnimator;
    private ValueAnimator mShrinkAnimator;

    public void grow(int duration, int startOffset) {
        if (mGrowAnimator == null) {
            mGrowAnimator = new ValueAnimator();
        } else {
            stopAnimators();
        }

        mGrowAnimator.setFloatValues(0f, 1f);
        mGrowAnimator.setInterpolator(new OvershootInterpolator());
        mGrowAnimator.setDuration(duration);
        mGrowAnimator.setStartDelay(startOffset);

        mGrowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue();
            }
        });

        mGrowAnimator.start();
    }

    public void stopAnimators() {
        if (mShrinkAnimator != null) {
            mShrinkAnimator.cancel();
        }

        if (mGrowAnimator != null) {
            mGrowAnimator.cancel();
        }

    }

    public void shrink(int duration, int startOffset) {
        if (mShrinkAnimator == null) {
            mShrinkAnimator = new ValueAnimator();
        } else {
            stopAnimators();
        }

        mShrinkAnimator.setFloatValues(1f, 0f);
        mShrinkAnimator.setDuration(duration);
        mShrinkAnimator.setStartDelay(startOffset);

        mShrinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue();
            }
        });

        mShrinkAnimator.start();
    }

    public float scale() {
        return mScale;
    }
}
