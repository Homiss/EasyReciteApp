package com.adm.dictionary.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adm.dictionary.R;
import com.adm.dictionary.ui.BaseActivity;
import com.adm.dictionary.ui.fragment.HomeFragment;
import com.adm.dictionary.ui.fragment.MineFragment;
import com.adm.dictionary.ui.fragment.QuestionListFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String userId, token;

    private LinearLayout lin1, lin2, lin3;
    private ViewPager viewpager;
    private ImageView imgs[];
    private TextView tvs[];
    private int imgId[] = new int[]{R.drawable.homeon, R.drawable.liston, R.drawable.accounton, R.drawable.homeoff, R.drawable.listoff, R.drawable.accountoff};
    private List<Fragment> fragments = new ArrayList<>();
    private MyPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobclickAgent.setDebugMode( true ); // 友盟集成测试
        // 友盟更新

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        token = userInfo.getString("token", null);
        if(token == null){
            Intent intent = new Intent(this, LoginAndRegistActivity.class);
            startActivity(intent);
            return;
        }
        findId();
        initListener();
    }

    private void findId() {

        lin1 = findLinById(R.id.act_main_lin1);
        lin2 = findLinById(R.id.act_main_lin2);
        lin3 = findLinById(R.id.act_main_lin3);

        ImageView img1 = findImageViewById(R.id.act_main_img1);
        ImageView img2 = findImageViewById(R.id.act_main_img2);
        ImageView img3 = findImageViewById(R.id.act_main_img3);
        TextView tv1 = findTextViewById(R.id.act_main_tv1);
        TextView tv2 = findTextViewById(R.id.act_main_tv2);
        TextView tv3 = findTextViewById(R.id.act_main_tv3);
        viewpager = (ViewPager) findViewById(R.id.act_main_vp);
        HomeFragment homeFrag = new HomeFragment();
        homeFrag.setViewpager(viewpager);
        fragments.add(homeFrag);
        fragments.add(new QuestionListFragment());
        fragments.add(new MineFragment());
        imgs = new ImageView[]{img1, img2, img3};
        tvs = new TextView[]{tv1, tv2, tv3};
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);
        viewpager.setOffscreenPageLimit(3);
    }

    public void initListener() {
        lin1.setOnClickListener(this);
        lin2.setOnClickListener(this);
        lin3.setOnClickListener(this);

        /**
         * 设置ViewPager的滑动事件
         */
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_main_lin1:
                setItem(0);
                viewpager.setCurrentItem(0);
                break;
            case R.id.act_main_lin2:
                setItem(1);
                viewpager.setCurrentItem(1);
                break;
            case R.id.act_main_lin3:
                setItem(2);
                viewpager.setCurrentItem(2);
                break;
        }
    }

    /**
     * 设置显示的页面
     *
     * @param index 下标
     */
    private void setItem(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                imgs[i].setImageResource(imgId[i]);
                tvs[i].setTextColor(Color.parseColor("#05B6F7"));
            } else {
                imgs[i].setImageResource(imgId[i + 3]);
                tvs[i].setTextColor(Color.parseColor("#515151"));
            }
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
