package kr.co.planet.newgreatluck.dialog.info;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogBirthCalBinding;
import kr.co.planet.newgreatluck.dialog.BasicDlg;

public class BirthCalDlg extends BasicDlg implements View.OnClickListener {
    DialogBirthCalBinding binding;
    Activity act;

    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_birth_cal, null);
        act = this;

        firstSet();
    }

    private void firstSet() {
        datePicker = binding.datePicker;

        //disable soft keyboard
        datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker.init(1970, 0, 1, null);

        binding.flOk.setOnClickListener(this);
        binding.flCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_cancel:
                finish();
                break;

            case R.id.fl_ok:
                String year = String.valueOf(datePicker.getYear());
                String month = String.valueOf(datePicker.getMonth()+1);
                String day = String.valueOf(datePicker.getDayOfMonth());


                setResultData("mYear", year);
                setResultData("mMonth", month);
                setResultData("mDay", day);
                dialogResult();
                break;
        }
    }
}
