package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityProfileOtherBinding;
import kr.co.planet.newgreatluck.dialog.info.BirthCalDlg;
import kr.co.planet.newgreatluck.dialog.info.BirthDlg;
import kr.co.planet.newgreatluck.dialog.info.BloodDlg;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class ProfileOtherAct extends BasicAct implements View.OnClickListener {
    ActivityProfileOtherBinding binding;
    public static Activity act;

    ActionBar actionBar;

    Realm realm;
    RealmConfiguration config;

    InfoData otherInfo;

    String[] constellationValues;

    private static final int DIALOG_BLOOD = 1001;
    private static final int DIALOG_BIRTH = 1002;
    private static final int DIALOG_BIRTH_DATE = 1003;


    /* my data parameter */
    String name, gender, blood, marriage;
    String year, month, day, calendarKind, moonKind, bornTime;
    String zodiac, constellation;

    String constellationText, zodiacText;
    String calendarMoonKindText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_other, null);
        act = this;

        setListener();

        checkOtherInfo();

        setActionBar();
    }


    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }



    private void setListener() {
        binding.flCharm.setOnClickListener(this);

        binding.llMarried.setOnClickListener(this);
        binding.llNonMarried.setOnClickListener(this);
        binding.llGenderMale.setOnClickListener(this);
        binding.llGenderFemale.setOnClickListener(this);
        binding.tvBirth.setOnClickListener(this);
        binding.tvConstellation.setOnClickListener(this);
        binding.tvBlood.setOnClickListener(this);
        binding.tvConstellation.setOnClickListener(this);
        binding.llSolarLunar.setOnClickListener(this);
        binding.flSave.setOnClickListener(this);


        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkButtonActivation();
            }
        });
    }




    private void checkOtherInfo() {
        constellationValues = getResources().getStringArray(R.array.constellation_value_array);


        // initialize Realm and create Realm object
        Realm.init(this);

        config = new RealmConfiguration.Builder()
                .name("luck.realm")
                .build();

        realm = null;
        try {
            realm = Realm.getInstance(config);
        } catch (RealmMigrationNeededException e) {
            // When the class structure is changed, create new Realm object
            Realm.deleteRealm(config);
            realm = Realm.getInstance(config);
        }



        // check other info
        InfoData otherInfo = realm.where(InfoData.class).equalTo("idx", "1").findFirst();
        if (otherInfo != null) {
            setOtherInfo();
        } else {
            binding.llGenderMale.performClick();
            binding.llNonMarried.performClick();
        }
    }


    private void setOtherInfo() {
        // get my all data
        otherInfo = UserPref.getOtherInfo(act);

        name = otherInfo.getName();
        gender = otherInfo.getGender();
        blood = otherInfo.getBlood();
        marriage = otherInfo.getMarrige();

        year = otherInfo.getYear();
        month = otherInfo.getMonth();
        day = otherInfo.getDay();
        calendarKind = otherInfo.getCalendarKind();
        moonKind = otherInfo.getMoonKind();
        bornTime = otherInfo.getBornTime();
        zodiac = otherInfo.getZodiac();
        constellation = otherInfo.getConstellation();

        zodiacText = otherInfo.getZodiacText();
        constellationText = otherInfo.getConstellationText();



        // set name
        binding.etName.setText(name);

        // set gender
        if (gender.equals("1")) {
            binding.llGenderMale.performClick();
        } else {
            binding.llGenderFemale.performClick();
        }

        // set calendar/moon kind
        if (otherInfo.getCalendarKind().equals(CommonCode.CALENDER_01)) {
            binding.tvSolarLunar.setText("양력");
        } else {
            if (otherInfo.getMoonKind().equals(CommonCode.MOON_01)) {
                binding.tvSolarLunar.setText("음력평달");
            } else {
                binding.tvSolarLunar.setText("음력윤달");
            }
        }

        // set birth
        if(month.length() == 1) {
            month  = "0" + month;
        }

        if(day.length() == 1) {
            day  = "0" + day;
        }

        binding.tvBirth.setText(year + "년 " + month + "월 " + day + "일");

        // set constellation
        constellationText = constellationValues[Integer.parseInt(constellation) - 1];
        binding.tvConstellation.setText(constellationText);

        // set blood
        binding.tvBlood.setText(blood.toUpperCase() + "형");

        // set marriage
        if(!StringUtil.isNull(marriage)) {
            if(marriage.equalsIgnoreCase("미혼")) {
                binding.llNonMarried.performClick();
            } else {
                binding.llMarried.performClick();
            }
        }

        // set save button image
        binding.ivSave.setImageResource(R.drawable.gf_morebutton);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case DIALOG_BLOOD:
                blood = data.getStringExtra("blood");
                binding.tvBlood.setText(blood.toUpperCase() + "형");
                break;


            case DIALOG_BIRTH_DATE:
                year = data.getStringExtra("mYear");
                month = data.getStringExtra("mMonth");
                day = data.getStringExtra("mDay");

                // set birth
                if(month.length() == 1) {
                    month  = "0" + month;
                }

                if(day.length() == 1) {
                    day  = "0" + day;
                }
                binding.tvBirth.setText(year + "년 " + month + "월 " + day + "일");

                // set constellation
                constellation = Common.checkConstellation(month, day);
                constellationText = constellationValues[Integer.parseInt(constellation) - 1];
                binding.tvConstellation.setText(constellationText);

                // set zodiac
                zodiac = Common.checkZodiac(year);
                zodiacText = Common.checkZodiacText(zodiac);
                break;

            case DIALOG_BIRTH:
                calendarKind = data.getStringExtra("calendarKind");
                moonKind = data.getStringExtra("moonKind");
                calendarMoonKindText = data.getStringExtra("calendarMoonKindText");
                binding.tvSolarLunar.setText(calendarMoonKindText);
                break;
        }

        checkButtonActivation();
    }


    /* 삭제시 참고 */
    private void deleteAllDB() {
        // obtain the results of a query
        final RealmResults<InfoData> results = realm.where(InfoData.class).findAll();

        // All changes to data must happen in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                // remove single match
//                results.deleteFirstFromRealm();
//                results.deleteLastFromRealm();
//
//                // remove a single object
//                InfoData dog = results.get(5);
//                dog.deleteFromRealm();
//
//                // remove a single object using other way
//                results.deleteFromRealm(5);

                // Delete all matches
                results.deleteAllFromRealm();
            }
        });
    }


    private void processDB() {
        try {
            writeDB();
            exit();
        } catch (RealmPrimaryKeyConstraintException e) {
            //이미 내 데이터 있을 때 - 기본키 Exception
            updateDB();
            exit();
        }
    }

    private void exit() {
        setResult(RESULT_OK);
        finish();
    }

    private void writeDB() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                InfoData data = realm.createObject(InfoData.class, "1");
                data.setData(name, gender, blood, marriage, year, month, day, calendarKind, moonKind, bornTime, zodiac, constellation);
                data.setDataText(zodiacText, constellationText);

                UserPref.saveOtherInfo(act, realm.copyFromRealm(data));
                Log.e(TAG, "my info: " + UserPref.getOtherInfo(act));
            }
        });
    }

    private void updateDB() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                InfoData data = realm.where(InfoData.class).equalTo("idx", "1").findFirst();
                if (data != null) {
                    data.setData(name, gender, blood, marriage, year, month, day, calendarKind, moonKind, bornTime, zodiac, constellation);
                    data.setDataText(zodiacText, constellationText);

                    UserPref.saveOtherInfo(act, realm.copyFromRealm(data));
                    Log.e(TAG, "my info: " + UserPref.getOtherInfo(act));
                }
            }
        });
    }


    private void checkButtonActivation() {
        if (!StringUtil.isNull(binding.etName.getText().toString()) &&
                !StringUtil.isNull(binding.tvBirth.getText().toString()) &&
                !StringUtil.isNull(binding.tvBlood.getText().toString()) &&
                !StringUtil.isNull(binding.tvConstellation.getText().toString())) {

            binding.ivSave.setImageResource(R.drawable.gf_morebutton);
        } else {
            binding.ivSave.setImageResource(R.drawable.gf_more_g_button);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_charm:
                startActivity(new Intent(act, CharmAct.class));
                break;

            case R.id.ll_gender_male:
                gender = CommonCode.GENDER_MALE;
                binding.llGenderMale.setSelected(true);
                binding.llGenderFemale.setSelected(false);
                break;
            case R.id.ll_gender_female:
                gender = CommonCode.GENDER_FEMALE;
                binding.llGenderMale.setSelected(false);
                binding.llGenderFemale.setSelected(true);
                break;

            case R.id.ll_non_married:
                marriage = "미혼";
                binding.llNonMarried.setSelected(true);
                binding.llMarried.setSelected(false);
                break;
            case R.id.ll_married:
                marriage = "기혼";
                binding.llNonMarried.setSelected(false);
                binding.llMarried.setSelected(true);
                break;

            case R.id.tv_blood:
                startActivityForResult(new Intent(act, BloodDlg.class), DIALOG_BLOOD);
                break;

            case R.id.tv_birth:
                startActivityForResult(new Intent(act, BirthCalDlg.class), DIALOG_BIRTH_DATE);
                break;

            case R.id.ll_solar_lunar:
                startActivityForResult(new Intent(act, BirthDlg.class), DIALOG_BIRTH);
                break;

            case R.id.tv_constellation:
                Common.showToast(act, "생년월일을 입력하세요.");
                break;


            case R.id.fl_save:
                if (StringUtil.isNull(binding.etName.getText().toString())) {
                    Common.showToast(act, "성명을 입력하세요.");
                } else if (binding.etName.length() == 1 || binding.etName.length() > 5) {
                    Common.showToast(act, "성명을 확인하세요.");
                } else if (StringUtil.isNull(binding.tvBirth.getText().toString())) {
                    Common.showToast(act, "생년월일을 입력하세요.");
                } else if (StringUtil.isNull(binding.tvBlood.getText().toString())) {
                    Common.showToast(act, "혈액형을 입력하세요.");
                } else {

                    // '양력/음력평달/음력윤달' 의 경우, 양력으로 기본 세팅 되게 만들었기 때문에 기본값 세팅
                    if(StringUtil.isNull(calendarKind)) {
                        calendarKind = CommonCode.CALENDER_01;
                        moonKind = CommonCode.MOON_01;
                    }

                    name = binding.etName.getText().toString();
                    processDB();
                }
                break;
        }
    }
}
