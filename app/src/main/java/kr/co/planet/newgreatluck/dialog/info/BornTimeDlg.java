package kr.co.planet.newgreatluck.dialog.info;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogBornTimeBinding;
import kr.co.planet.newgreatluck.dialog.BasicDlg;

public class BornTimeDlg extends BasicDlg implements View.OnClickListener {
    DialogBornTimeBinding binding;

    NumberPicker picker;
    String[] bornTimeValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_born_time, null);
        firstSet();
    }

    private void firstSet() {
        bornTimeValues = getResources().getStringArray(R.array.born_times_array);

        picker = binding.picker;
        picker.setMinValue(0);
        picker.setMaxValue(12);
        picker.setDisplayedValues(bornTimeValues);
        //disable soft keyboard
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set wrap true or false, try it you will know the difference
        picker.setWrapSelectorWheel(false);


        binding.flCancel.setOnClickListener(this);
        binding.flOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_ok:
                Log.e(TAG, "picker value: " + picker.getValue() );

                int bornTime = picker.getValue();
                String bornTimeText = bornTimeValues[bornTime];
                setResultData("bornTime", String.valueOf(bornTime));
                dialogResult();
                break;

            case R.id.fl_cancel:
                finish();
                break;
        }
    }
}
