package com.popdq.app;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Dang Luu on 10/06/2016.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private int mToolbarOffset = 0;
    private int mToolbarHeight;

    public HidingScrollListener(int heightButton) {
        mToolbarHeight = heightButton;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if ((mToolbarOffset < mToolbarHeight && dy > 0) || (mToolbarOffset > 0 && dy < 0)) {
            mToolbarOffset += dy;
        }
    }

    private void clipToolbarOffset() {
        if (mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if (mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    public abstract void onMoved(int distance);
}