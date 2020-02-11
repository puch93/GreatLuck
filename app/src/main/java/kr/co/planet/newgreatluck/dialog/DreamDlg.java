package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogDreamBinding;

public class DreamDlg extends BasicDlg implements View.OnClickListener {
    DialogDreamBinding binding;
    Activity act;

    private static final int TYPE_TAEMONG = 0;
    private static final int TYPE_FOOD = 1;
    private static final int TYPE_FACE = 2;
    private static final int TYPE_ANIMAL = 3;
    private static final int TYPE_PLANT = 4;
    private static final int TYPE_OTHER = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_dream, null);
        act = this;

        setListener();
    }

    private void setListener() {
        binding.llMenu01.setOnClickListener(this);
        binding.llMenu02.setOnClickListener(this);
        binding.llMenu03.setOnClickListener(this);
        binding.llMenu04.setOnClickListener(this);
        binding.llMenu05.setOnClickListener(this);
        binding.llMenu06.setOnClickListener(this);
    }

    private void process_intent(int type) {
        Intent intent = new Intent(act, DreamSubDlg.class);
        intent.putExtra("type", type);
        startActivity(intent);

//        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_menu01:
                process_intent(TYPE_TAEMONG);
                break;

            case R.id.ll_menu02:
                process_intent(TYPE_FOOD);
                break;

            case R.id.ll_menu03:
                process_intent(TYPE_FACE);
                break;

            case R.id.ll_menu04:
                process_intent(TYPE_ANIMAL);
                break;

            case R.id.ll_menu05:
                process_intent(TYPE_PLANT);
                break;

            case R.id.ll_menu06:
                process_intent(TYPE_OTHER);
                break;
        }
    }
}
