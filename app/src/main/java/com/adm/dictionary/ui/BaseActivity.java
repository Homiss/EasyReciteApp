package com.adm.dictionary.ui;

import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/10/18.
 */
public abstract class BaseActivity extends FragmentActivity {

    public TextView findTextViewById(int id) {
        return (TextView) findViewById(id);
    }

    public ImageView findImageViewById(int id) {
        return (ImageView) findViewById(id);
    }

    public LinearLayout findLinById(int id) {
        return (LinearLayout) findViewById(id);
    }

    public EditText findEtnById(int id) {
        return (EditText) findViewById(id);
    }

    public Button findButById(int id) {
        return (Button) findViewById(id);
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);// 友盟统计[统计时长]，父类添加后子类不用重复添加
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);// 友盟统计[统计时长]，父类添加后子类不用重复添加
    }
}
