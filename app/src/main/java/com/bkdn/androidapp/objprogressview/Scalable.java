package com.bkdn.androidapp.objprogressview;

/**
 * Created by Victor Souza on 11/05/2016.
 */ 
interface Scalable extends Animatable {
    
    void grow(int duration, int startOffset);
    void shrink(int duration, int startOffset);
}
