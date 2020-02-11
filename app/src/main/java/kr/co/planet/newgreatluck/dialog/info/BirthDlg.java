package kr.co.planet.newgreatluck.dialog.info;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.ProfileAct;
import kr.co.planet.newgreatluck.databinding.DialogBirthBinding;
import kr.co.planet.newgreatluck.dialog.BasicDlg;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class BirthDlg extends BasicDlg implements View.OnClickListener {
    DialogBirthBinding binding;

    String calendarKind, moonKind, calendarMoonKindText;

    Activity profile_act;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_birth, null);
        act = this;

        firstSet();
    }

    private void firstSet() {
        profile_act = (Activity) ProfileAct.act;

        binding.flOk.setOnClickListener(this);
        binding.flCancel.setOnClickListener(this);

        binding.llSolar.setOnClickListener(this);
        binding.llLunar1.setOnClickListener(this);
        binding.llLunar2.setOnClickListener(this);
    }

    private void init() {
        binding.llSolar.setSelected(false);
        binding.llLunar1.setSelected(false);
        binding.llLunar2.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_solar:
                init();
                binding.llSolar.setSelected(true);
                calendarKind = CommonCode.CALENDER_01;
                moonKind = CommonCode.MOON_01;
                calendarMoonKindText = "양력";
                break;

            case R.id.ll_lunar1:
                init();
                binding.llLunar1.setSelected(true);
                calendarKind = CommonCode.CALENDER_02;
                moonKind = CommonCode.MOON_01;
                calendarMoonKindText = "음력평달";
                break;

            case R.id.ll_lunar2:
                init();
                binding.llLunar2.setSelected(true);
                calendarKind = CommonCode.CALENDER_02;
                moonKind = CommonCode.MOON_02;
                calendarMoonKindText = "음력윤달";
                break;

            case R.id.fl_cancel:
                finish();
                break;

            case R.id.fl_ok:
                if(StringUtil.isNull(calendarMoonKindText)) {
                    Common.showToast(this, "표기법을 선택해주세요");
                } else {
                    setResultData("calendarKind", calendarKind);
                    setResultData("moonKind", moonKind);
                    setResultData("calendarMoonKindText", calendarMoonKindText);
                    dialogResult();
                }
                break;
        }
    }
}
