package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.databinding.ActivityCharmDetailBinding;
import kr.co.planet.newgreatluck.dialog.info.BirthCalDlg;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.StringUtil;

public class CharmDetailAct extends BasicAct {
    ActivityCharmDetailBinding binding;
    public static Activity act;
    ActionBar actionBar;

    CharmData selectedData;

    private static final int DIALOG_BIRTH_DATE = 1001;
    String year, month, day;
    String name;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charm_detail, null);
        act = this;

        Log.e(TAG, "isFullScreen: " + isFullScreen());

        /* EditText 포커스될때 키보드가 UI 가리는 것 막음 */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        /* EditText 포커스될때 ui 맨밑으로 */
        binding.scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.scrollView.scrollTo(0, binding.scrollView.getBottom());
//                            binding.scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 0);
                }
            }
        });

        dialog = new ProgressDialog(act);
        dialog.setMessage("부적을 불러오는 중입니다..");
        dialog.setCancelable(false);
        dialog.show();


        binding.llShimmer.startShimmer();

        setActionBar();

        setData();

        setListener();
    }

    public boolean isFullScreen() {
        int flg = getWindow().getAttributes().flags;
        boolean flag = false;
        if ((flg & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            flag = true;
        }
        return flag;
    }

    private void setListener() {
        binding.llBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(act, BirthCalDlg.class), DIALOG_BIRTH_DATE);
            }
        });

        binding.flOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isNull(binding.etName.getText().toString())) {
                    Common.showToast(act, "이름을 입력하세요.");
                } else if(binding.etName.getText().toString().length() < 2 || binding.etName.getText().toString().length() > 4) {
                    Common.showToast(act, "이름의 길이를 확인하세요.");
                } else if (StringUtil.isNull(year)) {
                    Common.showToast(act, "생년월일을 입력하세요.");
                } else {
                    name = binding.etName.getText().toString();
                    Intent intent = new Intent(act, CharmPurchaseAct.class);
                    intent.putExtra("selectedData", selectedData);
                    intent.putExtra("name", name);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", day);
                    act.startActivity(intent);
                }
            }
        });

        binding.flCharm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CharmAct.class));
            }
        });
    }

    private void setData() {
        selectedData = (CharmData) getIntent().getSerializableExtra("selectedData");

        binding.tvCharmName.setText(selectedData.getName());
        binding.tvCharmContents.setText(selectedData.getContents());

        Glide.with(act).load(selectedData.getImageUrl()).into(binding.ivCharm);
        Glide.with(act)
                .load(selectedData.getImageUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                .skipMemoryCache(true)// 메모리 캐시 저장 off
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.e(TAG, "onResourceReady");
                        binding.ivCharm.setImageBitmap(resource);

                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                });
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


    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case DIALOG_BIRTH_DATE:
                year = data.getStringExtra("mYear");
                month = data.getStringExtra("mMonth");
                day = data.getStringExtra("mDay");

                selectedData.setYear(year);
                selectedData.setMonth(month);
                selectedData.setDay(day);

                if(month.length() == 1) {
                    month  = "0" + month;
                }

                if(day.length() == 1) {
                    day  = "0" + day;
                }

                binding.tvBirth.setText(year + "년  " + month + "월  " + day + "일");
                binding.tvBirth.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_3b3a39));
                break;
        }
    }

}
