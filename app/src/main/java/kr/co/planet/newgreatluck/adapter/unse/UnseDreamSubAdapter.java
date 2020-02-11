package kr.co.planet.newgreatluck.adapter.unse;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;


public class UnseDreamSubAdapter extends RecyclerView.Adapter<UnseDreamSubAdapter.ViewHolder> {
    private ArrayList<String> sub_list;
    private Activity act;


    public UnseDreamSubAdapter(Activity act, ArrayList<String> sub_list) {
        this.act = act;
        this.sub_list = sub_list;
    }


    @NonNull
    @Override
    public UnseDreamSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unse_dream_sub, viewGroup, false);
        UnseDreamSubAdapter.ViewHolder viewHolder = new UnseDreamSubAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull UnseDreamSubAdapter.ViewHolder holder, int i) {
       holder.tv_count.setText(String.valueOf(i + 1));
       holder.tv_contents.setText(sub_list.get(i));
    }


    @Override
    public int getItemCount() {
        return sub_list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_count, tv_contents;

        ViewHolder(View v) {
            super(v);
            tv_count = (TextView) v.findViewById(R.id.tv_count);
            tv_contents = (TextView) v.findViewById(R.id.tv_contents);
        }
    }
}
