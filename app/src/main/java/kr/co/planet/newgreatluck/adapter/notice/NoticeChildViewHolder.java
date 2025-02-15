package kr.co.planet.newgreatluck.adapter.notice;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import kr.co.planet.newgreatluck.R;

public class NoticeChildViewHolder extends ChildViewHolder {

    private TextView contents;

    public NoticeChildViewHolder(View itemView) {
        super(itemView);
        contents = (TextView) itemView.findViewById(R.id.tv_contents);
    }

    public void setContents(String data) {
        contents.setText(data);
    }
}