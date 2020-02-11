package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.adapter.unse.UnseDreamSubMenuAdapter;
import kr.co.planet.newgreatluck.databinding.DialogDreamSubBinding;

public class DreamSubDlg extends BasicDlg {
    DialogDreamSubBinding binding;
    Activity act;

    int type;
    private static final int TYPE_TAEMONG = 0;
    private static final int TYPE_FOOD = 1;
    private static final int TYPE_FACE = 2;
    private static final int TYPE_ANIMAL = 3;
    private static final int TYPE_PLANT = 4;
    private static final int TYPE_OTHER = 5;

    String[] code_array;
    String[] title_array;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    UnseDreamSubMenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_dream_sub, null);
        act = this;

        type = getIntent().getIntExtra("type", -1);
        setTypeData(type);

        recyclerView = binding.rcv;
        manager = new LinearLayoutManager(act);
        adapter = new UnseDreamSubMenuAdapter(act, code_array, title_array);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);


        // if title size is too much
        if(title_array.length > 6) {
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 298, getResources().getDisplayMetrics());


            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // Set the height by params
            params.height=height;
            // set height of RecyclerView
            recyclerView.setLayoutParams(params);
        }
    }

    private void setTypeData(int ch) {
        switch (ch) {
            case TYPE_TAEMONG:
                binding.tvTitle.setText("태몽, 복권당첨");
                code_array = getResources().getStringArray(R.array.dream_taemong_code);
                title_array = getResources().getStringArray(R.array.dream_taemong_title);
                break;
            case TYPE_FOOD:
                binding.tvTitle.setText("음식물, 옷, 돈");
                code_array = getResources().getStringArray(R.array.dream_food_code);
                title_array = getResources().getStringArray(R.array.dream_food_title);
                break;
            case TYPE_FACE:
                binding.tvTitle.setText("얼굴, 모습, 행동");
                code_array = getResources().getStringArray(R.array.dream_face_code);
                title_array = getResources().getStringArray(R.array.dream_face_title);
                break;
            case TYPE_ANIMAL:
                binding.tvTitle.setText("동물");
                code_array = getResources().getStringArray(R.array.dream_animal_code);
                title_array = getResources().getStringArray(R.array.dream_animal_title);
                break;
            case TYPE_PLANT:
                binding.tvTitle.setText("식물");
                code_array = getResources().getStringArray(R.array.dream_plant_code);
                title_array = getResources().getStringArray(R.array.dream_plant_title);
                break;
            case TYPE_OTHER:
                binding.tvTitle.setText("기타");
                code_array = getResources().getStringArray(R.array.dream_other_code);
                title_array = getResources().getStringArray(R.array.dream_other_title);
                break;
        }
    }
}
