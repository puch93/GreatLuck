package kr.co.planet.newgreatluck.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;

public class MainTopPagerAdapter extends LoopingPagerAdapter<String> {
    private int[] backgrounds = {R.drawable.frtn_bg_banner01_190716, R.drawable.frtn_bg_banner02_190716, R.drawable.frtn_bg_banner03_190716, R.drawable.frtn_bg_banner04_190716};
    private ArrayList<String> contents;
    private Context ctx;

    public MainTopPagerAdapter(Context context, ArrayList<String> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
        this.ctx = context;
        this.contents = itemList;
    }

    //This method will be triggered if the item View has not been inflated before.
    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_main_top, container, false);
    }

    //Bind your data with your item View here.
    //Below is just an example in the demo app.
    //You can assume convertView will not be null here.
    //You may also consider using a ViewHolder pattern.
    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        TextView content = (TextView) convertView.findViewById(R.id.tv_contents);
        content.setText(contents.get(listPosition));

        FrameLayout background = (FrameLayout) convertView.findViewById(R.id.fl_background);
        background.setBackground(ContextCompat.getDrawable(ctx, backgrounds[listPosition]));
    }
}