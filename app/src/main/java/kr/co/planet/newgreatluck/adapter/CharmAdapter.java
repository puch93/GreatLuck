package kr.co.planet.newgreatluck.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.CharmDetailAct;
import kr.co.planet.newgreatluck.data.CharmData;


public class CharmAdapter extends RecyclerView.Adapter<CharmAdapter.ViewHolder> {
    private ArrayList<CharmData> array_data;
    private Activity act;


    public CharmAdapter(Activity act, ArrayList<CharmData> array_data) {
        this.act = act;
        this.array_data = array_data;
    }


    @NonNull
    @Override
    public CharmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_charm, viewGroup, false);
        CharmAdapter.ViewHolder viewHolder = new CharmAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CharmAdapter.ViewHolder holder, int i) {
        final CharmData data = array_data.get(i);

        holder.tv_charm_name.setText(data.getName());

        holder.ll_shimmer.startShimmer();
        Glide.with(act)
                .load(data.getImageUrl())
                .placeholder(R.drawable.gf_tp_m_off)
                .into(holder.iv_charm);

        holder.ll_charm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, CharmDetailAct.class);
                intent.putExtra("selectedData", data);
                act.startActivity(intent);
            }
        });
    }

    public void setItems(ArrayList<CharmData> array_data) {
        this.array_data = array_data;
    }

    @Override
    public int getItemCount() {
        return array_data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_charm_name;
        ImageView iv_charm;
        LinearLayout ll_charm;
        ShimmerFrameLayout ll_shimmer;

        ViewHolder(View v) {
            super(v);
            tv_charm_name = (TextView) v.findViewById(R.id.tv_charm_name);
            iv_charm = (ImageView) v.findViewById(R.id.iv_charm);
            ll_charm = (LinearLayout) v.findViewById(R.id.ll_charm);
            ll_shimmer = (ShimmerFrameLayout) v.findViewById(R.id.ll_shimmer);
        }
    }
}
