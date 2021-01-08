package com.codetalenta.socialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetalenta.socialapp.adapter.ViewPaggerAdapter;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnLeft, btnRight;
    private ViewPaggerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPaggerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v -> {
            if (btnRight.getText().toString().equals("Next")) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(OnboardActivity.this, AuthActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
        });
    }

    private void addDots(int position) {
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(25);
            dotsLayout.addView(dots[i]);
//            dots[position].setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
        }

        if (dots.length > 0) {
            // dots[position].setTextColor(getResources().getColor(R.color.design_default_color_on_primary));
        }
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            if (position == 0) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else if (position == 1) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}