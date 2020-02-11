package kr.co.planet.newgreatluck.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.FragmentMainTopBinding;
import kr.co.planet.newgreatluck.databinding.FragmentMenu01Binding;

public class MainTopBannerFrag extends BasicFrag {
    FragmentMainTopBinding binding;
    Activity act;

    private String title;
    private String contents;
    private int listPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_top, container, false);
        act = getActivity();

        binding.tvTitle.setText(title);
        binding.tvContents.setText(contents);

        return binding.getRoot();
    }

    public void setData(int i, String title, String contents) {
        this.title = title;
        this.contents = contents;
        listPosition = i;
    }
}

