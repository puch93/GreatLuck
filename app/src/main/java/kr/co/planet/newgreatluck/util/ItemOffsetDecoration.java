package kr.co.planet.newgreatluck.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ItemOffsetDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

//        outRect.left = space;
//        outRect.right = space;
//        outRect.bottom = space;

        outRect.top = space;
        outRect.left = space / 2;
        outRect.right = space / 2;
    }
}