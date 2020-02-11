package kr.co.planet.newgreatluck.dialog.info;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogBloodBinding;
import kr.co.planet.newgreatluck.dialog.BasicDlg;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.StringUtil;

public class BloodDlg extends BasicDlg implements View.OnClickListener {
    DialogBloodBinding binding;
    String blood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_blood, null);

        firstSet();
    }

    private void firstSet() {
        binding.llBloodA.setOnClickListener(this);
        binding.llBloodB.setOnClickListener(this);
        binding.llBloodO.setOnClickListener(this);
        binding.llBloodAb.setOnClickListener(this);

        binding.flOk.setOnClickListener(this);
        binding.flCancel.setOnClickListener(this);
    }



    private void init() {
        binding.llBloodA.setSelected(false);
        binding.llBloodB.setSelected(false);
        binding.llBloodO.setSelected(false);
        binding.llBloodAb.setSelected(false);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_blood_a:
                init();
                binding.llBloodA.setSelected(true);
                blood = "a";
                break;

            case R.id.ll_blood_b:
                init();
                binding.llBloodB.setSelected(true);
                blood = "b";
                break;

            case R.id.ll_blood_o:
                init();
                binding.llBloodO.setSelected(true);
                blood = "o";
                break;

            case R.id.ll_blood_ab:
                init();
                binding.llBloodAb.setSelected(true);
                blood = "ab";
                break;

            case R.id.fl_ok:
                if(StringUtil.isNull(blood)) {
                    Common.showToast(this, "혈액형을 선택해주세요");
                } else {
                    setResultData("blood", blood);
                    dialogResult();
                }
                break;

            case R.id.fl_cancel:
                finish();
                break;
        }
    }
}
