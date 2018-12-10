package com.yueyue_projects.library;

import android.graphics.Canvas;
import android.util.SparseArray;


public interface ITextureRenderer {
    void draw(Canvas canvas, int renderStartPx, int renderEndPx, SparseArray<Integer> renderDatas);
}
