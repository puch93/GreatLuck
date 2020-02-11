package kr.co.planet.newgreatluck.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class ItemOffsetDecorationLinear extends RecyclerView.ItemDecoration {
    private int space;
    private int space2;
    private int size;

    public ItemOffsetDecorationLinear(int space, int space2, int size) {
        this.space = space;
        this.space2 = space2;
        this.size = size;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildLayoutPosition(view) != size) {
            outRect.left = space;
            outRect.right = space;
        } else {
            outRect.left = space;
            outRect.right = space2;
            Log.e("TEST_HOME", "getItemOffsets");
        }

        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) <= 2) {
//            outRect.top = space;
//        } else {
//            outRect.top = 0;
//        }
    }
}