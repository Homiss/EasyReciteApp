package com.adm.dictionary.dictionary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adm.dictionary.base.BaseFragment;
import com.bumptech.glide.Glide;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/10/18.
 */
public class MineFragment extends BaseFragment {

    private View v;
    private ImageView headpicImg;
    private TextView nameTv, sentenceTv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_mine, null);
        initView();
        initData();
        return v;
    }

    @Override
    public void initView() {
        headpicImg = findImageViewbyId(v, R.id.frag_mine_headpic);
        nameTv = findTextViewbyId(v, R.id.frag_mine_name);
        sentenceTv = findTextViewbyId(v, R.id.frag_mine_sentence);
    }

    private void initData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
        String name = userInfo.getString("name", null);
        String haedpic = userInfo.getString("headpic", null);
        nameTv.setText(name);
        Glide.with(getActivity()).load(haedpic).into(headpicImg);
    }
}
