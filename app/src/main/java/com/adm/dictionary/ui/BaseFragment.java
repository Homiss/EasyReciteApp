package com.adm.dictionary.ui;

import android.support.v4.app.Fragment;
import android.view.View;
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
public abstract class BaseFragment extends Fragment {

    public TextView findTextViewbyId(View v, int id) {
        return (TextView) v.findViewById(id);
    }

    public ImageView findImageViewbyId(View v, int id) {
        return (ImageView) v.findViewById(id);
    }

    public LinearLayout findLinbyId(View v, int id) {
        return (LinearLayout) v.findViewById(id);
    }

    public EditText findEtbyId(View v, int id) {
        return (EditText) v.findViewById(id);
    }

    public Button findButById(View v, int id) {
        return (Button) v.findViewById(id);
    }

    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BaseFragment"); // 统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BaseFragment"); // 统计页面
    }
}
